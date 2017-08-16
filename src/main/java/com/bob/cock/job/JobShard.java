package com.bob.cock.job;

public class JobShard {
	/** 任务标识*/
	private String jobCode;
	/** 任务项标识*/
	private String jobShardCode;
	/** 当前处理调度器*/
	private Integer curServer;
	/** 申请获取处理权的调度器*/
	private Integer reqServer;
	/** 当前版本*/
	private int curVersion;
	/**目标版本*/
	private int reqVersion;
	/** 下次调度时间*/
	private Long nextScheduleTime;
	
    public String getJobCode() {
        return jobCode;
    }
    
    public void setJobCode(String jobCode) {
        this.jobCode = jobCode;
    }
    
    public String getJobShardCode() {
        return jobShardCode;
    }
    
    public void setJobShardCode(String jobShardCode) {
        this.jobShardCode = jobShardCode;
    }
    
    public Integer getCurServer() {
        return curServer;
    }
    
    public void setCurServer(Integer curServer) {
        this.curServer = curServer;
    }
    
    public Integer getReqServer() {
        return reqServer;
    }
    
    public void setReqServer(Integer reqServer) {
        this.reqServer = reqServer;
    }
    
    public int getCurVersion() {
        return curVersion;
    }

    public void setCurVersion(int curVersion) {
        this.curVersion = curVersion;
    }

    public int getReqVersion() {
        return reqVersion;
    }

    public void setReqVersion(int reqVersion) {
        this.reqVersion = reqVersion;
    }
    
    public Long getNextScheduleTime() {
        return nextScheduleTime;
    }

    public void setNextScheduleTime(Long nextScheduleTime) {
        this.nextScheduleTime = nextScheduleTime;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((jobShardCode == null) ? 0 : jobShardCode.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        JobShard other = (JobShard) obj;
        if (jobShardCode == null) {
            if (other.jobShardCode != null)
                return false;
        } else if (!jobShardCode.equals(other.jobShardCode))
            return false;
        return true;
    }
}
