package server.mine.servertest.mysql.Dao;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import server.mine.servertest.mysql.bean.PermissionBean;
import server.mine.servertest.mysql.bean.UIDBean;

import java.util.List;

public interface PermissionDao extends CrudRepository<PermissionBean, UIDBean> {

    @Query("select p from PermissionBean p where p.UUID=?1")
    List<PermissionBean> getPermissionBeanByUUID(Integer uid);
}
