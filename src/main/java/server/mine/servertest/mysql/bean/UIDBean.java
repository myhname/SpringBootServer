package server.mine.servertest.mysql.bean;

import java.io.Serializable;

public class UIDBean implements Serializable {
    Integer docUID;
    Integer UUID;

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
}
