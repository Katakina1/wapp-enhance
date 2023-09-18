package com.xforceplus.wapp.repository.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.Date;

/**
 * <p>
    * 非商电票上传记录税码
    * </p>
 *
 * @author malong@xforceplus.com
 * @since 2021-10-22
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper=true)
public class TXfNoneBusinessUploadDetailTaxCodeDto extends BaseEntity {

    private String label;

    private String value;

}
