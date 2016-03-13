package cn.com.screendata.client.bean;

import lombok.Getter;
import lombok.Setter;

public class TaskJsonBean {
	
	/**
	 * 需要爬取的url
	 */
	@Getter
	@Setter
	private String url;
	/**
	 * 视频类型：电视剧、电影、综艺
	 */
	@Getter
	@Setter
	private String type;
	/**
	 * 网站类型：0 优酷，1 爱奇艺，2  乐视网
	 */
	@Getter
	@Setter
	private int site;
	/**
	 * 任务类型 :0只抓取剧集信息，1抓取更加详细的信息
	 */
	@Setter
	@Getter
	private int taskType;
}
