package com.xforceplus.wapp.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

/**
 * @author mashaopeng@xforceplus.com
 */
@Data
public class IdsDto {
    @NotNull
    @Size(min = 1, message = "数据不能为空")
    private List<Long> ids;
}
