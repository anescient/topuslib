
uniform mat4 uVPMatrix;
uniform mat4 uMMatrix;

attribute vec3 aPos;
attribute vec2 aTexCoord;

varying vec2 vTexCoord;

void main() {
    vTexCoord = aTexCoord;
    gl_Position = uVPMatrix * uMMatrix * vec4(aPos, 1.0);
}
