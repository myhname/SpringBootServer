package server.mine.servertest.mysql.bean;

import jakarta.persistence.Entity;

import java.util.List;

public class DocContentBean {
    private int rowNumber;
    private  String content;

    public int getRowNumber() {
        return rowNumber;
    }

    public void setRowNumber(int rowNumber) {
        this.rowNumber = rowNumber;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
