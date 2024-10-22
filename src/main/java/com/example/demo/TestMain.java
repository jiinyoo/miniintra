package com.example.demo;

public class TestMain {
    public static void main(String[] args) {
		
    	 String tt=Test.print();
      	 //메소드명(tt);
    
    	 //메소드명(Test.print());
    	 
    	 Test test=new Test();
    	 String aa=test.output();
    	 //메소드명(aa);
    	 
    	 new Test().output();
    	 //메소드명(new Test().output());
	}
}
