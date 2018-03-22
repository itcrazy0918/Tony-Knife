package com.tly.bigdata.schedule;

import org.apache.log4j.Logger;

import java.util.Calendar;
import java.util.TimerTask;

/**
 *
 * <pre>
 * 采用Builder模式创建常用的任务,这里创建的任务会随系统的时间改变而改变
 * </pre>
 * @author tly  1170382650@qq.com
 * @version 1.00.00
 * <pre>
 * 修改记录
 *    修改后版本:    修改人：  修改日期:     修改内容:
 * </pre>
 */
public class TaskBuilder {

	private static Logger logger = Logger.getLogger(TaskBuilder.class);

	/**
	 * 创建一个立即运行的且只运行一次的异步任务
	 * 
	 * @param task
	 */
	public static void createAsyncTask(final TimerTask task) {
		TaskExecutor.addTask(new ScheduleTask(System.currentTimeMillis(), 0, 0) {
			@Override
			public void run() {
				task.run();

			}
		});
	}

	/**
	 * 采用线程封闭技术解决多线程并发问题。
	 * 
	 * @param task
	 */
	public static void createActorJob(final Runnable task) {
		TaskExecutor.addToActor(new Runnable() {

			@Override
			public void run() {
				try {
					task.run();

				} catch (Exception e) {
					logger.error("Actor Job Error", e);
				}

			}
		});
	}

	/**
	 * 构建一个逢整点执行的任务
	 * 
	 * @param task
	 *            要执行的任务内容
	 * @return
	 */
	public static void createWholeHourTask(final TimerTask task) {
		Calendar now = Calendar.getInstance();
		now.set(Calendar.MINUTE, 0);
		now.set(Calendar.SECOND, 0);
		now.add(Calendar.HOUR_OF_DAY, 1);
		long nextRunTime = now.getTimeInMillis();

		TaskExecutor.addTask(new ScheduleTask(nextRunTime, 60 * 60 * 1000, -1) {

			@Override
			public void run() {
				task.run();

			}
		});

	}

	/**
	 * 周期性任务
	 * 
	 * @param delay
	 *            推迟执行
	 * @param period
	 *            周期
	 */
	public static void createRepeatTask(long delay, long period, final TimerTask task) {
		long now = System.currentTimeMillis();
		TaskExecutor.addTask(new ScheduleTask(now + delay, period, -1) {

			@Override
			public void run() {
				task.run();

			}
		});

	}

	/**
	 * 仿JavaScript的setTimeout用法
	 * 
	 * @param task
	 * @param timeout
	 */
	public static void setTimeout(final TimerTask task, long timeout) {
		long now = System.currentTimeMillis();
		TaskExecutor.addTask(new ScheduleTask(now + timeout, 0, 1) {

			@Override
			public void run() {
				task.run();

			}
		});

	}

	/**
	 * 每周几,几点执行一个任务
	 * 
	 * @param day_of_week
	 * @param hour_of_day
	 * @param task
	 * @return
	 */
	public static void createDayOfWeekTask(int day_of_week, int hour_of_day, final TimerTask task) {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.DAY_OF_WEEK, day_of_week);
		cal.set(Calendar.HOUR_OF_DAY, hour_of_day);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		long now = System.currentTimeMillis();
		long time = cal.getTimeInMillis();
		long nextTime;
		if (time > now) {
			nextTime = time;
		} else {
			cal.add(Calendar.DAY_OF_MONTH, 7);
			nextTime = cal.getTimeInMillis();
		}

		TaskExecutor.addTask(new ScheduleTask(nextTime, 7 * 24 * 60 * 60 * 1000, -1) {

			@Override
			public void run() {
				task.run();
			}
		});

	}

	/**
	 * 构建一个从0点开始每隔半个小时的任务，如果因为服务器没有启动而错过了，之前的任务不会补充性执行
	 * 
	 * @param minute_of_day
	 * @param task
	 */
	public static void createHalfOfHourTask(final TimerTask task) {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.MINUTE, 30);
		cal.set(Calendar.SECOND, 0);
		long time = cal.getTimeInMillis();
		long now = System.currentTimeMillis();
		long nextRunTime = time;
		if (time < now) {
			cal.add(Calendar.HOUR, 1);
			cal.set(Calendar.MINUTE, 0);
			cal.set(Calendar.SECOND, 0);
			nextRunTime = cal.getTimeInMillis();
		}

		TaskExecutor.addTask(new ScheduleTask(nextRunTime, 30 * 60 * 1000, -1, "halfOfHour") {

			@Override
			public void run() {
				task.run();

			}
		});

	}

	/**
	 * 构建一个逢每天几点几分执行的任务 如果因为服务器没有启动而错过了，之前的任务不会补充性执行
	 * 
	 * @param hour_of_day
	 *            0-23
	 * @param minute
	 *            0-59
	 * @param task
	 * @return
	 */
	public static void createHourOfDayTask(int hour_of_day, int minute, final TimerTask task) {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.HOUR_OF_DAY, hour_of_day);
		cal.set(Calendar.MINUTE, minute);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		long time = cal.getTimeInMillis();
		long now = System.currentTimeMillis();
		long delta = time - now;
		long period = 24 * 60 * 60 * 1000;
		long nextRunTime;
		if (delta < 0) {
			nextRunTime = now + (period + delta);
		} else {
			nextRunTime = time;
		}
		TaskExecutor.addTask(new ScheduleTask(nextRunTime, period, -1) {

			@Override
			public void run() {
				task.run();

			}
		});

	}

	/**
	 * 构建一个逢每天某个小时执行的任务 如果因为服务器没有启动而错过了，之前的任务不会补充性执行
	 * 
	 * @param hour_of_day
	 * @param task
	 * @return
	 */
	public static void createHourOfDayTask(int hour_of_day, final TimerTask task) {
		createHourOfDayTask(hour_of_day, 0, task);

	}

}
