package server.mine.servertest.mysql.bean;

import jakarta.persistence.*;

@Entity
@Table(name = "document")
public class DocumentBean {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer docUID;
    private String title;
    private String format;
    private Integer authorUID;

    public int getDocUID() {
        return docUID;
    }

    public void setDocUID(Integer docUID) {
        this.docUID = docUID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public int getAuthorUID() {
        return authorUID;
    }

    public void setAuthorUID(int authorUID) {
        this.authorUID = authorUID;
    }
}
