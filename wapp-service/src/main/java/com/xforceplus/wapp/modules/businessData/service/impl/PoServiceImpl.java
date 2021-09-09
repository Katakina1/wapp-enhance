package com.xforceplus.wapp.modules.businessData.service.impl;

import com.xforceplus.wapp.common.safesoft.PagedQueryResult;
import com.xforceplus.wapp.common.utils.Query;
import com.xforceplus.wapp.modules.InformationInquiry.entity.QuestionnaireEntity;
import com.xforceplus.wapp.modules.InformationInquiry.entity.QuestionnaireExcelEntity;
import com.xforceplus.wapp.modules.businessData.dao.PoDao;
import com.xforceplus.wapp.modules.businessData.service.PoService;
import com.xforceplus.wapp.modules.posuopei.entity.PoEntity;
import com.xforceplus.wapp.modules.posuopei.entity.PoExcelEntity;
import com.xforceplus.wapp.modules.report.entity.FpjyExcelEntity;
import com.google.common.collect.Lists;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static org.slf4j.LoggerFactory.getLogger;

@Service
public class PoServiceImpl implements PoService {
    private static final Logger LOGGER= getLogger(PoServiceImpl.class);

    private  final PoDao poDao;
    @Autowired
    public PoServiceImpl(PoDao poDao){
        this.poDao=poDao;
    }
    @Override
    public PagedQueryResult<PoEntity> poQueryList(Map<String, Object> map) {
        final PagedQueryResult<PoEntity> poEntityPagedQueryResult=new PagedQueryResult<>();
        final Integer count=poDao.poQueryCount(map);
        List<PoEntity> list= Lists.newArrayList();
        if(count>0){
        	
            list=poDao.poQueryList(map);
            list.forEach(poEntity -> {
                if(!StringUtils.isEmpty(poEntity.getHoststatus())) {
                    if (!"1".equals(poEntity.getHoststatus())) {
                        if(poEntity.getNewAmount()!=null) {
                            if (poEntity.getNewAmount().compareTo(BigDecimal.ZERO) > 0) {
                                poEntity.setReceiptAmount(poEntity.getNewAmount());
                            } else {
                                poEntity.setReceiptAmount(poEntity.getAmountpaid());
                            }
                        }else{
                            poEntity.setReceiptAmount(poEntity.getAmountpaid());

                        }
                    }
                }
            });
        }

        poEntityPagedQueryResult.setResults(list);
        poEntityPagedQueryResult.setTotalCount(count);
        return poEntityPagedQueryResult;
    }

    @Override
    public Integer queryTotalResult(Map map){
        return poDao.poQueryCount(map);
    };
    @Override
    public List<PoEntity> queryList(Query query1){
        List<PoEntity> list= Lists.newArrayList();
        list=poDao.poQueryList(query1);
        list.forEach(poEntity -> {
            if(!"1".equals(poEntity.getHoststatus())){
                if(poEntity.getNewAmount()!=null) {
                    if (poEntity.getNewAmount().compareTo(BigDecimal.ZERO) > 0) {
                        poEntity.setReceiptAmount(poEntity.getNewAmount());
                    } else {
                        poEntity.setReceiptAmount(poEntity.getAmountpaid());
                    }
                }else{
                    poEntity.setReceiptAmount(poEntity.getAmountpaid());

                }
            }
        });
        return list;
    };

