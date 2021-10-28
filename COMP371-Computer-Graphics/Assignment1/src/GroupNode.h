#pragma once
#include "SceneNode.h"
#include <algorithm>

//Only SceneNode that is allowed to have children. Use to provide SceneGraph topology

class GroupNode : public SceneNode {
public:
	GroupNode() {};
	~GroupNode() {
		for (auto child : children) delete child;
	}

	void addChild(SceneNode*child) {
		children.push_back(child);
	}

	void removeChild(SceneNode* child) {
		if (std::find(children.begin(), children.end(), child) != children.end()) {
			children.erase(std::find(children.begin(), children.end(), child));
		}
	}

	std::vector<SceneNode*> getChildren() { return children; }

protected:
	std::vector<SceneNode*> children;
};

