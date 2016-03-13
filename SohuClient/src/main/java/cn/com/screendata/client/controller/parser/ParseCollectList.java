package cn.com.screendata.client.controller.parser;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutorCompletionService;

import org.apache.log4j.Logger;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import cn.com.screendata.client.bean.CollectAddress;
import cn.com.screendata.client.bean.CollectListVO;
import cn.com.screendata.client.bean.VideoType;
import cn.com.screendata.client.controller.util.HttpRequestUtil;
import cn.com.screendata.client.thread.ParseThreadPool;
import cn.com.screendata.client.thread.ParseVideoCollectListTask;
import cn.com.screendata.common.util.StringUtil;

public class ParseCollectList {
	private final static Logger log = Logger.getLogger(ParseCollectList.class);
	public final static String TV_LIST_BASE_URL = "http://so.tv.sohu.com/list_p1101_p20_p3_p40_p5_p6_p77_p80_p9_2d1_p10";
	public final static String TV_LIST_CONNECTOR = "_p110.html";
	public final static String MOV_LIST_BASE_URL = "http://so.tv.sohu.com/list_p1100_p20_p3_p40_p5_p6_p77_p82_p9_2d1_p10";
	public final static String MOV_LIST_CONNECTOR = "_p11.html";
	public final static String ZY_LIST_BASE_URL = "http://so.tv.sohu.com/list_p1106_p20_p3_p4_p5_p6_p77_p8_p9_2d1_p10";
	public final static String ZY_LIST_CONNECTOR = "_p110.html";
	public final static int SITE_TYPE_SOHU = 5;
	
	public  List<CollectAddress> getCollectList(String collectType){
		List<CollectAddress> totalcollectAddressList = new ArrayList<CollectAddress>();
		
		CompletionService<CollectListVO> parseCollectListThread = 
				new ExecutorCompletionService<CollectListVO>(ParseThreadPool.parseVideoCollectListThreadPool);
		
		if(collectType.equals(VideoType.dianshiju)){
			String urlStrFir = ParseCollectList.TV_LIST_BASE_URL + 1 + ParseCollectList.TV_LIST_CONNECTOR;
			String responseHtmlStrFir = HttpRequestUtil.httpGetRequestForComment(urlStrFir, null);
			CollectListVO collectListVOFirst = ParseCollectList.parseOnePageCollectList(responseHtmlStrFir,collectType);
			totalcollectAddressList.addAll(collectListVOFirst.getCollectAddressList());
			int pageCount = collectListVOFirst.getPageCount();
			
			for(int i=2;i<=pageCount;i++){
				String urlStr = ParseCollectList.TV_LIST_BASE_URL + i + ParseCollectList.TV_LIST_CONNECTOR;
				ParseVideoCollectListTask parseCollectListTask = new ParseVideoCollectListTask(collectType,urlStr);
				parseCollectListThread.submit(parseCollectListTask);
			}
			for(int j=2;j<=pageCount;j++){
				try {
					CollectListVO collectListVO = parseCollectListThread.take().get();
					totalcollectAddressList.addAll(collectListVO.getCollectAddressList());
				} catch (Exception e) {
					log.error("线程回收异常",e);
				}
			}
		}else if(collectType.equals(VideoType.dianying)){
			String urlStrFir = ParseCollectList.MOV_LIST_BASE_URL + 1 + ParseCollectList.MOV_LIST_CONNECTOR;
			String responseHtmlStrFir = HttpRequestUtil.httpGetRequestForComment(urlStrFir, null);
			CollectListVO collectListVOFirst = ParseCollectList.parseOnePageCollectList(responseHtmlStrFir,collectType);
			totalcollectAddressList.addAll(collectListVOFirst.getCollectAddressList());
			int pageCount = collectListVOFirst.getPageCount();
			
			for(int i=2;i<=pageCount;i++){
				String urlStr = ParseCollectList.MOV_LIST_BASE_URL + i + ParseCollectList.MOV_LIST_CONNECTOR;
				ParseVideoCollectListTask parseCollectListTask = new ParseVideoCollectListTask(collectType,urlStr);
				parseCollectListThread.submit(parseCollectListTask);
			}
			for(int j=2;j<=pageCount;j++){
				try {
					CollectListVO collectListVO = parseCollectListThread.take().get();
					totalcollectAddressList.addAll(collectListVO.getCollectAddressList());
				} catch (Exception e) {
					log.error("线程回收异常",e);
				}
			}
		}else{
			String urlStrFir = ParseCollectList.ZY_LIST_BASE_URL + 1 + ParseCollectList.ZY_LIST_CONNECTOR;
			String responseHtmlStrFir = HttpRequestUtil.httpGetRequestForComment(urlStrFir, null);
			CollectListVO collectListVOFirst = ParseCollectList.parseOnePageCollectList(responseHtmlStrFir,collectType);
			totalcollectAddressList.addAll(collectListVOFirst.getCollectAddressList());
			int pageCount = collectListVOFirst.getPageCount();
			
			for(int i=2;i<=pageCount;i++){
				String urlStr = ParseCollectList.ZY_LIST_BASE_URL + i + ParseCollectList.ZY_LIST_CONNECTOR;
				ParseVideoCollectListTask parseCollectListTask = new ParseVideoCollectListTask(collectType,urlStr);
				parseCollectListThread.submit(parseCollectListTask);
			}
			for(int j=2;j<=pageCount;j++){
				try {
					CollectListVO collectListVO = parseCollectListThread.take().get();
					totalcollectAddressList.addAll(collectListVO.getCollectAddressList());
				} catch (Exception e) {
					log.error("线程回收异常",e);
				}
			}
		}
		
		return totalcollectAddressList;
	}
	
