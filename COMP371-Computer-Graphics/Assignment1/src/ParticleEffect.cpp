#include "ParticleEmitter.h"
#include "Random.h"
#include "ParticleEffect.h"
#include <algorithm>


ParticleEffect::ParticleEffect(unsigned int numParticles) :
    particleEmitter(NULL),
    force(0, 0, 0),
    rotateAxis(0.0f, 0.0f, 1.0f),//if manually changing, make sure it's normalized
    rotateKeyFrames(0.0f, 90.0f),
    sizeKeyFrames(1.0f, 0.0f),
    colorKeyFrames(glm::vec3(1.0f, 1.0f, 1.0f), glm::vec3(0.0f, 0.0f, 0.0f))

{
    Resize(numParticles);

    //map indices to vertex positions
    //it's possible to have indices map to invalid vertices if there are less particles than MAX_PARTICLES
    //this is ok, since we override the draw method to only draw using indices of actual number of particles
    indices.resize(36 * MAX_PARTICLES);
    for (int i = 0; i < MAX_PARTICLES; ++i) {
        unsigned int vertexIndex = i * 8;
        unsigned int indicesIndex = i * 36;

        indices[indicesIndex + 0] = vertexIndex + 0;
        indices[indicesIndex + 1] = vertexIndex + 1;
        indices[indicesIndex + 2] = vertexIndex + 2;

        indices[indicesIndex + 3] = vertexIndex + 0;
        indices[indicesIndex + 4] = vertexIndex + 2;
        indices[indicesIndex + 5] = vertexIndex + 3;

        indices[indicesIndex + 6] = vertexIndex + 5;
        indices[indicesIndex + 7] = vertexIndex + 4;
        indices[indicesIndex + 8] = vertexIndex + 7;

        indices[indicesIndex + 9] = vertexIndex + 5;
        indices[indicesIndex + 10] = vertexIndex + 7;
        indices[indicesIndex + 11] = vertexIndex + 6;

        indices[indicesIndex + 12] = vertexIndex + 4;
        indices[indicesIndex + 13] = vertexIndex + 0;
        indices[indicesIndex + 14] = vertexIndex + 3;

        indices[indicesIndex + 15] = vertexIndex + 4;
        indices[indicesIndex + 16] = vertexIndex + 3;
        indices[indicesIndex + 17] = vertexIndex + 7;

        indices[indicesIndex + 18] = vertexIndex + 1;
        indices[indicesIndex + 19] = vertexIndex + 5;
        indices[indicesIndex + 20] = vertexIndex + 6;

        indices[indicesIndex + 21] = vertexIndex + 1;
        indices[indicesIndex + 22] = vertexIndex + 6;
        indices[indicesIndex + 23] = vertexIndex + 2;

        indices[indicesIndex + 24] = vertexIndex + 4;
        indices[indicesIndex + 25] = vertexIndex + 5;
        indices[indicesIndex + 26] = vertexIndex + 1;

        indices[indicesIndex + 27] = vertexIndex + 4;
        indices[indicesIndex + 28] = vertexIndex + 1;
        indices[indicesIndex + 29] = vertexIndex + 0;

        indices[indicesIndex + 30] = vertexIndex + 3;
        indices[indicesIndex + 31] = vertexIndex + 2;
        indices[indicesIndex + 32] = vertexIndex + 6;

        indices[indicesIndex + 33] = vertexIndex + 3;
        indices[indicesIndex + 34] = vertexIndex + 6;
        indices[indicesIndex + 35] = vertexIndex + 7;
    }

    setupBufferData();
}

ParticleEffect::~ParticleEffect() {}

void ParticleEffect::Resize(unsigned int numParticles)
{
    numParticles = std::min(numParticles, MAX_PARTICLES);
    particles.resize(numParticles, Particle());
    vertices.resize(8 * numParticles);
    colours.resize(8 * numParticles);
}

void ParticleEffect::setupBufferData() {
    glBindVertexArray(VAO);
    //positions
    glGenBuffers(1, &bufferObjects[VERTEX_BUFFER]);
    glBindBuffer(GL_ARRAY_BUFFER, bufferObjects[VERTEX_BUFFER]);
    glBufferData(GL_ARRAY_BUFFER, sizeof(glm::vec3) * 8 * MAX_PARTICLES, NULL, GL_DYNAMIC_DRAW);
    glVertexAttribPointer(VERTEX_BUFFER, 3, GL_FLOAT, GL_FALSE, 0, 0);
    glEnableVertexAttribArray(VERTEX_BUFFER);

    //colours
    glGenBuffers(1, &bufferObjects[COLOUR_BUFFER]);
    glBindBuffer(GL_ARRAY_BUFFER, bufferObjects[COLOUR_BUFFER]);
    glBufferData(GL_ARRAY_BUFFER, sizeof(glm::vec3) * 8 * MAX_PARTICLES, NULL, GL_DYNAMIC_DRAW);
    glVertexAttribPointer(COLOUR_BUFFER, 3, GL_FLOAT, GL_FALSE, 0, 0);
    glEnableVertexAttribArray(COLOUR_BUFFER);

    glGenBuffers(1, &bufferObjects[INDEX_BUFFER]);
    glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, bufferObjects[INDEX_BUFFER]);
    glBufferData(GL_ELEMENT_ARRAY_BUFFER, sizeof(unsigned int) * 36 * MAX_PARTICLES, indices.data(), GL_STATIC_DRAW);

    glBindVertexArray(0);
}


