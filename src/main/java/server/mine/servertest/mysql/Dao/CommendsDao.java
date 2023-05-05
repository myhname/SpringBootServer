package server.mine.servertest.mysql.Dao;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import server.mine.servertest.mysql.bean.CommendsBean;
import server.mine.servertest.mysql.bean.DocumentBean;

import java.util.List;

public interface CommendsDao extends CrudRepository<CommendsBean, Integer> {

    @Query("select d from CommendsBean d where d.docUID=?1")
    List<CommendsBean> getCommendsBeanByDocUID(Integer UID);

}
