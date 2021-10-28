#include "ParticleEmitter.h"
#include "Random.h"


ParticleEmitter::ParticleEmitter() :
	MinWidth(-1),
	MaxWidth(-1),
	MinHeight(-1),
	MaxHeight(-1),
	MinDepth(-1),
	MaxDepth(-1),
	MinSpeed(5),
	MaxSpeed(11),
	MinLifetime(0.5),
	MaxLifetime(1.25),
	Origin(0)
{}

void ParticleEmitter::EmitParticle(Particle& particle) {
	float x = RandRange(MinWidth, MaxWidth);
	float y = RandRange(MinHeight, MaxHeight);
	float z = RandRange(MinDepth, MaxDepth);

	float lifetime = RandRange(MinLifetime, MaxLifetime);
	float speed = RandRange(MinSpeed, MaxSpeed);

	glm::vec3 vector(x, y, z);

	particle.position = vector + Origin;
	particle.velocity = glm::normalize(vector) * speed;

	particle.lifeTime = lifetime;
	particle.age = 0;
}