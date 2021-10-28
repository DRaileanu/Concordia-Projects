#version 330 core
out vec4 FragColor;


uniform vec3 viewPos;

uniform float transparency;
in vec3 Color;


void main()
{
    FragColor = vec4(Color, 1.0f - transparency);
}

