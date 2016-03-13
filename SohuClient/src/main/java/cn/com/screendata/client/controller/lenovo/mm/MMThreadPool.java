package cn.com.screendata.client.controller.lenovo.mm;

import cn.com.screendata.client.controller.parser.HtmlDocUtil;
import org.jsoup.nodes.Document;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by hujb4 on 2016/3/11.
 */
public class MMThreadPool {
    public static final ExecutorService crawlPool = Executors.newFixedThreadPool(200);
    public static final ExecutorService parsePool = Executors.newFixedThreadPool(1000);
    public static final ExecutorService downloadPool = Executors.newFixedThreadPool(1000);
    public static final ExecutorService imagePool = Executors.newFixedThreadPool(50);

    public static void main(String[] args) {
        String url = "http://m.mm131.com/xinggan/2374.html";
        int slash = url.lastIndexOf("/");
        System.out.println(slash);
        System.out.println(url.substring(slash).replace("/","").replace(".html",""));

        Document document = HtmlDocUtil.getHtmlDocFromURL(url);
        String title = document.select("h2.mm-title").text();
        System.out.println(document.select("span.rw").text());
        Integer pageTotal = Integer.valueOf(document.select("span.rw").text().split("/")[1].replace("页",""));
        System.out.println(pageTotal);
        System.out.println(document.select("div.post-data > span.post-meta").text().split(" ")[0]);
        String test = "2016-02-04";
        int end = test.lastIndexOf("-");
        System.out.println(test.substring(0,end));
    }
}
