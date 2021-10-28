#include "Model.h"
#include <glm/gtx/transform2.hpp>


Model::Model(char c) {
	cube = new TimexCube;
	model = new GroupNode;
	model->translate(glm::vec3(0.0f, 0.0f, 0.0f));
	addChild(model);
	switch (c) {
	case ':': {createColumn(); }
			break;
	case '0': {create0(); }
			break;
	case '1': {create1(); }
			break;
	case '2': {create2(); }
			break;
	case '3': {create3(); }
			break;
	case '4': {create4(); }
			break;
	case '5': {create5(); }
			break;
	case '6': {create6(); }
			break;
	case '7': {create7(); }
			break;
	case '8': {create8(); }
			break;
	case '9': {create9(); }
			break;
	}


}

Model::~Model() {
	delete cube;
}

void Model::createColumn() {
	DrawNode* node;

	node = new DrawNode(cube);
	node->scale(glm::vec3(0.25f, 1.0f, 1.0f));
	node->translate(glm::vec3(0.0f, 0.5f, 0.0f));
	model->addChild(node);

	node = new DrawNode(cube);
	node->scale(glm::vec3(0.25f ,1.0f, 1.0f));
	node->translate(glm::vec3(0.0f, -0.5f, 0.0f));
	model->addChild(node);
}
void Model::create0() {
	DrawNode* node;

	node = new DrawNode(cube);
	node->rotate(glm::vec3(0.0f, 0.0f, 90.0f));
	node->translate(glm::vec3(-1.275f, 1.65f, 0.0f));
	model->addChild(node);

	node = new DrawNode(cube);
	node->rotate(glm::vec3(0.0f, 0.0f, 90.0f));
	node->translate(glm::vec3(-1.275f, 0.55f, 0.0f));
	model->addChild(node);

	node = new DrawNode(cube);
	node->rotate(glm::vec3(0.0f, 0.0f, 90.0f));
	node->translate(glm::vec3(-1.275f, -1.65f, 0.0f));
	model->addChild(node);

	node = new DrawNode(cube);
	node->rotate(glm::vec3(0.0f, 0.0f, 90.0f));
	node->translate(glm::vec3(-1.275f, -0.55f, 0.0f));
	model->addChild(node);


	node = new DrawNode(cube);
	node->rotate(glm::vec3(0.0f, 0.0f, 90.0f));
	node->translate(glm::vec3(1.275f, 1.65f, 0.0f));
	model->addChild(node);

	node = new DrawNode(cube);
	node->rotate(glm::vec3(0.0f, 0.0f, 90.0f));
	node->translate(glm::vec3(1.275f, 0.55f, 0.0f));
	model->addChild(node);

	node = new DrawNode(cube);
	node->rotate(glm::vec3(0.0f, 0.0f, 90.0f));
	node->translate(glm::vec3(1.275f, -1.65f, 0.0f));
	model->addChild(node);

	node = new DrawNode(cube);
	node->rotate(glm::vec3(0.0f, 0.0f, 90.0f));
	node->translate(glm::vec3(1.275f, -0.55f, 0.0f));
	model->addChild(node);


	node = new DrawNode(cube);
	node->translate(glm::vec3(-0.55f, 2.2f, 0.0f));
	model->addChild(node);

	node = new DrawNode(cube);
	node->translate(glm::vec3(0.55f, 2.2f, 0.0f));
	model->addChild(node);

	node = new DrawNode(cube);
	node->translate(glm::vec3(-0.55f, -2.2f, 0.0f));
	model->addChild(node);

	node = new DrawNode(cube);
	node->translate(glm::vec3(0.55f, -2.2f, 0.0f));
	model->addChild(node);
}

