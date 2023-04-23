package server.mine.servertest.websocket;

import java.util.List;

public class UpdateContentBean {

//    用户
    private Integer userUID;
//    文章
    private Integer docUID;
//    操作类型，行增+input、所有减+delete、粘贴paste，*compose编辑当前行
    private String changeType;
//    操作起始行号，from
    private Integer startLine;
//    text，新的文章内容
    private List<String> newContent;
//  removed，被修改的内容，直接替换所以只需要知道从当前行开始改变了多少行就行
    private Integer removedNumbers;

    public Integer getUserUID() {
        return userUID;
    }

    public void setUserUID(Integer userUID) {
        this.userUID = userUID;
    }

    public Integer getDocUID() {
        return docUID;
    }

    public void setDocUID(Integer docUID) {
        this.docUID = docUID;
    }

    public String getChangeType() {
        return changeType;
    }

    public void setChangeType(String changeType) {
        this.changeType = changeType;
    }

    public Integer getStartLine() {
        return startLine;
    }

    public void setStartLine(Integer startLine) {
        this.startLine = startLine;
    }

    public List<String> getNewContent() {
        return newContent;
    }

    public void setNewContent(List<String> newContent) {
        this.newContent = newContent;
    }

    public Integer getRemovedNumbers() {
        return removedNumbers;
    }

    public void setRemovedNumbers(Integer removedNumbers) {
        this.removedNumbers = removedNumbers;
    }

//    还差一个初始化
}
