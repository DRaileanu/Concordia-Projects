#pragma once
#include <glm/glm.hpp>
#include <glm/gtc/matrix_transform.hpp>
#include <glm/gtc/quaternion.hpp>
#include <vector>

struct Particle
{
    Particle()
        : position(0)
        , velocity(0)
        , color(0)
        , rotate(0)
        , age(0)
        , lifeTime(0)
    {}

    glm::vec3   position;   // Center point of particle
    glm::vec3   velocity;   // Current particle velocity
    glm::vec3   color;      // Particle color
    float       rotate;     // Degrees of rotation (relative to axis inside ParticleEffect)
    float       size;       // Size of the particle
    float       age;        // Current age of particle (seconds)
    float       lifeTime;   // Lifetime of a particle (seconds)

    std::pair<float, float> rotateKeyFrames;
    std::pair<float, float> sizeKeyFrames;
};
