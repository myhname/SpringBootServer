package server.mine.servertest.mysql.bean;

import jakarta.persistence.*;

@Entity
@Table(name="docView")
public class DocViewBean {
    @Id
    @Column(name="uid")
    private Integer uid;
    @Column(name = "docUid")
    private Integer docUid;
    @Column(name = "title")
    private String title;
    @Column(name = "format")
    private String format;
    @Column(name = "permissionType")
    private String permissionType;

    public Integer getUid() {
        return uid;
    }

    public void setUid(Integer uid) {
        this.uid = uid;
    }

    public Integer getDocUid() {
        return docUid;
    }

    public void setDocUid(Integer docUid) {
        this.docUid = docUid;
    }

    public String getPermissionType() {
        return permissionType;
    }

    public void setPermissionType(String permissionType) {
        this.permissionType = permissionType;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }
}
