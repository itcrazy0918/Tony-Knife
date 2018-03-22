package com.tly.bigdata.shell;

import java.util.concurrent.Callable;

import com.tly.bigdata.util.CheckUtil;


/**
 *
 * <pre>
 * 执行shell命令,并获取返回值
 * </pre>
 * @author tly  1170382650@qq.com
 * @version 1.00.00
 * <pre>
 * 修改记录
 *    修改后版本:    修改人：  修改日期:     修改内容:
 * </pre>
 */
public class ShellExecuteTask implements Callable<ShellExecutor.CommandResult>{
	
	private String command;
	
	public ShellExecuteTask (String command) {
        CheckUtil.checkNotNull("ShellExecute.ShellExecuteTask", command);
        
        this.command = command;
    }
	
	@Override
    public ShellExecutor.CommandResult call() {
        return ShellExecutor.execute(command);
    }
}
