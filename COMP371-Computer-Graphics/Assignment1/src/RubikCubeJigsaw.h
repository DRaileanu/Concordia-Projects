#pragma once
#include "RubikCube.h" 
#include "TextureLoader.h"

class RubikCubeJigsaw : public RubikCube {
public:
	RubikCubeJigsaw() {
		setupDrawNodes();
	}
	~RubikCubeJigsaw() {}

	void setupDrawNodes() {
		GroupNode* rubikPart;
		Material* textureMaterial = new Material{
			glm::vec3(1.0, 1.0, 1.0),
			glm::vec3(1.5, 1.5, 1.5),
			glm::vec3(0.75, 0.75f, 0.75f),
			32.0
		};
		GLuint jigsaw1 = loadTexture("res/jigsaw1.jpg");
		GLuint jigsaw2 = loadTexture("res/jigsaw2.jpg");
		GLuint jigsaw3 = loadTexture("res/jigsaw3.jpg");
		GLuint jigsaw4 = loadTexture("res/jigsaw4.jpg");
		GLuint jigsaw5 = loadTexture("res/jigsaw5.jpg");
		GLuint jigsaw6 = loadTexture("res/jigsaw6.jpg");

		std::vector<glm::vec2> bottomLeftCoords = {
			glm::vec2(0.00f, 0.00f),
			glm::vec2(0.33f, 0.00f),
			glm::vec2(0.33f, 0.33f),
			glm::vec2(0.00f, 0.00f),
			glm::vec2(0.33f, 0.33f),
			glm::vec2(0.00f, 0.33f)
		};
		std::vector<glm::vec2> bottomCenterCoords = {
			glm::vec2(0.33f, 0.00f),
			glm::vec2(0.66f, 0.00f),
			glm::vec2(0.66f, 0.33f),
			glm::vec2(0.33f, 0.00f),
			glm::vec2(0.66f, 0.33f),
			glm::vec2(0.33f, 0.33f)
		};
		std::vector<glm::vec2> bottomRightCoords = {
			glm::vec2(0.66f, 0.00f),
			glm::vec2(1.00f, 0.00f),
			glm::vec2(1.00f, 0.33f),
			glm::vec2(0.66f, 0.00f),
			glm::vec2(1.00f, 0.33f),
			glm::vec2(0.66f, 0.33f)
		};

		std::vector<glm::vec2> middleLeftCoords = {
			glm::vec2(0.00f, 0.33f),
			glm::vec2(0.33f, 0.33f),
			glm::vec2(0.33f, 0.66f),
			glm::vec2(0.00f, 0.33f),
			glm::vec2(0.33f, 0.66f),
			glm::vec2(0.00f, 0.66f)
		};
		std::vector<glm::vec2> middleCenterCoords = {
			glm::vec2(0.33f, 0.33f),
			glm::vec2(0.66f, 0.33f),
			glm::vec2(0.66f, 0.66f),
			glm::vec2(0.33f, 0.33f),
			glm::vec2(0.66f, 0.66f),
			glm::vec2(0.33f, 0.66f)
		};
		std::vector<glm::vec2> middleRightCoords = {
			glm::vec2(0.66f, 0.33f),
			glm::vec2(1.00f, 0.33f),
			glm::vec2(1.00f, 0.66),
			glm::vec2(0.66f, 0.33f),
			glm::vec2(1.00f, 0.66f),
			glm::vec2(0.66f, 0.66f)
		};
		std::vector<glm::vec2> upperLeftCoords = {
			glm::vec2(0.00f, 0.66f),
			glm::vec2(0.33f, 0.66f),
			glm::vec2(0.33f, 1.00f),
			glm::vec2(0.00f, 0.66f),
			glm::vec2(0.33f, 1.00f),
			glm::vec2(0.00f, 1.00f)
		};
		std::vector<glm::vec2> upperCenterCoords = {
			glm::vec2(0.33f, 0.66f),
			glm::vec2(0.66f, 0.66f),
			glm::vec2(0.66f, 1.00f),
			glm::vec2(0.33f, 0.66f),
			glm::vec2(0.66f, 1.00f),
			glm::vec2(0.33f, 1.00f)
		};
		std::vector<glm::vec2> upperRightCoords = {
			glm::vec2(0.66f, 0.66f),
			glm::vec2(1.00f, 0.66f),
			glm::vec2(1.00f, 1.00f),
			glm::vec2(0.66f, 0.66f),
			glm::vec2(1.00f, 1.00f),
			glm::vec2(0.66f, 1.00f)
		};

		//setup the DrawNodes and Quads for all rubikParts
		//first crate the quads
		std::map<std::pair<int, int>, Quad*> quadsMap;//used to later assign appripriate Quad to a DrawNode
		Quad* blackQuad = new Quad;
		blackQuad->setColours(glm::vec3(0, 0, 0));

		Quad* bottomLeftQuad = new Quad;
		bottomLeftQuad->setTextureCoords(bottomLeftCoords);
		quadsMap[std::make_pair(1, 1)] = bottomLeftQuad;

		Quad* bottomCenterQuad = new Quad;
		bottomCenterQuad->setTextureCoords(bottomCenterCoords);
		quadsMap[std::make_pair(2, 1)] = bottomCenterQuad;
		
		Quad* bottomRightQuad = new Quad;
		bottomRightQuad->setTextureCoords(bottomRightCoords);
		quadsMap[std::make_pair(3, 1)] = bottomRightQuad;
		
		Quad* middleLeftQuad = new Quad;
		middleLeftQuad->setTextureCoords(middleLeftCoords);
		quadsMap[std::make_pair(1, 2)] = middleLeftQuad;
		
		Quad* middleCenterQuad = new Quad;
		middleCenterQuad->setTextureCoords(middleCenterCoords);
		quadsMap[std::make_pair(2, 2)] = middleCenterQuad;
		
		Quad* middleRightQuad = new Quad;
		middleRightQuad->setTextureCoords(middleRightCoords);
		quadsMap[std::make_pair(3, 2)] = middleRightQuad;
		
		Quad* upperLeftQuad = new Quad;
		upperLeftQuad->setTextureCoords(upperLeftCoords);
		quadsMap[std::make_pair(1, 3)] = upperLeftQuad;
		
		Quad* upperCenterQuad = new Quad;
		upperCenterQuad->setTextureCoords(upperCenterCoords);
		quadsMap[std::make_pair(2, 3)] = upperCenterQuad;
		
		Quad* upperRightQuad = new Quad;
		upperRightQuad->setTextureCoords(upperRightCoords);
		quadsMap[std::make_pair(3, 3)] = upperRightQuad;

		//all Quads facing towards +z, coordinates (x,y,1)
		for (int x = 1; x <= 3; ++x) {
			for (int y = 1; y <= 3; ++y) {
				rubikPart = rubikParts[std::make_tuple(x, y, 1)];
				DrawNode* quadNode;
				
				quadNode = new DrawNode(quadsMap[std::make_pair(x,y)]);
				quadNode->translate(glm::vec3(0.0f, 0.0f, 0.5f));
				quadNode->setMaterial(textureMaterial);
				quadNode->setTexture(jigsaw1);
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

				quadNode = new DrawNode(quadsMap[std::make_pair(4-x, y)]);
				quadNode->rotate(glm::vec3(0.0f, 180.0f, 0.0f));
				quadNode->translate(glm::vec3(0.0f, 0.0f, -0.5f));
				quadNode->setMaterial(textureMaterial);
				quadNode->setTexture(jigsaw2);
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

				quadNode = new DrawNode(quadsMap[std::make_pair(z, y)]);
				quadNode->rotate(glm::vec3(0.0f, 90.0f, 0.0f));
				quadNode->translate(glm::vec3(0.5f, 0.0f, 0.0f));
				quadNode->setMaterial(textureMaterial);
				quadNode->setTexture(jigsaw3);
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

				quadNode = new DrawNode(quadsMap[std::make_pair(4-z, y)]);
				quadNode->rotate(glm::vec3(0.0f, -90.0f, 0.0f));
				quadNode->translate(glm::vec3(-0.5f, 0.0f, 0.0f));
				quadNode->setMaterial(textureMaterial);
				quadNode->setTexture(jigsaw4);
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

				quadNode = new DrawNode(quadsMap[std::make_pair(x, z)]);
				quadNode->rotate(glm::vec3(-90.0f, 0.0f, 0.0f));
				quadNode->translate(glm::vec3(0.0f, 0.5f, 0.0f));
				quadNode->setMaterial(textureMaterial);
				quadNode->setTexture(jigsaw5);
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

				quadNode = new DrawNode(quadsMap[std::make_pair(x, 4-z)]);
				quadNode->rotate(glm::vec3(90.0f, 0.0f, 0.0f));
				quadNode->translate(glm::vec3(0.0f, -0.5f, 0.0f));
				quadNode->setMaterial(textureMaterial);
				quadNode->setTexture(jigsaw6);
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