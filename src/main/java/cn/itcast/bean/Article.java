package cn.itcast.bean;

/**
 * Created by angel on 2018/4/26.
 */
public class Article {
    private String title;
    private String author;
    private String content;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setAuthor(String author) {
        this.author = author;
    }


    public void setTitle(String title) {
        this.title = title;
    }



    public String getAuthor() {
        return author;
    }

    public String getTitle() {
        return title;
    }

    @Override
    public String toString() {
        return title+"----"+author+"---"+content;
    }
}
