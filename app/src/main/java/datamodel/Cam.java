package datamodel;

public class Cam {
    private int number;
    private String name;
    private String ip;
    private int in_out;
    private int pay;
    private int open;

    public Cam(int number, String ip, String name, int in_out, int pay, int open) {
        this.number = number;
        this.name = name;
        this.ip = ip;
        this.in_out = in_out;
        this.pay = pay;
        this.open = open;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getIn_out() {
        return in_out;
    }

    public void setIn_out(int in_out) {
        this.in_out = in_out;
    }

    public int getPay() {
        return pay;
    }

    public void setPay(int pay) {
        this.pay = pay;
    }

    public int getOpen() {
        return open;
    }

    public void setOpen(int open) {
        this.open = open;
    }
}
