#pragma once
#include "RubikCube.h"
#include "ParticleEffect.h"

class RubikCubeParticles : public RubikCube {
public:
	RubikCubeParticles() : RubikCube() {
		setupDrawNodes();
	}
	~RubikCubeParticles() {}

	std::vector<ParticleEffect*> particleEffects;

	void update(const glm::mat4& CTM, float dt) override {
		if (animated) {
			if (rotatingWholeCube) {
				rotationAnimationUpdate();
			}
			else {
				animationUpdate();
			}
		}
		updateWorldTransform(CTM);
		for (auto& el : particleEffects) {
			el->Update(dt);
		}
	}


	void setupDrawNodes() {
		GroupNode* rubikPart;
		ParticleEmitter* pEmitter = new ParticleEmitter;
		pEmitter->MaxWidth = 0.1f;
		pEmitter->MinWidth = -0.1f;
		pEmitter->MaxHeight = 0.1f;
		pEmitter->MinHeight = -0.1f;
		pEmitter->MaxDepth = 0.0f;
		pEmitter->MinDepth = 0.02f;
		pEmitter->MaxSpeed = 1.5;
		pEmitter->MinSpeed = 0.5;
		pEmitter->MaxLifetime = 0.5;
		pEmitter->MinLifetime = 0.3;

		ParticleEffect* pEffectRed = new ParticleEffect(200);
		pEffectRed->SetParticleEmitter(pEmitter);
		pEffectRed->setRotateAxis(glm::vec3(0.25, 0.5, 0.75));
		pEffectRed->setRotateKeyFrames(0.0f, 90.0f);
		pEffectRed->setSizeKeyFrames(0.02, 0.002);
		pEffectRed->setColorKeyFrames(glm::vec3(1.0f, 0.0f, 0.0f), glm::vec3(1.0f, 0.0f, 0.0f));
		particleEffects.push_back(pEffectRed);

		ParticleEffect* pEffectGreen = new ParticleEffect(200);
		pEffectGreen->SetParticleEmitter(pEmitter);
		pEffectGreen->setRotateAxis(glm::vec3(0.25, 0.5, 0.75));
		pEffectGreen->setRotateKeyFrames(0.0f, 90.0f);
		pEffectGreen->setSizeKeyFrames(0.02, 0.002);
		pEffectGreen->setColorKeyFrames(glm::vec3(0.0f, 1.0f, 0.0f), glm::vec3(0.0f, 1.0f, 0.0f));
		particleEffects.push_back(pEffectGreen);

		ParticleEffect* pEffectWhite = new ParticleEffect(200);
		pEffectWhite->SetParticleEmitter(pEmitter);
		pEffectWhite->setRotateAxis(glm::vec3(0.25, 0.5, 0.75));
		pEffectWhite->setRotateKeyFrames(0.0f, 90.0f);
		pEffectWhite->setSizeKeyFrames(0.02, 0.002);
		pEffectWhite->setColorKeyFrames(glm::vec3(1.0f, 1.0f, 1.0f), glm::vec3(1.0f, 1.0f, 1.0f));
		particleEffects.push_back(pEffectWhite);

		ParticleEffect* pEffectYellow = new ParticleEffect(200);
		pEffectYellow->SetParticleEmitter(pEmitter);
		pEffectYellow->setRotateAxis(glm::vec3(0.25, 0.5, 0.75));
		pEffectYellow->setRotateKeyFrames(0.0f, 90.0f);
		pEffectYellow->setSizeKeyFrames(0.02, 0.002);
		pEffectYellow->setColorKeyFrames(glm::vec3(1.0f, 1.0f, 0.0f), glm::vec3(1.0f, 1.0f, 0.0f));
		particleEffects.push_back(pEffectYellow);

		ParticleEffect* pEffectOrange = new ParticleEffect(200);
		pEffectOrange->SetParticleEmitter(pEmitter);
		pEffectOrange->setRotateAxis(glm::vec3(0.25, 0.5, 0.75));
		pEffectOrange->setRotateKeyFrames(0.0f, 90.0f);
		pEffectOrange->setSizeKeyFrames(0.02, 0.002);
		pEffectOrange->setColorKeyFrames(glm::vec3(1.0f, 0.5f, 0.0f), glm::vec3(1.0f, 0.5f, 0.0f));
		particleEffects.push_back(pEffectOrange);

		ParticleEffect* pEffectBlue = new ParticleEffect(200);
		pEffectBlue->SetParticleEmitter(pEmitter);
		pEffectBlue->setRotateAxis(glm::vec3(0.25, 0.5, 0.75));
		pEffectBlue->setRotateKeyFrames(0.0f, 90.0f);
		pEffectBlue->setSizeKeyFrames(0.02, 0.002);
		pEffectBlue->setColorKeyFrames(glm::vec3(0.0f, 0.5f, 1.0f), glm::vec3(0.0f, 0.5f, 1.0f));
		particleEffects.push_back(pEffectBlue);

		//setup the DrawNodes and Quads for all rubikParts
		Drawable* blackQuad = new Quad;//provides the black interior of the cube. Essentially attached slightly towards the inside for every Quad in the RubikCube
		blackQuad->setColours(glm::vec3(0.0f, 0.0f, 0.0f));
		//all Quads facing towards +z, coordinates (x,y,1)
		for (int x = 1; x <= 3; ++x) {
			for (int y = 1; y <= 3; ++y) {
				rubikPart = rubikParts[std::make_tuple(x, y, 1)];
				DrawNode* quadNode;

				quadNode = new DrawNode(pEffectRed);
				quadNode->translate(glm::vec3(0.0f, 0.0f, 0.51f));
				rubikPart->addChild(quadNode);

				quadNode = new DrawNode(blackQuad);
				quadNode->translate(glm::vec3(0.0f, 0.0f, 0.5f));
				quadNode->setCastsShadow(true);
				rubikPart->addChild(quadNode);

				quadNode = new DrawNode(blackQuad);
				quadNode->rotate(glm::vec3(0.0f, 180.0f, 0.0f));
				quadNode->translate(glm::vec3(0.0f, 0.0f, 0.49f));
				quadNode->setCastsShadow(true);
				rubikPart->addChild(quadNode);
			}
		}
		//all Quads facing towards -z, coordinates (x,y,3)
		for (int x = 1; x <= 3; ++x) {
			for (int y = 1; y <= 3; ++y) {
				rubikPart = rubikParts[std::make_tuple(x, y, 3)];
				DrawNode* quadNode;

				quadNode = new DrawNode(pEffectGreen);
				quadNode->rotate(glm::vec3(0.0f, 180.0f, 0.0f));
				quadNode->translate(glm::vec3(0.0f, 0.0f, -0.51f));
				rubikPart->addChild(quadNode);

				quadNode = new DrawNode(blackQuad);
				quadNode->rotate(glm::vec3(0.0f, 180.0f, 0.0f));
				quadNode->translate(glm::vec3(0.0f, 0.0f, -0.5f));
				quadNode->setCastsShadow(true);
				rubikPart->addChild(quadNode);

				quadNode = new DrawNode(blackQuad);
				quadNode->translate(glm::vec3(0.0f, 0.0f, -0.49f));
				quadNode->setCastsShadow(true);
				rubikPart->addChild(quadNode);
			}
		}
		//all Quads facing towards +x, coordinates (3,y,z)
		for (int y = 1; y <= 3; ++y) {
			for (int z = 1; z <= 3; ++z) {
				rubikPart = rubikParts[std::make_tuple(3, y, z)];
				DrawNode* quadNode;

				quadNode = new DrawNode(pEffectWhite);
				quadNode->rotate(glm::vec3(0.0f, 90.0f, 0.0f));
				quadNode->translate(glm::vec3(0.51f, 0.0f, 0.0f));
				rubikPart->addChild(quadNode);

				quadNode = new DrawNode(blackQuad);
				quadNode->rotate(glm::vec3(0.0f, 90.0f, 0.0f));
				quadNode->translate(glm::vec3(0.5f, 0.0f, 0.0f));
				quadNode->setCastsShadow(true);
				rubikPart->addChild(quadNode);

				quadNode = new DrawNode(blackQuad);
				quadNode->rotate(glm::vec3(0.0f, -90.0f, 0.0f));
				quadNode->translate(glm::vec3(0.49f, 0.0f, 0.0f));
				quadNode->setCastsShadow(true);
				rubikPart->addChild(quadNode);
			}
		}
		//all Quads facing towards -x, coordinates (1,y,z)
		for (int y = 1; y <= 3; ++y) {
			for (int z = 1; z <= 3; ++z) {
				rubikPart = rubikParts[std::make_tuple(1, y, z)];
				DrawNode* quadNode;

				quadNode = new DrawNode(pEffectYellow);
				quadNode->rotate(glm::vec3(0.0f, -90.0f, 0.0f));
				quadNode->translate(glm::vec3(-0.51f, 0.0f, 0.0f));
				rubikPart->addChild(quadNode);

				quadNode = new DrawNode(blackQuad);
				quadNode->rotate(glm::vec3(0.0f, -90.0f, 0.0f));
				quadNode->translate(glm::vec3(-0.5f, 0.0f, 0.0f));
				quadNode->setCastsShadow(true);
				rubikPart->addChild(quadNode);

				quadNode = new DrawNode(blackQuad);
				quadNode->rotate(glm::vec3(0.0f, 90.0f, 0.0f));
				quadNode->translate(glm::vec3(-0.49f, 0.0f, 0.0f));
				quadNode->setCastsShadow(true);
				rubikPart->addChild(quadNode);
			}
		}
		//all Quads facing towards +y, coordinates (x,3,z)
		for (int x = 1; x <= 3; ++x) {
			for (int z = 1; z <= 3; ++z) {
				rubikPart = rubikParts[std::make_tuple(x, 3, z)];
				DrawNode* quadNode;

				quadNode = new DrawNode(pEffectOrange);
				quadNode->rotate(glm::vec3(-90.0f, 0.0f, 0.0f));
				quadNode->translate(glm::vec3(0.0f, 0.51f, 0.0f));
				rubikPart->addChild(quadNode);

				quadNode = new DrawNode(blackQuad);
				quadNode->rotate(glm::vec3(-90.0f, 0.0f, 0.0f));
				quadNode->translate(glm::vec3(0.0f, 0.5f, 0.0f));
				quadNode->setCastsShadow(true);
				rubikPart->addChild(quadNode);

				quadNode = new DrawNode(blackQuad);
				quadNode->rotate(glm::vec3(90.0f, 0.0f, 0.0f));
				quadNode->translate(glm::vec3(0.0f, 0.49f, 0.0f));
				quadNode->setCastsShadow(true);
				rubikPart->addChild(quadNode);
			}
		}
		//all Quads facing towards -y, coordinates (x,1,z)
		for (int x = 1; x <= 3; ++x) {
			for (int z = 1; z <= 3; ++z) {
				rubikPart = rubikParts[std::make_tuple(x, 1, z)];
				DrawNode* quadNode;

				quadNode = new DrawNode(pEffectBlue);
				quadNode->rotate(glm::vec3(90.0f, 0.0f, 0.0f));
				quadNode->translate(glm::vec3(0.0f, -0.51f, 0.0f));
				rubikPart->addChild(quadNode);

				quadNode = new DrawNode(blackQuad);
				quadNode->rotate(glm::vec3(90.0f, 0.0f, 0.0f));
				quadNode->translate(glm::vec3(0.0f, -0.5f, 0.0f));
				quadNode->setCastsShadow(true);
				rubikPart->addChild(quadNode);

				quadNode = new DrawNode(blackQuad);
				quadNode->rotate(glm::vec3(-90.0f, 0.0f, 0.0f));
				quadNode->translate(glm::vec3(0.0f, -0.49f, 0.0f));
				quadNode->setCastsShadow(true);
				rubikPart->addChild(quadNode);
			}
		}
	}
};

