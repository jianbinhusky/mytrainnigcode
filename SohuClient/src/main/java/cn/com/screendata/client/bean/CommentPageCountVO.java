package cn.com.screendata.client.bean;

import lombok.Getter;
import lombok.Setter;

public class CommentPageCountVO {
	@Getter
	@Setter
	private VideoCommentUserInfoVO videoCommentUserInfoVO;
	@Getter
	@Setter
	private int pageCount;
	@Getter
	@Setter
	private int totlaCommentSize;

}
