package util;


import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import datamodel.BasicFee;
import datamodel.BasicSetting;
import datamodel.DayHoliday;

public class ApacheServerRequest {
    public static final String url = "http://192.168.1.200:8080/function.php/";

    public static String getUsers() {
        return HTTPGetRequest.get(url, "func=user_search");
    }

    public static String getUser(String account, String password) {
        return HTTPGetRequest.get(url, String.format("func=user_single_search&account=%s&password=%s", account, password));
    }

    public static String deleteUser(String account) {
        return HTTPGetRequest.get(url, String.format("func=user_delete&account=%s", account));
    }

    public static String updateUser(String account, String password, String name, String phone, String permission) {
        return HTTPGetRequest.get(url, String.format("func=user_update&account=%s&password=%s&name=%s&phone=%s&permission=%s", account, password, name, phone, permission));
    }

    public static String addUser(String account, String password, String name, String phone, String permission) {
        return HTTPGetRequest.get(url, String.format("func=user_add&account=%s&password=%s&name=%s&phone=%s&permission=%s", account, password, name, phone, permission));
    }

    public static String changeUserPassword(String account, String password) {
        return HTTPGetRequest.get(url, String.format("func=user_password_change&account=%s&password=%s", account, password));
    }

    public static String getLeftLot() {
        return HTTPGetRequest.get(url, "func=slot_search");
    }

    public static String getCarInsideCount() {
        return HTTPGetRequest.get(url, "func=cars_inside_count");
    }

    public static String getCarInside() {
        return HTTPGetRequest.get(url, "func=cars_inside");
    }

    public static String getCarInside(String number) {
        return HTTPGetRequest.get(url, String.format("func=cars_inside_with_number&number=%s", number));
    }

    public static String getCarInsideWithDates(String start, String end) {
        return HTTPGetRequest.get(url, "func=cars_inside");
    }

    public static String setCarInsidePay(String carNumber, String payTime, int cost, int discount, String billNumber, String payment) {
        Map<String, String> map = new HashMap<>();
        map.put("car_number", carNumber);
        map.put("time_pay", payTime);
        map.put("cost", String.valueOf(cost));
        map.put("discount", String.valueOf(discount));
        map.put("bill_number", billNumber);
        map.put("payment", payment);
        return HTTPGetRequest.post(url + "?func=cars_inside_update", map);
    }

    public static String setCarInsidePayWithServerTime(String carNumber, int cost, int discount, String billNumber, String payment) {
        Map<String, String> map = new HashMap<>();
        map.put("car_number", carNumber);
        map.put("cost", String.valueOf(cost));
        map.put("discount", String.valueOf(discount));
        map.put("bill_number", billNumber);
        map.put("payment", payment);
        return HTTPGetRequest.post(url + "?func=cars_inside_update_with_server_time", map);
    }

    public static String getCams() {
        return HTTPGetRequest.get(url, "func=cam_search");
    }

    public static String getCam(String ip) {
        return HTTPGetRequest.get(url, String.format("func=cam_single_search&ip=%s", ip));
    }

    public static String updateCam(int number, String name, int inOut, int pay, int open, String oldIp, String newIp) {
        return HTTPGetRequest.get(url, String.format("func=cam_update&number=%d&name=%s&in_out=%d&pay=%d&open=%d&old_ip=%s&new_ip=%s", number, name, inOut, pay, open, oldIp, newIp));
    }

    public static String addCam(int number, String name, String ip, int inOut, int pay, int open) {
        return HTTPGetRequest.get(url, String.format("func=cam_add&number=%d&name=%s&ip=%s&in_out=%d&pay=%d&open=%d", number, name, ip, inOut, pay, open));
    }

    public static String deleteCam(String ip) {
        return HTTPGetRequest.get(url, String.format("func=cam_delete&ip=%s", ip));
    }

    public static String getECPay() {
        return HTTPGetRequest.get(url, "func=ecpay_search");
    }

