#pragma once

#include "Drawable.h"
#include "Particle.h"
#include "ParticleEmitter.h"
#include <vector>


/**
 * ParticleEffect class that sets up, updates and renders  particles.
 * The general approach and some code was inspired from https://www.3dgep.com/simulating-particle-effects-using-opengl/#Taking_full_advantage_of_the_power_of_the_GPU
 * Although particles are normally made using billboard quads, these particles will be rotated/translated around as a 3d object, hence they are modeled as cubes.
 */


class ParticleEffect : public Drawable {
    static const unsigned int MAX_PARTICLES = 1000;
public:
    ParticleEffect(unsigned int numParticles = 1);
    virtual ~ParticleEffect();

    //current implementation has indices map to invalid vertices if there are less particles than MAX_PARTICLES
    //so need to override draw in order to draw according to particles.size() instead of indices.size()
    //this is simply done to boost fps
    void draw() override;

    void SetParticleEmitter(ParticleEmitter* pEmitter);

    // Test method to randomize the particles in an interesting way
    void RandomizeParticles();
    //void EmitParticles();

    virtual void Update(float dt);

    // Resize the particle buffer with numParticles
    void Resize(unsigned int numParticles);

    void setForce(glm::vec3 f) { force = f; }
    void setRotateAxis(glm::vec3 axis) { rotateAxis = glm::normalize(axis); }
    void setRotateKeyFrames(float begin, float end) { rotateKeyFrames = std::make_pair(begin, end); }
    void setSizeKeyFrames(float begin, float end) { sizeKeyFrames = std::make_pair(begin, end); }
    void setColorKeyFrames(glm::vec3 begin, glm::vec3 end) { colorKeyFrames = std::make_pair(begin, end); }

protected:
    void setupBufferData() override;//override, since we make buffer big enough to hold MAX_PARTICLES particles even if have less
    void RandomizeParticle(Particle& particle);//creates new particle when when no ParticleEmitter is present
    void EmitParticle(Particle& particle);//creates new particle using ParticleEmitter
    void BuildVertexBuffer();// Build the vertex buffer from the particle buffer

    ParticleEmitter*        particleEmitter;

    std::vector<Particle>   particles;
    // Apply this force to every particle in the effect
    glm::vec3               force;//force that affects velocity at every update
    glm::vec3               rotateAxis;//axis on which particles rotate at every update
    std::pair<float, float> rotateKeyFrames;//rotation keyframes during the life of a particle (in degrees)
    std::pair<float, float> sizeKeyFrames;//size keyframes during lifetime of particle
    std::pair<glm::vec3, glm::vec3> colorKeyFrames;//color keyframes during lifetime of particle
};