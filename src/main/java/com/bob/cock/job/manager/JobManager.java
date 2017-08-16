package com.bob.cock.job.manager;

import java.util.Collections;
import java.util.List;

import com.bob.cock.job.Job;
import com.bob.cock.job.JobShard;
import com.bob.cock.job.data.jdbc.JobDao;
import com.bob.cock.job.data.jdbc.JobShardDao;
import com.bob.cock.job.utils.CollectionUtils;

public class JobManager implements IJobManager {
    
    private JobDao jobDao = new JobDao();

    private JobShardDao jobShardDao = new JobShardDao();

    @Override
    public void createJob(Job job) {
        jobDao.createJob(job);
    }

    @Override
    public void updateJob(Job job) {
        jobDao.updateJob(job);
    }

    @Override
    public void deleteJob(String jobCode) {
        jobDao.deleteJob(jobCode);
    }

    @Override
    public List<Job> loadAllJobs() {
        List<Job> jobs = jobDao.findAll();
        if (CollectionUtils.isEmpty(jobs)) {
            return Collections.emptyList();
        }
        
        return jobs;
    }

    @Override
    public Job loadJob(String jobCode) {
        return jobDao.findOne(jobCode);
    }

    @Override
    public void createJobShard(JobShard jobShard) {
        jobShardDao.createJobShard(jobShard);
    }

    @Override
    public List<JobShard> loadAllJobShard(String jobCode) {
        List<JobShard> jobShards = jobShardDao.findByJobCode(jobCode);
        if (CollectionUtils.isEmpty(jobShards)) {
            return Collections.emptyList();
        }
        
        return jobShards;
    }

    @Override
    public List<JobShard> loadAssignedJobShards(String jobCode, Integer schedulerId) {
        List<JobShard> jobShards = jobShardDao.finaByJobCodeAndCurServer(jobCode, schedulerId);
        if (CollectionUtils.isEmpty(jobShards)) {
            return Collections.emptyList();
        }
        
        return jobShards;
    }

    @Override
    public JobShard loadJobShard(String jobShardCode) {
        return jobShardDao.findOne(jobShardCode);
    }

    @Override
    public int updateJobShardCurAndReqServer(String jobShardCode, Integer curServer, Integer reqServer) {
        return jobShardDao.updateJobShardCurAndReqServer(jobShardCode, curServer, reqServer);
    }

    @Override
    public int updateJobShardCurServer(String jobShardCode, Integer curServer) {
        return jobShardDao.updateJobShardCurServer(jobShardCode, curServer);
    }

    @Override
    public int updateJobShardReqServer(String jobShardCode, Integer reqServer) {
        return jobShardDao.updateJobShardReqServer(jobShardCode, reqServer);
    }

    @Override
    public int balanceJobShardVersion(String jobShardCode) {
        return jobShardDao.updateJobShardCurVersionToPlanVersion(jobShardCode);
    }
    
    @Override
    public int refreshJobShardReqVersionAndNextScheduleTime(String jobShardCode, long nextScheduleTime) {
        return jobShardDao.updateJobShardReqVersionAndNextScheduleTime(jobShardCode, nextScheduleTime);
    }
}
