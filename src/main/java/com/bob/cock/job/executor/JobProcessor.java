package com.bob.cock.job.executor;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bob.cock.job.JobShard;
import com.bob.cock.job.manager.IJobManager;

public class JobProcessor implements IJobProcessor {
    
    private static final Logger LOG = LoggerFactory.getLogger(JobProcessor.class);
    
    private static final int STOP_PROCESSOR_SERVER_TIMEOUT = 60;
    
    private final PauseableThreadPoolExecutor executor;
    
    private final IJobManager jobManager;

    public JobProcessor(IJobManager jobManager, int workers) {
        this.executor = new PauseableThreadPoolExecutor(workers, workers,
            0L, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<Runnable>());
        
        this.jobManager = jobManager;
    }

    @Override
    public void submitJob(Runnable job) {
        executor.execute(job);
    }

    @Override
    public boolean isIdle() {
        return executor.getQueue().size() == 0 && executor.getActiveCount() == 0;
    }
    
    @Override
    public boolean isActive() {
        return executor.getQueue().size() !=0 || executor.getActiveCount() != 0;
    }

    @Override
    public void stopQuietly() {
        //关闭调度器
        try {
            executor.shutdown();
            if (executor.awaitTermination(STOP_PROCESSOR_SERVER_TIMEOUT, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        
            executor.awaitTermination(STOP_PROCESSOR_SERVER_TIMEOUT, TimeUnit.SECONDS);
        } catch (Exception ignore) {
            LOG.error("Stop JobProcessor error", ignore);
        }
    }
    
    @Override
    public void signalStop() {
        this.executor.shutdown();
    }
    
    class PauseableThreadPoolExecutor extends ThreadPoolExecutor {
        
        public PauseableThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue) {
            super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
        }

        @Override
        protected void afterExecute(Runnable r, Throwable cause) {
            JobShard shard = ((JobShardCommand) r).getJobShard();
            jobManager.balanceJobShardVersion(shard.getJobShardCode());
        }
    }
}
