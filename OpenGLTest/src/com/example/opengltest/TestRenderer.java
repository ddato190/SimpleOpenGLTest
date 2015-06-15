package com.example.opengltest;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
import android.opengl.Matrix;

public class TestRenderer implements GLSurfaceView.Renderer
{
	// Context
	private Context mContext;
	private final LayoutShaderData DATA = new LayoutShaderData();
	private int mScreenWidth = 0, mScreenHeight = 0;
	private int[] mTextures;
	private int mCurrentTexture;
	private int mCurrentProgram;
	private float[] mModel;
	private float[] mView;
	private float[] mProjection;
	private float[] mModelView;
	private float[] mModelViewProjection;
	
	private int mUniformMVPPosition;
	private int mUniformTexturePosition;
	private int mAttributePosition;
	private int mAttributeTexCoord;
	private int mAttributeNormal;
	
	private int mUniformModelViewPosition;
	private int mUniformLighttPosPosition;
	
	private FloatBuffer mVertices;
	private FloatBuffer mIndices;
	private FloatBuffer mNormals;
	private ShortBuffer mDrawOrders;
	
	int BYTESPERFLOAT = 4;
	int BYTESPERSHORT = 2;
	
	float mRotationAngle = 0.0f;
	
	private final float[] LIGHT_POS_IN_WORLD_SPACE = new float[] { 0.0f, 0.0f, 0.0f, 1.0f };
	private final float[] lightPosInEyeSpace = new float[4];
	
