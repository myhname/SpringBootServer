package server.mine.servertest.mysql.Dao;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import server.mine.servertest.mysql.bean.DocView;

import java.util.List;

public interface DocViewDao extends CrudRepository<DocView, Integer> {

    @Query(value = "select d from DocView d where d.userUID=?1")
    List<DocView> getDocViewBeanByUid(Integer uid);

}
