CREATE TABLE `job` (
  `job_code` varchar(25) NOT NULL,
  `handler_bean` varchar(50) NOT NULL,
  `cron_express` varchar(20) NOT NULL,
  `heartbeat_rate` int(11) unsigned zerofill NOT NULL,
  `judge_dead_interval` int(11) unsigned zerofill NOT NULL,
  `thread_num` tinyint(5) unsigned zerofill NOT NULL,
  `fetch_num` int(11) unsigned zerofill NOT NULL,
  `job_shards` varchar(128) NOT NULL,
  `job_parameter` varchar(128) DEFAULT NULL,
  PRIMARY KEY (`job_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `job_shard` (
  `job_shard_code` varchar(25) NOT NULL,
  `job_code` varchar(25) NOT NULL,
  `cur_server` int(11) DEFAULT NULL,
  `req_server` int(11) DEFAULT NULL,
  `cur_version` int(11) unsigned DEFAULT '0',
  `req_version` int(11) DEFAULT '0',
  `next_schedule_time` bigint(22) unsigned DEFAULT NULL,
  PRIMARY KEY (`job_shard_code`),
  KEY `idx_shard_jobcode` (`job_code`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `scheduler_info` (
  `id` int(9) unsigned NOT NULL AUTO_INCREMENT,
  `uuid` varchar(32) NOT NULL,
  `job_code` varchar(25) NOT NULL,
  `last_heartbeat_time` bigint(22) DEFAULT NULL,
  `register` varchar(5) NOT NULL,
  `status` varchar(10) DEFAULT NULL,
  `ip` varchar(25) DEFAULT NULL,
  `hostname` varchar(25) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_scheduler_jobcode` (`job_code`) USING BTREE,
  KEY `idx-scheduler_uuid` (`uuid`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;
