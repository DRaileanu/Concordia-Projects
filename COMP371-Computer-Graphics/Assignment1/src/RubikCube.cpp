#include "RubikCube.h"

extern irrklang::ISoundEngine* SoundEngine;
RubikCube::RubikCube() {
	animated = false;
	rotatingWholeCube = false;
	animationDuration = 0.4f;
	//add arrow
	arrow = new DrawNode(new Pyramid);
	arrow->setRotation(glm::vec3(180.0, 0.0f, 0.0f));
	arrow->setTranslation(glm::vec3(0.0f, 3.0f, 0.0f));
	arrow->scale(glm::vec3(0.75, 0.75, 0.75));
	arrow->setTransparency(0.75f);
	addChild(arrow);

	//setup all the rubikParts
	GroupNode* rubikPart;
	//1,1,1
	rubikPart = new GroupNode;
	rubikPart->translate(glm::vec3(-1.0f, -1.0f, 1.0f));
	rubikParts[std::make_tuple(1, 1, 1)] = rubikPart;
	addChild(rubikPart);
	//1,1,2
	rubikPart = new GroupNode;
	rubikPart->translate(glm::vec3(-1.0f, -1.0f, 0.0f));
	rubikParts[std::make_tuple(1, 1, 2)] = rubikPart;
	addChild(rubikPart);
	//1,1,3
	rubikPart = new GroupNode;
	rubikPart->translate(glm::vec3(-1.0f, -1.0f, -1.0f));
	rubikParts[std::make_tuple(1, 1, 3)] = rubikPart;
	addChild(rubikPart);
	//1,2,1
	rubikPart = new GroupNode;
	rubikPart->translate(glm::vec3(-1.0f, 0.0f, 1.0f));
	rubikParts[std::make_tuple(1, 2, 1)] = rubikPart;
	addChild(rubikPart);
	//1,2,2
	rubikPart = new GroupNode;
	rubikPart->translate(glm::vec3(-1.0f, 0.0f, 0.0f));
	rubikParts[std::make_tuple(1, 2, 2)] = rubikPart;
	addChild(rubikPart);
	//1,2,3
	rubikPart = new GroupNode;
	rubikPart->translate(glm::vec3(-1.0f, 0.0f, -1.0f));
	rubikParts[std::make_tuple(1, 2, 3)] = rubikPart;
	addChild(rubikPart);
	//1,3,1
	rubikPart = new GroupNode;
	rubikPart->translate(glm::vec3(-1.0f, 1.0f, 1.0f));
	rubikParts[std::make_tuple(1, 3, 1)] = rubikPart;
	addChild(rubikPart);
	//1,3,2
	rubikPart = new GroupNode;
	rubikPart->translate(glm::vec3(-1.0f, 1.0f, 0.0f));
	rubikParts[std::make_tuple(1, 3, 2)] = rubikPart;
	addChild(rubikPart);
	//1,3,3
	rubikPart = new GroupNode;
	rubikPart->translate(glm::vec3(-1.0f, 1.0f, -1.0f));
	rubikParts[std::make_tuple(1, 3, 3)] = rubikPart;
	addChild(rubikPart);
	//2,1,1
	rubikPart = new GroupNode;
	rubikPart->translate(glm::vec3(0.0f, -1.0f, 1.0f));
	rubikParts[std::make_tuple(2, 1, 1)] = rubikPart;
	addChild(rubikPart);
	//2,1,2
	rubikPart = new GroupNode;
	rubikPart->translate(glm::vec3(0.0f, -1.0f, 0.0f));
	rubikParts[std::make_tuple(2, 1, 2)] = rubikPart;
	addChild(rubikPart);
	//2,1,3
	rubikPart = new GroupNode;
	rubikPart->translate(glm::vec3(0.0f, -1.0f, -1.0f));
	rubikParts[std::make_tuple(2, 1, 3)] = rubikPart;
	addChild(rubikPart);
	//2,2,1
	rubikPart = new GroupNode;
	rubikPart->translate(glm::vec3(0.0f, 0.0f, 1.0f));
	rubikParts[std::make_tuple(2, 2, 1)] = rubikPart;
	addChild(rubikPart);
	//2,2,2
	rubikPart = new GroupNode;
	rubikPart->translate(glm::vec3(0.0f, 0.0f, 0.0f));
	rubikParts[std::make_tuple(2, 2, 2)] = rubikPart;
	addChild(rubikPart);
	//2,2,3
	rubikPart = new GroupNode;
	rubikPart->translate(glm::vec3(0.0f, 0.0f, -1.0f));
	rubikParts[std::make_tuple(2, 2, 3)] = rubikPart;
	addChild(rubikPart);
	//2,3,1
	rubikPart = new GroupNode;
	rubikPart->translate(glm::vec3(0.0f, 1.0f, 1.0f));
	rubikParts[std::make_tuple(2, 3, 1)] = rubikPart;
	addChild(rubikPart);
	//2,3,2
	rubikPart = new GroupNode;
	rubikPart->translate(glm::vec3(0.0f, 1.0f, 0.0f));
	rubikParts[std::make_tuple(2, 3, 2)] = rubikPart;
	addChild(rubikPart);
	//2,3,3
	rubikPart = new GroupNode;
	rubikPart->translate(glm::vec3(0.0f, 1.0f, -1.0f));
	rubikParts[std::make_tuple(2, 3, 3)] = rubikPart;
	addChild(rubikPart);
	//3,1,1
	rubikPart = new GroupNode;
	rubikPart->translate(glm::vec3(1.0f, -1.0f, 1.0f));
	rubikParts[std::make_tuple(3, 1, 1)] = rubikPart;
	addChild(rubikPart);
	//3,1,2
	rubikPart = new GroupNode;
	rubikPart->translate(glm::vec3(1.0f, -1.0f, 0.0f));
	rubikParts[std::make_tuple(3, 1, 2)] = rubikPart;
	addChild(rubikPart);
	//3,1,3
	rubikPart = new GroupNode;
	rubikPart->translate(glm::vec3(1.0f, -1.0f, -1.0f));
	rubikParts[std::make_tuple(3, 1, 3)] = rubikPart;
	addChild(rubikPart);
	//3,2,1
	rubikPart = new GroupNode;
	rubikPart->translate(glm::vec3(1.0f, 0.0f, 1.0f));
	rubikParts[std::make_tuple(3, 2, 1)] = rubikPart;
	addChild(rubikPart);
	//3,2,2
	rubikPart = new GroupNode;
	rubikPart->translate(glm::vec3(1.0f, 0.0f, 0.0f));
	rubikParts[std::make_tuple(3, 2, 2)] = rubikPart;
	addChild(rubikPart);
	//3,2,3
	rubikPart = new GroupNode;
	rubikPart->translate(glm::vec3(1.0f, 0.0f, -1.0f));
	rubikParts[std::make_tuple(3, 2, 3)] = rubikPart;
	addChild(rubikPart);
	//3,3,1
	rubikPart = new GroupNode;
	rubikPart->translate(glm::vec3(1.0f, 1.0f, 1.0f));
	rubikParts[std::make_tuple(3, 3, 1)] = rubikPart;
	addChild(rubikPart);
	//3,3,2
	rubikPart = new GroupNode;
	rubikPart->translate(glm::vec3(1.0f, 1.0f, 0.0f));
	rubikParts[std::make_tuple(3, 3, 2)] = rubikPart;
	addChild(rubikPart);
	//3,3,3
	rubikPart = new GroupNode;
	rubikPart->translate(glm::vec3(1.0f, 1.0f, -1.0f));
	rubikParts[std::make_tuple(3, 3, 3)] = rubikPart;
	addChild(rubikPart);

	selectFace1();
}



