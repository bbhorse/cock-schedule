package com.bob.cock.job;

/**
 * 
 * @author YuBo
 * @version $Id: Job.java, v 0.1 2017年8月11日 上午3:49:24 YuBo Exp $
 */
public class Job {
    /** 任务标识*/
	private String jobCode;
	/** 任务处理bean*/
	private String handlerBean;
	/** 任务触发表达式*/
	private volatile String cronExpress;
	/** 心跳频率*/
	private volatile long heartbeatRate;
	/** 任务调度器死亡超时*/
	private volatile int judgeDeadInterval;
	/** 每次获取用于处理的数据量*/
	private volatile int fetchNum;
	/** 处理线程数量*/
	private volatile int threadNum;
	/** 任务分片*/
	private volatile String jobShards;
	/** 任务参数*/
	private volatile String jobParameter;
	
    public String getJobCode() {
        return jobCode;
    }
    
    public void setJobCode(String jobCode) {
        this.jobCode = jobCode;
    }
    
    public String getHandlerBean() {
        return handlerBean;
    }

    public void setHandlerBean(String handlerBean) {
        this.handlerBean = handlerBean;
    }

    public String getCronExpress() {
        return cronExpress;
    }
    
    public void setCronExpress(String cronExpress) {
        this.cronExpress = cronExpress;
    }
    
    public long getHeartbeatRate() {
        return heartbeatRate;
    }

    public void setHeartbeatRate(long heartbeatRate) {
        this.heartbeatRate = heartbeatRate;
    }

    public int getJudgeDeadInterval() {
        return judgeDeadInterval;
    }
    
    public void setJudgeDeadInterval(int judgeDeadInterval) {
        this.judgeDeadInterval = judgeDeadInterval;
    }
    
    public int getFetchNum() {
        return fetchNum;
    }
    
    public void setFetchNum(int fetchNum) {
        this.fetchNum = fetchNum;
    }
    
    public int getThreadNum() {
        return threadNum;
    }
    
    public void setThreadNum(int threadNum) {
        this.threadNum = threadNum;
    }
    
    public String getJobShards() {
        return jobShards;
    }

    public void setJobShards(String jobShards) {
        this.jobShards = jobShards;
    }

    public String getJobParameter() {
        return jobParameter;
    }

    public void setJobParameter(String jobParameter) {
        this.jobParameter = jobParameter;
    }
}
