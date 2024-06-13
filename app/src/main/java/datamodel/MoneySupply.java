package datamodel;

public class MoneySupply {
    private int id;
    private  int five;
    private int ten;
    private int fifty;
    private int five_count;
    private int ten_count;
    private int fifty_count;
    private int supply;
    private String ip;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getFive() {
        return five;
    }

    public void setFive(int five) {
        this.five = five;
    }

    public int getTen() {
        return ten;
    }

    public void setTen(int ten) {
        this.ten = ten;
    }

    public int getFifty() {
        return fifty;
    }

    public void setFifty(int fifty) {
        this.fifty = fifty;
    }

    public int getFive_count() {
        return five_count;
    }

    public void setFive_count(int five_count) {
        this.five_count = five_count;
    }

    public int getTen_count() {
        return ten_count;
    }

    public void setTen_count(int ten_count) {
        this.ten_count = ten_count;
    }

    public int getFifty_count() {
        return fifty_count;
    }

    public void setFifty_count(int fifty_count) {
        this.fifty_count = fifty_count;
    }

    public int getSupply() {
        return supply;
    }

    public void setSupply(int supply) {
        this.supply = supply;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }
}
