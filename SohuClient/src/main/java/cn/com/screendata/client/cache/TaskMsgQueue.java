package cn.com.screendata.client.cache;

import java.util.LinkedList;

import cn.com.screendata.client.bean.TaskJsonBean;

public class TaskMsgQueue {

	private static LinkedList<TaskJsonBean> taskMsgQueue = new LinkedList<TaskJsonBean>();
	
	public static void addTask(TaskJsonBean task){
		taskMsgQueue.addLast(task);
	}
	
	public static void addTaskAtHead(TaskJsonBean task){
		taskMsgQueue.addFirst(task);
	}
	
	public static TaskJsonBean getTask(){
		return taskMsgQueue.poll();
	}
	
}
