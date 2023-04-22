package server.mine.servertest;

import jakarta.annotation.Resource;
import org.hibernate.type.descriptor.DateTimeUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.web.bind.annotation.*;
import server.mine.servertest.mysql.Dao.*;
import server.mine.servertest.mysql.MysqlOperate;
import server.mine.servertest.mysql.bean.*;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping(path = "/document")
@CrossOrigin("*")
public class DocunmentController {

    @Resource(name = "docMap")
    //文章ID，文章内容
    private Map<Integer, DocMapBean> allDocMap;

    @Resource
    private UserDao userDao;

    @Resource
    private DocumentDao documentDao;

    @Resource
    private PermissionDao permissionDao;

    @Resource
    private DocViewDao docViewDao;

    @Resource
    private CommendsDao commendsDao;

    @Resource
    private HistoryManageDao historyManageDao;

    @Resource
    private MysqlOperate mysqlOperate;

    @Value("${historyFile.path}")
    private String historyFilePath;

    /**
     * 从数据库中获取文档内容
     * @param tableName
     * @return
     */
    private List<String> getContentListFromSQL(String tableName){
        List<String> currList = new ArrayList<>();
        var x = mysqlOperate.findall(tableName);
        for (var c : x
        ) {
            currList.add(c.getContent());
        }
        return  currList;
    }

    /**
     * 创建新的临时文档
     * @param docUID
     */
    private void getDocumentFromSQL(Integer docUID){
        DocMapBean newDoc = new DocMapBean();
        var x = getContentListFromSQL("doc"+docUID);
        newDoc.setDocContent(x);
        newDoc.setUserNumber(newDoc.getUserNumber() + 1);
//        减去文章描述所占用的那一行
        newDoc.setLineNumber(x.size()-1);
        allDocMap.put(docUID, newDoc);
    }

    /**
     * 增加文档计数
     * @param docUID
     */
    private void addDocumentMapNumber(Integer docUID){
        allDocMap.get(docUID).setUserNumber(allDocMap.get(docUID).getUserNumber() + 1);
    }

    /**
     * 减少文档计数
     * @param docUID
     */
    private void minusDocumentMapNumber(Integer docUID){
        allDocMap.get(docUID).setUserNumber(allDocMap.get(docUID).getUserNumber() - 1);
        if(allDocMap.get(docUID).getUserNumber() <= 0){
            mysqlOperate.saveDocToSQL("doc"+docUID,allDocMap.get(docUID).getDocContent());
            allDocMap.remove(docUID);
        }
    }

    /**
     * 写入历史记录
     * @param fileName 文件名
     * @param content 文件内容
     * @throws IOException
     */
    private void createTxtFile(String fileName, String content) throws IOException {
        File file = new File(historyFilePath + fileName + ".txt");
        FileWriter writer = new FileWriter(file);
        writer.write(content);
        writer.close();
    }

    /**
     *
     * @param fileName
     * @return 文档内容，不包括文章描述
     */
    private Boolean readTxtFile(String fileName, List<String> curr){
        File file = new File(historyFilePath + fileName + ".txt");
        try (FileReader fileReader = new FileReader(file);
             BufferedReader reader = new BufferedReader(fileReader)) {
            String line;
            while ((line = reader.readLine()) != null) {
                curr.add(line);
            }
        } catch (IOException e) {
            return false;
        }
        return true;
    }

