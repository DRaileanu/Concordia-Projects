#pragma once
#include "TextureLoader.h"
#include <glad/glad.h>
#include <glm/glm.hpp>
#include <glm/gtc/matrix_transform.hpp>
#include <vector>

//base class for drawable geometry

class Drawable {

public:
	enum AttributeTypes { VERTEX_BUFFER, NORMAL_BUFFER, COLOUR_BUFFER, TEXTURE_BUFFER, INDEX_BUFFER, NUM_BUFFERS };	//layout has to match that of shaders
	Drawable();
	virtual ~Drawable();

	//provides basic implementation for drawing geometry
	virtual void draw();

	void setColours(glm::vec3);//sets all colours to given colour

protected:
	//handles setup of data buffers into coresponding array object
	virtual void setupBufferData();

	GLuint VAO;//array object
	GLuint bufferObjects[NUM_BUFFERS];
	GLuint type;//type of primitive

	std::vector<glm::vec3> vertices;
	std::vector<glm::vec3> normals;
	std::vector<glm::vec3> colours;
	std::vector<glm::vec2> texCoords;
	std::vector<unsigned int> indices;
};

