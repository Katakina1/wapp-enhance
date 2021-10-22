package com.xforceplus.wapp.dto;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author mashaopeng@xforceplus.com
 */
@Data
public class IdsDto {
    @NotNull
    @Length(min = 1, message = "数据不能为空")
    private List<Long> ids;
}
