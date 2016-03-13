package cn.com.screendata.client.thread;

import java.util.concurrent.Callable;

import cn.com.screendata.client.bean.UrlBean;
import cn.com.screendata.client.bean.VideoInfo;
import cn.com.screendata.client.controller.parser.ParseVideoInfoProperty;


public class ParseVideoInfoTask implements Callable<VideoInfo> {
	private UrlBean urlBean;
	
	public ParseVideoInfoTask(UrlBean urlBean){
		this.urlBean = urlBean;
	}

	@Override
	public VideoInfo call() throws Exception {
		VideoInfo videoInfo = ParseVideoInfoProperty.getVideoInfo(urlBean);
		
		return videoInfo;
	}

}
