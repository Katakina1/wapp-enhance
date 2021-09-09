package com.xforceplus.wapp.websocket;

import com.xforceplus.wapp.modules.sys.controller.AbstractController;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
@ServerEndpoint("/websocket/{userName}")
public class WebSocketServer  extends AbstractController{

//    /**
//     *  与某个客户端的连接对话，需要通过它来给客户端发送消息
//     */
//    private Session session;
	
    /**
     *  用于存所有的连接服务的客户端，这个对象存储是安全的
     */
    private static ConcurrentHashMap<String, WebSocketServer> webSocketSet = new ConcurrentHashMap<>();
    
    public static ConcurrentHashMap<String, Session> webSocketUserInfo = new ConcurrentHashMap<>();

    
	/*
	 * @Autowired private MessageControlService messageControlService;
	 */
    private static ApplicationContext applicationContext;
    
    public static void setApplicationContext(ApplicationContext applicationContext) {
    	WebSocketServer.applicationContext = applicationContext;
    }


    @OnOpen
    public void OnOpen(Session session,@PathParam(value = "userName") String userName){
        webSocketSet.put(userName,this);
        webSocketUserInfo.put(userName, session);
        log.info("[WebSocket] 连接成功，当前连接人数为：={}",webSocketSet.size());
    }

    
    @OnClose
    public void OnClose(Session session,@PathParam(value = "userName") String userName){
    	try {
			session.close();
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}
        webSocketSet.remove(userName);
        webSocketUserInfo.remove(userName);
        log.info("[WebSocket] 退出成功，当前连接人数为：={}",webSocketSet.size());
    }

    @OnMessage
    public void OnMessage(Session session,String userMessage){
    	if(null != session && !StringUtils.isEmpty(userMessage)) {
    		log.info("[WebSocket] 收到消息：{}",userMessage);
    		AppointSending(session, userMessage);
    	}
    }

//    /**
//     * 群发
//     * @param message
//     */
//    public void GroupSending(String message){
//        for (String name : webSocketSet.keySet()){
//            try {
//                webSocketSet.get(name).session.getBasicRemote().sendText(message);
//            }catch (Exception e){
//                e.printStackTrace();
//            }
//        }
//    }

    /**
     * 指定发送
     * @param name
     */
    public void AppointSending(Session session,String msg){
        try {
                session.getBasicRemote().sendText(msg);
                log.info("webService推送数据，消息 ::{}", msg);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}