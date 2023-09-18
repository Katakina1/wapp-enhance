package com.xforceplus.wapp.mq;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.google.gson.Gson;
import com.xforceplus.wapp.client.JanusClient;
import com.xforceplus.wapp.client.LockClient;
import com.xforceplus.wapp.common.dto.R;
import com.xforceplus.wapp.enums.TaxPreEnum;
import com.xforceplus.wapp.enums.ZeroTaxEnum;
import com.xforceplus.wapp.modules.rednotification.mapstruct.IdGenerator;
import com.xforceplus.wapp.modules.taxcode.dto.TaxCodeDto;
import com.xforceplus.wapp.modules.taxcode.service.TaxCodeServiceImpl;
import com.xforceplus.wapp.modules.taxcode.service.impl.TaxCodeReportServiceImpl;
import com.xforceplus.wapp.modules.taxcode.service.impl.TaxCodeRiversandServiceImpl;
import com.xforceplus.wapp.repository.entity.TXfTaxCodeReportEntity;
import com.xforceplus.wapp.repository.entity.TXfTaxCodeRiversandEntity;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

import javax.jms.TextMessage;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.BiConsumer;

/**
 * 监听riversand接收到的税编和wapp的税编进行比较
 */
@Slf4j
@Component
public class RiversandTaxCodeConsumer {


    @Autowired
    private TaxCodeServiceImpl taxCodeService;

    private  JanusClient janusClient;

    @Autowired
    private LockClient lockClient;

    @Autowired
    private TaxCodeReportServiceImpl taxCodeReportService;

    @Autowired
    private TaxCodeRiversandServiceImpl taxCodeRiversandService;

    public RiversandTaxCodeConsumer(JanusClient janusClient) {
        this.janusClient = janusClient;
    }

