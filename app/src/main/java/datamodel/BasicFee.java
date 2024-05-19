package datamodel;

public class BasicFee {
    private int enter_time_not_count;
    private int before_one_hour_count;
    private int after_one_hour_unit;
    private int weekday_fee;
    private int weekday_most_fee;
    private int holiday_fee;
    private int holiday_most_fee;
    private int weekday_holiday_cross;

    public BasicFee(int enterTimeNotCount, int beforeOneHourCount, int afterOneHourUnit, int weekdayFee, int weekdayMostFee, int holidayFee, int holidayMostFee, int weekdayHolidayCross) {
        setEnter_time_not_count(enterTimeNotCount);
        setBefore_one_hour_count(beforeOneHourCount);
        setAfter_one_hour_unit(afterOneHourUnit);
        setWeekday_fee(weekdayFee);
        setWeekday_most_fee(weekdayMostFee);
        setHoliday_fee(holidayFee);
        setHoliday_most_fee(holidayMostFee);
        setWeekday_holiday_cross(weekdayHolidayCross);
    }

    public int getEnter_time_not_count() {
        return enter_time_not_count;
    }

    public void setEnter_time_not_count(int enter_time_not_count) {
        this.enter_time_not_count = enter_time_not_count;
    }

    public int getBefore_one_hour_count() {
        return before_one_hour_count;
    }

    public void setBefore_one_hour_count(int before_one_hour_count) {
        this.before_one_hour_count = before_one_hour_count;
    }

    public int getAfter_one_hour_unit() {
        return after_one_hour_unit;
    }

    public void setAfter_one_hour_unit(int after_one_hour_unit) {
        this.after_one_hour_unit = after_one_hour_unit;
    }

    public int getWeekday_fee() {
        return weekday_fee;
    }

    public void setWeekday_fee(int weekday_fee) {
        this.weekday_fee = weekday_fee;
    }

    public int getWeekday_most_fee() {
        return weekday_most_fee;
    }

    public void setWeekday_most_fee(int weekday_most_fee) {
        this.weekday_most_fee = weekday_most_fee;
    }

    public int getHoliday_fee() {
        return holiday_fee;
    }

    public void setHoliday_fee(int holiday_fee) {
        this.holiday_fee = holiday_fee;
    }

    public int getHoliday_most_fee() {
        return holiday_most_fee;
    }

    public void setHoliday_most_fee(int holiday_most_fee) {
        this.holiday_most_fee = holiday_most_fee;
    }

    public int getWeekday_holiday_cross() {
        return weekday_holiday_cross;
    }

    public void setWeekday_holiday_cross(int weekday_holiday_cross) {
        this.weekday_holiday_cross = weekday_holiday_cross;
    }
}
