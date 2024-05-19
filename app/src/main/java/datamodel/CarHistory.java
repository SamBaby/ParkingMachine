package datamodel;

public class CarHistory {
    private long id;
    private String car_number;
    private String time_in;
    private String time_out;
    private String time_pay;
    private int cost;
    private String bill_number;
    private String payment;
    private int artificial;
    private String type;
    private String color;
    public CarHistory(long id, String carNumber, String timeIn, String timeOut, String timePay, int cost, String billNumber, String payment, int artificial, String type, String color) {
        this.setType(type);
        this.setColor(color);
        this.setArtificial(artificial);
        this.setId(id);
        setCar_number(carNumber);
        setTime_in(timeIn);
        setTime_out(timeOut);
        setTime_pay(timePay);
        this.setCost(cost);
        setBill_number(billNumber);
        this.setPayment(payment);
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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

    public String getTime_out() {
        return time_out;
    }

    public void setTime_out(String time_out) {
        this.time_out = time_out;
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
