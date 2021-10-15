package com.xforceplus.wapp.repository.dao;

import com.xforceplus.wapp.repository.entity.TDxQuestionPaperEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xforceplus.wapp.repository.entity.TXfBillDeductEntity;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.Date;
import java.util.List;


/**
* <p>
* 采购问题清单 Mapper 接口
* </p>
*
* @author malong@xforceplus.com
* @since 2021-10-14
*/
public interface TDxQuestionPaperDao extends BaseMapper<TDxQuestionPaperEntity> {

    @Select("select MAX(problem_stream)  problemStream " +
            " from t_dx_question_paper WITH(NOLOCK) where usercode =#{usercode}")
    TDxQuestionPaperEntity queryMaxProblemStream(@Param("usercode") String usercode);
}
