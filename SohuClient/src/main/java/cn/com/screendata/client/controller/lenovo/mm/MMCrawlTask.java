package cn.com.screendata.client.controller.lenovo.mm;

import cn.com.screendata.client.controller.parser.HtmlDocUtil;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * Created by hujb4 on 2016/3/11.
 */
public class MMCrawlTask implements Callable<List<String>> {
    private String url;

    public MMCrawlTask(String url){
        this.url = url;
    }
    @Override
    public List<String> call() throws Exception {
        List<String> subPageList = new ArrayList<String>();
        Document document = HtmlDocUtil.getHtmlDocFromURL(url);
        Elements elements = document.getElementsByTag("article");
        for (Element element : elements) {
            String targetURL = element.select("div.post-content > a").attr("href");
            subPageList.add(targetURL);
            System.out.println("target url is ---->" + element.select("div.post-content > a").attr("href"));
        }
        return subPageList;
    }
}
