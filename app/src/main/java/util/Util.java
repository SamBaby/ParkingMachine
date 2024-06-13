package util;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Build;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
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
                        data.set(new ECPayData(obj.getInt("print_status"), obj.getInt("plus_car_number"), obj.getString("merchant_id"), obj.getString("company_id"), obj.getString("hash_key"), obj.getString("hash_iv"), obj.getString("machine_id")));
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
                if(mode ==0){//print invoice
                    paper = paper * setting.getPrint_invoice();
                }else{//print coupon
                    paper = paper * setting.getPrint_coupon();
                }
                ApacheServerRequest.updatePrintPaperLeft(paper - count);
            });
            t.start();
        }
    }
}
