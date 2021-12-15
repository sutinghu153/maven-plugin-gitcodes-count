package sutinghu.tools.entity;

import java.util.Date;

/**
 * @data2021/12/15,15:31
 * @authorsutinghu
 */
public class UserCount {

    private String userName;

    private Date comTime;

    private Integer allSize;

    private Integer addSize;

    private Integer subSize;

    private String name;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Date getComTime() {
        return comTime;
    }

    public void setComTime(Date comTime) {
        this.comTime = comTime;
    }

    public Integer getAllSize() {
        return allSize;
    }

    public void setAllSize(Integer allSize) {
        this.allSize = allSize;
    }

    public Integer getAddSize() {
        return addSize;
    }

    public void setAddSize(Integer addSize) {
        this.addSize = addSize;
    }

    public Integer getSubSize() {
        return subSize;
    }

    public void setSubSize(Integer subSize) {
        this.subSize = subSize;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "details[" +
                "userName='" + userName + '\'' +
                ", commit time='" + comTime + '\'' +
                ", all lines=" + allSize +
                ", add lines=" + addSize +
                ", sub lines=" + subSize +
                ", commit version='" + name + '\'' +
                ']';
    }
}