void Model::create1() {
	DrawNode* node;

	node = new DrawNode(cube);
	node->rotate(glm::vec3(0.0f, 0.0f, 90.0f));
	node->translate(glm::vec3(1.275f, 1.65f, 0.0f));
	model->addChild(node);

	node = new DrawNode(cube);
	node->rotate(glm::vec3(0.0f, 0.0f, 90.0f));
	node->translate(glm::vec3(1.275f, 0.55f, 0.0f));
	model->addChild(node);

	node = new DrawNode(cube);
	node->rotate(glm::vec3(0.0f, 0.0f, 90.0f));
	node->translate(glm::vec3(1.275f, -1.65f, 0.0f));
	model->addChild(node);

	node = new DrawNode(cube);
	node->rotate(glm::vec3(0.0f, 0.0f, 90.0f));
	node->translate(glm::vec3(1.275f, -0.55f, 0.0f));
	model->addChild(node);

}

void Model::create2() {
	DrawNode* node;

	node = new DrawNode(cube);
	node->rotate(glm::vec3(0.0f, 0.0f, 90.0f));
	node->translate(glm::vec3(-1.275f, -1.65f, 0.0f));
	model->addChild(node);

	node = new DrawNode(cube);
	node->rotate(glm::vec3(0.0f, 0.0f, 90.0f));
	node->translate(glm::vec3(-1.275f, -0.55f, 0.0f));
	model->addChild(node);


	node = new DrawNode(cube);
	node->rotate(glm::vec3(0.0f, 0.0f, 90.0f));
	node->translate(glm::vec3(1.275f, 1.65f, 0.0f));
	model->addChild(node);

	node = new DrawNode(cube);
	node->rotate(glm::vec3(0.0f, 0.0f, 90.0f));
	node->translate(glm::vec3(1.275f, 0.55f, 0.0f));
	model->addChild(node);


	node = new DrawNode(cube);
	node->translate(glm::vec3(-0.55f, 2.2f, 0.0f));
	model->addChild(node);

	node = new DrawNode(cube);
	node->translate(glm::vec3(0.55f, 2.2f, 0.0f));
	model->addChild(node);

	node = new DrawNode(cube);
	node->translate(glm::vec3(-0.55f, 0.0f, 0.0f));
	model->addChild(node);

	node = new DrawNode(cube);
	node->translate(glm::vec3(0.55f, 0.0f, 0.0f));
	model->addChild(node);

	node = new DrawNode(cube);
	node->translate(glm::vec3(-0.55f, -2.2f, 0.0f));
	model->addChild(node);

	node = new DrawNode(cube);
	node->translate(glm::vec3(0.55f, -2.2f, 0.0f));
	model->addChild(node);
}

void Model::create3() {
	DrawNode* node;

	node = new DrawNode(cube);
	node->rotate(glm::vec3(0.0f, 0.0f, 90.0f));
	node->translate(glm::vec3(1.275f, 1.65f, 0.0f));
	model->addChild(node);

	node = new DrawNode(cube);
	node->rotate(glm::vec3(0.0f, 0.0f, 90.0f));
	node->translate(glm::vec3(1.275f, 0.55f, 0.0f));
	model->addChild(node);

	node = new DrawNode(cube);
	node->rotate(glm::vec3(0.0f, 0.0f, 90.0f));
	node->translate(glm::vec3(1.275f, -1.65f, 0.0f));
	model->addChild(node);

	node = new DrawNode(cube);
	node->rotate(glm::vec3(0.0f, 0.0f, 90.0f));
	node->translate(glm::vec3(1.275f, -0.55f, 0.0f));
	model->addChild(node);


	node = new DrawNode(cube);
	node->translate(glm::vec3(-0.55f, 2.2f, 0.0f));
	model->addChild(node);

	node = new DrawNode(cube);
	node->translate(glm::vec3(0.55f, 2.2f, 0.0f));
	model->addChild(node);

	node = new DrawNode(cube);
	node->translate(glm::vec3(-0.55f, 0.0f, 0.0f));
	model->addChild(node);

	node = new DrawNode(cube);
	node->translate(glm::vec3(0.55f, 0.0f, 0.0f));
	model->addChild(node);

	node = new DrawNode(cube);
	node->translate(glm::vec3(-0.55f, -2.2f, 0.0f));
	model->addChild(node);

	node = new DrawNode(cube);
	node->translate(glm::vec3(0.55f, -2.2f, 0.0f));
	model->addChild(node);
}

