#version 120

varying vec2 vTexCoord;
varying vec4 vColor;
varying vec2 vWorldPos;

uniform sampler2D uTexture;

uniform int uLightCount;
uniform vec3 uLightPos[32];   // x, y, radius (strength)
uniform vec3 uLightColor[32]; // r,g,b per light

uniform vec3 uAmbient; // small ambient light

void main()
{
    vec4 base = texture2D(uTexture, vTexCoord) * vColor;

    if (base.a <= 0.01)
    discard;

    vec3 lightAccum = uAmbient;

    for (int i = 0; i < uLightCount; i++)
    {
        vec2 lp = uLightPos[i].xy;
        float radius = uLightPos[i].z;

        float dist = distance(lp, vWorldPos);

        if (dist < radius)
        {
            float att = 1.0 - (dist / radius);
            att = att * att; // smooth falloff

            lightAccum += uLightColor[i] * att;
        }
    }

    gl_FragColor = vec4(base.rgb * lightAccum, base.a);
}
