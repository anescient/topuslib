
uniform mat4 uMMatrix;
uniform mat4 uVPMatrix;
uniform vec3 uColor;
uniform vec3 uLight;

attribute vec3 aPos;
attribute vec3 aNormal;

varying vec3 vColor;

void main() {
    vec3 normal = (uMMatrix * vec4(aNormal, 1.0)).xyz;
    vColor = uColor * (0.2 + 0.8 * pow(clamp(dot(normal, uLight), 0.0, 1.0), 3.0));
    gl_Position = uVPMatrix * uMMatrix * vec4(aPos, 1.0);
}
