package server.mine.servertest.websocket;

public class UpdateContentBean {

    private Integer docUID;
    private Integer rowNumber;
    private String content;

    //{"docUID":3,"rowNumber":3,"content":"2222"}以此类JSON格式传递消息的初始化
    public UpdateContentBean(String jsonString) {
        var curr = jsonString.split(",");
        docUID = Integer.valueOf(curr[0].split(":")[curr[0].split(":").length -1]);
        rowNumber = Integer.valueOf(curr[1].split(":")[curr[1].split(":").length -1]);
        String x = curr[2].split(":")[curr[2].split(":").length -1];
        content = x.substring(1,x.length()-2);
    }

    public Integer getDocUID() {
        return docUID;
    }

    public void setDocUID(Integer docUID) {
        this.docUID = docUID;
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
