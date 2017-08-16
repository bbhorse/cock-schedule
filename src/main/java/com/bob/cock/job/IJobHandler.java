package com.bob.cock.job;

public interface IJobHandler {
	
	public boolean execute(JobParameter parameter, JobShard jobShard, int fetchNum);
}
