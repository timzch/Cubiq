#version 430 core

layout (location = 0) in vec3 vposition;
layout (location = 1) in vec3 vInColor;
layout (location = 0) uniform mat4 pMatrix;
layout (location = 1) uniform mat4 mvMatrix;

out vec4 vColor;

void main(void) {
	gl_Position = pMatrix * mvMatrix * vec4(vposition, 1.0);
	vColor = vec4(vInColor, 1.0);
}
