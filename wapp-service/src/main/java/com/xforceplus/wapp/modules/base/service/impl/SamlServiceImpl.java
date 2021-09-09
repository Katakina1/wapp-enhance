/**  
 * @Title:  SamlServiceImpl.java   
 * @Package com.xforceplus.wapp.modules.base.service.impl
 * @Description:    TODO
 * @author: jiaohongyang     
 * @date:   2019年1月15日 下午7:34:20   
 */  
package com.xforceplus.wapp.modules.base.service.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.xforceplus.wapp.modules.base.service.SamlService;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.math.BigInteger;
import java.net.URLEncoder;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.X509EncodedKeySpec;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;

import javax.xml.parsers.DocumentBuilder;

import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.opensaml.Configuration;
import org.opensaml.DefaultBootstrap;
import org.opensaml.common.SAMLVersion;
import org.opensaml.common.xml.SAMLConstants;
import org.opensaml.saml2.core.Assertion;
import org.opensaml.saml2.core.AuthnContextClassRef;
import org.opensaml.saml2.core.AuthnContextComparisonTypeEnumeration;
import org.opensaml.saml2.core.AuthnRequest;
import org.opensaml.saml2.core.Issuer;
import org.opensaml.saml2.core.NameIDPolicy;
import org.opensaml.saml2.core.RequestedAuthnContext;
import org.opensaml.saml2.core.Response;
import org.opensaml.saml2.core.impl.AuthnContextClassRefBuilder;
import org.opensaml.saml2.core.impl.AuthnRequestBuilder;
import org.opensaml.saml2.core.impl.IssuerBuilder;
import org.opensaml.saml2.core.impl.NameIDPolicyBuilder;
import org.opensaml.saml2.core.impl.RequestedAuthnContextBuilder;
import org.opensaml.xml.XMLObject;
import org.opensaml.xml.io.Marshaller;
import org.opensaml.xml.io.MarshallingException;
import org.opensaml.xml.io.Unmarshaller;
import org.opensaml.xml.io.UnmarshallerFactory;
import org.opensaml.xml.parse.BasicParserPool;
import org.opensaml.xml.security.x509.BasicX509Credential;
import org.opensaml.xml.util.XMLHelper;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
/**   
 * @ClassName:  SamlServiceImpl   
 * @Description:TODO
 * @author: jiaohongyang
 * @date:   2019年1月15日 下午7:34:20   
 *   
 */
@Service
public class SamlServiceImpl implements SamlService{
	private static Logger logger = Logger.getLogger(SamlServiceImpl.class);
	
	@Value("${filePathConstan.sso}")
	private String ssoPath;
    @Value("${sso.loginUrl}")
    private String ssoLoginUrl;
    @Value("${sso.WMIDP_Metadata_Dev}")
    private String WMIDPDevUrl;

