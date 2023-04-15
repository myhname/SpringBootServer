package server.mine.servertest;

import jakarta.annotation.Resource;
import org.springframework.dao.DataAccessException;
import org.springframework.web.bind.annotation.*;
import server.mine.servertest.mysql.Dao.DocViewDao;
import server.mine.servertest.mysql.Dao.DocumentDao;
import server.mine.servertest.mysql.Dao.PermissionDao;
import server.mine.servertest.mysql.Dao.UserDao;
import server.mine.servertest.mysql.MysqlOperate;
import server.mine.servertest.mysql.bean.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
    private MysqlOperate mysqlOperate;

    //从数据库中获取文档内容
    private List<String> getContentListFromSQL(String tableName){
        List<String> currList = new ArrayList<>();
        var x = mysqlOperate.findall(tableName);
        for (var c : x
        ) {
            currList.add(c.getContent());
        }
        return  currList;
    }

    //创建新的临时文档
    private void getDocumentFromSQL(Integer docUID){
        DocMapBean newDoc = new DocMapBean();
        newDoc.setDocContent(getContentListFromSQL("doc"+docUID));
        newDoc.setNumber(newDoc.getNumber() + 1);
        allDocMap.put(docUID, newDoc);
    }

    //增加计数
    private void addDocumentMapNumber(Integer docUID){
        allDocMap.get(docUID).setNumber(allDocMap.get(docUID).getNumber() + 1);
    }

    //减计数
    private void minusDocumentMapNumber(Integer docUID){
        allDocMap.get(docUID).setNumber(allDocMap.get(docUID).getNumber() - 1);
        if(allDocMap.get(docUID).getNumber() <= 0){
            allDocMap.remove(docUID);
        }
    }

    //新建: 新建，上传，打开 or 新建，更新
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
        newDoc.setNumber(newDoc.getNumber() + 1);
        allDocMap.put(curr, newDoc);
        //发送返回值
        rmsg.setCode(200);
        rmsg.setObjectType("DocumentUID");
        rmsg.setObject(curr);
        return rmsg;
    }

    //上传文档内容(直接上传文档的话，内容要直接传上来，统一管理，这里不加计数)
    @PostMapping(path = "/newContent/{docUID}")
    public @ResponseBody ReturnMsg newContent(@PathVariable(value = "docUID") Integer docUID, @RequestBody RequestList content){
        var contentList = content.getContentList();
        ReturnMsg rmsg = new ReturnMsg();
        mysqlOperate.saveDocToSQL("doc"+docUID,contentList);
        //可以覆盖，所以重复就重复吧
        getDocumentFromSQL(docUID);
        allDocMap.get(docUID).setDocContent(getContentListFromSQL("doc"+docUID));
        rmsg.setCode(200);
        rmsg.setObjectType("String");
        rmsg.setObject("上传成功！");
        return rmsg;
    }

    //重命名
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

    //修改文档描述
    @PatchMapping(path = "/updateDesc/{docUID}")
    public @ResponseBody
    ReturnMsg changeDescription(@PathVariable(value = "docUID") Integer docUID, @RequestBody RequestString newDesc) {
        System.out.println(newDesc);
        ReturnMsg rmsg = new ReturnMsg();
        if (!allDocMap.containsKey(docUID)) {
            getDocumentFromSQL(docUID);
        }
        allDocMap.get(docUID).getDocContent().set(0, newDesc.getRequestContent());
        mysqlOperate.updateDesc("doc"+docUID,newDesc.getRequestContent());
        rmsg.setCode(200);
        rmsg.setObjectType("String");
        rmsg.setObject("修改成功");
        return rmsg;
    }

    //打开文档，上传完接获取——因为我不识别主机号，所以要避免同一台主机触发两次序列号加的操作
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
        rmsg.setObject(allDocMap.get(docUID).getDocContent());
        return rmsg;
    }

    //保存，文档值保存到数据库中
    @PostMapping(path = "/save/{docUID}")
    public @ResponseBody ReturnMsg saveDocumentToSQL(@PathVariable(value = "docUID") Integer docUID){
        ReturnMsg rmsg = new ReturnMsg();
        if(!allDocMap.containsKey(docUID)){
            rmsg.setCode(403);
            rmsg.setObjectType("String");
            rmsg.setObject("ERROR: 未上传，不能保存");
            return rmsg;
        }else {
            mysqlOperate.saveDocToSQL("doc"+docUID,allDocMap.get(docUID).getDocContent());
        }
        rmsg.setCode(200);
        rmsg.setObjectType("String");
        rmsg.setObject("保存成功！");
        return rmsg;
    }

    //关闭，减引用，并保存
    @PostMapping(path = "/close/{docUID}")
    public @ResponseBody ReturnMsg closeDocument(@PathVariable(value = "docUID") Integer docUID){
        ReturnMsg rmsg = new ReturnMsg();
        mysqlOperate.saveDocToSQL("doc"+docUID,allDocMap.get(docUID).getDocContent());
        minusDocumentMapNumber(docUID);
        rmsg.setCode(200);
        rmsg.setObjectType("String");
        rmsg.setObject("即将关闭文章！");
        return rmsg;
    }

    //更新文档内容，某一行的更改
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
        System.out.println("表是否没内容：" + allDocMap.isEmpty());
//        System.out.println(contentList.getContentList());
//        System.out.println(contentList.getContentList().get(0));
        ReturnMsg rmsg = new ReturnMsg();
        rmsg.setCode(200);
        rmsg.setObjectType("String");
        rmsg.setObject("即将关闭文章！");
        return rmsg;
    }

}
