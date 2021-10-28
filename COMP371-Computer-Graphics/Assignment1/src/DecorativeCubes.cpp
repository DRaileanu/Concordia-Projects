#include "DecorativeCubes.h"
#include <algorithm>
#include "Random.h"

DecorativeCubes::DecorativeCubes(unsigned int numCubes) {
    Resize(numCubes);

    //map indices to vertex positions
    //it's possible to have indices map to invalid vertices if there are less particles than MAX_PARTICLES
    //this is ok, since we override the draw method to only draw using indices of actual number of particles
    indices.resize(36 * MAX_CUBES);
    for (int i = 0; i < MAX_CUBES; ++i) {
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

void DecorativeCubes::Resize(unsigned int numCubes)
{
    numCubes = std::min(numCubes, MAX_CUBES);
    unsigned int cubesSize = cubes.size();
    if (cubes.size() < numCubes) {
        for (unsigned int i = 0; i < numCubes - cubesSize; ++i) {
            cubes.push_back(DecorativeCube());
        }
    }
    else {
        cubes.resize(numCubes);
    }
    vertices.resize(8 * numCubes);
    colours.resize(8 * numCubes);
}

void DecorativeCubes::addCubes(unsigned int numCubes) {
    unsigned int newNumCubes = std::min(cubes.size() + numCubes, MAX_CUBES);
    Resize(newNumCubes);
}

void DecorativeCubes::removeCubes(unsigned int numCubes) {
    if (numCubes >= cubes.size()) {
        Resize(0);
    }
    else {
        Resize(cubes.size() - numCubes);
    }
}

void DecorativeCubes::setupBufferData() {
    glBindVertexArray(VAO);
    //positions
    glGenBuffers(1, &bufferObjects[VERTEX_BUFFER]);
    glBindBuffer(GL_ARRAY_BUFFER, bufferObjects[VERTEX_BUFFER]);
    glBufferData(GL_ARRAY_BUFFER, sizeof(glm::vec3) * 8 * MAX_CUBES, NULL, GL_DYNAMIC_DRAW);
    glVertexAttribPointer(VERTEX_BUFFER, 3, GL_FLOAT, GL_FALSE, 0, 0);
    glEnableVertexAttribArray(VERTEX_BUFFER);

    //colours
    glGenBuffers(1, &bufferObjects[COLOUR_BUFFER]);
    glBindBuffer(GL_ARRAY_BUFFER, bufferObjects[COLOUR_BUFFER]);
    glBufferData(GL_ARRAY_BUFFER, sizeof(glm::vec3) * 8 * MAX_CUBES, NULL, GL_DYNAMIC_DRAW);
    glVertexAttribPointer(COLOUR_BUFFER, 3, GL_FLOAT, GL_FALSE, 0, 0);
    glEnableVertexAttribArray(COLOUR_BUFFER);

    glGenBuffers(1, &bufferObjects[INDEX_BUFFER]);
    glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, bufferObjects[INDEX_BUFFER]);
    glBufferData(GL_ELEMENT_ARRAY_BUFFER, sizeof(unsigned int) * 36 * MAX_CUBES, indices.data(), GL_STATIC_DRAW);

    glBindVertexArray(0);
}


void DecorativeCubes::draw() {
    glBindVertexArray(VAO);
    glDrawElements(type, cubes.size() * 36, GL_UNSIGNED_INT, 0);
}

void DecorativeCubes::Update(float dt) {
    for (unsigned int i = 0; i < cubes.size(); ++i) {
        DecorativeCube& cube = cubes[i];
        //update position
        cube.keyframeProgress+=dt;
        float progressRatio = glm::clamp(cube.keyframeProgress / cube.keyframeDuration, 0.0f, 1.0f);
        cube.position = glm::mix(cube.keyframePos1, cube.keyframePos2, progressRatio);
        cube.color = glm::normalize(glm::mix(cube.color, RandUnitVec(), 0.25f));
        //if reached end of keyframe, update direction and keyframe positions
        if (progressRatio == 1) {
            //if at edge of grid, rotate by 180deg
            if (abs(cube.position.x) == 50 || abs(cube.position.z) == 50) {
                cube.direction *= -1.0f;
            }
            //otherwise, rotate either -90 or +90 degrees
            else {
                unsigned int randInt = rand() % 100;
                if (randInt < 40) {
                    if (randInt < 20) {
                        glm::quat rotation(glm::vec3(0.0f, glm::radians(90.0f), 0.0f));
                        cube.direction = rotation * cube.direction;
                    }
                    else {
                        glm::quat rotation(glm::vec3(0.0f, glm::radians(-90.0f), 0.0f));
                        cube.direction = rotation * cube.direction;
                    }
                }
            }
            //update new keyframe positions
            cube.keyframePos1 = cube.position;
            cube.keyframePos2 = cube.position + cube.direction;
            cube.keyframeProgress = 0;

        }
    }

    BuildVertexBuffer();
}


void DecorativeCubes::BuildVertexBuffer() {
    const glm::vec3 X(0.1, 0, 0);
    const glm::vec3 Y(0, 0.1, 0);
    const glm::vec3 Z(0, 0, 0.1);

    for (unsigned int i = 0; i < cubes.size(); ++i) {
        DecorativeCube& cube = cubes[i];
        unsigned int vertexIndex = i * 8;

        // Bottom-left-front
        vertices[vertexIndex + 0] = cube.position + (-X - Y + Z);
        colours[vertexIndex + 0] = cube.color;
        // Bottom-right-front
        vertices[vertexIndex + 1] = cube.position + (X - Y + Z);
        colours[vertexIndex + 1] = cube.color;
        // Top-right-front
        vertices[vertexIndex + 2] = cube.position + (X + Y + Z);
        colours[vertexIndex + 2] = cube.color;
        // Top-left-front
        vertices[vertexIndex + 3] = cube.position + (-X + Y + Z);
        colours[vertexIndex + 3] = cube.color;
        // Bottom-left-back
        vertices[vertexIndex + 4] = cube.position + (-X - Y - Z);
        colours[vertexIndex + 4] = cube.color;
        // Bottom-right-back
        vertices[vertexIndex + 5] = cube.position + (X - Y - Z);
        colours[vertexIndex + 5] = cube.color;
        // Bottom-right-back
        vertices[vertexIndex + 6] = cube.position + (X + Y - Z);
        colours[vertexIndex + 6] = cube.color;
        // Bottom-left-back
        vertices[vertexIndex + 7] = cube.position + (-X + Y - Z);
        colours[vertexIndex + 7] = cube.color;
    }

    glBindVertexArray(VAO);
    glBindBuffer(GL_ARRAY_BUFFER, bufferObjects[VERTEX_BUFFER]);
    glBufferSubData(GL_ARRAY_BUFFER, 0, sizeof(glm::vec3) * vertices.size(), vertices.data());
    glBindBuffer(GL_ARRAY_BUFFER, bufferObjects[COLOUR_BUFFER]);
    glBufferSubData(GL_ARRAY_BUFFER, 0, sizeof(glm::vec3) * colours.size(), colours.data());
    glBindVertexArray(0);
}

