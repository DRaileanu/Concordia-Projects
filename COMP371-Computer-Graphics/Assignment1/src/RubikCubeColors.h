#pragma once
#include "RubikCube.h"

class RubikCubeColors : public RubikCube {
public:
	RubikCubeColors() : RubikCube() {
		setupDrawNodes();
	}
	~RubikCubeColors() {};

	void setupDrawNodes() {
		GroupNode* rubikPart;

		Drawable* redQuad = new Quad;
		redQuad->setColours(glm::vec3(1.0f, 0.0f, 0.0f));
		Drawable* greenQuad = new Quad;
		greenQuad->setColours(glm::vec3(0.0f, 1.0f, 0.0f));
		Drawable* whiteQuad = new Quad;
		whiteQuad->setColours(glm::vec3(1.0f, 1.0f, 1.0f));
		Drawable* yellowQuad = new Quad;
		yellowQuad->setColours(glm::vec3(1.0f, 1.0f, 0.0f));
		Drawable* orangeQuad = new Quad;
		orangeQuad->setColours(glm::vec3(1.0f, 0.5f, 0.0f));
		Drawable* blueQuad = new Quad;
		blueQuad->setColours(glm::vec3(0.0f, 0.0f, 1.0f));

		//setup the DrawNodes and Quads for all rubikParts
		Drawable* blackQuad = new Quad;//provides the black interior of the cube. Essentially attached slightly towards the inside for every Quad in the RubikCube
		blackQuad->setColours(glm::vec3(0.0f, 0.0f, 0.0f));
		//all Quads facing towards +z, coordinates (x,y,1)
		for (int x = 1; x <= 3; ++x) {
			for (int y = 1; y <= 3; ++y) {
				rubikPart = rubikParts[std::make_tuple(x, y, 1)];
				DrawNode* quadNode;

				quadNode = new DrawNode(redQuad);
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

				quadNode = new DrawNode(greenQuad);
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

				quadNode = new DrawNode(whiteQuad);
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

				quadNode = new DrawNode(yellowQuad);
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

				quadNode = new DrawNode(orangeQuad);
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

				quadNode = new DrawNode(blueQuad);
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

