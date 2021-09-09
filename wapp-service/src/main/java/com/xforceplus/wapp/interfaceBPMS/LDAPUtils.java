package com.xforceplus.wapp.interfaceBPMS;

import com.xforceplus.wapp.modules.base.entity.Staff;
import com.xforceplus.wapp.modules.job.utils.HttpRequestUtils;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.directory.*;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@PropertySource(value = {"classpath:config.properties"})
//@Service
public class LDAPUtils {

    private static String LDAP_SEARCH_BASE_USER_CN="";
    private DirContext directoryContext = null;
    private String hrmsUrl=null;
    private CostAppliction costAppliction=null;
    public LDAPUtils() throws Exception {
        try {
            PropertiesConfiguration config = new PropertiesConfiguration("config.properties");
            LDAP_SEARCH_BASE_USER_CN = config.getString("LDAP_SEARCH_BASE_USER_CN");
            Hashtable<String, String> env = new Hashtable<String, String>();
            env.put(Context.INITIAL_CONTEXT_FACTORY,
                    config.getString("LDAP_INITIAL_CONTEXT_FACTORY"));
            env.put(Context.PROVIDER_URL, config.getString("LDAP_PROVIDER_URL_CN"));
            env.put(Context.SECURITY_AUTHENTICATION, "DIGEST-MD5");
            env.put(Context.SECURITY_PRINCIPAL, config.getString("LDAP_QUERY_SECURITY_PRINCIPAL"));
            env.put(Context.SECURITY_CREDENTIALS,config.getString("LDAP_QUERY_SECURITY_CREDENTIALS") );
            directoryContext = new InitialDirContext(env);
            hrmsUrl = config.getString("hrms.url");
            costAppliction = CostAppliction.getInstance();
        } catch (Exception ex) {
            throw new RuntimeException("Error when get LDAP initial parameters!", ex);
        }
    }




    /**
     * 关闭
     */
    public void close() {
        if (directoryContext != null) {
            try {
                directoryContext.close();
            } catch (Exception ex) {
            }
        }
    }

    /**
     * Find user by Walmart userid.
     *
     * @param userid
     * @return
     */
    public Staff getUserByID(String email) throws Exception {
        Staff user = null;
        String filter = "(&(objectClass=user)(mail=" + email + "))";
        SearchControls l_oControls = new SearchControls();
        l_oControls.setSearchScope(SearchControls.SUBTREE_SCOPE);
        NamingEnumeration<SearchResult> userList = directoryContext.search(
                LDAP_SEARCH_BASE_USER_CN, filter, l_oControls);
        if (userList.hasMoreElements()) {
            user = new Staff();
            user.setEmail(email);
            SearchResult result = userList.next();
            Attributes attributes = result.getAttributes();

            Attribute displayname = attributes.get("wm-IdentificationNumber");
            if (displayname != null) {
                NamingEnumeration<?> values = displayname.getAll();
                while (values.hasMoreElements()) {
                    user.setWinID(values.next().toString());
                }
            } else {
                user.setWinID("");
            }
            Attribute ad = attributes.get("sAMAccountName");
            if (ad != null) {
                NamingEnumeration<?> values = ad.getAll();
                while (values.hasMoreElements()) {
                    user.setSamAd(values.next().toString());
                }
            } else {
                user.setSamAd("");
            }

//            Attribute department = attributes.get("department");
//            if (department != null) {
//                NamingEnumeration<?> values = department.getAll();
//                while (values.hasMoreElements()) {
//                    user.setDepartmentName(values.next().toString());
//                }
//            } else {
//                user.setDepartmentName("");
//            }
//
//            Attribute extensionAttribute2 = attributes.get("extensionAttribute2");
//            if (extensionAttribute2 != null) {
//                NamingEnumeration<?> values = extensionAttribute2.getAll();
//                while (values.hasMoreElements()) {
//                    user.setTeamName(values.next().toString());
//                }
//            } else {
//                user.setTeamName("");
//            }
//
//            Attribute title = attributes.get("title");
//            if (title != null) {
//                NamingEnumeration<?> values = title.getAll();
//                while (values.hasMoreElements()) {
//                    user.setTitle(values.next().toString());
//                }
//            } else {
//                user.setTitle("");
//            }

//            Attribute manager = attributes.get("manager");
//            if (manager != null) {
//                NamingEnumeration<?> values = manager.getAll();
//                while (values.hasMoreElements()) {
//                    user.setManager(values.next().toString());
//                }
//            } else {
//                user.setManager("");
//            }
//
//            Attribute directReports = attributes.get("directReports");
//            List<Object> reports = new ArrayList<>();
//            if (directReports != null) {
//                NamingEnumeration<?> values = directReports.getAll();
//                while (values.hasMoreElements()) {
//                    reports.add(values.next());
//                }
//            }
//            user.setDirectReports(reports);
//
//            Attribute memberOf = attributes.get("memberOf");
//            List<Object> members = new ArrayList<>();
//            if (memberOf != null) {
//                NamingEnumeration<?> values = memberOf.getAll();
//                while (values.hasMoreElements()) {
//                    members.add(values.next());
//                }
//            }
//            user.setMemberOf(members);
//
//            Attribute email = attributes.get("mail");
//            if (email != null) {
//                NamingEnumeration<?> values = email.getAll();
//                while (values.hasMoreElements()) {
//                    user.setEmail(values.next().toString());
//                }
//            } else {
//                user.setEmail("");
//            }
//
        }
       if(user!=null){
           String param = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:hrm=\"http://Walmart.ChinaISD.HomeofficeTeam/HRMSService/\">" +
                "   <soapenv:Header/>" +
                "   <soapenv:Body>" +
                "      <hrm:GetEmployeeInfoB>" +
                "         <hrm:EMPNO>"+user.getSamAd()+"</hrm:EMPNO>" +
                "         <hrm:LANGUAGE></hrm:LANGUAGE>" +
                "      </hrm:GetEmployeeInfoB>" +
                "   </soapenv:Body>" +
                "</soapenv:Envelope>";
//        String url="http://lcnnt51001.cn.wal-mart.com:8010/Web_HRMS.asmx";
        String  repsont = HttpRequestUtils.doXMLCityPost(param, hrmsUrl);  //请求接口
         String staffno =parseCbzxXml(repsont);
           user.setStaffNo(staffno);
         //调用接口校验是否职等超过十二级
           String levelParam = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:hrm=\"http://Walmart.ChinaISD.HomeofficeTeam/HRMSService/\">" +
                   "   <soapenv:Header/>" +
                   "   <soapenv:Body>" +
                   "      <hrm:GetEmployeeInfo>" +
                   "         <hrm:EMPNO>"+user.getStaffNo()+"</hrm:EMPNO>" +
                   "         <hrm:LANGUAGE></hrm:LANGUAGE>" +
                   "      </hrm:GetEmployeeInfo>" +
                   "   </soapenv:Body>" +
                   "</soapenv:Envelope>";
           String  levelResponse = HttpRequestUtils.doXMLCityPost(levelParam, hrmsUrl);  //请求接口
           boolean isOver = parseLevel(levelResponse);
           if(isOver){
               return user;
           }else{
               return null;
           }
       }
        return user;
    }

