#version 330 core

layout(location = 0) in vec3 inPosition; // Position attribute
layout(location = 1) in vec3 inColor;    // Color attribute

out vec3 fragPosition; // Pass position to fragment shader
out vec3 fragColor;    // Pass color to fragment shader

uniform mat4 modelViewProjectionMatrix;

void main() {
    fragPosition = (modelViewProjectionMatrix * vec4(inPosition, 1.0)).xyz;
    fragColor = inColor;
    gl_Position = modelViewProjectionMatrix * vec4(inPosition, 1.0);
}

