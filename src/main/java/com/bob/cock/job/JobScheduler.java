package com.bob.cock.job;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bob.cock.job.executor.IJobProcessor;
import com.bob.cock.job.executor.JobProcessor;
import com.bob.cock.job.executor.JobShardCommand;
import com.bob.cock.job.manager.IJobManager;
import com.bob.cock.job.manager.ISchedulerManager;
import com.bob.cock.job.utils.CollectionUtils;
import com.bob.cock.job.utils.DateUtils;
import com.bob.cock.job.utils.ScheduleUtils;

public class JobScheduler {
    
	private static final Logger LOG = LoggerFactory.getLogger(JobScheduler.class);
	
	private static final int STOP_SCHEDULE_SERVER_TIMEOUT = 60;
	
	private static final int SCHEDULE_PROCESSORS = 2;
	
	private volatile Job job;
	
	private final IJobManager jobManager;
	
	private final ISchedulerManager schedulerManager;
	
	private final SchedulerInfo schedulerInfo;
	
	private final ScheduledExecutorService schedule = Executors.newScheduledThreadPool(SCHEDULE_PROCESSORS);
	
	private final List<JobShard> assignedJobShards = Collections.synchronizedList(new ArrayList<JobShard>(0)); 
	
	private IJobProcessor processor;
	
	private volatile Long timeDelta;
	
	public JobScheduler(Job job, IJobManager jobManager, ISchedulerManager schedulerManager) {
	    this.job = job;
		this.jobManager = jobManager;
		this.schedulerManager = schedulerManager;
		this.schedulerInfo = new SchedulerInfo(null, job.getJobCode(), Register.YES.getCode(), this.getSystemTime(), Status.IDLE.getCode());
	}
	
	public void start() {
        //注册
        register(this.schedulerInfo);
        
        //注册心跳检查
        schedule.scheduleAtFixedRate(new HeartBeatTask(this), 0, job.getHeartbeatRate(), TimeUnit.SECONDS);
        
        //开始调度
        startSchedule();
	}
	
	public void stop() {
        //关闭调度器
        try {
            this.schedule.shutdown();
            if (this.schedule.awaitTermination(STOP_SCHEDULE_SERVER_TIMEOUT, TimeUnit.SECONDS)) {
                this.schedule.shutdownNow();
            }
        
            this.schedule.awaitTermination(STOP_SCHEDULE_SERVER_TIMEOUT, TimeUnit.SECONDS);
        } catch (Exception ignore) {
            LOG.error("Stop scheduler[id={}] error.", this.schedulerInfo.getId(), ignore);
        }
        
        //关闭执行器
        this.processor.stopQuietly();
    }
    
    public void signalStop() {
        this.schedule.shutdown();
        this.processor.signalStop();
    }
    
    public boolean isExpired() {
        return getSystemTime() - schedulerInfo.getLastHeartbeatTime() > job.getJudgeDeadInterval() * 1000;
    }
    
    public void refreshJob(final Job job) {
        this.job = job;
    }
	
	private void register(SchedulerInfo schedulerInfo) {
		schedulerManager.createSchedulerInfo(schedulerInfo);
		schedulerInfo.setRegister(Register.YES.getCode());
	}
	
	private void startSchedule() {
	    //有数据任务分配时，开始实际调度
	    while (getScheduledJobShardList().size() <= 0 ) {
	        try {
                Thread.sleep(500);
            } catch (InterruptedException ignore) {
            }
	    }
	    
		//计算第一次执行时间
		CronExpression cron = null;
		try {
	        cron = new CronExpression(job.getCronExpress());
        } catch (ParseException ex) {
            LOG.error("Cronexpress[{}] error, pls recheck it.", job.getCronExpress(), ex);
        	return ;
        }
		
		Date firstScheduleTime = cron.getNextValidTimeAfter(new Date(this.getSystemTime()));
		this.schedule.schedule(new ComputeAndStartWork(firstScheduleTime), firstScheduleTime.getTime() - this.getSystemTime(), TimeUnit.MILLISECONDS);
		
		LOG.debug("First trigger job[jobcode={}] process, first schedule time: {}.", job.getJobCode(), DateUtils.format(firstScheduleTime));
	}
	
