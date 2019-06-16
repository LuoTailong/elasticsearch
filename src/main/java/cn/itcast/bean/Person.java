package cn.itcast.bean;

import java.util.Date;

/**
 * Created by angel on 2018/4/24.
 */
public class Person {
    private String user;
    private Date postDate;
    private String message;

    public Date getPostDate() {
        return postDate;
    }

    public String getMessage() {
        return message;
    }

    public String getUser() {
        return user;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setPostDate(Date postDate) {
        this.postDate = postDate;
    }

    public void setUser(String user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
