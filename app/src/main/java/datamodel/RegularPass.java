package datamodel;

public class RegularPass {
    private int id;
    private String car_number;
    private String customer_name;
    private String start_date;
    private String due_date;
    private int cost;
    private String phone_number;


    public RegularPass(int id, String carNumber, String customerName, String startDate, String endDate, int cost, String phoneNumber) {
        this.id = id;
        car_number = carNumber;
        customer_name = customerName;
        start_date = startDate;
        due_date = endDate;
        this.cost = cost;
        phone_number = phoneNumber;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCar_number() {
        return car_number;
    }

    public void setCar_number(String car_number) {
        this.car_number = car_number;
    }

    public String getCustomer_name() {
        return customer_name;
    }

    public void setCustomer_name(String customer_name) {
        this.customer_name = customer_name;
    }

    public String getStart_date() {
        return start_date;
    }

    public void setStart_date(String start_date) {
        this.start_date = start_date;
    }

    public String getDue_date() {
        return due_date;
    }

    public void setDue_date(String due_date) {
        this.due_date = due_date;
    }

    public int getCost() {
        return cost;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }
}
