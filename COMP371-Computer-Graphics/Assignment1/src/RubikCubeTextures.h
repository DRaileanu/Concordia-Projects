#pragma once
#include "RubikCube.h" 
#include "TextureLoader.h"

class RubikCubeTextures : public RubikCube {
public:
	RubikCubeTextures() {
		setupDrawNodes();
	}
	~RubikCubeTextures() {}

	void setupDrawNodes() {
		GroupNode* rubikPart;
		Material* textureMaterial = new Material{
			glm::vec3(1.0, 1.0, 1.0),
			glm::vec3(0.5, 0.5, 0.5),
			glm::vec3(0.75, 0.75f, 0.75f),
			32.0
		};
		GLuint heartTex = loadTexture("res/heart.jpg");
		GLuint squareTex = loadTexture("res/square.jpg");
		GLuint pentagonTex = loadTexture("res/pentagon.jpg");
		GLuint starTex = loadTexture("res/star.jpg");
		GLuint circleTex = loadTexture("res/circle.jpg");
		GLuint triangleTex = loadTexture("res/triangle.jpg");

		//setup the DrawNodes and Quads for all rubikParts
		Drawable* quad = new Quad;
		quad->setColours(glm::vec3(0, 0, 0));
		//all Quads facing towards +z, coordinates (x,y,1)
		for (int x = 1; x <= 3; ++x) {
			for (int y = 1; y <= 3; ++y) {
				rubikPart = rubikParts[std::make_tuple(x, y, 1)];
				DrawNode* quadNode;

				quadNode = new DrawNode(quad);
				quadNode->translate(glm::vec3(0.0f, 0.0f, 0.5f));
				quadNode->setMaterial(textureMaterial);
				quadNode->setTexture(heartTex);
				quadNode->setCastsShadow(true);
				rubikPart->addChild(quadNode);

				quadNode = new DrawNode(quad);
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

				quadNode = new DrawNode(quad);
				quadNode->rotate(glm::vec3(0.0f, 180.0f, 0.0f));
				quadNode->translate(glm::vec3(0.0f, 0.0f, -0.5f));
				quadNode->setMaterial(textureMaterial);
				quadNode->setTexture(squareTex);
				quadNode->setCastsShadow(true);
				rubikPart->addChild(quadNode);

				quadNode = new DrawNode(quad);
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

				quadNode = new DrawNode(quad);
				quadNode->rotate(glm::vec3(0.0f, 90.0f, 0.0f));
				quadNode->translate(glm::vec3(0.5f, 0.0f, 0.0f));
				quadNode->setMaterial(textureMaterial);
				quadNode->setTexture(pentagonTex);
				quadNode->setCastsShadow(true);
				rubikPart->addChild(quadNode);

				quadNode = new DrawNode(quad);
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

				quadNode = new DrawNode(quad);
				quadNode->rotate(glm::vec3(0.0f, -90.0f, 0.0f));
				quadNode->translate(glm::vec3(-0.5f, 0.0f, 0.0f));
				quadNode->setMaterial(textureMaterial);
				quadNode->setTexture(starTex);
				quadNode->setCastsShadow(true);
				rubikPart->addChild(quadNode);

				quadNode = new DrawNode(quad);
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

				quadNode = new DrawNode(quad);
				quadNode->rotate(glm::vec3(-90.0f, 0.0f, 0.0f));
				quadNode->translate(glm::vec3(0.0f, 0.5f, 0.0f));
				quadNode->setMaterial(textureMaterial);
				quadNode->setTexture(circleTex);
				quadNode->setCastsShadow(true);
				rubikPart->addChild(quadNode);

				quadNode = new DrawNode(quad);
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

				quadNode = new DrawNode(quad);
				quadNode->rotate(glm::vec3(90.0f, 0.0f, 0.0f));
				quadNode->translate(glm::vec3(0.0f, -0.5f, 0.0f));
				quadNode->setMaterial(textureMaterial);
				quadNode->setTexture(triangleTex);
				quadNode->setCastsShadow(true);
				rubikPart->addChild(quadNode);

				quadNode = new DrawNode(quad);
				quadNode->rotate(glm::vec3(-90.0f, 0.0f, 0.0f));
				quadNode->translate(glm::vec3(0.0f, -0.49f, 0.0f));
				quadNode->setCastsShadow(true);
				rubikPart->addChild(quadNode);
			}
		}
		



	}


};

