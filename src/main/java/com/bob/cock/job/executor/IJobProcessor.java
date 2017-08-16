package com.bob.cock.job.executor;

public interface IJobProcessor {
    
    void submitJob(Runnable job);
	
	boolean isIdle();
	
	boolean isActive();
	
	void stopQuietly();
	
	void signalStop();
}
