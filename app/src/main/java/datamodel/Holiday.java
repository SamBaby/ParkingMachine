package datamodel;

public class Holiday {
    private int number;
    private String date;
    private int weekday;
    private String updateDate;
    private String account;
    private String description;

    public Holiday(int number, String date, int weekday, String updateDate, String account, String description) {
        this.setNumber(number);
        this.setDate(date);
        this.setWeekday(weekday);
        this.setUpdateDate(updateDate);
        this.setAccount(account);
        this.setDescription(description);
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getWeekday() {
        return weekday;
    }

    public void setWeekday(int weekday) {
        this.weekday = weekday;
    }

    public String getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(String updateDate) {
        this.updateDate = updateDate;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
