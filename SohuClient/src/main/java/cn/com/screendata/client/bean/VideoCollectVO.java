package cn.com.screendata.client.bean;

import java.util.List;

import cn.com.screendata.client.bean.CollectPlayInfo;
import cn.com.screendata.client.bean.CollectProperty;
import lombok.Getter;
import lombok.Setter;

public class VideoCollectVO {
	@Getter
	@Setter
	private VideoInfoPropertyVO videoInfoPropertyVO;
	@Getter
	@Setter
	private List<CollectPlayInfo> collectPlayInfoList;
	@Getter
	@Setter
	private List<CollectProperty> collectPropertyList;
}
