
uniform mat4 uMVPMatrix;

attribute vec2 aPos;
attribute vec2 aTexCoord;
attribute float aAlpha;

varying vec2 vTexCoord;
varying float vAlpha;

void main() {
    vTexCoord = aTexCoord;
    vAlpha = clamp(aAlpha, 0.0, 1.0);
    gl_Position = uMVPMatrix * vec4(aPos, 0.0, 1.0);
}