	/**   
	 * <p>Title: getUserID</p>   
	 * <p>Description: </p>   
	 * @param responseMessage
	 * @return   
	 * @see com.xforceplus.wapp.modules.base.service.SamlService#getUserID(java.lang.String)
	 */  
	@Override
	public String getUserID(String responseMessage) {
		try {
			// String
			// certificateS=SDK.getRuleAPI().executeAtScript("@webconfig(login-sso-cert)");
			// if("".equals(certificateS)){
			// return null;
			// }
			// Read certificate
			String certificateS = getConentFromFile(ssoPath);
			if ("".equals(certificateS)) {
				return null;
			}
			// FileInputStream fis = new FileInputStream("./conf/walmart/sso.txt");
			// BufferedInputStream bis = new BufferedInputStream(fis);
			CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
			InputStream inputStream = new ByteArrayInputStream(Base64.decodeBase64(certificateS.getBytes("UTF-8")));
			X509Certificate certificate = (X509Certificate) certificateFactory.generateCertificate(inputStream);
			// inputStream.close();

			BasicX509Credential credential = new BasicX509Credential();
			KeyFactory keyFactory = KeyFactory.getInstance("RSA");
			X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(certificate.getPublicKey().getEncoded());
			PublicKey key = keyFactory.generatePublic(publicKeySpec);
			credential.setPublicKey(key);
			// Initialize the library
			DefaultBootstrap.bootstrap();
			// Parse response
			byte[] base64DecodedResponse = Base64.decodeBase64(responseMessage);
			logger.info(new String(base64DecodedResponse,"utf-8"));
			ByteArrayInputStream is = new ByteArrayInputStream(base64DecodedResponse);

			// Get parser pool manager
			BasicParserPool ppMgr = new BasicParserPool();
			ppMgr.setNamespaceAware(true);

			DocumentBuilder docBuilder = ppMgr.getBuilder();
			Document document = docBuilder.parse(is);
			Element element = document.getDocumentElement();
			UnmarshallerFactory unmarshallerFactory = Configuration.getUnmarshallerFactory();
			Unmarshaller unmarshaller = unmarshallerFactory.getUnmarshaller(element);
			XMLObject responseXmlObj = unmarshaller.unmarshall(element);
			Response responseObj = (Response) responseXmlObj;
			Assertion assertion = responseObj.getAssertions().get(0);

			org.opensaml.xml.signature.Signature sig = assertion.getSignature();
			org.opensaml.xml.signature.SignatureValidator validator = new org.opensaml.xml.signature.SignatureValidator(
					credential);
			validator.validate(sig);

			String subject = assertion.getSubject().getNameID().getValue();
			SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			DateTime failTime = assertion.getConditions().getNotOnOrAfter();//洛杉矶时间 
			Calendar cnnow=Calendar.getInstance();//中国
			cnnow.setTime(new Date());
			Calendar amnow=Calendar.getInstance();//美国 
			amnow.setTime(failTime.toDate());
			amnow.setTimeZone(cnnow.getTimeZone());
			if(cnnow.after(amnow)){
				System.err.println("sso登录失效(失效时间："+sdf.format(amnow.getTime())+",当前时间："+sdf.format(cnnow.getTime())+")");
//				throw new AWSException("sso登录失效(失效时间："+sdf.format(amnow.getTime())+",当前时间："+sdf.format(cnnow.getTime())+")");
			}
			
//			String email = null;
//			try {
//				email = ((XSStringImpl) assertion.getAttributeStatements().get(0).getAttributes().get(1)
//						.getAttributeValues().get(0)).getValue();
//			} catch (Exception e) {
//			}
//			String issuer = assertion.getIssuer().getValue();
//			String audience = assertion.getConditions().getAudienceRestrictions().get(0).getAudiences().get(0)
//					.getAudienceURI();
			String statusCode = responseObj.getStatus().getStatusCode().getValue();
			if (statusCode.endsWith("Success")) {
				// return email==null?subject:email;
				return subject;
			}else {
				return null;
			}
		}catch(Exception e) {
			e.printStackTrace();
			return null;
		}
		
	}
	
