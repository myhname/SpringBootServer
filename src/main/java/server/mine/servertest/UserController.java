package server.mine.servertest;

import jakarta.annotation.Resource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import server.mine.servertest.JWT.JWTInterceptor;
import server.mine.servertest.JWT.JWTUtils;
import server.mine.servertest.mysql.Dao.DocViewDao;
import server.mine.servertest.mysql.Dao.DocumentDao;
import server.mine.servertest.mysql.Dao.PermissionDao;
import server.mine.servertest.mysql.Dao.UserDao;
import server.mine.servertest.mysql.bean.DocViewBean;
import server.mine.servertest.mysql.bean.DocumentBean;
import server.mine.servertest.mysql.bean.UserBean;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(path = "/user")
@CrossOrigin("*")
public class UserController {

    @Resource
    private UserDao userDao;

    @Resource
    private DocumentDao documentDao;

    @Resource
    private PermissionDao permissionDao;

    @Resource
    private DocViewDao docViewDao;

    //登录
    @PostMapping(path = "/login")
    public @ResponseBody
    ReturnMsg login(@RequestParam String account, @RequestParam String password) {
        System.out.println("接收到:"+account);
        var x = userDao.findAll();
        UserBean curr = null;
        for (var c : x) {
            //todo 测试输出
//            System.out.println(c.getAccount());
            if (c.getAccount().equals(account)) {
                curr = c;
                break;
            }
        }
        ReturnMsg rmsg = new ReturnMsg();
        if (curr == null) {
            rmsg.setCode(401);
            rmsg.setObjectType("String");
            rmsg.setObject("Error: User Not Found!");
            return rmsg;
        }
        if(curr.getPassword().equals(password)){
            rmsg.setCode(200);
            rmsg.setObjectType("Token");
            Map<String,String> payload = new HashMap<>();
            payload.put("account",account);
            payload.put("uuid",curr.getUUID().toString());
            String token = JWTUtils.getToken(payload);
            rmsg.setObject(token);
        }else {
            rmsg.setCode(403);
            rmsg.setObjectType("String");
            rmsg.setObject("Error: Password Mismatched!");
        }
        return rmsg;
    }

    //注册
    @PostMapping(path = "/register")
    public @ResponseBody ReturnMsg register(@RequestBody UserBean newUser){
        //todo 测试输出
        System.out.println("接收到信息:" + newUser.toString());
        ReturnMsg rmsg = new ReturnMsg();
        var x = userDao.findAll();
        for (var c:x
             ) {
            if(c.getAccount().equals(newUser.getAccount())){
                rmsg.setCode(401);
                rmsg.setObjectType("String");
                rmsg.setObject("Error: 账户名重复!");
                return rmsg;
            }
        }
        userDao.save(newUser);
        rmsg.setCode(200);
        rmsg.setObjectType("Token");
        Map<String,String> payload = new HashMap<>();
        payload.put("account",newUser.getAccount());
        payload.put("uuid",newUser.getUUID().toString());
        String token = JWTUtils.getToken(payload);
        rmsg.setObject(token);
        return rmsg;
    }

    //删除指定用户
    @DeleteMapping(path = "/deleteUser/{uid}")
    public @ResponseBody ReturnMsg deleteUser(@PathVariable(value = "uid") Integer uid){
        var x = userDao.findById(uid);
        System.out.println(x);
        ReturnMsg rmsg = new ReturnMsg();
        if(x.isEmpty()){
            rmsg.setCode(401);
            rmsg.setObjectType("String");
            rmsg.setObject("Error: 用户不存在！");
            return rmsg;
        }
        userDao.deleteById(uid);
        rmsg.setCode(200);
        rmsg.setObjectType("String");
        rmsg.setObject("注销成功！");
        return rmsg;
    }

    //修改密码
    @PatchMapping(value = "/changePassword/{uid}")
    public @ResponseBody ReturnMsg changePassword(@PathVariable(value = "uid") Integer uid, @RequestParam String newPassword){
        var x = userDao.findById(uid);
        System.out.println(x);
        ReturnMsg rmsg = new ReturnMsg();
        if(x.isEmpty()){
            rmsg.setCode(401);
            rmsg.setObjectType("String");
            rmsg.setObject("用户不存在！");
            return rmsg;
        }
        UserBean curr = x.get();
        curr.setPassword(newPassword);
        userDao.save(curr);
        rmsg.setCode(200);
        rmsg.setObjectType("String");
        rmsg.setObject("修改成功!");
        return rmsg;
    }

    //获取文档列表
    @GetMapping(path = "/docList/{uid}")
    public ReturnMsg getDocList(@PathVariable(value = "uid") Integer uid){
        var x = userDao.findById(uid);
        System.out.println(x);
        ReturnMsg rmsg = new ReturnMsg();
        if(x.isEmpty()){
            rmsg.setCode(406);
            rmsg.setObjectType("String");
            rmsg.setObject("用户不存在");
            return rmsg;
        }
        rmsg.setCode(200);
        rmsg.setObjectType("List of DocumentBean");
//        var y = permissionDao.getPermissionBeanByUUID(uid);
//        var y = documentDao.getDocumentBeanByAuthorUID(uid);
        var y = docViewDao.getDocViewBeanByUid(uid);
        rmsg.setObject(y);
        return rmsg;
    }
}
