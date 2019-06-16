package cn.itcast.bean;

/**
 * Created by angel
 */
public class Articles {
    private String studentNo;
    private String name;
    private String male;
    private String age;
    private String birthday;
    private String classNo;
    private String address;
    private String isLeader;

    public void setAddress(String address) {
        this.address = address;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public void setClassNo(String classNo) {
        this.classNo = classNo;
    }

    public void setMale(String male) {
        this.male = male;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setStudentNo(String studentNo) {
        this.studentNo = studentNo;
    }

    public void setIsLeader(String isLeader) {
        this.isLeader = isLeader;
    }

    public String getAddress() {
        return address;
    }

    public String getAge() {
        return age;
    }

    public String getMale() {
        return male;
    }

    public String getBirthday() {
        return birthday;
    }

    public String getClassNo() {
        return classNo;
    }

    public String getIsLeader() {
        return isLeader;
    }

    public String getName() {
        return name;
    }

    public String getStudentNo() {
        return studentNo;
    }

    @Override
    public String toString() {
        return "[studentNO:"+studentNo+" name: +"+name+" age :"+age+" birthday: "+birthday+" classNo: "+classNo+" address: "+address+" isLeader: "+isLeader+"]";
    }

}
