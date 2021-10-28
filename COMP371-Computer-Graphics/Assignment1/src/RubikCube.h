#pragma once
#include "GroupNode.h"
#include "DrawNode.h"
#include "Quad.h"
#include "irrKlang/irrKlang.h"
#include "Pyramid.h"
#include <map>

//Class that handles the topology and transformations of the RubikCube. The class is abstract, different kinds of Rubic Cubes will have to implement their own setupDrawNodes() according to what type of puzzle they have
/*
	When naming/mapping nodes to rubikParts, it's according to the following convention:
	Think of RubikCube as a 3x3x3 cube, where coordinate 2,2,2 should be thought of as the origin (the middle-most cube)
	The following examples should give an idea how coordinates map when viewing the RubikCube fron front facing side:
	1,1,1: left-bottom-front
	3,1,1: right-bottom-front
	1,3,1: left-upper-front
	3,1,3: right-bottom-back
	3,3,3: right-upper-back
	Notice that there are 27 total permutations representing the 27 mini-cubes making up a RubikCube. However, the mini-cubes are not really cubes, they are just GroupNodes made of 1,2 or 3 Quads.
	Since the middle-most mini-cube is never rendered (coordinate 2,2,2), there are a total of 26 GroupNodes in the RubikCube.
	During rotations, the RubikCube handles swaping Groupnodes to their appropriate coordinates in the rubikParts	

	The face numbers have their normals pointing in the following directions:
	1 : +Y
	2 : +Z
	3 : +X
	4 : -Y
	5 : -X
	6 : -Z
*/

class RubikCube : public GroupNode{
public:
	//RubikCube();
	~RubikCube() {};

	virtual void setupDrawNodes() = 0;//ever

	//methods to select face to apply rotations
	void selectFace1();
	void selectFace2();
	void selectFace3();
	void selectFace4();
	void selectFace5();
	void selectFace6();

	//methods to rotate selected face
	void rotateFaceCW();
	void rotateFaceCCW();

	//methods to rotate faces of the Rubik Cube
	void rotateXCW();
	void rotateXCCW();

	void rotateYCW();
	void rotateYCCW();

	void rotateZCW();
	void rotateZCCW();

protected:
	RubikCube();
	virtual void update(const glm::mat4& CTM, float dt) override;//override SceneNode method in order to call animationUpdate() during an animation
	void animationUpdate();//rotates/translates animationNodes during an animation
	void rotationAnimationUpdate();//performs animation of whole rubik cube, added at last minute, so didn't have time to integrate with animationUpdate together
	void refreshSelectedFace();//after whole cube rotation, need to refresh selected face, as we want it to be the one that rotated in place of old one

	DrawNode* arrow;
	int selectedFace;
	std::map<std::tuple<int, int, int>, GroupNode*> rubikParts;//keeps track of the coordinates of the 26 mini-cubes of the RubikCube
	bool animated;
	std::vector<GroupNode*> animationNodes;//animationUpdate() needs to keep track of which mini-cubes are we animating
	glm::vec3 animationRotation;//animationUpdate() needs to keep track of how are we animating
	float animationDuration;//controls speed of animation

	bool rotatingWholeCube;//to know if need to call rotationAnimationUpdate() or animationUpdate() (because feature added at last minute)
	glm::vec3 wholeCubeRotation;

};

