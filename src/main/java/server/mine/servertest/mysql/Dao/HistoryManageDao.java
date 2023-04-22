package server.mine.servertest.mysql.Dao;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import server.mine.servertest.mysql.bean.DocumentBean;
import server.mine.servertest.mysql.bean.HistoryManageBean;

import java.util.List;

public interface HistoryManageDao extends CrudRepository<HistoryManageBean, Integer> {
}