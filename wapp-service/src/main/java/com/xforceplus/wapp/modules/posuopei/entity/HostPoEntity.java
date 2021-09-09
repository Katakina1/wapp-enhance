package com.xforceplus.wapp.modules.posuopei.entity;

import com.xforceplus.wapp.modules.base.entity.BaseEntity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author raymond.yan
 */
public class HostPoEntity extends BaseEntity implements Serializable {


   
    /**
	 * 
	 */
	private static final long serialVersionUID = 1774497609732069635L;



	public HostPoEntity(){

    }

    

    private Long id;
   
    /**
     *匹配关联号
     */
    private  String matchno;


    
    private  String tractionIdSeq;

  

    public String getTractionIdSeq() {
        return tractionIdSeq;
    }

    public void setTractionIdSeq(String tractionIdSeq) {
        this.tractionIdSeq = tractionIdSeq;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

   
    public String getMatchno() {
        return matchno;
    }

    public void setMatchno(String matchno) {
        this.matchno = matchno;
    }

    
}
