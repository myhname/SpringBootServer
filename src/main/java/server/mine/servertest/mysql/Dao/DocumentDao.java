package server.mine.servertest.mysql.Dao;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import server.mine.servertest.mysql.bean.DocumentBean;
import server.mine.servertest.mysql.bean.UserBean;

import java.util.List;

public interface DocumentDao extends CrudRepository<DocumentBean, Integer> {

    @Query("select d from DocumentBean d where d.authorUID=?1")
    List<DocumentBean> getDocumentBeanByAuthorUID(Integer UID);

    @Query("select max(docUID) from DocumentBean")
    Integer getDocumentBeanByMaxDocUID();

}
