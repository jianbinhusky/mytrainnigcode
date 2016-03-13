package cn.com.screendata.client.thread;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ParseThreadPool {
	public static ExecutorService parseVideoInfoThreadPool = Executors.newFixedThreadPool(40);
	public static ExecutorService parseVideoPropertyThreadPool = Executors.newFixedThreadPool(40);
	public static ExecutorService parseUrlBeanListThreadPool = Executors.newFixedThreadPool(30);
	public static ExecutorService parseVideoCommentThreadPool = Executors.newFixedThreadPool(50);
	public static ExecutorService parseVideoCollectListThreadPool = Executors.newFixedThreadPool(50);
	public static ExecutorService parseVideoCollectListThreadPool2 = Executors.newFixedThreadPool(50);
	public static ExecutorService crawlerPool = Executors.newFixedThreadPool(20);
}
