package datamodel;

public class MoneyBasic {
    private int id;
    private int five_basic;
    private int ten_basic;
    private int fifty_basic;
    private int five_alert;
    private int ten_alert;
    private int fifty_alert;
    private String ip;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getFive_basic() {
        return five_basic;
    }

    public void setFive_basic(int five_basic) {
        this.five_basic = five_basic;
    }

    public int getTen_basic() {
        return ten_basic;
    }

    public void setTen_basic(int ten_basic) {
        this.ten_basic = ten_basic;
    }

    public int getFifty_basic() {
        return fifty_basic;
    }

    public void setFifty_basic(int fifty_basic) {
        this.fifty_basic = fifty_basic;
    }

    public int getFive_alert() {
        return five_alert;
    }

    public void setFive_alert(int five_alert) {
        this.five_alert = five_alert;
    }

    public int getTen_alert() {
        return ten_alert;
    }

    public void setTen_alert(int ten_alert) {
        this.ten_alert = ten_alert;
    }

    public int getFifty_alert() {
        return fifty_alert;
    }

    public void setFifty_alert(int fifty_alert) {
        this.fifty_alert = fifty_alert;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }
}
