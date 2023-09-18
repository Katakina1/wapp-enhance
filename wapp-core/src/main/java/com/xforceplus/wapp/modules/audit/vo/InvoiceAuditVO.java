package com.xforceplus.wapp.modules.audit.vo;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

/**
 * @author mashaopeng@xforceplus.com
 */
@Data
@Builder
public class InvoiceAuditVO {
    @NotNull
    @Size(min = 1, message = "数据不能为空")
    private List<String> uuids;

    @NotBlank(message = "审核状态不能为空")
    private String auditStatus;

    private String remark;

    private String auditRemark;
}
