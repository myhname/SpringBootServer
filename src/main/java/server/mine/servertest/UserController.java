package server.mine.servertest;

import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;
import server.mine.servertest.JWT.JWTUtils;
import server.mine.servertest.mysql.Dao.*;
import server.mine.servertest.mysql.bean.CommendsBean;
import server.mine.servertest.mysql.bean.PermissionBean;
import server.mine.servertest.mysql.bean.RequestString;
import server.mine.servertest.mysql.bean.UserBean;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

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

    @Resource
    private CommendsDao commendsDao;

    /**
     * 登录
     * @param user userBean uid为空，账户，昵称为空，密码
     * @return 失败；成功返回用户uid和token
     */
    @PostMapping(path = "/login")
    public @ResponseBody
    ReturnMsg login(@RequestBody UserBean user) {
        var x = userDao.findAll();
        UserBean curr = null;
        for (var c : x) {
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

    /**
     * 注册
     * @param newUser userBean uid为空，账户，昵称，密码
     * @return 失败；成功返回用户uid和token
     */
    @PostMapping(path = "/register")
    public @ResponseBody ReturnMsg register(@RequestBody UserBean newUser){
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
        UserBean curr = null;
        for (var c : x) {
            if (c.getAccount().equals(newUser.getAccount())) {
                curr = c;
                break;
            }
        }
        rmsg.setCode(200);
        rmsg.setObjectType("Token");
        Map<String,String> payload = new HashMap<>();
        payload.put("account",newUser.getAccount());
        payload.put("uuid",newUser.getUUID().toString());
        String token = JWTUtils.getToken(payload);
        String backMsg = token + "|||" + curr.getUUID();
         rmsg.setObject(backMsg);
         return rmsg;
    }

    /**
     * 删除指定用户
     * @param uid 请求时附带参数
     * @return 失败；成功
     */
    @DeleteMapping(path = "/deleteUser/{uid}")
    public @ResponseBody ReturnMsg deleteUser(@PathVariable(value = "uid") Integer uid){
        var x = userDao.findById(uid);
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

    /**
     * 修改密码
     * @param uid 标识用户身份UID
     * @param newPassword 新的密码
     * @return 失败；成功
     */
    @PatchMapping(value = "/changePassword/{uid}")
    public @ResponseBody ReturnMsg changePassword(@PathVariable(value = "uid") Integer uid, @RequestBody RequestString newPassword){
        var x = userDao.findById(uid);
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

    /**
     * 获取文档列表
     * @param uid 标识用户身份
     * @return 失败；成功返回该用户拥有访问权限的文章列表
     */
    @GetMapping(path = "/docList/{uid}")
    public ReturnMsg getDocList(@PathVariable(value = "uid") Integer uid){
        var x = userDao.findById(uid);
        ReturnMsg rmsg = new ReturnMsg();
        if(x.isEmpty()){
            rmsg.setCode(406);
            rmsg.setObjectType("String");
            rmsg.setObject("用户不存在");
            return rmsg;
        }
        rmsg.setCode(200);
        rmsg.setObjectType("List of DocView");
        var y = docViewDao.getDocViewBeanByUid(uid);
        rmsg.setObject(y);
        return rmsg;
    }

    /**
     * 修改权限
     * @param uid 标识用户身份
     * @param permission userUID docUID permissionType 删除的话给delete
     * @return
     */
    @PostMapping(path = "/addPermission/{UID}")
    public  ReturnMsg

    addPermission(@PathVariable(value = "UID") Integer uid, @RequestBody PermissionBean permission){
//        先验证权限
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
//        获取记录，有or无
        var y = permissionDao.getPermissionBeanByUUIDAAndDocUID(permission.getUserUID(),permission.getDocUID());
//        如果是要撤销权限
        if(Objects.equals(permission.getPermissionType(), "delete")){
            if(y.isEmpty()){
                rmsg.setObject("但是用户本来也没权限");
            }else {
                permissionDao.delete(y.get(0));
                rmsg.setObjectType("撤销成功");
            }
            rmsg.setCode(200);
            rmsg.setObjectType("String");
            return rmsg;
        }
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

    /**
     * 添加评论
     * @param commend 上传评论内容
     * @return
     */
    @PostMapping(path = "/addCommends")
    public  ReturnMsg addCommends(@RequestBody CommendsBean commend){
        ReturnMsg rmsg = new ReturnMsg();
        commendsDao.save(commend);
        rmsg.setCode(200);
        rmsg.setObjectType("String");
        rmsg.setObject("添加成功");
        return rmsg;
    }

    /**
     * 删除某条评论
     * @param uid 标识用户身份
     * @param commend 被删除的评论信息
     * @return
     */
    @PostMapping(path = "/deleteCommends/{UID}")
    public ReturnMsg deleteCommends(@PathVariable(value = "UID") Integer uid, @RequestBody CommendsBean commend){
        ReturnMsg rmsg = new ReturnMsg();
//        首先判断是否有权限删除评论，本人的文章，或者本人的评论
        if(!(Objects.equals(uid, commend.getUid()))){
            boolean flag = false;
            var docList =  documentDao.getDocumentBeanByAuthorUID(uid);
            for (var c:docList
            ) {
                if(Objects.equals(c.getDocUID(), commend.getDocUID())){
                    flag = true;
                    break;
                }
            }
            if(!flag){
                rmsg.setCode(400);
                rmsg.setObjectType("String");
                rmsg.setObject("您没有操作权限");
            }
        }
        commendsDao.delete(commend);
        rmsg.setCode(200);
        rmsg.setObjectType("String");
        rmsg.setObject("成功");
        return rmsg;
    }

}
