//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.actionsoft.sdk.service.model;

/*import com.actionsoft.sdk.adapter.DateAdapter;*/

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.xml.bind.annotation.XmlRootElement;
import java.sql.Timestamp;

@XmlRootElement
public class ProcessInstance {
    private String id;
    private String processBusinessKey;
    private String processGroupId;
    private String title = "";
    private String controlState;
    private String processDefId;
    private String processDefVerId;
    private Timestamp createTime;
    private Timestamp startTime;
    private Timestamp endTime;
    private String startActivityId;
    private String startTaskInstId;
    private String endActivityId;
    private String remark = "";
    private boolean isProcess = true;
    private boolean isStart = false;
    private boolean isEnd = false;
    private boolean isAsync = false;
    private boolean isException;
    private boolean isOvertime;
    private boolean subProcess;
    private boolean existSubProcess;
    private String createUser;
    private String createUserDeptId;
    private String createUserRoleId = "";
    private String createUserLocation;
    private String createUserOrgId;
    private int securityLayer;
    private long executeCostTime;
    private long executeExpireTime;
    private String parentProcessInstId;
    private String parentTaskInstId;
    private String processProfileId;
    private int subInstType;
    @JsonProperty(value = "IOBD")
    private String IOBD;
    @JsonProperty(value = "IOR")
    private String IOR;
    @JsonProperty(value = "IOS")
    private String IOS;
    @JsonProperty(value = "IOC")
    private String IOC;
    private String ext1;
    private String ext2;
    private String ext3;
    private int remindTimes;

    public ProcessInstance() {
    }

    public boolean isExistSubProcess() {
        return this.existSubProcess;
    }

    public void setExistSubProcess(boolean existSubProcess) {
        this.existSubProcess = existSubProcess;
    }

    public String getStartTaskInstId() {
        if (this.startTaskInstId == null) {
            this.startTaskInstId = "";
        }

        return this.startTaskInstId;
    }

    public void setStartTaskInstId(String startTaskInstId) {
        this.startTaskInstId = startTaskInstId;
    }

    public boolean isProcess() {
        return this.isProcess;
    }

    public void setProcess(boolean isProcess) {
        this.isProcess = isProcess;
    }

    public boolean isAsync() {
        return this.isAsync;
    }

    public void setAsync(boolean isAsync) {
        this.isAsync = isAsync;
    }

    public String getCreateUserDeptId() {
        if (isEmpty(this.createUserDeptId)) {
            this.createUserDeptId = "";
        }

        return this.createUserDeptId;
    }

    public void setCreateUserDeptId(String createUserDeptId) {
        this.createUserDeptId = createUserDeptId;
    }

    public String getCreateUserRoleId() {
        if (isEmpty(this.createUserRoleId)) {
            this.createUserRoleId = "";
        }

        return this.createUserRoleId;
    }

    public void setCreateUserRoleId(String createUserRoleId) {
        this.createUserRoleId = createUserRoleId;
    }

  /*  @XmlJavaTypeAdapter(DateAdapter.class)*/
    public Timestamp getCreateTime() {
        return this.createTime;
    }

    public void setCreateTime(Timestamp createTime) {
        this.createTime = createTime;
    }

    public String getStartActivityId() {
        if (this.startActivityId == null) {
            this.startActivityId = "";
        }

        return this.startActivityId;
    }

    public void setStartActivityId(String startActivityId) {
        this.startActivityId = startActivityId;
    }

    public String getParentProcessInstId() {
        if (this.parentProcessInstId == null) {
            this.parentProcessInstId = "";
        }

        return this.parentProcessInstId;
    }

    public void setParentProcessInstId(String parentProcessInstId) {
        this.parentProcessInstId = parentProcessInstId;
    }

    public String getParentTaskInstId() {
        if (this.parentTaskInstId == null) {
            this.parentTaskInstId = "";
        }

        return this.parentTaskInstId;
    }

    public void setParentTaskInstId(String parentTaskInstId) {
        this.parentTaskInstId = parentTaskInstId;
    }

    public long getExecuteCostTime() {
        return this.executeCostTime;
    }

    public void setExecuteCostTime(long executeCostTime) {
        this.executeCostTime = executeCostTime;
    }

    public long getExecuteExpireTime() {
        return this.executeExpireTime;
    }

    public void setExecuteExpireTime(long executeExpireTime) {
        this.executeExpireTime = executeExpireTime;
    }

    public String getControlState() {
        if (this.controlState == null) {
            this.controlState = "";
        }

        return this.controlState;
    }

    public void setControlState(String controlState) {
        this.controlState = controlState;
    }

 /*   @XmlJavaTypeAdapter(DateAdapter.class)*/
    public Timestamp getEndTime() {
        return this.endTime;
    }

    public void setEndTime(Timestamp endTime) {
        this.endTime = endTime;
    }

    public String getEndActivityId() {
        if (this.endActivityId == null) {
            this.endActivityId = "";
        }

        return this.endActivityId;
    }

    public void setEndActivityId(String endActivityId) {
        this.endActivityId = endActivityId;
    }

