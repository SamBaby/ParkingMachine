package datamodel;

public class User {
    private String account;
    private String password;
    private String name;
    private String phone;
    private String permission;

    public User(String account, String password, String name, String phone, String permission) {
        this.account = account;
        this.password = password;
        this.name = name;
        this.phone = phone;
        this.permission = permission;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPermission() {
        return permission;
    }

    public void setPermission(String permission) {
        this.permission = permission;
    }
}
