package server.mine.servertest.mysql.bean;

import jakarta.persistence.*;

@Entity
@IdClass(UIDBean.class)
@Table(name = "permission")
public class PermissionBean {

    @Id
    @Column(name = "docUID")
    private Integer docUID;

    @Override
    public String toString() {
        return "PermissionBean{" +
                "docUID=" + docUID +
                ", UUID=" + userUID +
                ", permissionType='" + permissionType + '\'' +
                '}';
    }

    @Id
    @Column(name = "UUID")
    private Integer userUID;

    @Column(name = "permissionType")
    String permissionType;

    public PermissionBean() {
    }

    public PermissionBean(int docUID, int UUID, String permissionType) {
        this.docUID = docUID;
        this.userUID = UUID;
        this.permissionType = permissionType;
    }

    public int getDocUID() {
        return docUID;
    }

    public void setDocUID(int docUID) {
        this.docUID = docUID;
    }

    public int getUserUID() {
        return userUID;
    }

    public void setUserUID(int UUID) {
        this.userUID = UUID;
    }

    public String getPermissionType() {
        return permissionType;
    }

    public void setPermissionType(String permissionType) {
        this.permissionType = permissionType;
    }
}
