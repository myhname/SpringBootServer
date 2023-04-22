package server.mine.servertest.mysql.bean;

import jakarta.persistence.*;

@Entity
@Table(name = "commends")
public class CommendsBean {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "uid")
    private Integer uid;
    @Column(name = "docUId")
    private Integer docUID;
    @Column(name = "authorUID")
    private Integer authorUID;
    @Column(name = "rowNumber")
    private Integer rowNumber;
    @Column(name = "content")
    private String content;

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

    public Integer getAuthorUID() {
        return authorUID;
    }

    public void setAuthorUID(Integer authorUID) {
        this.authorUID = authorUID;
    }

    public Integer getRowNumber() {
        return rowNumber;
    }

    public void setRowNumber(Integer rowNumber) {
        this.rowNumber = rowNumber;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
