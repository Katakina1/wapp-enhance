package com.xforceplus.wapp.modules.base.controller;

import com.xforceplus.wapp.common.utils.Base64;
import com.xforceplus.wapp.common.utils.JsonUtil;
import com.xforceplus.wapp.common.utils.MD5Utils;
import com.xforceplus.wapp.common.utils.R;
import com.xforceplus.wapp.common.utils.ShiroUtils;
import com.xforceplus.wapp.modules.base.entity.AccountCentRequest;
import com.xforceplus.wapp.modules.base.entity.AccountCentResponse;
import com.xforceplus.wapp.modules.base.entity.UserEntity;
import com.xforceplus.wapp.modules.base.service.AccountCentService;
import com.xforceplus.wapp.modules.base.service.BaseUserService;
import com.xforceplus.wapp.modules.base.service.UserTokenService;
import com.google.code.kaptcha.Constants;
import com.google.code.kaptcha.Producer;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.bouncycastle.util.encoders.UrlBase64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

/**
 * 登录相关
 *
 * @author chenshun
 * @email sunlightcs@gmail.com
 * @date 2016年11月10日 下午1:15:31
 */
@RestController
public class LoginController {
    private static final Logger LOGGER = LoggerFactory.getLogger(LoginController.class);

    private final Producer producer;

    private final BaseUserService baseUserService;

    private final UserTokenService userTokenService;

    private final AccountCentService accountCentService;

    @Value("${mycat.default_schema_label}")
    private String mycatDefaultSchemaLabel;

    @Value("${AccountCent.isService}")
    private Boolean isAccountCent;
    
    @Value("${token.expire_time}")
    private int expire;

    @Autowired
    public LoginController(BaseUserService baseUserService, Producer producer, UserTokenService userTokenService, AccountCentService accountCentService) {
        this.baseUserService = baseUserService;
        this.producer = producer;
        this.userTokenService = userTokenService;
        this.accountCentService = accountCentService;
    }

    @RequestMapping("captcha.jpg")
    public void captcha(HttpServletResponse response) throws IOException {
        response.setHeader("Cache-Control", "no-store, no-cache");
        response.setContentType("image/jpeg");

        //生成文字验证码
        String text = producer.createText();
        //生成图片验证码
        BufferedImage image = producer.createImage(text);
        //保存到shiro session
        ShiroUtils.setSessionAttribute(Constants.KAPTCHA_SESSION_KEY, text);

        ServletOutputStream out = response.getOutputStream();
        ImageIO.write(image, "jpg", out);
        IOUtils.closeQuietly(out);
    }

    /**
     * 登录
     */
    @RequestMapping(value = "/sys/login", method = RequestMethod.POST)
    public Map<String, Object> login(String username, String password, String captcha) {
    	String userJson = "";
        if(StringUtils.isBlank(captcha)){
            return R.error(2, "验证码不可为空");
        }
        String kaptcha = ShiroUtils.getKaptcha(Constants.KAPTCHA_SESSION_KEY);
        if (!captcha.equalsIgnoreCase(kaptcha)) {
            return R.error(2,"验证码不正确");
        }

        //用户信息
        UserEntity user = baseUserService.queryByUserName(mycatDefaultSchemaLabel, username);

        //账号不存在、密码错误
//        if (user == null || !user.getPassword().equalsIgnoreCase(MD5Utils.encode(password))) {
//            return R.error(1,"账号或密码不正确");
//        }
//
//        //账号锁定
//        if (!"1".equals(user.getStatus())) {
//            return R.error("账号已被锁定或不可用,请联系管理员");
//        }
        //生成token，并保存到数据库
//        R r = userTokenService.createToken(mycatDefaultSchemaLabel, user.getUserid());
        

        //当前时间
        Date now = new Date();
        //过期时间
        Date expireTime = new Date(now.getTime() + expire * 1000);
        long time = expireTime.getTime();
        user.setExpireTime(String.valueOf(time));
       
        try {
        	userJson = JsonUtil.toJson(user);
		} catch (IOException e) {
			e.printStackTrace();
		}
        
        if (LOGGER.isDebugEnabled()) {
            new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(expireTime);
            LOGGER.debug("用户:{}, 生成Token:{}, 过期时间:{}", username, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(expireTime));
        }
        String token = URLEncoder.encode(Base64.encode(userJson.getBytes()));
        LOGGER.debug("token: "+ token);
        return R.ok().put("token",token).put("expire", expire);
    }

}

