#include "Cube.h"

TimexCube::TimexCube() {


    vertices = {
        //Model them using the old Timex watch display style using parallelepipeds (3D “cubes”, scaled) halfunit tall, 0.25 unit deep
        //front
        glm::vec3(-0.5f, -0.125f, 0.125f),
        glm::vec3(0.5f, -0.125f, 0.125f),
        glm::vec3(0.5f, 0.125f, 0.125f),
        glm::vec3(-0.5f, -0.125f, 0.125f),
        glm::vec3(0.5f, 0.125f, 0.125f),
        glm::vec3(-0.5f, 0.125f, 0.125f),
        //back
        glm::vec3(-0.5f, -0.125f, -0.125f),
        glm::vec3(-0.5f, 0.125f, -0.125f),
        glm::vec3(0.5f, 0.125f, -0.125f),
        glm::vec3(-0.5f, -0.125f, -0.125f),
        glm::vec3(0.5f, 0.125f, -0.125f),
        glm::vec3(0.5f, -0.125f, -0.125f),
        //top
        glm::vec3(-0.5f, 0.125f, 0.125f),
        glm::vec3(0.5f, 0.125f, 0.125f),
        glm::vec3(0.5f, 0.125f, -0.125f),
        glm::vec3(-0.5f, 0.125f, 0.125f),
        glm::vec3(0.5f, 0.125f, -0.125f),
        glm::vec3(-0.5f, 0.125f, -0.125f),
        //bottom
        glm::vec3(-0.5f, -0.125f, -0.125f),
        glm::vec3(0.5f, -0.125f, -0.125f),
        glm::vec3(0.5f, -0.125f, 0.125f),
        glm::vec3(-0.5f, -0.125f, -0.125f),
        glm::vec3(0.5f, -0.125f, 0.125f),
        glm::vec3(-0.5f, -0.125f, 0.125f),
        //left
        glm::vec3(-0.5f, -0.125f, -0.125f),
        glm::vec3(-0.5f, -0.125f, 0.125f),
        glm::vec3(-0.5f, 0.125f, 0.125f),
        glm::vec3(-0.5f, -0.125f, -0.125f),
        glm::vec3(-0.5f, 0.125f, 0.125f),
        glm::vec3(-0.5f, 0.125f, -0.125f),
        //right
        glm::vec3(0.5f, -0.125f, 0.125f),
        glm::vec3(0.5f, -0.125f, -0.125f),
        glm::vec3(0.5f, 0.125f, -0.125f),
        glm::vec3(0.5f, -0.125f, 0.125f),
        glm::vec3(0.5f, 0.125f, -0.125f),
        glm::vec3(0.5f, 0.125f, 0.125f),
    };

    colours = {
        //front
        glm::vec3(1.0f, 1.0f, 1.0f),
        glm::vec3(1.0f, 1.0f, 1.0f),
        glm::vec3(1.0f, 1.0f, 1.0f),
        glm::vec3(1.0f, 1.0f, 1.0f),
        glm::vec3(1.0f, 1.0f, 1.0f),
        glm::vec3(1.0f, 1.0f, 1.0f),
        //back
        glm::vec3(1.0f, 1.0f, 1.0f),
        glm::vec3(1.0f, 1.0f, 1.0f),
        glm::vec3(1.0f, 1.0f, 1.0f),
        glm::vec3(1.0f, 1.0f, 1.0f),
        glm::vec3(1.0f, 1.0f, 1.0f),
        glm::vec3(1.0f, 1.0f, 1.0f),
        //top
        glm::vec3(1.0f, 1.0f, 1.0f),
        glm::vec3(1.0f, 1.0f, 1.0f),
        glm::vec3(1.0f, 1.0f, 1.0f),
        glm::vec3(1.0f, 1.0f, 1.0f),
        glm::vec3(1.0f, 1.0f, 1.0f),
        glm::vec3(1.0f, 1.0f, 1.0f),
        //bottom
        glm::vec3(1.0f, 1.0f, 1.0f),
        glm::vec3(1.0f, 1.0f, 1.0f),
        glm::vec3(1.0f, 1.0f, 1.0f),
        glm::vec3(1.0f, 1.0f, 1.0f),
        glm::vec3(1.0f, 1.0f, 1.0f),
        glm::vec3(1.0f, 1.0f, 1.0f),
        //left
        glm::vec3(1.0f, 1.0f, 1.0f),
        glm::vec3(1.0f, 1.0f, 1.0f),
        glm::vec3(1.0f, 1.0f, 1.0f),
        glm::vec3(1.0f, 1.0f, 1.0f),
        glm::vec3(1.0f, 1.0f, 1.0f),
        glm::vec3(1.0f, 1.0f, 1.0f),
        //right
        glm::vec3(1.0f, 1.0f, 1.0f),
        glm::vec3(1.0f, 1.0f, 1.0f),
        glm::vec3(1.0f, 1.0f, 1.0f),
        glm::vec3(1.0f, 1.0f, 1.0f),
        glm::vec3(1.0f, 1.0f, 1.0f),
        glm::vec3(1.0f, 1.0f, 1.0f)
    };

    normals = {
        //front
        glm::vec3(0.0f, 0.0f, 1.0f),
        glm::vec3(0.0f, 0.0f, 1.0f),
        glm::vec3(0.0f, 0.0f, 1.0f),
        glm::vec3(0.0f, 0.0f, 1.0f),
        glm::vec3(0.0f, 0.0f, 1.0f),
        glm::vec3(0.0f, 0.0f, 1.0f),
        //back
        glm::vec3(0.0f, 0.0f, -1.0f),
        glm::vec3(0.0f, 0.0f, -1.0f),
        glm::vec3(0.0f, 0.0f, -1.0f),
        glm::vec3(0.0f, 0.0f, -1.0f),
        glm::vec3(0.0f, 0.0f, -1.0f),
        glm::vec3(0.0f, 0.0f, -1.0f),
        //top
        glm::vec3(0.0f, 1.0f, 0.0f),
        glm::vec3(0.0f, 1.0f, 0.0f),
        glm::vec3(0.0f, 1.0f, 0.0f),
        glm::vec3(0.0f, 1.0f, 0.0f),
        glm::vec3(0.0f, 1.0f, 0.0f),
        glm::vec3(0.0f, 1.0f, 0.0f),
        //bottom
        glm::vec3(0.0f, -1.0f, 0.0f),
        glm::vec3(0.0f, -1.0f, 0.0f),
        glm::vec3(0.0f, -1.0f, 0.0f),
        glm::vec3(0.0f, -1.0f, 0.0f),
        glm::vec3(0.0f, -1.0f, 0.0f),
        glm::vec3(0.0f, -1.0f, 0.0f),
        //left
        glm::vec3(-1.0f, 0.0f, 0.0f),
        glm::vec3(-1.0f, 0.0f, 0.0f),
        glm::vec3(-1.0f, 0.0f, 0.0f),
        glm::vec3(-1.0f, 0.0f, 0.0f),
        glm::vec3(-1.0f, 0.0f, 0.0f),
        glm::vec3(-1.0f, 0.0f, 0.0f),
        //right
        glm::vec3(1.0f, 0.0f, 0.0f),
        glm::vec3(1.0f, 0.0f, 0.0f),
        glm::vec3(1.0f, 0.0f, 0.0f),
        glm::vec3(1.0f, 0.0f, 0.0f),
        glm::vec3(1.0f, 0.0f, 0.0f),
        glm::vec3(1.0f, 0.0f, 0.0f)
    };

    texCoords = {
        //front
        glm::vec2(0.0f, 0.0f),
        glm::vec2(1.0f, 0.0f),
        glm::vec2(1.0f, 1.0f),
        glm::vec2(0.0f, 0.0f),
        glm::vec2(1.0f, 1.0f),
        glm::vec2(0.0f, 1.0f),
        //back
        glm::vec2(1.0f, 0.0f),
        glm::vec2(1.0f, 1.0f),
        glm::vec2(0.0f, 1.0f),
        glm::vec2(1.0f, 0.0f),
        glm::vec2(0.0f, 1.0f),
        glm::vec2(0.0f, 0.0f),
        //top
        glm::vec2(0.0f, 1.0f),
        glm::vec2(1.0f, 1.0f),
        glm::vec2(1.0f, 0.0f),
        glm::vec2(0.0f, 1.0f),
        glm::vec2(1.0f, 0.0f),
        glm::vec2(0.0f, 0.0f),
        //bottom
        glm::vec2(0.0f, 0.0f),
        glm::vec2(1.0f, 0.0f),
        glm::vec2(1.0f, 1.0f),
        glm::vec2(0.0f, 0.0f),
        glm::vec2(1.0f, 1.0f),
        glm::vec2(0.0f, 1.0f),
        //left
        glm::vec2(0.0f, 0.0f),
        glm::vec2(1.0f, 0.0f),
        glm::vec2(1.0f, 1.0f),
        glm::vec2(0.0f, 0.0f),
        glm::vec2(1.0f, 1.0f),
        glm::vec2(0.0f, 1.0f),
        //right
        glm::vec2(0.0f, 0.0f),
        glm::vec2(1.0f, 0.0f),
        glm::vec2(1.0f, 1.0f),
        glm::vec2(0.0f, 0.0f),
        glm::vec2(1.0f, 1.0f),
        glm::vec2(0.0f, 1.0f)
    };

    setupBufferData();
}