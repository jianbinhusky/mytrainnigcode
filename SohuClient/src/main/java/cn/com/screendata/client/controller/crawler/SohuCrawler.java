package cn.com.screendata.client.controller.crawler;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutorCompletionService;

import lombok.extern.slf4j.Slf4j;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import cn.com.screendata.client.bean.UrlBean;
import cn.com.screendata.client.bean.UrlBeanExt;
import cn.com.screendata.client.bean.VideoCollect;
import cn.com.screendata.client.bean.VideoType;
import cn.com.screendata.client.controller.parser.ParseVideoInfoProperty;
import cn.com.screendata.client.controller.util.HttpRequestUtil;
import cn.com.screendata.client.thread.CrawlerTask;
import cn.com.screendata.client.thread.ParseThreadPool;
import cn.com.screendata.common.util.DateUtil;

@Slf4j 
public class SohuCrawler {
	
	public final static String URL_LIST_BASE_API = "http://pl.hd.sohu.com/videolist?playlistid=";
	public final static String PLAY_LIST_YEAR_CONNECTOR = "&year=";
	public final static int PLAY_LIST_YEAR = 2014;
	public final static String PLAY_LIST_MONTH = "&month=12";
	public final static String PLAY_LIST_PAGE_SIZE = "pagesize=100";
	public final static String PLAY_COUNT_BASE_URL = "http://count.vrs.sohu.com/count/query_album.action?type=2&albumId=";
	public final static String ZS_BASE_URL = "http://index.tv.sohu.com/index/switch-aid/";
	public final static String ZS_EXTENTION = ".html";
	public final static String ZY_VOTE_BASE_URL = "http://vote.biz.itc.cn/count_v92_t1_i";
	public final static String TV_VOTE_BASE_URL = "http://vote.biz.itc.cn/count_v77_t2_i";
	public final static String MOV_VOTE_BASE_URL = "http://vote.biz.itc.cn/count_v77_t1_i";
	public final static String VOTE_EXTENSION = "_b_c.json?_=1397012017697";
	
	
	public UrlBeanExt execute(String url,int taskType){
		
		UrlBeanExt urlBeanExt = null;
		
		//第一次请求获取url列表
		String html = HttpRequestUtil.httpGetRequest(url, null);
		
		Document document = Jsoup.parse(html);
		
		try {
			String head1 = document.select(" h1.color3 > a").html();
			
			if(head1.contains(VideoType.dianshiju)){
				urlBeanExt = parseDianshiju(document, url,html,taskType);
			}else if(head1.contains(VideoType.dianying)){
				urlBeanExt = parseDianying(document, url,html);
			}else if(head1.contains(VideoType.zongyi)){
				urlBeanExt = parseZongyi(document, url,html,taskType);
			}else{
				return null;
			}
		} catch (Exception e1) {
			log.error("解析剧集部分有异常eg：topicId获取不到",e1);
		}
		
		//如果taskType为1减少不必要的的请求
		if(taskType == 2){
			//第二次请求获取html页面
			List<UrlBean> list = urlBeanExt.getUrlList();
			
			CompletionService<UrlBean> crawlerPool = new ExecutorCompletionService<UrlBean>(ParseThreadPool.crawlerPool);
			
			for(UrlBean urlBean:list){
				CrawlerTask crawlerTask = new CrawlerTask(urlBean);
				crawlerPool.submit(crawlerTask);
			}
			
			List<UrlBean> resultList = new ArrayList<UrlBean>();
			
			try {
				for(int i=0;i<list.size();i++){
					UrlBean urlBean = crawlerPool.take().get();
					resultList.add(urlBean);
				}
			} catch (Exception e) {
				log.error("线程回收数据出错",e);
			}
			urlBeanExt.setUrlList(resultList);
		}
		
		return urlBeanExt;
	}
	
