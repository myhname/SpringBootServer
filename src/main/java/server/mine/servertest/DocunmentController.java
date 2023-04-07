package server.mine.servertest;

import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import server.mine.servertest.mysql.Dao.DocViewDao;
import server.mine.servertest.mysql.Dao.DocumentDao;
import server.mine.servertest.mysql.Dao.PermissionDao;
import server.mine.servertest.mysql.Dao.UserDao;

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


}
