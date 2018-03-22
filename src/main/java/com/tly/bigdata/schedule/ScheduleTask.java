package com.tly.bigdata.schedule;

/**
 *
 * <pre>
 * 按照计划执行的任务，只在服务器启动后才有效
 * </pre>
 * @author tly  1170382650@qq.com
 * @version 1.00.00
 * <pre>
 * 修改记录
 *    修改后版本:    修改人：  修改日期:     修改内容:
 * </pre>
 */
public abstract class ScheduleTask implements Runnable {

	private String name;
	private long period;
	private long repeat;
	private long runCount;
	private long nextRunTime;

	private enum TASK_STATE {
		SCHEDULED, REMOVED
	};

	private TASK_STATE state;

	/**
	 * 
	 * @param startRunTime
	 *            计划开始时间准确时间点 距离 January 1, 1970 UTC. 单位毫秒,如果这个时间是在过去，那么任务会被立即执行
	 * @param period
	 *            重复周期单位 ms
	 * @param repeat
	 *            重复执行次数 -1 表示 无限循环
	 */
	public ScheduleTask(long startRunTime, long period, long repeat, String... otherParam) {
		this.setPeriod(period);
		this.setRepeat(repeat);
		this.setNextRunTime(startRunTime);
		if (otherParam != null && otherParam.length > 0) {
			name = otherParam[0];
		}
	}

	/**
	 * 从任务队列中删除任务,未来不会被执行
	 */
	public void remove() {
		TaskExecutor.removeTask(this);
		state = TASK_STATE.REMOVED;
	}

	public void start() {
		if (state != TASK_STATE.SCHEDULED) {
			TaskExecutor.addTask(this);
			state = TASK_STATE.SCHEDULED;
		}
	}

	public long getPeriod() {
		return period;
	}

	public void setPeriod(long period) {
		this.period = period;
	}

	/**
	 * 
	 * @return 已经运行的次数
	 */
	public long getRunCount() {
		return runCount;
	}

	public void setRunCount(long runCount) {
		this.runCount = runCount;
	}

	/**
	 * 
	 * @return 重复次数
	 */
	public long getRepeat() {
		return repeat;
	}

	public void setRepeat(long repeat) {
		this.repeat = repeat;
	}

	/**
	 * 
	 * @return 下一次运行的时间
	 */
	public long getNextRunTime() {
		return nextRunTime;
	}

	public void setNextRunTime(long nextRunTime) {
		this.nextRunTime = nextRunTime;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
