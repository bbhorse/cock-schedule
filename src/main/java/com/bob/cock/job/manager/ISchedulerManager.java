package com.bob.cock.job.manager;

import java.util.List;

import com.bob.cock.job.SchedulerInfo;

public interface ISchedulerManager {

    void createSchedulerInfo(SchedulerInfo schedulerInfo);
    
    void deleteSchedulerInfo(Integer id);
    
    void updateSchedulerInfoLastHeartbeatTimeAndStatus(Long lastHeartbeatTime, String status, Integer schedulerId);
    
    List<SchedulerInfo> loadAllSchedulers(String jobCode);

    List<Integer> loadAllSchedulerIds(String jobCode);
    
    SchedulerInfo loadSchedulerInfo(Integer id);
    
    Long getCurTimestrap();
}