void Model::create4() {
	DrawNode* node;

	node = new DrawNode(cube);
	node->rotate(glm::vec3(0.0f, 0.0f, 90.0f));
	node->translate(glm::vec3(-1.275f, 1.65f, 0.0f));
	model->addChild(node);

	node = new DrawNode(cube);
	node->rotate(glm::vec3(0.0f, 0.0f, 90.0f));
	node->translate(glm::vec3(-1.275f, 0.55f, 0.0f));
	model->addChild(node);

	
	node = new DrawNode(cube);
	node->rotate(glm::vec3(0.0f, 0.0f, 90.0f));
	node->translate(glm::vec3(1.275f, 1.65f, 0.0f));
	model->addChild(node);

	node = new DrawNode(cube);
	node->rotate(glm::vec3(0.0f, 0.0f, 90.0f));
	node->translate(glm::vec3(1.275f, 0.55f, 0.0f));
	model->addChild(node);

	node = new DrawNode(cube);
	node->rotate(glm::vec3(0.0f, 0.0f, 90.0f));
	node->translate(glm::vec3(1.275f, -1.65f, 0.0f));
	model->addChild(node);

	node = new DrawNode(cube);
	node->rotate(glm::vec3(0.0f, 0.0f, 90.0f));
	node->translate(glm::vec3(1.275f, -0.55f, 0.0f));
	model->addChild(node);


	node = new DrawNode(cube);
	node->translate(glm::vec3(-0.55f, 0.0f, 0.0f));
	model->addChild(node);

	node = new DrawNode(cube);
	node->translate(glm::vec3(0.55f, 0.0f, 0.0f));
	model->addChild(node);

}

void Model::create5() {
	DrawNode* node;

	node = new DrawNode(cube);
	node->rotate(glm::vec3(0.0f, 0.0f, 90.0f));
	node->translate(glm::vec3(-1.275f, 1.65f, 0.0f));
	model->addChild(node);

	node = new DrawNode(cube);
	node->rotate(glm::vec3(0.0f, 0.0f, 90.0f));
	node->translate(glm::vec3(-1.275f, 0.55f, 0.0f));
	model->addChild(node);


	node = new DrawNode(cube);
	node->rotate(glm::vec3(0.0f, 0.0f, 90.0f));
	node->translate(glm::vec3(1.275f, -1.65f, 0.0f));
	model->addChild(node);

	node = new DrawNode(cube);
	node->rotate(glm::vec3(0.0f, 0.0f, 90.0f));
	node->translate(glm::vec3(1.275f, -0.55f, 0.0f));
	model->addChild(node);


	node = new DrawNode(cube);
	node->translate(glm::vec3(-0.55f, 2.2f, 0.0f));
	model->addChild(node);

	node = new DrawNode(cube);
	node->translate(glm::vec3(0.55f, 2.2f, 0.0f));
	model->addChild(node);

	node = new DrawNode(cube);
	node->translate(glm::vec3(-0.55f, 0.0f, 0.0f));
	model->addChild(node);

	node = new DrawNode(cube);
	node->translate(glm::vec3(0.55f, 0.0f, 0.0f));
	model->addChild(node);

	node = new DrawNode(cube);
	node->translate(glm::vec3(-0.55f, -2.2f, 0.0f));
	model->addChild(node);

	node = new DrawNode(cube);
	node->translate(glm::vec3(0.55f, -2.2f, 0.0f));
	model->addChild(node);
}