	private void compute(final Date currentScheduleTime) {
		if (null == processor) {
		    processor = new JobProcessor(jobManager, job.getThreadNum());
		}
		
		//提交实际执行任务前先修改任务版本，用于在任务提交时机器挂掉掉后，进行一次补偿
        for (JobShard jobShard : assignedJobShards) {
            jobManager.refreshJobShardReqVersionAndNextScheduleTime(jobShard.getJobShardCode(), currentScheduleTime.getTime());
            processor.submitJob(JobShardCommand.adapt(job, jobShard));
        }
		
		// 计算下一次执行时间 
		CronExpression cron = null;
		try {
	        cron = new CronExpression(job.getCronExpress());
        } catch (ParseException ex) {
            LOG.error("Cronexpress[{}] error, pls check it.", job.getCronExpress(), ex);
        	return ;
        }
		
        Date nextScheduleTime = cron.getNextValidTimeAfter(currentScheduleTime);
		schedule.schedule(new ComputeAndStartWork(nextScheduleTime), nextScheduleTime.getTime() - this.getSystemTime(), TimeUnit.MILLISECONDS);
		
		LOG.debug("Next trigger job[jobcode={}] process, next schedule time: {}.", job.getJobCode(), DateUtils.format(nextScheduleTime));
	}
	
	private class ComputeAndStartWork implements Runnable {
	    
	    private final Date currentScheduleTime;
	    
	    public ComputeAndStartWork(Date currentScheduleTime) {
	        this.currentScheduleTime = currentScheduleTime;
	    }
	    
	    @Override
	    public void run() {
	        try {
	            compute(currentScheduleTime);
	        } catch (Exception ex) {
	            LOG.error("Scheduler[id={}] processor initial error.", JobScheduler.this.schedulerInfo.getId(), ex);
	        }
	    }
	}

	private class HeartBeatTask implements Runnable {
		private final JobScheduler scheduler;
		
		public HeartBeatTask(JobScheduler scheduler) {
			this.scheduler = scheduler;
		}
		
	    public void run() {
	    	try {
	            scheduler.refreshScheduler();
            } catch (Exception ex) {
            	LOG.error("Refresh scheduler[id={}] error.", JobScheduler.this.schedulerInfo.getId(), ex);
            }
	    }
    }
	
	private void refreshScheduler() {
	    /**
	     * 由于某些原因，比如网络抖动，导致心跳更新及时持久化到数据库，从而出现数据库中的任务调度器信息被过期清除，
	     * 此时不再处理内存中的心跳更新，等待调度器管理者对调度器进行清理。
	     */
	    if (!isSchedulerPersistenceExpired()) {
	        return ;
	    }
	    
		//更新心跳信息
    	updateSchedulerHeartbeatAndStatus();
    	
		//清理超时调度器
    	reClearExpiredScheduler();
    	
    	//释放分配后，调度器dead的Job Shard
    	releaseJobShardOutOfControl();
    	
    	//重新分配任务项
    	reAssignJobShard();
    	
    	//加载任务项列表
    	getScheduledJobShardList();
	}
	
	private boolean isSchedulerPersistenceExpired() {
	    return null != schedulerManager.loadSchedulerInfo(this.schedulerInfo.getId());
	}
	
	private void releaseJobShardOutOfControl() {
		List<Integer> schedulers = schedulerManager.loadAllSchedulerIds(job.getJobCode());
		List<JobShard> jobShards = jobManager.loadAllJobShard(job.getJobCode());
		if (CollectionUtils.isEmpty(jobShards)) {
			return ;
		}
		
		for (JobShard jobShard : jobShards) {
			if (!schedulers.contains(jobShard.getCurServer())) {
			    jobManager.updateJobShardCurAndReqServer(jobShard.getJobShardCode(), null, null);
			}
		}
	}
	
	private void releaseJobShardReqByOther() {
		List<JobShard> jobShards = jobManager.loadAssignedJobShards(job.getJobCode(), schedulerInfo.getId());
		for (JobShard jobShard : jobShards) {
			if (null != jobShard.getReqServer()) {
			    jobManager.updateJobShardCurAndReqServer(jobShard.getJobShardCode(), jobShard.getReqServer(), null);
			}
		}
	}
	
	private List<JobShard> getScheduledJobShardList() {
	    if (null == processor) {
	        //释放被申请的item
            releaseJobShardReqByOther();
            
            //此时说明已经不是第一次进行数据处理，后面若发现有任务重新分配，直接进行一次处理
            List<JobShard> assignedNow = jobManager.loadAssignedJobShards(job.getJobCode(), schedulerInfo.getId());
            assignedJobShards.clear();
            assignedJobShards.addAll(assignedNow);
	    } else if(processor.isIdle()) {
	        //释放被申请的item
		    releaseJobShardReqByOther();

		    //此时说明已经不是第一次进行数据处理，后面若发现有任务重新分配，直接进行一次处理
		    List<JobShard> assignedNow = jobManager.loadAssignedJobShards(job.getJobCode(), schedulerInfo.getId());
		    List<JobShard> assignedCopy = new ArrayList<JobShard>(assignedNow);
		    List<JobShard> increament = Collections.emptyList();
		    if (assignedNow.removeAll(assignedJobShards)) {
		        increament = assignedNow;
		        for (JobShard shard : increament) {
	                if (shard.getCurVersion() < shard.getReqVersion()) {
	                    processor.submitJob(JobShardCommand.adapt(job, shard));

	                    LOG.debug("Scheduler[id={}] make up for job shard[jobShadCod={}].", this.schedulerInfo.getId(), shard.getJobShardCode());
	                }
	            }
		    }
		    
	        //重新加载任务分片项
		    assignedJobShards.clear();
			assignedJobShards.addAll(assignedCopy);
		}
		
		return assignedJobShards;
	}
	
