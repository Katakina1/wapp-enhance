package com.xforceplus.wapp.interfaceBPMS;/*
package com.xforceplus.wapp.interfaceBPMS;

import com.xforceplus.wapp.modules.job.pojo.vendorMaster.Table1;
import com.xforceplus.wapp.modules.job.utils.HttpRequestUtils;
//import com.walmart.china.common.util.LDAPUtils;
import org.apache.xmlbeans.StringEnumAbstractBase;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;


public class LDAPTest {



    public static void main(String[] args) throws Exception {
        String param = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:hrm=\"http://Walmart.ChinaISD.HomeofficeTeam/HRMSService/\">" +
                "   <soapenv:Header/>" +
                "   <soapenv:Body>" +
                "      <hrm:GetEmployeeInfo>" +
                "         <!--Optional:-->" +
                "         <hrm:EMPNO>5476</hrm:EMPNO>" +
                "         <!--Optional:-->" +
                "         <hrm:LANGUAGE></hrm:LANGUAGE>" +
                "      </hrm:GetEmployeeInfo>" +
                "   </soapenv:Body>" +
                "</soapenv:Envelope>";
        String url="http://lcnnt51001.cn.wal-mart.com:8010/Web_HRMS.asmx";
        String  repsont = HttpRequestUtils.doXMLCityPost(param, url);  //请求接口
        List<Table> cbzx = new LDAPTest().parseXml(repsont);
        System.out.println(cbzx);
//        List<Table> list = new LDAPTest().parseXml(repsont);
//        String param="<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:tem=\"http://tempuri.org/\">" +
//                "   <soapenv:Header/>" +
//                "   <soapenv:Body>" +
//                "      <tem:GetAllCity/>" +
//                "   </soapenv:Body>" +
//                "</soapenv:Envelope>";
//        String url="http://pcnnt20029a.homeoffice.cn.wal-mart.com:8081/BasicService.asmx";
//        String  repsont = HttpRequestUtils.doXMLCityPost(param, url);  //请求接口
//        List<Table> list = new LDAPTest().parseXml(repsont);
//        System.out.println(list.size());

    }

    */
/**
     * 解析City数据
     * @param repsont
     * @return
     *//*

    private List<Table> parseXml(String repsont)  {
        List<Table> list = new LinkedList<Table>();
        try {
            Document doc = DocumentHelper.parseText(repsont);
            Element rootElt = doc.getRootElement(); // 获取根节点
            Iterator iters = rootElt.elementIterator("Body");  //根据返回报文结构，获取返回数据
            while (iters.hasNext()) {
                Element itemEle = (Element) iters.next();
                Iterator itersElIterator = itemEle.elementIterator("GetAllCityResponse");
                while (itersElIterator.hasNext()) {
                    Element res = (Element) itersElIterator.next();
                    Iterator restultElIterator = res.elementIterator("GetAllCityResult");
                    while (restultElIterator.hasNext()) {
                        Element dif = (Element) restultElIterator.next();
                        Iterator dif1 = dif.elementIterator("diffgram");
                        while (dif1.hasNext()) {
                            Element set = (Element) dif1.next();
                            Iterator set1 = set.elementIterator("NewDataSet");
                            while (set1.hasNext()) {
                                Element dataSet = (Element) set1.next();
                                Iterator dataSet1 = dataSet.elementIterator("Table");
                                while (dataSet1.hasNext()) {
                                    Element infos = (Element) dataSet1.next();
                                    Table table = new Table();
                                    table.setCity(infos.elementTextTrim("city")); //供应商号
                                    table.setCityCode(infos.elementTextTrim("city_code")); //供应商名称
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

    */
/**
     * 解析成本中心返回报文
     * @param repsont
     * @return
     *//*

    private String parseCbzxXml(String repsont)  {
        try {
            Document doc = DocumentHelper.parseText(repsont);
            Element rootElt = doc.getRootElement(); // 获取根节点
            Iterator iters = rootElt.elementIterator("Body");  //根据返回报文结构，获取返回数据
            while (iters.hasNext()) {
                Element itemEle = (Element) iters.next();
                Iterator itersElIterator = itemEle.elementIterator("GetEmployeeInfoResponse");
                while (itersElIterator.hasNext()) {
                    Element res = (Element) itersElIterator.next();
                    Iterator restultElIterator = res.elementIterator("GetEmployeeInfoResult");
                    while (restultElIterator.hasNext()) {
                        Element dif = (Element) restultElIterator.next();
                        Iterator dif1 = dif.elementIterator("diffgram");
                        while (dif1.hasNext()) {
                            Element set = (Element) dif1.next();
                            Iterator set1 = set.elementIterator("NewDataSet");
                            while (set1.hasNext()) {
                                Element dataSet = (Element) set1.next();
                                Iterator dataSet1 = dataSet.elementIterator("Table");
                                while (dataSet1.hasNext()) {
                                    Element infos = (Element) dataSet1.next();
                                    return infos.elementTextTrim("cosno"); //成本中心

                                }
                            }
                        }

                    }
                }

            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return "";
    }
}
*/