	private UrlBeanExt parseDianshiju(Document document,String url,String htmlStr,int taskType){
		
		UrlBeanExt urlBeanExt = new UrlBeanExt();
		//获取并装配UrlBean
		UrlBean urlBeanForIds = SohuCrawler.getIds(htmlStr);
		String collectId = urlBeanForIds.getCollectId();
		
		String requestStrForUrlList = SohuCrawler.URL_LIST_BASE_API + collectId;
		String responseStr = HttpRequestUtil.httpGetRequest(requestStrForUrlList, null);
		JSONObject root  = JSONObject.parseObject(responseStr);
		JSONArray videos = root.getJSONArray("videos");
		
		List<UrlBean> list = new ArrayList<UrlBean>();
		for(int i=0;i<videos.size();i++){
			String urlStr = videos.getJSONObject(i).getString("pageUrl");
			long videoId = videos.getJSONObject(i).getLongValue("vid");
			UrlBean urlBean = new UrlBean();
			//如果taskType为1减少不必要的请求
			if(taskType == 2){
				long topicId = SohuCrawler.getTopicId(videoId);
				urlBean.setTopId(String.valueOf(topicId));
			}
			urlBean.setType(VideoType.dianshiju);
			urlBean.setUrl(urlStr);
			urlBean.setVideoId(String.valueOf(videoId));
			urlBean.setCollectId(collectId);
			list.add(urlBean);
		}
		
		//获取剧集信息
		String createTime = DateUtil.getCurrentDateTime();
		
		//剧集名称
		String collectName = null;
		String playCount = null;
		try {
			collectName = document.select("div.right > div.blockRA > h2 > span").html().trim();
			
			//总播放量
			String requestCountStr = SohuCrawler.PLAY_COUNT_BASE_URL + collectId;
			String responseCountStr = HttpRequestUtil.httpGetRequest(requestCountStr, null);
			playCount = responseCountStr.replace("var count=", "").replace(";", "").trim();
		} catch (Exception e) {
			log.error("剧集名称或其总播放量未解析到，此条记录舍去",e);
			return null;
		}
		
		//导演
		String director = "";
		try {
			director = document.select("div.right > div.blockRA > div.cont > p").get(0).text();
			director = director.replaceAll("&middot;",".").replace("导演：", "").replaceAll("/", ",").trim();
			if(director.length() > 50){
				String temp[] = director.split("/");
				int i = 0;
				director = "";
				while(director.length() < 50){
					if(director.length() == 0){
						director += temp[i];
					}else{
						director += "," + temp[i];
					}
					i++;
				}
			}
		} catch (Exception e) {
			director = null;
		}
		
		//主演
		String actor = "";
		try {
			actor = document.select("div.right > div.blockRA > div.cont > p").get(1).text();
			actor = actor.replaceAll("&middot;",".").replace("主演：", "").replaceAll("/", ",").trim();
			if(actor.length() > 50){
				String temp[] = actor.split(",");
				int i = 0;
				actor = "";
				while(actor.length() < 50){
					if(actor.length() == 0){
						actor += temp[i];
					}else{
						actor += "," + temp[i];
					}
					i++;
				}
			}
		} catch (Exception e) {
			 actor = null;
		}
		//标签 地区
		String tag = "";
		try {
			tag = document.select("div.right > div.blockRA > div.cont > p").last().text();
			tag = tag.replace("类型： ", "").replaceAll("/", ",").trim();
		} catch (Exception e) {
			 tag = null;
		}
		//年代
		String year = "";
		try {
			year = document.select("div.right > div.blockRA > div.cont > p").get(2).getElementsByTag("a").html();
			year = year.replace("年", "").trim();
		} catch (Exception e) {
			year = null;
		}
		//类型
		String type = "";
		try {
			type = document.select(" h1.color3 > a").html().trim();
		} catch (Exception e) {
			type = null;
		}

		//简介
		String desc = "";
		try {
			desc = document.select("div.right > div.blockRA > div.cont > div.d1").text();
			desc = desc.replace("全部剧情>>", "").trim();
			if(desc.length() > 503){
				desc = desc.substring(0, 502);
			}
		} catch (Exception e) {
			desc = null;
		}
		
		//指数url
		String zsUrl = SohuCrawler.ZS_BASE_URL + collectId + SohuCrawler.ZS_EXTENTION;
		
		/**
		 * 4月9日新添字段
		 * 评分
		 */
		//评分
		BigDecimal voteScoreAfter = null;
		try {
			String voteUrl = SohuCrawler.TV_VOTE_BASE_URL + collectId + SohuCrawler.VOTE_EXTENSION;
			String responseVoteStr = HttpRequestUtil.httpGetRequestForComment(voteUrl, null);
			String jsonStr = responseVoteStr.substring(responseVoteStr.indexOf("{"));
			JSONObject jsonRoot = JSONObject.parseObject(jsonStr);
			double voteCount = jsonRoot.getDoubleValue("votecount_77");
			double totalSorce = jsonRoot.getDoubleValue("totalScore_77");
			double voteScoreBefore = totalSorce / voteCount;
			voteScoreAfter = new BigDecimal(voteScoreBefore);   
			voteScoreAfter = voteScoreAfter.setScale(1,BigDecimal.ROUND_HALF_UP);
		} catch (Exception e) {
			voteScoreAfter = null;
		}
		
		VideoCollect videoCollect = new VideoCollect();
		videoCollect.setCollectId(collectId);
		videoCollect.setSite(ParseVideoInfoProperty.SITE_TYPE_SOHU);
		videoCollect.setPartCount(list.size());
		videoCollect.setCreateTime(createTime);
		videoCollect.setActor(actor);
		videoCollect.setCollectTag(tag);
		videoCollect.setDirector(director);
		videoCollect.setCollectName(collectName);
		videoCollect.setYear(Integer.parseInt(year));
		videoCollect.setCollectCategory(type);
		videoCollect.setCollectDesc(desc);
		videoCollect.setPlayCount(Long.parseLong(playCount));
		videoCollect.setScore(voteScoreAfter.doubleValue());
		
		urlBeanExt.setUrlList(list);
		urlBeanExt.setVideoCollect(videoCollect);
		urlBeanExt.setZsUrl(zsUrl);
		
		return urlBeanExt;
	}
	
