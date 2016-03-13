import java.sql.SQLException;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import cn.com.screendata.client.bean.TaskJsonBean;
import cn.com.screendata.client.bean.VideoCollect;
import cn.com.screendata.client.cache.TaskMsgQueue;
import cn.com.screendata.client.dao.CommentDao;
import cn.com.screendata.client.dao.VideoCollectDao;
import cn.com.screendata.common.spring.BeanFactory;


public class Startup {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		ApplicationContext ctx  = new ClassPathXmlApplicationContext("applicationContext.xml");
		BeanFactory.ctx=ctx;
	}

}
