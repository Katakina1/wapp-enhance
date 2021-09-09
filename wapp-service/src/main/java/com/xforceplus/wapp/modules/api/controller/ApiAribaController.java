package com.xforceplus.wapp.modules.api.controller;

import com.xforceplus.wapp.common.utils.R;
import com.xforceplus.wapp.modules.api.annotation.AuthIgnore;
import com.xforceplus.wapp.modules.api.entity.AribaCheckEntity;
import com.xforceplus.wapp.modules.api.entity.AribaCheckReturn;
import com.xforceplus.wapp.modules.api.service.AribaService;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sun.misc.BASE64Decoder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.ParseException;

/**
 * TODO
 * @author wyman
 * @date 2020-05-05 9:17
 **/
@RestController
@RequestMapping("/aribaAPI")
public class ApiAribaController {

    @Value("${basicAuth.ariba}")
    private String basicAuthAriba;



    @Autowired
    private AribaService aribaService;



    /**
     *
     */
    @AuthIgnore
    @PostMapping("/invoice/check")
    public String check(HttpServletRequest request, HttpServletResponse response,@RequestBody AribaCheckEntity aribaCheckEntity) throws ParseException {
        String auth = request.getHeader("Authorization");
        int status=checkHeaderAuth(request,auth);
        if (status!=200){
            response(status,response);
//            JSONObject.fromObject(aribaCheckEntity).toString()
            aribaService.saveRequest(JSONObject.fromObject(aribaCheckEntity).toString(),getFromBase64(auth),"发票验证", JSONObject.fromObject(R.error().put("msg", "权限验证不通过").put("code",status)).toString());
            return JSONObject.fromObject((R.error().put("msg", "权限验证不通过").put("code",status))).toString();
        }else{

            AribaCheckReturn aribaCheckReturn=aribaService.check(aribaCheckEntity);

            aribaService.saveRequest(JSONObject.fromObject(aribaCheckEntity).toString(),auth,"发票验证",JSONObject.fromObject(aribaCheckReturn).toString());
            return JSONObject.fromObject(aribaCheckReturn).toString();
        }
    }

    @AuthIgnore
    @PostMapping("/invoice/signInMark")
    public String signInMark(HttpServletRequest request, HttpServletResponse response,@RequestBody AribaCheckEntity aribaCheckEntity) throws ParseException {
        String auth = request.getHeader("Authorization");
        int status=checkHeaderAuth(request,auth);
        if (status!=200){
            response(status,response);
            aribaService.saveRequest(JSONObject.fromObject(aribaCheckEntity).toString(),getFromBase64(auth),"可收票", JSONObject.fromObject(R.error().put("msg", "权限验证不通过").put("code",status)).toString());
            return JSONObject.fromObject(R.error().put("msg", "权限验证不通过").put("code",status)).toString();
        }else{
            AribaCheckReturn aribaCheckReturn=aribaService.signInMark(aribaCheckEntity);

            aribaService.saveRequest(JSONObject.fromObject(aribaCheckEntity).toString(),auth,"可收票",JSONObject.fromObject(aribaCheckReturn).toString());
            return JSONObject.fromObject(aribaCheckReturn).toString();
        }
    }
    @AuthIgnore
    @PostMapping("/invoice/auth")
    public String auth(HttpServletRequest request, HttpServletResponse response,@RequestBody AribaCheckEntity aribaCheckEntity) throws ParseException {
        String auth = request.getHeader("Authorization");

        int status=checkHeaderAuth(request,auth);
        if (status!=200){
            response(status,response);
            aribaService.saveRequest(JSONObject.fromObject(aribaCheckEntity).toString(),getFromBase64(auth),"认证状态更新", JSONObject.fromObject(R.error().put("msg", "权限验证不通过").put("code",status)).toString());
            return JSONObject.fromObject(R.error().put("msg", "权限验证不通过").put("code",status)).toString();
        }else{
            AribaCheckReturn aribaCheckReturn=aribaService.auth(aribaCheckEntity);

            aribaService.saveRequest(JSONObject.fromObject(aribaCheckEntity).toString(),auth,"认证状态更新",JSONObject.fromObject(aribaCheckReturn).toString());
            return JSONObject.fromObject(aribaCheckReturn).toString();
        }
    }

    @AuthIgnore
    @PostMapping("/e-invoice/upload")
    public String upload(HttpServletRequest request, HttpServletResponse response,@RequestBody AribaCheckEntity aribaCheckEntity) throws ParseException {
        String auth = request.getHeader("Authorization");
        int status=checkHeaderAuth(request,auth);
        if (status!=200){
            response(status,response);
            aribaService.saveRequest(JSONObject.fromObject(aribaCheckEntity).toString(),getFromBase64(auth),"电票上传", JSONObject.fromObject(R.error().put("msg", "权限验证不通过").put("code",status)).toString());
            return JSONObject.fromObject(R.error().put("msg", "权限验证不通过").put("code",status)).toString();
        }else{
            AribaCheckReturn aribaCheckReturn=aribaService.upload(aribaCheckEntity);

            aribaService.saveRequest(JSONObject.fromObject(aribaCheckEntity).toString(),auth,"电票上传",JSONObject.fromObject(aribaCheckReturn).toString());
            return JSONObject.fromObject(aribaCheckReturn).toString();
        }
    }


    public static void main(String[] args) throws UnsupportedEncodingException {
        String s1="http://test-sapdctm.wal-mart.com:80/assap/archivelink/dmgbsap_tst?get&amp;pVersion=0046&amp;contRep=E3&amp;docId=000D3A7B55E51EDAA9AF905D2C88E029&amp;accessMode=r&amp;authId=CN=DR3,OU=I0020289037,OU=SAPWebAS,O=SAPTrustCommunity,C=DE&amp;expiration=20200603114145";
        String s2=URLDecoder.decode(s1,"UTF-8");
        System.out.println(s1);
        System.out.println(s2);
    }

    @Scheduled(cron = "${basicAuth.scheduled}")
    public void getTaxInformation() {
        aribaService.check();
    }

    private void response(int status,HttpServletResponse response){
        response.setStatus(status);
        response.setHeader("Cache-Control", "no-store");
        response.setDateHeader("Expires", 0);
        String authenticateVaule=status==401?"Unauthorized":"Forbidden";
        response.setHeader("WWW-authenticate", "Basic Realm=\""+authenticateVaule+"\"");
    }
    private int checkHeaderAuth(HttpServletRequest request,String auth){
        int authorizationlenght=6;
        if (auth != null && auth.length() > authorizationlenght&&auth.contains("Basic")) {
            auth = auth.substring(authorizationlenght, auth.length());
            String decodedAuth = getFromBase64(auth);
            if(basicAuthAriba.equals(decodedAuth)){
                return 200;
            }else{
                return 403;
            }
        }else{
            return 401;
        }
    }

    private String getFromBase64(String s) {
        if (s == null) {
            return null;
        }
        BASE64Decoder decoder = new BASE64Decoder();
        try {
            byte[] b = decoder.decodeBuffer(s);
            return new String(b);
        } catch (Exception e) {
            return null;
        }
    }


}