	private UrlBeanExt parseZongyi(Document document,String url,String htmlStr,int taskType){
		
		UrlBeanExt urlBeanExt = new UrlBeanExt();
		
		UrlBean urlBeanForIds = SohuCrawler.getIds(htmlStr);
		String collectId = urlBeanForIds.getCollectId();
		
		//年代,取列表的前两年
		List<String> yearList = new ArrayList<String>();
		Elements eleYear = document.select("ul#zy_y > li");
		if(eleYear.size()>2){
			for(int y=1;y<=2;y++){
				String yearTemp = eleYear.get(y).getElementsByTag("a").html();
				yearList.add(yearTemp);
			}
		}else{
			String year1 = document.select("ul#zy_y > li").get(1).getElementsByTag("a").html();
			yearList.add(year1);
		}
		List<UrlBean> listTotal = new ArrayList<UrlBean>();
		for (String yearStr : yearList) {
			String requestStrForUrlList = SohuCrawler.URL_LIST_BASE_API + collectId 
					+ SohuCrawler.PLAY_LIST_YEAR_CONNECTOR + yearStr;
			String responseStr = HttpRequestUtil.httpGetRequest(requestStrForUrlList, null);
			JSONObject root  = JSONObject.parseObject(responseStr);
			JSONArray videos = root.getJSONArray("videos");

			List<UrlBean> list = new ArrayList<UrlBean>();
			for(int i=0;i<videos.size();i++){
				String urlStr = videos.getJSONObject(i).getString("pageUrl");
				long videoId = videos.getJSONObject(i).getLongValue("vid");
				UrlBean urlBean = new UrlBean();
				//如果taskType为1减少不必要的请求(taskType只可能为1或2)
				if(taskType == 2){
					long topicId = SohuCrawler.getTopicId(videoId);
					urlBean.setTopId(String.valueOf(topicId));
				}
				urlBean.setType(VideoType.zongyi);
				urlBean.setUrl(urlStr);
				urlBean.setVideoId(String.valueOf(videoId));
				urlBean.setCollectId(collectId);
				list.add(urlBean);
			}
			listTotal.addAll(list);
		}
		//获取剧集信息
		String createTime = DateUtil.getCurrentDateTime();
		
		//剧集名称
		String collectName = null;
		String playCount = null;
		try {
			collectName = document.select("div.right > div.blockRA > h2 > span").html().trim();
			//总播放量
			String requestCountStr = SohuCrawler.PLAY_COUNT_BASE_URL + collectId;
			String responseCountStr = HttpRequestUtil.httpGetRequest(requestCountStr, null);
			playCount = responseCountStr.replace("var count=", "").replace(";", "").trim();
		} catch (Exception e1) {
			log.error("剧集名称或者播放量解析不到，该条记录舍去",e1);
			return null;
		}
		
		//导演
		String director = "";
		try {
			director = document.select("div.right > div.blockRA > div.cont > p").first().text();
			director = director.replaceAll("&middot;",".").replace("主持人： ", "").replaceAll("/", ",").trim();
			if(director.length() > 50){
				String temp[] = director.split(",");
				int i = 0;
				director = "";
				while(director.length() < 50){
					if(director.length() == 0){
						director += temp[i];
					}else{
						director += "," + temp[i];
					}
					i++;
				}
			}
		} catch (Exception e1) {
			director = null;
		}
		
		//主演
		String actor = director;
		//标签 地区
		String tag = "";
		try {
			String area = document.select("div.right > div.blockRA > div.cont > p").get(2).text();
			area = area.replace("地区：", "").trim();
			String label = document.select("div.right > div.blockRA > div.cont > p").last().text();
			label = label.replace("类型：", "").replaceAll("/", ",").trim();
			tag = area + "," + label;
		} catch (Exception e1) {
			tag = null;
		}
		//年代
		String year = "";
		try {
			year = document.select("ul#zy_y > li").get(1).getElementsByTag("a").html().trim();
		} catch (Exception e1) {
			year = null;
		}

		//类型
		String type = "";
		try {
			type = document.select(" h1.color3 > a").html().trim();
		} catch (Exception e1) {
			type = null;
		}

		//简介
		String desc = "";
		try {
			desc = document.select("div.right > div.blockRA > div.cont > div.d1").text();
			desc = desc.replace("全部剧情>>", "").trim();
			if(desc.length() > 503){
				desc = desc.substring(0, 502);
			}
		} catch (Exception e1) {
			desc = null;
		}
		
		//指数url
		String zsUrl = SohuCrawler.ZS_BASE_URL + collectId + SohuCrawler.ZS_EXTENTION;
		
		/**
		 * 4月9日新添字段
		 * 评分
		 */
		//评分
		BigDecimal voteScoreAfter = null;
		try {
			String voteUrl = SohuCrawler.ZY_VOTE_BASE_URL + collectId + SohuCrawler.VOTE_EXTENSION;
			String responseVoteStr = HttpRequestUtil.httpGetRequestForComment(voteUrl, null);
			String jsonStr = responseVoteStr.substring(responseVoteStr.indexOf("{"));
			JSONObject jsonRoot = JSONObject.parseObject(jsonStr);
			double voteCount = jsonRoot.getDoubleValue("votecount_92");
			double totalSorce = jsonRoot.getDoubleValue("totalScore_92");
			double voteScoreBefore = totalSorce / voteCount;
			voteScoreAfter = new BigDecimal(voteScoreBefore);   
			voteScoreAfter = voteScoreAfter.setScale(1,BigDecimal.ROUND_HALF_UP);
		} catch (Exception e) {
			 voteScoreAfter = null;
		}

		
		VideoCollect videoCollect = new VideoCollect();
		videoCollect.setCollectId(collectId);
		videoCollect.setSite(ParseVideoInfoProperty.SITE_TYPE_SOHU);
		videoCollect.setPartCount(listTotal.size());
		videoCollect.setCreateTime(createTime);
		videoCollect.setActor(actor);
		videoCollect.setCollectTag(tag);
		videoCollect.setDirector(director);
		videoCollect.setCollectName(collectName);
		videoCollect.setYear(Integer.parseInt(year));
		videoCollect.setCollectCategory(type);
		videoCollect.setCollectDesc(desc);
		videoCollect.setPlayCount(Long.parseLong(playCount));
		videoCollect.setScore(voteScoreAfter.doubleValue());

		urlBeanExt.setUrlList(listTotal);
		urlBeanExt.setVideoCollect(videoCollect);
		urlBeanExt.setZsUrl(zsUrl);
		
		return urlBeanExt;
	}
	
