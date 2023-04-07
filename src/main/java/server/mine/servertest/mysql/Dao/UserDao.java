package server.mine.servertest.mysql.Dao;

import jakarta.persistence.Table;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.core.CrudMethods;
import server.mine.servertest.mysql.bean.UserBean;

public interface UserDao extends CrudRepository<UserBean, Integer> {
}