    /**
     * 新建: 新建，上传，打开 or 新建，更新
     * @param uid
     * @param newDocument
     * @return
     */
    @PostMapping(path = "/new/{uid}")
    public @ResponseBody
    ReturnMsg newDocument(@PathVariable(value = "uid") Integer uid, @RequestBody DocumentBean newDocument) {
        ReturnMsg rmsg = new ReturnMsg();
        //新建文件索引
        documentDao.save(newDocument);
        PermissionBean currp = new PermissionBean();
        Integer curr = documentDao.getDocumentBeanByMaxDocUID();
        currp.setUserUID(uid);
        currp.setDocUID(curr);
        currp.setPermissionType("RU");
        //新建操作权限关系
        permissionDao.save(currp);
        //新建文件内容
        String tableName = "doc" + curr;
        mysqlOperate.newDoc(tableName);
        mysqlOperate.newRowContent(tableName, 0, "这是一篇新文章");
        //新建临时文档
        DocMapBean newDoc = new DocMapBean();
        List<String> currList = new ArrayList<>();
        currList.add(mysqlOperate.findRowContent(tableName, 0));
        newDoc.setDocContent(currList);
        newDoc.setUserNumber(newDoc.getUserNumber() + 1);
        allDocMap.put(curr, newDoc);
        //发送返回值
        rmsg.setCode(200);
        rmsg.setObjectType("DocumentUID");
        rmsg.setObject(curr);
        return rmsg;
    }

    /**
     * 上传文档内容(直接上传文档的话，内容要直接传上来，统一管理，这里不加计数)
     * @param docUID
     * @param content
     * @return
     */
    @PostMapping(path = "/newContent/{docUID}")
    public @ResponseBody ReturnMsg newContent(@PathVariable(value = "docUID") Integer docUID, @RequestBody RequestList content){
        var contentList = content.getContentList();
        ReturnMsg rmsg = new ReturnMsg();
        mysqlOperate.saveDocToSQL("doc"+docUID,contentList);
        //可以覆盖，所以重复就重复吧
        getDocumentFromSQL(docUID);
//        重复赋值了似乎，用的时候测一下需要
//        allDocMap.get(docUID).setDocContent(getContentListFromSQL("doc"+docUID));
        rmsg.setCode(200);
        rmsg.setObjectType("String");
        rmsg.setObject("上传成功！");
        return rmsg;
    }

    /**
     * 重命名
     * @param docUID
     * @param newTitle
     * @return
     */
    @PatchMapping(path = "/updateTitle/{docUID}")
    public @ResponseBody
    ReturnMsg changeTitle(@PathVariable(value = "docUID") Integer docUID, @RequestBody RequestString newTitle) {
        var x = documentDao.findById(docUID);
        DocumentBean curr = x.get();
        curr.setTitle(newTitle.getRequestContent());
        documentDao.save(curr);
        ReturnMsg rmsg = new ReturnMsg();
        rmsg.setCode(200);
        rmsg.setObjectType("String");
        rmsg.setObject("修改成功");
        return rmsg;
    }

    /**
     * 修改文档描述
     * @param docUID
     * @param newDesc
     * @return
     */
    @PatchMapping(path = "/updateDesc/{docUID}")
    public @ResponseBody
    ReturnMsg changeDescription(@PathVariable(value = "docUID") Integer docUID, @RequestBody RequestString newDesc) {
        ReturnMsg rmsg = new ReturnMsg();
//        文章描述在列表界面修改，只给作者提供接口，不新建了, 如果有就改一下
//        if (!allDocMap.containsKey(docUID)) {
//            getDocumentFromSQL(docUID);
//        }
        if(allDocMap.containsKey(docUID)) allDocMap.get(docUID).getDocContent().set(0, newDesc.getRequestContent());
        //文档描述直接改就行，这个不算文章内容，不纳入版本管理
        mysqlOperate.updateDesc("doc"+docUID,newDesc.getRequestContent());
        rmsg.setCode(200);
        rmsg.setObjectType("String");
        rmsg.setObject("修改成功");
        return rmsg;
    }

    /**
     * 打开文档，上传完接获取——因为我不识别主机号，所以要避免同一台主机触发两次序列号加的操作
     * @param docUID
     * @return
     */
    @GetMapping(path = "/open/{docUID}")
    public @ResponseBody ReturnMsg openDocumentByUID(@PathVariable(value = "docUID") Integer docUID){
        ReturnMsg rmsg = new ReturnMsg();
        if(!allDocMap.containsKey(docUID)){
            getDocumentFromSQL(docUID);
        }else {
            addDocumentMapNumber(docUID);
        }
        rmsg.setCode(200);
        rmsg.setObjectType("DocumentContent");
//        文章描述一并发过去了，记得赋值的时候去掉
        rmsg.setObject(allDocMap.get(docUID).getDocContent());
        return rmsg;
    }

