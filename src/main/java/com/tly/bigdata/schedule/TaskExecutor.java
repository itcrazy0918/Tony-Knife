package com.tly.bigdata.schedule;

import org.apache.log4j.Logger;

import java.util.Comparator;
import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.concurrent.*;


/**
 *
 * <pre>
 * 全局计时任务 Timer:基于绝对时间，因为异常会导致timer终止 ScheduledExecutorService
 *              基于相对时间，如果出现异常会重新创建线程
 *              系统时间调整后，任务会一次性执行没有执行过的所有周期任务。假如某个任务一个小时执行一次，
 *              那么直接调一天后，这个任务会一次性执行24次
 * </pre>
 * @author tly  1170382650@qq.com
 * @version 1.00.00
 * <pre>
 * 修改记录
 *    修改后版本:    修改人：  修改日期:     修改内容:
 * </pre>
 */
public class TaskExecutor {

    private static Logger logger = Logger.getLogger(TaskExecutor.class);

    private static ScheduledExecutorService ses;//周期线程池
    private static PriorityQueue<ScheduleTask> taskQueue;//任务队列
	private static ExecutorService es;//任务线程池
	private static ExecutorService actorES;// Actor单线程模式，用于解决并发问题

	public static void init() {
		ses = new ScheduledThreadPoolExecutor(1, new NamedThreadFactory("GameExecutorTick"));
		es = Executors.newFixedThreadPool(3, new NamedThreadFactory("GameExecutorTask"));
		actorES = Executors.newSingleThreadExecutor(new NamedThreadFactory("ActorThread"));

		taskQueue = new PriorityQueue<ScheduleTask>(1, new Comparator<ScheduleTask>() {

			@Override
			public int compare(ScheduleTask task1, ScheduleTask task2) {
				if (task1.getNextRunTime() < task2.getNextRunTime())
					return -1;
				if (task1.getNextRunTime() == task2.getNextRunTime())
					return 0;
				return 1;
			}

		});
		ses.scheduleAtFixedRate(new JobTask(), 0, 1000, TimeUnit.MILLISECONDS);
	}

	public static void stop() {
		ses.shutdown();
		es.shutdown();
		actorES.shutdown();
		taskQueue.clear();
	}

	/**
	 * 添加一个任务，任务之间会并发执行，其线程安全需要外部保证
	 * 
	 * @param task
	 */
	public static void addTask(ScheduleTask task) {
		if (task == null) {
			logger.error("Task must not be null");
			return;
		}
		synchronized (taskQueue) {
			try {
				taskQueue.add(task);
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			}
		}

	}

	public static void addToActor(Runnable task) {
		actorES.submit(task);
	}

    /**
     * 从任务队列清除一个任务
     *
     * @param task
     */
	public static void removeTask(ScheduleTask task) {
		if (task == null) {
			logger.error("Task must not be null");
			return;
		}
		synchronized (taskQueue) {
			try {
				taskQueue.remove(task);
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			}
		}
	}

	static class JobTask implements Runnable {

		private Set<ScheduleTask> toRunSet = new HashSet<ScheduleTask>();

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.util.TimerTask#run()
		 */
		@Override
		public void run() {
			long now = System.currentTimeMillis();
			synchronized (taskQueue) {// 快进快出
				while (taskQueue.size() > 0) {
					ScheduleTask task = taskQueue.element();
					long nextRunTime = task.getNextRunTime();
					if (task != null && now >= nextRunTime) {
						task.setRunCount(task.getRunCount() + 1);
						taskQueue.remove(task);
						if (task.getRunCount() < task.getRepeat() || task.getRepeat() == -1) {
							task.setNextRunTime(nextRunTime == 0 ? now : nextRunTime + task.getPeriod());// 使用nextRunTime
																											// 调系统时间后，可以执行多次，如果使用now，只会执行一次
							taskQueue.add(task);// 重新添加到队列中
						}
						toRunSet.add(task);// 不重复添加
						if (toRunSet.size() == taskQueue.size())
							break;// 避免异常情况下的死循环
					} else {
						break;
					}
				}
			}

			for (ScheduleTask task : toRunSet) {
				String taskName = task.getName();
				logger.debug("do job" + (taskName != null ? taskName : ""));
				try {
					es.execute(task);// 一定要保证它是线程安全的
				} catch (Exception e) {
					logger.error(e.getMessage(), e);
				}
			}
			toRunSet.clear();

		}

	}

}
