package cn.com.screendata.client.controller.parser;
/**
 * @author hujb
 */

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutorCompletionService;

import org.apache.log4j.Logger;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import cn.com.screendata.client.bean.CollectPlayInfo;
import cn.com.screendata.client.bean.CollectProperty;
import cn.com.screendata.client.bean.Comment;
import cn.com.screendata.client.bean.CommentPageCountVO;
import cn.com.screendata.client.bean.UrlBean;
import cn.com.screendata.client.bean.UrlBeanExt;
import cn.com.screendata.client.bean.UserInfo;
import cn.com.screendata.client.bean.VideoCollectVO;
import cn.com.screendata.client.bean.VideoCommentUserInfoVO;
import cn.com.screendata.client.bean.VideoInfo;
import cn.com.screendata.client.bean.VideoInfoPropertyVO;
import cn.com.screendata.client.bean.VideoProperty;
import cn.com.screendata.client.bean.VideoType;

import cn.com.screendata.client.controller.util.HttpRequestUtil;
import cn.com.screendata.client.thread.ParseThreadPool;
import cn.com.screendata.client.thread.ParseVideoCommentTask;
import cn.com.screendata.client.thread.ParseVideoInfoTask;
import cn.com.screendata.client.thread.ParseVideoPropertyTask;
import cn.com.screendata.common.util.DateUtil;

/**
 * 爬取解析VideoInfo & VideoProperty
 * @author hujb
 *
 */
public class ParseVideoInfoProperty {
	private final static Logger log = Logger.getLogger(ParseVideoInfoProperty.class);
	public final static String UP_DOWN_MOV_BASE_URL = "http://score.my.tv.sohu.com/digg/get.do?type=1&vid=";
	public final static String UP_DOWN_TV_BASE_URL = "http://score.my.tv.sohu.com/digg/get.do?&type=2&vid=";
	public final static String UP_DOWN_ZY_BASE_URL = "http://score.my.tv.sohu.com/digg/get.do?&type=7&vid=";
	public final static String COUNT_BASE_URL = "http://count.vrs.sohu.com/count/stat.do?";
	public final static String COUNT_VIDEOID_CONNECTOR = "videoId=";
	public final static String COUNT_TVID_CONNECTOR = "&tvid=";
	public final static int COUNT_TVID = 1;
	public final static String COUNT_PLAYLISTID_CONNECTOR = "&playlistId=";
	public final static String COUNT_OTHER_CONNECTOR = "&plat=flash&type=vrs&t&os=Windows8&online=0&catecode&categoryId=1";
	public final static String COMMENT_BASE_URL = "http://changyan.sohu.com/api/services/comment/list?topic_id=";
	public final static String COMMENT_TOPIC_BASE_URL = "http://changyan.sohu.com/api/services/topic/load?client_id=cyqyBluaj&outer_page_no=1&topic_source_id=";
	public final static String COMMENT_PAGE_NUM_CONNECTOR = "&outer_page_no=";
	public final static String ZS_AREA_BASE_URL = "http://index.tv.sohu.com/index/tvs-area/";
	public final static String ZS_SEX_BASE_URL = "http://index.tv.sohu.com/index/tvs-sex/";
	public final static String ZS_XML_EXTENTION = ".xml";
	public final static int COMMENT_PAGE_SIZE = 30;
	public final static int SITE_TYPE_SOHU = 5;
	
