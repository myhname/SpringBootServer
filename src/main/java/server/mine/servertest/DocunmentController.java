package server.mine.servertest;

import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;
import server.mine.servertest.mysql.Dao.DocViewDao;
import server.mine.servertest.mysql.Dao.DocumentDao;
import server.mine.servertest.mysql.Dao.PermissionDao;
import server.mine.servertest.mysql.Dao.UserDao;
import server.mine.servertest.mysql.bean.DocumentBean;
import server.mine.servertest.mysql.bean.PermissionBean;

@RestController
@RequestMapping(path = "/document")
@CrossOrigin("*")
public class DocunmentController {

    @Resource
    private UserDao userDao;

    @Resource
    private DocumentDao documentDao;

    @Resource
    private PermissionDao permissionDao;

    @Resource
    private DocViewDao docViewDao;


    //新建、初次上传文档
    @PostMapping(path = "/new/{uid}")
    public @ResponseBody ReturnMsg newDocument(@PathVariable(value = "uid") Integer uid, @RequestBody DocumentBean newDocument){
        ReturnMsg rmsg = new ReturnMsg();
        documentDao.save(newDocument);
        PermissionBean currp = new PermissionBean();
        Integer curr = documentDao.getDocumentBeanByMaxDocUID();
        currp.setUserUID(uid);
        currp.setDocUID(curr);
        currp.setPermissionType("RU");
        permissionDao.save(currp);
        rmsg.setCode(200);
        rmsg.setObjectType("DocumentUID");
        rmsg.setObject(curr);
        return rmsg;
    }

    //重命名
    @PatchMapping(path = "/updataTitle/{docUID}")
    public @ResponseBody ReturnMsg changeTitle(@PathVariable(value = "docUID") Integer docUID, @RequestBody String newTitle){
        var x = documentDao.findById(docUID);
        DocumentBean curr = x.get();
        curr.setTitle(newTitle);
        documentDao.save(curr);
        ReturnMsg rmsg = new ReturnMsg();
        rmsg.setCode(200);
        rmsg.setObjectType("String");
        rmsg.setObject("修改成功");
        return rmsg;
    }


}
