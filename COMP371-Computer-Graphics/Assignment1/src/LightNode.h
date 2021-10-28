#pragma once
#include "SceneNode.h"

//SceneNode that's allowed to have Light Properties.

enum class LightType {
	DirLight, PointLight, SpotLight
};

struct LightProperties {
	glm::vec3 ambient;
	glm::vec3 diffuse;
	glm::vec3 specular;
	//NOTE: if implementing direction or coneAngle, have to modify size and structure of Uniform Interface Block in Renderer
	//glm::vec3 direction;//uncomment if implementing SpotLight/DirectionLight in Renderer
	//float coneAngle;//uncomment if implementing SpotLight in Renderer
};

class LightNode : public SceneNode {
public:
	LightNode() = delete;//have to specify a LightType
	LightNode(LightType t) {
		type = t;
		properties.ambient = glm::vec3(0.0f, 0.0f, 0.0f);
		properties.diffuse = glm::vec3(0.0f, 0.0f, 0.0f);
		properties.specular = glm::vec3(0.0f, 0.0f, 0.0f);
	}
	~LightNode() {}

	LightType getType() { return type; }
	LightProperties getProperties() { return properties; }

	void setType(LightType t) { type = t; }
	void setProperties(LightProperties p) { properties = p; }


private:
	LightType type;
	LightProperties properties;

};

