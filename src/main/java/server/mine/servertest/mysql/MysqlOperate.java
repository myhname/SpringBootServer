package server.mine.servertest.mysql;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import server.mine.servertest.mysql.bean.UserBean;

import java.util.List;

@Service("mysqlOperate")
public class MysqlOperate {
        private JdbcTemplate jdbcTemplate;

        //查询全部用户信息
        public List<UserBean> findall(){
            String sql = "seletct * from user";
            return  jdbcTemplate.query(sql,new BeanPropertyRowMapper<UserBean>(UserBean.class));
        }

}
