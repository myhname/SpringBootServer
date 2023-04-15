package server.mine.servertest.websocket;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    @Autowired
    private MyWebSocketHandler myWebSocketHandler;

    //只要访问同一个地址就认为是同一组用户的编辑，访问不同地址来划分群组，可能需要动态注册路径和handler
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        //.setAllowedOriginPatterns("")设置来源.withSockJS()
        registry.addHandler(myWebSocketHandler, "/my-websocket").setAllowedOriginPatterns("*");
    }
}