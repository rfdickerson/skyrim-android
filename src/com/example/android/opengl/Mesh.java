package com.example.android.opengl;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

//import javax.microedition.khronos.opengles.GL10;

import android.opengl.GLES20;
import android.util.Log;

public class Mesh {
	
	public static final String TAG = "Mesh";

    private FloatBuffer vertexBuffer;
    private ShortBuffer drawListBuffer;
    private FloatBuffer normalsBuffer;
    private FloatBuffer texCoordsBuffer;
    
    private int mProgram;
    private int mPositionHandle;
    private int mNormalsHandle;
    
    private int mColorHandle;
    private int mMVPMatrixHandle;
    private int mTextureUniformHandle;
    private int mTextureCoordinateHandle;
    private final int mTextureCoordinateDataSize = 2;
    private int mTextureDataHandle;
    
    private float[] lightPos = {1,2,2};
    // private float[] globalAmbient = {.2f,.2f,.2f,1};
    
    /*
    private float[] lightAmbient = {1,1,1,1};
    private float[] lightDiffuse = {.5f,.5f,.5f,1};
    private float[] materialAmbient = {.5f,.5f,.5f,1};
    private float[] materialDiffuse = {.5f,.5f,.5f,1};
    */

    // number of coordinates per vertex in this array
    static final int COORDS_PER_VERTEX = 3;
    //static final int TEX_COORDS_PER_VERTEX = 2;
    
    /*
    static float squareCoords[] = { -0.5f,  0.5f, 0.0f,   // top left
                                    -0.5f, -0.5f, 0.0f,   // bottom left
                                     0.5f, -0.5f, 0.0f,   // bottom right
                                     0.5f,  0.5f, 0.0f }; // top right

    private final short drawOrder[] = { 0, 1, 2, 0, 2, 3 }; // order to draw vertices
	*/
    
    //private float vertices[];
    private final short faces[];
    private final float vertices[];
    private final float vn[];
    private final float uv[];
    
    //private final float textureCoords[];
    
    private final int texVertexStride = mTextureCoordinateDataSize * 4;
    private final int vertexStride = COORDS_PER_VERTEX * 4; // 4 bytes per vertex

    // Set color with red, green, blue and alpha (opacity) values
    //float color[] = { 0.2f, 0.709803922f, 0.898039216f, 1.0f };
    float color[] = { 1.0f, 0.709803922f, 0.898039216f, 1.0f };
    
    public Mesh(float[] vertices, short[] faces, float[] vn, float[] uv) {
    	
    	//this.vertices = vertices;
    	this.faces = faces;
    	this.vertices = vertices;
    	this.vn = vn;
    	this.uv = uv;
    	//this.textureCoords = textureCoords;
       
    }
    
    public void setLighting(float[] mvMatrix)
    {
    	 int mvMatrixHandle = GLES20.glGetUniformLocation(mProgram, "u_MVMatrix");
    	 MyGLRenderer.checkGlError("get model view matrix");
    	 int lightLocHandle = GLES20.glGetUniformLocation(mProgram, "u_LightPos");
    	 MyGLRenderer.checkGlError("get light location");
    	 	 
    	 GLES20.glUniformMatrix4fv(mvMatrixHandle, 1, false, mvMatrix, 0);
         MyGLRenderer.checkGlError("set model view matrix");
    	 
         GLES20.glUniform3fv(lightLocHandle, 1, lightPos, 0);
         MyGLRenderer.checkGlError("light location set");
              
    }
    