void RubikCube::update(const glm::mat4& CTM, float dt) {
	if (animated) {
		if (rotatingWholeCube) {
			rotationAnimationUpdate();
		}
		else {
			animationUpdate();
		}
	}
	updateWorldTransform(CTM);
}

/*
	Handles animation by updating the animationNodes using the animationRotation
	Updates propertionally to how much time passed since last update divided by animationDuration
*/
void RubikCube::animationUpdate() {
	static bool animationInProgress = false;
	static float animationStartFrame = glfwGetTime();
	static float lastFrame = glfwGetTime();

	if (!animationInProgress) {//first time function is called, we simply mark that we are going to start animation starting from next frame
		animationInProgress = true;
		lastFrame = animationStartFrame = glfwGetTime();
	}
	else {
		float currentFrame = glfwGetTime();
		float dt;

		if (animationStartFrame + animationDuration <= currentFrame) {//if time elapsed since start of animation is longer than animationDuration, we manually set dt so that we don't overshot the rotation
			dt = animationStartFrame + animationDuration - lastFrame;
			animationInProgress = false;
			animated = false;
		}
		else {
			dt = currentFrame - lastFrame;
		}
		lastFrame = currentFrame;


		//update all animationNodes
		glm::vec3 deltaRotation = (dt / animationDuration) * animationRotation;
		glm::quat rotationQuaternion(glm::radians(deltaRotation));
		rotationQuaternion = glm::normalize(rotationQuaternion);
		glm::mat4 rotationMatrix = glm::mat4_cast(rotationQuaternion);
		for (auto& animNode : animationNodes) {
			animNode->rotate(rotationMatrix);//rotates the mini-cube relative to it's own center
			//because all mini-cubes are translated relative to the center of the cube, rotating the translation matrix results in rotating about the RubikCube axes
			glm::vec3 translation = animNode->getTranslation();
			//glm::mat4 translationMatrix = glm::translate(glm::mat4(1.0f), translation);
			//glm::mat4 rotatedTranslationMatrix = rotationMatrix * translationMatrix;
			//glm::vec3 rotatedTranslation = glm::vec3(rotatedTranslationMatrix[3]);
			animNode->setTranslation(translation * glm::conjugate(rotationQuaternion));
		}


	}

}


void RubikCube::rotationAnimationUpdate() {
	static bool animationInProgress = false;
	static float animationStartFrame = glfwGetTime();
	static float lastFrame = glfwGetTime();

	if (!animationInProgress) {//first time function is called, we simply mark that we are going to start animation starting from next frame
		animationInProgress = true;
		lastFrame = animationStartFrame = glfwGetTime();
	}
	else {
		float currentFrame = glfwGetTime();
		float dt;

		if (animationStartFrame + animationDuration <= currentFrame) {//if time elapsed since start of animation is longer than animationDuration, we manually set dt so that we don't overshot the rotation
			dt = animationStartFrame + animationDuration - lastFrame;
			animationInProgress = false;
			animated = false;
			rotatingWholeCube = false;
			refreshSelectedFace();
		}
		else {
			dt = currentFrame - lastFrame;
		}
		lastFrame = currentFrame;


		//update all mini cubes in rubikParts
		glm::vec3 deltaRotation = (dt / animationDuration) * wholeCubeRotation;
		glm::quat rotationQuaternion(glm::radians(deltaRotation));
		rotationQuaternion = glm::normalize(rotationQuaternion);
		glm::mat4 rotationMatrix = glm::mat4_cast(rotationQuaternion);
		for (auto& element : rubikParts) {
			element.second->rotate(rotationMatrix);//rotates the mini-cube relative to it's own center
			//because all mini-cubes are translated relative to the center of the cube, rotating the translation matrix results in rotating about the RubikCube axes
			glm::vec3 translation = element.second->getTranslation();
			//glm::mat4 translationMatrix = glm::translate(glm::mat4(1.0f), translation);
			//glm::mat4 rotatedTranslationMatrix = rotationMatrix * translationMatrix;
			//glm::vec3 rotatedTranslation = glm::vec3(rotatedTranslationMatrix[3]);
			element.second->setTranslation(translation * glm::conjugate(rotationQuaternion));
		}

	}
}


/*
	Methods to select appropriate face to animate.
	It essentially choses appropriate nodes from rubikParts and places in animationNodes and updates arrow position/rotation
*/
void RubikCube::selectFace1() {
	if (!animated) {
		selectedFace = 1;
		//clear current selected nodes and add new ones
		animationNodes.clear();
		for (int x = 1; x <= 3; ++x) {
			for (int z = 1; z <= 3; ++z) {
				std::tuple<int, int, int> coord(x, 3, z);
				GroupNode* node = rubikParts[coord];
				animationNodes.push_back(node);
			}
		}
		//update arrow position
		arrow->setTranslation(glm::vec3(0.0f, 3.0f, 0.0f));
		arrow->setRotation(glm::vec3(180.0f, 0.0f, 0.0f));
	}
}

void RubikCube::selectFace2() {
	if (!animated) {
		selectedFace = 2;
		animationNodes.clear();
		for (int x = 1; x <= 3; ++x) {
			for (int y = 1; y <= 3; ++y) {
				std::tuple<int, int, int> coord(x, y, 1);
				GroupNode* node = rubikParts[coord];
				animationNodes.push_back(node);
			}
		}
		arrow->setTranslation(glm::vec3(0.0f, 0.0f, 3.0f));
		arrow->setRotation(glm::vec3(-90.0f, 0.0f, 0.0f));
	}
}

void RubikCube::selectFace3() {
	if (!animated) {
		selectedFace = 3;
		animationNodes.clear();
		for (int y = 1; y <= 3; ++y) {
			for (int z = 1; z <= 3; ++z) {
				std::tuple<int, int, int> coord(3, y, z);
				GroupNode* node = rubikParts[coord];
				animationNodes.push_back(node);
			}
		}
		arrow->setTranslation(glm::vec3(3.0f, 0.0f, 0.0f));
		arrow->setRotation(glm::vec3(0.0f, 0.0f, 90.0f));
	}
}

