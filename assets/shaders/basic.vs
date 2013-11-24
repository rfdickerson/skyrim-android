uniform mat4 u_MVPMatrix;
uniform mat4 u_MVMatrix;
uniform vec3 u_LightPos;
uniform vec4 u_Color;

attribute vec4 a_Position;
attribute vec3 a_Normal;

varying vec4 v_Color;

void main() {
  vec3 modelViewVertex = vec3(u_MVMatrix * a_Position);
  vec3 modelViewNormal = vec3(u_MVMatrix * vec4(a_Normal, 0.0));
  float distance = length(u_LightPos - modelViewVertex);
  vec3 lightVector = normalize(u_LightPos - modelViewVertex);
  
  float diffuse = max(dot(modelViewNormal, lightVector), 0.1);
  diffuse = .25 + diffuse * (1.0 / (1.0 + (0.05 * distance * distance)));
  //v_Color = u_Color * diffuse;
  v_Color = vec4(a_Normal,1);
  
  gl_Position =  a_Position * u_MVPMatrix;
   
}