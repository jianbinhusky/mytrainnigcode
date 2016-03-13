package cn.com.screendata.client.thread;

import java.util.concurrent.Callable;
import cn.com.screendata.client.bean.CollectListVO;
import cn.com.screendata.client.controller.parser.ParseCollectList;
import cn.com.screendata.client.controller.util.HttpRequestUtil;

public class ParseVideoCollectListTask implements Callable<CollectListVO> {
	private String collectType;
	private String requestUrlStr;
	
	public ParseVideoCollectListTask(String collectType,String requestUrlStr){
		this.collectType = collectType;
		this.requestUrlStr = requestUrlStr;
	}

	@Override
	public CollectListVO call() throws Exception {
		String responseHtmlStr = HttpRequestUtil.httpGetRequestForComment(requestUrlStr, null);
		CollectListVO collectListVO = ParseCollectList.parseOnePageCollectList(responseHtmlStr,collectType);
		
		return collectListVO;
	}

}
