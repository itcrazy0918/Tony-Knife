package com.tly.bigdata.shell;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import com.tly.bigdata.exception.CommonRuntimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 *
 * <pre>
 * shell执行处理
 * </pre>
 * @author tly  1170382650@qq.com
 * @version 1.00.00
 * <pre>
 * 修改记录
 *    修改后版本:    修改人：  修改日期:     修改内容:
 * </pre>
 */
public class ShellExecutor {
    private static final Logger logger = LoggerFactory.getLogger(ShellExecutor.class);
    private static final String NEWLINE = "  ";
    
	static String[] shellCommand;
	static {
		shellCommand = new String[2];
		final String anOSName = System.getProperty("os.name");
		if (anOSName.toLowerCase().startsWith("windows")) {
			// Windows XP, Vista ...
			shellCommand[0] = "cmd";
			shellCommand[1] = "/C";
		} else {
			// Unix, Linux ...
			shellCommand[0] = "/bin/sh";
			shellCommand[1] = "-c";
		}
	}
        
    public static CommandResult execute(String command) {
        
        CommandResult commandResult = null;
        logger.debug("Executing command: {}", command);
        Process process;
        try {
            process = Runtime.getRuntime().exec(
                    new String[] {shellCommand[0], shellCommand[1], command});
        } catch (IOException e) {
            process = null;
            logger.error("Failed construct runtime env.", e);
            throw new CommonRuntimeException("Runtime execute failed.");
        }

        if(process != null) {
            //启动单独的线程来清空进程的标准错误流的缓冲区
            final InputStream errStream = process.getErrorStream();
            final StringBuilder errorLines = new StringBuilder();
            if(errStream != null) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        BufferedReader stdErr = new BufferedReader(new InputStreamReader(errStream));
                        try {
                            String stdErrLine;
                            while((stdErrLine = stdErr.readLine()) != null) {	    		        		
                                    errorLines.append(stdErrLine).append(NEWLINE);
                                    logger.debug(stdErrLine);
                            }
                        } catch(IOException e) {
                                logger.error("Read error output stream failed.", e);
                                throw new CommonRuntimeException("Read error output stream failed.");
                        } finally {
                            try{
                                if(stdErr != null) {
                                    stdErr.close();
                                }
                            } catch(IOException e) {
                                logger.error("Close error output stream failed.", e);
                            }
                        }
                    }
                }).start();
            }

            // 获取命令执行情况
            StringBuilder resultLines = new StringBuilder();
            InputStream inStream = process.getInputStream();
            BufferedReader stdIn = new BufferedReader(new InputStreamReader(inStream));
            try {
                String stdInLine;
                while((stdInLine = stdIn.readLine()) != null) {
                    resultLines.append(stdInLine).append(NEWLINE);
                    logger.debug(stdInLine);
                }
            } catch(IOException e) {
                logger.error("Read standard output stream failed.", e);
                throw new CommonRuntimeException("Read standard output stream failed.");
            } finally {
                try{
                    if(stdIn != null) {
                        stdIn.close();
                    }
                } catch(IOException e) {
                    logger.error("Close standard output stream failed.", e);
                }
            }

            int exitValue = -1;
            try {
                exitValue = process.waitFor();
            } catch (InterruptedException e) {
                logger.error("Execute comand interrupted: {}", command, e);
                throw new CommonRuntimeException("Process interrupted.");
            }
            if(exitValue != 0) {
                logger.error("Execute comand failed: {} exit value: {}", command, exitValue);
            }
            commandResult = new CommandResult();
            commandResult.setExitValue(exitValue);
            commandResult.setResultOutput(resultLines.toString());
            commandResult.setErrorOutput(errorLines.toString());
        } else {
            logger.error("System process internal error when executing command: {}", command);
            throw new CommonRuntimeException("Process failed.");
        }
        
        return commandResult;
    }        
        
    public static class CommandResult {
        private int exitValue;
        private String resultOutput;
        private  String errorOutput;
        public int getExitValue() {
            return exitValue;
        }

        public void setExitValue(int exitValue) {
            this.exitValue = exitValue;
        }

        public String getResultOutput() {
            return resultOutput;
        }
        
        public String[] getRsultArray() {
            return resultOutput.split(ShellExecutor.NEWLINE);
        }
        
        public void setResultOutput(String resultOutput) {
            this.resultOutput = resultOutput;
        }

        public String getErrorOutput() {
            return errorOutput;
        }

        public void setErrorOutput(String errorOutput) {
            this.errorOutput = errorOutput;
        }
        
        @Override
        public String toString(){
            StringBuilder sb = new StringBuilder();
            sb.append("Exit value: ").append(getExitValue()).append(NEWLINE);
            sb.append("Standard output: ").append(getResultOutput()).append(NEWLINE);
            sb.append("Error output: ").append(getErrorOutput()).append(NEWLINE);               
            return sb.toString();
        }

    }
    
}
