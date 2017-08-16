package com.bob.cock.job.utils;

import java.net.InetAddress;
import java.net.ServerSocket;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public final class ScheduleUtils {
	
    public static String getLocalHostName() {
		try {
			return InetAddress.getLocalHost().getHostName();
		} catch (Exception e) {
			return "";
		}
	}

	public static int getFreeSocketPort() {
		try {
			ServerSocket ss = new ServerSocket(0);
			int freePort = ss.getLocalPort();
			ss.close();
			return freePort;
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	public static String getLocalIP() {
		try {
			return InetAddress.getLocalHost().getHostAddress();
		} catch (Exception e) {
			return "";
		}
	}

	public static String transferDataToString(Date d) {
		SimpleDateFormat DATA_FORMAT_yyyyMMddHHmmss = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return DATA_FORMAT_yyyyMMddHHmmss.format(d);
	}
	
	public static Date transferStringToDate(String d) throws ParseException {
		SimpleDateFormat DATA_FORMAT_yyyyMMddHHmmss = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return DATA_FORMAT_yyyyMMddHHmmss.parse(d);
	}
	
	public static Date transferStringToDate(String d,String formate) throws ParseException {
		SimpleDateFormat FORMAT = new SimpleDateFormat(formate);
        return FORMAT.parse(d);
	}
	
	public static String splitBaseTaskTypeFromTaskType(String taskType) {
	    if(taskType.indexOf("$") >=0){
	        return taskType.substring(0,taskType.indexOf("$"));
	    }else{
	        return taskType;
	    }
	}
	
	/**
	 * 分配任务数量
	 * @param serverNum 总的服务器数量
	 * @param taskItemNum 任务项数量
	 * @param maxNumOfOneServer 每个server最大任务项数目
	 * @param maxNum 总的任务数量
	 * @return
	 */
	public static int[] assignTaskNumber(int serverNum, int taskItemNum) {
		int[] taskNums = new int[serverNum];
		int numOfSingle = taskItemNum / serverNum;
		int otherNum = taskItemNum % serverNum;
		//20150323 删除, 任务分片保证分配到所有的线程组数上。 开始
//		if (maxNumOfOneServer >0 && numOfSingle >= maxNumOfOneServer) {
//			numOfSingle = maxNumOfOneServer;
//			otherNum = 0;
//		}
		//20150323 删除, 任务分片保证分配到所有的线程组数上。 结束
		for (int i = 0; i < taskNums.length; i++) {
			if (i < otherNum) {
				taskNums[i] = numOfSingle + 1;
			} else {
				taskNums[i] = numOfSingle;
			}
		}
		return taskNums;
	}
	
	private static String printArray(int[] items) {
		String s="";
		for(int i=0;i<items.length;i++){
			if(i >0){s = s +",";}
			s = s + items[i];
		}
		return s;
	}
	
	private ScheduleUtils() {
	}
	
	public static void main(String[] args) {
		System.out.println(printArray(assignTaskNumber(1,10)));
		System.out.println(printArray(assignTaskNumber(2,10)));
		System.out.println(printArray(assignTaskNumber(3,10)));
		System.out.println(printArray(assignTaskNumber(4,10)));
		System.out.println(printArray(assignTaskNumber(5,10)));
		System.out.println(printArray(assignTaskNumber(6,10)));
		System.out.println(printArray(assignTaskNumber(7,10)));
		System.out.println(printArray(assignTaskNumber(8,10)));		
		System.out.println(printArray(assignTaskNumber(9,10)));
		System.out.println(printArray(assignTaskNumber(10,10)));
		
		System.out.println("-----------------");
		
		System.out.println(printArray(assignTaskNumber(1,10)));
		System.out.println(printArray(assignTaskNumber(2,10)));
		System.out.println(printArray(assignTaskNumber(3,10)));
		System.out.println(printArray(assignTaskNumber(4,10)));
		System.out.println(printArray(assignTaskNumber(5,10)));
		System.out.println(printArray(assignTaskNumber(6,10)));
		System.out.println(printArray(assignTaskNumber(7,10)));
		System.out.println(printArray(assignTaskNumber(8,10)));		
		System.out.println(printArray(assignTaskNumber(9,10)));
		System.out.println(printArray(assignTaskNumber(10,10)));
		
	}
}