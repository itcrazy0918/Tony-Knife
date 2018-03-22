package com.tly.bigdata.shell;


public class ShellExecutorTest {
    
    public ShellExecutorTest() {
    }
    
    public static void main(String[] args) {
    	System.out.println("execute");
        ShellExecutor.CommandResult result = ShellExecutor.execute("dir c:");
        System.out.println(result.toString());
	}
}
