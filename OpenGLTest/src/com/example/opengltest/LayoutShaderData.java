package com.example.opengltest;

public final class LayoutShaderData 
{
	public final float[] COORDS = new float[] 
	{
		-1.0f, 1.0f, 1.0f, // Front
		1.0f, 1.0f, 1.0f,
	   	-1.0f, -1.0f, 1.0f,
	   	1.0f, -1.0f, 1.0f,
	   	
	   	-1.0f, 1.0f, -1.0f, //Left
		-1.0f, 1.0f, 1.0f,
	   	-1.0f, -1.0f, -1.0f,
	   	-1.0f, -1.0f, 1.0f,
	   	
	   	1.0f, 1.0f, 1.0f, // Right
		1.0f, 1.0f, -1.0f,
	   	1.0f, -1.0f, 1.0f,
	   	1.0f, -1.0f, -1.0f,
	   	
	   	-1.0f, 1.0f, -1.0f, // Back
		1.0f, 1.0f, -1.0f,
	   	-1.0f, -1.0f, -1.0f,
	   	1.0f, -1.0f, -1.0f,
	   	
	   	-1.0f, 1.0f, -1.0f, // Up
		1.0f, 1.0f, -1.0f,
	   	-1.0f, 1.0f, 1.0f,
	   	1.0f, 1.0f, 1.0f,
	   	
	   	-1.0f, -1.0f, 1.0f, // Down
		1.0f, -1.0f, 1.0f,
	   	-1.0f, -1.0f, -1.0f,
	   	1.0f, -1.0f, -1.0f,
	};
	
	public final float[] NORMALS = new float[]
	{
		0.0f, 0.0f, 1.0f,	// Front
		0.0f, 0.0f, 1.0f,
		0.0f, 0.0f, 1.0f,
		0.0f, 0.0f, 1.0f,
		
		-1.0f, 0.0f, 0.0f,	// Left
		-1.0f, 0.0f, 0.0f,
		-1.0f, 0.0f, 0.0f,
		-1.0f, 0.0f, 0.0f,
		
		1.0f,  0.0f, 0.0f,	// Right
		1.0f,  0.0f, 0.0f,
		1.0f,  0.0f, 0.0f,
		1.0f,  0.0f, 0.0f,
		
		0.0f, 0.0f, -1.0f,	// Back
		0.0f, 0.0f, -1.0f,
		0.0f, 0.0f, -1.0f,
		0.0f, 0.0f, -1.0f,
		
		0.0f,  1.0f, 0.0f,	// Up
		0.0f,  1.0f, 0.0f,
		0.0f,  1.0f, 0.0f,
		0.0f,  1.0f, 0.0f,
		
		0.0f, -1.0f, 0.0f,	// Down
		0.0f, -1.0f, 0.0f,
		0.0f, -1.0f, 0.0f,
		0.0f, -1.0f, 0.0f,
	};
	
	public final float[] INDICES = new float[]
	{
	   0.0f, 0.0f,
	   1.0f, 0.0f,
	   0.0f, 1.0f,
	   1.0f, 1.0f, 
	   
	   0.0f, 0.0f,
	   1.0f, 0.0f,
	   0.0f, 1.0f,
	   1.0f, 1.0f,  
	   
	   0.0f, 0.0f,
	   1.0f, 0.0f,
	   0.0f, 1.0f,
	   1.0f, 1.0f,  
	   
	   0.0f, 0.0f,
	   1.0f, 0.0f,
	   0.0f, 1.0f,
	   1.0f, 1.0f,  
	   
	   0.0f, 0.0f,
	   1.0f, 0.0f,
	   0.0f, 1.0f,
	   1.0f, 1.0f,  
	   
	   0.0f, 0.0f,
	   1.0f, 0.0f,
	   0.0f, 1.0f,
	   1.0f, 1.0f,  
	};
	
	public final short[] DRAWORDER =  new short[]
	{
			0,2,3,
			0,3,1,
			
			4,6,7,
			4,7,5,
			
			8,10,11,
			8,11,9,
			
			12,14,15,
			12,15,13,
			
			16,18,19,
			16,19,17,
			
			20,22,23,
			20,23,21,
	}; // order to draw vertices
	
	public final String ImageVertexShaderCode =
			"uniform mat4 u_MVP;" +
			"uniform mat4 u_ModelView;" +
			"uniform vec3 u_LightPos;" +
	        "attribute vec3 position;" +
	        "attribute vec2 inputTextureCoordinate;" +
	        "attribute vec3 a_Normal;" +
	        "varying vec2 textureCoordinate;" +
	        "varying float diffuse;" +
	        "void main()" +
	        "{"+
	        	"diffuse = 1.0f;" +
	            "gl_Position = u_MVP * vec4(position, 1.0);"+
	            "textureCoordinate = inputTextureCoordinate;" +
	            "vec3 modelViewVertex = vec3(u_ModelView * vec4(position, 1.0));" +  
			    "vec3 modelViewNormal = vec3(u_ModelView * vec4(a_Normal, 0.0));" +
			    "vec3 lightVector = normalize(u_LightPos - modelViewVertex);" +
			    "diffuse = max(dot(modelViewNormal, lightVector), 0.3f);" +
			    "float distance = length(u_LightPos - modelViewVertex);" +
			    "diffuse = diffuse * (1.0 / (1.0 + (0.01 * distance * distance)));" +
	        "}";
	public final String ImageFragmentShaderCode =
	        "precision mediump float;" +
	        "varying vec2 textureCoordinate;                            \n" +
	        "varying float diffuse;										\n" +
	        "uniform sampler2D s_texture;               \n" +
	        "void main(void) {" +
	        "  gl_FragColor = texture2D( s_texture, textureCoordinate ) * diffuse;\n" +
	        "}";
}
