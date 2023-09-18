package com.xforceplus.wapp.modules.upcmock;

import com.xforceplus.wapp.annotation.EnhanceApiV1;
import com.xforceplus.wapp.client.NbrRsp;
import com.xforceplus.wapp.client.WappHostClient;
import com.xforceplus.wapp.modules.taxcode.service.impl.TaxCodeRiversandServiceImpl;
import com.xforceplus.wapp.repository.entity.TXfTaxCodeRiversandEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.*;

/**
 * @author mashaopeng@xforceplus.com
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(EnhanceApiV1.BASE_PATH)
public class UpcMockController {
    private final WappHostClient wappHostClient;
    private final TaxCodeRiversandServiceImpl taxCodeRiversandService;

    @GetMapping("/findNbrs")
    public Map<String, Object> sendUpc(@RequestParam Set<String> nbrs) {
        Mono<List<NbrRsp.HyperNbr>> hyper = wappHostClient.findHyperByNbrs(nbrs);
        Mono<List<NbrRsp.SamsNbr>> sams = wappHostClient.findSamsByNbrs(nbrs);
        return new HashMap<String, Object>() {{
            put("hyper", hyper.block());
            put("sams", sams.block());
        }};
    }
    @GetMapping("/list")
    public List<TXfTaxCodeRiversandEntity> listTaxCode(@RequestParam(required = false) String time) {
        ArrayList<TXfTaxCodeRiversandEntity> list = new ArrayList<>();
        taxCodeRiversandService.getRiverSandTaxCode(time, list::addAll);
        return list;
    }
}
