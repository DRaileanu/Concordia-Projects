#include "Drawable.h"


Drawable::Drawable() {
	glGenVertexArrays(1, &VAO);
	for (int i = 0; i < NUM_BUFFERS; ++i) {
		bufferObjects[i] = 0;
	}
	type = GL_TRIANGLES;
}

Drawable::~Drawable() {
	glDeleteVertexArrays(1, &VAO);
	glDeleteBuffers(NUM_BUFFERS, bufferObjects);
}

void Drawable::setupBufferData() {
	glBindVertexArray(VAO);
	//positions
	if (vertices.empty()) { return; }
	glGenBuffers(1, &bufferObjects[VERTEX_BUFFER]);
	glBindBuffer(GL_ARRAY_BUFFER, bufferObjects[VERTEX_BUFFER]);
	glBufferData(GL_ARRAY_BUFFER, sizeof(glm::vec3) * vertices.size(), &vertices[0], GL_STATIC_DRAW);
	glVertexAttribPointer(VERTEX_BUFFER, 3, GL_FLOAT, GL_FALSE, 0, 0);
	glEnableVertexAttribArray(VERTEX_BUFFER);
	//normals (if specified)
	if (!normals.empty()) {
		glGenBuffers(1, &bufferObjects[NORMAL_BUFFER]);
		glBindBuffer(GL_ARRAY_BUFFER, bufferObjects[NORMAL_BUFFER]);
		glBufferData(GL_ARRAY_BUFFER, sizeof(glm::vec3) * normals.size(), &normals[0], GL_STATIC_DRAW);
		glVertexAttribPointer(NORMAL_BUFFER, 3, GL_FLOAT, GL_FALSE, 0, 0);
		glEnableVertexAttribArray(NORMAL_BUFFER);
	}
	//colours (if specified)
	if (!colours.empty()) {
		glGenBuffers(1, &bufferObjects[COLOUR_BUFFER]);
		glBindBuffer(GL_ARRAY_BUFFER, bufferObjects[COLOUR_BUFFER]);
		glBufferData(GL_ARRAY_BUFFER, sizeof(glm::vec3) * colours.size(), &colours[0], GL_STATIC_DRAW);
		glVertexAttribPointer(COLOUR_BUFFER, 3, GL_FLOAT, GL_FALSE, 0, 0);
		glEnableVertexAttribArray(COLOUR_BUFFER);
	}
	//textures
	if (!texCoords.empty()) {
		glGenBuffers(1, &bufferObjects[TEXTURE_BUFFER]);
		glBindBuffer(GL_ARRAY_BUFFER, bufferObjects[TEXTURE_BUFFER]);
		glBufferData(GL_ARRAY_BUFFER, sizeof(glm::vec2) * texCoords.size(), &texCoords[0], GL_STATIC_DRAW);
		glVertexAttribPointer(TEXTURE_BUFFER, 2, GL_FLOAT, GL_FALSE, 0, 0);
		glEnableVertexAttribArray(TEXTURE_BUFFER);
	}
	//indices (if specified)
	if (!indices.empty()) {
		glGenBuffers(1, &bufferObjects[INDEX_BUFFER]);
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, bufferObjects[INDEX_BUFFER]);
		glBufferData(GL_ELEMENT_ARRAY_BUFFER, sizeof(unsigned int) * indices.size(), &indices[0], GL_STATIC_DRAW);
	}

	glBindVertexArray(0);
}

void Drawable::draw() {
	glBindVertexArray(VAO);
	if (bufferObjects[INDEX_BUFFER]) {
		glDrawElements(type, indices.size(), GL_UNSIGNED_INT, 0);
	}
	else {
		glDrawArrays(type, 0, vertices.size());
	}

	glBindVertexArray(0);
}


void Drawable::setColours(glm::vec3 newColour) {
	glBindVertexArray(VAO);
	for (auto &colour : colours) {
		colour = newColour;
	}
	glBindBuffer(GL_ARRAY_BUFFER, bufferObjects[COLOUR_BUFFER]);
	glBufferSubData(GL_ARRAY_BUFFER, 0, sizeof(glm::vec3) * colours.size(), &colours[0]);
}