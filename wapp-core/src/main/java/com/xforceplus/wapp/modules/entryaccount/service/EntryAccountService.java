package com.xforceplus.wapp.modules.entryaccount.service;

import com.xforceplus.wapp.common.dto.R;
import com.xforceplus.wapp.modules.entryaccount.dto.entryAccoount.EntryAccountDTO;
import com.xforceplus.wapp.modules.entryaccount.dto.entryAccoount.EntryAccountResultDTO;
import com.xforceplus.wapp.repository.entity.TDxCustomsEntity;

import java.util.List;
import java.util.Map;

/**
 * @Author: ChenHang
 * @Date: 2023/6/29 10:10
 */
public interface EntryAccountService {

    /**
     * 非商入账
     * @param entryAccountDTO
     * @return
     */
    void nonCommodity(EntryAccountDTO entryAccountDTO, List<EntryAccountResultDTO> successList, List<EntryAccountResultDTO> failList);

    /**
     * 海关缴款书入账
     * @param entryAccountDTO
     * @return
     */
    void customs(EntryAccountDTO entryAccountDTO, List<EntryAccountResultDTO> successList, List<EntryAccountResultDTO> failList);

    /**
     * 从BMS获取海关缴款书明细并推送BMS比对状态
     * @param customsEntity
     */
    void customerToBMS(TDxCustomsEntity customsEntity) throws Exception;

    /**
     * 海关缴款书单条主动获取明细并比对结果
     * @param id
     */
    void activeCustomerToBMS(String id) throws Exception;

    Map<String, List<EntryAccountResultDTO>> entryAccount(List<EntryAccountDTO> entryAccountDTOList);
}
