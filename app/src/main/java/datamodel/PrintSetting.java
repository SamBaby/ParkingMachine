package datamodel;

public class PrintSetting {
    private int new_roll;
    private int warning;
    private int print_invoice;
    private int print_revenue;
    private int print_coupon;
    private int pay_left;
    private int exit_left;

    public int getNew_roll() {
        return new_roll;
    }

    public void setNew_roll(int new_roll) {
        this.new_roll = new_roll;
    }

    public int getWarning() {
        return warning;
    }

    public void setWarning(int warning) {
        this.warning = warning;
    }

    public int getPrint_invoice() {
        return print_invoice;
    }

    public void setPrint_invoice(int print_invoice) {
        this.print_invoice = print_invoice;
    }

    public int getPrint_revenue() {
        return print_revenue;
    }

    public void setPrint_revenue(int print_revenue) {
        this.print_revenue = print_revenue;
    }

    public int getPrint_coupon() {
        return print_coupon;
    }

    public void setPrint_coupon(int print_coupon) {
        this.print_coupon = print_coupon;
    }

    public int getPay_left() {
        return pay_left;
    }

    public void setPay_left(int pay_left) {
        this.pay_left = pay_left;
    }

    public int getExit_left() {
        return exit_left;
    }

    public void setExit_left(int exit_left) {
        this.exit_left = exit_left;
    }
}
