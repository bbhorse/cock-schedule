package com.bob.cock.job.manager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.bob.cock.job.SchedulerInfo;
import com.bob.cock.job.data.jdbc.SchedulerInfoDao;
import com.bob.cock.job.utils.CollectionUtils;

public class SchedulerManager implements ISchedulerManager {
    
    private SchedulerInfoDao schedulerInfoDao = new SchedulerInfoDao();

    @Override
    public void createSchedulerInfo(SchedulerInfo schedulerInfo) {
        schedulerInfoDao.createSchedulerInfo(schedulerInfo);
    }

    @Override
    public void deleteSchedulerInfo(Integer id) {
        schedulerInfoDao.deleteSchedulerInfo(id);
    }

    @Override
    public void updateSchedulerInfoLastHeartbeatTimeAndStatus(Long lastHeartbeatTime, String status, Integer schedulerId) {
        schedulerInfoDao.updateSchedulerInfoLastHeartbeatTimeAndStatus(lastHeartbeatTime, status, schedulerId);
    }

    @Override
    public List<SchedulerInfo> loadAllSchedulers(String jobCode) {
        List<SchedulerInfo> infos = schedulerInfoDao.findByJobCode(jobCode);
        if (CollectionUtils.isEmpty(infos)) {
            return Collections.emptyList();
        }
        
        return infos;
    }

    @Override
    public List<Integer> loadAllSchedulerIds(String jobCode) {
        List<SchedulerInfo> infos = loadAllSchedulers(jobCode);
        if (CollectionUtils.isEmpty(infos)) {
            return Collections.emptyList();
        }
        
        List<Integer> ids = new ArrayList<>(infos.size());
        for (SchedulerInfo info : infos) {
            ids.add(info.getId());
        }
        
        return ids;
    }

    @Override
    public SchedulerInfo loadSchedulerInfo(Integer id) {
        return schedulerInfoDao.findById(id);
    }
    
    @Override
    public Long getCurTimestrap() {
        return schedulerInfoDao.getCurTimestamp();
    }
}
