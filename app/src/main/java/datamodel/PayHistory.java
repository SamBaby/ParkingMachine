package datamodel;

public class PayHistory {
    private String car_number;
    private String time_in;
    private String time_pay;
    private int cost;
    private String bill_number;
    private String payment;

    public PayHistory(String carNumber, String timeIn, String timePay, int cost, String billNumber, String payment) {
        setCar_number(carNumber);
        setTime_in(timeIn);
        setTime_pay(timePay);
        this.setCost(cost);
        setBill_number(billNumber);
        this.setPayment(payment);
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
}
