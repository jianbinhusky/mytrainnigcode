package cn.com.screendata.client.controller.lenovo.mm;

import cn.com.screendata.client.controller.parser.HtmlDocUtil;
import org.jsoup.nodes.Document;

import java.util.concurrent.Callable;

/**
 * Created by hujb4 on 2016/3/11.
 */
public class MMParseTask implements Callable<MMBean> {
    private String url;

    public MMParseTask(String url){
        this.url = url;
    }

    @Override
    public MMBean call() throws Exception {
        Document document = HtmlDocUtil.getHtmlDocFromURL(url);
        String title = document.select("h2.mm-title").text();
        int slash = url.lastIndexOf("/");
        String id = url.substring(slash).replace("/","").replace(".html","");
        Integer pageTotal = Integer.valueOf(document.select("span.rw").text().split("/")[1].replace("页",""));
        String publishTime = document.select("div.post-data > span.post-meta").text().split(" ")[0];

        MMBean mmBean = new MMBean();
        mmBean.setId(id);
        mmBean.setTitle(title);
        mmBean.setPageTotal(pageTotal);
        mmBean.setPublishTime(publishTime);
        mmBean.setSourceURL(url);

        System.out.println("parse url --->" + url);

        return mmBean;
    }
}