    @JmsListener(destination = "${activemq.queue-name.import-riversand-taxcode-queue}")
    public void onMessage(Message<String> message, TextMessage textMessage) {
        log.info("--------比较riversand的税编和wapp的税编-------------");
        try {
            String text = textMessage.getText();
            if (StringUtils.isBlank(text)) {
                log.error("处理riversand的税编消息为空");
                return;
            }
            text = convert(text.replaceAll("\r", "%0D").replaceAll("\n", "%0A"));
            TXfTaxCodeRiversandEntity taxCodeRiversandEntity = new Gson().fromJson(text, TXfTaxCodeRiversandEntity.class);
            if(StringUtils.isBlank(taxCodeRiversandEntity.getItemNo()) || "1".equals(taxCodeRiversandEntity.getDeleteFlag())){
                return;
            }
            //更新riversand表状态 0-默认 1-比对一致 2-比对不一致 -1-上传失败 3-上传3.0平台成功 4-上传已经存在
            UpdateWrapper<TXfTaxCodeRiversandEntity> refWrapper = new UpdateWrapper<>();
            refWrapper.eq(TXfTaxCodeRiversandEntity.ITEM_NO,taxCodeRiversandEntity.getItemNo());

            Optional<TaxCodeDto> taxCodeOptional = taxCodeService.getTaxCodeByItemNo(taxCodeRiversandEntity.getItemNo());
            if (taxCodeOptional.isPresent()) {
                TaxCodeDto taxCode = taxCodeOptional.get();

                BiConsumer<LambdaQueryWrapper<TXfTaxCodeReportEntity>, TXfTaxCodeReportEntity> saveOrUpdate =
                        (LambdaQueryWrapper<TXfTaxCodeReportEntity> it1, TXfTaxCodeReportEntity it2) -> {
                            if (taxCodeReportService.count(it1) > 0) {
                                log.info("更新 例外报告{}", it2);
                                it2.setUpdateTime(new Date());
                                taxCodeReportService.update(it2, it1);
                            } else {
                                log.info("新增 例外报告{}", it2);
                                it2.setId(IdGenerator.generate());
                                it2.setCreateTime(new Date());
                                it2.setUpdateTime(it2.getCreateTime());
                                taxCodeReportService.save(it2);
                            }
                            it1.clear();
                        };

                LambdaQueryWrapper<TXfTaxCodeReportEntity> wrapper = new LambdaQueryWrapper<>();
                boolean match=true;
                if (!Objects.equals(taxCodeRiversandEntity.getGoodsTaxNo(), taxCode.getGoodsTaxNo())){
                    TXfTaxCodeReportEntity tXfTaxCodeReport=new TXfTaxCodeReportEntity();
                    tXfTaxCodeReport.setItemNo(taxCodeRiversandEntity.getItemNo());
                    tXfTaxCodeReport.setItemName(taxCode.getItemName());
                    tXfTaxCodeReport.setGoodsTaxNo(taxCode.getGoodsTaxNo());
                    tXfTaxCodeReport.setRsGoodsTaxNo(taxCodeRiversandEntity.getGoodsTaxNo());
                    tXfTaxCodeReport.setReportDesc("税收分类编码不一致，票易通的税编:"+taxCode.getGoodsTaxNo()+",riversand的税编:"+taxCodeRiversandEntity.getGoodsTaxNo());
                    wrapper.eq(TXfTaxCodeReportEntity::getGoodsTaxNo, taxCode.getGoodsTaxNo())
                            .eq(TXfTaxCodeReportEntity::getItemNo, taxCode.getItemNo());
                    log.info("税收分类编码不一致，票易通的税编:"+taxCode.getGoodsTaxNo()+",riversand的税编:"+taxCodeRiversandEntity.getGoodsTaxNo());
                    lockClient.tryLock("taxCode:" + taxCode.getItemNo() + ":" + taxCode.getGoodsTaxNo(),
                            ()->saveOrUpdate.accept(wrapper, tXfTaxCodeReport), -1, 3);
                    match=false;
                }
                if (!Objects.equals(taxCodeRiversandEntity.getTaxRate().setScale(2, BigDecimal.ROUND_HALF_UP), taxCode.getTaxRate().setScale(2, BigDecimal.ROUND_HALF_UP))){
                    TXfTaxCodeReportEntity tXfTaxCodeReport=new TXfTaxCodeReportEntity();
                    tXfTaxCodeReport.setItemNo(taxCodeRiversandEntity.getItemNo());
                    tXfTaxCodeReport.setItemName(taxCode.getItemName());
                    tXfTaxCodeReport.setTaxRate(taxCode.getTaxRate().setScale(2, BigDecimal.ROUND_HALF_UP));
                    tXfTaxCodeReport.setRsTaxRate(taxCodeRiversandEntity.getTaxRate().setScale(2,BigDecimal.ROUND_HALF_UP));
                    DecimalFormat df = new DecimalFormat("0%");
                    df.setRoundingMode(RoundingMode.DOWN);
                    tXfTaxCodeReport.setReportDesc("税率不一致，票易通的税率:"+ df.format(taxCode.getTaxRate().setScale(2,BigDecimal.ROUND_HALF_UP))
                            +",riversand的税率:"+df.format(taxCodeRiversandEntity.getTaxRate().setScale(2,BigDecimal.ROUND_HALF_UP)));
                    wrapper.eq(TXfTaxCodeReportEntity::getTaxRate, taxCode.getTaxRate())
                            .eq(TXfTaxCodeReportEntity::getItemNo, taxCode.getItemNo());
                    log.info("税率不一致，票易通的税率:"+ df.format(taxCode.getTaxRate().setScale(2,BigDecimal.ROUND_HALF_UP)));
                    lockClient.tryLock("taxCode:" + taxCode.getItemNo() + ":" + taxCode.getTaxRate(),
                            ()->saveOrUpdate.accept(wrapper, tXfTaxCodeReport), -1, 3);

                    match=false;
                }
                if (!Objects.equals(taxCodeRiversandEntity.getZeroTax(), taxCode.getZeroTax())){
                    TXfTaxCodeReportEntity tXfTaxCodeReport=new TXfTaxCodeReportEntity();
                    tXfTaxCodeReport.setItemNo(taxCodeRiversandEntity.getItemNo());
                    tXfTaxCodeReport.setItemName(taxCode.getItemName());
                    tXfTaxCodeReport.setRsZeroTax(taxCodeRiversandEntity.getZeroTax());
                    tXfTaxCodeReport.setZeroTax(taxCode.getZeroTax());
                    tXfTaxCodeReport.setReportDesc("零税率标识不一致，票易通的零税率标识:"+ ZeroTaxEnum.fromCode(taxCode.getZeroTax()).getDescription()+",riversand的零税率标识:"+ZeroTaxEnum.fromCode(taxCodeRiversandEntity.getZeroTax()).getDescription());
                    wrapper.eq(TXfTaxCodeReportEntity::getZeroTax, taxCode.getZeroTax())
                            .eq(TXfTaxCodeReportEntity::getItemNo, taxCode.getItemNo());
                    log.info("零税率标识不一致，票易通的零税率标识:"+ ZeroTaxEnum.fromCode(taxCode.getZeroTax()).getDescription()+",riversand的零税率标识:"+ZeroTaxEnum.fromCode(taxCodeRiversandEntity.getZeroTax()).getDescription());
                    lockClient.tryLock("taxCode:" + taxCode.getItemNo() + ":" + taxCode.getZeroTax(),
                            ()->saveOrUpdate.accept(wrapper, tXfTaxCodeReport), -1, 3);

                    match=false;
                }
                if (!Objects.equals(taxCodeRiversandEntity.getTaxPre(), taxCode.getTaxPre())){
                    TXfTaxCodeReportEntity tXfTaxCodeReport=new TXfTaxCodeReportEntity();
                    tXfTaxCodeReport.setItemNo(taxCodeRiversandEntity.getItemNo());
                    tXfTaxCodeReport.setItemName(taxCode.getItemName());
                    tXfTaxCodeReport.setRsTaxPre(TaxPreEnum.fromCode(taxCodeRiversandEntity.getTaxPre()).getDescription());
                    tXfTaxCodeReport.setTaxPre(TaxPreEnum.fromCode(taxCode.getTaxPre()).getDescription());
                    tXfTaxCodeReport.setReportDesc("优惠政策标识不一致，票易通的优惠政策标识:"+ TaxPreEnum.fromCode(taxCode.getTaxPre()).getDescription()+",riversand的优惠政策标识:"+TaxPreEnum.fromCode(taxCodeRiversandEntity.getTaxPre()).getDescription());
                    wrapper.eq(TXfTaxCodeReportEntity::getTaxPre, TaxPreEnum.fromCode(taxCode.getTaxPre()).getDescription())
                            .eq(TXfTaxCodeReportEntity::getItemNo, taxCode.getItemNo());
                    log.info("优惠政策标识不一致，票易通的优惠政策标识:"+ TaxPreEnum.fromCode(taxCode.getTaxPre()).getDescription()+",riversand的优惠政策标识:"+TaxPreEnum.fromCode(taxCodeRiversandEntity.getTaxPre()).getDescription());
                    lockClient.tryLock("taxCode:" + taxCode.getItemNo() + ":" + taxCode.getTaxPre(),
                            ()->saveOrUpdate.accept(wrapper, tXfTaxCodeReport), -1, 3);

                    match=false;
                }
                if (!Objects.equals(taxCodeRiversandEntity.getTaxPreCon(), taxCode.getTaxPreCon())){
                    TXfTaxCodeReportEntity tXfTaxCodeReport=new TXfTaxCodeReportEntity();
                    tXfTaxCodeReport.setItemNo(taxCodeRiversandEntity.getItemNo());
                    tXfTaxCodeReport.setItemName(taxCode.getItemName());
                    tXfTaxCodeReport.setRsTaxPreCon(taxCodeRiversandEntity.getTaxPreCon());
                    tXfTaxCodeReport.setTaxPreCon(taxCode.getTaxPreCon());
                    tXfTaxCodeReport.setReportDesc("优惠政策内容不一致，票易通的优惠政策内容:"+taxCode.getTaxPreCon()+",riversand的优惠政策内容:"+taxCodeRiversandEntity.getTaxPreCon());
                    wrapper.eq(TXfTaxCodeReportEntity::getTaxPreCon, taxCode.getTaxPreCon())
                            .eq(TXfTaxCodeReportEntity::getItemNo, taxCode.getItemNo());
                    log.info("优惠政策内容不一致，票易通的优惠政策内容:"+taxCode.getTaxPreCon()+",riversand的优惠政策内容:"+taxCodeRiversandEntity.getTaxPreCon());
                    lockClient.tryLock("taxCode:" + taxCode.getItemNo() + ":" + taxCode.getTaxPreCon(),
                            ()->saveOrUpdate.accept(wrapper, tXfTaxCodeReport), -1, 3);

                    match=false;
                }
                //更新匹配状态
                if(match){
                    refWrapper.set("status","1");
                }else{
                    refWrapper.set("status","2");
                }
            } else {
                //3.0不存着税编，需要新增导入
                log.info("商品编号:{}未匹配到税编", taxCodeRiversandEntity.getItemNo());
                //税编同步更新到3.0平台
                R r=janusClient.sendTaxCode(taxCodeRiversandEntity);
                if("1".equals(r.getCode())){
                    refWrapper.set("status","3");
                }else{
                    if (r.getMessage().contains("存在")){
                        refWrapper.set("status","4");
                    }else{
                        refWrapper.set("status","-1");
                    }
                }
            }
            taxCodeRiversandService.update(refWrapper);//更新状态
            log.info("riversand的税编:" + message);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }
    /**
     * 复制对象
     */
    public String convert(String source) {
        return String.copyValueOf(source.toCharArray());
    }
}