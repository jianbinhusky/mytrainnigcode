package cn.com.screendata.client.controller.lenovo.mm;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;

/**
 * Created by hujb4 on 2016/3/10.
 */
public class MMCrawler {

    public static final int PAGE_NO = 238;
    public static final String BASE_URL = "http://m.mm131.com/more.php?page=";

    public static List<String> crawl(){
        CompletionService<List<String>> crawlerPool = new ExecutorCompletionService<List<String>>(MMThreadPool.crawlPool);

        for (int i = 1; i < PAGE_NO; i++) {
            String url = BASE_URL + i;
            MMCrawlTask crawlerTask = new MMCrawlTask(url);
            crawlerPool.submit(crawlerTask);
        }
        List<String> totalPageList = new ArrayList<String>();
        try {
            for (int i = 1; i < PAGE_NO; i++) {
                List<String> subPageList = crawlerPool.take().get();
                totalPageList.addAll(subPageList);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        return totalPageList;
    }

    public static List<MMBean> parse(List<String> pageList){
        CompletionService<MMBean> parsePool = new ExecutorCompletionService<MMBean>(MMThreadPool.parsePool);
        List<MMBean> mmBeanList = new ArrayList<MMBean>();
        for (String pageURL : pageList) {
            MMParseTask parseTask = new MMParseTask(pageURL);
            parsePool.submit(parseTask);
        }
        for (int i = 0; i < pageList.size(); i++) {
            try {
                MMBean mmBean = parsePool.take().get();
                mmBeanList.add(mmBean);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }

        return mmBeanList;
    }

    public static void download(List<MMBean> mmBeanList){
        CompletionService downloadPool = new ExecutorCompletionService(MMThreadPool.downloadPool);
        for (MMBean mmBean : mmBeanList) {
            MMDownloadTask mmDownloadTask = new MMDownloadTask(mmBean);
            downloadPool.submit(mmDownloadTask);
        }
        for (int i = 0; i < mmBeanList.size(); i++) {
            try {
                downloadPool.take().get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args){
        long start = System.currentTimeMillis();
        List<String> pageList = crawl();
        List<MMBean> mmBeanList = parse(pageList);
        download(mmBeanList);
        long end = System.currentTimeMillis();
        System.out.println("MM crawl done~!" );
        System.out.println("Use time : " + ((end - start) / 1000) + " s " );
    }

}

