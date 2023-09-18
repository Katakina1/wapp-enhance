package com.xforceplus.wapp.modules.claim.dto;

import com.xforceplus.wapp.modules.deduct.vo.ManagerSellerVO;
import lombok.Data;

/**
 * @author mashaopeng@xforceplus.com
 */
@Data
public class ManagerSellerRequest {
    private Integer pageNum = 1;
    private Integer pageSize = 20;

    private ManagerSellerVO params = new ManagerSellerVO();
}
