package com.example.demo;

public class YourTest {

	private static YourTest yt;
	
	public static YourTest getInstance()
	{
		if(yt==null)
		  yt=new YourTest();
		
		return yt;
	}
	
	public void output()
	{
		System.out.println("출력");
	}
	
}
