package com.bob.cock.job.manager;

import java.util.List;

import com.bob.cock.job.Job;
import com.bob.cock.job.JobShard;

public interface IJobManager {
	
	void createJob(Job job);
	
	void updateJob(Job job);
	
	void deleteJob(String jobCode);

	List<Job> loadAllJobs();
	
	Job loadJob(String jobCode);

	void createJobShard(JobShard jobShard);
	
	List<JobShard> loadAllJobShard(String jobCode);

	List<JobShard> loadAssignedJobShards(String jobCode, Integer schedulerId);
	
	JobShard loadJobShard(String jobShardCode);
	
    int updateJobShardCurAndReqServer(String jobShardCode, Integer curServer, Integer reqServer);
    
    int updateJobShardCurServer(String jobShardCode, Integer curServer);
    
    int updateJobShardReqServer(String jobShardCode, Integer reqServer);
    
    int refreshJobShardReqVersionAndNextScheduleTime(String jobShardCode, long nextScheduleTime);
    
    int balanceJobShardVersion(String jobShardCode);
}