void ParticleEffect::draw() {
    glBindVertexArray(VAO);
    glDrawElements(type, particles.size() * 36, GL_UNSIGNED_INT, 0);
}


void ParticleEffect::SetParticleEmitter(ParticleEmitter* pEmitter)
{
    particleEmitter = pEmitter;
}


void ParticleEffect::RandomizeParticle(Particle& particle)
{
    particle.age = 0.0f;
    particle.lifeTime = RandRange(0.75, 1.5);

    glm::vec3 unitVec = RandUnitVec();

    particle.position = unitVec * 1.0f;
    particle.velocity = unitVec * RandRange(2.0, 5.0);
}

void ParticleEffect::RandomizeParticles()
{
    for (unsigned int i = 0; i < particles.size(); ++i)
    {
        RandomizeParticle(particles[i]);
    }
}


/*
void ParticleEffect::EmitParticles()
{
    if (particleEmitter == NULL)
    {
        RandomizeParticles();
    }
    else
    {
        for (unsigned int i = 0; i < particles.size(); ++i)
        {
            EmitParticle(particles[i]);
        }
    }
}
*/

void ParticleEffect::EmitParticle(Particle& particle)
{
    assert(particleEmitter != NULL);
    particleEmitter->EmitParticle(particle);
}

void ParticleEffect::Update(float dt)
{
    for (unsigned int i = 0; i < particles.size(); ++i)
    {
        Particle& particle = particles[i];

        particle.age += dt;
        if (particle.age > particle.lifeTime)
        {
            if (particleEmitter != NULL) EmitParticle(particle);
            else RandomizeParticle(particle);
        }

        float lifeRatio = glm::clamp(particle.age / particle.lifeTime, 0.0f, 1.0f);
        particle.velocity += (force * dt);
        particle.position += (particle.velocity * dt);
        particle.color = glm::mix(colorKeyFrames.first, colorKeyFrames.second, lifeRatio);
        particle.rotate = glm::mix<float>(rotateKeyFrames.first, rotateKeyFrames.second, lifeRatio);
        particle.size = glm::mix<float>(sizeKeyFrames.first, sizeKeyFrames.second, lifeRatio);
    }

    BuildVertexBuffer();
}

void ParticleEffect::BuildVertexBuffer()
{
    const glm::vec3 X(0.5, 0, 0);
    const glm::vec3 Y(0, 0.5, 0);
    const glm::vec3 Z(0, 0, 0.5);


    for (unsigned int i = 0; i < particles.size(); ++i) {
        Particle& particle = particles[i];
        glm::quat rotation = glm::normalize(glm::angleAxis(particle.rotate, rotateAxis));

        unsigned int vertexIndex = i * 8;
        // Bottom-left-front
        vertices[vertexIndex + 0] = particle.position + (rotation * (-X - Y + Z) *  particle.size);
        colours[vertexIndex + 0] = particle.color;
        // Bottom-right-front
        vertices[vertexIndex + 1] = particle.position + (rotation * (X - Y + Z) * particle.size);
        colours[vertexIndex + 1] = particle.color;
        // Top-right-front
        vertices[vertexIndex + 2] = particle.position + (rotation * (X + Y + Z) * particle.size);
        colours[vertexIndex + 2] = particle.color;
        // Top-left-front
        vertices[vertexIndex + 3] = particle.position + (rotation * (-X + Y + Z) * particle.size);
        colours[vertexIndex + 3] = particle.color;    
        // Bottom-left-back
        vertices[vertexIndex + 4] = particle.position + (rotation * (-X - Y - Z) * particle.size);
        colours[vertexIndex + 4] = particle.color;
        // Bottom-right-back
        vertices[vertexIndex + 5] = particle.position + (rotation * (X - Y - Z) * particle.size);
        colours[vertexIndex + 5] = particle.color;
        // Bottom-right-back
        vertices[vertexIndex + 6] = particle.position + (rotation * (X + Y - Z) * particle.size);
        colours[vertexIndex + 6] = particle.color;
        // Bottom-left-back
        vertices[vertexIndex + 7] = particle.position + (rotation * (-X + Y - Z) * particle.size);
        colours[vertexIndex + 7] = particle.color;



    }

    glBindVertexArray(VAO);
    glBindBuffer(GL_ARRAY_BUFFER, bufferObjects[VERTEX_BUFFER]);
    glBufferSubData(GL_ARRAY_BUFFER, 0, sizeof(glm::vec3) * vertices.size(), &vertices[0]);
    glBindBuffer(GL_ARRAY_BUFFER, bufferObjects[COLOUR_BUFFER]);
    glBufferSubData(GL_ARRAY_BUFFER, 0, sizeof(glm::vec3) * colours.size(), &colours[0]);
    glBindVertexArray(0);
}



