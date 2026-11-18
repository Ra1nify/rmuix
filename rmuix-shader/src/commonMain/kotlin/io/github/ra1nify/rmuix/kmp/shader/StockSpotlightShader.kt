// Copyright 2026, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.ra1nify.rmuix.kmp.shader

internal const val STOCK_SPOTLIGHT_SHADER = """
uniform vec4 u_lightPosAndHeight;
uniform vec4 u_lightColorAndAmbient;
uniform vec4 u_lightingParams;
uniform vec2 resolution;

float luminance(vec3 color) { return dot(color, vec3(0.299, 0.587, 0.114)); }
float maxChannel(vec3 color) { return max(max(color.r, color.g), color.b); }
float noise(vec2 position) {
    return fract(sin(dot(position, vec2(127.1, 311.7))) * 43758.5453);
}
float softLight(float source, float destination) {
    float low = destination - (1.0 - 2.0 * source) * destination * (1.0 - destination);
    float high = destination + (2.0 * source - 1.0) * (sqrt(destination) - destination);
    return mix(low, high, step(0.5, source));
}
float dither(vec2 coordinate, float strength) {
    float firstHash = fract(sin(dot(coordinate, vec2(12.9898, 78.233))) * 43758.5453);
    float secondHash = fract(sin(dot(coordinate + vec2(1.0), vec2(12.9898, 78.233))) * 43758.5453);
    return (firstHash + secondHash - 1.0) * strength / 255.0;
}
half4 main(float2 fragCoord) {
    float2 lightPosition = u_lightPosAndHeight.xy;
    float radius = u_lightPosAndHeight.z;
    float intensity = u_lightPosAndHeight.w;
    vec3 lightColor = u_lightColorAndAmbient.rgb;
    float ambientStrength = u_lightColorAndAmbient.w;
    float diffuseStrength = u_lightingParams.x;
    float lightHeight = u_lightingParams.y;
    float noiseStrength = u_lightingParams.z;
    float ditherStrength = u_lightingParams.w;
    const float lightIntensity = 1.5;
    vec2 delta = fragCoord - lightPosition;
    vec3 lightDirection = vec3(delta, lightHeight);
    float lightDistance = length(lightDirection);
    lightDirection = normalize(lightDirection);
    vec3 normal = vec3(0.0, 0.0, 1.0);
    float diffuse = max(dot(normal, lightDirection), 0.0);
    vec3 lighting = (ambientStrength + diffuseStrength * diffuse) * lightColor;
    if (lightDistance >= radius) return half4(0.0, 0.0, 0.0, 0.0);
    float attenuation = smoothstep(1.0, 0.0, lightDistance / radius) * step(lightDistance, radius);
    lighting *= attenuation * lightIntensity * intensity;
    float alpha = clamp(luminance(lighting), 0.0, 1.0);
    float postEnabled = max(step(0.001, noiseStrength), step(0.001, ditherStrength));
    vec3 postLighting = lighting;
    float postAlpha = alpha;
    float noiseEnabled = step(0.001, noiseStrength);
    vec2 noisePosition = fragCoord * 9.259e-4;
    float noiseValue = clamp((noise(noisePosition) - 0.5) * noiseStrength + 0.5, 0.0, 1.0);
    float mask = maxChannel(postLighting);
    float fadeAlpha = clamp(softLight(noiseValue, mask), 0.0, 1.0);
    float noiseDelta = clamp(abs(fadeAlpha - mask), 0.0, 1.0);
    postLighting += vec3(noiseDelta) * noiseEnabled;
    float ditherEnabled = step(0.001, ditherStrength);
    float ditherValue = dither(fragCoord, ditherStrength);
    postLighting += vec3(ditherValue) * ditherEnabled;
    postAlpha = mix(postAlpha, clamp(postAlpha + ditherValue, 0.0, 1.0), ditherEnabled);
    lighting = mix(lighting, postLighting, postEnabled);
    alpha = mix(alpha, postAlpha, postEnabled);
    return half4(lighting, alpha);
}
"""
