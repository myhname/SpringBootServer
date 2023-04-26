package server.mine.servertest.mysql.Dao;

import org.springframework.data.repository.CrudRepository;
import server.mine.servertest.mysql.bean.ModifyRecordBean;

public interface ModifyRecordDao   extends CrudRepository<ModifyRecordBean,Integer> {
}
