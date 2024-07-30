package util;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Build;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;

import com.android.machine.R;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import datamodel.CouponSetting;
import datamodel.ECPayData;
import datamodel.PrintSetting;
import event.Var;
import invoice_print_machine.PrintCommand;
import usb.UsbConnectionContext;
import usb.UsbConnector;

public class Util {
    private static String algorithm = "AES/CBC/PKCS7Padding";

    public String ECPayEncrypt(String data, String key, String IV) {
        String URLEncode = encodeValue(data);
        return encrypt(algorithm, URLEncode, new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "AES"), new IvParameterSpec(IV.getBytes(StandardCharsets.UTF_8)));
    }

    public String ECPayDecrypt(String data, String key, String IV) {
        String aesDecrypt = decrypt(algorithm, data, new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "AES"), new IvParameterSpec(IV.getBytes(StandardCharsets.UTF_8)));
        return decode(aesDecrypt);
    }

    private static String encodeValue(String value) {
        String code = null;
        try {
            code = URLEncoder.encode(value, "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return code;
    }

    private static String decode(String value) {
        String code = null;
        try {
            code = URLDecoder.decode(value, "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return code;
    }

    public static String encrypt(String algorithm, String input, SecretKeySpec key, IvParameterSpec iv) {

        try {
            Cipher cipher = Cipher.getInstance(algorithm);
            cipher.init(Cipher.ENCRYPT_MODE, key, iv);
            byte[] cipherText = cipher.doFinal(input.getBytes());
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                return Base64.getEncoder().encodeToString(cipherText);
            } else {
                return android.util.Base64.encodeToString(cipherText, 0);
            }
        } catch (Exception e) {
            return null;
        }
    }


    public static String decrypt(String algorithm, String cipherText, SecretKeySpec key, IvParameterSpec iv) {

        try {
            Cipher cipher = Cipher.getInstance(algorithm);
            cipher.init(Cipher.DECRYPT_MODE, key, iv);
            byte[] plainText = new byte[0];
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                plainText = cipher.doFinal(Base64.getDecoder().decode(cipherText));
            } else {
                plainText = cipher.doFinal(android.util.Base64.decode(cipherText, 0));
            }
            return new String(plainText);
        } catch (Exception e) {
            return null;
        }
    }

    public static String ECPayEncrypt(String data, String algorithm, String key, String IV) {
        String URLEncode = encodeValue(data);
        return encrypt(algorithm, URLEncode, new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "AES"), new IvParameterSpec(IV.getBytes(StandardCharsets.UTF_8)));
    }

    public static String ECPayDecrypt(String data, String algorithm, String key, String IV) {
        String aesDecrypt = decrypt(algorithm, data, new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "AES"), new IvParameterSpec(IV.getBytes(StandardCharsets.UTF_8)));
        return decode(aesDecrypt);
    }


    public static long getStartOfToday() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DATE);
        calendar.setTimeInMillis(0);
        calendar.set(year, month, day, 0, 0, 0);
        return calendar.getTimeInMillis();
    }

    public static long getEndOfToday() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DATE);
        calendar.setTimeInMillis(0);
        calendar.set(year, month, day, 23, 59, 59);
        return calendar.getTimeInMillis();
    }

    public static long getStartOfWeek() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DATE);
        calendar.setTimeInMillis(0);
        calendar.set(year, month, day, 0, 0, 0);
        return calendar.getTimeInMillis();
    }

    public static long getEndOfWeek() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DATE);
        calendar.setTimeInMillis(0);
        calendar.set(year, month, day, 23, 59, 59);
        calendar.set(Calendar.DAY_OF_WEEK, calendar.getFirstDayOfWeek());
        return calendar.getTimeInMillis();
    }

    public static long getStartOfMonth() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DATE);
        calendar.setTimeInMillis(0);
        calendar.set(year, month, day, 0, 0, 0);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        return calendar.getTimeInMillis();
    }

    public static long getEndOfMonth() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DATE);
        calendar.setTimeInMillis(0);
        calendar.set(year, month, day, 23, 59, 59);
        calendar.set(Calendar.DAY_OF_MONTH, -1);
        return calendar.getTimeInMillis();
    }

    public static boolean isAllowedOut(long time) {
        Date date = new Date();
        return (date.getTime() - time) <= 900;
    }

    public static byte[] getBase64Decode(String base64) {
        return android.util.Base64.decode(base64, 0);
    }

    public static String getBase64Encode(byte[] text) {
        return android.util.Base64.encodeToString(text, 0);
    }

    public static String byteArrayToHexStr(byte[] byteArray) {
        if (byteArray == null) {
            return null;
        }
        StringBuilder hex = new StringBuilder(byteArray.length * 2);
        for (byte aData : byteArray) {
            hex.append(String.format("%02X ", aData));
        }
        String gethex = hex.toString();
        return gethex;
    }

    public static byte[] hexToByte(String str) {
        byte[] bytes = new byte[str.length() / 2];
        for (int i = 0; i < bytes.length; i++) {
            bytes[i] = (byte) Integer.parseInt(str.substring(2 * i, 2 * i + 2), 16);
        }
        return bytes;
    }

    public static String encrypt64(String value) throws GeneralSecurityException {
        String key = UUID.randomUUID().toString().replaceAll("-", "");
        SecretKeySpec sks = new SecretKeySpec(hexStringToByteArray(key), "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, sks, cipher.getParameters());
        byte[] encrypted = cipher.doFinal(value.getBytes());
        return byteArrayToHexString(encrypted);
    }

    public static String decrypt64(String message) throws GeneralSecurityException {
        String key = UUID.randomUUID().toString().replaceAll("-", "");
        SecretKeySpec sks = new SecretKeySpec(hexStringToByteArray(key), "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, sks);
        byte[] decrypted = cipher.doFinal(hexStringToByteArray(message));
        return new String(decrypted);
    }

    private static String byteArrayToHexString(byte[] b) {
        StringBuffer sb = new StringBuffer(b.length * 2);
        for (int i = 0; i < b.length; i++) {
            int v = b[i] & 0xff;
            if (v < 16) {
                sb.append('0');
            }
            sb.append(Integer.toHexString(v));
        }
        return sb.toString().toUpperCase();
    }

    private static byte[] hexStringToByteArray(String s) {
        byte[] b = new byte[s.length() / 2];
        for (int i = 0; i < b.length; i++) {
            int index = i * 2;
            int v = Integer.parseInt(s.substring(index, index + 2), 16);
            b[i] = (byte) v;
        }
        return b;
    }

    public static ECPayData getECPayData() {
        Var<ECPayData> data = new Var<>();
        Thread t = new Thread(() -> {
            try {
                String json = ApacheServerRequest.getECPay();
                JSONArray array = new JSONArray(json);
                if (array.length() > 0) {
                    for (int i = 0; i < 1; i++) {
                        JSONObject obj = array.getJSONObject(i);
                        data.set(new ECPayData(obj.getInt("print_status"), obj.getInt("plus_car_number"), obj.getString("merchant_id"),
                                obj.getString("company_id"), obj.getString("hash_key"), obj.getString("hash_iv"), obj.getString("machine_id"), obj.getInt("test")));
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        try {
            t.start();
            t.join();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return data.get();
    }

    public static boolean areSameDay(Date date1, Date date2) {
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal1.setTime(date1);
        cal2.setTime(date2);
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) && cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH) && cal1.get(Calendar.DAY_OF_MONTH) == cal2.get(Calendar.DAY_OF_MONTH);
    }

    public static long daysBetween(Date startDate, Date endDate) {
        // Reset the time part of the start and end date to midnight
        Calendar startCal = Calendar.getInstance();
        startCal.setTime(startDate);
        startCal.set(Calendar.HOUR_OF_DAY, 0);
        startCal.set(Calendar.MINUTE, 0);
        startCal.set(Calendar.SECOND, 0);
        startCal.set(Calendar.MILLISECOND, 0);

        Calendar endCal = Calendar.getInstance();
        endCal.setTime(endDate);
        endCal.set(Calendar.HOUR_OF_DAY, 0);
        endCal.set(Calendar.MINUTE, 0);
        endCal.set(Calendar.SECOND, 0);
        endCal.set(Calendar.MILLISECOND, 0);

        long diffInMillis = endCal.getTimeInMillis() - startCal.getTimeInMillis();
        return TimeUnit.DAYS.convert(diffInMillis, TimeUnit.MILLISECONDS);
    }

    public static void couponPrint(UsbConnector connector, UsbConnectionContext cxt, CouponSetting setting, String text, String slotName) throws UnsupportedEncodingException {
        if (cxt != null) {
            byte[] size = new byte[]{0x1D, 0x01, 0x03, 0x0A};
            byte[] faultLevel = new byte[]{0x1D, 0x01, 0x04, 0x32};
            byte[] length = new byte[]{0x1D, 0x01, 0x01, (byte) text.length(), 0x00};
            byte[] print = new byte[]{0x1D, 0x01, 0x02};
            byte[] line = new byte[]{0x0A};
            byte[] content = new byte[text.length()];
            for (int i = 0; i < text.length(); i++) {
                content[i] = (byte) text.charAt(i);
            }
            byte[] init = new byte[]{0x1b, 0x21, 0x00, 0x1c, 0x21, 0x00, 0x1d, 0x21, 0x00, 0x1b, 0x56, 0x00, 0x1b, 0x40, 0x1c, 0x26, 0x1B, 0x17, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x01, 0x1D, 0x4C, 0x50, 0x00};
            connector.WriteBytes(cxt, init, 0);
//            connector.WriteBytes(cxt, PrintCommand.reset, 0);
            //print title
            byte[] titleSize = new byte[]{0x1d, 0x21, 0x11};
            byte[] title = (slotName + "優惠券").getBytes("Big5");
            connector.WriteBytes(cxt, titleSize, 0);
            connector.WriteBytes(cxt, title, 0);
            connector.WriteBytes(cxt, line, 0);
            //print QR code
            connector.WriteBytes(cxt, size, 0);
            connector.WriteBytes(cxt, faultLevel, 0);
            connector.WriteBytes(cxt, length, 0);
            connector.WriteBytes(cxt, content, 0);
            connector.WriteBytes(cxt, PrintCommand.position50, 0);
            connector.WriteBytes(cxt, print, 0);
            connector.WriteBytes(cxt, line, 0);
            //print deadline
            byte[] deadlineSize = new byte[]{0x1d, 0x21, 0x00};
            byte[] deadline = String.format("折抵%s\n使用期限:%s\n", getAmountString(setting), setting.getDeadline()).getBytes("Big5");
            connector.WriteBytes(cxt, deadlineSize, 0);
            connector.WriteBytes(cxt, deadline, 0);
            connector.WriteBytes(cxt, line, 0);
            //print description
            byte[] descriptionSize = new byte[]{0x1d, 0x21, 0x00};
            byte[] description = ("使用說明:\n" + "1.折抵券只可使用一次\n" + "2.離場前請至自動繳費機折抵\n" + "3.繳完費請於15分鐘內離場\n").getBytes("Big5");
//                    " (時間帶入B1設定值)\n" +
//                    "\n" +
//                    "2024/XX/XX  00:00\t(列印時間)\n" +
//                    "人員:xxxxxxx\t編號:000000\t序號:000000").getBytes("Big5");
            connector.WriteBytes(cxt, descriptionSize, 0);
            connector.WriteBytes(cxt, description, 0);

            connector.WriteBytes(cxt, PrintCommand.blankA0, 0);
            connector.WriteBytes(cxt, PrintCommand.cut, 0);
            connector.WriteBytes(cxt, PrintCommand.rollback60, 0);
            connector.WriteBytes(cxt, PrintCommand.rollForward05, 0);
            connector.WriteBytes(cxt, PrintCommand.reset, 0);
        }
    }

    private static String getAmountString(CouponSetting setting) {
        String ret = String.valueOf(setting.getAmount());
        if (setting.getTime_fee() == 0) {
            ret += "小時";
        } else {
            ret += "元";
        }
        return ret;
    }

    public static void qrPrint(UsbConnector connector, UsbConnectionContext cxt, String text) {
        if (cxt != null) {
            byte[] size = new byte[]{0x1D, 0x01, 0x03, 0x0A};
            byte[] faultLevel = new byte[]{0x1D, 0x01, 0x04, 0x32};
            byte[] length = new byte[]{0x1D, 0x01, 0x01, (byte) text.length(), 0x00};
            byte[] print = new byte[]{0x1D, 0x01, 0x02};
            byte[] line = new byte[]{0x0A};
            byte[] content = new byte[text.length()];
            for (int i = 0; i < text.length(); i++) {
                content[i] = (byte) text.charAt(i);
            }
            connector.WriteBytes(cxt, PrintCommand.rollForward05, 0);
            connector.WriteBytes(cxt, PrintCommand.reset, 0);

            connector.WriteBytes(cxt, size, 0);
            connector.WriteBytes(cxt, faultLevel, 0);
            connector.WriteBytes(cxt, length, 0);
            connector.WriteBytes(cxt, content, 0);
            connector.WriteBytes(cxt, PrintCommand.position50, 0);
            connector.WriteBytes(cxt, print, 0);
            connector.WriteBytes(cxt, line, 0);

            connector.WriteBytes(cxt, PrintCommand.blankA0, 0);
            connector.WriteBytes(cxt, PrintCommand.cut, 0);
            connector.WriteBytes(cxt, PrintCommand.rollback60, 0);
            connector.WriteBytes(cxt, PrintCommand.reset, 0);
        }
    }

    public static PrintSetting getPrintSetting() {
        Var<PrintSetting> ret = new Var<>();
        Thread t = new Thread(() -> {
            try {
                String res = ApacheServerRequest.getPrintSettings();
                if (res != null && !res.isEmpty()) {
                    JSONObject obj = new JSONArray(res).getJSONObject(0);
                    Gson gson = new GsonBuilder().setPrettyPrinting().create();
                    ret.set(gson.fromJson(obj.toString(), PrintSetting.class));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        t.start();
        try {
            t.join();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret.get();
    }

    public static void setPrintSettingPaperMinus(int count, int mode) {
        PrintSetting setting = getPrintSetting();
        if (setting != null) {
            Thread t = new Thread(() -> {
                int paper = setting.getPay_left();
                if (mode == 0) {//print invoice
                    paper = paper * setting.getPrint_invoice();
                } else {//print coupon
                    paper = paper * setting.getPrint_coupon();
                }
                ApacheServerRequest.updatePrintPaperLeft(paper - count);
            });
            t.start();
        }
    }

    public static String getServerTime() {
        Var<String> serverTime = new Var<>();
        Thread t = new Thread(() -> {
            String time = ApacheServerRequest.getServerTime();
            serverTime.set(time);
        });
        try {
            t.start();
            t.join();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (serverTime.get() == null || serverTime.get().isEmpty()) {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.TAIWAN);
            Date nowDate = new Date();
            serverTime.set(format.format(nowDate));
        }
        return serverTime.get();
    }

    public static String getEnvoiceTitleBase64() {
        return "iVBORw0KGgoAAAANSUhEUgAAAcgAAABcCAIAAABlQ7JcAAAAA3NCSVQICAjb4U/gAAAIwUlEQVR4\n" +
                "nO2d0bKkKgxF7an7/7/c89A1lpeQECAo4FpPM30wRJRNCIqf7/d7AABAHH+edgAAYDcQVgCAYBBW\n" +
                "AIBgEFYAgGAQVgCAYBBWAIBgEFYAgGAQVgCAYBBWAIBgEFYAgGD+e9oBGMLn8/EU44VmgBEQsb4a\n" +
                "p/4ah/+I8qe29tsqeuocYVGIWN/O5/MZGrfaktRW9WlztPNHnHx77JznUlUYJoSI9RV8/8/T7vRy\n" +
                "PYXRsWSV2AH8IGJ9I9/v9yoT4XHfaXyciCen0EatBQJJcIKwvpQ2YdIOuf6uKYs/zByhd4ZjNyPd\n" +
                "0PzPOkzgvAQIKwRzQ7i6NH5lREPXBWHdBE/M2E9iWWroolpgx4by7PxRJ7wThHV52rr0bWs+RtWe\n" +
                "pIHB0NAYoYQeENaFier8UcK0ZRIg/FzIsb4BhHVVqjrYDb1xpw4/9FzIsb4BhHVJArucPyIzKk3+\n" +
                "5MlFzhDVFkNCv5MznA7MA8L6amaWg8DIrm3wmLlxYHIQ1vWoSslpNKhGYHw3G7JJ7XPpuQSdZcgP\n" +
                "LAHCugl2dx0keRuoalanjMcVAnXNb2rRtn0zCOtiVAnBbW5U5SJ/x/rfo9WKFdO1DSJYdClWW2FX\n" +
                "ENblqX3qs/ZAj6k24zfsTVXkFErWqSAQhPW9NOhaSLA2W9D3uFAGRtwwCQjrW+iUs707edSA0W8E\n" +
                "9gBhXZvbOnNWl5tfmW/ItAIsBMK6NncK0+o7rUwL7bkfCOtbGNF7t1EE51OrBNfgBGHdk6gn1eEG\n" +
                "WLzaD755tTx0P4DZIGJ9BYPEt3bxKvBZWoCZQVgXI7s6bz/iPsnLWjewaPC+qNtggLDug5TX0T22\n" +
                "x/79DzMA3AbCuh72o/4zhz+jNU62TNsmVZ3lO/dAgA1AWN/IEl26zcklTq2BmcdLkCCsSzLJ6/Z8\n" +
                "rPQ2kiu+6/ixDQjr66BPnizaFIu6/Sp4U3thRuw3CgD98IIAAEAwCOvC1IafhKsA94Cwrg1aCTAh\n" +
                "LF69heUk+PE9pWo/2QJwwuLVDhRXscKvctv3ops/z5U9dpDwhXxdHF4OEStUk3131thy2//0QjZK\n" +
                "9X9roH/b704HAH6QY90Bu8MvJweJw9/v9/eLfF110KlJB0bUAhuDsG6C1vkHzZSz0pONTGvD1TlV\n" +
                "rJjoKJ6mp8zhaC6nHXgWUgFQhyExRhq0XwvsHOtpX/6YPbBBvpND/Js3XktqK3KyjMfOnIMQHESs\n" +
                "S1PcyamoBYM8MXwYxJkWuOYHsnG03QjnIf7A8HtBqy4pk3BVW6cdz7nAgyCsS6IFLNdftL+G90bN\n" +
                "4KLdXi7KdZ5Iwy6CRhheZROeAmFdj87l6d/EPEr17LntbP3f6ZWMLrUWKy5zNbdAcWlutraFK+RY\n" +
                "F8PzyOptDwbNr6rnKNLmTDLpdtpJinnGMKfZYhmYBIR1SeweXlzCjvLBrs6/vOOvbrRYaw3bvP4m\n" +
                "N1HVnp3wxNENDsAjkApYlf5+3l/7uCdJA0mizhscljU2NBTx6dIQsS7GNXTyv01f+waqx1rbc1eG\n" +
                "WWPa3q8yVY/T+hunYdbvx/ZknnwLSBDW9ZDT0tr+3K+qWqUh/TyRDP/4cfQlSTodMJ6OsB+QkiNK\n" +
                "4oBWAKaF15/X5k5J9dRo51urdK1oWdo0FDDcgdOg/TRxdq5geK7ZcZaBSUBY92FcCLkB4RNnZuJg\n" +
                "QCpgH+jkNrQP3AZPBcD+kJGEmyFihZ2pWvgCiIIcKwBAMKQCAACCQVgBAIJBWAEAgkFYAQCCQVgB\n" +
                "AIJBWAEAgkFYAQCCQVgBAIJBWCNJvowU+Gkpw+A872vO48kIhp5d+K0Cz8IrrQWumxgZGxrJbUD9\n" +
                "21FnzT64eZK2Id6PTpeST5X4q/BcCL82hTes3PHa+HKBsXNr0U9jV1bnzopV23hnzWb90U7qne92\n" +
                "IqzxeD5zZJRp2DNUWstWKtF6+/V3rYz27Sa7liPXJg2f3st+a+DZT/L5NStQa4qXu7gtetGCfx/Y\n" +
                "EZ9+WBSENZ7rnXp2Nk0CEj31fJvkV+BqQfv+nbRQ3F2/Leirja9r+5u0r0W7sRjXQl7Qz78vuV6L\n" +
                "GUe1bRle663fVOfu4MeLZVSCsI7id5fX5s60Q4q5iOVoiFLlj3IC3mBEo1YmriOonQqwXSpOBbRp\n" +
                "gRGZjpinX4eQbPRg1Ls9CGsG+24ozmGz4ZVhJClfGzlGofXDoidajPbD03VtcdTaSiYE7hlyshI2\n" +
                "umo5smrRtFPB/ZkcLXWgDSFwIKxZaj9GdCj3fbH8Ie5LZ5Yw6du1+dksbUGxUXVxfCrWkjUuEwLj\n" +
                "dM0wmwj62W7+VEC2mAdN1u30SEOSJ/E5PHexMQhrPFIOtJ5/ZuWSjmEUPnSVCYwX7DxveBeyF17s\n" +
                "jq2NMdmSfW6qlR7i2nlSAbX+aFOlrE0tzKytSP7Xc4jm1XtAWANwRrW2BTvoyFZRNWF3umHXWFXe\n" +
                "IHG+uKRmlyxqVn9U6CGZqdjpgra08nWJrJgykutp/lquTnoi1v3WADpBWAM4Aysj2yjvufMQj8po\n" +
                "vWhchlG6Kqs+zCi7SpuuSmEH7IZLsXgyFZpWasl3O/OeGNf+m71hbOOeJm2mePO/UGoR1niqlhTk\n" +
                "IUkYaEdznpDEiTOErCrT7ICnxnHa2tyeRhSpueqXIambTj+1wk6XPHlbOcq+fCELYW3H6NjFOZE8\n" +
                "Nslg+mdVUfMv2f20f3eGP3aq1GncXrbq7NXOccXIhNQmRooHetS2OAwfpbsle096bvJr0CrX9F4I\n" +
                "wuqiNjgq3lL9SxkJI8K34uKbLJm45Cxcm3C8Z4WqiDHdNjIn0k6b+oRrlmZQC3WT89XW7t4Jwqpi\n" +
                "x2jhC0e1yIWFR+7mqHM/O6QWy9cGrXdSNSX3/OhEjt89rVE1GGgl5SGPX51HQFgzeGZAR+nuqcqC\n" +
                "1XqVvX2vKlMbfRRLGsNM57JVNnXo1CD/ZLM2g5w9UKOzBRqOPQcbLaHUbLkNeVu+Ngnw4/nRHgBg\n" +
                "M9iPFQAgGIQVACCYv1y+G6thhN01AAAAAElFTkSuQmCC";
    }

    public static void showWarningDialog(Context context, String text) {
        final View dialogView = View.inflate(context, R.layout.warning_dialog, null);
        TextView textView = dialogView.findViewById(R.id.warning_text);
        textView.setText(text);
        final AlertDialog alertDialog = new AlertDialog.Builder(context).create();
        dialogView.findViewById(R.id.confirm_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });
        alertDialog.setView(dialogView);
        alertDialog.show();
    }

    public static void showRestartDialog(Context context) {
        final View dialogView = View.inflate(context, R.layout.restart_dialog, null);
        final AlertDialog alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setView(dialogView);
        alertDialog.show();
    }
}
