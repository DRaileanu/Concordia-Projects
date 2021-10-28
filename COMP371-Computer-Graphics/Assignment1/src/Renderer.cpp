#include "Renderer.h"


Renderer::Renderer(Camera* camera, Shader* genericShader,  Shader* lightingMaterialShader, Shader* lightingTextureShader, Shader* shadowShader) {
	this->mainCamera = camera;
	this->genericShader = genericShader;
	this->lightingMaterialShader = lightingMaterialShader;
	this->lightingTextureShader = lightingTextureShader;
	this->shadowShader = shadowShader;
	shadowCasterLight = NULL;

	texRatio = 1.0f;
	shadowMode = true;

	// configure depthMapFBO
	// -----------------------
	glGenFramebuffers(1, &depthMapFBO);
	// setup depthCubeMap texture
	glGenTextures(1, &depthCubeMap);
	glBindTexture(GL_TEXTURE_CUBE_MAP, depthCubeMap);
	for (unsigned int i = 0; i < 6; ++i) {
		glTexImage2D(GL_TEXTURE_CUBE_MAP_POSITIVE_X + i, 0, GL_DEPTH_COMPONENT, SHADOW_WIDTH, SHADOW_HEIGHT, 0, GL_DEPTH_COMPONENT, GL_FLOAT, NULL);
	}
	glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
	glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
	glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
	glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
	glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_R, GL_CLAMP_TO_EDGE);
	// attach depth texture as FBO's depth buffer
	glBindFramebuffer(GL_FRAMEBUFFER, depthMapFBO);
	glFramebufferTexture(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, depthCubeMap, 0);
	glDrawBuffer(GL_NONE);
	glReadBuffer(GL_NONE);
	glBindFramebuffer(GL_FRAMEBUFFER, 0);


	//setup depth map texture for shaders that use lighting
	glActiveTexture(GL_TEXTURE7);//7 is arbitrary
	glBindTexture(GL_TEXTURE_CUBE_MAP, depthCubeMap);

	lightingMaterialShader->use();
	lightingMaterialShader->setInt("depthCubeMap", 7);
	lightingMaterialShader->setFloat("far_plane", far_plane);//NOTE: if want to vary far_plane, needs to be inside render(). Putting here to reduce setting uniforms

	lightingTextureShader->use();
	lightingTextureShader->setInt("texture1", 0);
	lightingTextureShader->setInt("depthCubeMap", 7);
	lightingTextureShader->setFloat("far_plane", far_plane);


	
	// configure Uniform Interface Block so can set PointLights in all shaders at once
	GLuint materialIndex = glGetUniformBlockIndex(lightingMaterialShader->ID, "PointLights");
	glUniformBlockBinding(lightingMaterialShader->ID, materialIndex, 0);

	GLuint textureIndex = glGetUniformBlockIndex(lightingTextureShader->ID, "PointLights");
	glUniformBlockBinding(lightingTextureShader->ID, textureIndex, 0);

	glGenBuffers(1, &pointLightsUniformBlock);
	glBindBuffer(GL_UNIFORM_BUFFER, pointLightsUniformBlock);
	glBufferData(GL_UNIFORM_BUFFER, 4 * sizeof(glm::vec4) * MAX_LIGHTS, NULL, GL_STATIC_DRAW);//although each LightProperty uses 3 * vec3, Uniform Blocks go in chunks of 16 bytes, so need to allocate vec4 for each property in LightProperty
	glBindBuffer(GL_UNIFORM_BUFFER, 0);

	glBindBufferBase(GL_UNIFORM_BUFFER, 0, pointLightsUniformBlock);

}

Renderer::~Renderer() {

}



