#pragma once
#include "Drawable.h"
#include "Particle.h"
#include "ParticleEmitter.h"
#include "Random.h"
#include <vector>

//class creates, updates and moves around many small cubes to act as decoration. It's hard coded to work on a 100x100 grid and move the mini-cubes a distance of 1 before having the options to turn. If reach edge of grid, it reverses direction


class DecorativeCubes : public Drawable {
	static const unsigned int MAX_CUBES = 10000;

	// private struct for a single Decorative Cube that travels on xz plane
	struct DecorativeCube {
		DecorativeCube() {
			int xPos = RandRange(-48, 48);
			int zPos = RandRange(-48, 48);
			position = glm::vec3(xPos, 0.0f, zPos);
			color = glm::vec3(RandUnitVec());
			keyframeProgress = 0.0f;
			keyframeDuration = 0.4f;
			keyframePos1 = position;

			switch (rand() % 4) {
			case 0:	direction = glm::vec3(1.0f, 0.0f, 0.0f);
				break;
			case 1:	direction = glm::vec3(-1.0f, 0.0f, 0.0f);
				break;
			case 2:	direction = glm::vec3(0.0f, 0.0f, 1.0f);
				break;
			case 3:	direction = glm::vec3(0.0f, 0.0f, -1.0f);
				break;
			}
			keyframePos2 = keyframePos1 + direction;
		}
        glm::vec3   position; 
		glm::vec3	direction;
		glm::vec3   color;
		glm::vec3	keyframePos1;
		glm::vec3	keyframePos2;
		float		keyframeProgress;
		float		keyframeDuration;
	};


public:
	DecorativeCubes(unsigned int numCubes = 0);
	virtual ~DecorativeCubes() {}

	void draw() override;

	virtual void Update(float dt);

	void Resize(unsigned int numCubes);//resize number of decorative cubes

	void addCubes(unsigned int numCubes);//adds numCubes as long as still under MAX_CUBES
	void removeCubes(unsigned int numCubes);//removes numCubes unless already 0

protected:
	void setupBufferData() override;
	void BuildVertexBuffer();

	std::vector<DecorativeCube>	cubes;

};

