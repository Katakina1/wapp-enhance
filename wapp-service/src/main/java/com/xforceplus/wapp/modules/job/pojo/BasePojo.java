package com.xforceplus.wapp.modules.job.pojo;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import java.io.Serializable;

/**
 *  * @Description 外层报文
 *  * @Package com.xforceplus.wapp.modules.job.pojo
 *  * @author fth
 *  * @date 2018年04月13日 上午9:56:32
 *  * @version 1.0
 */
public abstract class BasePojo implements Serializable {
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}
}