	private boolean isLeader(Integer curScheduler, List<Integer> schedulers) {
		if (schedulers == null || schedulers.size() == 0) {
			return false;
		}
		
		return curScheduler.equals(schedulers.get(0));
	}
	
	private void reClearExpiredScheduler() { 
		List<SchedulerInfo> schedulers = schedulerManager.loadAllSchedulers(job.getJobCode());
		if (schedulers == null || schedulers.size() == 0) {
			return ;
		}
		
		for (SchedulerInfo scheduler : schedulers) {
			if (getSystemTime() - scheduler.getLastHeartbeatTime() > job.getJudgeDeadInterval() * 1000) {
				try {
				    schedulerManager.deleteSchedulerInfo(scheduler.getId());
	                LOG.debug("Scheduler[id={}] was cleaned.", scheduler.getId());
                } catch (Exception ignore) {
                	//存在多台调度器的时 候，会有并发清理的问题，此处忽略异常
                }
			}
		}
	}
	
	private void reAssignJobShard() {
		//非leader，不能进行任务分配
		List<Integer> schedulers = schedulerManager.loadAllSchedulerIds(job.getJobCode());
		if (!isLeader(schedulerInfo.getId(), schedulers)) {
			return ;
		}
		
		LOG.debug("Lead-scheduler[id={}] is assiging job shards.", schedulerInfo.getId());
		
		List<JobShard> children = jobManager.loadAllJobShard(job.getJobCode());

		//保存分配结果
		int[] taskNums = ScheduleUtils.assignTaskNumber(schedulers.size(), children.size());
		int point = 0;
		int count = 0;
		Integer NO_SERVER_DEAL = -20000;
		int unModifyCount = 0;
		for (int i=0;i < children.size();i++) {
			String jobShardCode = children.get(i).getJobShardCode();
			if (point < schedulers.size() && i >= count + taskNums[point]) {
				count = count + taskNums[point];
				point = point + 1;
			}
			Integer serverId = NO_SERVER_DEAL;
			if (point < schedulers.size() ) {
			    serverId = schedulers.get(point);
			}
			
			JobShard jobShard = jobManager.loadJobShard(jobShardCode);
			if(null == jobShard.getCurServer() || jobShard.getCurServer().equals(NO_SERVER_DEAL)){
			    jobManager.updateJobShardCurServer(jobShard.getJobShardCode(), serverId);
			} else if (jobShard.getCurServer().equals(serverId) && null != jobShard.getReqServer()) {
				unModifyCount ++;
			} else {
                jobManager.updateJobShardReqServer(jobShard.getJobShardCode(), serverId);
			}
		}
		
		//标识需要重新进行任务加载
        if (unModifyCount < schedulers.size()) {
            //TODO 重新加载任务
        }
	}
	
	private void updateSchedulerHeartbeatAndStatus() {
		long systemTime = getSystemTime();
		
		//更新scheduler运行时
		schedulerInfo.setLastHeartbeatTime(systemTime);
		schedulerInfo.setStatus((null != processor && processor.isActive()) ? Status.ACTIVE.getCode() : Status.IDLE.getCode());
		
		//更新db心跳时间
		schedulerManager.updateSchedulerInfoLastHeartbeatTimeAndStatus(systemTime, schedulerInfo.getStatus(), schedulerInfo.getId());
	}
	
	/**
	 * 获取系统统一时间，以数据库为准
	 * 
	 * @return
	 */
	private long getSystemTime() {
	    if (null == timeDelta) {
	        timeDelta = schedulerManager.getCurTimestrap() - System.currentTimeMillis();
	    }
	    
		return System.currentTimeMillis() + timeDelta;
	}
}

enum Register {
    YES("YES"), NO("NO");
    
    private String code;

    private Register(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}

enum Status {
    
    ACTIVE("ACTIVE"),
    IDLE("IDLE");
    
    private String code;

    private Status(String code) {
        this.code = code;
    }
    
    public String getCode() {
        return code;
    }
}
