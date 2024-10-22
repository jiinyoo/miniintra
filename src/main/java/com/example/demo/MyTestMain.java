package com.example.demo;

public class MyTestMain {

	public static void main(String[] args) {
	   
		// 일반적인 new
		/*
		 * MyTest mt=new MyTest(); mt.output();
		 * 
		 * YourTest yt=YourTest.getInstance(); yt.output();
		 */
		
		int a=1;
		String b=String.format("%03d", a);
		System.out.println(b);
	}

}