    public String getProcessBusinessKey() {
        if (this.processBusinessKey == null) {
            this.processBusinessKey = "";
        }

        return this.processBusinessKey;
    }

    public void setProcessBusinessKey(String processBusinessKey) {
        this.processBusinessKey = processBusinessKey;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return this.id;
    }

    public void setProcessDefId(String processDefId) {
        if (processDefId == null) {
            processDefId = "";
        }

        this.processDefId = processDefId;
    }

    public String getProcessDefId() {
        return this.processDefId;
    }

    public void setProcessDefVerId(String processDefVerId) {
        if (processDefVerId == null) {
            processDefVerId = "";
        }

        this.processDefVerId = processDefVerId;
    }

    public String getProcessDefVerId() {
        return this.processDefVerId;
    }

    public void setProcessGroupId(String processGroupId) {
        this.processGroupId = processGroupId;
    }

    public String getProcessGroupId() {
        if (this.processGroupId == null) {
            this.processGroupId = "";
        }

        return this.processGroupId;
    }

    public void setStart(boolean isStart) {
        this.isStart = isStart;
    }

    public boolean isStart() {
        return this.isStart;
    }

    public void setEnd(boolean isEnd) {
        this.isEnd = isEnd;
    }

    public boolean isEnd() {
        return this.isEnd;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }

    public String getCreateUser() {
        if (this.createUser == null) {
            this.createUser = "";
        }

        return this.createUser;
    }

    public void setStartTime(Timestamp startTime) {
        this.startTime = startTime;
    }

    /*@XmlJavaTypeAdapter(DateAdapter.class)*/
    public Timestamp getStartTime() {
        return this.startTime;
    }

    public void setCreateUserLocation(String createUserLocation) {
        this.createUserLocation = createUserLocation;
    }

    public String getCreateUserLocation() {
        if (this.createUserLocation == null) {
            this.createUserLocation = "";
        }

        return this.createUserLocation;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        if (this.title == null) {
            this.title = "";
        }

        return this.title;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getRemark() {
        if (this.remark == null) {
            this.remark = "";
        }

        return this.remark;
    }

    public void setSecurityLayer(int securityLayer) {
        this.securityLayer = securityLayer;
    }

    public int getSecurityLayer() {
        return this.securityLayer;
    }

    public void setException(boolean isException) {
        this.isException = isException;
    }

    public boolean isException() {
        return this.isException;
    }

    public void setOvertime(boolean isOvertime) {
        this.isOvertime = isOvertime;
    }

    public boolean isOvertime() {
        return this.isOvertime;
    }

    public String getIOBD() {
        if (this.IOBD == null) {
            this.IOBD = "";
        }

        return this.IOBD;
    }

    public void setIOBD(String iOBD) {
        this.IOBD = iOBD;
    }

    public String getIOR() {
        if (this.IOR == null) {
            this.IOR = "";
        }

        return this.IOR;
    }

    public void setIOR(String iOR) {
        this.IOR = iOR;
    }

    public String getIOS() {
        if (this.IOS == null) {
            this.IOS = "";
        }

        return this.IOS;
    }

    public void setIOS(String iOS) {
        this.IOS = iOS;
    }

    public String getIOC() {
        if (this.IOC == null) {
            this.IOC = "";
        }

        return this.IOC;
    }

    public void setIOC(String iOC) {
        this.IOC = iOC;
    }

    public void setExt1(String ext1) {
        this.ext1 = ext1;
    }

    public String getExt1() {
        if (this.ext1 == null) {
            this.ext1 = "";
        }

        return this.ext1;
    }

    public void setExt2(String ext2) {
        this.ext2 = ext2;
    }

    public String getExt2() {
        if (this.ext2 == null) {
            this.ext2 = "";
        }

        return this.ext2;
    }

    public void setExt3(String ext3) {
        this.ext3 = ext3;
    }

    public String getExt3() {
        if (this.ext3 == null) {
            this.ext3 = "";
        }

        return this.ext3;
    }

    public void setCreateUserOrgId(String orgId) {
        this.createUserOrgId = orgId;
    }

    public String getCreateUserOrgId() {
        if (isEmpty(this.createUserOrgId)) {
            this.createUserOrgId = "";
        }

        return this.createUserOrgId;
    }

    public String getProcessProfileId() {
        if (isEmpty(this.processProfileId)) {
            this.processProfileId = "";
        }

        return this.processProfileId;
    }

    public void setProcessProfileId(String processProfileId) {
        this.processProfileId = processProfileId;
    }

    public int getSubInstType() {
        return this.subInstType;
    }

    public void setSubInstType(int subInstType) {
        this.subInstType = subInstType;
    }

    public void setSubProcess(boolean subProcess) {
        this.subProcess = subProcess;
    }

    public boolean isSubProcess() {
        return !isEmpty(this.parentTaskInstId);
    }

    public static boolean isEmpty(String str) {
        return str == null || str.trim().length() == 0;
    }

    public int getRemindTimes() {
        return this.remindTimes;
    }

    public void setRemindTimes(int remindTimes) {
        this.remindTimes = remindTimes;
    }
}
