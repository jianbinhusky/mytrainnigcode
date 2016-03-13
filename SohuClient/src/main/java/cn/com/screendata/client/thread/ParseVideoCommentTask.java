package cn.com.screendata.client.thread;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutorCompletionService;

import org.apache.log4j.Logger;

import cn.com.screendata.client.bean.CommentPageCountVO;
import cn.com.screendata.client.bean.UrlBean;
import cn.com.screendata.client.bean.VideoCommentUserInfoVO;
import cn.com.screendata.client.controller.parser.ParseVideoInfoProperty;
import cn.com.screendata.client.controller.util.HttpRequestUtil;

public class ParseVideoCommentTask implements Callable<List<VideoCommentUserInfoVO>> {
	private final static Logger log = Logger.getLogger(ParseVideoCommentTask.class);
	private UrlBean urlBean;
	
	public ParseVideoCommentTask(UrlBean urlBean){
		this.urlBean = urlBean;
	}

	@Override
	public List<VideoCommentUserInfoVO> call() throws Exception {
		List<VideoCommentUserInfoVO> videoCommentUserInfoVOList = new ArrayList<VideoCommentUserInfoVO>();
		//第一次请求第一页，可以获取总页数
		int pageNum = 1;
		String requestUrlStr = ParseVideoInfoProperty.getRequestUrlStrForComment(urlBean.getTopId(), pageNum);
		String responseJonStr = HttpRequestUtil.httpGetRequestForComment(requestUrlStr, null);
		CommentPageCountVO commentPageCountVOFirst = ParseVideoInfoProperty.parseOnePageCommentUserInfo(responseJonStr,urlBean.getVideoId());
		VideoCommentUserInfoVO videoCommentUserInfoVO = commentPageCountVOFirst.getVideoCommentUserInfoVO();
		videoCommentUserInfoVOList.add(videoCommentUserInfoVO);
		int pageCount = commentPageCountVOFirst.getPageCount();
		
		CompletionService<CommentPageCountVO> parseVideoCommentUserInfoThread 
		= new ExecutorCompletionService<CommentPageCountVO>(ParseThreadPool.parseUrlBeanListThreadPool);
		
		for(int i=2;i<=pageCount;i++){
			ParseCommentUserInfoTask parseCommentUserInfoTask = new ParseCommentUserInfoTask(urlBean.getTopId(),urlBean.getVideoId(), i);
			parseVideoCommentUserInfoThread.submit(parseCommentUserInfoTask);
		}
		for(int j=2;j<=pageCount;j++){
			try {
				CommentPageCountVO commentPageCountVO = parseVideoCommentUserInfoThread.take().get();
				videoCommentUserInfoVOList.add(commentPageCountVO.getVideoCommentUserInfoVO());
			} catch (Exception e) {
				log.error("线程回收异常",e);
			}
		}
		
		return videoCommentUserInfoVOList;
	}

}