void Renderer::render() {
	if (!rootSceneNode) {
		return;
	}
	glDepthMask(GL_TRUE);//needs to be set to true before clearing depth buffer, else getting weird issues
	glClearColor(0.2f, 0.3f, 0.3f, 1.0f);
	glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

	//View-Projection matrix to be given to shaders
	glm::mat4 projection = glm::perspective(glm::radians(mainCamera->Zoom), (float)SCR_WIDTH / (float)SCR_HEIGHT, 0.1f, 1000.0f);
	glm::mat4 view = mainCamera->GetViewMatrix();
	glm::mat4 VP = projection * view;
	//camera position for specular lighting calculation in fragment shader
	glm::vec3 viewPos = mainCamera->Position;


	//TODO: checking for shadowCasterLight can be a problem if LightNode is deleted from main. Solution: smart pointers
	if (shadowMode && shadowCasterLight) {

		//TODO for now only PointLight implementation of shadows is made. Add others if needed later
		if (shadowCasterLight->getType() == LightType::PointLight) {
			glm::vec3 lightPos = shadowCasterLight->getWorldTransform()[3];//light position relative to which shadows are created
			//create depthCubeMap transformation matrics
			glm::mat4 shadowProj = glm::perspective(glm::radians(90.0f), (float)SHADOW_WIDTH / (float)SHADOW_HEIGHT, near_plane, far_plane);
			std::vector<glm::mat4> shadowTransforms;
			shadowTransforms.push_back(shadowProj * glm::lookAt(lightPos, lightPos + glm::vec3(1.0f, 0.0f, 0.0f), glm::vec3(0.0f, -1.0f, 0.0f)));
			shadowTransforms.push_back(shadowProj * glm::lookAt(lightPos, lightPos + glm::vec3(-1.0f, 0.0f, 0.0f), glm::vec3(0.0f, -1.0f, 0.0f)));
			shadowTransforms.push_back(shadowProj * glm::lookAt(lightPos, lightPos + glm::vec3(0.0f, 1.0f, 0.0f), glm::vec3(0.0f, 0.0f, 1.0f)));
			shadowTransforms.push_back(shadowProj * glm::lookAt(lightPos, lightPos + glm::vec3(0.0f, -1.0f, 0.0f), glm::vec3(0.0f, 0.0f, -1.0f)));
			shadowTransforms.push_back(shadowProj * glm::lookAt(lightPos, lightPos + glm::vec3(0.0f, 0.0f, 1.0f), glm::vec3(0.0f, -1.0f, 0.0f)));
			shadowTransforms.push_back(shadowProj * glm::lookAt(lightPos, lightPos + glm::vec3(0.0f, 0.0f, -1.0f), glm::vec3(0.0f, -1.0f, 0.0f)));

			//render scene to depthCubeMap
			//----------------------------
			glViewport(0, 0, SHADOW_WIDTH, SHADOW_HEIGHT);//switch viewport since depthMap might use different resolution (SHADOW_WIDTH * SHADOW_HEIGHT)
			glBindFramebuffer(GL_FRAMEBUFFER, depthMapFBO);//bind and clear depthMap frame buffer
			glClear(GL_DEPTH_BUFFER_BIT);
			//configure shadowShader
			shadowShader->use();
			for (unsigned int i = 0; i < 6; ++i) {
				shadowShader->setMat4("shadowMatrices[" + std::to_string(i) + "]", shadowTransforms[i]);
			}
			shadowShader->setFloat("far_plane", far_plane);//needed to clamp distance from Frag_Depth and lightPos to [0,1] in shadow.fs
			shadowShader->setVec3("lightPos", lightPos);

			//render scene into depthCubeMap
			glEnable(GL_CULL_FACE);
			for (auto& node : genericDraws) {
				if (node->getCastsShadow()) {
					shadowShader->setMat4("model", node->getWorldTransform());
					node->draw();
				}
			}
			for (auto& node : opaqueTexDraws) {
				shadowShader->setMat4("model", node->getWorldTransform());
				node->draw();
			}
			for (auto& node : opaqueMaterialDraws) {
				shadowShader->setMat4("model", node->getWorldTransform());
				node->draw();
			}
			glBindFramebuffer(GL_FRAMEBUFFER, 0);//for safety
			glViewport(0, 0, SCR_WIDTH, SCR_HEIGHT);

			//provide shaders that implement shadows position of shadowCastingLight
			lightingMaterialShader->use();
			lightingMaterialShader->setVec3("shadowCastingLightPos", lightPos);
			lightingTextureShader->use();
			lightingTextureShader->setVec3("shadowCastingLightPos", lightPos);
		}
		//TODO if DirLight & if SpotLight

	}



	//configure rest of shaders
	//-------------------------
	glBindBuffer(GL_UNIFORM_BUFFER, pointLightsUniformBlock);
	for (int i = 0; i < lights.size(); ++i) {
		LightProperties prop = lights[i]->getProperties();
		std::size_t stride = sizeof(glm::vec4) * 4;//although inserting glm::vec3, stride is 16 bytes for Uniform Interface Blocks
		glBufferSubData(GL_UNIFORM_BUFFER, stride * i, sizeof(glm::vec3), &lights[i]->getWorldTransform()[3]);
		for (int j = 0; j < 3; ++j) {
			glBufferSubData(GL_UNIFORM_BUFFER, stride * i + (sizeof(glm::vec4) * (j + 1)), sizeof(glm::vec3), &prop.ambient + j);
		}
	}
	glBindBuffer(GL_UNIFORM_BUFFER, 0);


	genericShader->use();
	genericShader->setMat4("VP", VP);

	lightingMaterialShader->use();
	lightingMaterialShader->setMat4("VP", VP);
	lightingMaterialShader->setVec3("viewPos", viewPos);
	lightingMaterialShader->setBool("shadows", shadowMode);

	lightingTextureShader->use();
	lightingTextureShader->setMat4("VP", VP);
	lightingTextureShader->setVec3("viewPos", viewPos);
	lightingTextureShader->setBool("shadows", shadowMode);




	//sort DrawNodes by Texture and Material to reduce state changes.
	//commented-out, since there's so too few Materials/Textures, so sorting is not worth it. Usefull for project maybe?
	/*
	std::sort(opaqueTexDraws.begin(), opaqueTexDraws.end(), [](DrawNode* a, DrawNode* b) {
		if (a->getTexture() < b->getTexture()) {
			return true;
		}
		else {
			return a->getMaterial() < b->getMaterial();
		}
	});

	std::sort(opaqueMaterialDraws.begin(), opaqueMaterialDraws.end(), [](DrawNode* a, DrawNode* b) {
		return a->getMaterial() < b->getMaterial();
	});
	*/
	

	//sort transparent DrawNodes from furthest to closest
	std::sort(transparentDraws.begin(), transparentDraws.end(), [&](DrawNode* a, DrawNode* b) {
		glm::vec3 aPos = glm::vec3(a->getWorldTransform()[3]);
		glm::vec3 bPos = glm::vec3(b->getWorldTransform()[3]);
		float aDistance = glm::length(mainCamera->Position - aPos);
		float bDistance = glm::length(mainCamera->Position - bPos);
		return aDistance > bDistance;
	});

	

	//DRAW CALLS
	//----------
	
	glDisable(GL_BLEND);//needs to be disabled just in case Drawable has alpha <1.0 but DrawNode sets transparency to false
	glEnable(GL_CULL_FACE);//optimization

	//DrawNodes that do not have Materials set
	genericShader->use();
	for (auto& node : genericDraws) {
		glm::mat4 model = node->getWorldTransform();
		genericShader->setMat4("model", model);

		node->draw();
	}

	//DrawNodes with Material and Texture
	lightingTextureShader->use();
	lightingTextureShader->setFloat("texRatio", texRatio);
	for (auto& node : opaqueTexDraws) {
		static GLuint prevTexture = 0;
		static Material* prevMaterial = NULL;
		GLuint texture = node->getTexture();
		Material* material = node->getMaterial();

		if (texture != prevTexture) {
			prevTexture = texture;
			glActiveTexture(GL_TEXTURE0);//technically not needed, but needed if want to add several textures later, so leaving to not forget about it
			glBindTexture(GL_TEXTURE_2D, texture);
		}
		if (material != prevMaterial) {
			prevMaterial = material;
			lightingTextureShader->setVec3("material.ambient", material->ambient);
			lightingTextureShader->setVec3("material.diffuse", material->diffuse);
			lightingTextureShader->setVec3("material.specular", material->specular);
			lightingTextureShader->setFloat("material.shininess", material->shininess);
		}

		glm::mat4 model = node->getWorldTransform();
		glm::mat3 normalMatrix = glm::mat3(glm::transpose(glm::inverse(model)));
		lightingTextureShader->setMat4("model", model);
		lightingTextureShader->setMat3("normalMatrix", normalMatrix);

		node->draw();
	}
	
	//DrawNodes with Materials and no Texture
	lightingMaterialShader->use();
	for (auto& node : opaqueMaterialDraws) {
		static Material* prevMaterial = NULL;
		Material* material = node->getMaterial();
		if (material != prevMaterial) {
			prevMaterial = material;
			lightingMaterialShader->setVec3("material.ambient", material->ambient);
			lightingMaterialShader->setVec3("material.diffuse", material->diffuse);
			lightingMaterialShader->setVec3("material.specular", material->specular);
			lightingMaterialShader->setFloat("material.shininess", material->shininess);
		}

		glm::mat4 model = node->getWorldTransform();
		glm::mat3 normalMatrix = glm::mat3(glm::transpose(glm::inverse(model)));
		lightingMaterialShader->setMat4("model", model);
		lightingMaterialShader->setMat3("normalMatrix", normalMatrix);

		node->draw();
	}


	// render transparents, which are already sorted back to front
	// rendering with genericShader, so doent't draw textures even if present and not affected by lighting. Just an implementation detail
	// if need different behaviour, requires new shader(s)
	glEnable(GL_BLEND);
	glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
	glDepthMask(GL_FALSE);//don't write to depth buffer, or else incorectly draws transparent objects that overlap

	genericShader->use();
	glCullFace(GL_FRONT);
	for (auto& node : transparentDraws) {
		genericShader->setFloat("transparency", node->getTransparency());

		glm::mat4 model = node->getWorldTransform();
		genericShader->setMat4("model", model);

		node->draw();
	}
	glCullFace(GL_BACK);
	for (auto& node : transparentDraws) {
		genericShader->setFloat("transparency", node->getTransparency());

		glm::mat4 model = node->getWorldTransform();
		genericShader->setMat4("model", model);

		node->draw();
	}


	//clear containers
	genericDraws.clear();
	opaqueTexDraws.clear();
	opaqueMaterialDraws.clear();
	transparentDraws.clear();
	lights.clear();
}

