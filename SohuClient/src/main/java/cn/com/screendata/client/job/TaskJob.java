package cn.com.screendata.client.job;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cn.com.screendata.client.bean.CollectAddress;
import cn.com.screendata.client.bean.CollectPlayInfo;
import cn.com.screendata.client.bean.CollectProperty;
import cn.com.screendata.client.bean.Comment;
import cn.com.screendata.client.bean.TaskJsonBean;
import cn.com.screendata.client.bean.UrlBeanExt;
import cn.com.screendata.client.bean.VideoCollect;
import cn.com.screendata.client.bean.VideoCollectHistory;
import cn.com.screendata.client.bean.VideoCollectVO;
import cn.com.screendata.client.bean.VideoCommentUserInfoVO;
import cn.com.screendata.client.bean.VideoInfo;
import cn.com.screendata.client.bean.VideoInfoPropertyVO;
import cn.com.screendata.client.bean.VideoProperty;
import cn.com.screendata.client.bean.VideoPropertyHistory;
import cn.com.screendata.client.cache.TaskMsgQueue;
import cn.com.screendata.client.controller.crawler.SohuCrawler;
import cn.com.screendata.client.controller.parser.ParseCollectList;
import cn.com.screendata.client.controller.parser.ParseVideoInfoProperty;
import cn.com.screendata.client.service.CollectAddressService;
import cn.com.screendata.client.service.CollectPlayInfoService;
import cn.com.screendata.client.service.CollectPropertyService;
import cn.com.screendata.client.service.CommentService;
import cn.com.screendata.client.service.VideoCollectHistoryService;
import cn.com.screendata.client.service.VideoCollectService;
import cn.com.screendata.client.service.VideoInfoService;
import cn.com.screendata.client.service.VideoPropertyHistoryService;
import cn.com.screendata.client.service.VideoPropertyService;
import cn.com.screendata.client.search.CollectAddressSearchBean;
import cn.com.screendata.client.search.VideoCollectSearchBean;
import cn.com.screendata.common.spring.BeanFactory;
import cn.com.screendata.monitor.client.MonitorClient;

@Component(value="taskJob")

public class TaskJob {
	private final static Logger log = Logger.getLogger(TaskJob.class);
	@Autowired
	@Getter
	@Setter
	private VideoCollectService videoCollectService;
	@Autowired
	@Getter
	@Setter
	private CommentService commentService;
	@Autowired
	@Getter
	@Setter
	private VideoInfoService videoInfoService;
	@Autowired
	@Getter
	@Setter
	private VideoPropertyService videoPropertyService;
	@Autowired
	@Getter
	@Setter
	private CollectPlayInfoService collectPlayInfoService;
	@Autowired
	@Getter
	@Setter
	private CollectPropertyService collectPropertyService;
	@Autowired
	@Getter
	@Setter
	private VideoCollectHistoryService  videoCollectHistoryService;
	@Autowired
	@Getter
	@Setter
	private VideoPropertyHistoryService videoPropertyHistoryService;
	@Autowired
	@Getter
	@Setter
	private CollectAddressService collectAddressService;
	
