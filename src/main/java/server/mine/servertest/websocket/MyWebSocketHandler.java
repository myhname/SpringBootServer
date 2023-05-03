package server.mine.servertest.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import server.mine.servertest.mysql.Dao.ModifyRecordDao;
import server.mine.servertest.mysql.bean.DocMapBean;
import server.mine.servertest.mysql.bean.ModifyRecordBean;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class MyWebSocketHandler extends TextWebSocketHandler {
//    @Resource
//    private ApplicationContext applicationContext;

    //采用消息队列可能性能更好？之后了解一下，这样设计的问题就是关闭文章时候要重连
//    群组表
    private final Map<String, Set<WebSocketSession>> rooms = new ConcurrentHashMap<>();
    //    文章内容表
    @Resource(name = "docMap")
    private Map<Integer, DocMapBean> allDocMap;
    //    操作记录表,关闭文章时候操作
    @Resource
    private ModifyRecordDao modifyRecordDao;
    //    只记录到文章本次修改的最后一次修改
    private Map<Integer, Map<Integer, ModifyRecord>> modifyLast = new HashMap<>();
    //    锁的信息
    private Map<Integer, Integer> blockMSG = new HashMap<>();

    //    保存操作记录到数据库
    public void saveModifyRecordToSQL(Integer uid) {
        //                  保存修改记录到数据库
        var currModifyRecord = modifyLast.get(uid);
        currModifyRecord.forEach((key, value) -> {
            ModifyRecordBean a = new ModifyRecordBean();
            a.setDocUID(uid);
            a.setTime(value.getTime());
            a.setRowNumber(key);
            a.setUserUID(value.getUserUID());
            modifyRecordDao.save(a);
        });
//                    删除修改记录暂存表
        modifyLast.remove(uid);
    }

    //    获取时间
    public String getCurrTime() {
        // 创建一个DateTimeFormatter实例，指定格式为"yyyy-MM-dd"
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
// 获取当前日期
        LocalDate currentDate = LocalDate.now();
// 使用formatter将日期格式化为指定格式的字符串

        return currentDate.format(formatter);
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String roomId = "editor";
        Set<WebSocketSession> sessions = rooms.computeIfAbsent(roomId, k -> ConcurrentHashMap.newKeySet());
        sessions.add(session);
        var currAttributes = session.getAttributes();
        currAttributes.put("docUID", "editor");
        System.out.println("Client connected: " + session.getId());
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
//        this.allDocMap = (Map<Integer, DocMapBean>) applicationContext.getBean("docMap");

        //todo
        System.out.println("Received message: " + message.getPayload());
        //格式处理
        var x = message.getPayload();
        ObjectMapper objectMapper = new ObjectMapper();
        UpdateContentBean curr = objectMapper.readValue(x, UpdateContentBean.class);
//      没打开文章不操作
        if (curr.getDocUID() == null) return;
        //todo
        System.out.println(curr.getChangeType());
        var currAttributes = session.getAttributes();
//        如果是打开文章时候发起的信息
        if (Objects.equals(curr.getChangeType(), "startEditor")) {
//            保存分组信息
            currAttributes.put("docUID", curr.getDocUID());
            //        按照编辑的文档进行分组
            Set<WebSocketSession> sessions = rooms.computeIfAbsent(String.valueOf(currAttributes.get("docUID")), k -> ConcurrentHashMap.newKeySet());
            sessions.add(session);
//            新建历史记录表
            if (!modifyLast.containsKey(curr.getDocUID())) modifyLast.put(curr.getDocUID(), new HashMap<>());
            session.sendMessage(new TextMessage("blockMSG:" + blockMSG.toString()));
            return;
        }
//       还没划进群组就错了
        if (currAttributes.get("docUID") == null) {
//            返回错误信息
            session.sendMessage(new TextMessage("Error：请重新打开文章建立连接"));
            return;
        }
        String roomId = String.valueOf(currAttributes.get("docUID"));
//        如果是关闭文章时候发起的信息
        if (Objects.equals(curr.getChangeType(), "endEditor")) {
            Set<WebSocketSession> sessions = rooms.getOrDefault(roomId, Collections.emptySet());
//          还是得转发，去锁
            for (WebSocketSession sess : sessions) {
                if (sess.isOpen() && sess != session) {
                    sess.sendMessage(new TextMessage(message.getPayload()));
                }
            }
            sessions.remove(session);
            currAttributes.put("docUID", "editor");
            blockMSG.remove(curr.getUserUID());
            //        为空则删除
            if (sessions.isEmpty()) {
//                    移出群组
                rooms.remove(roomId);
//                  保存修改记录
                saveModifyRecordToSQL(curr.getDocUID());
            }
            return;
        }
//       消息处理
        int docUID = curr.getDocUID();
//        如果没有打开文章
        if (!allDocMap.containsKey(docUID)) {
            session.sendMessage(new TextMessage("ERROR：未打开当前文章！"));
            return;
        }
        //      添加修改记录
        var currModifyRecord = modifyLast.get(curr.getDocUID());
        var currDocMap = allDocMap.get(docUID);
        var currContent = currDocMap.getDocContent();
        int startLine = curr.getStartLine();
//        应对四种类型的修改， 中文单行编辑，粘贴，英文编辑大写、新增行，删除内容(单行或多行)，undo撤销
        if (Objects.equals(curr.getChangeType(), "*compose")) {
//            只需要替换单行内容
//            System.out.println("文章地9行是：" + currContent.get(startLine));
            currContent.set(startLine, curr.getNewContent().get(0));
//            System.out.println("文章地9行是：" + currContent.get(startLine));
//            System.out.println("时间是：");
//            System.out.println(getCurrTime());
            currModifyRecord.put(startLine, new ModifyRecord(getCurrTime(), curr.getUserUID()));
            blockMSG.put(curr.getUserUID(), curr.getStartLine());
        } else if (Objects.equals(curr.getChangeType(), "paste")) {
//          需要替换内容段
            for (int i = 0; i < curr.getRemovedNumbers(); i++) {
                currContent.remove(startLine);
            }
//            插入新的内容
            for (int i = curr.getNewContent().size() - 1; i >= 0; i--) {
                currContent.add(startLine, curr.getNewContent().get(i));
                currModifyRecord.put(startLine + i, new ModifyRecord(getCurrTime(), curr.getUserUID()));
            }
            blockMSG.put(curr.getUserUID(), curr.getStartLine() + curr.getNewContent().size() - 1);
        } else if (Objects.equals(curr.getChangeType(), "+input")) {
//            有两种情况：英文输入法或换行
            if (curr.getNewContent().size() > 1) {
                //            新增一行，先替换原本的那一行
                currContent.set(startLine, curr.getNewContent().get(0));
                currModifyRecord.put(startLine, new ModifyRecord(getCurrTime(), curr.getUserUID()));
//            再新增一行
                currContent.add(startLine + 1, curr.getNewContent().get(1));
                currModifyRecord.put(startLine + 1, new ModifyRecord(getCurrTime(), curr.getUserUID()));
            } else {
                currContent.set(startLine, curr.getNewContent().get(0));
                currModifyRecord.put(startLine, new ModifyRecord(getCurrTime(), curr.getUserUID()));
            }
            blockMSG.put(curr.getUserUID(), curr.getStartLine() + curr.getNewContent().size() - 1);
        } else if (Objects.equals(curr.getChangeType(), "+delete")) {
//            先删除,当前行不用删，直接替换就好
            if (curr.getRemovedNumbers() != 1) {
                for (int i = 0; i < curr.getRemovedNumbers() - 1; i++) {
                    currContent.remove(startLine);
                }
            }
            currContent.set(startLine, curr.getNewContent().get(0));
            currModifyRecord.put(startLine, new ModifyRecord(getCurrTime(), curr.getUserUID()));
            blockMSG.put(curr.getUserUID(), curr.getStartLine() + curr.getNewContent().size());
        }
//      消息转发
        Set<WebSocketSession> sessions = rooms.getOrDefault(roomId, Collections.emptySet());
        if (sessions.isEmpty()) {
            session.sendMessage(new TextMessage("ERROR：群组不存在"));
            return;
        }
//        群组中只有一个人就没必要转发了
        if (sessions.size() == 1) {
            return;
        }
//        群组内消息转发
        for (WebSocketSession sess : sessions) {
            if (sess.isOpen() && sess != session) {
                sess.sendMessage(new TextMessage(message.getPayload()));
            }
        }
    }

    //WebSocket 连接关闭后执行的方法
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
//        获取当前用户所在分组
        var currAttributes = session.getAttributes();
        String roomId = String.valueOf(currAttributes.get("docUID"));
        if (!roomId.equals("editor")) {
            System.out.println("移除:" + roomId);
            Set<WebSocketSession> sessions = rooms.getOrDefault(roomId, Collections.emptySet());
            sessions.remove(session);
//        为空则删除
            if (sessions.isEmpty()) {
                rooms.remove(roomId);
                saveModifyRecordToSQL(Integer.parseInt(roomId));
            }
        }
        Set<WebSocketSession> allSessions = rooms.getOrDefault("editor", Collections.emptySet());
        allSessions.remove(session);
//        为空则删除
        if (allSessions.isEmpty()) {
            rooms.remove(roomId);
        }
        System.out.println("Client disconnected: " + session.getId());
    }

    //WebSocket 连接出错后执行的方法
    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        System.err.println("Error on WebSocket connection: " + exception.getMessage());
    }
}