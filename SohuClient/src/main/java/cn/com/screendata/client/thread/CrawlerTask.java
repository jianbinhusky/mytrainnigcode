package cn.com.screendata.client.thread;

import java.util.concurrent.Callable;

import cn.com.screendata.client.bean.UrlBean;
import cn.com.screendata.client.controller.util.HttpRequestUtil;

public class CrawlerTask implements Callable<UrlBean>{
	
	private UrlBean urlBean;
	
	public CrawlerTask(UrlBean urlBean){
		this.urlBean = urlBean;
	}

	@Override
	public UrlBean call() throws Exception {
		
		String url = urlBean.getUrl();
		
		String html = HttpRequestUtil.httpGetRequest(url, null);
		
		urlBean.setHtml(html);
		
		return urlBean;
	}

}
