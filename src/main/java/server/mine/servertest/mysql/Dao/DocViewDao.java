package server.mine.servertest.mysql.Dao;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import server.mine.servertest.mysql.bean.DocViewBean;
import server.mine.servertest.mysql.bean.DocumentBean;

import java.util.List;

public interface DocViewDao extends CrudRepository<DocViewBean, Integer> {

    @Query("select d from docView d where d.uid=?1")
    List<DocViewBean> getDocViewBeanByUid(Integer uid);

}
