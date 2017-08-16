package com.bob.cock.job;

import com.bob.cock.job.utils.ScheduleUtils;
import com.bob.cock.job.utils.UUIDUtils;

public class SchedulerInfo {

    private Integer id;
    
    private String uuid;

    private String jobCode;
	
	private String register;
	
	private Long lastHeartbeatTime;
	
	private String ip;
	
	private String hostName;
	
	private String status;
	
	public SchedulerInfo() {
	}
	
	public SchedulerInfo(Integer id, String jobCode, String register, Long lastHeartbeatTime, String status) {
	    this.id = id;
	    this.uuid = UUIDUtils.randomUUID();
	    this.jobCode = jobCode;
	    this.register = register;
	    this.lastHeartbeatTime = lastHeartbeatTime;
	    this.ip = ScheduleUtils.getLocalIP();
	    this.hostName = ScheduleUtils.getLocalHostName();
	    this.status = status;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
    
    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getJobCode() {
        return jobCode;
    }

    public void setJobCode(String jobCode) {
        this.jobCode = jobCode;
    }
    
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRegister() {
        return register;
    }

    public void setRegister(String register) {
        this.register = register;
    }

    public Long getLastHeartbeatTime() {
        return lastHeartbeatTime;
    }

    public void setLastHeartbeatTime(Long lastHeartbeatTime) {
        this.lastHeartbeatTime = lastHeartbeatTime;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }
}
