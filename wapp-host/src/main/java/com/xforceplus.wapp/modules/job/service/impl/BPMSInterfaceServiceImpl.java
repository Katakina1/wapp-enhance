package com.xforceplus.wapp.modules.job.service.impl;


import com.xforceplus.wapp.common.utils.MD5Utils;
import com.xforceplus.wapp.modules.base.entity.OrganizationEntity;
import com.xforceplus.wapp.modules.base.entity.UserEntity;
import com.xforceplus.wapp.modules.job.dao.VendorMasterDao;
import com.xforceplus.wapp.modules.job.pojo.vendorMaster.DictPo;
import com.xforceplus.wapp.modules.job.pojo.vendorMaster.Table1;
import com.xforceplus.wapp.modules.job.pojo.vendorMaster.VendorLog;
import com.xforceplus.wapp.modules.job.service.BPMSInterfaceService;
import com.xforceplus.wapp.modules.job.utils.HttpRequestUtils;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.text.SimpleDateFormat;
import java.util.*;

@PropertySource(value = {"classpath:config.properties"})
@Service
public class BPMSInterfaceServiceImpl implements BPMSInterfaceService {
    private static Logger logger = LoggerFactory.getLogger(BPMSInterfaceServiceImpl.class);
    @Autowired
    private VendorMasterDao vendorMasterDao;

