package server.mine.servertest.mysql.bean;

import jakarta.persistence.*;

@Entity
@IdClass(UIDBean.class)
@Table(name="docview")
public class DocView {
    @Id
    @Column(name="uid")
    private Integer userUID;

    @Id
    @Column(name = "docUid")
    private Integer docUID;

    @Column(name = "title")
    private String title;
    @Column(name = "format")
    private String format;
    @Column(name = "permissionType")
    private String permissionType;

    public Integer getUserUID() {
        return userUID;
    }

    public void setUserUID(Integer uid) {
        this.userUID = uid;
    }

    public Integer getDocUID() {
        return docUID;
    }

    public void setDocUID(Integer docUid) {
        this.docUID = docUid;
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