	/**获取聚集所有内容
	 * @param urlBeanExt
	 * @return VideoCollectVO
	 */
	public VideoCollectVO getVideoInfoProperty(UrlBeanExt urlBeanExt){
		VideoCollectVO videoCollectVO = new VideoCollectVO();
		VideoInfoPropertyVO videoInfoPropertyVO = new VideoInfoPropertyVO();
		List<VideoInfo> videoInfoList = new ArrayList<VideoInfo>();
		List<VideoProperty> videoPropertyList = new ArrayList<VideoProperty>();
		//解析视频信息和属性 VideoInfo & VideoProperty 
		CompletionService<VideoInfo> parseVideoInfoThread 
		= new ExecutorCompletionService<VideoInfo>(ParseThreadPool.parseVideoInfoThreadPool);
		
		CompletionService<VideoProperty> parseVideoPropertyThread 
		= new ExecutorCompletionService<VideoProperty>(ParseThreadPool.parseVideoPropertyThreadPool);
		
		List<UrlBean> urlBeanList = urlBeanExt.getUrlList();
		for(int i=0;i<urlBeanList.size();i++){
			UrlBean urlBean = urlBeanList.get(i);
			ParseVideoInfoTask videoInfoTask = new ParseVideoInfoTask(urlBean);
			parseVideoInfoThread.submit(videoInfoTask);
			ParseVideoPropertyTask videoPropertyTask = new ParseVideoPropertyTask(urlBean);
			parseVideoPropertyThread.submit(videoPropertyTask);
		}
		for(int j=0;j<urlBeanList.size();j++){
			try {
				VideoInfo videoInfo  = parseVideoInfoThread.take().get();
				videoInfoList.add(videoInfo);
				VideoProperty videoProperty = parseVideoPropertyThread.take().get();
				videoPropertyList.add(videoProperty);
			} catch (Exception e) {
				log.error("线程回收异常",e);
			}
		}
		videoInfoPropertyVO.setVideoInfoList(videoInfoList);
		videoInfoPropertyVO.setVideoPropertyList(videoPropertyList);
		
		//解析指数信息
		VideoCollectVO videoCollect = ParseVideoInfoProperty.getCollectPlayInfoProperty(urlBeanExt);
		List<CollectPlayInfo> collectPlayInfoList = videoCollect.getCollectPlayInfoList();
		List<CollectProperty> collectPropertyList = videoCollect.getCollectPropertyList();
		
		videoCollectVO.setVideoInfoPropertyVO(videoInfoPropertyVO);
		videoCollectVO.setCollectPlayInfoList(collectPlayInfoList);
		videoCollectVO.setCollectPropertyList(collectPropertyList);
		
		return videoCollectVO;
	}
	
