package server.mine.servertest.websocket;

public class ModifyRecord {

    public ModifyRecord(String time, Integer userUID) {
        this.time = time;
        this.userUID = userUID;
    }

    private String time;
    private Integer userUID;



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
}
