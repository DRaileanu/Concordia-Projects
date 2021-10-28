#pragma once
#include "SceneNode.h"
#include "Drawable.h"

//only SceneNode allowed to have Drawable* and draw()
//if want lighting to affect the DrawNode, needs Material to be set, otherwise draws using genericShader

struct Material {
	glm::vec3 ambient;
	glm::vec3 diffuse;
	glm::vec3 specular;
	float shininess;
};

class DrawNode : public SceneNode {
public:
	DrawNode() {}
	DrawNode(Drawable* d) : drawable(d), material(NULL), texture(0), transparency(0) {}
	~DrawNode() {}

	Drawable* getDrawable() { return drawable; }
	void setDrawable(Drawable* d) { drawable = d; }

	void draw() {
		if (drawable) {
			drawable->draw();
		}
	}

	Material* getMaterial() { return material; }
	void setMaterial(Material* m) { material = m; }

	GLuint getTexture() { return texture; }
	void setTexture(GLuint tex) { texture = tex; }

	float getTransparency() { return transparency; }
	void setTransparency(float t) { transparency = t; }

	bool getCastsShadow() { return castsShadow; }
	void setCastsShadow(bool b) { castsShadow = b; }

private:
	Drawable* drawable;
	Material* material;
	GLuint texture;
	float transparency;
	bool castsShadow = false;
};