	/**
	 * 解析视频信息
	 * @param urlBean
	 * @return
	 */
	public static VideoInfo getVideoInfo(UrlBean urlBean){
		VideoInfo videoInfo = new VideoInfo();
		String htmlStr = urlBean.getHtml();
		Document videoInfoDoc = HtmlDocUtil.getHtmlDocFromStr(htmlStr);
		String videoType = urlBean.getType();
		/*解析字段*/
  		//获取视频标题,
  	    String title = videoInfoDoc.select("h2").first().html().trim();
  	    if(title == null){
  	    	return null;
  	    }

      	//获取视频栏目category
  	    String category = "";
		try {
			category = videoInfoDoc.select("div#crumbsBar > div.area > div.left > div.crumbs > a").first().html().trim();
		} catch (Exception e1) {
			category = null;
		}
		String type = "";
		String vmainactor = "";
		String vprofile = "";
		int playTime = 0;
		//如果视频类型是综艺
		if(videoType.equals(VideoType.zongyi)){
			//获取综艺视频类型标签
  	  	    try {
				Elements eleZYType  = videoInfoDoc.select("div#crumbsBar > div.area > div.left > div.crumbs");
				String allHead = eleZYType.text().replaceAll(">", "").replaceAll("/", ",");
				String last = eleZYType.select("a").last().html();
				type = allHead.replace(category, "").replace(last, "").trim();
			} catch (Exception e) {
				type = null;
			}
			//综艺类演员或者主持人
  	  	    try {
				Elements eleZYactor  = videoInfoDoc.select("div#playlist > div.info > ul.u > li");
				String actor =  eleZYactor.get(1).text();
				vmainactor= actor.replace("嘉宾：", "").replaceAll(" ", ",").trim();
			} catch (Exception e) {
				vmainactor = null;
			}
	  		//综艺类单个视频简介
	  	    try {
				vprofile = videoInfoDoc.select("div#playlist > div.info > p.intro").text();
				vprofile = vprofile.replace("展开信息", "").trim();
			} catch (Exception e) {
				vprofile = null;
			}
	  		//4月9日添加字段视频时长：playTime
			try {
				Elements elePlayTime = videoInfoDoc.select("div#playlist > div.info > ul > li");
				for (org.jsoup.nodes.Element element : elePlayTime) {
					String tempStr = element.text();
					if(tempStr.contains("时长")){
						String timeStr = tempStr.replace("时长", "");
						playTime = ParseVideoInfoProperty.getPlayTime(timeStr);
					}
				}
			} catch (Exception e) {
				playTime = (Integer) null;
			}
		}else if(videoType.equals(VideoType.dianying)){
      		//获取电影标签
  	  	    try {
				Elements eleZYType  = videoInfoDoc.select("div#crumbsBar > div.area > div.left > div.crumbs");
				String allHead = eleZYType.text().replaceAll(">", "").replaceAll("/", ",");
				String last = eleZYType.select("a").last().html();
				type = allHead.replace(category, "").replace(last, "").trim();
			} catch (Exception e) {
				type = null;
			}
			//获取电影视频类演员或者主持人
			try {
				Elements eleMovType  = videoInfoDoc.select("div#playlist > div.info > ul.u > li");
				String actor =  eleMovType.get(1).text();
				vmainactor= actor.replace("主演：", "").replaceAll(" ", ",").trim();
			} catch (Exception e) {
				vmainactor = null;
			}
	  		//电影类单个视频简介
	  		try {
				vprofile = videoInfoDoc.select("div#playlist > div.info > p.intro").text();
				vprofile = vprofile.replace("展开信息", "").trim();
			} catch (Exception e) {
				vprofile = null;
			}
	  		//4月9日添加字段视频时长：playTime
			try {
				Elements elePlayTime = videoInfoDoc.select("div#playlist > div.info > ul > li");
				for (org.jsoup.nodes.Element element : elePlayTime) {
					String tempStr = element.text();
					if(tempStr.contains("时长")){
						String timeStr = tempStr.replace("时长", "");
						playTime = ParseVideoInfoProperty.getPlayTime(timeStr);
					}
				}
			} catch (Exception e) {
				playTime = (Integer) null;
			}
		}else{
			//获取电视剧视频类型标签
  	  	    try {
				Elements eleZYType  = videoInfoDoc.select("div#crumbsBar > div.area > div.left > div.crumbs");
				String allHead = eleZYType.text().replaceAll(">", "").replaceAll("/", ",");
				String last = eleZYType.select("a").last().html();
				type = allHead.replace(category, "").replace(last, "").trim();
			} catch (Exception e) {
				type = null;
			}
			//获取电视剧视频演员或者主持人
	  	    try {
				Elements eleTvType  = videoInfoDoc.select("div.right > div.info > ul.u > li#mainactor > a");
				int flag = 0;
				for (org.jsoup.nodes.Element element : eleTvType) {
					if(flag == 0){
						vmainactor = element.html();
					}else{
						vmainactor += "," +  element.html();
					}
					flag++;
				}
			} catch (Exception e) {
				vmainactor = null;
			}
	  		//电视剧类单个视频简介
	  	    try {
				vprofile= videoInfoDoc.select("div.right > div.info > p.intro").text();
				vprofile = vprofile.replace("展开信息", "").trim();
			} catch (Exception e) {
				vprofile = null;
			}
	  		//4月9日添加字段视频时长：playTime
	  		try {
				Elements elePlayTime = videoInfoDoc.select("div.right > div.info > ul > li");
				for (org.jsoup.nodes.Element element : elePlayTime) {
					String tempStr = element.text();
					if(tempStr.contains("时长")){
						String timeStr = tempStr.replace("时长", "");
						playTime = ParseVideoInfoProperty.getPlayTime(timeStr);
					}
				}
			} catch (Exception e) {
				playTime = (Integer) null;
			}
		}
  		//视频URL
  		String vUrl = urlBean.getUrl();
  		//视频video_id
  		String videoId = urlBean.getVideoId();
  		//剧集id
  		String collectId = urlBean.getCollectId();

		videoInfo.setVideoId(videoId);
		videoInfo.setCollectId(collectId);
		videoInfo.setVideoName(title);
		videoInfo.setVideoTime(playTime);
		videoInfo.setTag(type);
		videoInfo.setCategory(category);
		videoInfo.setActor(vmainactor);
		videoInfo.setVideoDesc(vprofile);
		videoInfo.setUrl(vUrl);
		videoInfo.setSite(SITE_TYPE_SOHU);
		videoInfo.setCreateTime(DateUtil.getCurrentDateTime());
  	
		return videoInfo;
	}
	