void RubikCube::selectFace4() {
	if (!animated) {
		selectedFace = 4;
		animationNodes.clear();
		for (int x = 1; x <= 3; ++x) {
			for (int z = 1; z <= 3; ++z) {
				std::tuple<int, int, int> coord(x, 1, z);
				GroupNode* node = rubikParts[coord];
				animationNodes.push_back(node);
			}
		}
		arrow->setTranslation(glm::vec3(0.0f, -4.0f, 0.0f));
		arrow->setRotation(glm::vec3(0.0f, 0.0f, 0.0f));
	}
}

void RubikCube::selectFace5() {
	if (!animated) {
		selectedFace = 5;
		animationNodes.clear();
		for (int y = 1; y <= 3; ++y) {
			for (int z = 1; z <= 3; ++z) {
				std::tuple<int, int, int> coord(1, y, z);
				GroupNode* node = rubikParts[coord];
				animationNodes.push_back(node);
			}
		}
		arrow->setTranslation(glm::vec3(-4.0f, 0.0f, 0.0f));
		arrow->setRotation(glm::vec3(0.0f, 0.0f, -90.0f));
	}
}

void RubikCube::selectFace6() {
	if (!animated) {
		selectedFace = 6;
		animationNodes.clear();
		for (int x = 1; x <= 3; ++x) {
			for (int y = 1; y <= 3; ++y) {
				std::tuple<int, int, int> coord(x, y, 3);
				GroupNode* node = rubikParts[coord];
				animationNodes.push_back(node);
			}
		}
		arrow->setTranslation(glm::vec3(0.0f, 0.0f, -4.0f));
		arrow->setRotation(glm::vec3(90.0f, 0.0f, 0.0f));
	}
}


/*
	Methods to start the animation process. Does nothing if animation already in progress.
	Essentially updates nodes at appropriate coordinates inside rubikParts depending on which face is selected
*/

void RubikCube::rotateFaceCW() {
	if (!animated) {//only do the setup when called without any animation in progress
		animated = true;
		SoundEngine->play2D("audio/turn.mp3");

		std::map<std::tuple<int, int, int>, GroupNode*> temp;
		switch (selectedFace) {
		case 1:
			animationRotation = glm::vec3(0.0f, -90.0f, 0.0f);
			for (int x = 1; x <= 3; ++x) {
				for (int z = 1; z <= 3; ++z) {
					std::tuple<int, int, int> coord(x, 3, z);
					GroupNode* node = rubikParts[coord];
					temp[coord] = node;
				}
			}
			rubikParts.at(std::make_tuple(1, 3, 1)) = temp[std::make_tuple(3, 3, 1)];
			rubikParts.at(std::make_tuple(1, 3, 2)) = temp[std::make_tuple(2, 3, 1)];
			rubikParts.at(std::make_tuple(1, 3, 3)) = temp[std::make_tuple(1, 3, 1)];
			rubikParts.at(std::make_tuple(2, 3, 1)) = temp[std::make_tuple(3, 3, 2)];
			rubikParts.at(std::make_tuple(2, 3, 3)) = temp[std::make_tuple(1, 3, 2)];
			rubikParts.at(std::make_tuple(3, 3, 1)) = temp[std::make_tuple(3, 3, 3)];
			rubikParts.at(std::make_tuple(3, 3, 2)) = temp[std::make_tuple(2, 3, 3)];
			rubikParts.at(std::make_tuple(3, 3, 3)) = temp[std::make_tuple(1, 3, 3)];
			break;
		
		case 2:
			animationRotation = glm::vec3(0.0f, 0.0f, -90.0f);
			for (int x = 1; x <= 3; ++x) {
				for (int y = 1; y <= 3; ++y) {
					std::tuple<int, int, int> coord(x, y, 1);
					GroupNode* node = rubikParts[coord];
					temp[coord] = node;
				}
			}
			rubikParts.at(std::make_tuple(1, 1, 1)) = temp[std::make_tuple(3, 1, 1)];
			rubikParts.at(std::make_tuple(2, 1, 1)) = temp[std::make_tuple(3, 2, 1)];
			rubikParts.at(std::make_tuple(3, 1, 1)) = temp[std::make_tuple(3, 3, 1)];
			rubikParts.at(std::make_tuple(1, 2, 1)) = temp[std::make_tuple(2, 1, 1)];
			rubikParts.at(std::make_tuple(3, 2, 1)) = temp[std::make_tuple(2, 3, 1)];
			rubikParts.at(std::make_tuple(1, 3, 1)) = temp[std::make_tuple(1, 1, 1)];
			rubikParts.at(std::make_tuple(2, 3, 1)) = temp[std::make_tuple(1, 2, 1)];
			rubikParts.at(std::make_tuple(3, 3, 1)) = temp[std::make_tuple(1, 3, 1)];
			break;
		
		case 3:
			animationRotation = glm::vec3(-90.0f, 0.0f, 0.0f);
			for (int y = 1; y <= 3; ++y) {
				for (int z = 1; z <= 3; ++z) {
					std::tuple<int, int, int> coord(3, y, z);
					GroupNode* node = rubikParts[coord];
					temp[coord] = node;
				}
			}
			rubikParts.at(std::make_tuple(3, 1, 1)) = temp[std::make_tuple(3, 1, 3)];
			rubikParts.at(std::make_tuple(3, 1, 2)) = temp[std::make_tuple(3, 2, 3)];
			rubikParts.at(std::make_tuple(3, 1, 3)) = temp[std::make_tuple(3, 3, 3)];
			rubikParts.at(std::make_tuple(3, 2, 1)) = temp[std::make_tuple(3, 1, 2)];
			rubikParts.at(std::make_tuple(3, 2, 3)) = temp[std::make_tuple(3, 3, 2)];
			rubikParts.at(std::make_tuple(3, 3, 1)) = temp[std::make_tuple(3, 1, 1)];
			rubikParts.at(std::make_tuple(3, 3, 2)) = temp[std::make_tuple(3, 2, 1)];
			rubikParts.at(std::make_tuple(3, 3, 3)) = temp[std::make_tuple(3, 3, 1)];
			break;
		
		case 4:
			animationRotation = glm::vec3(0.0f, 90.0f, 0.0f);
			for (int x = 1; x <= 3; ++x) {
				for (int z = 1; z <= 3; ++z) {
					std::tuple<int, int, int> coord(x, 1, z);
					GroupNode* node = rubikParts[coord];
					temp[coord] = node;
				}
			}
			rubikParts.at(std::make_tuple(1, 1, 1)) = temp[std::make_tuple(1, 1, 3)];
			rubikParts.at(std::make_tuple(1, 1, 2)) = temp[std::make_tuple(2, 1, 3)];
			rubikParts.at(std::make_tuple(1, 1, 3)) = temp[std::make_tuple(3, 1, 3)];
			rubikParts.at(std::make_tuple(2, 1, 1)) = temp[std::make_tuple(1, 1, 2)];
			rubikParts.at(std::make_tuple(2, 1, 3)) = temp[std::make_tuple(3, 1, 2)];
			rubikParts.at(std::make_tuple(3, 1, 1)) = temp[std::make_tuple(1, 1, 1)];
			rubikParts.at(std::make_tuple(3, 1, 2)) = temp[std::make_tuple(2, 1, 1)];
			rubikParts.at(std::make_tuple(3, 1, 3)) = temp[std::make_tuple(3, 1, 1)];
			break;
		
		case 5:
			animationRotation = glm::vec3(90.0f, 0.0f, 0.0f);
			for (int y = 1; y <= 3; ++y) {
				for (int z = 1; z <= 3; ++z) {
					std::tuple<int, int, int> coord(1, y, z);
					GroupNode* node = rubikParts[coord];
					temp[coord] = node;
				}
			}
			rubikParts.at(std::make_tuple(1, 1, 1)) = temp[std::make_tuple(1, 3, 1)];
			rubikParts.at(std::make_tuple(1, 1, 2)) = temp[std::make_tuple(1, 2, 1)];
			rubikParts.at(std::make_tuple(1, 1, 3)) = temp[std::make_tuple(1, 1, 1)];
			rubikParts.at(std::make_tuple(1, 2, 1)) = temp[std::make_tuple(1, 3, 2)];
			rubikParts.at(std::make_tuple(1, 2, 3)) = temp[std::make_tuple(1, 1, 2)];
			rubikParts.at(std::make_tuple(1, 3, 1)) = temp[std::make_tuple(1, 3, 3)];
			rubikParts.at(std::make_tuple(1, 3, 2)) = temp[std::make_tuple(1, 2, 3)];
			rubikParts.at(std::make_tuple(1, 3, 3)) = temp[std::make_tuple(1, 1, 3)];
			break;

		case 6:
			animationRotation = glm::vec3(0.0f, 0.0f, 90.0f);
			for (int x = 1; x <= 3; ++x) {
				for (int y = 1; y <= 3; ++y) {
					std::tuple<int, int, int> coord(x, y, 3);
					GroupNode* node = rubikParts[coord];
					temp[coord] = node;
				}
			}
			rubikParts.at(std::make_tuple(1, 1, 3)) = temp[std::make_tuple(1, 3, 3)];
			rubikParts.at(std::make_tuple(2, 1, 3)) = temp[std::make_tuple(1, 2, 3)];
			rubikParts.at(std::make_tuple(3, 1, 3)) = temp[std::make_tuple(1, 1, 3)];
			rubikParts.at(std::make_tuple(1, 2, 3)) = temp[std::make_tuple(2, 3, 3)];
			rubikParts.at(std::make_tuple(3, 2, 3)) = temp[std::make_tuple(2, 1, 3)];
			rubikParts.at(std::make_tuple(1, 3, 3)) = temp[std::make_tuple(3, 3, 3)];
			rubikParts.at(std::make_tuple(2, 3, 3)) = temp[std::make_tuple(3, 2, 3)];
			rubikParts.at(std::make_tuple(3, 3, 3)) = temp[std::make_tuple(3, 1, 3)];
			break;

		};


	}
}

