package com.xforceplus.wapp.modules.certification.entity;

import com.aisinopdf.text.pdf.S;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 
 * ****************************************************************************
 * 抵扣统计表实体
 *
 * @author(作者)：xuyongyun	
 * @date(创建日期)：2019年4月24日
 ******************************************************************************
 */
@Setter
@Getter
public class TDxDkCountLogEntity implements Serializable {
    
	private static final long serialVersionUID = 7667687516272734767L;

	/**
    * 税号
    */
    private String taxno;
    private String skssq;
    private String operaType;
    private String operaName;
    private String createDate;
}
