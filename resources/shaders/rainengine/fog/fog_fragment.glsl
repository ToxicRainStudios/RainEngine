#version 330 core

in vec3 fragPosition; // Received from vertex shader
in vec3 fragColor;    // Received from vertex shader

uniform vec3 fogColor;
uniform float fogDensity;
uniform float fogStart;
uniform float fogEnd;

out vec4 color;

void main() {
    float distance = length(fragPosition); // Compute distance from the camera
    float fogFactor = clamp((fogEnd - distance) / (fogEnd - fogStart), 0.0, 1.0);

    vec3 finalColor = mix(fogColor, fragColor, fogFactor);
    color = vec4(finalColor, 1.0);
}