	/**
	 * 解析视频属性
	 * @param urlBean
	 * @return
	 */
	public static VideoProperty getVideoProperty(UrlBean urlBean){
		VideoProperty videoPro = new VideoProperty();
		
		int up = -1;
		int down = -1;
  		String videoId = urlBean.getVideoId();
  		String type = urlBean.getType();
  		if(type.equals(VideoType.dianying)){
  	  		//http://score.my.tv.sohu.com/digg/get.do?vid=333239&type=1
  	  		try {
				String requestUpStr =  UP_DOWN_MOV_BASE_URL + videoId;
				String responseStr = HttpRequestUtil.httpGetRequestForComment(requestUpStr, null);
				responseStr = responseStr.replace("(", "");
				String responsJsonStr = responseStr.replace(")", "");
				JSONObject root = JSONObject.parseObject(responsJsonStr);
				up = root.getIntValue("upCount");
				down = root.getIntValue("downCount");
			} catch (Exception e) {
				up = (Integer) null;
				down = (Integer) null;
			}
  		}else if(type.equals(VideoType.dianshiju)){
  	  		//http://score.my.tv.sohu.com/digg/get.do?vid=390286&type=2
  	  		try {
				String requestUpStr =  UP_DOWN_TV_BASE_URL + videoId;
				String responseStr = HttpRequestUtil.httpGetRequest(requestUpStr, null);
				responseStr = responseStr.replace("(", "");
				String responsJsonStr = responseStr.replace(")", "");
				JSONObject root = JSONObject.parseObject(responsJsonStr);
				up = root.getIntValue("upCount");
				down = root.getIntValue("downCount");
			} catch (Exception e) {
				up = (Integer) null;
				down = (Integer) null;
			}
  		}else{
  	  		//http://score.my.tv.sohu.com/digg/get.do?vid=1667607&type=7
  	  		try {
				String requestUpStr =  UP_DOWN_ZY_BASE_URL + videoId;
				String responseStr = HttpRequestUtil.httpGetRequest(requestUpStr, null);
				responseStr = responseStr.replace("(", "");
				String responsJsonStr = responseStr.replace(")", "");
				JSONObject root = JSONObject.parseObject(responsJsonStr);
				up = root.getIntValue("upCount");
				down = root.getIntValue("downCount");
			} catch (Exception e) {
				up = (Integer) null;
				down = (Integer) null;
			}
  		}

  		
  		//单集播放量，请求URL	  		
  		//http://count.vrs.sohu.com/count/stat.do?videoId=333239&tvid=254685&playlistId=1003315
  		//&os=Windows8&online=0&categoryId=1&catecode&plat=flash&type=vrs&t
  		int playCount = -1;
		try {
			String playlistId = urlBean.getCollectId();
			String requestCountStr = COUNT_BASE_URL + COUNT_VIDEOID_CONNECTOR + videoId
						+ COUNT_TVID_CONNECTOR + COUNT_TVID + COUNT_PLAYLISTID_CONNECTOR 
						+ playlistId + COUNT_OTHER_CONNECTOR;
			String responseCountStr = HttpRequestUtil.httpGetRequest(requestCountStr, null);
			responseCountStr = responseCountStr.replace("var count=", "");
			playCount = Integer.parseInt(responseCountStr.trim());
		} catch (NumberFormatException e1) {
			playCount = (Integer) null;
		}
  		//评论条数
		int totalCommentSize = -1;
		try {
			int pageNum = 1;
			String requestUrlStr = ParseVideoInfoProperty.getRequestUrlStrForComment(urlBean.getTopId(), pageNum);
			String responseJonStr = HttpRequestUtil.httpGetRequestForComment(requestUrlStr, null);
			CommentPageCountVO commentPageCountVOFirst = ParseVideoInfoProperty.parseOnePageCommentUserInfo(responseJonStr,urlBean.getVideoId());
			totalCommentSize = commentPageCountVOFirst.getTotlaCommentSize();
		} catch (Exception e1) {
			totalCommentSize = (Integer) null;
		}
		
		videoPro.setVideoId(videoId);
		videoPro.setSite(SITE_TYPE_SOHU);
		videoPro.setCreateTime(DateUtil.getCurrentDateTime());
    	try {
			videoPro.setUpCount(up);
			videoPro.setDownCount(down);
			videoPro.setPlayCount(playCount);
			videoPro.setCommentCount(totalCommentSize);
		} catch (Exception e) {
			log.error("解析VideoProperty(up,down,play,commentcount)异常",e);
		}
  		
		return videoPro;
	}
	
