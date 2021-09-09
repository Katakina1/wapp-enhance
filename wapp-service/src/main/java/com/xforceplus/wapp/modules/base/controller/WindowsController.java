/**
 * @Title:  SAMLResponseHandler.java
 * @Package com.xforceplus.wapp.modules.base.controller
 * @Description:    TODO
 * @author: jiaohongyang
 * @date:   2019年1月15日 下午5:15:15
 */
package com.xforceplus.wapp.modules.base.controller;

import com.xforceplus.wapp.common.utils.Base64;
import com.xforceplus.wapp.common.utils.JsonUtil;
import com.xforceplus.wapp.common.utils.R;
import com.xforceplus.wapp.interfaceBPMS.ADAuthenticateService;
import com.xforceplus.wapp.modules.base.entity.UserEntity;
import com.xforceplus.wapp.modules.base.service.BaseUserService;
import com.xforceplus.wapp.modules.base.service.UserTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.net.URLDecoder;
import java.util.Date;

@RestController
@RequestMapping("/WinLogin")
public class WindowsController {
	@Autowired
	@Qualifier("ADAuthenticateService")
    private ADAuthenticateService aDAuthenticateService;
	@Autowired
	private UserTokenService userTokenService;
	@Autowired
	private BaseUserService baseUserService;
	@Value("${mycat.default_schema_label}")
	private String mycatDefaultSchemaLabel;
	@Value("${token.expire_time}")
	private int expire;
	@PostMapping("/login")
	@ResponseBody
    public R login(String userId, String password) {
		// 远程walmart账号服务中心对用户进行校验
		try{
			String userJson = "";
			if(password!=null){
					password=password.replace("amp;","");
			}
			boolean authenticate=aDAuthenticateService.authenticate(userId.toLowerCase(),password);
			if (authenticate) {
				//认证通过
				UserEntity user = baseUserService.queryByUserName(mycatDefaultSchemaLabel,userId);
				if(user != null) {
						//当前时间
						Date now = new Date();
						//过期时间
						Date expireTime = new Date(now.getTime() + expire * 1000);
						long time = expireTime.getTime();
						user.setExpireTime(String.valueOf(time));
						userJson = JsonUtil.toJson(user);
					return R.ok().put("token",Base64.encode(userJson.getBytes()));
				}else{
					return R.error().put("msg","Wapp没有该用户！");
				}
			}else{
				return R.error().put("msg","请检查WindowsId和密码是否正确！");
			}
		}catch (Exception e){
			return R.error().put("msg","系统异常！");
		}
	}
}