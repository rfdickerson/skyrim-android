package com.example.android.opengl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.res.AssetManager;
import android.util.Log;

public class ResourceLoader {

	private static final String TAG = "ObjLoader";
	
	private static ResourceLoader instance = null;
	private Activity mActivity;
	
	private Map<String,Mesh> meshes = new HashMap<String,Mesh>();
	
	private ResourceLoader() {
		
	}
	
	public void setActivity(Activity a)
	{
		this.mActivity = a;
	}
	
	public static ResourceLoader getResourceLoader()
	{
		if (instance == null)
		{
			instance = new ResourceLoader();
		}
		
		return instance;
	}

	public static void parseFace(String[] words, List<Short> vertexIndices, List<Short> textureIndices, List<Short> normalIndices)
	{
		//List<Short> indices = new ArrayList<Short>();
		String[] parts;
		int i=1;
		for (i=1;i<4;i++)
		{
			 parts = words[i].split("/");
			 short s1 = Short.parseShort(parts[0]);
			 short s2 = Short.parseShort(parts[1]);
			 short s3 = Short.parseShort(parts[2]);
			 
			 s1--;
			 s2--;
			 s3--;
			 
			 vertexIndices.add(s1);
			 textureIndices.add(s2);
			 normalIndices.add(s3);
		}
		
		
	}
	
	public String readTextFile(String filename)
	{
		BufferedReader br = null;
		AssetManager assetMgr = mActivity.getAssets();
		try {
			InputStream is = assetMgr.open(filename);
			StringBuilder builder = new StringBuilder();
			String line;
			br = new BufferedReader(new InputStreamReader(is));
			while ((line = br.readLine()) != null)
			{
				builder.append(line);
				builder.append("\n");
			}
			
			return builder.toString();
		} catch (IOException ex)
		{
			Log.e(TAG,"Could not open file: " + filename);
		}
		
		return null;
	}
	
	
	public void loadMesh(String meshName, String fileName)
	{
		
		List<Short> vertexIndices = new ArrayList<Short>();
		List<Short> textureIndices = new ArrayList<Short>();
		List<Short> normalIndices = new ArrayList<Short>();
		
		List<Vector3> vertices = new ArrayList<Vector3>();
		List<Vector3> vertexNormals = new ArrayList<Vector3>();
		
		
		//List<Float> newNormals = new ArrayList<Float>();
		
		BufferedReader br = null;
		AssetManager assetMgr = mActivity.getAssets();
		try {
			InputStream is = assetMgr.open(fileName,
					AssetManager.ACCESS_STREAMING);
			Log.i(TAG, "Loaded the stream" + is);
			
			String line;
			br = new BufferedReader(new InputStreamReader(is));
			
			//List<Short> faces = new ArrayList<Short>();
			
			while ((line = br.readLine()) != null) {
				String[] words;
				words = line.split(" ");
				
				
				if (words[0].equals("v"))
				{
					float x = Float.parseFloat(words[1]);
					float y = Float.parseFloat(words[2]);
					float z = Float.parseFloat(words[3]);
					
					Vector3 v = new Vector3(x, y, z);
					vertices.add(v);
					
					
				}
				else if (words[0].equals("f"))
				{				
					parseFace(words, vertexIndices, textureIndices, normalIndices);			
				}
					
			}
			
			for (int i=0; i<vertices.size();i++)
			{
				vertexNormals.add(new Vector3(0,0,0));
			}
			
			// compute the normals
			for (int i=0; i<vertexIndices.size(); i+=3)
			{
				short i1 = vertexIndices.get(i);
				short i2 = vertexIndices.get(i+1);
				short i3 = vertexIndices.get(i+2);
				
				Vector3 v1 = vertices.get(i1); 
				Vector3 v2 = vertices.get(i2); 
				Vector3 v3 = vertices.get(i3); 
				
				Vector3 a = v1.sub(v2);
				Vector3 b = v1.sub(v3);
				
				Vector3 norm = Vector3.cross(a, b);
				norm.normalize();
				
				Vector3 current;
				current = vertexNormals.get(i1);
				current = current.add(norm);
				current.normalize();
				vertexNormals.set(i1, current);
				
				current = vertexNormals.get(i2);
				current = current.add(norm);
				current.normalize();
				vertexNormals.set(i2, current);
				
				current = vertexNormals.get(i3);
				current = current.add(norm);
				current.normalize();
				vertexNormals.set(i3, current);
				//vertexNormals.add(norm);
				
			}
			
			int i=0;
			float[] v = new float[vertices.size()*3];
			for (Vector3 vertex: vertices)
			{
				v[i++] = vertex.x;
				v[i++] = vertex.y;
				v[i++] = vertex.z;
			}
			
			i = 0;
			short[] f = new short[vertexIndices.size()];
			for (Short face: vertexIndices)
			{
				f[i++] = face;
			}
			
			
			i = 0;
			float[] vn = new float[vertexNormals.size()*3];
			for (Vector3 vertex : vertexNormals)
			{
				vn[i++] = vertex.x;
				vn[i++] = vertex.y;
				vn[i++] = vertex.z;
			}
			
			
			Mesh mesh = new Mesh(v, f, vn);
			meshes.put(meshName, mesh);
			
		} catch (IOException ex)
		{
			Log.e(TAG, "Could not find the model");
		}
		
		
	}
	
	public Mesh getMeshByName(String name)
	{
		return meshes.get(name);
	}
	
	
	
}
