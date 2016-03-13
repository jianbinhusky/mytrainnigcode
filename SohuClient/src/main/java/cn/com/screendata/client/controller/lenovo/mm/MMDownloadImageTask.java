package cn.com.screendata.client.controller.lenovo.mm;

import cn.com.screendata.client.controller.util.FileUtil;

import java.util.concurrent.Callable;

/**
 * Created by hujb4 on 2016/3/12.
 */
public class MMDownloadImageTask implements Callable {
    private String imageURL;
    private String filename;
    private String savepath;

    public MMDownloadImageTask(String imageURL , String filename , String savepath){
        this.imageURL = imageURL;
        this.filename = filename;
        this.savepath = savepath;
    }
    @Override
    public Object call() throws Exception {
        FileUtil.downloadImage(imageURL,filename,savepath);
        System.out.println("download image --->" + imageURL);
        return null;
    }
}