	private UrlBeanExt parseDianying(Document document,String url,String htmlStr){
		UrlBeanExt urlBeanExt = new UrlBeanExt();
		List<UrlBean> list = new ArrayList<UrlBean>();	
		UrlBean urlBeanForIds = SohuCrawler.getIds(htmlStr);
		String collectId = urlBeanForIds.getCollectId();
		String videoId = urlBeanForIds.getVideoId();
		String topId = urlBeanForIds.getTopId();
		//获取电影url
		String movieUrl = null;
		try {
			movieUrl = document.select("div#picFocus > a").attr("href");
			if(movieUrl == null){
				return null;
			}
		} catch (Exception e) {
			log.error("电影剧集URL未解析到，此条记录舍去",e);
		}
		
		UrlBean urlBean = new UrlBean();
		urlBean.setCollectId(collectId);
		urlBean.setType(VideoType.dianying);
		urlBean.setUrl(movieUrl);
		urlBean.setVideoId(videoId);
		urlBean.setTopId(topId);
		
		list.add(urlBean);
		
		//获取剧集信息
		String createTime = DateUtil.getCurrentDateTime();
		
		//剧集名称
		String collectName = "";
		String playCountStr = "";
		long playCount = -1;
		try {
			collectName = document.select("div#blockA > div.tab1 > ul > li.tk1 > a").html().trim();
			
			//总播放量
			String requestCountStr = ParseVideoInfoProperty.COUNT_BASE_URL + ParseVideoInfoProperty.COUNT_VIDEOID_CONNECTOR + videoId
					+ ParseVideoInfoProperty.COUNT_TVID_CONNECTOR + ParseVideoInfoProperty.COUNT_TVID + ParseVideoInfoProperty.COUNT_PLAYLISTID_CONNECTOR 
					+ collectId + ParseVideoInfoProperty.COUNT_OTHER_CONNECTOR;
			String responseCountStr = HttpRequestUtil.httpGetRequest(requestCountStr, null);
			playCountStr = responseCountStr.replace("var count=", "").trim();
			playCount = Long.parseLong(playCountStr);
		} catch (Exception e) {
			log.error("电影剧集播放量或者名称未解析到，该条记录 舍去");
			playCount = (Long) null;
		}
		
		//导演
		String director = "";
		try {
			director = document.select("div#allist > div.yzp > div.d4 > div.dCont > ul > li > p.p0 > a").text();
			director = director.replaceAll("&middot;",".").replaceAll("/", ",").trim();
			if(director.length() > 50){
				String temp[] = director.split(",");
				int i = 0;
				director = "";
				while(director.length() < 50){
					if(director.length() == 0){
						director += temp[i];
					}else{
						director += "," + temp[i];
					}
					i++;
				}
			}
		} catch (Exception e) {
			director = null;
		}
		//主演
		String actor = "";
		try {
			actor = document.select("div#allist > div.yzp > div.d4 > div.dCont > ul > li > p.p1").first().text();
			actor = actor.replaceAll("&middot;",".").replace("主演： ", "").replaceAll("/", ",").trim();
		} catch (Exception e) {
			actor = null;
		}
		//标签 地区
		String tag = "";
		try {
			tag = document.select("div#allist > div.yzp > div.d4 > div.dCont > ul > li > p.p1").last().text();
			tag = tag.replace("类型：", "").replaceAll("/", ",").trim();
		} catch (Exception e) {
			tag = null;
		}
		
		//类型  年代
		String year = "";
		try {
			year = document.select("div#allist > div.yzp > div.d4 > div.dCont > ul > li > p.p0 > span.color3 > a").html();
			year = year.replace("年", "").trim();
		} catch (Exception e) {
			year = null;
		}
		
		//类型
		String type = "";
		try {
			type = document.select(" h1.color3 > a").html().trim();
		} catch (Exception e) {
			type = null;
		}

		//简介
		String desc = "";
		try {
			desc = document.select("div#allist > div.yzp > div.d4 > div.dCont > p > span.color3").text().trim();
			if(desc.length() > 503){
				desc = desc.substring(0, 502);
			}
		} catch (Exception e) {
			desc = null;
		}
		
		/**
		 * 4月9日新添字段
		 * 评分
		 */
		//评分
		BigDecimal voteScoreAfter = null;
		try {
			String voteUrl = SohuCrawler.MOV_VOTE_BASE_URL + collectId + SohuCrawler.VOTE_EXTENSION;
			String responseVoteStr = HttpRequestUtil.httpGetRequestForComment(voteUrl, null);
			String jsonStr = responseVoteStr.substring(responseVoteStr.indexOf("{"));
			JSONObject jsonRoot = JSONObject.parseObject(jsonStr);
			double voteCount = jsonRoot.getDoubleValue("votecount_77");
			double totalSorce = jsonRoot.getDoubleValue("totalScore_77");
			double voteScoreBefore = totalSorce / voteCount;
			voteScoreAfter = new BigDecimal(voteScoreBefore);   
			voteScoreAfter = voteScoreAfter.setScale(1,BigDecimal.ROUND_HALF_UP);
		} catch (Exception e) {
			voteScoreAfter = null;
		}
		
		VideoCollect videoCollect = new VideoCollect();
		videoCollect.setCollectId(collectId);
		videoCollect.setSite(ParseVideoInfoProperty.SITE_TYPE_SOHU);
		videoCollect.setPartCount(1);
		videoCollect.setCreateTime(createTime);
		videoCollect.setActor(actor);
		videoCollect.setCollectTag(tag);
		videoCollect.setDirector(director);
		videoCollect.setCollectName(collectName);
		videoCollect.setYear(Integer.parseInt(year));
		videoCollect.setCollectCategory(type);
		videoCollect.setCollectDesc(desc);
		videoCollect.setPlayCount(playCount);
		videoCollect.setScore(voteScoreAfter.doubleValue());
		
		urlBeanExt.setUrlList(list);
		urlBeanExt.setVideoCollect(videoCollect);
		
		return urlBeanExt;
	}
	