void RubikCube::rotateFaceCCW() {
	if (!animated) {//only do the setup when called without any animation in progress
		animated = true;
		SoundEngine->play2D("audio/turn.mp3");

		std::map<std::tuple<int, int, int>, GroupNode*> temp;
		switch (selectedFace) {
		case 1:
			animationRotation = glm::vec3(0.0f, 90.0f, 0.0f);
			for (int x = 1; x <= 3; ++x) {
				for (int z = 1; z <= 3; ++z) {
					std::tuple<int, int, int> coord(x, 3, z);
					GroupNode* node = rubikParts[coord];
					temp[coord] = node;
				}
			}
			rubikParts.at(std::make_tuple(1, 3, 1)) = temp[std::make_tuple(1, 3, 3)];
			rubikParts.at(std::make_tuple(1, 3, 2)) = temp[std::make_tuple(2, 3, 3)];
			rubikParts.at(std::make_tuple(1, 3, 3)) = temp[std::make_tuple(3, 3, 3)];
			rubikParts.at(std::make_tuple(2, 3, 1)) = temp[std::make_tuple(1, 3, 2)];
			rubikParts.at(std::make_tuple(2, 3, 3)) = temp[std::make_tuple(3, 3, 2)];
			rubikParts.at(std::make_tuple(3, 3, 1)) = temp[std::make_tuple(1, 3, 1)];
			rubikParts.at(std::make_tuple(3, 3, 2)) = temp[std::make_tuple(2, 3, 1)];
			rubikParts.at(std::make_tuple(3, 3, 3)) = temp[std::make_tuple(3, 3, 1)];
			break;

		case 2:
			animationRotation = glm::vec3(0.0f, 0.0f, 90.0f);
			for (int x = 1; x <= 3; ++x) {
				for (int y = 1; y <= 3; ++y) {
					std::tuple<int, int, int> coord(x, y, 1);
					GroupNode* node = rubikParts[coord];
					temp[coord] = node;
				}
			}
			rubikParts.at(std::make_tuple(1, 1, 1)) = temp[std::make_tuple(1, 3, 1)];
			rubikParts.at(std::make_tuple(2, 1, 1)) = temp[std::make_tuple(1, 2, 1)];
			rubikParts.at(std::make_tuple(3, 1, 1)) = temp[std::make_tuple(1, 1, 1)];
			rubikParts.at(std::make_tuple(1, 2, 1)) = temp[std::make_tuple(2, 3, 1)];
			rubikParts.at(std::make_tuple(3, 2, 1)) = temp[std::make_tuple(2, 1, 1)];
			rubikParts.at(std::make_tuple(1, 3, 1)) = temp[std::make_tuple(3, 3, 1)];
			rubikParts.at(std::make_tuple(2, 3, 1)) = temp[std::make_tuple(3, 2, 1)];
			rubikParts.at(std::make_tuple(3, 3, 1)) = temp[std::make_tuple(3, 1, 1)];
			break;

		case 3:
			animationRotation = glm::vec3(90.0f, 0.0f, 0.0f);
			for (int y = 1; y <= 3; ++y) {
				for (int z = 1; z <= 3; ++z) {
					std::tuple<int, int, int> coord(3, y, z);
					GroupNode* node = rubikParts[coord];
					temp[coord] = node;
				}
			}
			rubikParts.at(std::make_tuple(3, 1, 1)) = temp[std::make_tuple(3, 3, 1)];
			rubikParts.at(std::make_tuple(3, 1, 2)) = temp[std::make_tuple(3, 2, 1)];
			rubikParts.at(std::make_tuple(3, 1, 3)) = temp[std::make_tuple(3, 1, 1)];
			rubikParts.at(std::make_tuple(3, 2, 1)) = temp[std::make_tuple(3, 3, 2)];
			rubikParts.at(std::make_tuple(3, 2, 3)) = temp[std::make_tuple(3, 1, 2)];
			rubikParts.at(std::make_tuple(3, 3, 1)) = temp[std::make_tuple(3, 3, 3)];
			rubikParts.at(std::make_tuple(3, 3, 2)) = temp[std::make_tuple(3, 2, 3)];
			rubikParts.at(std::make_tuple(3, 3, 3)) = temp[std::make_tuple(3, 1, 3)];
			break;

		case 4:
			animationRotation = glm::vec3(0.0f, -90.0f, 0.0f);
			for (int x = 1; x <= 3; ++x) {
				for (int z = 1; z <= 3; ++z) {
					std::tuple<int, int, int> coord(x, 1, z);
					GroupNode* node = rubikParts[coord];
					temp[coord] = node;
				}
			}
			rubikParts.at(std::make_tuple(1, 1, 1)) = temp[std::make_tuple(3, 1, 1)];
			rubikParts.at(std::make_tuple(1, 1, 2)) = temp[std::make_tuple(2, 1, 1)];
			rubikParts.at(std::make_tuple(1, 1, 3)) = temp[std::make_tuple(1, 1, 1)];
			rubikParts.at(std::make_tuple(2, 1, 1)) = temp[std::make_tuple(3, 1, 2)];
			rubikParts.at(std::make_tuple(2, 1, 3)) = temp[std::make_tuple(1, 1, 2)];
			rubikParts.at(std::make_tuple(3, 1, 1)) = temp[std::make_tuple(3, 1, 3)];
			rubikParts.at(std::make_tuple(3, 1, 2)) = temp[std::make_tuple(2, 1, 3)];
			rubikParts.at(std::make_tuple(3, 1, 3)) = temp[std::make_tuple(1, 1, 3)];
			break;

		case 5:
			animationRotation = glm::vec3(-90.0f, 0.0f, 0.0f);
			for (int y = 1; y <= 3; ++y) {
				for (int z = 1; z <= 3; ++z) {
					std::tuple<int, int, int> coord(1, y, z);
					GroupNode* node = rubikParts[coord];
					temp[coord] = node;
				}
			}
			rubikParts.at(std::make_tuple(1, 1, 1)) = temp[std::make_tuple(1, 1, 3)];
			rubikParts.at(std::make_tuple(1, 1, 2)) = temp[std::make_tuple(1, 2, 3)];
			rubikParts.at(std::make_tuple(1, 1, 3)) = temp[std::make_tuple(1, 3, 3)];
			rubikParts.at(std::make_tuple(1, 2, 1)) = temp[std::make_tuple(1, 1, 2)];
			rubikParts.at(std::make_tuple(1, 2, 3)) = temp[std::make_tuple(1, 3, 2)];
			rubikParts.at(std::make_tuple(1, 3, 1)) = temp[std::make_tuple(1, 1, 1)];
			rubikParts.at(std::make_tuple(1, 3, 2)) = temp[std::make_tuple(1, 2, 1)];
			rubikParts.at(std::make_tuple(1, 3, 3)) = temp[std::make_tuple(1, 3, 1)];
			break;

		case 6:
			animationRotation = glm::vec3(0.0f, 0.0f, -90.0f);
			for (int x = 1; x <= 3; ++x) {
				for (int y = 1; y <= 3; ++y) {
					std::tuple<int, int, int> coord(x, y, 3);
					GroupNode* node = rubikParts[coord];
					temp[coord] = node;
				}
			}
			rubikParts.at(std::make_tuple(1, 1, 3)) = temp[std::make_tuple(3, 1, 3)];
			rubikParts.at(std::make_tuple(2, 1, 3)) = temp[std::make_tuple(3, 2, 3)];
			rubikParts.at(std::make_tuple(3, 1, 3)) = temp[std::make_tuple(3, 3, 3)];
			rubikParts.at(std::make_tuple(1, 2, 3)) = temp[std::make_tuple(2, 1, 3)];
			rubikParts.at(std::make_tuple(3, 2, 3)) = temp[std::make_tuple(2, 3, 3)];
			rubikParts.at(std::make_tuple(1, 3, 3)) = temp[std::make_tuple(1, 1, 3)];
			rubikParts.at(std::make_tuple(2, 3, 3)) = temp[std::make_tuple(1, 2, 3)];
			rubikParts.at(std::make_tuple(3, 3, 3)) = temp[std::make_tuple(1, 3, 3)];
			break;

		};


	}
}


