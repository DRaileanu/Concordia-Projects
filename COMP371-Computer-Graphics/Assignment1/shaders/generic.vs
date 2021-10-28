#version 330 core
layout (location = 0) in vec3 aPos;
layout (location = 2) in vec3 aColor;

uniform mat4 model;
uniform mat4 VP;

out vec3 Color;


void main()
{
	gl_Position = VP * model * vec4(aPos, 1.0f);  
	Color = aColor;
}