	public static CollectListVO parseOnePageCollectList(String responseHtmlStrFir,String collectType){
		CollectListVO collectListVO = new CollectListVO();
		Document doc = HtmlDocUtil.getHtmlDocFromStr(responseHtmlStrFir);
		//总页数
		int pageCount = -1;
		try {
			String pageCountStr = doc.select("div#contentList > div.page > div.num > a").last().html();
			pageCount = Integer.parseInt(pageCountStr);
		} catch (Exception e) {
			log.error("总页数获取失败",e);
		}
		
		List<CollectAddress> collectAddressList = new ArrayList<CollectAddress>();
		Elements eleUlList = doc.select("div#contentList > div.column-bd > ul");
		for (Element element : eleUlList) {
			Elements eleLiListElements = element.select("li");
			for (Element element2 : eleLiListElements) {
				CollectAddress collectAddress = new CollectAddress();
				String collectName = null;
				try {
					collectName = element2.select("div.show-txt > div.info > p.tit-p > a").attr("title");
				} catch (Exception e1) {
					collectName = null;
				}
				if(collectType.equals(VideoType.dianying)){
					String urlPlay = element2.select("div.show-txt > div.info > p.tit-p > a").attr("href");
					String responseHtmlStr = HttpRequestUtil.httpGetRequestForComment(urlPlay, null);
					Document docMov = HtmlDocUtil.getHtmlDocFromStr(responseHtmlStr);
					String url = null;
					try {
						url = docMov.select("div#crumbsBar > div.area > div.left > div.crumbs > a").last().attr("href");
					} catch (Exception e) {
						log.error("没有解析到电影剧集URL",e);
					}
					//电影中有些付费电影的URL并不符合解析要求的URL规则，这里先舍去了
					if(url != null && !url.equals("") && url.startsWith("http://tv.sohu.com/")){
						collectAddress.setCollectName(collectName);
						collectAddress.setUrl(url);
						collectAddress.setSite(ParseCollectList.SITE_TYPE_SOHU);
						collectAddress.setId(StringUtil.getUUID());
						collectAddress.setCollectType(collectType);
						collectAddress.setState(1);
						collectAddress.setTaskType(0);
						collectAddressList.add(collectAddress);
					}
				}else{
					String url = element2.select("div.show-txt > div.info > p.tit-p > a").attr("href");
					if(url != null && !url.equals("")){
						collectAddress.setCollectName(collectName);
						collectAddress.setUrl(url);
						collectAddress.setSite(ParseCollectList.SITE_TYPE_SOHU);
						collectAddress.setId(StringUtil.getUUID());
						collectAddress.setCollectType(collectType);
						collectAddress.setState(1);
						collectAddress.setTaskType(0);
						collectAddressList.add(collectAddress);
					}
				}
			}
		}
		collectListVO.setCollectAddressList(collectAddressList);
		collectListVO.setPageCount(pageCount);
		
		return collectListVO;
	}

}