	public String getConentFromFile(String patch) {
		String content = "";
		BufferedReader br = null;
		try {
			File filename = new File(patch); // 要读取以上路径的input。txt文件
			if(!filename.exists()){
				return "";
			}
			InputStreamReader reader = new InputStreamReader(new FileInputStream(filename)); // 建立一个输入流对象reader
			br = new BufferedReader(reader); // 建立一个对象，它把文件内容转成计算机能读懂的语言
			String line = "";
			StringBuilder sb = new StringBuilder();
			line = br.readLine();
			while (line != null) {
				if (line.startsWith("---")) {
					line = br.readLine(); // 一次读入一行数据
					continue;
				}
				sb.append(line.trim());
				line = br.readLine(); // 一次读入一行数据
			}
			reader.close();
			content=sb.toString();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// 关闭BufferedReader
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return content;
	}
	
	@Override
	public String generateRequestURL(){
		try {
			String conentFromFile = getConentFromFile(WMIDPDevUrl);
			String base64SamlRequest = new String (Base64.encodeBase64(conentFromFile.getBytes("UTF-8")));
			return ssoLoginUrl + "&SAMLRequest="+ URLEncoder.encode(base64SamlRequest);
		}catch(Exception e){
			e.printStackTrace();
			return "";
		}
	}
	public static void main(String[] args) throws Exception {
		SamlServiceImpl impl = new SamlServiceImpl();
		String re = "PHNhbWxwOlJlc3BvbnNlIFZlcnNpb249IjIuMCIgSUQ9IlVlMEYwcGdCbHMySkZrWW1UYVg3b3lrbjJVcSIgSXNzdWVJbnN0YW50PSIyMDE5LTAzLTEzVDAzOjM5OjEyLjc0MFoiIHhtbG5zOnNhbWxwPSJ1cm46b2FzaXM6bmFtZXM6dGM6U0FNTDoyLjA6cHJvdG9jb2wiPjxzYW1sOklzc3VlciB4bWxuczpzYW1sPSJ1cm46b2FzaXM6bmFtZXM6dGM6U0FNTDoyLjA6YXNzZXJ0aW9uIj5QRi1ERVZCT1g8L3NhbWw6SXNzdWVyPjxzYW1scDpTdGF0dXM+PHNhbWxwOlN0YXR1c0NvZGUgVmFsdWU9InVybjpvYXNpczpuYW1lczp0YzpTQU1MOjIuMDpzdGF0dXM6U3VjY2VzcyIvPjwvc2FtbHA6U3RhdHVzPjxzYW1sOkFzc2VydGlvbiBJRD0iUUZRZUR4dWN4cXBPblhPLlhTNjdWaHBoSW0zIiBJc3N1ZUluc3RhbnQ9IjIwMTktMDMtMTNUMDM6Mzk6MTIuNzYyWiIgVmVyc2lvbj0iMi4wIiB4bWxuczpzYW1sPSJ1cm46b2FzaXM6bmFtZXM6dGM6U0FNTDoyLjA6YXNzZXJ0aW9uIj48c2FtbDpJc3N1ZXI+UEYtREVWQk9YPC9zYW1sOklzc3Vlcj48ZHM6U2lnbmF0dXJlIHhtbG5zOmRzPSJodHRwOi8vd3d3LnczLm9yZy8yMDAwLzA5L3htbGRzaWcjIj48ZHM6U2lnbmVkSW5mbz48ZHM6Q2Fub25pY2FsaXphdGlvbk1ldGhvZCBBbGdvcml0aG09Imh0dHA6Ly93d3cudzMub3JnLzIwMDEvMTAveG1sLWV4Yy1jMTRuIyIvPjxkczpTaWduYXR1cmVNZXRob2QgQWxnb3JpdGhtPSJodHRwOi8vd3d3LnczLm9yZy8yMDAxLzA0L3htbGRzaWctbW9yZSNyc2Etc2hhMjU2Ii8+PGRzOlJlZmVyZW5jZSBVUkk9IiNRRlFlRHh1Y3hxcE9uWE8uWFM2N1ZocGhJbTMiPjxkczpUcmFuc2Zvcm1zPjxkczpUcmFuc2Zvcm0gQWxnb3JpdGhtPSJodHRwOi8vd3d3LnczLm9yZy8yMDAwLzA5L3htbGRzaWcjZW52ZWxvcGVkLXNpZ25hdHVyZSIvPjxkczpUcmFuc2Zvcm0gQWxnb3JpdGhtPSJodHRwOi8vd3d3LnczLm9yZy8yMDAxLzEwL3htbC1leGMtYzE0biMiLz48L2RzOlRyYW5zZm9ybXM+PGRzOkRpZ2VzdE1ldGhvZCBBbGdvcml0aG09Imh0dHA6Ly93d3cudzMub3JnLzIwMDEvMDQveG1sZW5jI3NoYTI1NiIvPjxkczpEaWdlc3RWYWx1ZT5ONmRlN2IwY0MrOGV0SFNPL0p4OXEra0xWYm56WWwwWlJqb1ppWlAxR1dZPTwvZHM6RGlnZXN0VmFsdWU+PC9kczpSZWZlcmVuY2U+PC9kczpTaWduZWRJbmZvPjxkczpTaWduYXR1cmVWYWx1ZT5sR2ZhTzlqUnJjM2ZzNDZrN1lIcFdncThJOHpCaHpXMFdJMEYzMDFDc1JCWnpId1VGYnliMGM1OXdqaTE1NmM4dkV6bjJzbnJJTWhwcmlHckNEaFpXcld3bW94b29SbzI0K2VKUmNpaDc2cXVBalZBbTVWaGMvempnWktNNVhTY0FUTlBwQkNGdGRrRklYelFPQjBnUitzdk54WndpUlVtOU1paFMzQzFCQ1IrMXVCbkU0bzlGQ25TaC9IdWU3Um1ETlkxd2VZdmdDb2pyQjZZU2xNY1lISEpHNG1rcDlTM1hId0FoSGxoNC9DRWpvSVZZVVpHL2xKbEFLQ2VHYW1nVWdodnF4WFhjbGR3elpwZjFmd3Y5MXMvZE9MWnpZeWRSTXJxL1RORGVOMjJrQUtTWkNMaStPNm1VTk9rdElyUzFzTDUyUmpGVkRaN2tndVZyaER2bEE9PTwvZHM6U2lnbmF0dXJlVmFsdWU+PC9kczpTaWduYXR1cmU+PHNhbWw6U3ViamVjdD48c2FtbDpOYW1lSUQgRm9ybWF0PSJ1cm46b2FzaXM6bmFtZXM6dGM6U0FNTDoxLjE6bmFtZWlkLWZvcm1hdDp1bnNwZWNpZmllZCI+bTBsMDEwejwvc2FtbDpOYW1lSUQ+PHNhbWw6U3ViamVjdENvbmZpcm1hdGlvbiBNZXRob2Q9InVybjpvYXNpczpuYW1lczp0YzpTQU1MOjIuMDpjbTpiZWFyZXIiPjxzYW1sOlN1YmplY3RDb25maXJtYXRpb25EYXRhIFJlY2lwaWVudD0iaHR0cHM6Ly9jZXJ0Y253YXBwLWludC5jbi53YWwtbWFydC5jb20vYjJicG9ydGFsL2R4aHktZ3lscHQvbG9naW4uaHRtbCIgTm90T25PckFmdGVyPSIyMDE5LTAzLTEzVDAzOjQ0OjEyLjc2MloiLz48L3NhbWw6U3ViamVjdENvbmZpcm1hdGlvbj48L3NhbWw6U3ViamVjdD48c2FtbDpDb25kaXRpb25zIE5vdEJlZm9yZT0iMjAxOS0wMy0xM1QwMzozNDoxMi43NjJaIiBOb3RPbk9yQWZ0ZXI9IjIwMTktMDMtMTNUMDM6NDQ6MTIuNzYyWiI+PHNhbWw6QXVkaWVuY2VSZXN0cmljdGlvbj48c2FtbDpBdWRpZW5jZT5DTldBUFA8L3NhbWw6QXVkaWVuY2U+PC9zYW1sOkF1ZGllbmNlUmVzdHJpY3Rpb24+PC9zYW1sOkNvbmRpdGlvbnM+PHNhbWw6QXV0aG5TdGF0ZW1lbnQgU2Vzc2lvbkluZGV4PSJRRlFlRHh1Y3hxcE9uWE8uWFM2N1ZocGhJbTMiIEF1dGhuSW5zdGFudD0iMjAxOS0wMy0xM1QwMzozOToxMi43NjFaIj48c2FtbDpBdXRobkNvbnRleHQ+PHNhbWw6QXV0aG5Db250ZXh0Q2xhc3NSZWY+dXJuOm9hc2lzOm5hbWVzOnRjOlNBTUw6Mi4wOmFjOmNsYXNzZXM6dW5zcGVjaWZpZWQ8L3NhbWw6QXV0aG5Db250ZXh0Q2xhc3NSZWY+PC9zYW1sOkF1dGhuQ29udGV4dD48L3NhbWw6QXV0aG5TdGF0ZW1lbnQ+PC9zYW1sOkFzc2VydGlvbj48L3NhbWxwOlJlc3BvbnNlPg==";
		System.out.println(impl.getUserID(re));
	}
}
