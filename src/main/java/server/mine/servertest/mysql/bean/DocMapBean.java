package server.mine.servertest.mysql.bean;

import java.util.ArrayList;
import java.util.List;

public class DocMapBean {

    private List<String> docContent;
    private Integer number;

    public DocMapBean(){
        docContent = new ArrayList<>();
        number = 0;
    }

    public List<String> getDocContent() {
        return docContent;
    }

    public void setDocContent(List<String> docContent) {
        this.docContent = docContent;
    }

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

}