void RubikCube::rotateXCW() {
	if (!animated) {//only do the setup when called without any animation in progress
		animated = true;
		rotatingWholeCube = true;

		std::map<std::tuple<int, int, int>, GroupNode*> temp;
		//fill the animationNodes and get a copy of all nodes inside temp to do the swapping after
		for (int x = 1; x <= 3; ++x) {
			for (int y = 1; y <= 3; ++y) {
				for (int z = 1; z <= 3; ++z) {
					if (x == 2 && y == 2 && z == 2) { continue; }
					std::tuple<int, int, int> coord(x, y, z);
					GroupNode* node = rubikParts[coord];
					temp[coord] = node;
				}
			}
		}
		wholeCubeRotation = glm::vec3(90.0f, 0.0f, 0.0f);

		rubikParts.at(std::make_tuple(1, 1, 1)) = temp[std::make_tuple(1, 3, 1)];
		rubikParts.at(std::make_tuple(1, 1, 2)) = temp[std::make_tuple(1, 2, 1)];
		rubikParts.at(std::make_tuple(1, 1, 3)) = temp[std::make_tuple(1, 1, 1)];
		rubikParts.at(std::make_tuple(1, 2, 1)) = temp[std::make_tuple(1, 3, 2)];
		rubikParts.at(std::make_tuple(1, 2, 3)) = temp[std::make_tuple(1, 1, 2)];
		rubikParts.at(std::make_tuple(1, 3, 1)) = temp[std::make_tuple(1, 3, 3)];
		rubikParts.at(std::make_tuple(1, 3, 2)) = temp[std::make_tuple(1, 2, 3)];
		rubikParts.at(std::make_tuple(1, 3, 3)) = temp[std::make_tuple(1, 1, 3)];

		rubikParts.at(std::make_tuple(2, 1, 1)) = temp[std::make_tuple(2, 3, 1)];
		rubikParts.at(std::make_tuple(2, 1, 2)) = temp[std::make_tuple(2, 2, 1)];
		rubikParts.at(std::make_tuple(2, 1, 3)) = temp[std::make_tuple(2, 1, 1)];
		rubikParts.at(std::make_tuple(2, 2, 1)) = temp[std::make_tuple(2, 3, 2)];
		rubikParts.at(std::make_tuple(2, 2, 3)) = temp[std::make_tuple(2, 1, 2)];
		rubikParts.at(std::make_tuple(2, 3, 1)) = temp[std::make_tuple(2, 3, 3)];
		rubikParts.at(std::make_tuple(2, 3, 2)) = temp[std::make_tuple(2, 2, 3)];
		rubikParts.at(std::make_tuple(2, 3, 3)) = temp[std::make_tuple(2, 1, 3)];

		rubikParts.at(std::make_tuple(3, 1, 1)) = temp[std::make_tuple(3, 3, 1)];
		rubikParts.at(std::make_tuple(3, 1, 2)) = temp[std::make_tuple(3, 2, 1)];
		rubikParts.at(std::make_tuple(3, 1, 3)) = temp[std::make_tuple(3, 1, 1)];
		rubikParts.at(std::make_tuple(3, 2, 1)) = temp[std::make_tuple(3, 3, 2)];
		rubikParts.at(std::make_tuple(3, 2, 3)) = temp[std::make_tuple(3, 1, 2)];
		rubikParts.at(std::make_tuple(3, 3, 1)) = temp[std::make_tuple(3, 3, 3)];
		rubikParts.at(std::make_tuple(3, 3, 2)) = temp[std::make_tuple(3, 2, 3)];
		rubikParts.at(std::make_tuple(3, 3, 3)) = temp[std::make_tuple(3, 1, 3)];
	

	}
}