    public static String updateECPay(int printStatus, int plusCarNumber, String merchantID, String CompanyID, String key, String IV) {
        return HTTPGetRequest.get(url, String.format("func=ecpay_update&print_status=%d&plus_car_number=%d&merchant_id=%s&company_id=%s&hash_key=%s&hash_iv=%s", printStatus, plusCarNumber, merchantID, CompanyID, key, IV));
    }

    public static String getHolidays() {
        return HTTPGetRequest.get(url, "func=holiday_search");
    }

    public static String getHoliday(String date) {
        return HTTPGetRequest.get(url, String.format("func=holiday_single_search&date=%s", date));
    }

    public static String updateHoliday(int number, String oldDate, String newDate, int weekday, String description, String updateDate, String account) {
        return HTTPGetRequest.get(url, String.format("func=holiday_update&number=%d&old_date=%s&new_date=%s&weekday=%d&description=%s&update_date=%s&account=%s", number, oldDate, newDate, weekday, description, updateDate, account));
    }

    public static String addHoliday(int number, String date, int weekday, String description, String updateDate, String account) {
        return HTTPGetRequest.get(url, String.format("func=holiday_add&number=%d&date=%s&weekday=%d&description=%s&update_date=%s&account=%s", number, date, weekday, description, updateDate, account));
    }

    public static String deleteHoliday(String date) {
        return HTTPGetRequest.get(url, String.format("func=holiday_delete&date=%s", date));
    }

    public static String getHistories() {
        return HTTPGetRequest.get(url, "func=history_search");
    }

    public static String getHistoriesWithDates(String start, String end) {
        return HTTPGetRequest.get(url, String.format("func=history_date_search&start=%s&end=%s", start, end));
    }

    public static String deleteHistory(long id) {
        return HTTPGetRequest.get(url, String.format("func=history_delete&id=%d", id));
    }

    public static String getPayHistory() {
        return HTTPGetRequest.get(url, "func=pay_search");
    }

    public static String getPayHistoryWithDates(String start, String end, String carNumber, String payment) {
        return HTTPGetRequest.get(url, String.format("func=pay_dates_search&start=%s&end=%s&car_number=%s&payment=%s", start, end, carNumber, payment));
    }

    public static String addPayHistory(String carNumber, String timeIn, String timePay, int cost, String billNumber, String payment) {
        Map<String, String> map = new HashMap<>();
        map.put("car_number", carNumber);
        map.put("time_in", timeIn);
        map.put("time_pay", timePay);
        map.put("cost", String.valueOf(cost));
        map.put("bill_number", billNumber);
        map.put("payment", payment);
        return HTTPGetRequest.post(url + "?func=pay_dates_add", map);
    }

    public static String getCompanyInformation() {
        return HTTPGetRequest.get(url, "func=company_info_search");
    }

    public static String setCompanyInformation(BasicSetting setting) {
        return HTTPGetRequest.get(url,
                String.format("func=company_info_update&lot_name=%s&company_name=%s&company_address=%s&company_phone=%s&server_token=%s&cht_chat_id=%s&standby_path=%s&standby_sec=%d&auto_upload_server=%d&standby_play=%d",
                        setting.getLot_name(),
                        setting.getCompany_name(),
                        setting.getCompany_address(),
                        setting.getCompany_phone(),
                        setting.getServer_token(),
                        setting.getCht_chat_id(),
                        setting.getStandby_path(),
                        setting.getStandby_sec(),
                        setting.getAuto_upload_server(),
                        setting.getStandby_play()));
    }

    public static String getBasicFee() {
        return HTTPGetRequest.get(url, "func=fee_search");
    }

    public static String setBasicFee(BasicFee fee) {
        return HTTPGetRequest.get(url, String.format("func=fee_update&enter_time_not_count=%d&before_one_hour_count=%d&after_one_hour_unit=%d&weekday_fee=%d&weekday_most_fee=%d&holiday_fee=%d&holiday_most_fee=%d&weekday_holiday_cross=%d",
                fee.getEnter_time_not_count(),
                fee.getBefore_one_hour_count(),
                fee.getAfter_one_hour_unit(),
                fee.getWeekday_fee(),
                fee.getWeekday_most_fee(),
                fee.getHoliday_fee(),
                fee.getHoliday_most_fee(),
                fee.getWeekday_holiday_cross()
        ));
    }

