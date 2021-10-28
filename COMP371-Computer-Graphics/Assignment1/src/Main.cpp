/*
---------------------------------------------------------------------------------------------------------------------------------------------------------------
 The program uses a modified skeleton code provided by https://learnopengl.com, as well as modified shaders from the ones they provide
 The original skeleton code is available at: https://github.com/JoeyDeVries/LearnOpenGL
 The shadow shaders implemantion was according to https://learnopengl.com/Advanced-Lighting/Shadows/Point-Shadows
 Since we already implemented manually the Camera in assignment 1, we took the Camera class from learnopengl.com for this one, since it's very simple and neat.

 We use irrKlang audio engine from https://www.ambiera.com/irrklang
 Game audio: https://www.bensound.com/royalty-free-music/track/moose
 Rubik Cube turning sound: https://freesound.org/people/jrayhartley/sounds/514303/
---------------------------------------------------------------------------------------------------------------------------------------------------------------
*/

#pragma once
#include <glad/glad.h>
#include <GLFW/glfw3.h>
#include <glm/glm.hpp>
#include <glm/gtc/matrix_transform.hpp>
#include <glm/gtc/type_ptr.hpp>
#include <irrKlang/irrKlang.h>

#include "TextureLoader.h"
#include "Shader.h"
#include "GroupNode.h"
#include "DrawNode.h"
#include "Model.h"
#include "Grid.h"
#include "Renderer.h"
#include "Camera.h"
#include "RubikCubeColors.h"
#include "RubikCubeParticles.h"
#include "RubikCubeTextures.h"
#include "RubikCubeJigsaw.h"
#include "ParticleEffect.h"
#include "Random.h"
#include "DecorativeCubes.h"
#include "Pyramid.h"


#include <iostream>
#include <algorithm>
#include <stdlib.h>
#include <time.h>
#include "Timer.h"


//----------------------------------------
// Globals
//----------------------------------------

extern  unsigned int SCR_WIDTH = 1024;//extern, otherwise extern won't work in Renderer which needs to for perspective and window viewport resize after computing shadow
extern  unsigned int SCR_HEIGHT = 768;//if create Window class, can move in there and not have this

GLFWwindow* window;

Camera* mainCamera;
double lastX;
double lastY;

//setup sound engine using irrKlang
irrklang::ISoundEngine* SoundEngine = irrklang::createIrrKlangDevice();

//----------------------------------------
// Functions prototypes
//----------------------------------------

void ErrorCallback(int, const char* err_str);
void programInit();
void framebuffer_size_callback(GLFWwindow* window, int width, int height);
void mouse_callback(GLFWwindow* window, double xpos, double ypos);


