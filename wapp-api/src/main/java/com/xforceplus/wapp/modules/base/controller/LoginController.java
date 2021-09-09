package com.xforceplus.wapp.modules.base.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.xforceplus.wapp.modules.base.service.BaseUserService;
import com.xforceplus.wapp.modules.base.service.MenuService;
import com.xforceplus.wapp.modules.base.service.UserTokenService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.xforceplus.wapp.common.utils.MD5Utils;
import com.xforceplus.wapp.common.utils.R;
import com.xforceplus.wapp.modules.base.entity.MenuEntity;
import com.xforceplus.wapp.modules.base.entity.UserBilltypeEntity;
import com.xforceplus.wapp.modules.base.entity.UserEntity;
import com.xforceplus.wapp.modules.base.service.OrganizationService;
import com.xforceplus.wapp.modules.base.service.UserBilltypeService;
import com.xforceplus.wapp.modules.sys.service.ShiroService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * 登录相关
 *
 * @author chenshun
 * @email sunlightcs@gmail.com
 * @date 2016年11月10日 下午1:15:31
 */
@RequestMapping("/api")
@Api(tags="客户端登录接口")
@RestController
public class LoginController {
    private static final Logger LOGGER = LoggerFactory.getLogger(LoginController.class);


    private final BaseUserService baseUserService;

    private final UserTokenService userTokenService;
    
    private final OrganizationService organizationService;

    private final MenuService menuService;
    
    private final UserBilltypeService userBilltypeService;
    
    private final ShiroService shiroService;

    @Value("${mycat.default_schema_label}")
    private String mycatDefaultSchemaLabel;

    
    @Value("${CustomerCent.customerMenuPre}")
    private String customer;

    @Autowired
    public LoginController(BaseUserService baseUserService,  UserTokenService userTokenService,MenuService menuService,
    		ShiroService shiroService,OrganizationService organizationService,UserBilltypeService userBilltypeService) {
    		
        this.baseUserService = baseUserService;
        this.userTokenService = userTokenService;
        this.menuService = menuService;
        this.organizationService=organizationService;
        this.shiroService = shiroService;
        this.userBilltypeService=userBilltypeService;
    }

    /**
     * 登录
     */
    @PostMapping("loginCustomer")
    @ApiOperation("客户端登录")
    @RequestMapping(method = RequestMethod.POST)
    public Map<String, Object> login(String username, String password,String fromCustomer) {
    	//客户端登录无需验证验证码
    	if(fromCustomer == null)
		 {
    		LOGGER.info("非客户端用户登录");
		 }
        //用户信息
        UserEntity user = baseUserService.queryByUserName(mycatDefaultSchemaLabel, username);

        //账号不存在、密码错误
        if (user == null || !user.getPassword().equalsIgnoreCase(MD5Utils.encode(password))) {
            return R.error(1,"账号或密码不正确");
        }

        //账号锁定
        if (!"1".equals(user.getStatus())) {
            return R.error("账号已被锁定或不可用,请联系管理员");
        }
        
        //生成token，并保存到数据库
        R r = userTokenService.createToken(mycatDefaultSchemaLabel, user.getUserid());
        
       
        
        //说明是从客户端登录过来的  我需要额外返回用户信息，用户的客户端菜单信息
         if(fromCustomer!=null){

        	
        	 //查询用户信息
	        UserEntity userCurrent = shiroService.queryUser(mycatDefaultSchemaLabel, user.getUserid().longValue());
	        //设置
	        //设置用户信息
	        r.put("user", userCurrent);
	       



	        
	        final List<UserBilltypeEntity> userBilltypeList = userBilltypeService.getOrgDetail(userCurrent.getSchemaLabel(), null);


	        //设置用户的客户端票据类型，带默认值
	        r.put("billtypeList", userBilltypeList);



             //设置用户菜单列表，这里需要控制只查询到客户端的菜单,默认以customer开头，需要系统配置
             List<MenuEntity> menuList = menuService.getUserMenuList(userCurrent.getSchemaLabel(), new Long(user.getUserid()), 0);
             List<MenuEntity> menuListnew = new ArrayList<MenuEntity>();
             for(MenuEntity menuEntity:menuList) {
                 if(menuEntity.getMenucode() !=null && menuEntity.getMenucode().startsWith(customer)) {
                     menuListnew.add(menuEntity);

                 }
             }
             r.put("menuList", menuListnew);


        }
       
        return r;
    }

}
