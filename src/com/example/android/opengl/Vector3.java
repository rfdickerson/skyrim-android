package com.example.android.opengl;

public class Vector3 {

	public float x;
	public float y;
	public float z;
	
	public Vector3(float x, float y, float z)
	{
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public float magnitude()
	{
		double sum = Math.pow(this.x, 2)
				+ Math.pow(this.y, 2)
				+ Math.pow(this.z, 2);
		
		return (float)Math.sqrt(sum);
		
		
	}
	
	public void normalize()
	{
		float mag = this.magnitude();
		this.x = this.x/mag;
		this.y = this.y/mag;
		this.z = this.z/mag;
	}
	
	public static Vector3 cross(Vector3 a, Vector3 b)
	{
		float s1 = a.y * b.z - a.z * b.y;
		float s2 = a.z * b.x - a.x * b.z;
		float s3 = a.x * b.y - a.y * b.x;
		
		return new Vector3(s1, s2, s3);
	}
	
	public Vector3 sub(Vector3 v)
	{
		float dx = this.x - v.x;
		float dy = this.y - v.y;
		float dz = this.z - v.z;
		
		return new Vector3(dx, dy, dz);
	}
	
	public Vector3 add(Vector3 v)
	{
		float dx = this.x + v.x;
		float dy = this.y + v.y;
		float dz = this.z + v.z;
		
		return new Vector3(dx, dy, dz);
	}
	
	
	
}
