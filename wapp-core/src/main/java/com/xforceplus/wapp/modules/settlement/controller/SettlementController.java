package com.xforceplus.wapp.modules.settlement.controller;

import com.xforceplus.wapp.annotation.EnhanceApi;
import com.xforceplus.wapp.common.dto.R;
import com.xforceplus.wapp.modules.claim.dto.ApplyVerdictRequest;
import com.xforceplus.wapp.modules.claim.dto.SettlementApplyVerdictRequest;
import com.xforceplus.wapp.modules.claim.service.ClaimService;
import com.xforceplus.wapp.modules.rednotification.model.Response;
import com.xforceplus.wapp.modules.settlement.dto.SettlementUndoRedNotificationRequest;
import com.xforceplus.wapp.modules.sys.util.UserUtil;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author malong@xforceplus.com
 * @program wapp-enhance
 * @description
 * @create 2021-10-16 19:51
 **/
@RestController
@RequestMapping(EnhanceApi.BASE_PATH + "/settlement")
public class SettlementController {

    @Autowired
    private ClaimService claimService;

    @ApiOperation(value = "申请不定案", notes = "", response = Response.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "response", response = Response.class)})
    @PostMapping(value = "/applyVerdict")
    public Response applyVerdict(@RequestBody SettlementApplyVerdictRequest request) {
        claimService.applyClaimVerdict(request.getSettlementId(), request.getBillDeductIdList());
        return Response.ok("成功", "");
    }

    @PostMapping("undo-red-notification")
    public R undoRedNotification(@RequestBody SettlementUndoRedNotificationRequest request){
        claimService.agreeClaimVerdict(request.getSettlementId());
        return R.ok("撤销红字信息表申请已提交成功");
    }

}