    @Override
    public List<PoExcelEntity> transformExcle(List<PoEntity> list) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        List<PoExcelEntity> list2=new ArrayList<>();
        String[] s=new String[]{"序号","JV","供应商号","订单号","订单类型","收货日期","收货号","交易号","收货金额","发票号码","匹配状态","沃尔玛状态"};
        reflect(list2,s);
        for (int i=0; i<list.size();i++) {
            PoEntity entity=list.get(i);
            PoExcelEntity entity1=new PoExcelEntity();
            entity1.setCell0(  i+1+"");
//JV
            entity1.setCell1(entity.getJvcode());
            //供应商号
            entity1.setCell2(entity.getVenderid());
            //订单号
            entity1.setCell3(entity.getPocode());
            //订单类型
            entity1.setCell4(entity.getPoType());
            //收货日期
            entity1.setCell5(formatDate(entity.getReceiptdate()));
            //收货号
            entity1.setCell6(entity.getReceiptid());
            //交易号
            entity1.setCell7(entity.getTractionNbr());
            //收货金额
            entity1.setCell8(formatAmount(entity.getReceiptAmount().toString()));
            //发票号
            entity1.setCell9(entity.getInvoiceno());
            //匹配状态
            entity1.setCell10(formatDxhyMatchStatus(entity.getDxhyMatchStatus()));
            //订单状态
            entity1.setCell11(formatHostStatus(entity.getHoststatus()));
            list2.add(entity1);
        }
        return list2;
    };
    private void reflect(List<PoExcelEntity> list2, String[] s) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        PoExcelEntity e=new PoExcelEntity();
        Class cls = e.getClass();
        Field[] fields = cls.getDeclaredFields();
        for(int i=0; i<fields.length; i++){
            String name = fields[i].getName(); // 获取属性的名字
            name = name.substring(0, 1).toUpperCase() + name.substring(1); // 将属性的首字符大写，方便构造get，set方法
            String type = fields[i].getGenericType().toString(); // 获取属性的类型
            if (type.equals("class java.lang.String")) { // 如果type是类类型，则前面包含"class "，后面跟类名
                Method m = e.getClass().getMethod("get" + name);
                String value = (String) m.invoke(e); // 调用getter方法获取属性值
                if (value == null) {
                    m = e.getClass().getMethod("set"+name,String.class);
                    m.invoke(e, s[i]);
                }
            }
        }
        list2.add(e);
    }
    private String formatHostStatus(String dxhyMatchStatus) {
        String str = "";
        if(StringUtils.isEmpty(dxhyMatchStatus)){
            return "未处理";
        }
        if(dxhyMatchStatus.equals("0")){
            str="未处理";
        }
        if(dxhyMatchStatus.equals("1")){
            str="未处理";
        }
        if(dxhyMatchStatus.equals("5")){
            str="已处理";
        }
        if(dxhyMatchStatus.equals("10")){
            str="未处理";
        }
        if(dxhyMatchStatus.equals("13")){
            str="已删除";
        }
        if(dxhyMatchStatus.equals("14")){
            str="待付款";
        }
        if(dxhyMatchStatus.equals("11")){
            str="已匹配";
        }
        if(dxhyMatchStatus.equals("12")){
            str="已匹配";
        }
        if(dxhyMatchStatus.equals("15")){
            str="已付款";
        }
        if(dxhyMatchStatus.equals("19")){
            str="已付款";
        }
        if(dxhyMatchStatus.equals("9")){
            str="待付款";
        }
        if(dxhyMatchStatus.equals("99")){
            str="已付款";
        }
        if(dxhyMatchStatus.equals("999")){
            str="已付款";
        }
        if(dxhyMatchStatus.equals("8")){
            str="HOLD";
        }
        return str;
    }

    private String formatDxhyMatchStatus(String dxhyMatchStatus) {
        String str = "";
        if(StringUtils.isEmpty(dxhyMatchStatus)){
            return "";
        }
        if(dxhyMatchStatus.equals("0")){
            str="未匹配";
        }
        if(dxhyMatchStatus.equals("1")){
            str="预匹配";
        }
        if(dxhyMatchStatus.equals("2")){
            str="部分匹配";
        }
        if(dxhyMatchStatus.equals("3")){
            str="完全匹配";
        }
        if(dxhyMatchStatus.equals("4")){
            str="差异匹配";
        }
        if(dxhyMatchStatus.equals("5")){
            str="匹配失败";
        }
        if(dxhyMatchStatus.equals("6")){
            str="取消匹配";
        }
        return str;
    }



    /*private String formatPoType(String poType) {

        if(StringUtils.isEmpty(poType)){
            return "";
        }
        if(poType.equals()){
            return "";
        }
    }*/

    public String formatDate(Date date){
        if(date ==null){
            return "";
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(date);

    }
    private String formatAmount(String amount) {
        return amount == null ? "" : amount.substring(0, amount.length()-2);
    }
}
