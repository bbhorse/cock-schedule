package com.bob.cock.job.data.jdbc;

import java.sql.Types;
import java.util.List;

import com.bob.cock.job.JobShard;

public class JobShardDao extends BaseDao<JobShard> {

    private static final String INSERT                                      = "insert into job_shard(job_shard_code, job_code, cur_server, req_server) values (?, ?, ?, ?)";
    private static final String FIND_BY_JOBCODE                             = "select * from job_shard where job_code = ?";
    private static final String FIND_BY_JOBCODE_AND_CURSERVER               = "select * from job_shard where job_code = ? and cur_server = ?";
    private static final String FINAONE                                     = "select * from job_shard where job_shard_code = ?";
    private static final String UPDATE_CUR_AND_REQ_SERVER                   = "update job_shard set cur_server = ?, req_server = ? where job_shard_code = ?";
    private static final String UPDATE_CUR_SERVER                           = "update job_shard set cur_server = ? where job_shard_code = ?";
    private static final String UPDATE_REQ_SERVER                           = "update job_shard set req_server = ? where job_shard_code = ?";
    private static final String UPDATE_REQ_VERSION_AND_NEXT_SCHEDULE_TIME   = "update job_shard set req_version = req_version + 1, next_schedule_time = ? where job_shard_code = ?";
    private static final String UPDATE_CUR_VERSION_TO_PLAN_VERSION          = "update job_shard set cur_version = req_version where job_shard_code = ?";
    
    public int createJobShard(JobShard jobShard) {
        return this.save(INSERT, 
                new Object[] {jobShard.getJobShardCode(), jobShard.getJobCode(), jobShard.getCurServer(), jobShard.getReqServer()}, 
                new int[] {Types.VARCHAR, Types.VARCHAR, Types.INTEGER, Types.INTEGER});
    }
    
    public int updateJobShardCurAndReqServer(String jobShardCode, Integer curServer, Integer reqServer) {
        return this.update(UPDATE_CUR_AND_REQ_SERVER, 
                new Object[] {curServer, reqServer, jobShardCode},
                new int[] {Types.INTEGER, Types.INTEGER, Types.VARCHAR});
    }
    
    public int updateJobShardCurServer(String jobShardCode, Integer curServer) {
        return this.update(UPDATE_CUR_SERVER, 
                new Object[] {curServer, jobShardCode},
                new int[] {Types.INTEGER, Types.VARCHAR});
    }
    
    public int updateJobShardReqServer(String jobShardCode, Integer reqServer) {
        return this.update(UPDATE_REQ_SERVER, 
                new Object[] {reqServer, jobShardCode},
                new int[] {Types.INTEGER, Types.VARCHAR});
    }
    
    public int updateJobShardReqVersionAndNextScheduleTime(String jobShardCode, long nextScheduleTime) {
        return this.update(UPDATE_REQ_VERSION_AND_NEXT_SCHEDULE_TIME, 
                new Object[] {nextScheduleTime, jobShardCode},
                new int[] {Types.BIGINT, Types.VARCHAR});
    }
    
    public int updateJobShardCurVersionToPlanVersion(String jobShardCode) {
        return this.update(UPDATE_CUR_VERSION_TO_PLAN_VERSION, 
                new Object[] {jobShardCode},
                new int[] {Types.VARCHAR});
    }
    
    public List<JobShard> findByJobCode(String jobCode) {
        return this.queryList(JobShard.class, FIND_BY_JOBCODE, jobCode);
    }
    
    public List<JobShard> finaByJobCodeAndCurServer(String jobCode, Integer curServer) {
        return this.queryList(JobShard.class, FIND_BY_JOBCODE_AND_CURSERVER, jobCode, curServer);
    }
    
    public JobShard findOne(String jobShardCode) {
        return this.queryObject(JobShard.class, FINAONE, jobShardCode);
    }
}
