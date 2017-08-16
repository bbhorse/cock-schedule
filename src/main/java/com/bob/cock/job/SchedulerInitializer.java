package com.bob.cock.job;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import com.bob.cock.job.manager.IJobManager;
import com.bob.cock.job.manager.ISchedulerManager;
import com.bob.cock.job.manager.JobManager;
import com.bob.cock.job.manager.SchedulerManager;
import com.bob.cock.job.utils.ContextUtils;

public class SchedulerInitializer extends LifeCycle implements ApplicationContextAware {
    
    private static final Logger LOG = LoggerFactory.getLogger(SchedulerInitializer.class);
    
    private final IJobManager jobManager = new JobManager();
    
    private final ISchedulerManager schedulerManager = new SchedulerManager();
    
    private final Map<String, JobScheduler> schedulers = new ConcurrentHashMap<String, JobScheduler>();
    
    private final ScheduledExecutorService heartBeatScheduler = Executors.newSingleThreadScheduledExecutor();
    
    public SchedulerInitializer() {
        /** 加载任务*/
        doStart();
    }
    
    private class HeartBeatJob implements Runnable {
        @Override
        public void run() {
            try {
                refreshSchedulers();
            } catch (Exception ex) {
                LOG.error("Refresh schedulers error.", ex);
            }
        }
    }
    
    @Override
    public void doStop() {
        for (JobScheduler scheduler : schedulers.values()) {
            scheduler.stop();
        }
    }
    
    @Override
    public void doStart() {
        heartBeatScheduler.scheduleAtFixedRate(new HeartBeatJob(), 0, 2, TimeUnit.SECONDS);
    }
    
    private void refreshSchedulers() {
        List<Job> jobs = jobManager.loadAllJobs();
        
        LOG.debug("There are {} jobs currently available.", jobs.size());
        
        for (Job job : jobs) {
            JobScheduler scheduler = schedulers.get(job.getJobCode());
            if (null == scheduler) {
                scheduler = new JobScheduler(job, jobManager, schedulerManager);
                schedulers.put(job.getJobCode(), scheduler);
                scheduler.start();
            }
            
            //移除失效scheduler
            if (scheduler.isExpired()) {
                scheduler.signalStop();
                schedulers.remove(job.getJobCode());
            }
            
            //刷新job信息
            scheduler.refreshJob(job);
        }
    }
    
    @Override
    public void setApplicationContext(ApplicationContext applicationContext)
            throws BeansException {
        ContextUtils.setApplicationContext(applicationContext);
    }
}