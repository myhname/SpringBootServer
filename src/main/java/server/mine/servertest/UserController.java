package server.mine.servertest;

import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;
import server.mine.servertest.JWT.JWTUtils;
import server.mine.servertest.mysql.Dao.DocViewDao;
import server.mine.servertest.mysql.Dao.DocumentDao;
import server.mine.servertest.mysql.Dao.PermissionDao;
import server.mine.servertest.mysql.Dao.UserDao;
import server.mine.servertest.mysql.bean.PermissionBean;
import server.mine.servertest.mysql.bean.RequestString;
import server.mine.servertest.mysql.bean.UserBean;

import java.util.HashMap;
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
    ReturnMsg login(@RequestBody UserBean user) {
        //todo
        System.out.println("接收到:"+user.getAccount());
        var x = userDao.findAll();
        UserBean curr = null;
        for (var c : x) {
            //todo 测试输出
//            System.out.println(c.getAccount());
            if (c.getAccount().equals(user.getAccount())) {
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
        if(curr.getPassword().equals(user.getPassword())){
            rmsg.setCode(200);
            rmsg.setObjectType("Token");
            Map<String,String> payload = new HashMap<>();
            payload.put("account",user.getAccount());
            payload.put("uuid",curr.getUUID().toString());
            String token = JWTUtils.getToken(payload);
            String backMsg = token + "|||" + curr.getUUID();
            rmsg.setObject(backMsg);
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
    public @ResponseBody ReturnMsg changePassword(@PathVariable(value = "uid") Integer uid, @RequestBody RequestString newPassword){
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
        curr.setPassword(newPassword.getRequestContent());
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
        rmsg.setObjectType("List of DocView");
//        var y = permissionDao.getPermissionBeanByUUID(uid);
//        var y = documentDao.getDocumentBeanByAuthorUID(uid);
        var y = docViewDao.getDocViewBeanByUid(uid);
        rmsg.setObject(y);
        return rmsg;
    }

    //修改权限
    @PostMapping(path = "/addPermission/{UID}")
    public  ReturnMsg addPermission(@PathVariable(value = "UID") Integer uid, @RequestBody PermissionBean permission){
        System.out.println(permission.toString());
        var x = documentDao.getDocumentBeanByAuthorUID(uid);
        boolean flag = false;
        for (var c:x
             ) {
            if(c.getDocUID().equals(permission.getDocUID())){
                flag = true;
                break;
            }
        }
        ReturnMsg rmsg = new ReturnMsg();
        if(!flag){
            rmsg.setCode(403);
            rmsg.setObjectType("String");
            rmsg.setObject("ERROR: 您并非这篇文章的作者，没有此类操作权限!");
            return rmsg;
        }
        var y = permissionDao.getPermissionBeanByUUIDAAndDocUID(permission.getUserUID(),permission.getDocUID());
        PermissionBean curr = new PermissionBean();
        if(y.isEmpty()){
            curr.setUserUID(permission.getUserUID());
            curr.setDocUID(permission.getDocUID());
        }else {
            curr = y.get(0);
        }
        curr.setPermissionType(permission.getPermissionType());
        permissionDao.save(curr);
        rmsg.setCode(200);
        rmsg.setObjectType("String");
        rmsg.setObject("成功！");
        return rmsg;
    }

}