    @Value("${vendorMaster.url}")
    private String url;
    /**
     * 执行VenderMaster任务
     */
    @Override
    public void executeVender() {
        logger.info("--------获取VendorMaster数据开始------");
        String beginTime = "";
        String endTime = "";
        // 获取时间段  , 从数据字典里获取
       List<DictPo> list  = vendorMasterDao.findDict();
        if(list!=null&&list.size()!=0){

            for (DictPo enttiy :list) {
                if ("beginTime".equals(enttiy.getDictname())) {
                    beginTime = enttiy.getDictcode();
                }
                if ("endTime".equals(enttiy.getDictname())) {
                    endTime = enttiy.getDictcode();
                }
                if ("status".equals(enttiy.getDictname())){
                    if("1".equals(enttiy.getDictcode())){
                        //获取前一天的时间
                        Date dNow = new Date();   //当前时间
                        Date dBefore = new Date();

                        Calendar calendar = Calendar.getInstance(); //得到日历
                        calendar.setTime(dNow);//把当前时间赋给日历
//                        calendar.add(Calendar.DAY_OF_MONTH, -1);  //设置为前一天
                        dBefore = calendar.getTime();   //得到前一天的时间
                        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd"); //设置时间格式
                        beginTime = sdf.format(dBefore);    //格式化前一天
                        calendar.add(Calendar.DAY_OF_MONTH, 1); //设置加一天
                        dBefore = calendar.getTime();   //得到前一天的时间
                        endTime= sdf.format(dBefore);    //格式化前一天
                    }
                }
            }
        }else{
            //获取前一天的时间
            Date dNow = new Date();   //当前时间
            Date dBefore = new Date();

            Calendar calendar = Calendar.getInstance(); //得到日历
            calendar.setTime(dNow);//把当前时间赋给日历
//            calendar.add(Calendar.DAY_OF_MONTH, -1);  //设置为前一天
            dBefore = calendar.getTime();   //得到前一天的时间
            SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd"); //设置时间格式
            beginTime = sdf.format(dBefore);    //格式化前一天
            calendar.add(Calendar.DAY_OF_MONTH, 1); //设置加一天
            dBefore = calendar.getTime();   //得到前一天的时间
            endTime= sdf.format(dBefore);    //格式化前一天
        }
        // 保存数据库记录
        VendorLog vlog = new VendorLog();
        vlog.setReqDate(new Date());
        vlog.setReqText(beginTime+"&-&"+endTime);
        vlog.setStatus("0");

        String params  = getRequestParams(beginTime,endTime);
        try {
           String  repsont = HttpRequestUtils.doXMLCityPost(params, url);  //请求接口 content_type=text/xml; charset=utf-8

           vlog.setResDate(new Date());
           vlog.setResText(repsont);
           List<Table1> VendorList = parseXml(repsont);
            System.out.println(VendorList.size());
           vendorMasterDao.inserVendorLog(vlog);  //插入日志信息
           saveOrgAndUser(VendorList);  //插入机构用户信息
            logger.info("--------获取VendorMaster数据结束------");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 保存vendorMaster信息到数据库中
     * @param vendorList
     */
    private void saveOrgAndUser(List<Table1> vendorList) {

         if(vendorList.size()>0) {
             Integer shangpinroleid = vendorMasterDao.findRoleIdByCode("WRMSP");
             Integer feiyongroleid = vendorMasterDao.findRoleIdByCode("WRMFY");
             for (Table1 data : vendorList) {
                 // 遍历集合，先拿供应商号查询用户表，再去查机构表是否有
                 UserEntity usercount = vendorMasterDao.findUserById(data.getVendorNumber());
                 if("TRUE".equals(data.getDeletionStatus())){
                     data.setUserStatus("4");
                 }else{
                     data.setUserStatus("1");
                 }
                 if (usercount != null) {
                     //更新用户信息

                     int count = vendorMasterDao.findOrgByTaxnoAndId(data.getTaxNumber(), usercount.getOrgid()+"",data.getBankAccount());
                     if (count > 0) {
                            //更新原来用户信息
                           vendorMasterDao.updateUser(data,"interface");
                     }else{
                         //更新机构信息 以及 用户信息
                         vendorMasterDao.updateOrg(data, usercount.getOrgid());
                         vendorMasterDao.updateUser(data,"interface");
                     }
                 }else{
                     // 没有用户信息， 查询机构信息是否存在
                        OrganizationEntity count = vendorMasterDao.findOrgByTaxno(data.getTaxNumber(),data.getVendorName());
                        if(count!=null) {
                            // 有 -- 插入用户信息
                            data.setOrgid(Integer.valueOf(count.getOrgid()+""));
                            String encodePW = MD5Utils.encode(data.getVendorNumber());

                            vendorMasterDao.insertUser(data,encodePW,"interface");
                            Integer userid = data.getUserid();
                            Integer roid = 0;
                            //插入角色表
                            if(data.getTypeCode().startsWith("P")){
                                 roid = shangpinroleid;
                            }
                            if(data.getTypeCode().startsWith("E")){
                                 roid = feiyongroleid;
                            }
                            vendorMasterDao.insertRole(userid,roid);
                        }else {
                            // 无 -- 插入机构信息  再插入用户信息
                            int length = data.getTaxNumber().length();
                            String orgcode="";
                            if(length>6) {
                                orgcode = data.getTaxNumber().substring(length - 6, length);
                            }
                            String company="walmart";
                            int status = vendorMasterDao.insertOrgInfo(data,"interface",orgcode,company);
                            data.setOrgid(data.getOrgid());
                            String encodePW = MD5Utils.encode(data.getVendorNumber());
                            vendorMasterDao.insertUser(data,encodePW,"interface");
                            Integer userid = data.getUserid();
                            Integer roid = 0;
                            //插入角色表
                            if(data.getTypeCode().startsWith("P")){ //商品
                                roid = shangpinroleid;
                            }
                            if(data.getTypeCode().startsWith("E")){ //费用
                                roid = feiyongroleid;
                            }
                            vendorMasterDao.insertRole(userid,roid);
                        }
                 }
             }
         }


    }

    /**
     * 解析vendorMaster数据
     * @param repsont
     * @return
     */
    private List<Table1> parseXml(String repsont)  {
        List<Table1> list = new LinkedList<Table1>();
        try {
            Document doc = DocumentHelper.parseText(repsont);
            Element rootElt = doc.getRootElement(); // 获取根节点
            Iterator iters = rootElt.elementIterator("Body");  //根据返回报文结构，获取返回数据
            while (iters.hasNext()) {
                Element itemEle = (Element) iters.next();
                Iterator itersElIterator = itemEle.elementIterator("GetVendorInfoResponse");
                while (itersElIterator.hasNext()) {
                    Element res = (Element) itersElIterator.next();
                    Iterator restultElIterator = res.elementIterator("GetVendorInfoResult");
                    while (restultElIterator.hasNext()) {
                        Element dif = (Element) restultElIterator.next();
                        Iterator dif1 = dif.elementIterator("diffgram");
                        while (dif1.hasNext()) {
                            Element set = (Element) dif1.next();
                            Iterator set1 = set.elementIterator("NewDataSet");
                            while (set1.hasNext()) {
                                Element dataSet = (Element) set1.next();
                                Iterator dataSet1 = dataSet.elementIterator("Table1");
                                while (dataSet1.hasNext()) {
                                    Element infos = (Element) dataSet1.next();
                                    Table1 table = new Table1();
                                    String no =infos.elementTextTrim("VendorNumber");
                                    String zero = "";
                                    if(no.length()<6){
                                        int e = 6-no.length();
                                        for(int i=0;i<e;i++){
                                            zero=zero+"0";
                                        }
                                        no = zero+no;
                                    }
                                    table.setVendorNumber(no); //供应商号
                                    table.setVendorName(infos.elementTextTrim("VendorName")); //供应商名称
                                    table.setTypeCode(infos.elementTextTrim("TypeCode"));  //供应商类型
                                    table.setTaxNumber(infos.elementTextTrim("TaxNumber")); //供应商税号
                                    table.setFinanceTelephoneNumber1(infos.elementTextTrim("TelephoneNumber1")); //财务电话号码
                                    table.setFinanceFaxNumber1(infos.elementTextTrim("FinanceFaxNumber1")); //财务传真号码
                                    table.setFinanceContactPerson1(infos.elementTextTrim("ContactPerson1")); //财务联系人
                                    table.setEmailAddress1(infos.elementTextTrim("EmailAddress1")); //财务人邮箱
                                    table.setAddress1(infos.elementTextTrim("Address1")); //财务人邮箱
                                    table.setBankName(infos.elementTextTrim("BankName"));
                                    table.setBankAccount(infos.elementTextTrim("BankAccount"));
                                    table.setHoldStatus(infos.elementTextTrim("HoldStatus"));
                                    table.setDeletionStatus(infos.elementTextTrim("DeletionStatus"));
                                    list.add(table);
                                }
                            }
                        }

                    }
                }

            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return list;
    }

    private String getRequestParams(String beginTime, String endTime) {
        String params = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ven=\"http://Walmart.ChinaISD.HomeofficeTeam/VendorInfoService/\">" +
                "   <soapenv:Header/>" +
                "   <soapenv:Body>" +
                "      <ven:GetVendorInfo>" +
                "         <ven:FromUpdateDateTime>"+beginTime+"</ven:FromUpdateDateTime>" +
                "         <ven:ToUpdateDateTime>"+endTime+"</ven:ToUpdateDateTime>" +
                "      </ven:GetVendorInfo>" +
                "   </soapenv:Body>" +
                "</soapenv:Envelope>";
        return params;
    }



    public static void main(String[] args) throws Exception {
//        JaxWsDynamicClientFactory dcf = JaxWsDynamicClientFactory.newInstance();
//      //  Client client = dcf.createClient("http://vendormasterservice.cn.wal-mart.com/VendorMasterPowerService.asmx");
//        Client client = dcf.createClient("VendorMasterPowerService.wsdl");
//
//        // 需要密码的情况需要加上用户名和密码
//        // client.getOutInterceptors().add(new ClientLoginInterceptor(USER_NAME, PASS_WORD));
//        Object[] objects = new Object[0];
//        try {
//            // invoke("方法名",参数1,参数2,参数3....);GetVendorInfo
//            SimpleDateFormat sim = new SimpleDateFormat("yyyy-MM-dd");
//            Date startTime = sim.parse("2018-01-01");
//            Date endTime = sim.parse("2018-01-02");
//
//            objects = client.invoke("GetVendorInfo", converToXMLGregorianCalendar(startTime),converToXMLGregorianCalendar(endTime));
//            GetVendorInfoResponse.GetVendorInfoResult result= new GetVendorInfoResponse.GetVendorInfoResult();
//             result = (GetVendorInfoResponse.GetVendorInfoResult)objects[0];
//           JAXBContext context =  JAXBContext.newInstance(GetVendorInfoResponse.class);
//            Marshaller mar= context.createMarshaller();
//            mar.setProperty(Marshaller.JAXB_ENCODING,"UTF-8");
//            mar.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT,true);
//            mar.setProperty(Marshaller.JAXB_FRAGMENT,false);
//            mar.marshal(objects,System.out);
//            System.out.println("返回数据:" + result.toString());
//        }
//        catch (Exception e) {
//            e.printStackTrace();
//
//        }
//         String params = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ven=\"http://Walmart.ChinaISD.HomeofficeTeam/VendorInfoService/\">" +
//                 "   <soapenv:Header/>" +
//                 "   <soapenv:Body>" +
//                 "      <ven:GetVendorInfo>" +
//                 "         <ven:FromUpdateDateTime>2018-01-01</ven:FromUpdateDateTime>" +
//                 "         <ven:ToUpdateDateTime>2018-01-02</ven:ToUpdateDateTime>" +
//                 "      </ven:GetVendorInfo>" +
//                 "   </soapenv:Body>" +
//                 "</soapenv:Envelope>";
//
//        String repsont = HttpRequestUtils.doXMLPost(params, "http://vendormasterservice.cn.wal-mart.com/VendorMasterPowerService.asmx");
//        System.out.println("返回数据:" + repsont);

//        Document doc = DocumentHelper.parseText(response);
//        Element rootElt = doc.getRootElement(); // 获取根节点
//        System.out.println("根节点：" + rootElt.getName()); // 拿到根节点的名称
//        Iterator iters = rootElt.elementIterator("Body");
//        while (iters.hasNext()) {
//            Element itemEle = (Element) iters.next();
//            Iterator itersElIterator = itemEle.elementIterator("GetVendorInfoResponse"); // 获取子节点body下的子节点form
//            while (itersElIterator.hasNext()) {
//                Element res = (Element) itersElIterator.next();
//                Iterator restultElIterator = res.elementIterator("GetVendorInfoResult");
//                while (restultElIterator.hasNext()) {
//                    Element dif = (Element) restultElIterator.next();
//                    Iterator dif1 = dif.elementIterator("diffgram");
//                    while (dif1.hasNext()) {
//                        Element set = (Element) dif1.next();
//                        Iterator set1 = set.elementIterator("NewDataSet");
//                        while (set1.hasNext()){
//                            Element dataSet = (Element) set1.next();
//                            Iterator dataSet1 = dataSet.elementIterator("Table1");
//                            while (dataSet1.hasNext()) {
//                                Element infos = (Element)dataSet1.next();
//                                String VendorNumber = infos.elementTextTrim("VendorNumber");
//                                String VendorName = infos.elementTextTrim("VendorName");
//                                String TypeCode = infos.elementTextTrim("TypeCode");
//                                String TaxNumber = infos.elementTextTrim("TaxNumber");
//                                String FinanceTelephoneNumber1 = infos.elementTextTrim("FinanceTelephoneNumber1"); //财务电话号码
//                                String FinanceFaxNumber1 = infos.elementTextTrim("FinanceFaxNumber1"); //财务传真号码
//                                String FinanceContactPerson1 = infos.elementTextTrim("FinanceContactPerson1"); //财务联系人
//                                String EmailAddress1 = infos.elementTextTrim("EmailAddress1"); //财务人邮箱
//                                System.out.println(VendorNumber+":"+VendorName+":"+TypeCode+":"+TaxNumber+":"+FinanceTelephoneNumber1+":"+FinanceFaxNumber1+":"+FinanceContactPerson1+":"+EmailAddress1);
//                            }
//                        }
//                    }
//
//                }
//            }
//
//        }
    }

    public static XMLGregorianCalendar converToXMLGregorianCalendar(Date date){
        GregorianCalendar cal = new GregorianCalendar();
        cal.setTime(date);
        XMLGregorianCalendar gc = null;
        try{
            gc= DatatypeFactory.newInstance().newXMLGregorianCalendar(cal);
        }catch (Exception e){
            e.printStackTrace();
        }
        return gc;
    }
}
