package com.tly.bigdata.shell;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;


public class ShellExecuteServiceTest {
    
    String command;
    

    
    public void setUp() {
        if(ShellExecutor.shellCommand[0].equals("cmd"))
            command = "dir";
        else
            command = "ll";        
        
    }

    public void executeInThreadPool() {

        System.out.println("execute in thread pool.");
        ShellExecuteService.startUp();
        setUp();
        
        
        ShellExecuteTask task = new ShellExecuteTask(command);

        Future<ShellExecutor.CommandResult> shellFuture = ShellExecuteService .submitShellExecuteTask(task);
        System.out.println("main thread continue: " + System.currentTimeMillis()); 
        try {
            Thread.sleep(5000);
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
        try {
            ShellExecutor.CommandResult commandResult = shellFuture.get();
            System.out.println(commandResult.toString());
        } catch (InterruptedException ex) {
            ex.printStackTrace(); 
        } catch (ExecutionException ex) {
            ex.printStackTrace();
        }
    }
    
    public static void main(String[] args) {
		new ShellExecuteServiceTest().executeInThreadPool();
	}
}
