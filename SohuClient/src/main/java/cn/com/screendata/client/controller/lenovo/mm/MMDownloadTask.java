package cn.com.screendata.client.controller.lenovo.mm;

import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutorCompletionService;

/**
 * Created by hujb4 on 2016/3/12.
 */
public class MMDownloadTask implements Callable {
    private MMBean mmBean;

    public MMDownloadTask(MMBean mmBean){
        this.mmBean = mmBean;
    }

    @Override
    public Object call() throws Exception {
        CompletionService imagePool = new ExecutorCompletionService(MMThreadPool.imagePool);
        for (int i = 0; i <= mmBean.getPageTotal(); i++) {
            String imageURL = MMConstant.IMG_BASE_URL + mmBean.getId() + "/" + i + ".jpg";
            if(i == 0){
                imageURL = MMConstant.IMG_BASE_URL + mmBean.getId() + "/m.jpg";
            }
            String filename = mmBean.getId() + "_" + i + mmBean.getTitle() + ".jpg";
            int end = mmBean.getPublishTime().lastIndexOf("-");
            String month = mmBean.getPublishTime().substring(0,end);
            String savepath = MMConstant.SAVE_BASE_DIR + "\\" + "yyyy-MM" + "\\" + month;
            MMDownloadImageTask mmDownloadImageTask = new MMDownloadImageTask(imageURL,filename,savepath);
            imagePool.submit(mmDownloadImageTask);
        }
        for (int i = 0; i <= mmBean.getPageTotal(); i++) {
            imagePool.take().get();
        }
        return null;
    }
}