    public void initialize()
    {
    	 // initialize vertex byte buffer for shape coordinates
        ByteBuffer bb = ByteBuffer.allocateDirect(
        // (# of coordinate values * 4 bytes per float)
                vertices.length * 4);
        bb.order(ByteOrder.nativeOrder());
        vertexBuffer = bb.asFloatBuffer();
        vertexBuffer.put(this.vertices);
        vertexBuffer.position(0);

        // initialize byte buffer for the draw list
        ByteBuffer dlb = ByteBuffer.allocateDirect(
        // (# of coordinate values * 2 bytes per short)
        		faces.length * 2);
        dlb.order(ByteOrder.nativeOrder());
        drawListBuffer = dlb.asShortBuffer();
        drawListBuffer.put(faces);
        drawListBuffer.position(0);
        
        /* initialize the normals */
        ByteBuffer nlb = ByteBuffer.allocateDirect(
        		vn.length * 4);
        nlb.order(ByteOrder.nativeOrder());
        normalsBuffer = nlb.asFloatBuffer();
        normalsBuffer.put(this.vn);
        normalsBuffer.position(0);
        
        /* setup UV texcoords */
        ByteBuffer uvlb = ByteBuffer.allocateDirect(
        		uv.length * 4);
        uvlb.order(ByteOrder.nativeOrder());
        texCoordsBuffer = uvlb.asFloatBuffer();
        texCoordsBuffer.put(this.uv);
        texCoordsBuffer.position(0);
        
        String vertexSource = ResourceLoader.getResourceLoader().readTextFile("shaders/basic.vs");
        String fragSource = ResourceLoader.getResourceLoader().readTextFile("shaders/basic.fs");

        if (vertexSource == null || fragSource == null)
        {
        	Log.e(TAG, "Could not load the files");
        	return;
        	
        }
        // prepare shaders and OpenGL program
        int vertexShader = MyGLRenderer.loadShader(GLES20.GL_VERTEX_SHADER,
        		vertexSource);
        int fragmentShader = MyGLRenderer.loadShader(GLES20.GL_FRAGMENT_SHADER,
        		fragSource);

        mProgram = GLES20.glCreateProgram();             // create empty OpenGL Program
        GLES20.glAttachShader(mProgram, vertexShader);   // add the vertex shader to program
        GLES20.glAttachShader(mProgram, fragmentShader); // add the fragment shader to program
        GLES20.glLinkProgram(mProgram);                  // create OpenGL program executables
        MyGLRenderer.checkGlError("linking the shader");
        
        ResourceLoader loader = ResourceLoader.getResourceLoader();
        
        mTextureDataHandle = loader.loadTexture("textures/cube.etc");
        
        Log.v(TAG, "Loading shader successful");
    }

    public void draw(float[] mvpMatrix, float[] mvMatrix) {
        // Add program to OpenGL environment
        GLES20.glUseProgram(mProgram);
        MyGLRenderer.checkGlError("Use program");
        
        mPositionHandle = GLES20.glGetAttribLocation(mProgram, "a_Position");
        MyGLRenderer.checkGlError("get Position attribute");
        mNormalsHandle = GLES20.glGetAttribLocation(mProgram, "a_Normal");
        MyGLRenderer.checkGlError("get Normals attribute");
         
        mColorHandle = GLES20.glGetUniformLocation(mProgram, "u_Color");
        MyGLRenderer.checkGlError("get Color uniform");
        mTextureUniformHandle = GLES20.glGetUniformLocation(mProgram, "u_Texture");
        mTextureCoordinateHandle = GLES20.glGetAttribLocation(mProgram, "a_TexCoordinate");
        
        GLES20.glEnableVertexAttribArray(mPositionHandle);
        GLES20.glEnableVertexAttribArray(mNormalsHandle);
        GLES20.glEnableVertexAttribArray(mTextureCoordinateHandle);
        
        

        GLES20.glVertexAttribPointer(mPositionHandle, COORDS_PER_VERTEX,
                                     GLES20.GL_FLOAT, false,
                                     vertexStride, vertexBuffer);
        
         
        GLES20.glVertexAttribPointer(mNormalsHandle, COORDS_PER_VERTEX, 
        		GLES20.GL_FLOAT, false, 
        		vertexStride, normalsBuffer);
        MyGLRenderer.checkGlError("set normals attribute pointer");
        
        GLES20.glVertexAttribPointer(mTextureCoordinateHandle, mTextureCoordinateDataSize, 
        		GLES20.GL_FLOAT, false, 
        		texVertexStride, texCoordsBuffer);
        MyGLRenderer.checkGlError("set texture coordinate attribute pointer");

        GLES20.glUniform4fv(mColorHandle, 1, color, 0);

     
        mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "u_MVPMatrix");
        MyGLRenderer.checkGlError("get MVP Matrix Uniform");
        
        // Apply the projection and view transformation
        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mvpMatrix, 0);
        MyGLRenderer.checkGlError("set MVP Matrix Uniform");
        
        setLighting(mvMatrix);     
        
        // texture stuff

     
        //GLES20.glEnable(GL10.GL_TEXTURE_2D);
        
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextureDataHandle);
        GLES20.glUniform1i(mTextureUniformHandle, 0);
        
        // Draw the square
        GLES20.glDrawElements(GLES20.GL_TRIANGLES, faces.length,
                              GLES20.GL_UNSIGNED_SHORT, drawListBuffer);

        // Disable vertex array
        GLES20.glDisableVertexAttribArray(mPositionHandle);
        GLES20.glDisableVertexAttribArray(mNormalsHandle);
        GLES20.glDisableVertexAttribArray(mTextureCoordinateHandle);
    }
}