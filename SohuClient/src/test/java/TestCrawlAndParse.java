//import java.util.ArrayList;
//import java.util.List;
//
//import org.springframework.context.ApplicationContext;
//import org.springframework.context.support.ClassPathXmlApplicationContext;
//
//import cn.com.screendata.client.bean.CollectPlayInfo;
//import cn.com.screendata.client.bean.CollectProperty;
//import cn.com.screendata.client.bean.Comment;
//import cn.com.screendata.client.bean.UrlBeanExt;
//import cn.com.screendata.client.bean.VideoCollect;
//import cn.com.screendata.client.bean.VideoCollectVO;
//import cn.com.screendata.client.bean.VideoCommentUserInfoVO;
//import cn.com.screendata.client.bean.VideoInfo;
//import cn.com.screendata.client.bean.VideoInfoPropertyVO;
//import cn.com.screendata.client.bean.VideoProperty;
//import cn.com.screendata.client.controller.crawler.SohuCrawler;
//import cn.com.screendata.client.controller.parser.ParseVideoInfoProperty;
//import cn.com.screendata.client.search.VideoCollectSearchBean;
//import cn.com.screendata.client.service.CollectPlayInfoService;
//import cn.com.screendata.client.service.CollectPropertyService;
//import cn.com.screendata.client.service.CommentService;
//import cn.com.screendata.client.service.VideoCollectService;
//import cn.com.screendata.client.service.VideoInfoService;
//import cn.com.screendata.client.service.VideoPropertyService;
//
//
//public class TestCrawlAndParse {
//
//	/**
//	 * @param args
//	 */
//	public static void main(String[] args) {
//		//zongyi
////		String urlStr = "http://tv.sohu.com/fcjjl/#pllist_full";
////		String urlStr = "http://tv.sohu.com/fcwm/";
//		//mov
//		String urlStr = "http://tv.sohu.com/s2011/xqnyh/";
////		String urlStr = "http://tv.sohu.com/s2014/xzjzmb/#";
////		String urlStr = "http://tv.sohu.com/s2012/jqjcd/";
//		//tv
////		String urlStr = "http://tv.sohu.com/s2014/lb2014/";
////		String urlStr = "http://tv.sohu.com/s2013/fsyxb/";
////		String urlStr = "http://tv.sohu.com/s2013/4402/s383695385/";
////		String urlStr = "http://tv.sohu.com/s2013/wstzbzhfh/";
//		SohuCrawler crawler = new SohuCrawler();
//		UrlBeanExt urlBeanExt = crawler.execute(urlStr);
//		VideoCollect videoCollect = urlBeanExt.getVideoCollect();
//		System.out.println("1.CollectId-------->"+videoCollect.getCollectId());
//		System.out.println("2.CollectName------>"+videoCollect.getCollectName());
//		System.out.println("3.CollectCategory-->"+videoCollect.getCollectCategory());
//		System.out.println("4.CollectTag------->"+videoCollect.getCollectTag());
//		System.out.println("5.CollectDesc------>"+videoCollect.getCollectDesc());
//		System.out.println("6.Actor------------>"+videoCollect.getActor());
//		System.out.println("7.Director--------->"+videoCollect.getDirector());
//		System.out.println("8.PartCount-------->"+videoCollect.getPartCount());
//		System.out.println("9.PlayCount-------->"+videoCollect.getPlayCount());
//		System.out.println("10.Year------------>"+videoCollect.getYear());
//		System.out.println("11.Site------------>"+videoCollect.getSite());
//		System.out.println("12.CreateTime------>"+videoCollect.getCreateTime());
//		System.out.println("###################################################");
////		List<UrlBean> urlBeanList = urlBeanExt.getUrlList();
////		for (UrlBean urlBean : urlBeanList) {
////			System.out.println("CollectId------>"+urlBean.getCollectId());
////			System.out.println("VideoId-------->"+urlBean.getVideoId());
////			System.out.println("Type----------->"+urlBean.getType());
////			System.out.println("Url------------>"+urlBean.getUrl());
////			System.out.println("###############################################");
////		}
////		System.out.println("剧集集数"+urlBeanList.size());
//
//		ApplicationContext ctx  = new ClassPathXmlApplicationContext("applicationContext.xml");
//		VideoCollectService videoCollectService = (VideoCollectService) ctx.getBean("videoCollectService");
//		CommentService commentService = (CommentService) ctx.getBean("commentService");
//		VideoInfoService videoInfoService = (VideoInfoService) ctx.getBean("videoInfoService");
//		VideoPropertyService videoPropertyService = (VideoPropertyService) ctx.getBean("videoPropertyService");
//		CollectPlayInfoService collectPlayInfoService = (CollectPlayInfoService) ctx.getBean("collectPlayInfoService");
//		CollectPropertyService collectPropertyService = (CollectPropertyService) ctx.getBean("collectPropertyService");
//
//		VideoCollectSearchBean vcSearchBean = new VideoCollectSearchBean();
//		vcSearchBean.setCollectId(videoCollect.getCollectId());
//		vcSearchBean.setSite(ParseVideoInfoProperty.SITE_TYPE_SOHU);
//		videoCollectService.removeVideoCollectByCondition(vcSearchBean);
//
//		videoCollectService.addVideoCollect(videoCollect);
//
//		ParseVideoInfoProperty parseVideoInfoProperty = new ParseVideoInfoProperty();
//
//		VideoCollectVO videoCollectVO = parseVideoInfoProperty.getVideoInfoProperty(urlBeanExt);
//		VideoInfoPropertyVO videoInfoPropertyVO = videoCollectVO.getVideoInfoPropertyVO();
//		List<VideoInfo> videoInfoList = videoInfoPropertyVO.getVideoInfoList();
//		List<VideoProperty> videoPropertyList = videoInfoPropertyVO.getVideoPropertyList();
//
//		//VideoInfo
//		for(VideoInfo videoInfo : videoInfoList){
//			VideoInfo videoInfoTemp = new VideoInfo();
//			videoInfoTemp.setVideoId(videoInfo.getVideoId());
//			videoInfoTemp = videoInfoService.getVideoInfoByExample(videoInfoTemp);
//			if(videoInfoTemp == null){
//				videoInfoService.addVideoInfo(videoInfo);
//			}
//
//		}
//		//VideoProperty
//		List<VideoProperty> videoProListTemp = new ArrayList<VideoProperty>();
//		for(VideoProperty videoProperty : videoPropertyList){
//
//			VideoProperty videoPropertyTemp = new VideoProperty();
//			videoPropertyTemp.setVideoId(videoProperty.getVideoId());
//			videoPropertyTemp.setSite(videoProperty.getSite());
//			videoProListTemp.add(videoPropertyTemp);
//		}
//		videoPropertyService.batchRemoveVideoPropertyByExample(videoProListTemp);
//		videoPropertyService.batchAddVideoProperty(videoPropertyList);
//
//		//CollectPlayInfo
//		List<CollectPlayInfo> collectPlayInfoList = videoCollectVO.getCollectPlayInfoList();
//		List<CollectPlayInfo> collectPlayInfoListTemp = new ArrayList<CollectPlayInfo>();
//		for(CollectPlayInfo collectPlayInfo : collectPlayInfoList){
//			CollectPlayInfo collectPlayInfoTemp = new CollectPlayInfo();
//			collectPlayInfoTemp.setCollectId(collectPlayInfo.getCollectId());
//			collectPlayInfoTemp.setProvinceCode(collectPlayInfo.getProvinceCode());
//			collectPlayInfoListTemp.add(collectPlayInfoTemp);
//		}
//		collectPlayInfoService.batchRemoveCollectPlayInfoByExample(collectPlayInfoListTemp);
//		collectPlayInfoService.batchAddCollectPlayInfo(collectPlayInfoList);
//
//		//CollectProperty
//		List<CollectProperty> collectPropertyList = videoCollectVO.getCollectPropertyList();
//		List<CollectProperty> collectPropertyListTemp = new ArrayList<CollectProperty>();
//		for(CollectProperty collectProperty : collectPropertyList){
//			CollectProperty collectPropertyTemp = new CollectProperty();
//			collectPropertyTemp.setCollectId(collectProperty.getCollectId());
//			collectPropertyTemp.setPropertyKey(collectProperty.getPropertyKey());
//			collectPropertyListTemp.add(collectPropertyTemp);
//		}
//		collectPropertyService.batchRemoveCollectPropertyByExample(collectPropertyListTemp);
//		collectPropertyService.batchAddCollectProperty(collectPropertyList);
//
//		//Commment
//
//		VideoCommentUserInfoVO videoCommentUserInfoVO = parseVideoInfoProperty.getCommentUserInfo(urlBeanExt);
//		List<Comment> totalCommentListBefore = videoCommentUserInfoVO.getCommentList();
//
//		//去重
//		List<Comment> totalCommentList = ParseVideoInfoProperty.removeDuplicateComment(totalCommentListBefore);
//		List<Comment> commentListTemp = new ArrayList<Comment>();
//
//		for (Comment comment : totalCommentList) {
//			Comment commentTemp = new Comment();
//			commentTemp.setVideoId(comment.getVideoId());
//			commentTemp.setCommentId(comment.getCommentId());
//			commentTemp.setSite(comment.getSite());
//			commentListTemp.add(commentTemp);
//		}
//		commentService.batchRemoveCommentByExample(commentListTemp);
//		commentService.batchAddComment(totalCommentList);
//	System.out.println("Test done!");
//	}
//
//}
