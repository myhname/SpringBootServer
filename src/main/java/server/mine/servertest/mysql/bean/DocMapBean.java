package server.mine.servertest.mysql.bean;

import java.util.ArrayList;
import java.util.List;

public class DocMapBean {

    private List<String> docContent;
    private Integer lineNumber;
    private Integer userNumber;

    public Integer getLineNumber() {
        return lineNumber;
    }

    public void setLineNumber(Integer lineNumber) {
        this.lineNumber = lineNumber;
    }

    public DocMapBean(){
        docContent = new ArrayList<>();
        lineNumber = 0;
        userNumber = 0;
    }

    public List<String> getDocContent() {
        return docContent;
    }

    public void setDocContent(List<String> docContent) {
        this.docContent = docContent;
    }

    public Integer getUserNumber() {
        return userNumber;
    }

    public void setUserNumber(Integer number) {
        this.userNumber = number;
    }

}
