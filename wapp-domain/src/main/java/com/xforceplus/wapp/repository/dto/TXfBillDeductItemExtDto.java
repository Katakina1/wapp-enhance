package com.xforceplus.wapp.repository.dto;

import com.xforceplus.wapp.repository.entity.TXfBillDeductItemEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author malong@xforceplus.com
 * @program wapp-enhance
 * @description
 * @create 2022-09-16 16:44
 **/
@Setter
@Getter
@ToString(callSuper = true)
public class TXfBillDeductItemExtDto extends TXfBillDeductItemEntity {
    private Long itemRefId;
}
