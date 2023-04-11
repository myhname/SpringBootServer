package server.mine.servertest.mysql;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Service;
import server.mine.servertest.mysql.bean.DocContentBean;
import server.mine.servertest.mysql.bean.UserBean;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Service("mysqlOperate")
public class MysqlOperate {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * 转义特殊字符
     * @param old
     * @return new
     */
    static String escapeSpecialPunctuation(String old){
        return old.replace("'","\\'").replace("\"","\\\"").replace("%","\\%");
    }

    //获得文章内容
    public List<DocContentBean> findall(String tableName) {
        String sql = "select content from " + tableName + " order by rowNumber";
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(DocContentBean.class));
    }

    //修改文章描述
    public void updateDesc(String tableName, String newDesc){
        newDesc = escapeSpecialPunctuation(newDesc);
        String sql = "update " + tableName + " set content='" + newDesc + "' where rowNumber=0";
        jdbcTemplate.update(sql);
    }

    /**
     * 修改文章某一行的内容
     * @param tableName
     * @param rowNumber
     * @param newContent
     */
    public void updateRowContent(String tableName, Integer rowNumber ,String newContent){
        newContent = escapeSpecialPunctuation(newContent);
        String sql = "update " + tableName + " set content='" + newContent + "' where rowNumber=" + rowNumber;
        jdbcTemplate.update(sql);
    }

    //获得文章某一行的内容
    public String findRowContent(String tableName, Integer rowNumber){
        String sql = "select content from " + tableName + " where rowNumber=" + rowNumber;
        var a = jdbcTemplate.queryForMap(sql);
        return (String) a.values().toArray()[0];
    }

    /**
     * 获取文章最大行号
     * @param tableName
     * @return
     */
    public Integer getMaxRowNumber(String tableName){
        String sql = "select max(rowNumber) from " + tableName;
        var a = jdbcTemplate.queryForMap(sql);
        return (Integer) a.values().toArray()[0];
    }

    /**
     * 新增某一行内容（最后一行）
     * @param tableName
     * @param rowNumber
     * @param content
     */
    public void newRowContent(String tableName, Integer rowNumber ,String content){
        String sql = "insert " + tableName + " values(" + rowNumber + ",\"" + escapeSpecialPunctuation(content) + "\");";
        jdbcTemplate.update(sql);
    }

    /**
     * 保存
     * @param tableName
     * @param contents
     */
    public void saveDoc(String tableName, List<DocContentBean> contents){
        if (contents.isEmpty()){
            return;
        }
        String sql;
        for (DocContentBean c:contents
             ) {
            sql = "insert into " + tableName + " values(" + c.getRowNumber() + ",\"" + escapeSpecialPunctuation(c.getContent()) + "\");";
            jdbcTemplate.update(sql);
        }
    }

    /**
     * 向数据库中保存
     * @param tableName
     * @param contentList
     */
    public void saveDocToSQL(String tableName, List<String> contentList){
        Integer maxLineNumber = getMaxRowNumber(tableName);
        for(int i=0;i<contentList.size();i++){
            if(i<=maxLineNumber){
                updateRowContent(tableName,i,contentList.get(i));
            }else{
                newRowContent(tableName,i,contentList.get(i));
            }
        }
    }

    /**
     * 建表，空表
     * @param tableName
     */
    public void newDoc(String tableName){
        String sql = "create table " + tableName + "(rowNumber int,content TEXT,PRIMARY KEY(rowNumber));";
        jdbcTemplate.execute(sql);
    }
}
