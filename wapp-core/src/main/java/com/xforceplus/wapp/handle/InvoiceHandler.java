package com.xforceplus.wapp.handle;

import com.xforceplus.apollo.msg.SealedMessage;
import com.xforceplus.wapp.converters.InvoiceConverter;
import com.xforceplus.wapp.converters.InvoiceItemConverter;
import com.xforceplus.wapp.handle.vo.InvoiceVo;
import com.xforceplus.wapp.modules.company.service.CompanyService;
import com.xforceplus.wapp.modules.invoice.service.InvoiceItemServiceImpl;
import com.xforceplus.wapp.modules.invoice.service.InvoiceServiceImpl;
import com.xforceplus.wapp.repository.entity.TAcOrgEntity;
import com.xforceplus.wapp.repository.entity.TDxRecordInvoiceEntity;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;


/**
 * @author mashaopeng@xforceplus.com
 */
//@Component
@Slf4j
public class InvoiceHandler /*implements IntegrationResultHandler*/ {
    private final InvoiceServiceImpl invoiceService;
    private final InvoiceItemServiceImpl invoiceDetailsService;
    private final InvoiceConverter invoiceConverter;
    private final InvoiceItemConverter invoiceItemConverter;


    private final CompanyService companyService;

    public static final String GOODS_LIST_TEXT="(详见销货清单)";

    public InvoiceHandler(InvoiceServiceImpl invoiceService, InvoiceItemServiceImpl invoiceDetailsService, InvoiceConverter invoiceConverter, InvoiceItemConverter invoiceItemConverter, CompanyService companyService) {
        this.invoiceService = invoiceService;
        this.invoiceDetailsService = invoiceDetailsService;
        this.invoiceConverter = invoiceConverter;
        this.invoiceItemConverter = invoiceItemConverter;
        this.companyService = companyService;
    }

//    @Override
    public String requestName() {
        return "purchaserInvoiceSync";
    }

//    @Override
    @Transactional(rollbackFor = RuntimeException.class)
    public boolean handle(SealedMessage sealedMessage) {
        /**
         * 马哥说这个进项发票下发没有用，此处代码先注释
        log.info("request name:{},payload obj:{},header:{}", this.requestName(), sealedMessage.getPayload().getObj(), JSON.toJSONString(sealedMessage.getHeader()));
        InvoiceVo vo = JsonUtil.fromJson(sealedMessage.getPayload().getObj().toString(), InvoiceVo.class);
        InvoiceVo.Invoice invoice = vo.getData();
        if (!INVOICE_TYPE_MAP.containsKey(invoice.getInvoiceType())) {
            log.info("发票类型[{}]无法转换,invoiceCode:{},invoiceNo:{}。", invoice.getInvoiceType(), invoice.getInvoiceCode(), invoice.getInvoiceNo());
            return true;
        }
        List<InvoiceVo.InvoiceItemVO> items = invoice.getItems();
        if (CollectionUtils.isNotEmpty(items)){
            int sequence=1;
            for (int i = 0; i < items.size(); i++) {
                if (!Objects.equals(GOODS_LIST_TEXT, items.get(i).getCargoName())){
                    items.get(i).setSequence(sequence++);
                }
            }
        }
        TDxRecordInvoiceEntity invoiceMap = invoiceConverter.map(invoice, CollectionUtils.isEmpty(items) ? 0 : 1);
        return new LambdaQueryChainWrapper<>(invoiceService.getBaseMapper())
                .eq(TDxRecordInvoiceEntity::getUuid, invoice.getInvoiceCode() + invoice.getInvoiceNo())
                .eq(TDxRecordInvoiceEntity::getInvoiceCode, invoice.getInvoiceCode())
                .eq(TDxRecordInvoiceEntity::getInvoiceNo, invoice.getInvoiceNo()).oneOpt()
                .map(it -> {
                    invoiceMap.setId(it.getId());
                    log.warn("发票更新,invoiceCode:{},invoiceNo:{}", it.getInvoiceCode(), it.getInvoiceNo());
                    //如果有销货清单 判断是否只有一条明细 如果都满足则删除重新添加
                    LambdaQueryChainWrapper<TDxRecordInvoiceDetailEntity> eq = new LambdaQueryChainWrapper<>(invoiceDetailsService.getBaseMapper())
                            .eq(TDxRecordInvoiceDetailEntity::getUuid,invoice.getInvoiceCode() + invoice.getInvoiceNo())
                            .eq(TDxRecordInvoiceDetailEntity::getInvoiceNo, invoiceMap.getInvoiceNo())
                            .eq(TDxRecordInvoiceDetailEntity::getInvoiceCode, invoiceMap.getInvoiceCode());
                    Integer count = eq.count();
                    if ("1".equals(it.getGoodsListFlag())) {
                        if (count == 1 && CollectionUtils.isNotEmpty(items)) {
                            // mybatis plus bug 不能复用 eq
//                            invoiceDetailsService.remove(eq);
                            invoiceDetailsService.remove(Wrappers.<TDxRecordInvoiceDetailEntity>lambdaQuery().eq(TDxRecordInvoiceDetailEntity::getUuid,invoice.getInvoiceCode() + invoice.getInvoiceNo()));
                            saveItems(items,invoiceMap);
                        }
                    }

                    if (count==0){
                        saveItems(items,invoiceMap);
                    }

                    return invoiceService.updateById(invoiceMap);
                })
                .orElseGet(() -> {
                    insertInvoice(invoiceMap, items);
                    return true;
                });
         **/
        return true;

    }

    private void insertInvoice(TDxRecordInvoiceEntity invoiceMap,List<InvoiceVo.InvoiceItemVO> items){
        invoiceMap.setRemainingAmount(BigDecimal.ZERO.max(invoiceMap.getInvoiceAmount()));
        invoiceMap.setCreateDate(new Date());
        final TAcOrgEntity purchaserOrgInfoByTaxNo = companyService.getPurchaserOrgInfoByTaxNo(invoiceMap.getGfTaxNo());
        Optional.ofNullable(purchaserOrgInfoByTaxNo).ifPresent(x->{
            invoiceMap.setJvname(x.getOrgName());
            invoiceMap.setJvcode(x.getOrgCode());
        });

        final TAcOrgEntity sellerOrgInfoByTaxNo = companyService.getSellerOrgInfoByTaxNo(invoiceMap.getXfTaxNo());
        Optional.ofNullable(sellerOrgInfoByTaxNo).ifPresent(x->{
            invoiceMap.setVenderid(x.getOrgCode());
        });
        saveItems(items, invoiceMap);
        invoiceService.save(invoiceMap);
        log.warn("发票插入,invoiceCode:{},invoiceNo:{}", invoiceMap.getInvoiceCode(), invoiceMap.getInvoiceNo());
    }

    private void saveItems(List<InvoiceVo.InvoiceItemVO> items,TDxRecordInvoiceEntity invoiceMap){
        if (CollectionUtils.isNotEmpty(items)) {
            //TODO  mybatis plus 批量插入会获取插入的ID（很奇怪，设置 mybatis-plus.configuration. use-generated-keys= false 不生效 ），但是SQLServer驱动不支持，导致插入报错，只能单个循环插入
//            invoiceDetailsService.saveBatch(invoiceItemConverter.map(items), 2000);
            invoiceItemConverter.map(items).parallelStream().forEach(
                    x -> {
                        if (!Objects.equals(GOODS_LIST_TEXT, x.getGoodsName())) {
                            invoiceDetailsService.save(x);
                        }else {
                            invoiceMap.setGoodsListFlag("1");
                        }
                    }
            );
        }
    }
}
