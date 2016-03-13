package cn.com.screendata.client.bean;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

public class CollectListVO {
	@Getter
	@Setter
	private List<CollectAddress> collectAddressList;
	@Getter
	@Setter
	private int pageCount;
}
