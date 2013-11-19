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

	public static List<Short> parseFace(String[] words)
	{
		List<Short> indices = new ArrayList<Short>();
		String[] parts;
		int i=1;
		for (i=1;i<4;i++)
		{
			 parts = words[i].split("/");
			 short s = Short.parseShort(parts[0]);
			 s--;
			 indices.add(s  );
		}
		
		return indices;
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
		BufferedReader br = null;
		AssetManager assetMgr = mActivity.getAssets();
		try {
			InputStream is = assetMgr.open(fileName,
					AssetManager.ACCESS_STREAMING);
			Log.i(TAG, "Loaded the stream" + is);
			
			String line;
			br = new BufferedReader(new InputStreamReader(is));
			List<Float> vertices = new ArrayList<Float>();
			List<Float> vertexNormals = new ArrayList<Float>();
			List<Short> faces = new ArrayList<Short>();
			
			while ((line = br.readLine()) != null) {
				String[] words;
				words = line.split(" ");
				
				
				if (words[0].equals("v"))
				{
					float x = Float.parseFloat(words[1]);
					float y = Float.parseFloat(words[2]);
					float z = Float.parseFloat(words[3]);
					vertices.add(x);
					vertices.add(y);
					vertices.add(z);
					
				}
				else if (words[0].equals("f"))
				{
					
					List<Short> indices = parseFace(words);
					
					faces.addAll(indices);
				}
				else if (words[0].equals("vn"))
				{
					float x = Float.parseFloat(words[1]);
					float y = Float.parseFloat(words[2]);
					float z = Float.parseFloat(words[3]);
					vertexNormals.add(x);
					vertexNormals.add(y);
					vertexNormals.add(z);
				}
				
			}
			
			int i=0;
			float[] v = new float[vertices.size()];
			for (Float vertex: vertices)
			{
				v[i++] = vertex;
			}
			
			i = 0;
			short[] f = new short[faces.size()];
			for (Short face: faces)
			{
				f[i++] = face;
			}
			
			/*
			i = 0;
			float[] vn = new float[vertexNormals.size()];
			for (Float n : vertexNormals)
			{
				vn[i++] = n;
			}
			*/
			
			Mesh mesh = new Mesh(v, f);
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
