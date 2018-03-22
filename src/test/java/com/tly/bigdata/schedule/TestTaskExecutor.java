package com.tly.bigdata.schedule;

import com.tly.bigdata.util.DateTimeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.TimerTask;

/***
 * 测试
 */
public class TestTaskExecutor {
    protected final static Logger logger  =  LoggerFactory.getLogger(TestTaskExecutor.class);

    protected void process() {
        int batchSize = 5;
        TaskExecutor.init();
        TaskBuilder.createRepeatTask(1 * 1000,5 * 1000, new TimerTask()
        {
            @Override
            public void run()
            {
                try {
                    //CUSTOM YOUR CODE
                    System.out.println(DateTimeUtil.date2String(new Date(), "yyyy-MM-dd HH:mm:ss"));

                } catch (Exception e) {
                    logger.error("start error", e);
                }
            }
        });
    }

    public static void main(String args[]) {
        TestTaskExecutor test = new TestTaskExecutor();
        test.process();
    }
}