void RubikCube::rotateXCCW() {
	if (!animated) {//only do the setup when called without any animation in progress
		animated = true;
		rotatingWholeCube = true;

		std::map<std::tuple<int, int, int>, GroupNode*> temp;
		//fill the animationNodes and get a copy of all nodes inside temp to do the swapping after
		for (int x = 1; x <= 3; ++x) {
			for (int y = 1; y <= 3; ++y) {
				for (int z = 1; z <= 3; ++z) {
					if (x == 2 && y == 2 && z == 2) { continue; }
					std::tuple<int, int, int> coord(x, y, z);
					GroupNode* node = rubikParts[coord];
					temp[coord] = node;
				}
			}
		}
		wholeCubeRotation = glm::vec3(-90.0f, 0.0f, 0.0f);

		rubikParts.at(std::make_tuple(1, 1, 1)) = temp[std::make_tuple(1, 1, 3)];
		rubikParts.at(std::make_tuple(1, 1, 2)) = temp[std::make_tuple(1, 2, 3)];
		rubikParts.at(std::make_tuple(1, 1, 3)) = temp[std::make_tuple(1, 3, 3)];
		rubikParts.at(std::make_tuple(1, 2, 1)) = temp[std::make_tuple(1, 1, 2)];
		rubikParts.at(std::make_tuple(1, 2, 2)) = temp[std::make_tuple(1, 2, 2)];
		rubikParts.at(std::make_tuple(1, 2, 3)) = temp[std::make_tuple(1, 3, 2)];
		rubikParts.at(std::make_tuple(1, 3, 1)) = temp[std::make_tuple(1, 1, 1)];
		rubikParts.at(std::make_tuple(1, 3, 2)) = temp[std::make_tuple(1, 2, 1)];
		rubikParts.at(std::make_tuple(1, 3, 3)) = temp[std::make_tuple(1, 3, 1)];

		rubikParts.at(std::make_tuple(2, 1, 1)) = temp[std::make_tuple(2, 1, 3)];
		rubikParts.at(std::make_tuple(2, 1, 2)) = temp[std::make_tuple(2, 2, 3)];
		rubikParts.at(std::make_tuple(2, 1, 3)) = temp[std::make_tuple(2, 3, 3)];
		rubikParts.at(std::make_tuple(2, 2, 1)) = temp[std::make_tuple(2, 1, 2)];
		rubikParts.at(std::make_tuple(2, 2, 3)) = temp[std::make_tuple(2, 3, 2)];
		rubikParts.at(std::make_tuple(2, 3, 1)) = temp[std::make_tuple(2, 1, 1)];
		rubikParts.at(std::make_tuple(2, 3, 2)) = temp[std::make_tuple(2, 2, 1)];
		rubikParts.at(std::make_tuple(2, 3, 3)) = temp[std::make_tuple(2, 3, 1)];

		rubikParts.at(std::make_tuple(3, 1, 1)) = temp[std::make_tuple(3, 1, 3)];
		rubikParts.at(std::make_tuple(3, 1, 2)) = temp[std::make_tuple(3, 2, 3)];
		rubikParts.at(std::make_tuple(3, 1, 3)) = temp[std::make_tuple(3, 3, 3)];
		rubikParts.at(std::make_tuple(3, 2, 1)) = temp[std::make_tuple(3, 1, 2)];
		rubikParts.at(std::make_tuple(3, 2, 2)) = temp[std::make_tuple(3, 2, 2)];
		rubikParts.at(std::make_tuple(3, 2, 3)) = temp[std::make_tuple(3, 3, 2)];
		rubikParts.at(std::make_tuple(3, 3, 1)) = temp[std::make_tuple(3, 1, 1)];
		rubikParts.at(std::make_tuple(3, 3, 2)) = temp[std::make_tuple(3, 2, 1)];
		rubikParts.at(std::make_tuple(3, 3, 3)) = temp[std::make_tuple(3, 3, 1)];

	}
}

void RubikCube::rotateYCW() {
	if (!animated) {//only do the setup when called without any animation in progress
		animated = true;
		rotatingWholeCube = true;

		std::map<std::tuple<int, int, int>, GroupNode*> temp;
		//fill the animationNodes and get a copy of all nodes inside temp to do the swapping after
		for (int x = 1; x <= 3; ++x) {
			for (int y = 1; y <= 3; ++y) {
				for (int z = 1; z <= 3; ++z) {
					if (x == 2 && y == 2 && z == 2) { continue; }
					std::tuple<int, int, int> coord(x, y, z);
					GroupNode* node = rubikParts[coord];
					temp[coord] = node;
				}
			}
		}
		wholeCubeRotation = glm::vec3(0.0f, -90.0f, 0.0f);


		rubikParts.at(std::make_tuple(1, 1, 1)) = temp[std::make_tuple(3, 1, 1)];
		rubikParts.at(std::make_tuple(1, 1, 2)) = temp[std::make_tuple(2, 1, 1)];
		rubikParts.at(std::make_tuple(1, 1, 3)) = temp[std::make_tuple(1, 1, 1)];
		rubikParts.at(std::make_tuple(2, 1, 1)) = temp[std::make_tuple(3, 1, 2)];
		rubikParts.at(std::make_tuple(2, 1, 2)) = temp[std::make_tuple(2, 1, 2)];
		rubikParts.at(std::make_tuple(2, 1, 3)) = temp[std::make_tuple(1, 1, 2)];
		rubikParts.at(std::make_tuple(3, 1, 1)) = temp[std::make_tuple(3, 1, 3)];
		rubikParts.at(std::make_tuple(3, 1, 2)) = temp[std::make_tuple(2, 1, 3)];
		rubikParts.at(std::make_tuple(3, 1, 3)) = temp[std::make_tuple(1, 1, 3)];

		rubikParts.at(std::make_tuple(1, 2, 1)) = temp[std::make_tuple(3, 2, 1)];
		rubikParts.at(std::make_tuple(1, 2, 2)) = temp[std::make_tuple(2, 2, 1)];
		rubikParts.at(std::make_tuple(1, 2, 3)) = temp[std::make_tuple(1, 2, 1)];
		rubikParts.at(std::make_tuple(2, 2, 1)) = temp[std::make_tuple(3, 2, 2)];
		rubikParts.at(std::make_tuple(2, 2, 3)) = temp[std::make_tuple(1, 2, 2)];
		rubikParts.at(std::make_tuple(3, 2, 1)) = temp[std::make_tuple(3, 2, 3)];
		rubikParts.at(std::make_tuple(3, 2, 2)) = temp[std::make_tuple(2, 2, 3)];
		rubikParts.at(std::make_tuple(3, 2, 3)) = temp[std::make_tuple(1, 2, 3)];

		rubikParts.at(std::make_tuple(1, 3, 1)) = temp[std::make_tuple(3, 3, 1)];
		rubikParts.at(std::make_tuple(1, 3, 2)) = temp[std::make_tuple(2, 3, 1)];
		rubikParts.at(std::make_tuple(1, 3, 3)) = temp[std::make_tuple(1, 3, 1)];
		rubikParts.at(std::make_tuple(2, 3, 1)) = temp[std::make_tuple(3, 3, 2)];
		rubikParts.at(std::make_tuple(2, 3, 2)) = temp[std::make_tuple(2, 3, 2)];
		rubikParts.at(std::make_tuple(2, 3, 3)) = temp[std::make_tuple(1, 3, 2)];
		rubikParts.at(std::make_tuple(3, 3, 1)) = temp[std::make_tuple(3, 3, 3)];
		rubikParts.at(std::make_tuple(3, 3, 2)) = temp[std::make_tuple(2, 3, 3)];
		rubikParts.at(std::make_tuple(3, 3, 3)) = temp[std::make_tuple(1, 3, 3)];

	}
}