void Renderer::updateScene(float dt) {
	if (!rootSceneNode) {
		return;
	}
	updateNode(rootSceneNode, glm::mat4(1.0f), dt);
}


// update SceneGraph and collect DrawNodes to be rendered
// TODO instead of dynamic_cast, implement visitor pattern
void Renderer::updateNode(SceneNode* node, const glm::mat4& CTM, float dt) {
	node->update(CTM, dt);

	if (DrawNode* drawNode = dynamic_cast<DrawNode*>(node)) {
		if (drawNode->getTransparency() > 0.0f) {
			transparentDraws.push_back(drawNode);
		}
		else if (drawNode->getMaterial()) {
			if (drawNode->getTexture()) {
				opaqueTexDraws.push_back(drawNode);
			}
			else {
				opaqueMaterialDraws.push_back(drawNode);
			}
		}
		else { genericDraws.push_back(drawNode); }
	}

	else if (LightNode* lightNode = dynamic_cast<LightNode*>(node)) {
		if (lights.size() > MAX_LIGHTS) {
			std::cout << "Reached maximum of " << MAX_LIGHTS << " lights, can't add more!" << std::endl;
			return;
		}
		lights.push_back(lightNode);
	}

	else if (GroupNode* groupNode = dynamic_cast<GroupNode*>(node)) {
		for (auto& child : groupNode->getChildren()) {
			updateNode(child, node->getWorldTransform(), dt);
		}
	}
}
