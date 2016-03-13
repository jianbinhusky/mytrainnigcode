package cn.com.screendata.client.thread;

import java.util.concurrent.Callable;

import org.apache.log4j.Logger;

import cn.com.screendata.client.bean.CommentPageCountVO;
import cn.com.screendata.client.controller.parser.ParseVideoInfoProperty;
import cn.com.screendata.client.controller.util.HttpRequestUtil;

public class ParseCommentUserInfoTask implements Callable<CommentPageCountVO>{
	private final static Logger log = Logger.getLogger(ParseCommentUserInfoTask.class);
	private String topicId;
	private String videoId;
	private int pageNum;
	
	public ParseCommentUserInfoTask(String topicId,String videoId,int pageNum){
		this.topicId = topicId;
		this.videoId = videoId;
		this.pageNum = pageNum;
	}
	
	@Override
	public CommentPageCountVO call() throws Exception {
		String requestUrlStr = ParseVideoInfoProperty.getRequestUrlStrForComment(topicId, pageNum);
		String reponseJsonStr = null;
		CommentPageCountVO commentPageCountVO = null;
		try {
			reponseJsonStr = HttpRequestUtil.httpGetRequestForComment(requestUrlStr, null);
		    commentPageCountVO = ParseVideoInfoProperty.parseOnePageCommentUserInfo(reponseJsonStr,videoId);
		} catch (Exception e) {
			log.error("请求返回异常",e);
		}
		
		return commentPageCountVO;
	}

}
