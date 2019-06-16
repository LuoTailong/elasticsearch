package cn.itcast.bean;

/**
 * Created by angel on 2018/4/26.
 */
public class Document {
    private Integer id;
    private String title;
    private String content;
    private String comment;

    public String getTitle() {
        return title;
    }

    public Integer getId() {
        return id;
    }

    public String getComment() {
        return comment;
    }

    public String getContent() {
        return content;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