    /**
     * 保存，文档值保存到数据库中
     * @param docUID
     * @return
     */
    @PostMapping(path = "/save/{docUID}")
    public @ResponseBody ReturnMsg saveDocumentToSQL(@PathVariable(value = "docUID") Integer docUID) {
        ReturnMsg rmsg = new ReturnMsg();
//        如果已经上传，那么保存的肯定是当前已经打开的文件
        if(!allDocMap.containsKey(docUID)){
            rmsg.setCode(403);
            rmsg.setObjectType("String");
            rmsg.setObject("ERROR: 未上传，不能保存");
            return rmsg;
        }else {
//            每次保存到数据库更新历史版本
            HistoryManageBean lastHistory = new HistoryManageBean();
            lastHistory.setDocUID(docUID);
            String date = DateTimeUtils.FORMAT_STRING_DATE;
            lastHistory.setTime(date);
            lastHistory.setName(docUID + "-" + date);
            var x = getContentListFromSQL("doc" + docUID);
//            描述不要
            x.remove(0);
            String content = String.join("\n",x);
            try {
                createTxtFile(lastHistory.getName(),content);
            }catch (IOException e) {
                rmsg.setCode(500);
                rmsg.setObjectType("String");
                rmsg.setObject("ERROR：历史版本保存失败，请重试或先将文件保存到本地");
                return rmsg;
            }
//            保存好之后更新
            mysqlOperate.saveDocToSQL("doc"+docUID,allDocMap.get(docUID).getDocContent());
        }
        rmsg.setCode(200);
        rmsg.setObjectType("String");
        rmsg.setObject("保存成功！");
        return rmsg;
    }

    /**
     * 获取文章历史记录信息
     * @param docUID
     * @return
     */
    @GetMapping(path = "/history/{docUID}")
    public @ResponseBody ReturnMsg getHistoryList(@PathVariable(value = "docUID") Integer docUID){
        ReturnMsg rmsg = new ReturnMsg();
        var x = historyManageDao.findAll();
        List<HistoryManageBean> curr = new ArrayList<>();
        for (var c:x
             ) {
            curr.add(c);
        }
        if(curr.isEmpty()){
            rmsg.setCode(200);
            rmsg.setObjectType("String");
            rmsg.setObject("评论为空");
            return rmsg;
        }
        rmsg.setCode(200);
        rmsg.setObjectType("HistoryList");
        rmsg.setObject(curr);
        return rmsg;
    }

    /**
     *
     * @param userUID
     * @param historyUID
     * @return
     */
    @PostMapping(path = "/historyBack/{UID}/{historyUID}")
    public @ResponseBody ReturnMsg historyBack(@PathVariable(value = "UID") Integer userUID, @PathVariable(value = "historyUID") Integer historyUID){
        ReturnMsg rmsg = new ReturnMsg();
        Optional<HistoryManageBean> currHistory = historyManageDao.findById(historyUID);
        if(currHistory.isEmpty()){
            rmsg.setCode(400);
            rmsg.setObjectType("String");
            rmsg.setObject("ERROR: 未找到改历史记录信息");
            return rmsg;
        }
        //        先验证身份权限
        boolean flag = false;
        var docList = documentDao.getDocumentBeanByAuthorUID(userUID);
        for (var c:docList
             ) {
            if(c.getDocUID() == currHistory.get().getDocUID()){
                flag = true;
                break;
            }
        }
        if(!flag){
            rmsg.setCode(400);
            rmsg.setObjectType("String");
            rmsg.setObject("ERROR: 您并非作者，无权回退历史版本");
        }
        var docUID = currHistory.get().getDocUID();
        List<String> curr = new ArrayList<>();
        if(!readTxtFile(currHistory.get().getName(),curr)){
            rmsg.setCode(400);
            rmsg.setObjectType("String");
            rmsg.setObject("ERROR: 读文件失败");
            return rmsg;
        }
        if(allDocMap.containsKey(docUID)){
            curr.add(0,allDocMap.get(docUID).getDocContent().get(0));
            allDocMap.get(docUID).setDocContent(curr);
            allDocMap.get(docUID).setLineNumber(curr.size()-1);
        }else{
            curr.add(0,mysqlOperate.findRowContent("doc"+docUID,0));
        }
//        保存到数据库
        mysqlOperate.saveDocToSQL("doc"+docUID,allDocMap.get(docUID).getDocContent());
        rmsg.setCode(200);
        rmsg.setObjectType("String");
        rmsg.setObject("历史版本已回退！");
        return rmsg;
    }

