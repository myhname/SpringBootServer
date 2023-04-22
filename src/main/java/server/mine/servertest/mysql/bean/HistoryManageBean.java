package server.mine.servertest.mysql.bean;

import jakarta.persistence.*;

@Entity
@Table(name = "historymanage")
public class HistoryManageBean {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "uid")
    private Integer uid;
    @Column(name = "docUId")
    private Integer docUID;
    @Column(name = "time")
    private String time;
    @Column(name = "name")
    private  String name;

    public Integer getUid() {
        return uid;
    }

    public void setUid(Integer uid) {
        this.uid = uid;
    }

    public Integer getDocUID() {
        return docUID;
    }

    public void setDocUID(Integer docUID) {
        this.docUID = docUID;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
