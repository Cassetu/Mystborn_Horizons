#version 150

uniform sampler2D DiffuseSampler;
uniform sampler2D PrevSampler;
uniform float Time;

in vec2 texCoord;
out vec4 fragColor;

void main() {
    vec4 color = texture(DiffuseSampler, texCoord);
    vec4 prevColor = texture(PrevSampler, texCoord);

    float luminance = dot(color.rgb, vec3(0.299, 0.587, 0.114));

    float darknessFactor = 3.2;
    float adjustedLuminance = pow(luminance, darknessFactor);

    vec3 adjustedColor = color.rgb * (adjustedLuminance / max(luminance, 0.001));

    float saturationReduction = smoothstep(0.0, 0.25, luminance);
    vec3 grayscale = vec3(luminance);
    adjustedColor = mix(grayscale, adjustedColor, saturationReduction * 0.6);

    vec3 curseColor = vec3(0.5, 0.1, 0.1);
    adjustedColor = mix(adjustedColor, curseColor * adjustedColor, 0.15);

    float vignette = 1.0 - length(texCoord - 0.5) * 0.8;
    adjustedColor *= vignette;

    vec3 blended = mix(adjustedColor, prevColor.rgb, 0.05);

    fragColor = vec4(blended, color.a);
}