	/**
	 * 返回评论列表
	 * @param urlBeanExt
	 * @return
	 */
	public VideoCommentUserInfoVO getCommentUserInfo(UrlBeanExt urlBeanExt){
		List<VideoCommentUserInfoVO> totalVideoCommentUserInfoVOList = new ArrayList<VideoCommentUserInfoVO>();
		VideoCommentUserInfoVO videoCommentUserInfoVO  = new VideoCommentUserInfoVO();
		
		CompletionService<List<VideoCommentUserInfoVO>> parseVideoCommentUserInfoThread 
		= new ExecutorCompletionService<List<VideoCommentUserInfoVO>>(ParseThreadPool.parseVideoCommentThreadPool);
		
		List<UrlBean> urlBeanList = urlBeanExt.getUrlList();
		for(int i=0;i<urlBeanList.size();i++){
			UrlBean urlBean = urlBeanList.get(i);
			ParseVideoCommentTask parseVideoCommentTask = new ParseVideoCommentTask(urlBean);
			parseVideoCommentUserInfoThread.submit(parseVideoCommentTask);
		}
		for(int j=0;j<urlBeanList.size();j++){
			List<VideoCommentUserInfoVO> videoCommentUserInfoVOList;
			try {
				videoCommentUserInfoVOList = parseVideoCommentUserInfoThread.take().get();
				totalVideoCommentUserInfoVOList.addAll(videoCommentUserInfoVOList);
			} catch (Exception e) {
				log.error("线程回收异常",e);
			}
		}
		List<Comment> totalCommentList = new ArrayList<Comment>();
		List<UserInfo> totalUserInfoList = new ArrayList<UserInfo>();
		for (VideoCommentUserInfoVO videoCommentUserInfoVOTemp : totalVideoCommentUserInfoVOList) {
			totalCommentList.addAll(videoCommentUserInfoVOTemp.getCommentList());
			totalUserInfoList.addAll(videoCommentUserInfoVOTemp.getUserInfoList());
		}
		
		videoCommentUserInfoVO.setCommentList(totalCommentList);
		videoCommentUserInfoVO.setUserInfoList(totalUserInfoList);
		
		return videoCommentUserInfoVO;
	}
	/**
	 * 解析评论
	 * @param jsonStr
	 * @param tvid
	 * @return
	 */
	public static CommentPageCountVO parseOnePageCommentUserInfo(String jsonStr,String tvid){
		CommentPageCountVO commentPageCountVO = new CommentPageCountVO();
		VideoCommentUserInfoVO videoCommentUserInfoVO = new VideoCommentUserInfoVO();
		List<Comment> commentList = new ArrayList<Comment>();
		List<UserInfo> userInfoList = new ArrayList<UserInfo>();
		//pageCount
		JSONObject root = JSONObject.parseObject(jsonStr);
		int totalComment = root.getIntValue("cmt_sum");
		int mod = totalComment % COMMENT_PAGE_SIZE ;
		int pageCount = totalComment / COMMENT_PAGE_SIZE;
		pageCount = (mod > 0) ? (pageCount + 1) : pageCount;
		
		JSONArray data = root.getJSONArray("comments");
		for(int i=0;i<data.size();i++){
        	Comment commentObj = new Comment();	
        	UserInfo userInfoObj = new UserInfo();
        	
        	JSONObject  userinfo = data.getJSONObject(i).getJSONObject("passport");
        	//评论用户Id
        	String userId = userinfo.getString("user_id");
        	//用户名
        	String nickName = userinfo.getString("nickname");
        	//转发数
        	int forwardCount = userinfo.getIntValue("followers_count");
        	
        	//评论用户头像地址url
        	String headerPic = userinfo.getString("img_url");
        	//评论用户个人空间介绍地址URL,暂无信息
        	String profileUrl = userinfo.getString("profile_url");
        	
        	//获取评论Id 
        	String commentId = data.getJSONObject(i).getString("comment_id");
        	
            /*获取评论时间*/
            //获取返回的时间戳
            long timestamp = data.getJSONObject(i).getLong("create_time");
            //转换成字符串
            String publishTime = DateUtil.getDateFromTimeStamp(timestamp);
            //获取sohu No转发数，回复数，喜欢数
            int commentCount = data.getJSONObject(i).getIntValue("reply_count");
            int likeCount =  data.getJSONObject(i).getIntValue("support_count");
            //获取评论对应视频在letv的tvid
            String videoId = tvid;
            
        	try {
				//获取评论内容
				String commentContent = data.getJSONObject(i).getString("content");
				if(commentContent.length() > 500){
					commentContent = commentContent.substring(0, 499);
				}
				try {
					commentContent = URLDecoder.decode(URLEncoder.encode(commentContent, "gbk"),"gbk");
				} catch (UnsupportedEncodingException e) {
					log.error("评论解析异常",e);
				}
				
				if(!commentContent.trim().equals("")){
					//设置评论信息
					commentObj.setVideoId(videoId);
					commentObj.setCommentId(commentId);
					commentObj.setSite(SITE_TYPE_SOHU);
					commentObj.setUserId(userId);
					commentObj.setCommentContent(commentContent);
					commentObj.setPublishTime(publishTime);
					commentObj.setCommentCount(commentCount);
					commentObj.setLikeCount(likeCount);
					commentObj.setForwardCount(forwardCount);
					commentObj.setCreateTime(DateUtil.getCurrentDateTime());
				    commentList.add(commentObj); 
				}
			} catch (NumberFormatException e) {
				log.error("评论内容获取异常",e);
			}
            
        	//设置用户信息
        	userInfoObj.setUserId(userId);
        	userInfoObj.setSite(SITE_TYPE_SOHU);
        	userInfoObj.setNickName(nickName);
        	userInfoObj.setHeaderPic(headerPic);
        	userInfoObj.setProfileUrl(profileUrl);
        	
        	userInfoList.add(userInfoObj);
		}
		videoCommentUserInfoVO.setCommentList(commentList);
		videoCommentUserInfoVO.setUserInfoList(userInfoList);
		commentPageCountVO.setPageCount(pageCount);
		commentPageCountVO.setTotlaCommentSize(totalComment);
		commentPageCountVO.setVideoCommentUserInfoVO(videoCommentUserInfoVO);
		
		return commentPageCountVO;
	}
	/**
	 * 拼接评论
	 * @param videoId
	 * @param pageNum
	 * @return eg:
	 */
	public static String getRequestUrlStrForComment(String topicId,int pageNum){
		//"http://changyan.sohu.com/api/services/topic/load?client_id=cyqyBluaj&topic_source_id=1667607&outer_page_no=1"
		String requestUrlStr = COMMENT_BASE_URL + topicId
								+ COMMENT_PAGE_NUM_CONNECTOR +  pageNum;
		return requestUrlStr;
	}