	public TestRenderer(Context context) 
	{
    	mContext = context;
    	
    	mModel = new float[16];
		mView = new float[16];
		mProjection = new float[16];
		mModelView = new float[16];
		mModelView = new float[16];
		mModelViewProjection = new float[16];
    }
	
	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config) 
	{
		GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
		
		//Prepare Camera (View)
		Matrix.setLookAtM(mView, 0, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, -1.0f, 0.0f, 1.0f, 0.0f);
		
		//Gen Textures
		mTextures = new int[1];
		GLES20.glGenTextures(1, mTextures, 0);
		mCurrentTexture = mTextures[0];
		
		//Prepare Buffers
		ByteBuffer bfVertices = ByteBuffer.allocateDirect(DATA.COORDS.length * BYTESPERFLOAT);
		bfVertices.order(ByteOrder.nativeOrder());
		mVertices = bfVertices.asFloatBuffer();
		mVertices.put(DATA.COORDS);
		mVertices.position(0);
		
		ByteBuffer bfIndices = ByteBuffer.allocateDirect(DATA.INDICES.length * BYTESPERFLOAT);
		bfIndices.order(ByteOrder.nativeOrder());
		mIndices = bfIndices.asFloatBuffer();
		mIndices.put(DATA.INDICES);
		mIndices.position(0);
		
		ByteBuffer bfNormals = ByteBuffer.allocateDirect(DATA.NORMALS.length * BYTESPERFLOAT);
		bfNormals.order(ByteOrder.nativeOrder());
		mNormals = bfNormals.asFloatBuffer();
		mNormals.put(DATA.NORMALS);
		mNormals.position(0);
		
		ByteBuffer bfDrawOder = ByteBuffer.allocateDirect(DATA.DRAWORDER.length * BYTESPERSHORT);
		bfDrawOder.order(ByteOrder.nativeOrder());
		mDrawOrders = bfDrawOder.asShortBuffer();
		mDrawOrders.put(DATA.DRAWORDER);
		mDrawOrders.position(0);
		
		int mVertexShader = GLProgramUtil.loadGLShader(GLES20.GL_VERTEX_SHADER, DATA.ImageVertexShaderCode);
        int mFragmentShader = GLProgramUtil.loadGLShader(GLES20.GL_FRAGMENT_SHADER, DATA.ImageFragmentShaderCode);
        
        mCurrentProgram = GLES20.glCreateProgram();
        GLES20.glAttachShader(mCurrentProgram, mVertexShader);
		GLES20.glAttachShader(mCurrentProgram, mFragmentShader);
		GLES20.glLinkProgram(mCurrentProgram);
		
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mCurrentTexture);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
        		GLES20.GL_TEXTURE_MIN_FILTER,GLES20.GL_LINEAR);        
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
        		GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D,
        		GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D,
        		GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
        Bitmap mImageBMP = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.pano2);
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, mImageBMP, 0);
        mImageBMP.recycle();
        
        
        GLES20.glEnable( GLES20.GL_DEPTH_TEST );
	}
	
	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height) 
	{
		mScreenWidth = width;
		mScreenHeight = height;
		
		//Set View Port
		GLES20.glViewport(0, 0, mScreenWidth, mScreenHeight);
		
		//Prepare Projection
		float aspect = (float)mScreenWidth / mScreenHeight;
		Matrix.perspectiveM(mProjection, 0, 70.0f, aspect, 0.01f, 100.0f);
	}
	
	@Override
	public void onDrawFrame(GL10 gl) 
	{
		GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT  | GLES20.GL_DEPTH_BUFFER_BIT);
		GLES20.glCullFace(GLES20.GL_BACK);
		
		GLES20.glUseProgram(mCurrentProgram);
		GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mCurrentTexture);
		
		mAttributePosition = GLES20.glGetAttribLocation(mCurrentProgram, "position");
		mAttributeTexCoord = GLES20.glGetAttribLocation(mCurrentProgram, "inputTextureCoordinate");
		mAttributeNormal = GLES20.glGetAttribLocation(mCurrentProgram, "a_Normal");
		GLES20.glEnableVertexAttribArray(mAttributePosition);
        GLES20.glEnableVertexAttribArray(mAttributeTexCoord);
        GLES20.glEnableVertexAttribArray(mAttributeNormal);
        
        mUniformMVPPosition = GLES20.glGetUniformLocation(mCurrentProgram, "u_MVP");
        mUniformTexturePosition = GLES20.glGetUniformLocation(mCurrentProgram, "s_texture");
        mUniformModelViewPosition = GLES20.glGetUniformLocation(mCurrentProgram, "u_ModelView");
        mUniformLighttPosPosition = GLES20.glGetUniformLocation(mCurrentProgram, "u_LightPos");
        
        Matrix.setIdentityM(mModel, 0);
        Matrix.translateM(mModel, 0, 0, 0, -5.0f);
        Matrix.rotateM(mModel, 0, mRotationAngle, 1.0f, 0.0f, 0.0f);
        Matrix.rotateM(mModel, 0, mRotationAngle, 0.0f, 1.0f, 0.0f);
        Matrix.multiplyMM(mModelView, 0, mView, 0, mModel, 0);
        Matrix.multiplyMM(mModelViewProjection, 0, mProjection, 0, mModelView, 0);
        
        Matrix.multiplyMV(lightPosInEyeSpace, 0, mView, 0, LIGHT_POS_IN_WORLD_SPACE, 0);
		GLES20.glUniform3fv(mUniformLighttPosPosition, 1, lightPosInEyeSpace, 0);
        
        
        GLES20.glVertexAttribPointer(mAttributePosition, 3, GLES20.GL_FLOAT, false, 12, mVertices);
        GLES20.glVertexAttribPointer(mAttributeTexCoord, 2, GLES20.GL_FLOAT, false, 8, mIndices);
        GLES20.glVertexAttribPointer(mAttributeNormal, 3, GLES20.GL_FLOAT, false, 12, mNormals);
        GLES20.glUniformMatrix4fv(mUniformMVPPosition, 1, false, mModelViewProjection, 0);
        GLES20.glUniformMatrix4fv(mUniformModelViewPosition, 1, false, mModelView, 0);
        GLES20.glUniform1i( mUniformTexturePosition, 0);
        
        GLES20.glDrawElements(GLES20.GL_TRIANGLES, DATA.DRAWORDER.length,
				  GLES20.GL_UNSIGNED_SHORT, mDrawOrders); // Front Cube
        
        Matrix.setIdentityM(mModel, 0);
        Matrix.translateM(mModel, 0, 2.0f, 5.0f, -12.0f);
        Matrix.rotateM(mModel, 0, mRotationAngle, 1.0f, 0.0f, 0.0f);
        Matrix.rotateM(mModel, 0, mRotationAngle, 0.0f, 1.0f, 0.0f);
        Matrix.multiplyMM(mModelView, 0, mView, 0, mModel, 0);
        Matrix.multiplyMM(mModelViewProjection, 0, mProjection, 0, mModelView, 0);
        GLES20.glUniformMatrix4fv(mUniformMVPPosition, 1, false, mModelViewProjection, 0);
        GLES20.glUniformMatrix4fv(mUniformModelViewPosition, 1, false, mModelView, 0);
        
        GLES20.glDrawElements(GLES20.GL_TRIANGLES, DATA.DRAWORDER.length,
				  GLES20.GL_UNSIGNED_SHORT, mDrawOrders); // Back Cube
        
        mRotationAngle += 1.0f;
        
        GLES20.glDisableVertexAttribArray(mAttributePosition);
        GLES20.glDisableVertexAttribArray(mAttributeTexCoord);
        GLES20.glDisableVertexAttribArray(mAttributeNormal);
	}
}
