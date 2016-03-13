import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

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


public class TestParse {
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
		urlList.add(tv);
//		urlList.add(movie);
		
		UrlBeanExt urlBeanExt = new UrlBeanExt();
		urlBeanExt.setUrlList(urlList);
		urlBeanExt.setVideoCollect(null);
		
		ParseVideoInfoProperty parseVideoInfoProperty = new ParseVideoInfoProperty();
		VideoCollectVO videoCollectVO = parseVideoInfoProperty.getVideoInfoProperty(urlBeanExt);
		VideoInfoPropertyVO videoInfoPropertyVO = videoCollectVO.getVideoInfoPropertyVO();
		List<VideoInfo> videoInfoList = videoInfoPropertyVO.getVideoInfoList();
		List<VideoProperty> videoPropertyList = videoInfoPropertyVO.getVideoPropertyList();
		for (VideoInfo videoInfo : videoInfoList) {
			log.info("VideoId------->"+videoInfo.getVideoId());
			log.info("VideoName----->"+videoInfo.getVideoName());
			log.info("VideoUrl-->"+videoInfo.getUrl());
			log.info("VideoActor---->"+videoInfo.getActor());
			log.info("VideoCategory->"+videoInfo.getCategory());
			log.info("VideoTag------>"+videoInfo.getTag());
			log.info("VideoDesc----->"+videoInfo.getVideoDesc());
			log.info("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
		}
		for (VideoProperty videoProperty : videoPropertyList) {
			log.info("VideoId------->"+videoProperty.getVideoId());
			log.info("UpCount------->"+videoProperty.getUpCount());
			log.info("DownCount----->"+videoProperty.getDownCount());
			log.info("PlayCount----->"+videoProperty.getPlayCount());
			log.info("CommentCount-->"+videoProperty.getCommentCount());
			log.info("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
		}
		
		VideoCommentUserInfoVO videoCommentUserInfoVO = parseVideoInfoProperty.getCommentUserInfo(urlBeanExt);
		List<Comment> totalCommentList = videoCommentUserInfoVO.getCommentList();
		List<UserInfo> totalUserInfoList = videoCommentUserInfoVO.getUserInfoList();
		for (Comment comment : totalCommentList) {
			log.info("VideoId---------->"+comment.getVideoId());
			log.info("CommentId-------->"+comment.getCommentId());
			log.info("CommentContent--->"+comment.getCommentContent());
			log.info("UserId----------->"+comment.getUserId());
			log.info("ForwardCount----->"+comment.getForwardCount());
			log.info("LikeCount-------->"+comment.getLikeCount());
			log.info("ReplyCount------->"+comment.getCommentCount());
			log.info("PublishTime------>"+comment.getPublishTime());
			log.info("PublicClient----->"+comment.getPublishClient());
			log.info("##############################################");
		}
		log.info("TotalCommentListSize----------->"+totalCommentList.size());
//		for (UserInfo userInfo : totalUserInfoList) {
//			log.info("UserId------>"+userInfo.getUserId());
//			log.info("HeaderPic--->"+userInfo.getHeaderPic());
//			log.info("Province---->"+userInfo.getProvince());
//			log.info("NickName---->"+userInfo.getNickName());
//			log.info("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
//		}
		
	}

}