	public void execute(){
		try {
			TaskJsonBean taskJsonBean = TaskMsgQueue.getTask();
			if(taskJsonBean == null){
				return;
			}
			int taskType = taskJsonBean.getTaskType();
			if(taskType == 0){
				String collectType = taskJsonBean.getType();
				ParseCollectList parseCollectList = new ParseCollectList();
				List<CollectAddress> tempList = new ArrayList<CollectAddress>();
				List<CollectAddress> collectAddressList = parseCollectList.getCollectList(collectType);
				for (CollectAddress collectAddress : collectAddressList) {
//					CollectAddress collectAddressTemp = new CollectAddress();
//					collectAddressTemp.setUrl(collectAddress.getUrl());
//					collectAddressTemp.setSite(5);
//					int t = collectAddressService.getCollectAddressCountByExample(collectAddressTemp);
//					if(t<1){
//						data.add(collectAddress);
//					}
					CollectAddressSearchBean collectAddressSearchBean = new CollectAddressSearchBean();
					collectAddressSearchBean.setUrl(collectAddress.getUrl());
					List<CollectAddress> countList =  collectAddressService.getCollectAddressByCondition(collectAddressSearchBean);
					
					int count = countList.size();
					
					if(count > 0){
						
					}else{
						tempList.add(collectAddress);
					}
				}
				collectAddressService.batchAddCollectAddress(tempList);
			}else if(taskType == 1){
				UrlBeanExt urlBeanExt = null;
				try {
					String url = taskJsonBean.getUrl();
					SohuCrawler crawler = new SohuCrawler();
					urlBeanExt = crawler.execute(url,taskType);
				} catch (Exception e) {
					log.error("网络异常",e);
				}
				
				if(urlBeanExt != null){
					//剧集信息入库
					VideoCollect videoCollect = urlBeanExt.getVideoCollect();
					VideoCollectSearchBean vcSearchBean = new VideoCollectSearchBean();
					vcSearchBean.setCollectId(videoCollect.getCollectId());
					vcSearchBean.setSite(ParseVideoInfoProperty.SITE_TYPE_SOHU);
					
					videoCollectService.removeVideoCollectByCondition(vcSearchBean);
					videoCollectService.addVideoCollect(videoCollect);
					
					//videoCollectHistory
					VideoCollectHistory videoCollectHistory = new VideoCollectHistory();
					BeanUtils.copyProperties(videoCollect, videoCollectHistory);
					videoCollectHistoryService.addVideoCollectHistory(videoCollectHistory);
				}

			}else if(taskType == 2){
				UrlBeanExt urlBeanExt = null;
				try {
					String url = taskJsonBean.getUrl();
					SohuCrawler crawler = new SohuCrawler();
					urlBeanExt = crawler.execute(url,taskType);
				} catch (Exception e) {
					log.error("网络异常",e);
				}
				
				if(urlBeanExt != null){
					
					//剧集信息入库
					VideoCollect videoCollect = urlBeanExt.getVideoCollect();
					
					VideoCollectSearchBean vcSearchBean = new VideoCollectSearchBean();
					vcSearchBean.setCollectId(videoCollect.getCollectId());
					vcSearchBean.setSite(ParseVideoInfoProperty.SITE_TYPE_SOHU);
					
					videoCollectService.removeVideoCollectByCondition(vcSearchBean);
					videoCollectService.addVideoCollect(videoCollect);
					
					//videoCollectHistory
					VideoCollectHistory videoCollectHistory = new VideoCollectHistory();
					BeanUtils.copyProperties(videoCollect, videoCollectHistory);
					videoCollectHistoryService.addVideoCollectHistory(videoCollectHistory);
					
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
					//VideoPropertyHistory
					List<VideoPropertyHistory> videoPropertyHistoryList = new ArrayList<VideoPropertyHistory>();
					for (VideoProperty videoProperty : videoPropertyList) {
						VideoPropertyHistory videoPropertyHistoryTemp = new VideoPropertyHistory();
						BeanUtils.copyProperties(videoProperty, videoPropertyHistoryTemp);
						videoPropertyHistoryList.add(videoPropertyHistoryTemp);
					}
					videoPropertyHistoryService.batchAddVideoPropertyHistory(videoPropertyHistoryList);
					
					//CollectPlayInfo
					List<CollectPlayInfo> collectPlayInfoList = videoCollectVO.getCollectPlayInfoList();
					List<CollectPlayInfo> collectPlayInfoListTemp = new ArrayList<CollectPlayInfo>();
					for(CollectPlayInfo collectPlayInfo : collectPlayInfoList){
						CollectPlayInfo collectPlayInfoTemp = new CollectPlayInfo();
						collectPlayInfoTemp.setCollectId(collectPlayInfo.getCollectId());
						collectPlayInfoTemp.setProvinceCode(collectPlayInfo.getProvinceCode());
						collectPlayInfoListTemp.add(collectPlayInfoTemp);
					}
					collectPlayInfoService.batchRemoveCollectPlayInfoByExample(collectPlayInfoListTemp);
					collectPlayInfoService.batchAddCollectPlayInfo(collectPlayInfoList);
					
					//CollectProperty
					List<CollectProperty> collectPropertyList = videoCollectVO.getCollectPropertyList();
					List<CollectProperty> collectPropertyListTemp = new ArrayList<CollectProperty>();
					for(CollectProperty collectProperty : collectPropertyList){
						CollectProperty collectPropertyTemp = new CollectProperty();
						collectPropertyTemp.setCollectId(collectProperty.getCollectId());
						collectPropertyTemp.setPropertyKey(collectProperty.getPropertyKey());
						collectPropertyListTemp.add(collectPropertyTemp);
					}
					collectPropertyService.batchRemoveCollectPropertyByExample(collectPropertyListTemp);
					collectPropertyService.batchAddCollectProperty(collectPropertyList);
					
					//Commment
					VideoCommentUserInfoVO videoCommentUserInfoVO = parseVideoInfoProperty.getCommentUserInfo(urlBeanExt);
					List<Comment> totalCommentListBefore = videoCommentUserInfoVO.getCommentList();
					
					//去重
					List<Comment> totalCommentList = ParseVideoInfoProperty.removeDuplicateComment(totalCommentListBefore);
					List<Comment> commentListTemp = new ArrayList<Comment>();
					
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
		} catch (Exception e) {
			log.error("TaskJob error!",e);
		}
		log.info("Oh,Yeah! Crawl and Parse Done!");
		//本次任务执行完毕，向服务端发送空闲消息
		MonitorClient monitorClient=BeanFactory.getBean("monitorClient",MonitorClient.class);
		monitorClient.sendModifyStatusMessage();
	}

}