	/**
	 * get collectId
	 * @param urlStr
	 * @return
	 */
	public static UrlBean getIds(String htmlStr){
		UrlBean urlBean = new UrlBean();
		//collectId
		String collectIdStr = null;
		try {
			int lastOccurPlaylistId = htmlStr.lastIndexOf("PLAYLIST_ID=");
			String childHtmlStr = htmlStr.substring(lastOccurPlaylistId);
			int endChild = childHtmlStr.indexOf(";");  
			collectIdStr = childHtmlStr.substring(0, endChild);
			collectIdStr = collectIdStr.replaceAll("\"", "").replace("PLAYLIST_ID=", "").trim();
		} catch (Exception e) {
			log.error("剧集Id未解析到，该条记录舍去",e);
		}
		//videoId
		String vidStr = null;
		try {
			int firstOccurVid = htmlStr.indexOf("vid=");
			String childHtmlVid = htmlStr.substring(firstOccurVid);
			int endChildVid = childHtmlVid.indexOf(";");
			vidStr = childHtmlVid.substring(0, endChildVid);
			vidStr = vidStr.replaceAll("\"", "").replace("vid=", "").trim();
		} catch (Exception e) {
			log.error("videoid未解析到，该条记录舍去",e);
		}
		
		//topId
		long topId = 0;
		try {
			String requestUrlStr = ParseVideoInfoProperty.COMMENT_TOPIC_BASE_URL + vidStr;
			String responseStr = HttpRequestUtil.httpGetRequestForComment(requestUrlStr, null);
			JSONObject root = JSONObject.parseObject(responseStr);
			topId = root.getLongValue("topic_id");
		} catch (Exception e) {
			log.error("topicId未解析到，该条记录舍去",e);
		}
		
		urlBean.setTopId(String.valueOf(topId));
		urlBean.setVideoId(vidStr);
		urlBean.setCollectId(collectIdStr);
		
		return urlBean;
	}

	public static long getTopicId(long videoId){
		long topicId = 0;
		try {
			String requestUrlStr = ParseVideoInfoProperty.COMMENT_TOPIC_BASE_URL + videoId;
			String responseStr = HttpRequestUtil.httpGetRequestForComment(requestUrlStr, null);
			JSONObject root = JSONObject.parseObject(responseStr);
			topicId = root.getLongValue("topic_id");
		} catch (Exception e) {
			log.error("topicId获取不到",e);
		}
		return topicId;
	}
}
