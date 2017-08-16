package com.bob.cock.job.data.jdbc;

import java.sql.Types;
import java.util.List;

import com.bob.cock.job.SchedulerInfo;

public class SchedulerInfoDao extends BaseDao<SchedulerInfo> {
    
    private static final String INSERT                      = "insert into scheduler_info(uuid, job_code, last_heartbeat_time, ip, hostname, register) values (?, ?, ?, ?, ?, ?)";
    private static final String DELETE                      = "delete from scheduler_info where id = ?";
    private static final String UPDATE_LAST_HEARBEAT_TIME   = "update scheduler_info set last_heartbeat_time = ?, status = ? where id = ?";
    private static final String FIND_BY_JOBCODE             = "select * from scheduler_info where job_code = ? order by id asc";
    private static final String FIND_ID_BY_JOBCODE          = "select id from scheduler_info where uuid = ?";
    private static final String FIND_BY_ID                  = "select * from scheduler_info where id = ?";
    
    public int createSchedulerInfo(SchedulerInfo schedulerInfo) {
        int effect = this.save(INSERT, 
            new Object[] {schedulerInfo.getUuid(), schedulerInfo.getJobCode(), schedulerInfo.getLastHeartbeatTime(), schedulerInfo.getIp(), schedulerInfo.getHostName(), schedulerInfo.getRegister()}, 
            new int[] {Types.VARCHAR, Types.VARCHAR, Types.BIGINT, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR});
        
        schedulerInfo.setId(jdbcTemplate.queryForObject(FIND_ID_BY_JOBCODE, new Object[] {schedulerInfo.getUuid()}, new int[] {Types.VARCHAR}, Integer.class));
        return effect;
    }
    
    public int deleteSchedulerInfo(Integer id) {
        return this.delete(DELETE, new Object[] {id}, new int[] {Types.INTEGER});
    }
    
    public int updateSchedulerInfoLastHeartbeatTimeAndStatus(Long lastHeartbeatTime, String status, Integer id) {
        return this.update(UPDATE_LAST_HEARBEAT_TIME, new Object[] {lastHeartbeatTime, status, id}, new int[] {Types.BIGINT, Types.VARCHAR, Types.INTEGER});
    }

    public List<SchedulerInfo> findByJobCode(String jobCode) {
        return this.queryList(SchedulerInfo.class, FIND_BY_JOBCODE, jobCode);
    }
    
    public SchedulerInfo findById(Integer id) {
        return this.queryObject(SchedulerInfo.class, FIND_BY_ID, id);
    }
}