    /**
     * 对由于抓取过程中突然有人评论导致之前的
     * 评论重复抓取的情况，去重
     * @param commentListBefore  去重前
     * @return commentListAfter  去重后
     */
    public static List<Comment> removeDuplicateComment(List<Comment> commentListBefore){
		Map<String,Comment> commentMap = new HashMap<String,Comment>();
		List<Comment> commentListAfter = new ArrayList<Comment>();
		for(int i=0;i<commentListBefore.size();i++){
			commentMap.put(commentListBefore.get(i).getVideoId()+"-"+commentListBefore.get(i).getCommentId()+"-"+commentListBefore.get(i).getSite(), commentListBefore.get(i));
		}
		commentListAfter.addAll(commentMap.values());
    	return commentListAfter;
    }
    
    /**
     * 指数解析
     * @param urlBeanExt
     * @return
     */
    public static VideoCollectVO getCollectPlayInfoProperty(UrlBeanExt urlBeanExt){
		String collectId = urlBeanExt.getVideoCollect().getCollectId();
		VideoCollectVO videoCollectVO = new VideoCollectVO();
		List<CollectPlayInfo> collectPlayInfoList = new ArrayList<CollectPlayInfo>();
		List<CollectProperty> collectPropertyList = new ArrayList<CollectProperty>();
		
		//解析省份播放量
		String requestProvinceUrl = ParseVideoInfoProperty.ZS_AREA_BASE_URL + collectId + ParseVideoInfoProperty.ZS_XML_EXTENTION;
		String responseProvinceStr = HttpRequestUtil.httpGetRequestForComment(requestProvinceUrl, null);
		org.dom4j.Document xmlDocProvince;
		try {
			xmlDocProvince = org.dom4j.DocumentHelper.parseText(responseProvinceStr);
			List<org.dom4j.Element> itemList =  xmlDocProvince.selectNodes("/UnifiedResponse/attachment/DataList/Item");
			for (org.dom4j.Element element : itemList) {
				CollectPlayInfo collectPlayInfo = new CollectPlayInfo();
				String provinceCode = element.attributeValue("code");
				long playCount = Long.parseLong(element.attributeValue("top"));
				collectPlayInfo.setCollectId(collectId);
				collectPlayInfo.setProvinceCode(provinceCode);
				collectPlayInfo.setPlayCount(playCount);
				collectPlayInfo.setSite(ParseVideoInfoProperty.SITE_TYPE_SOHU);
				collectPlayInfoList.add(collectPlayInfo);
			}
		} catch (org.dom4j.DocumentException e1) {
			log.error("XML解析province错误",e1);
		}
		//Sex
		String requestSexUrl = ParseVideoInfoProperty.ZS_SEX_BASE_URL + collectId + ParseVideoInfoProperty.ZS_XML_EXTENTION;
		String responseSexStr = HttpRequestUtil.httpGetRequestForComment(requestSexUrl, null);
		org.dom4j.Document xmlDocSex;
		try {
			xmlDocSex = org.dom4j.DocumentHelper.parseText(responseSexStr);
			List<org.dom4j.Element> itemList = xmlDocSex.selectNodes("/UnifiedResponse/attachment/PieData/Item");
			for (org.dom4j.Element element : itemList) {
				CollectProperty collectSex = new CollectProperty();
				collectSex.setCollectId(collectId);
				collectSex.setPropertyKey(element.attributeValue("type"));
				collectSex.setPropertyValue(element.attributeValue("value"));
				collectSex.setSite(ParseVideoInfoProperty.SITE_TYPE_SOHU);
				collectPropertyList.add(collectSex);
			}
		} catch (org.dom4j.DocumentException e) {
			log.error("XML解析sex错误",e);
		}
  	 	
  	 	videoCollectVO.setCollectPropertyList(collectPropertyList);
  	 	videoCollectVO.setCollectPlayInfoList(collectPlayInfoList);
		return videoCollectVO;
	}
    
