package server.mine.servertest.mysql.bean;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;

@Entity
@IdClass(UIDBean.class)
@Table(name = "permission")
public class PermissionBean {

    @Id
    private Integer docUID;
    @Id
    private Integer UUID;
    String permissionType;

    public PermissionBean() {
    }

    public PermissionBean(int docUID, int UUID, String permissionType) {
        this.docUID = docUID;
        this.UUID = UUID;
        this.permissionType = permissionType;
    }

    public int getDocUID() {
        return docUID;
    }

    public void setDocUID(int docUID) {
        this.docUID = docUID;
    }

    public int getUUID() {
        return UUID;
    }

    public void setUUID(int UUID) {
        this.UUID = UUID;
    }

    public String getPermissionType() {
        return permissionType;
    }

    public void setPermissionType(String permissionType) {
        this.permissionType = permissionType;
    }
}