    public static String getDayHoliday() {
        return HTTPGetRequest.get(url, "func=day_holiday_search");
    }

    public static String setDayHoliday(DayHoliday holiday) {
        return HTTPGetRequest.get(url, String.format("func=day_holiday_update&sunday=%d&monday=%d&tuesday=%d&wednesday=%d&thursday=%d&friday=%d&saturday=%d",
                holiday.getSunday(), holiday.getMonday(), holiday.getTuesday(), holiday.getWednesday(),
                holiday.getThursday(), holiday.getFriday(), holiday.getSaturday()));
    }

    public static String getBase64Picture(String path) {
        return HTTPGetRequest.get(url, String.format("func=get_car_image&path=%s", path));
    }

    private static String encodeValue(String value) {
        try {
            return URLEncoder.encode(value, StandardCharsets.UTF_8.toString());
        } catch (Exception e) {
            e.printStackTrace();
            return value;
        }
    }

    public static String getCouponSetting() {
        return HTTPGetRequest.get(url, "func=coupon_setting_search");
    }

    public static String setCouponSettingStop() {
        return HTTPGetRequest.get(url, "func=coupon_setting_update_stop");
    }

    public static String moneyCountSearch() {
        return HTTPGetRequest.get(url, "func=money_count_search");
    }

    public static String moneyCountUpdate(int five, int ten, int fifty, int hundred) {
        return HTTPGetRequest.get(url, String.format("func=money_count_update&five=%d&ten=%d&fifty=%d&hundred=%d", five, ten, fifty, hundred));
    }

    public static String moneyBasicSearch() {
        return HTTPGetRequest.get(url, "func=money_basic_search");
    }

    public static String moneyBasicUpdate(String five_basic, String ten_basic, String fifty_basic, String five_alert, String ten_alert, String fifty_alert) {
        return HTTPGetRequest.get(url, String.format("func=money_basic_update&five_basic=%s&ten_basic=%s&fifty_basic=%s&five_alert=%s&ten_alert=%s&fifty_alert=%s",
                five_basic, ten_basic, fifty_basic, five_alert, ten_alert, fifty_alert));
    }

    public static String moneyRefundStart(String five, String ten, String fifty) {
        return HTTPGetRequest.get(url, String.format("func=money_refund_update&five=%s&ten=%s&fifty=%s",
                five, ten, fifty));
    }

    public static String moneySupplyStart(String five, String ten, String fifty) {
        return HTTPGetRequest.get(url, String.format("func=money_supply_start&five=%s&ten=%s&fifty=%s",
                five, ten, fifty));
    }

    public static String moneySupplySearch() {
        return HTTPGetRequest.get(url, "func=money_supply_search");
    }

    public static String moneySupplyUpdate(int five, int ten, int fifty) {
        return HTTPGetRequest.get(url, String.format("func=money_supply_update&five_count=%d&ten_count=%d&fifty_count=%d", five, ten, fifty));
    }

    public static String moneySupplyStop() {
        return HTTPGetRequest.get(url, "func=money_supply_stop");
    }

    public static String moneyRefundSearch() {
        return HTTPGetRequest.get(url, "func=money_refund_search");
    }

    public static String moneyRefundStop() {
        return HTTPGetRequest.get(url, "func=money_refund_stop");
    }

    public static String getPrintSettings() {
        return HTTPGetRequest.get(url, "func=print_search");
    }

    public static String updatePrintPaperLeft(int paper_left) {
        return HTTPGetRequest.get(url, String.format("func=print_update_paper_left&pay_left=%d",
                paper_left));
    }

    public static String getRegularCar(String carNumber) {
        return HTTPGetRequest.get(url, String.format("func=regular_pass_single_search&car_number=%s",
                carNumber));
    }

    public static String getServerTime(){
        return HTTPGetRequest.get(url, "func=get_server_time");
    }

}
