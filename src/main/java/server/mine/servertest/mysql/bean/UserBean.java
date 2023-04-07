package server.mine.servertest.mysql.bean;

import jakarta.persistence.*;

@Entity
@Table(name = "user")
public class UserBean {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer UUID;
    private String name;
    private String password;
    private String account;

    public Integer getUUID() {
        return UUID;
    }

    public void setUUID(int UUID) {
        this.UUID = UUID;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UserBean userBean = (UserBean) o;

        if (!getUUID().equals(userBean.getUUID())) return false;
        if (!getName().equals(userBean.getName())) return false;
        if (!getPassword().equals(userBean.getPassword())) return false;
        return getAccount().equals(userBean.getAccount());
    }

    @Override
    public int hashCode() {
        int result = getUUID().hashCode();
        result = 31 * result + getName().hashCode();
        result = 31 * result + getPassword().hashCode();
        result = 31 * result + getAccount().hashCode();
        return result;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    @Override
    public String toString() {
        return "UserBean{" +
                "UUID=" + UUID +
                ", name='" + name + '\'' +
                ", password='" + password + '\'' +
                ", account='" + account + '\'' +
                '}';
    }
}