//----------------------------------------
// MAIN
//----------------------------------------
int main() {
    //random generator for repositioning models
    int randomInt;
    srand(time(NULL));
    
    //initialize OpenGL libraries and create window
    programInit();

    
    // setup Camera
    mainCamera = new Camera(glm::vec3(45.0f, 75.0f, 45.0f), glm::vec3(0.0f, 1.0f, 0.0f), -135.0f, -30.0f);
    glfwGetCursorPos(window, &lastX, &lastY);

    ////frame time parameters
    float lastFrame = glfwGetTime();

    // build and compile shader program
    Shader genericShader("shaders/generic.vs", "shaders/generic.fs");
    Shader lightingMaterialShader("shaders/lightingMaterial.vs", "shaders/lightingMaterial.fs");
    Shader lightingTextureShader("shaders/lightingTexture.vs", "shaders/lightingTexture.fs");
    Shader shadowShader("shaders/shadow.vs", "shaders/shadow.fs", "shaders/shadow.gs");

    // setup Renderer
    Renderer* renderer = new Renderer(mainCamera, &genericShader, &lightingMaterialShader, &lightingTextureShader, &shadowShader);

    // set up the Scene Graph (sets up vertex data, buffers and configures vertex attributes)
    // --------------------------------------------------------------------------------------

    //generate all the Materials and textures
    Material* gridMaterial = new Material{
        glm::vec3(1.5, 1.5, 1.5), 
        glm::vec3(0.5, 0.5, 0.5), 
        glm::vec3(0.25, 0.25f, 0.25f), 
        32.0 
    };

    Material* textureMaterial = new Material{
        glm::vec3(0.25, 0.25, 0.25),
        glm::vec3(0.25, 0.25, 0.25),
        glm::vec3(0.3, 0.3, 0.3),
        1.0 
    };

    GLuint tileTexture = loadTexture("res/floor.jpg");

    //root of scene
    GroupNode* root = new GroupNode;
    
    //rubik cubes
    RubikCube* rubikCube1 = new RubikCubeColors;
    rubikCube1->scale(glm::vec3(5.0f, 5.0f, 5.0f));
    rubikCube1->translate(glm::vec3(14.0f, 44.0f, 14.0f));
    root->addChild(rubikCube1);//only add one rubik cube, rest are added during swap

    RubikCube* rubikCube2 = new RubikCubeTextures;
    rubikCube2->scale(glm::vec3(5.0f, 5.0f, 5.0f));
    rubikCube2->translate(glm::vec3(14.0f, 44.0f, 14.0f));
    
    RubikCube* rubikCube3 = new RubikCubeJigsaw;
    rubikCube3->scale(glm::vec3(5.0f, 5.0f, 5.0f));
    rubikCube3->translate(glm::vec3(14.0f, 44.0f, 14.0f));

    RubikCube* rubikCube4 = new RubikCubeParticles;
    rubikCube4->scale(glm::vec3(5.0f, 5.0f, 5.0f));
    rubikCube4->translate(glm::vec3(14.0f, 44.f, 14.0f));


    //skybox, made from 6 grids and decorative cubes
    //decorative cubes drawable
    DecorativeCubes* decorativeCubes = new DecorativeCubes(2000);
    //grid1(ground)
    GroupNode* grid1Node = new GroupNode;
    root->addChild(grid1Node);

    DrawNode* grid1 = new DrawNode(new Grid);
    grid1->setMaterial(gridMaterial);
    grid1->setTexture(tileTexture);
    root->addChild(grid1);
    
    DrawNode* decorativeCubesNode1 = new DrawNode(decorativeCubes);
    decorativeCubesNode1->translate(glm::vec3(0.0f, 0.1f, 0.0f));
    grid1Node->addChild(decorativeCubesNode1);
    //grid 2(ceiling)
    GroupNode* grid2Node = new GroupNode;
    grid2Node->rotate(glm::vec3(180.0f, 0.0f, 0.0f));
    grid2Node->translate(glm::vec3(0.0f, 100.0f, 0.0f));
    root->addChild(grid2Node);

    DrawNode* grid2 = new DrawNode(new Grid);
    grid2->setMaterial(gridMaterial);
    grid2->setTexture(tileTexture);
    grid2Node->addChild(grid2);
    
    DrawNode* decorativeCubesNode2 = new DrawNode(decorativeCubes);
    grid2Node->addChild(decorativeCubesNode2);
    //grid3(left wall)
    GroupNode* grid3Node = new GroupNode;
    grid3Node->rotate(glm::vec3(0.0f, 0.0f, -90.0f));
    grid3Node->translate(glm::vec3(-50.0f, 50.0f, 0.0f));
    root->addChild(grid3Node);

    DrawNode* grid3 = new DrawNode(new Grid);
    grid3->setMaterial(gridMaterial);
    grid3->setTexture(tileTexture);
    grid3Node->addChild(grid3);

    DrawNode* decorativeCubesNode3 = new DrawNode(decorativeCubes);
    grid3Node->addChild(decorativeCubesNode3);
    //grid4(right wall)
    GroupNode* grid4Node = new GroupNode;
    grid4Node->rotate(glm::vec3(0.0f, 0.0f, 90.0f));
    grid4Node->translate(glm::vec3(50.0f, 50.0f, 0.0f));
    root->addChild(grid4Node);
    
    DrawNode* grid4 = new DrawNode(new Grid);
    grid4->setMaterial(gridMaterial);
    grid4->setTexture(tileTexture);
    grid4Node->addChild(grid4);

    DrawNode* decorativeCubesNode4 = new DrawNode(decorativeCubes);
    grid4Node->addChild(decorativeCubesNode4);

    //grid5(front wall)
    GroupNode* grid5Node = new GroupNode;
    grid5Node->rotate(glm::vec3(90.0f, 0.0f, 0.0f));
    grid5Node->translate(glm::vec3(0.0f, 50.0f, -50.0f));
    root->addChild(grid5Node);

    DrawNode* grid5 = new DrawNode(new Grid);
    grid5->setMaterial(gridMaterial);
    grid5->setTexture(tileTexture);
    grid5Node->addChild(grid5);

    DrawNode* decorativeCubesNode5 = new DrawNode(decorativeCubes);
    grid5Node->addChild(decorativeCubesNode5);
    //grid6(back wall)
    GroupNode* grid6Node = new GroupNode;
    grid6Node->rotate(glm::vec3(-90.0f, 0.0f, 0.0f));
    grid6Node->translate(glm::vec3(0.0f, 50.0f, 50.0f));
    root->addChild(grid6Node);

    DrawNode* grid6 = new DrawNode(new Grid);
    grid6->setMaterial(gridMaterial);
    grid6->setTexture(tileTexture);
    grid6Node->addChild(grid6);

    DrawNode* decorativeCubesNode6 = new DrawNode(decorativeCubes);
    grid6Node->addChild(decorativeCubesNode6);

    //timer
    Timer* timer = new Timer();
    timer->scale(glm::vec3(3.0f, 3.0f, 3.0f));
    timer->rotate(glm::vec3(0.0f, 45.0f, 0.0f));
    timer->translate(glm::vec3(-25.0f, 53.0f, -25.0f));
    root->addChild(timer);

    
    //light source(s)
    GroupNode* lightNode = new GroupNode();
    lightNode->translate(glm::vec3(45.0f, 85.0f, 45.0f));
    root->addChild(lightNode);

    LightNode* light = new LightNode(LightType::PointLight);
    light->setProperties(LightProperties{
        glm::vec3(0.4f, 0.4f, 0.4f),
        glm::vec3(3.0f, 3.0f, 3.0f),
        glm::vec3(1.0f, 1.0f, 1.0f),
        });
    lightNode->addChild(light);

    Drawable* lightcube = new Cube;
    lightcube->setColours(glm::vec3(1.0f));
    DrawNode* light1cube = new DrawNode(lightcube);
    light1cube->translate(glm::vec3(0.0f, 0.5f, 0.0f));
    lightNode->addChild(light1cube);

    //set light source that cast shadows. Can have multiple lights, but only one casts shadows
    renderer->setShadowCasterLight(light);
    //root of the Scene
    renderer->setRootSceneNode(root);
    //default selected Rubik Cube
    RubikCube* selectedRubikCube = rubikCube1;


    //start music
    //SoundEngine->play2D("audio/gameSong.mp3", true);
    irrklang::ISound* mainSong = SoundEngine->play2D("audio/gameSong.mp3", true, false, true);
    //------------------------------------------------------------------------------

    // render loop
    // -----------
    while (!glfwWindowShouldClose(window))
    {
        // update frame time parameters
        float dt = glfwGetTime() - lastFrame;
        lastFrame += dt;


        // keyboard input handling
        // --------------
        //camera movement
        if (glfwGetKey(window, GLFW_KEY_DOWN) == GLFW_PRESS && glfwGetKey(window, GLFW_KEY_RIGHT_SHIFT) == GLFW_RELEASE) {
            mainCamera->ProcessKeyboard(BACKWARD, dt);
        }
        if (glfwGetKey(window, GLFW_KEY_UP) == GLFW_PRESS && glfwGetKey(window, GLFW_KEY_RIGHT_SHIFT) == GLFW_RELEASE) {
            mainCamera->ProcessKeyboard(FORWARD, dt);
        }
        if (glfwGetKey(window, GLFW_KEY_LEFT) == GLFW_PRESS && glfwGetKey(window, GLFW_KEY_RIGHT_SHIFT) == GLFW_RELEASE) {
            mainCamera->ProcessKeyboard(LEFT, dt);
        }
        if (glfwGetKey(window, GLFW_KEY_RIGHT) == GLFW_PRESS && glfwGetKey(window, GLFW_KEY_RIGHT_SHIFT) == GLFW_RELEASE) {
            mainCamera->ProcessKeyboard(RIGHT, dt);
        }

        // exit program
        if (glfwGetKey(window, GLFW_KEY_ESCAPE) == GLFW_PRESS)
            glfwSetWindowShouldClose(window, true);


        
        //select appropriate rubik cube
        //select Colours Rubik CUbe
        {
            static bool keyPress = false;
            if (glfwGetKey(window, GLFW_KEY_7) == GLFW_PRESS) {
                if (!keyPress) {
                    root->removeChild(selectedRubikCube);
                    root->addChild(rubikCube1);
                    selectedRubikCube = rubikCube1;
                    keyPress = true;
                }
            }
            if (glfwGetKey(window, GLFW_KEY_7) == GLFW_RELEASE) {
                keyPress = false;
            }
        }
        //select Textures Rubik Cube
        {
            static bool keyPress = false;
            if (glfwGetKey(window, GLFW_KEY_8) == GLFW_PRESS) {
                if (!keyPress) {
                    root->removeChild(selectedRubikCube);
                    root->addChild(rubikCube2);
                    selectedRubikCube = rubikCube2;
                    keyPress = true;
                }
            }
            if (glfwGetKey(window, GLFW_KEY_8) == GLFW_RELEASE) {
                keyPress = false;
            }
        }
        //select Jigsaw Rubik Cube
        {
            static bool keyPress = false;
            if (glfwGetKey(window, GLFW_KEY_9) == GLFW_PRESS) {
                if (!keyPress) {
                    root->removeChild(selectedRubikCube);
                    root->addChild(rubikCube3);
                    selectedRubikCube = rubikCube3;
                    keyPress = true;
                }
            }
            if (glfwGetKey(window, GLFW_KEY_9) == GLFW_RELEASE) {
                keyPress = false;
            }
        }
        //select Particles Rubik Cube
        {
            static bool keyPress = false;
            if (glfwGetKey(window, GLFW_KEY_0) == GLFW_PRESS) {
                if (!keyPress) {
                    root->removeChild(selectedRubikCube);
                    root->addChild(rubikCube4);
                    selectedRubikCube = rubikCube4;
                    keyPress = true;
                }
            }
            if (glfwGetKey(window, GLFW_KEY_0) == GLFW_RELEASE) {
                keyPress = false;
            }
        }

        //rotate whole cube on some axis
        //x-axis
        if (glfwGetKey(window, GLFW_KEY_R) == GLFW_PRESS) {
            selectedRubikCube->rotateXCCW();
        }
        if (glfwGetKey(window, GLFW_KEY_F) == GLFW_PRESS) {
            selectedRubikCube->rotateXCW();
        }
        //y-axis
        if (glfwGetKey(window, GLFW_KEY_Q) == GLFW_PRESS) {
            selectedRubikCube->rotateYCCW();
        }
        if (glfwGetKey(window, GLFW_KEY_E) == GLFW_PRESS) {
            selectedRubikCube->rotateYCW();
        }
        //z-axis
        if (glfwGetKey(window, GLFW_KEY_Z) == GLFW_PRESS) {
            selectedRubikCube->rotateZCCW();
        }
        if (glfwGetKey(window, GLFW_KEY_X) == GLFW_PRESS) {
            selectedRubikCube->rotateZCW();
        }

        // selected rubik cube face
        if (glfwGetKey(window, GLFW_KEY_1) == GLFW_PRESS) {
            selectedRubikCube->selectFace1();
        }
        if (glfwGetKey(window, GLFW_KEY_2) == GLFW_PRESS) {
            selectedRubikCube->selectFace2();
        }
        if (glfwGetKey(window, GLFW_KEY_3) == GLFW_PRESS) {
            selectedRubikCube->selectFace3();
        }
        if (glfwGetKey(window, GLFW_KEY_4) == GLFW_PRESS) {
            selectedRubikCube->selectFace4();
        }
        if (glfwGetKey(window, GLFW_KEY_5) == GLFW_PRESS) {
            selectedRubikCube->selectFace5();
        }
        if (glfwGetKey(window, GLFW_KEY_6) == GLFW_PRESS) {
            selectedRubikCube->selectFace6();
        }

        // rotate selected face of the selected rubik cube
        // clockwise
        if (glfwGetKey(window, GLFW_KEY_C) == GLFW_PRESS) {
            selectedRubikCube->rotateFaceCW();
        }
        // anti-clockwise
        if (glfwGetKey(window, GLFW_KEY_V) == GLFW_PRESS) {
            selectedRubikCube->rotateFaceCCW();
        }

        //increase/decrease number of decorative cubes
        {
            static bool keyPress = false;
            if (glfwGetKey(window, GLFW_KEY_MINUS) == GLFW_PRESS) {
                if (!keyPress) {
                    decorativeCubes->removeCubes(1000);
                    keyPress = true;
                }
            }
            if (glfwGetKey(window, GLFW_KEY_MINUS) == GLFW_RELEASE) {
                keyPress = false;
            }
        }
        {
            static bool keyPress = false;
            if (glfwGetKey(window, GLFW_KEY_EQUAL) == GLFW_PRESS) {
                if (!keyPress) {
                    decorativeCubes->addCubes(1000);
                    keyPress = true;
                }
            }
            if (glfwGetKey(window, GLFW_KEY_EQUAL) == GLFW_RELEASE) {
                keyPress = false;
            }
        }


        //starting timer
        if (glfwGetKey(window, GLFW_KEY_B) == GLFW_PRESS) {
            if (!timer->timeStarted) {
                timer->timeStarted = true;
                timer->start();
            }
        }
        //stopping timer
        if (glfwGetKey(window, GLFW_KEY_N) == GLFW_PRESS) {
            timer->pause();
        }
        //reseting timer
        if (glfwGetKey(window, GLFW_KEY_M) == GLFW_PRESS) {
            timer->reset();
        }
 

        
        // move light source
        //strafe left continously (-x direction)
        if (glfwGetKey(window, GLFW_KEY_A) == GLFW_PRESS && glfwGetKey(window, GLFW_KEY_LEFT_CONTROL) == GLFW_RELEASE) {
            lightNode->strafeLeft(25 * dt);
        }
        //strafe right continously (+x direction)
        if (glfwGetKey(window, GLFW_KEY_D) == GLFW_PRESS && glfwGetKey(window, GLFW_KEY_LEFT_CONTROL) == GLFW_RELEASE) {
            lightNode->strafeRight(25 * dt);
        }     
        //move forward continously (-z direction)
        if (glfwGetKey(window, GLFW_KEY_W) == GLFW_PRESS && glfwGetKey(window, GLFW_KEY_LEFT_SHIFT) == GLFW_RELEASE && glfwGetKey(window, GLFW_KEY_LEFT_CONTROL) == GLFW_RELEASE) {
            lightNode->moveForward(25 * dt);
        }     
        //move backwards continously (+z direction)
        if (glfwGetKey(window, GLFW_KEY_S) == GLFW_PRESS && glfwGetKey(window, GLFW_KEY_LEFT_SHIFT) == GLFW_RELEASE && glfwGetKey(window, GLFW_KEY_LEFT_CONTROL) == GLFW_RELEASE) {
            lightNode->moveBackwards(25 * dt);
        }       
        //move up continously (+y direction)
        if (glfwGetKey(window, GLFW_KEY_W) == GLFW_PRESS && glfwGetKey(window, GLFW_KEY_LEFT_SHIFT) == GLFW_PRESS && glfwGetKey(window, GLFW_KEY_LEFT_CONTROL) == GLFW_RELEASE) {
            lightNode->moveUp(25 * dt);
        }
        //move down continously (-y direction)
        if (glfwGetKey(window, GLFW_KEY_S) == GLFW_PRESS && glfwGetKey(window, GLFW_KEY_LEFT_SHIFT) == GLFW_PRESS && glfwGetKey(window, GLFW_KEY_LEFT_CONTROL) == GLFW_RELEASE) {
            lightNode->moveDown(25 * dt);
        }
        

        // change rendering modes
        if (glfwGetKey(window, GLFW_KEY_P) == GLFW_PRESS) {
            glPolygonMode(GL_FRONT_AND_BACK, GL_POINT);
        }
        if (glfwGetKey(window, GLFW_KEY_L) == GLFW_PRESS) {
            glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
        }
        if (glfwGetKey(window, GLFW_KEY_T) == GLFW_PRESS) {
            glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
        }
        //toggle shadows
        {
            static bool keyPress = false;
            if (glfwGetKey(window, GLFW_KEY_O) == GLFW_PRESS) {
                if (!keyPress) { 
                    renderer->toggleShadowMode(); 
                    keyPress = true;
                }
            }
            if (glfwGetKey(window, GLFW_KEY_O) == GLFW_RELEASE) {
                keyPress = false;
            }
        }
        //toggle textures
        {
            static bool keyPress = false;
            if (glfwGetKey(window, GLFW_KEY_I) == GLFW_PRESS) {
                if (!keyPress) {
                    renderer->toggleTexture();
                    keyPress = true;
                }
            }
            if (glfwGetKey(window, GLFW_KEY_I) == GLFW_RELEASE) {
                keyPress = false;
            }
        }

        //toggle music
        {
            static bool keyPress = false;
            static bool soundOn = true;
            if (glfwGetKey(window, GLFW_KEY_U) == GLFW_PRESS) {
                if (!keyPress) {
                    if (soundOn) {
                        mainSong->setIsPaused(true);
                        keyPress = true;
                        soundOn = false;
                    }
                    else {
                        mainSong->setIsPaused(false);
                        keyPress = true;
                        soundOn = true;
                    }
                }
            }
            if (glfwGetKey(window, GLFW_KEY_U) == GLFW_RELEASE) {
                keyPress = false;
            }
        }


        // update scene
        //-------------
        //updates the timer if timer has started
        timer->timeUpdate(timer->elapsedTime(), timer->timeStarted);
        //update decorative cube positions
        decorativeCubes->Update(dt);
        //update rest of scene
        renderer->updateScene(dt);
        // render
        // ------
        renderer->render();

        //displays FPS for debugging
        static int frames = 1;
        static float total = 0;
        total += 1/dt;
        float avg = total / frames;
        frames++;
        std::cout << '\r' << "dt: " << dt << "\tFPS: " << 1 / dt << "\tavgFPS: " << avg;;//for debugging

        // glfw: swap buffers and poll IO events (keys pressed/released, mouse moved etc.)
        // -------------------------------------------------------------------------------
        glfwSwapBuffers(window);
        glfwPollEvents();
    }

    // optional: de-allocate all resources once they've outlived their purpose:
    // ------------------------------------------------------------------------


    // glfw: terminate, clearing all previously allocated GLFW resources.
    // ------------------------------------------------------------------
    glfwTerminate();
    return 0;
}




