//Renderer handles everything needed to draw the Scene
//Needs to be provided all required Shaders and a root SceneNode
//Updates all SceneNodes under the given root, sorts DrawNodes into appropriate containers and draws them


#pragma once
#include <glad/glad.h>
#include <GLFW/glfw3.h>
#include <glm/glm.hpp>
#include <glm/gtc/matrix_transform.hpp>
#include <glm/gtc/type_ptr.hpp>

#include <iostream>
#include <vector>
#include <algorithm>

#include "Camera.h"
#include "GroupNode.h"
#include "DrawNode.h"
#include "Shader.h"
#include "LightNode.h"

extern  unsigned int SCR_WIDTH;
extern  unsigned int SCR_HEIGHT;

class Renderer{
	const static int MAX_LIGHTS = 1;//be sure that it matches the MAX_LIGHTS from shaders !!!
	const static unsigned int SHADOW_WIDTH = 1024, SHADOW_HEIGHT = 1024;//quality of depth map
	//if want variable far_plane, need to send them to shaders in render() and not in Renderer constructor!
	const float near_plane = 0.1;//minimum distance from which depth is computed
	const float far_plane = 200;//maximum distance at which depth is computed
public:
	Renderer(Camera* camera, Shader* genericShader, Shader* lightingMaterialShader, Shader* lightingTextureShader, Shader* shadowShader);
	~Renderer();

	void updateScene(float dt);//makes first call to updateNode() that updates worldTransform for all nodes under rootSceneNode and sorts all DrawNodes into appropriate vectors for later future draw calls
	void render();

	void setRootSceneNode(GroupNode* node) { rootSceneNode = node; }
	void removeRootSceneNode() { rootSceneNode = NULL; }//never used, but for project can be used to have different scenes and chose which to render (like different levels)

	void setShadowCasterLight(LightNode* light) { shadowCasterLight = light; }//wont generate shadows if not specifying this

	//drawing parameters
	void setTexRatio(float ratio) { texRatio = ratio; }//manually set TexRatio (not used since assignment says to toggle, leaving if needed later
	void toggleTexture() {texRatio = (texRatio == 0) ? 1.0f : 0.0f;	}
	void toggleShadowMode() { shadowMode = !shadowMode; }



private:
	//shaders
	Shader* genericShader;
	Shader* lightingMaterialShader;
	Shader* lightingTextureShader;
	Shader* shadowShader;
	//cameras (only one for now, but could add more that go along a spline curve later)
	Camera* mainCamera;

	GroupNode* rootSceneNode;

	//updateScene() sorts all DrawNodes in these containers. Each require a specific shader to draw, so avoids shader swaps 
	std::vector<DrawNode*> genericDraws;
	std::vector<DrawNode*> opaqueTexDraws;
	std::vector<DrawNode*> opaqueMaterialDraws;
	std::vector<DrawNode*> transparentDraws;//only one that truely needs to be sorted out, since needs to be drawn at end

	GLuint pointLightsUniformBlock;//avoids setting all lights individually in each shader, instead uses Uniform Interface Block
	std::vector<LightNode*> lights;//won't add more than MAX_LIGHTS number of lights (will print to console if you try)

	LightNode* shadowCasterLight;//although can have MAX_LIGHTS number of lights in scene, only 1 can cast shadows

	float texRatio;
	bool shadowMode;

	GLuint depthMapFBO = 0;//frame buffer for rendering shadows
	GLuint depthCubeMap = 0;//cube depth map texture to hold info for shadow maping Point Lights


	void updateNode(SceneNode* node, const glm::mat4& CTM, float dt);//recursively updates worldTransforms in Scene and sorts into containers
	

};

