package server.mine.servertest.mysql.bean;

import jakarta.persistence.*;

@Entity
@Table(name = "modiftrecord")
public class ModifyRecordBean {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "uid")
    private Integer uid;
    @Column(name = "docUId")
    private Integer docUID;
    @Column(name = "time")
    private String time;
    @Column(name = "userUID")
    private Integer userUID;
    @Column(name = "rowNumber")
    private Integer rowNumber;

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

    public Integer getUserUID() {
        return userUID;
    }

    public void setUserUID(Integer userUID) {
        this.userUID = userUID;
    }

    public Integer getRowNumber() {
        return rowNumber;
    }

    public void setRowNumber(Integer rowNumber) {
        this.rowNumber = rowNumber;
    }
}
