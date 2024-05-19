package datamodel;

public class CarInside {
    private String car_number;
    private String time_in;
    private String time_pay_count;
    private String time_pay;
    private int cost;
    private int discount;
    private String gate;
    private String bill_number;
    private String payment;
    private int artificial;
    private String picture_url;
    private String type;
    private String color;

    public CarInside(String carNumber, String timeIn, String timePayCount, String timePay, int cost, int discount, String gate, String billNumber, String payment, int artificial, String pictureUrl, String type, String color) {
        this.setType(type);
        this.setColor(color);
        setPicture_url(pictureUrl);
        setCar_number(carNumber);
        setTime_in(timeIn);
        setTime_pay_count(timePayCount);
        setTime_pay(timePay);
        this.setCost(cost);
        this.setDiscount(discount);
        this.setGate(gate);
        setBill_number(billNumber);
        this.setPayment(payment);
        this.setArtificial(artificial);
    }

    public String getCar_number() {
        return car_number;
    }

    public void setCar_number(String car_number) {
        this.car_number = car_number;
    }

    public String getTime_in() {
        return time_in;
    }

    public void setTime_in(String time_in) {
        this.time_in = time_in;
    }

    public String getTime_pay_count() {
        return time_pay_count;
    }

    public void setTime_pay_count(String time_pay_count) {
        this.time_pay_count = time_pay_count;
    }

    public String getTime_pay() {
        return time_pay;
    }

    public void setTime_pay(String time_pay) {
        this.time_pay = time_pay;
    }

    public int getCost() {
        return cost;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }

    public int getDiscount() {
        return discount;
    }

    public void setDiscount(int discount) {
        this.discount = discount;
    }

    public String getGate() {
        return gate;
    }

    public void setGate(String gate) {
        this.gate = gate;
    }

    public String getBill_number() {
        return bill_number;
    }

    public void setBill_number(String bill_number) {
        this.bill_number = bill_number;
    }

    public String getPayment() {
        return payment;
    }

    public void setPayment(String payment) {
        this.payment = payment;
    }

    public int getArtificial() {
        return artificial;
    }

    public void setArtificial(int artificial) {
        this.artificial = artificial;
    }

    public String getPicture_url() {
        return picture_url;
    }

    public void setPicture_url(String picture_url) {
        this.picture_url = picture_url;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }
}
