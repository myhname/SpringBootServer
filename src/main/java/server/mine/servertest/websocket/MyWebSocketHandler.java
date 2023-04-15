package server.mine.servertest.websocket;

import com.mongodb.util.JSON;
import jakarta.annotation.Resource;
import org.springframework.beans.BeansException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import server.mine.servertest.mysql.bean.DocMapBean;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class MyWebSocketHandler extends TextWebSocketHandler{
//    @Resource
//    private ApplicationContext applicationContext;

    @Resource(name = "docMap")
    private Map<Integer, DocMapBean> allDocMap;

    //这里其实全局只有一个会话，还不能区分群组，后续再改进
    //采用消息队列可能性能更好？之后了解一下
    private final Map<String, Set<WebSocketSession>> rooms = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String roomId = "editor";
        Set<WebSocketSession> sessions = rooms.computeIfAbsent(roomId, k->ConcurrentHashMap.newKeySet());
        sessions.add(session);
        System.out.println("Client connected: " + session.getId());
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
//        this.allDocMap = (Map<Integer, DocMapBean>) applicationContext.getBean("docMap");

        //todo
        System.out.println("Received message: " + message.getPayload());
        //格式处理
        var x = message.getPayload();
        UpdateContentBean curr = new UpdateContentBean(x);
        //todo
        System.out.println(curr.getContent());
        //存储
        int docUID = curr.getDocUID();
        if(allDocMap.isEmpty()){
            session.sendMessage(new TextMessage("未打开任何文章！"));
            return ;
        }
        if(!allDocMap.containsKey(docUID)){
            session.sendMessage(new TextMessage("ERROR: 未发现此文章被使用！"));
            return ;
        }else if(allDocMap.get(docUID).getDocContent().size() <= curr.getRowNumber()){
            allDocMap.get(docUID).getDocContent().add(curr.getContent());
        }else {
            allDocMap.get(docUID).getDocContent().set(curr.getRowNumber(),curr.getContent());
        }
//        //消息转发
        String roomId = "editor";
        Set<WebSocketSession> sessions = rooms.getOrDefault(roomId, Collections.emptySet());
        for (WebSocketSession sess : sessions) {
            if (sess.isOpen() && sess != session) {
                sess.sendMessage(new TextMessage(message.getPayload()));
            }
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        String roomId = "editor";
        Set<WebSocketSession> sessions = rooms.getOrDefault(roomId, Collections.emptySet());
        sessions.remove(session);
        System.out.println("Client disconnected: " + session.getId());
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        System.err.println("Error on WebSocket connection: " + exception.getMessage());
    }
}