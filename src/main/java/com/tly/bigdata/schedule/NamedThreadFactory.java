package com.tly.bigdata.schedule;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

import com.tly.bigdata.exception.CommonRuntimeException;

/**
 *
 * <pre>
 * 自定义线程工厂
 * </pre>
 * @author tly  1170382650@qq.com
 * @version 1.00.00
 * <pre>
 * 修改记录
 *    修改后版本:    修改人：  修改日期:     修改内容:
 * </pre>
 */
public class NamedThreadFactory implements ThreadFactory {
    private static Set<String> threadNamePrefixCache = new HashSet<String>();
    
    private final AtomicInteger autoId = new AtomicInteger(0);
    private final String threadNamePrefix;
    
    public NamedThreadFactory(String threadNamePrefix) {
        if (threadNamePrefix == null) {
            throw new NullPointerException("NamedThreadFactory.threadNamePrefix can not be null.");
        }
        
        threadNamePrefix = threadNamePrefix.trim();
        if (threadNamePrefix.isEmpty()) {
            throw new CommonRuntimeException("NamedThreadFactory.threadNamePrefix can not be empty.");
        }
        
        synchronized (threadNamePrefixCache) {
            // 验证threadNamePrefix是否已经存在
            if (threadNamePrefixCache.contains(threadNamePrefix)) {
                throw new CommonRuntimeException("NamedThreadFactory.threadNamePrefix='" + threadNamePrefix + "' be repeat.");
            }
            threadNamePrefixCache.add(threadNamePrefix);
        }
        
        this.threadNamePrefix = threadNamePrefix;
    }
    
    @Override
    public Thread newThread(Runnable r) {
        String name = String.format("%s_%d", threadNamePrefix, autoId.incrementAndGet());
        return new Thread(r, name);
    }

}
