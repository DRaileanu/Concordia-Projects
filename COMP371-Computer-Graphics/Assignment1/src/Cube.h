#pragma once
#include <glad/glad.h>
#include <glm/glm.hpp>
#include <glm/gtc/matrix_transform.hpp>
#include "Drawable.h"
#include <string>
#include <vector>

class Cube : public Drawable {
public:
	Cube();
	~Cube() {}
};

class TimexCube : public Drawable
{
public:
	TimexCube();
	~TimexCube() {}
};
