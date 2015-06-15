package com.example.opengltest;

import android.content.Context;
import android.opengl.GLSurfaceView;

public class TestSurfaceView extends GLSurfaceView
{
	TestRenderer mTestRenderer;
	public TestSurfaceView(Context context) 
	{
        super(context);
        // Create an OpenGL ES 2.0 context.
        setEGLContextClientVersion(2);
        
        mTestRenderer = new TestRenderer(context);
        setRenderer(mTestRenderer);
        setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
	}
}
