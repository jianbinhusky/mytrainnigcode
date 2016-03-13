package cn.com.screendata.client.cache;

import java.util.LinkedList;

import cn.com.screendata.client.bean.HttpRequestInfo;

public class HttpInfoQueue {
	
	private static LinkedList<HttpRequestInfo> httpInfoQueue = new LinkedList<HttpRequestInfo>();
	
	public static void addInfoAtEnd(HttpRequestInfo info){
		httpInfoQueue.addLast(info);
	}
	
	public static void addInfoAtFirst(HttpRequestInfo info){
		httpInfoQueue.addFirst(info);
	}
	
	public static HttpRequestInfo getInfo(){
		return httpInfoQueue.poll();
	}

}