//----------------------------------------
// Function definitions
//----------------------------------------

// for debugging if getting error
void ErrorCallback(int, const char* err_str)
{
    std::cout << "GLFW Error: " << err_str << std::endl;
}

// initialize glfw and create window, then use glad to load function pointers
// TODO abstract into window class
// --------------------------------------------------------------------------
void programInit() {
    // glfw: initialize and configure
    glfwSetErrorCallback(ErrorCallback);
    glfwInit();
    glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
    glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);
    glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);

    // glfw window creation
    window = glfwCreateWindow(SCR_WIDTH, SCR_HEIGHT, "Final Project", NULL, NULL);
    if (window == NULL) {
        std::cout << "Failed to create GLFW window" << std::endl;
        glfwTerminate();
        exit(-1);
    }

    // set required callback functions
    glfwMakeContextCurrent(window);
    glfwSetFramebufferSizeCallback(window, framebuffer_size_callback);
    glfwSetCursorPosCallback(window, mouse_callback);

    // tell GLFW to capture our mouse
    glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_NORMAL);

    // glad: load all OpenGL function pointers
    if (!gladLoadGLLoader((GLADloadproc)glfwGetProcAddress)) {
        std::cout << "Failed to initialize GLAD" << std::endl;
        exit(-1);
    }

    //enable z-buffering
    glEnable(GL_DEPTH_TEST);
    glDepthFunc(GL_LESS);
}


// glfw: whenever the window size changed (by OS or user resize) this callback function executes
// ---------------------------------------------------------------------------------------------
void framebuffer_size_callback(GLFWwindow* window, int width, int height) {
    // make sure the viewport matches the new window dimensions
    glViewport(0, 0, width, height);
    SCR_HEIGHT = height;
    SCR_WIDTH = width;
}


void mouse_callback(GLFWwindow* window, double xpos, double ypos)
{
    float xoffset = xpos - lastX;
    float yoffset = lastY - ypos; // reversed since y-coordinates go from bottom to top

    lastX = xpos;
    lastY = ypos;

    // update camera angular angles only if appropriate button is pressed
    if (glfwGetMouseButton(window, GLFW_MOUSE_BUTTON_RIGHT) == GLFW_PRESS) {
        mainCamera->ProcessMouseMovement(xoffset, 0.0);
    }
    if (glfwGetMouseButton(window, GLFW_MOUSE_BUTTON_LEFT) == GLFW_PRESS) {
        mainCamera->ProcessMouseMovement(0.0, yoffset);
    }
    if (glfwGetKey(window, GLFW_KEY_RIGHT_CONTROL) == GLFW_PRESS) {
        mainCamera->ProcessMouseScroll(yoffset);
    }

}
