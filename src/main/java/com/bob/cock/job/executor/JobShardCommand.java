package com.bob.cock.job.executor;

import com.bob.cock.job.IJobHandler;
import com.bob.cock.job.Job;
import com.bob.cock.job.JobShard;
import com.bob.cock.job.utils.ContextUtils;
import com.bob.cock.job.utils.ParameterUtils;

public class JobShardCommand implements Runnable {
    private Job job;
    
    private JobShard jobShard;
    
    private JobShardCommand(Job job, JobShard jobShard) {
        this.job = job;
        this.jobShard = jobShard;
    }

    @Override
    public void run() {
        IJobHandler dealBean = (IJobHandler)ContextUtils.getJobHandler(job.getHandlerBean());
        dealBean.execute(ParameterUtils.parse(job.getJobParameter()), jobShard, job.getFetchNum());
    }

    public Job getJob() {
        return job;
    }

    public JobShard getJobShard() {
        return jobShard;
    }
    
    public static JobShardCommand adapt(Job job, JobShard jobShard) {
        return new JobShardCommand(job, jobShard);
    }
}
