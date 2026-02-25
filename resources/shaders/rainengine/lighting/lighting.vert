#version 120

varying vec2 vTexCoord;
varying vec4 vColor;
varying vec2 vWorldPos;

void main()
{
    // fixed-function input
    gl_Position = gl_ModelViewProjectionMatrix * gl_Vertex;

    vTexCoord = gl_MultiTexCoord0.xy;
    vColor = gl_Color;

    vWorldPos = gl_Vertex.xy;
}
