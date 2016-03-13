package cn.com.screendata.client.job;

import java.text.SimpleDateFormat;
import java.util.Date;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import cn.com.screendata.client.bean.HttpRequestInfo;
import cn.com.screendata.client.cache.HttpInfoQueue;

@Slf4j
public class RecordRequestJob {
	@Setter
	private String clientId;
	@Setter
	private String dataDir;
	@Setter
	private String time;

	public void execute() {
		HttpRequestInfo httpRequestInfo = HttpInfoQueue.getInfo();
		if (httpRequestInfo != null) {
			
		}
	}
}
