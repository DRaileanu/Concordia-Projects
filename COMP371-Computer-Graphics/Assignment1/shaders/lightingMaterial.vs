#version 330 core
layout (location = 0) in vec3 aPos;
layout (location = 1) in vec3 aNormal;
layout (location = 2) in vec3 aColor;

uniform mat4 model;
uniform mat3 normalMatrix;
uniform mat4 VP;

out vec3 FragPos;
out vec3 Normal;
out vec3 Color;


void main()
{
	FragPos = vec3(model * vec4(aPos,1.0f));
	gl_Position = VP * vec4(FragPos, 1.0);
	Normal = normalMatrix * aNormal;  
	Color = aColor;
}