import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ApplicationObjectSupport;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import cn.com.screendata.client.bean.Comment;
import cn.com.screendata.client.bean.UrlBean;
import cn.com.screendata.client.bean.UrlBeanExt;
import cn.com.screendata.client.bean.UserInfo;
import cn.com.screendata.client.bean.VideoCollectVO;
import cn.com.screendata.client.bean.VideoCommentUserInfoVO;
import cn.com.screendata.client.bean.VideoInfo;
import cn.com.screendata.client.bean.VideoInfoPropertyVO;
import cn.com.screendata.client.bean.VideoProperty;
import cn.com.screendata.client.bean.VideoType;
import cn.com.screendata.client.controller.parser.ParseVideoInfoProperty;
import cn.com.screendata.client.controller.util.HttpRequestUtil;
import cn.com.screendata.client.dao.CommentDao;
import cn.com.screendata.client.dao.VideoCollectDao;
import cn.com.screendata.client.dao.VideoInfoDao;
import cn.com.screendata.client.dao.VideoPropertyDao;
import cn.com.screendata.client.search.VideoCollectSearchBean;
import cn.com.screendata.client.service.CommentService;
import cn.com.screendata.client.service.VideoCollectService;
import cn.com.screendata.client.service.VideoInfoService;
import cn.com.screendata.client.service.VideoPropertyService;



public class TestInsertParse {

	private final static Logger log = Logger.getLogger(TestParse.class);
	
	public static void main(String[] args){
		List<UrlBean> urlList = new ArrayList<UrlBean>();
		UrlBean zongyi = new UrlBean();
		String url = "http://tv.sohu.com/20140317/n396695478.shtml";
		String html = HttpRequestUtil.httpGetRequest(url, null);
		String collectId = "1539";
		String videoId = "1667607";
		long tvid = 1195893;
		zongyi.setCollectId(collectId);
		zongyi.setHtml(html);
		zongyi.setUrl(url);
		zongyi.setVideoId(videoId);
		zongyi.setType(VideoType.zongyi);
		
		UrlBean movie = new UrlBean();
		String urlMov = "http://tv.sohu.com/20110602/n309183131.shtml";
		String htmlMov = HttpRequestUtil.httpGetRequest(urlMov, null);
		String collectIdMov = "1003315";
		String videoIdMov = "333239";
		long tvidMov = 254685;
		String topId = "5175241";
		String clientIdMov = "cyqyBluaj";
		
		movie.setCollectId(collectIdMov);
		movie.setHtml(htmlMov);
		movie.setUrl(urlMov);
		movie.setVideoId(videoIdMov);
		movie.setTopId(topId);
		movie.setType(VideoType.dianying);
		
		UrlBean tv = new UrlBean();
		String urlTv = "http://tv.sohu.com/20110816/n316500606.shtml";
		String htmlTv = HttpRequestUtil.httpGetRequest(urlTv, null);
		String collectIdTv = "1004491";
		String videoIdTv = "390286";
		long tvidTv = 299663;
		String clientIdTv = "cyqyBluaj";
		tv.setCollectId(collectIdTv);
		tv.setHtml(htmlTv);
		tv.setUrl(urlTv);
		tv.setVideoId(videoIdTv);
		tv.setType(VideoType.dianshiju);
		
//		urlList.add(zongyi);
		urlList.add(movie);
//		urlList.add(tv);
		
		UrlBeanExt urlBeanExt = new UrlBeanExt();
		urlBeanExt.setUrlList(urlList);
		urlBeanExt.setVideoCollect(null);
		
		ApplicationContext ctx  = new ClassPathXmlApplicationContext("applicationContext.xml");
		VideoCollectService videoCollectService = (VideoCollectService) ctx.getBean("videoCollectService");
		CommentService commentService = (CommentService) ctx.getBean("commentService");
		VideoInfoService videoInfoService = (VideoInfoService) ctx.getBean("videoInfoService");
		VideoPropertyService videoPropertyService = (VideoPropertyService) ctx.getBean("videoPropertyService");
		
		ParseVideoInfoProperty parseVideoInfoProperty = new ParseVideoInfoProperty();
		
		VideoCollectVO videoCollectVO = parseVideoInfoProperty.getVideoInfoProperty(urlBeanExt);
		VideoInfoPropertyVO videoInfoPropertyVO = videoCollectVO.getVideoInfoPropertyVO();
		List<VideoInfo> videoInfoList = videoInfoPropertyVO.getVideoInfoList();
		List<VideoProperty> videoPropertyList = videoInfoPropertyVO.getVideoPropertyList();

		//VideoInfo 
		for(VideoInfo videoInfo : videoInfoList){
			VideoInfo videoInfoTemp = new VideoInfo();
			videoInfoTemp.setVideoId(videoInfo.getVideoId());
			videoInfoTemp = videoInfoService.getVideoInfoByExample(videoInfoTemp);
			if(videoInfoTemp == null){
				videoInfoService.addVideoInfo(videoInfo);
			}
		
		}
		//VideoProperty
		List<VideoProperty> videoProListTemp = new ArrayList<VideoProperty>();
		for(VideoProperty videoProperty : videoPropertyList){

			VideoProperty videoPropertyTemp = new VideoProperty();
			videoPropertyTemp.setVideoId(videoProperty.getVideoId());
			videoPropertyTemp.setSite(videoProperty.getSite());
			videoProListTemp.add(videoPropertyTemp);
		}
		videoPropertyService.batchRemoveVideoPropertyByExample(videoProListTemp);
		videoPropertyService.batchAddVideoProperty(videoPropertyList);
		
		//Commment
		
		VideoCommentUserInfoVO videoCommentUserInfoVO = parseVideoInfoProperty.getCommentUserInfo(urlBeanExt);
		List<Comment> totalCommentListBefore = videoCommentUserInfoVO.getCommentList();
		
		//去重
		List<Comment> totalCommentList = ParseVideoInfoProperty.removeDuplicateComment(totalCommentListBefore);
		List<Comment> commentListTemp = new ArrayList<Comment>();
		log.error("去重后有=======>"+totalCommentList.size());
		for (Comment comment : totalCommentList) {
			Comment commentTemp = new Comment();
			commentTemp.setVideoId(comment.getVideoId());
			commentTemp.setCommentId(comment.getCommentId());
			commentTemp.setSite(comment.getSite());
			commentListTemp.add(commentTemp);
		}
		commentService.batchRemoveCommentByExample(commentListTemp);
		commentService.batchAddComment(totalCommentList);

	}
}
