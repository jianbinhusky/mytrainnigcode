package cn.com.screendata.client.thread;

import java.util.concurrent.Callable;

import cn.com.screendata.client.bean.UrlBean;
import cn.com.screendata.client.bean.VideoProperty;
import cn.com.screendata.client.controller.parser.ParseVideoInfoProperty;


public class ParseVideoPropertyTask implements Callable<VideoProperty> {
	private UrlBean urlBean;
	
	public ParseVideoPropertyTask(UrlBean urlBean){
		this.urlBean = urlBean;
	}

	@Override
	public VideoProperty call() throws Exception {
		VideoProperty videoProperty = ParseVideoInfoProperty.getVideoProperty(urlBean);
		
		return videoProperty;
	}

}
