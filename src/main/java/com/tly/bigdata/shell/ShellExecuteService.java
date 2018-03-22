package com.tly.bigdata.shell;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import com.tly.bigdata.schedule.NamedThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



/**
 *
 * <pre>
 * shell任务线程池
 * </pre>
 * @author tly  1170382650@qq.com
 * @version 1.00.00
 * <pre>
 * 修改记录
 *    修改后版本:    修改人：  修改日期:     修改内容:
 * </pre>
 */
public class ShellExecuteService {
    
    private static ExecutorService shellService;   // shell执行任务
    
    private static final Logger logger = LoggerFactory.getLogger(ShellExecuteService.class);
    
    public synchronized static void startUp () {
    	
    	shellService = Executors.newFixedThreadPool(16, new NamedThreadFactory("ShellExecuteService.shellService"+System.currentTimeMillis()));
    }
    
    public synchronized static void shutdown () {
        if (shellService != null) {
        	shellService.shutdown();
        }
    }
    
    public static Future<ShellExecutor.CommandResult> submitShellExecuteTask (ShellExecuteTask task) {
        return shellService.submit(task);
    }

}