void Model::create6() {
	DrawNode* node;

	node = new DrawNode(cube);
	node->rotate(glm::vec3(0.0f, 0.0f, 90.0f));
	node->translate(glm::vec3(-1.275f, 1.65f, 0.0f));
	model->addChild(node);

	node = new DrawNode(cube);
	node->rotate(glm::vec3(0.0f, 0.0f, 90.0f));
	node->translate(glm::vec3(-1.275f, 0.55f, 0.0f));
	model->addChild(node);

	node = new DrawNode(cube);
	node->rotate(glm::vec3(0.0f, 0.0f, 90.0f));
	node->translate(glm::vec3(-1.275f, -1.65f, 0.0f));
	model->addChild(node);

	node = new DrawNode(cube);
	node->rotate(glm::vec3(0.0f, 0.0f, 90.0f));
	node->translate(glm::vec3(-1.275f, -0.55f, 0.0f));
	model->addChild(node);


	node = new DrawNode(cube);
	node->rotate(glm::vec3(0.0f, 0.0f, 90.0f));
	node->translate(glm::vec3(1.275f, -1.65f, 0.0f));
	model->addChild(node);

	node = new DrawNode(cube);
	node->rotate(glm::vec3(0.0f, 0.0f, 90.0f));
	node->translate(glm::vec3(1.275f, -0.55f, 0.0f));
	model->addChild(node);


	node = new DrawNode(cube);
	node->translate(glm::vec3(-0.55f, 2.2f, 0.0f));
	model->addChild(node);

	node = new DrawNode(cube);
	node->translate(glm::vec3(0.55f, 2.2f, 0.0f));
	model->addChild(node);

	node = new DrawNode(cube);
	node->translate(glm::vec3(-0.55f, 0.0f, 0.0f));
	model->addChild(node);

	node = new DrawNode(cube);
	node->translate(glm::vec3(0.55f, 0.0f, 0.0f));
	model->addChild(node);

	node = new DrawNode(cube);
	node->translate(glm::vec3(-0.55f, -2.2f, 0.0f));
	model->addChild(node);

	node = new DrawNode(cube);
	node->translate(glm::vec3(0.55f, -2.2f, 0.0f));
	model->addChild(node);
}

void Model::create7() {
	DrawNode* node;

	node = new DrawNode(cube);
	node->rotate(glm::vec3(0.0f, 0.0f, 90.0f));
	node->translate(glm::vec3(1.275f, 1.65f, 0.0f));
	model->addChild(node);

	node = new DrawNode(cube);
	node->rotate(glm::vec3(0.0f, 0.0f, 90.0f));
	node->translate(glm::vec3(1.275f, 0.55f, 0.0f));
	model->addChild(node);

	node = new DrawNode(cube);
	node->rotate(glm::vec3(0.0f, 0.0f, 90.0f));
	node->translate(glm::vec3(1.275f, -1.65f, 0.0f));
	model->addChild(node);

	node = new DrawNode(cube);
	node->rotate(glm::vec3(0.0f, 0.0f, 90.0f));
	node->translate(glm::vec3(1.275f, -0.55f, 0.0f));
	model->addChild(node);


	node = new DrawNode(cube);
	node->translate(glm::vec3(-0.55f, 2.2f, 0.0f));
	model->addChild(node);

	node = new DrawNode(cube);
	node->translate(glm::vec3(0.55f, 2.2f, 0.0f));
	model->addChild(node);
}

void Model::create8() {
	DrawNode* node;
	
	node = new DrawNode(cube);
	node->rotate(glm::vec3(0.0f, 0.0f, 90.0f));
	node->translate(glm::vec3(-1.275f, 1.65f, 0.0f));
	model->addChild(node);

	node = new DrawNode(cube);
	node->rotate(glm::vec3(0.0f, 0.0f, 90.0f));
	node->translate(glm::vec3(-1.275f, 0.55f, 0.0f));
	model->addChild(node);

	node = new DrawNode(cube);
	node->rotate(glm::vec3(0.0f, 0.0f, 90.0f));
	node->translate(glm::vec3(-1.275f, -1.65f, 0.0f));
	model->addChild(node);

	node = new DrawNode(cube);
	node->rotate(glm::vec3(0.0f, 0.0f, 90.0f));
	node->translate(glm::vec3(-1.275f, -0.55f, 0.0f));
	model->addChild(node);


	node = new DrawNode(cube);
	node->rotate(glm::vec3(0.0f, 0.0f, 90.0f));
	node->translate(glm::vec3(1.275f, 1.65f, 0.0f));
	model->addChild(node);

	node = new DrawNode(cube);
	node->rotate(glm::vec3(0.0f, 0.0f, 90.0f));
	node->translate(glm::vec3(1.275f, 0.55f, 0.0f));
	model->addChild(node);

	node = new DrawNode(cube);
	node->rotate(glm::vec3(0.0f, 0.0f, 90.0f));
	node->translate(glm::vec3(1.275f, -1.65f, 0.0f));
	model->addChild(node);

	node = new DrawNode(cube);
	node->rotate(glm::vec3(0.0f, 0.0f, 90.0f));
	node->translate(glm::vec3(1.275f, -0.55f, 0.0f));
	model->addChild(node);


	node = new DrawNode(cube);
	node->translate(glm::vec3(-0.55f, 2.2f, 0.0f));
	model->addChild(node);

	node = new DrawNode(cube);
	node->translate(glm::vec3(0.55f, 2.2f, 0.0f));
	model->addChild(node);

	node = new DrawNode(cube);
	node->translate(glm::vec3(-0.55f, 0.0f, 0.0f));
	model->addChild(node);

	node = new DrawNode(cube);
	node->translate(glm::vec3(0.55f, 0.0f, 0.0f));
	model->addChild(node);

	node = new DrawNode(cube);
	node->translate(glm::vec3(-0.55f, -2.2f, 0.0f));
	model->addChild(node);

	node = new DrawNode(cube);
	node->translate(glm::vec3(0.55f, -2.2f, 0.0f));
	model->addChild(node);

}

