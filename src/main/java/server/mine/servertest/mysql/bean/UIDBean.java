package server.mine.servertest.mysql.bean;

import java.io.Serializable;

public class UIDBean implements Serializable {
    Integer docUID;
    Integer userUID;

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
}
