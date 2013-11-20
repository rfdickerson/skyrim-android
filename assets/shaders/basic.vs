uniform mat4 uMVPMatrix;

attribute vec4 vPosition;
varying vec4 vColor2;

void main() {
   vColor2 = vPosition;
   gl_Position = vPosition * uMVPMatrix;
   
}