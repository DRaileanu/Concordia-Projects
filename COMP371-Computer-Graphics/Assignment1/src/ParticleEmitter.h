#pragma once;
#include "Particle.h"

// Particle emitter creates random properties of a Particle
// Note that it does not actually create the new particle, it takes one as argument and sets its properties

class ParticleEmitter {
public:
    ParticleEmitter();
    virtual ~ParticleEmitter() {}

    virtual void EmitParticle(Particle& particle);

    float MinWidth;
    float MaxWidth;

    float MinHeight;
    float MaxHeight;

    float MinDepth;
    float MaxDepth;

    float MinSpeed;
    float MaxSpeed;

    float MinLifetime;
    float MaxLifetime;

    glm::vec3   Origin;
};