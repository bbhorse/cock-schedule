package com.bob.cock.job.data.jdbc;

import java.sql.Types;
import java.util.List;

import com.bob.cock.job.Job;

public class JobDao extends BaseDao<Job> {
    
    private static final String INSERT  = "insert into job(job_code, handler_bean, cron_express, heartbeat_rate, judge_dead_interval, fetch_num, thread_num, job_shards, job_parameter) values (?, ?, ?, ?, ?, ?, ?, ?, ?)";
    private static final String UPDATE  = "update job set cron_express = ?, heartbeat_rate = ?, judge_dead_interval = ?, fetch_num = ?, thread_num = ?, job_shards = ?, job_parameter = ? where job_code = ?";
    private static final String DELETE  = "delete from job where job_code = ?";
    private static final String FINDALL = "select * from job";
    private static final String FINDONE = "select * from job where job_code = ?";
    
    public int createJob(Job job) {
        return this.save(INSERT, 
            new Object[] {job.getJobCode(), job.getHandlerBean(), job.getCronExpress(), job.getHeartbeatRate(), job.getJudgeDeadInterval(), job.getFetchNum(), job.getThreadNum(), job.getJobShards(), job.getJobParameter()}, 
            new int[] {Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.INTEGER, Types.INTEGER, Types.INTEGER, Types.INTEGER, Types.VARCHAR, Types.VARCHAR});
    }
    
    public int updateJob(Job job) {
        return this.update(UPDATE, 
                new Object[] {job.getCronExpress(), job.getHeartbeatRate(), job.getJudgeDeadInterval(), job.getFetchNum(), job.getJobShards(), job.getJobParameter(), job.getJobCode()},
                new int[] {Types.VARCHAR, Types.INTEGER, Types.INTEGER, Types.INTEGER, Types.INTEGER, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR});
    }
    
    public int deleteJob(String jobCode) {
        return this.delete(DELETE, new Object[] {jobCode}, new int[] {Types.VARCHAR});
    }
    
    public List<Job> findAll() {
        return this.queryList(Job.class, FINDALL);
    }
    
    public Job findOne(String jobCode) {
        return this.queryObject(Job.class, FINDONE, jobCode);
    }
}