    /**
     * 获取文章评论
     * @param docUID
     * @return
     */
    @GetMapping(path = "/getCommends/{docUID}")
    public @ResponseBody ReturnMsg getCommends(@PathVariable(value = "docUID") Integer docUID){
        ReturnMsg rmsg = new ReturnMsg();
        var x = commendsDao.getCommendsBeanByDocUID(docUID);
        if(x.isEmpty()){
            rmsg.setObjectType("String");
            rmsg.setObject("暂无评论");
        }else {
            rmsg.setObjectType("CommendsList");
            rmsg.setObject(x);
        }
        rmsg.setCode(200);
        return rmsg;
    }

    /**
     * 关闭，减引用，并保存
     * @param docUID
     * @return
     */
    @PostMapping(path = "/close/{docUID}")
    public @ResponseBody ReturnMsg closeDocument(@PathVariable(value = "docUID") Integer docUID){
        ReturnMsg rmsg = new ReturnMsg();
//      不手动保存不更新历史版本， 引用为零的时候再保存到数据库中，减少操作次数
        minusDocumentMapNumber(docUID);
        rmsg.setCode(200);
        rmsg.setObjectType("String");
        rmsg.setObject("文章关闭");
        return rmsg;
    }

    /**
     * 更新文档内容，某一行的更改，实时更新全部交给websocket那边，这里不做考虑
     * @param docUID
     * @param content
     * @return
     */
    @PostMapping(path = "/updateContent/{docUID}")
    public @ResponseBody ReturnMsg updateInTime(@PathVariable(value = "docUID") Integer docUID, @RequestBody DocContentBean content){
        ReturnMsg rmsg = new ReturnMsg();
        if(!allDocMap.containsKey(docUID)){
            rmsg.setCode(403);
            rmsg.setObjectType("String");
            rmsg.setObject("ERROR: 未发现此文章被使用！");
            return rmsg;
        }else if(allDocMap.get(docUID).getDocContent().size() <= content.getRowNumber()){
            allDocMap.get(docUID).getDocContent().add(content.getContent());
        }else {
            allDocMap.get(docUID).getDocContent().set(content.getRowNumber(),content.getContent());
        }
        rmsg.setCode(200);
        rmsg.setObjectType("Update");
        rmsg.setObject("更新成功");
        return rmsg;
    }

    //curl 中文会有乱码
    @PostMapping(path = "/test")
    public @ResponseBody
    ReturnMsg test() {
//        try{
//            mysqlOperate.updateDesc("doc10000000", "试一试''");
//        }catch (DataAccessException err){
//            System.out.println(err);
//        }
//        var curr =  mysqlOperate.findall("doc10000000");
//        for (var c:curr
//             ) {
//            System.out.println(c.getContent());
//        }
//        System.out.println(mysqlOperate.getMaxRowNumber("doc10000000"));
//        System.out.println("表是否没内容：" + allDocMap.isEmpty());
//        System.out.println(contentList.getContentList());
//        System.out.println(contentList.getContentList().get(0));

        List<String> curr = new ArrayList<>();
        if(!readTxtFile("test",curr)){
            System.out.println("读文件失败");
        }else {
            System.out.println(curr.get(0));
        }

        ReturnMsg rmsg = new ReturnMsg();
        rmsg.setCode(200);
        rmsg.setObjectType("String");
        rmsg.setObject("即将关闭文章！");
        return rmsg;
    }

}
