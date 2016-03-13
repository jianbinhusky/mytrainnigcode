package cn.com.screendata.client.bean;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

import cn.com.screendata.client.bean.VideoInfo;
import cn.com.screendata.client.bean.VideoProperty;

public class VideoInfoPropertyVO {
	@Getter
	@Setter
	List<VideoInfo> videoInfoList;
	@Getter
	@Setter
	List<VideoProperty> videoPropertyList;
}
