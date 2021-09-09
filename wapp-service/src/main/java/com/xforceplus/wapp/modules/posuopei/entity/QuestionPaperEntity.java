package com.xforceplus.wapp.modules.posuopei.entity;

import com.aisinopdf.text.pdf.S;
import com.xforceplus.wapp.modules.base.entity.BaseEntity;
import com.xforceplus.wapp.modules.cost.entity.SettlementFileEntity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @author raymond.yan
 */
public class QuestionPaperEntity extends BaseEntity implements Serializable {
    private static final long serialVersionUID = -3767718186531999338L;
    private String rownumber;
    private  Integer id;
    private String partition;
    private String purchaser;
    private String jvcode;
    private String city;
    private String cityCode;
    private String usercode;
    private String username;
    private String telephone;
    private String department;
    private String invoiceNo;
    private String invoiceDate;
    private String questionType;
    private BigDecimal totalAmount;
    private String problemCause;
    private String description;
    private String checkstatus;
    private String unPassReason;
    private List<PoQuestionEntity> poList;
    private List<PoDiscountQuestionEntity> poDiscountList;
    private List<ClaimtQuestionEntity> claimList;
    private List<CountQuestionEntity> countList;
    private List<SettlementFileEntity> fileList;
    private List<OtherQuestionEntity> otherList;
    private String src;
    private String checkDate;
    private String createdDate;
    private String rejectDate;
    private  String replyDate;
    private  String storeNbr;
    private String problemStream;

    public List<OtherQuestionEntity> getOtherList() {
        return otherList;
    }

    public void setOtherList(List<OtherQuestionEntity> otherList) {
        this.otherList = otherList;
    }

    public List<PoDiscountQuestionEntity> getPoDiscountList() {
        return poDiscountList;
    }

    public void setPoDiscountList(List<PoDiscountQuestionEntity> poDiscountList) {
        this.poDiscountList = poDiscountList;
    }

    public String getStoreNbr() {
        return storeNbr;
    }

    public void setStoreNbr(String storeNbr) {
        this.storeNbr = storeNbr;
    }

    public String getCheckDate() {
        return checkDate;
    }

    public void setCheckDate(String checkDate) {
        this.checkDate = checkDate;
    }

    public QuestionPaperEntity() {
		super();
		// TODO Auto-generated constructor stub
	}

	public QuestionPaperEntity(Integer id, String partition, String purchaser,
			String jvcode, String city, String usercode, String username,
			String telephone, String department, String invoiceNo,
			String invoiceDate, String questionType, BigDecimal totalAmount,
			String problemCause, String description, String checkstatus,
			String unPassReason, List<PoQuestionEntity> poList,
			List<ClaimtQuestionEntity> claimList,
			List<CountQuestionEntity> countList,
			List<SettlementFileEntity> fileList) {
		super();
		this.id = id;
		this.partition = partition;
		this.purchaser = purchaser;
		this.jvcode = jvcode;
		this.city = city;
		this.usercode = usercode;
		this.username = username;
		this.telephone = telephone;
		this.department = department;
		this.invoiceNo = invoiceNo;
		this.invoiceDate = invoiceDate;
		this.questionType = questionType;
		this.totalAmount = totalAmount;
		this.problemCause = problemCause;
		this.description = description;
		this.checkstatus = checkstatus;
		this.unPassReason = unPassReason;
		this.poList = poList;
		this.claimList = claimList;
		this.countList = countList;
		this.fileList = fileList;
	}

	public QuestionPaperEntity(Integer id, String partition, String purchaser,
			String jvcode, String city, String usercode, String username,
			String telephone, String department, String invoiceNo,
			String invoiceDate, String questionType, BigDecimal totalAmount,
			String problemCause, String description, String checkstatus,
			String unPassReason, List<PoQuestionEntity> poList,
			List<ClaimtQuestionEntity> claimList,
			List<CountQuestionEntity> countList,
			List<SettlementFileEntity> fileList, String src) {
		super();
		this.id = id;
		this.partition = partition;
		this.purchaser = purchaser;
		this.jvcode = jvcode;
		this.city = city;
		this.usercode = usercode;
		this.username = username;
		this.telephone = telephone;
		this.department = department;
		this.invoiceNo = invoiceNo;
		this.invoiceDate = invoiceDate;
		this.questionType = questionType;
		this.totalAmount = totalAmount;
		this.problemCause = problemCause;
		this.description = description;
		this.checkstatus = checkstatus;
		this.unPassReason = unPassReason;
		this.poList = poList;
		this.claimList = claimList;
		this.countList = countList;
		this.fileList = fileList;
		this.src = src;
	}

	public String getSrc() {
		return src;
	}

	public void setSrc(String src) {
		this.src = src;
	}

	public Integer getId() {
        return id;
    }

    public String getUnPassReason() {
        return unPassReason;
    }

    public void setUnPassReason(String unPassReason) {
        this.unPassReason = unPassReason;
    }

    public String getCheckstatus() {
        return checkstatus;
    }

    public void setCheckstatus(String checkstatus) {
        this.checkstatus = checkstatus;
    }

    public List<SettlementFileEntity> getFileList() {
        return fileList;
    }

    public void setFileList(List<SettlementFileEntity> fileList) {
        this.fileList = fileList;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getPartition() {
        return partition;
    }

    public void setPartition(String partition) {
        this.partition = partition;
    }

    public String getPurchaser() {
        return purchaser;
    }

    public void setPurchaser(String purchaser) {
        this.purchaser = purchaser;
    }

    public String getJvcode() {
        return jvcode;
    }

    public void setJvcode(String jvcode) {
        this.jvcode = jvcode;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getUsercode() {
        return usercode;
    }

    public void setUsercode(String usercode) {
        this.usercode = usercode;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getInvoiceNo() {
        return invoiceNo;
    }

    public void setInvoiceNo(String invoiceNo) {
        this.invoiceNo = invoiceNo;
    }

    public String getInvoiceDate() {
        return invoiceDate;
    }

    public void setInvoiceDate(String invoiceDate) {
        this.invoiceDate = invoiceDate;
    }

    public String getQuestionType() {
        return questionType;
    }

    public void setQuestionType(String questionType) {
        this.questionType = questionType;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getProblemCause() {
        return problemCause;
    }

    public void setProblemCause(String problemCause) {
        this.problemCause = problemCause;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<PoQuestionEntity> getPoList() {
        return poList;
    }

    public void setPoList(List<PoQuestionEntity> poList) {
        this.poList = poList;
    }

    public List<ClaimtQuestionEntity> getClaimList() {
        return claimList;
    }

    public void setClaimList(List<ClaimtQuestionEntity> claimList) {
        this.claimList = claimList;
    }

    public List<CountQuestionEntity> getCountList() {
        return countList;
    }

    public void setCountList(List<CountQuestionEntity> countList) {
        this.countList = countList;
    }
    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }
    public String getReplyDate() {
        return replyDate;
    }

    public void setReplyDate(String replyDate) {
        this.replyDate = replyDate;
    }

    public String getCityCode() {
        return cityCode;
    }

    public void setCityCode(String cityCode) {
        this.cityCode = cityCode;
    }

    public String getProblemStream() {
        return problemStream;
    }

    public void setProblemStream(String problemStream) {
        this.problemStream = problemStream;
    }
    public String getRejectDate() {
        return rejectDate;
    }

    public void setRejectDate(String rejectDate) {
        this.rejectDate = rejectDate;
    }
    public String getRownumber() {
        return rownumber;
    }

    public void setRownumber(String rownumber) {
        this.rownumber = rownumber;
    }
}
