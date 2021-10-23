package com.xforceplus.wapp.modules.upcmock;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.xforceplus.wapp.annotation.AuthIgnore;
import com.xforceplus.wapp.client.UpcRsp;
import com.xforceplus.wapp.repository.dao.UpcTestDao;
import com.xforceplus.wapp.repository.entity.UpcTest;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author mashaopeng@xforceplus.com
 */
@RestController
public class UpcMockController {
    private final UpcTestDao upcTestDao;

    public UpcMockController(UpcTestDao upcTestDao) {
        this.upcTestDao = upcTestDao;
    }

    @AuthIgnore
    @PostMapping("/item-oe/findByNbrs")
    public UpcRsp findByNbrs(@RequestBody UpcVo vo) {
        String itemNo = "";
        List<UpcTest> list = upcTestDao.selectList(new QueryWrapper<>());
        for (UpcTest test : list) {
            if (vo.nbrs.contains(test.getUpc())) {
                 itemNo = test.getItemNo();
                 break;
            }
        }
        UpcRsp upcRsp = new UpcRsp();
        if (StringUtils.isNotBlank(itemNo)) {
            upcRsp.setCode("1");
            upcRsp.setMessage("成功");
            UpcRsp.UpcVO upcVO = new UpcRsp.UpcVO();
            upcVO.setItemNo(itemNo);
            upcRsp.setResult(upcVO);
        } else {
            upcRsp.setCode("0");
            upcRsp.setMessage("未查到数据");
        }
        return upcRsp;
    }

    @Data
    public static class UpcVo {
        private List<String> nbrs;
    }
}