    /**
     * 把截取的时长字符串转换成以秒为单位
     * @param timeStr
     * @return
     */
    public static int getPlayTime(String timeStr){
    	int playTime = 0;
		try {
			if(timeStr.contains("时") && timeStr.contains("分") && timeStr.contains("秒")){
				int startHour = timeStr.indexOf("时") -1;
				int endHour = timeStr.indexOf("时");
				int hourCount = Integer.parseInt(timeStr.substring(startHour, endHour));
				
				int startMin = timeStr.indexOf("时") + 1;
				int endMin = timeStr.indexOf("分");
				int minCount = Integer.parseInt(timeStr.substring(startMin, endMin));
				
				int startSec = timeStr.indexOf("分") + 1;
				int endSec = timeStr.indexOf("秒");
				int secCount = Integer.parseInt(timeStr.substring(startSec, endSec));
				
				playTime = hourCount * 3600 + minCount * 60 + secCount;
			}else if(timeStr.contains("时") && timeStr.contains("秒")){
				int startHour = timeStr.indexOf("时") -1;
				int endHour = timeStr.indexOf("时");
				int hourCount = Integer.parseInt(timeStr.substring(startHour, endHour));

				int startSec = timeStr.indexOf("时") + 1;
				int endSec = timeStr.indexOf("秒");
				int secCount = Integer.parseInt(timeStr.substring(startSec, endSec));
				
				playTime = hourCount * 3600 + secCount;
			}else if(timeStr.contains("时") && timeStr.contains("分")){
				int startHour = timeStr.indexOf("时") -1;
				int endHour = timeStr.indexOf("时");
				int hourCount = Integer.parseInt(timeStr.substring(startHour, endHour));
				
				int startMin = timeStr.indexOf("时") + 1;
				int endMin = timeStr.indexOf("分");
				int minCount = Integer.parseInt(timeStr.substring(startMin, endMin));
				
				playTime = hourCount * 3600 + minCount * 60;
			}else if(timeStr.contains("分") && timeStr.contains("秒")){
				int startMin = timeStr.indexOf("：") + 1;
				int endMin = timeStr.indexOf("分");
				int minCount = Integer.parseInt(timeStr.substring(startMin, endMin));
				
				int startSec = timeStr.indexOf("分") + 1;
				int endSec = timeStr.indexOf("秒");
				int secCount = Integer.parseInt(timeStr.substring(startSec, endSec));
				
				playTime = minCount * 60 + secCount;
			}else if(timeStr.contains("时")){
				int startHour = timeStr.indexOf(":") + 1;
				int endHour = timeStr.indexOf("时");
				int hourCount = Integer.parseInt(timeStr.substring(startHour, endHour));
				
				playTime = hourCount * 3600;
			}else if(timeStr.contains("分")){
				int startMin = timeStr.indexOf(":") + 1;
				int endMin = timeStr.indexOf("分");
				int minCount = Integer.parseInt(timeStr.substring(startMin, endMin));
				
				playTime = minCount * 60;
			}else{
				int startSec = timeStr.indexOf(":") + 1;
				int endSec = timeStr.indexOf("秒");
				int secCount = Integer.parseInt(timeStr.substring(startSec, endSec));
				
				playTime = secCount;
			}
		} catch (NumberFormatException e) {
			log.error("从页面截取的时长字符特殊暂时不能解析",e);
		}
    	
    	return playTime;
    }
}