    private boolean parseLevel(String levelResponse) {
        String level="";
        try {
            Document doc = DocumentHelper.parseText(levelResponse);
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
                                    level = infos.elementTextTrim("posno"); //员工号

                                }
                            }
                        }

                    }
                }


            }
        }catch (Exception e){
            e.printStackTrace();
        }
        if("".equals(level)||level==null){
            return false;
        }
        String reg=".*[a-zA-Z]+.*";
        Matcher m  = Pattern.compile(reg).matcher(level);
        if(m.matches()){//如果带有字符转换成数值
            //调用接口查询对应数值
            Integer intlevel = costAppliction.exchange(level);
            if(intlevel==null){
                return false;
            }else{
                if(intlevel-12>=0){
                    return false;
                }else{
                    return true;
                }
            }
        }
        Integer le = Integer.valueOf(level);
        if(le-12>=0){
            return false;
        }else{
            return true;
        }
    }

    private String parseCbzxXml(String repsont)  {
        try {
            Document doc = DocumentHelper.parseText(repsont);
            Element rootElt = doc.getRootElement(); // 获取根节点
            Iterator iters = rootElt.elementIterator("Body");  //根据返回报文结构，获取返回数据
            while (iters.hasNext()) {
                Element itemEle = (Element) iters.next();
                Iterator itersElIterator = itemEle.elementIterator("GetEmployeeInfoBResponse");
                while (itersElIterator.hasNext()) {
                    Element res = (Element) itersElIterator.next();
                    Iterator restultElIterator = res.elementIterator("GetEmployeeInfoBResult");
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
                                    return infos.elementTextTrim("EMPNO"); //员工号

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
    public static void main(String arg[]) throws Exception {
//      LDAPUtils ldap = new LDAPUtils();
//      ldap.getUserByID("c4tang");
        ADAuthenticateService au =   new  ADAuthenticateService();
        boolean b = au.authenticate("vn088jh","Dxhy@2018!");
       if(b){
           DirContext ctx = null;
            Hashtable<String, String> env = new Hashtable<String, String>();
           env.put(Context.INITIAL_CONTEXT_FACTORY,
                   "com.sun.jndi.ldap.LdapCtxFactory");
           env.put(Context.PROVIDER_URL, "ldap://cnnts010.cn.wal-mart.com/");
           env.put(Context.SECURITY_AUTHENTICATION, "DIGEST-MD5");

           env.put(Context.SECURITY_PRINCIPAL, "vn088jh");
           env.put(Context.SECURITY_CREDENTIALS, "Dxhy@2018!");
           ctx = new InitialDirContext(env);
           SearchControls sch = new SearchControls();
           sch.setSearchScope(SearchControls.SUBTREE_SCOPE);
           String filter = "(&(objectClass=user)(mail=" + "Jeff.yao@walmart.com" + "))";
           NamingEnumeration<SearchResult> userList = ctx.search(
                   "DC=CN,DC=Wal-Mart,DC=com", filter, sch);
           while(userList.hasMoreElements()){
               NamingEnumeration<? extends Attribute> attrs  = userList.next().getAttributes().getAll();
               while (attrs.hasMore()){
                   Attribute attr = attrs.next();
                   System.out.println(attr.getID()+":"+attr.get());
               }
           }
       }

    }
}