void RubikCube::rotateYCCW() {
	if (!animated) {//only do the setup when called without any animation in progress
		animated = true;
		rotatingWholeCube = true;

		std::map<std::tuple<int, int, int>, GroupNode*> temp;
		//fill the animationNodes and get a copy of all nodes inside temp to do the swapping after
		for (int x = 1; x <= 3; ++x) {
			for (int y = 1; y <= 3; ++y) {
				for (int z = 1; z <= 3; ++z) {
					if (x == 2 && y == 2 && z == 2) { continue; }
					std::tuple<int, int, int> coord(x, y, z);
					GroupNode* node = rubikParts[coord];
					temp[coord] = node;
				}
			}
		}
		wholeCubeRotation = glm::vec3(0.0f, 90.0f, 0.0f);


		rubikParts.at(std::make_tuple(1, 1, 1)) = temp[std::make_tuple(1, 1, 3)];
		rubikParts.at(std::make_tuple(1, 1, 2)) = temp[std::make_tuple(2, 1, 3)];
		rubikParts.at(std::make_tuple(1, 1, 3)) = temp[std::make_tuple(3, 1, 3)];
		rubikParts.at(std::make_tuple(2, 1, 1)) = temp[std::make_tuple(1, 1, 2)];
		rubikParts.at(std::make_tuple(2, 1, 2)) = temp[std::make_tuple(2, 1, 2)];
		rubikParts.at(std::make_tuple(2, 1, 3)) = temp[std::make_tuple(3, 1, 2)];
		rubikParts.at(std::make_tuple(3, 1, 1)) = temp[std::make_tuple(1, 1, 1)];
		rubikParts.at(std::make_tuple(3, 1, 2)) = temp[std::make_tuple(2, 1, 1)];
		rubikParts.at(std::make_tuple(3, 1, 3)) = temp[std::make_tuple(3, 1, 1)];

		rubikParts.at(std::make_tuple(1, 2, 1)) = temp[std::make_tuple(1, 2, 3)];
		rubikParts.at(std::make_tuple(1, 2, 2)) = temp[std::make_tuple(2, 2, 3)];
		rubikParts.at(std::make_tuple(1, 2, 3)) = temp[std::make_tuple(3, 2, 3)];
		rubikParts.at(std::make_tuple(2, 2, 1)) = temp[std::make_tuple(1, 2, 2)];
		rubikParts.at(std::make_tuple(2, 2, 3)) = temp[std::make_tuple(3, 2, 2)];
		rubikParts.at(std::make_tuple(3, 2, 1)) = temp[std::make_tuple(1, 2, 1)];
		rubikParts.at(std::make_tuple(3, 2, 2)) = temp[std::make_tuple(2, 2, 1)];
		rubikParts.at(std::make_tuple(3, 2, 3)) = temp[std::make_tuple(3, 2, 1)];

		rubikParts.at(std::make_tuple(1, 3, 1)) = temp[std::make_tuple(1, 3, 3)];
		rubikParts.at(std::make_tuple(1, 3, 2)) = temp[std::make_tuple(2, 3, 3)];
		rubikParts.at(std::make_tuple(1, 3, 3)) = temp[std::make_tuple(3, 3, 3)];
		rubikParts.at(std::make_tuple(2, 3, 1)) = temp[std::make_tuple(1, 3, 2)];
		rubikParts.at(std::make_tuple(2, 3, 2)) = temp[std::make_tuple(2, 3, 2)];
		rubikParts.at(std::make_tuple(2, 3, 3)) = temp[std::make_tuple(3, 3, 2)];
		rubikParts.at(std::make_tuple(3, 3, 1)) = temp[std::make_tuple(1, 3, 1)];
		rubikParts.at(std::make_tuple(3, 3, 2)) = temp[std::make_tuple(2, 3, 1)];
		rubikParts.at(std::make_tuple(3, 3, 3)) = temp[std::make_tuple(3, 3, 1)];

	}
}