void Model::create9() {
	DrawNode* node;

	node = new DrawNode(cube);
	node->rotate(glm::vec3(0.0f, 0.0f, 90.0f));
	node->translate(glm::vec3(-1.275f, 1.65f, 0.0f));
	model->addChild(node);

	node = new DrawNode(cube);
	node->rotate(glm::vec3(0.0f, 0.0f, 90.0f));
	node->translate(glm::vec3(-1.275f, 0.55f, 0.0f));
	model->addChild(node);


	node = new DrawNode(cube);
	node->rotate(glm::vec3(0.0f, 0.0f, 90.0f));
	node->translate(glm::vec3(1.275f, 1.65f, 0.0f));
	model->addChild(node);

	node = new DrawNode(cube);
	node->rotate(glm::vec3(0.0f, 0.0f, 90.0f));
	node->translate(glm::vec3(1.275f, 0.55f, 0.0f));
	model->addChild(node);

	node = new DrawNode(cube);
	node->rotate(glm::vec3(0.0f, 0.0f, 90.0f));
	node->translate(glm::vec3(1.275f, -1.65f, 0.0f));
	model->addChild(node);

	node = new DrawNode(cube);
	node->rotate(glm::vec3(0.0f, 0.0f, 90.0f));
	node->translate(glm::vec3(1.275f, -0.55f, 0.0f));
	model->addChild(node);


	node = new DrawNode(cube);
	node->translate(glm::vec3(-0.55f, 2.2f, 0.0f));
	model->addChild(node);

	node = new DrawNode(cube);
	node->translate(glm::vec3(0.55f, 2.2f, 0.0f));
	model->addChild(node);

	node = new DrawNode(cube);
	node->translate(glm::vec3(-0.55f, 0.0f, 0.0f));
	model->addChild(node);

	node = new DrawNode(cube);
	node->translate(glm::vec3(0.55f, 0.0f, 0.0f));
	model->addChild(node);

	node = new DrawNode(cube);
	node->translate(glm::vec3(-0.55f, -2.2f, 0.0f));
	model->addChild(node);

	node = new DrawNode(cube);
	node->translate(glm::vec3(0.55f, -2.2f, 0.0f));
	model->addChild(node);
}


//using lambda recursion, sets textures for all DrawNodes under this Model
void Model::setTexture(GLuint tex) {
	auto traversal = [&](SceneNode* node)->void {
		auto lambda = [&](SceneNode* node, const auto& lambda)->void {
			if (DrawNode* drawNode = dynamic_cast<DrawNode*>(node)) {
				drawNode->setTexture(tex);
				return;
			}
			else if (GroupNode* groupNode = dynamic_cast<GroupNode*>(node)) {
				for (SceneNode* child : groupNode->getChildren()) {
					lambda(child, lambda);
				}
			}
			else return;
		};
		return lambda(node, lambda);
	};
	traversal(this);
}