void RubikCube::rotateZCW() {
	if (!animated) {//only do the setup when called without any animation in progress
		animated = true;
		rotatingWholeCube = true;

		std::map<std::tuple<int, int, int>, GroupNode*> temp;
		//fill the animationNodes and get a copy of all nodes inside temp to do the swapping after
		for (int x = 1; x <= 3; ++x) {
			for (int y = 1; y <= 3; ++y) {
				for (int z = 1; z <= 3; ++z) {
					if (x == 2 && y == 2 && z == 2) { continue; }
					std::tuple<int, int, int> coord(x, y, z);
					GroupNode* node = rubikParts[coord];
					temp[coord] = node;
				}
			}
		}
		wholeCubeRotation = glm::vec3(0.0f, 0.0f, -90.0f);


		rubikParts.at(std::make_tuple(1, 1, 1)) = temp[std::make_tuple(3, 1, 1)];
		rubikParts.at(std::make_tuple(1, 2, 1)) = temp[std::make_tuple(2, 1, 1)];
		rubikParts.at(std::make_tuple(1, 3, 1)) = temp[std::make_tuple(1, 1, 1)];
		rubikParts.at(std::make_tuple(2, 1, 1)) = temp[std::make_tuple(3, 2, 1)];
		rubikParts.at(std::make_tuple(2, 2, 1)) = temp[std::make_tuple(2, 2, 1)];
		rubikParts.at(std::make_tuple(2, 3, 1)) = temp[std::make_tuple(1, 2, 1)];
		rubikParts.at(std::make_tuple(3, 1, 1)) = temp[std::make_tuple(3, 3, 1)];
		rubikParts.at(std::make_tuple(3, 2, 1)) = temp[std::make_tuple(2, 3, 1)];
		rubikParts.at(std::make_tuple(3, 3, 1)) = temp[std::make_tuple(1, 3, 1)];

		rubikParts.at(std::make_tuple(1, 1, 2)) = temp[std::make_tuple(3, 1, 2)];
		rubikParts.at(std::make_tuple(1, 2, 2)) = temp[std::make_tuple(2, 1, 2)];
		rubikParts.at(std::make_tuple(1, 3, 2)) = temp[std::make_tuple(1, 1, 2)];
		rubikParts.at(std::make_tuple(2, 1, 2)) = temp[std::make_tuple(3, 2, 2)];
		rubikParts.at(std::make_tuple(2, 3, 2)) = temp[std::make_tuple(1, 2, 2)];
		rubikParts.at(std::make_tuple(3, 1, 2)) = temp[std::make_tuple(3, 3, 2)];
		rubikParts.at(std::make_tuple(3, 2, 2)) = temp[std::make_tuple(2, 3, 2)];
		rubikParts.at(std::make_tuple(3, 3, 2)) = temp[std::make_tuple(1, 3, 2)];

		rubikParts.at(std::make_tuple(1, 1, 3)) = temp[std::make_tuple(3, 1, 3)];
		rubikParts.at(std::make_tuple(1, 2, 3)) = temp[std::make_tuple(2, 1, 3)];
		rubikParts.at(std::make_tuple(1, 3, 3)) = temp[std::make_tuple(1, 1, 3)];
		rubikParts.at(std::make_tuple(2, 1, 3)) = temp[std::make_tuple(3, 2, 3)];
		rubikParts.at(std::make_tuple(2, 2, 3)) = temp[std::make_tuple(2, 2, 3)];
		rubikParts.at(std::make_tuple(2, 3, 3)) = temp[std::make_tuple(1, 2, 3)];
		rubikParts.at(std::make_tuple(3, 1, 3)) = temp[std::make_tuple(3, 3, 3)];
		rubikParts.at(std::make_tuple(3, 2, 3)) = temp[std::make_tuple(2, 3, 3)];
		rubikParts.at(std::make_tuple(3, 3, 3)) = temp[std::make_tuple(1, 3, 3)];

	}
}


void RubikCube::rotateZCCW() {
	if (!animated) {//only do the setup when called without any animation in progress
		animated = true;
		rotatingWholeCube = true;

		std::map<std::tuple<int, int, int>, GroupNode*> temp;
		//fill the animationNodes and get a copy of all nodes inside temp to do the swapping after
		for (int x = 1; x <= 3; ++x) {
			for (int y = 1; y <= 3; ++y) {
				for (int z = 1; z <= 3; ++z) {
					if (x == 2 && y == 2 && z == 2) { continue; }
					std::tuple<int, int, int> coord(x, y, z);
					GroupNode* node = rubikParts[coord];
					temp[coord] = node;
				}
			}
		}
		wholeCubeRotation = glm::vec3(0.0f, 0.0f, 90.0f);


		rubikParts.at(std::make_tuple(1, 1, 1)) = temp[std::make_tuple(1, 3, 1)];
		rubikParts.at(std::make_tuple(1, 2, 1)) = temp[std::make_tuple(2, 3, 1)];
		rubikParts.at(std::make_tuple(1, 3, 1)) = temp[std::make_tuple(3, 3, 1)];
		rubikParts.at(std::make_tuple(2, 1, 1)) = temp[std::make_tuple(1, 2, 1)];
		rubikParts.at(std::make_tuple(2, 2, 1)) = temp[std::make_tuple(2, 2, 1)];
		rubikParts.at(std::make_tuple(2, 3, 1)) = temp[std::make_tuple(3, 2, 1)];
		rubikParts.at(std::make_tuple(3, 1, 1)) = temp[std::make_tuple(1, 1, 1)];
		rubikParts.at(std::make_tuple(3, 2, 1)) = temp[std::make_tuple(2, 1, 1)];
		rubikParts.at(std::make_tuple(3, 3, 1)) = temp[std::make_tuple(3, 1, 1)];

		rubikParts.at(std::make_tuple(1, 1, 2)) = temp[std::make_tuple(1, 3, 2)];
		rubikParts.at(std::make_tuple(1, 2, 2)) = temp[std::make_tuple(2, 3, 2)];
		rubikParts.at(std::make_tuple(1, 3, 2)) = temp[std::make_tuple(3, 3, 2)];
		rubikParts.at(std::make_tuple(2, 1, 2)) = temp[std::make_tuple(1, 2, 2)];
		rubikParts.at(std::make_tuple(2, 3, 2)) = temp[std::make_tuple(3, 2, 2)];
		rubikParts.at(std::make_tuple(3, 1, 2)) = temp[std::make_tuple(1, 1, 2)];
		rubikParts.at(std::make_tuple(3, 2, 2)) = temp[std::make_tuple(2, 1, 2)];
		rubikParts.at(std::make_tuple(3, 3, 2)) = temp[std::make_tuple(3, 1, 2)];

		rubikParts.at(std::make_tuple(1, 1, 3)) = temp[std::make_tuple(1, 3, 3)];
		rubikParts.at(std::make_tuple(1, 2, 3)) = temp[std::make_tuple(2, 3, 3)];
		rubikParts.at(std::make_tuple(1, 3, 3)) = temp[std::make_tuple(3, 3, 3)];
		rubikParts.at(std::make_tuple(2, 1, 3)) = temp[std::make_tuple(1, 2, 3)];
		rubikParts.at(std::make_tuple(2, 2, 3)) = temp[std::make_tuple(2, 2, 3)];
		rubikParts.at(std::make_tuple(2, 3, 3)) = temp[std::make_tuple(3, 2, 3)];
		rubikParts.at(std::make_tuple(3, 1, 3)) = temp[std::make_tuple(1, 1, 3)];
		rubikParts.at(std::make_tuple(3, 2, 3)) = temp[std::make_tuple(2, 1, 3)];
		rubikParts.at(std::make_tuple(3, 3, 3)) = temp[std::make_tuple(3, 1, 3)];

	}
}


void RubikCube::refreshSelectedFace() {
	switch (selectedFace) {
	case 1: selectFace1();
		break;
	case 2: selectFace2();
		break;
	case 3: selectFace3();
		break;
	case 4: selectFace4();
		break;
	case 5: selectFace5();
		break;
	case 6: selectFace6();
		break;
	}
}
