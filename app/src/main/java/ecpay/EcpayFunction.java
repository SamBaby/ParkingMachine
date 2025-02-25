package ecpay;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Picture;
import android.os.Build;
import android.os.Environment;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.android.machine.R;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Document;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Vector;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import event.Var;
import invoice_print_machine.PrintCommand;
import usb.UsbConnectionContext;
import usb.UsbConnector;
import util.Util;

public class EcpayFunction {
    private static final String algorithm = "AES/CBC/PKCS7Padding";

    public static String httpPost(String url, String urlParameters, String encoding) {
        try {
            URL obj = new URL(url);
            HttpURLConnection connection = null;
            if (obj.getProtocol().toLowerCase().equals("https")) {
                trustAllHosts();
                connection = (HttpsURLConnection) obj.openConnection();
            } else {
                connection = (HttpURLConnection) obj.openConnection();
            }
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Accept-Language", encoding);
            connection.setDoOutput(true);
            DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
            wr.writeBytes(urlParameters);
            wr.flush();
            wr.close();
            BufferedReader in = connection.getResponseCode() != 200 ? new BufferedReader(new InputStreamReader(connection.getErrorStream(), encoding)) : new BufferedReader(new InputStreamReader(connection.getInputStream(), encoding));
            String inputLine;
            StringBuilder response = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            return response.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * ���� Unix TimeStamp
     *
     * @return TimeStamp
     */
    public static long genUnixTimeStamp() {
        return System.currentTimeMillis() / 1000L;
    }

    public static Document xmlParser(String uri) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setValidating(false);
            factory.setIgnoringElementContentWhitespace(true);
            DocumentBuilder builder = factory.newDocumentBuilder();
            return builder.parse(uri);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * https �B�z
     */
    private static void trustAllHosts() {

        X509TrustManager easyTrustManager = new X509TrustManager() {

            public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                // Oh, I am easy!
            }

            public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                // Oh, I am easy!
            }

            public X509Certificate[] getAcceptedIssuers() {
                return null;
            }

        };

        // Create a trust manager that does not validate certificate chains
        TrustManager[] trustAllCerts = new TrustManager[]{easyTrustManager};

        // Install the all-trusting trust manager
        try {
            SSLContext sc = SSLContext.getInstance("TLS");

            sc.init(null, trustAllCerts, new java.security.SecureRandom());

            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static String encodeValue(String value) {
        String code = null;
        try {
            code = URLEncoder.encode(value, "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return code;
    }

    public static String decode(String value) {
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

    public static String getCurrentDateTime() {
        Date currentDate = new Date();

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.TAIWAN);

        return formatter.format(currentDate);
    }

    public static Vector<DistributedNumberInfo> getDistributedNumbers(String year, String merchantID, String algorithm, String key, String IV) {
        RqHeader header = new RqHeader();
        header.setTimestamp(genUnixTimeStamp());
        DistributedNumberData data = new DistributedNumberData();
        data.setMerchantID(merchantID);
        data.setInvoiceYear(year);
        Gson gson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE).create();
        String dataString = gson.toJson(data);

        EnvoiceJson json = new EnvoiceJson();
        json.MerchantID = merchantID;
        json.RqHeader = header;
        json.Data = EcpayFunction.ECPayEncrypt(dataString, algorithm, key, IV);

        String jsonText = gson.toJson(json);
        String res = EcpayFunction.httpPost("https://einvoice-stage.ecpay.com.tw/B2CInvoice/GetGovInvoiceWordSetting", jsonText, "UTF-8");
        if (res != null) {
            try {
                JSONObject ret = new JSONObject(res);
                String reply = ret.getString("Data");
                if (reply.isEmpty()) {
                    return null;
                }
                JSONObject dataJson = new JSONObject(EcpayFunction.ECPayDecrypt(reply, algorithm, key, IV));
                if (dataJson.getInt("RtnCode") == 1) {
                    JSONArray array = dataJson.getJSONArray("InvoiceInfo");
                    Vector<DistributedNumberInfo> infoList = new Vector<>();
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject info = array.getJSONObject(i);
                        infoList.add(gson.fromJson(info.toString(), DistributedNumberInfo.class));
                    }
                    return infoList;
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static MachineNumberInfo getMachineNumbers(String merchantID, String year, int term, String machineID, String algorithm, String key, String IV) {
        RqHeader header = new RqHeader();
        header.setTimestamp(genUnixTimeStamp());
        MachineNumberData data = new MachineNumberData();
        data.setMerchantID(merchantID);
        data.setInvoiceYear(year);
        data.setInvoiceTerm(term);
        data.setInvoiceStatus(1);
        data.setMachineID(machineID);
        Gson gson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE).create();
        String dataString = gson.toJson(data);

        EnvoiceJson json = new EnvoiceJson();
        json.MerchantID = merchantID;
        json.RqHeader = header;
        json.Data = EcpayFunction.ECPayEncrypt(dataString, algorithm, key, IV);

        String jsonText = gson.toJson(json);
        String res = EcpayFunction.httpPost("https://einvoice-stage.ecpay.com.tw/B2CInvoice/GetOfflineInvoiceWordSetting", jsonText, "UTF-8");
        if (res != null) {
            try {
                JSONObject ret = new JSONObject(res);
                String reply = ret.getString("Data");
                if (reply.isEmpty()) {
                    return null;
                }
                JSONObject dataJson = new JSONObject(EcpayFunction.ECPayDecrypt(reply, algorithm, key, IV));
                if (dataJson.getInt("RtnCode") == 1) {
                    MachineNumberInfo info = new MachineNumberInfo();
                    info.setInvoiceHeader(dataJson.getString("InvoiceHeader"));
                    info.setInvoiceStart(dataJson.getString("InvoiceStart"));
                    info.setInvoiceEnd(dataJson.getString("InvoiceEnd"));
                    info.setTimes(dataJson.getInt("Times"));
                    return info;
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static String getCurrentYear() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        return String.valueOf(year - 1911);
    }

    public static int getCurrentTerm() {
        Calendar calendar = Calendar.getInstance();
        int month = calendar.get(Calendar.MONTH) + 1;
        int term = month / 2;
        if (month % 2 == 1) {
            term++;
        }
        return term;
    }

    public static MachineNumberInfo getMachineInvoiceNumberInfo(String merchantID, String machineID, String algorithm, String key, String IV) {
        RqHeader header = new RqHeader();
        header.setTimestamp(genUnixTimeStamp());
        MachineNumberData data = new MachineNumberData();

        data.setMerchantID(merchantID);
        data.setInvoiceYear(getCurrentYear());
        data.setInvoiceTerm(getCurrentTerm());
        data.setInvoiceStatus(1);
        data.setMachineID(machineID);
        Gson gson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE).create();
        String dataString = gson.toJson(data);

        EnvoiceJson json = new EnvoiceJson();
        json.MerchantID = merchantID;
        json.RqHeader = header;
        json.Data = EcpayFunction.ECPayEncrypt(dataString, algorithm, key, IV);

        String jsonText = gson.toJson(json);
        String res = EcpayFunction.httpPost("https://einvoice-stage.ecpay.com.tw/B2CInvoice/GetOfflineInvoiceWordSettingNumber", jsonText, "UTF-8");
        if (res != null) {
            try {
                JSONObject ret = new JSONObject(res);
                String reply = ret.getString("Data");
                if (reply.isEmpty()) {
                    return null;
                }
                JSONObject dataJson = new JSONObject(EcpayFunction.ECPayDecrypt(reply, algorithm, key, IV));
                if (dataJson.getInt("RtnCode") == 1) {
                    MachineNumberInfo info = new MachineNumberInfo();
                    int times = dataJson.getInt("Times");
                    JSONArray infos = new JSONArray(dataJson.getString("InvoiceInfo"));

                    if (times > infos.length()) {
                        return null;
                    } else {
                        JSONObject obj = infos.getJSONObject(times - 1);
                        info.setInvoiceNumber(obj.getString("InvoiceNo"));
                        info.setRandomNumber(obj.getString("RandomNumber"));
                        return info;
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static String getMachineInvoiceNumber(String merchantID, String machineID, String algorithm, String key, String IV) {
        RqHeader header = new RqHeader();
        header.setTimestamp(genUnixTimeStamp());
        MachineNumberData data = new MachineNumberData();

        data.setMerchantID(merchantID);
        data.setInvoiceYear(getCurrentYear());
        data.setInvoiceTerm(getCurrentTerm());
        data.setInvoiceStatus(1);
        data.setMachineID(machineID);
        Gson gson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE).create();
        String dataString = gson.toJson(data);

        EnvoiceJson json = new EnvoiceJson();
        json.MerchantID = merchantID;
        json.RqHeader = header;
        json.Data = EcpayFunction.ECPayEncrypt(dataString, algorithm, key, IV);

        String jsonText = gson.toJson(json);
        String res = EcpayFunction.httpPost("https://einvoice-stage.ecpay.com.tw/B2CInvoice/GetOfflineInvoiceWordSetting", jsonText, "UTF-8");
        if (res != null) {
            try {
                JSONObject ret = new JSONObject(res);
                String reply = ret.getString("Data");
                if (reply.isEmpty()) {
                    return null;
                }
                JSONObject dataJson = new JSONObject(EcpayFunction.ECPayDecrypt(reply, algorithm, key, IV));
                if (dataJson.getInt("RtnCode") == 1) {
                    MachineNumberInfo info = new MachineNumberInfo();
                    info.setInvoiceHeader(dataJson.getString("InvoiceHeader"));
                    info.setInvoiceStart(dataJson.getString("InvoiceStart"));
                    info.setInvoiceEnd(dataJson.getString("InvoiceEnd"));
                    info.setTimes(dataJson.getInt("Times"));
                    long start = Long.parseLong(info.getInvoiceStart());
                    long end = Long.parseLong(info.getInvoiceEnd());
                    long times = info.getTimes() - 1;
                    long current = start + times;
                    if (current > end) {
                        return null;
                    } else {
                        return info.getInvoiceHeader() + current;
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static boolean setMachineNumbers(String merchantID, String year, int term, String machineID,
                                            String invoiceHeader, String start, String end,
                                            String algorithm, String key, String IV) {
        RqHeader header = new RqHeader();
        header.setTimestamp(genUnixTimeStamp());
        NumberDistributionData data = new NumberDistributionData();
        data.setMerchantID(merchantID);
        data.setInvoiceYear(year);
        data.setInvoiceTerm(term);
        data.setMachineID(machineID);
        data.setInvoiceHeader(invoiceHeader);
        data.setInvoiceStart(start);
        data.setInvoiceEnd(end);
        data.setInvType("07");
        data.setInvoiceCategory("4");
        Gson gson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE).create();
        String dataString = gson.toJson(data);

        EnvoiceJson json = new EnvoiceJson();
        json.MerchantID = merchantID;
        json.RqHeader = header;
        json.Data = EcpayFunction.ECPayEncrypt(dataString, algorithm, key, IV);

        String jsonText = gson.toJson(json);
        String res = EcpayFunction.httpPost("https://einvoice-stage.ecpay.com.tw/B2CInvoice/AddInvoiceWordSetting", jsonText, "UTF-8");
        if (res != null) {
            try {
                JSONObject ret = new JSONObject(res);
                String reply = ret.getString("Data");
                if (reply.isEmpty()) {
                    return false;
                }
                String fff = "";
                fff.getBytes("GBK");
                JSONObject dataJson = new JSONObject(EcpayFunction.ECPayDecrypt(reply, algorithm, key, IV));
                if (dataJson.getInt("RtnCode") == 1) {
                    return true;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public static String getECPayUrl(boolean test) {
        if (test) {
            return "https://einvoice-stage.ecpay.com.tw";
        } else {
            return "https://einvoice.ecpay.com.tw";
        }
    }

    //invoice issuing function
    public static String invoiceIssue(boolean test, Activity activity, UsbConnector connector, UsbConnectionContext cxt, String merchantID, String companyID, String carrierID, int amount, String key, String IV, String enterDate) {
        Var<String> invoiceNo = new Var<>("");
        Var<String> invoiceDate = new Var<>("");
        Thread t = new Thread(() -> {
            Date currentTime = Calendar.getInstance().getTime();
            long unixTime = currentTime.getTime();
            EnvoiceData data = new EnvoiceData();
            data.setMerchantID(merchantID);
            data.setRelateNumber("PJ" + String.valueOf(unixTime));
            data.setCustomerIdentifier(companyID);
            data.setCustomerID("");
            data.setCustomerName("客戶名稱");
            data.setCustomerAddr("客戶地址");
            data.setCustomerPhone("0912345678");
            data.setCustomerEmail("test@ecpay.com.tw");
            data.setClearanceMark("");
            data.setPrint(carrierID.isEmpty() ? "1" : "0");
            data.setDonation("0");
            data.setLoveCode("");
            data.setCarrierType(carrierID.isEmpty() ? "" : "3");
            data.setCarrierNum(carrierID);
            data.setTaxType(1);
            data.setSpecialTaxType(0);
            data.setSalesAmount(amount);
            data.setInvType("07");
            data.setVat("1");
            String remark = "";
            try {
                remark = String.format("入 %s 現金", enterDate);
            } catch (Exception e) {
                e.printStackTrace();
            }
            data.setInvoiceRemark(remark);
            data.setItems(new EnvoiceItem[]{new EnvoiceItem()});
            data.getItems()[0] = new EnvoiceItem();
            data.getItems()[0].setItemSeq(1);
            data.getItems()[0].setItemName("停車費");
            data.getItems()[0].setItemCount(1);
            data.getItems()[0].setItemWord("次");
            data.getItems()[0].setItemPrice(amount);
            data.getItems()[0].setItemTaxType("");
            data.getItems()[0].setItemAmount(amount);
            data.getItems()[0].setItemRemark("");
            EnvoiceJson json = new EnvoiceJson();
            RqHeader header = new RqHeader();
            header.setTimestamp(unixTime);

            json.MerchantID = merchantID;
            Gson gson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE).create();
            json.RqHeader = header;
            String dataString = gson.toJson(data);
            json.Data = EcpayFunction.ECPayEncrypt(dataString, algorithm, key, IV);
            String jsonText = gson.toJson(json);

            try {
                String res = EcpayFunction.httpPost(getECPayUrl(test) + "/B2CInvoice/Issue", jsonText, "UTF-8");
                if (res != null) {
                    JSONObject ret = new JSONObject(res);
                    if (ret.has("Data") && !ret.getString("Data").isEmpty() && !ret.getString("Data").equals("null")) {
                        JSONObject returnData = new JSONObject(EcpayFunction.ECPayDecrypt(ret.getString("Data"), algorithm, key, IV));
                        if ((returnData.has("RtnCode") && returnData.getInt("RtnCode") == 1) || (returnData.has("RQnCode") && returnData.getInt("RQnCode") == 1)) {
                            invoiceNo.set(returnData.getString("InvoiceNo"));
                            invoiceDate.set(returnData.getString("InvoiceDate").split(" ")[0]);
                            if (carrierID.isEmpty()) {
                                boolean printResult = invoicePrint(test, activity, connector, cxt, merchantID, algorithm, key, IV, invoiceNo.get(), invoiceDate.get(), enterDate, (companyID != null && !companyID.isEmpty()));
                                if (!printResult) {
                                    invoiceNo.set(null);
                                } else {
                                    invoiceNo.set(invoiceNo.get());
                                }
                            } else {
                                invoiceNo.set(invoiceNo.get());
                            }
                        } else {
                            //失敗
                        }
                    }
                }
            } catch (Exception ee) {
                ee.printStackTrace();
            }
        });
        try {
            t.start();
            t.join();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return invoiceNo.get();
    }

    public static String invoiceIssueOffline(boolean test, Activity activity, UsbConnector connector, UsbConnectionContext cxt, String merchantID, String machineID, String companyID, String carrierID, int amount, String key, String IV, String enterDate) {
        Var<String> billNumber = new Var<>();
        Thread t = new Thread(() -> {
            Var<String> invoiceNo = new Var<>();
            Var<String> invoiceDate = new Var<>();
            int index = 0;
            while (index < 3 && (billNumber.get() == null || billNumber.get().isEmpty())) {
                MachineNumberInfo info = EcpayFunction.getMachineInvoiceNumberInfo(merchantID, machineID, algorithm, key, IV);
                if (info != null) {
                    String no = info.getInvoiceNumber();
                    String random = info.getRandomNumber();
                    Date currentTime = Calendar.getInstance().getTime();
                    long unixTime = currentTime.getTime();
                    InvoiceDataOffline data = new InvoiceDataOffline();
                    data.setMerchantID(merchantID);
                    data.setRelateNumber("ParkJohn" + unixTime);
                    data.setCustomerIdentifier(companyID);
                    data.setCustomerID("");
                    data.setCustomerName("");
                    data.setCustomerAddr("");
                    data.setCustomerPhone("");
                    data.setCustomerEmail("");
                    data.setClearanceMark("");
                    data.setPrint(carrierID.isEmpty() ? "1" : "0");
                    data.setDonation("0");
                    data.setLoveCode("");
                    data.setCarrierType(carrierID.isEmpty() ? "" : "3");
                    data.setCarrierNum(carrierID);
                    data.setTaxType("1");
                    data.setSpecialTaxType("0");
                    data.setSalesAmount(amount);
                    data.setInvType("07");
                    data.setVat("1");
                    data.setInvoiceRemark("");
                    data.setItems(new EnvoiceItem[]{new EnvoiceItem()});
                    data.getItems()[0] = new EnvoiceItem();
                    data.getItems()[0].setItemSeq(1);
                    data.getItems()[0].setItemName("停車費");
                    data.getItems()[0].setItemCount(1);
                    data.getItems()[0].setItemWord("次");
                    data.getItems()[0].setItemPrice(amount);
                    data.getItems()[0].setItemTaxType("1");
                    data.getItems()[0].setItemAmount(amount);
                    data.getItems()[0].setItemRemark("one hour");
                    data.setMachineID(machineID);
                    data.setInvoiceNo(no);
                    data.setRandomNumber(random);
                    String currentDate = EcpayFunction.getCurrentDateTime();
                    data.setInvoiceDate(currentDate);
                    EnvoiceJson json = new EnvoiceJson();
                    RqHeader header = new RqHeader();
                    header.setTimestamp(unixTime);

                    json.MerchantID = merchantID;
                    Gson gson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.IDENTITY).create();
                    json.RqHeader = header;
                    String dataString = gson.toJson(data);
                    json.Data = EcpayFunction.ECPayEncrypt(dataString, algorithm, key, IV);
                    String jsonText = gson.toJson(json);
                    try {
                        String res = EcpayFunction.httpPost(getECPayUrl(test) + "/B2CInvoice/OfflineIssue", jsonText, "UTF-8");
                        if (res != null) {
                            JSONObject ret = new JSONObject(res);
                            if (!ret.getString("Data").isEmpty() && !ret.getString("Data").equals("null")) {
                                JSONObject returnData = new JSONObject(EcpayFunction.ECPayDecrypt(ret.getString("Data"), algorithm, key, IV));
                                if (returnData.getInt("RtnCode") == 1) {
                                    invoiceNo.set(no);
                                    invoiceDate.set(currentDate.split(" ")[0]);
                                    if (carrierID.isEmpty()) {
                                        boolean printResult = invoicePrint(test, activity, connector, cxt, merchantID, algorithm, key, IV, invoiceNo.get(), invoiceDate.get(), enterDate, (companyID != null && !companyID.isEmpty()));
                                        if (!printResult) {
                                            billNumber.set(null);
                                            break;
                                        } else {
                                            billNumber.set(invoiceNo.get());
                                        }
                                    } else {
                                        billNumber.set(invoiceNo.get());
                                    }
                                } else {
                                    //失敗
                                }
                            }
                        } else {
                            //失敗
                        }
                    } catch (Exception ee) {
                        ee.printStackTrace();
                        //失敗
                    }

                }
                index++;
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        try {
            t.start();
            t.join();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return billNumber.get();
    }

    //invoice printing function
    public static boolean invoicePrint(boolean test, Activity activity, UsbConnector connector, UsbConnectionContext cxt, String merchantID, String algorithm, String key, String IV, String invoiceNumber, String date, String enterDate, boolean printDetail) {
        Var<Boolean> result = new Var<>(false);
        Var<WebView> webViewVar = new Var<>();
        if (invoiceNumber != null && date != null && !invoiceNumber.isEmpty() && !date.isEmpty()) {
            Thread t = new Thread(() -> {
                long unixTime = System.currentTimeMillis() / 1000L;
                EnvoiceJson json = new EnvoiceJson();
                RqHeader header = new RqHeader();
                header.setTimestamp(unixTime);
                InvoicePrintJson data = new InvoicePrintJson();
                data.setMerchantID(merchantID);
                data.setInvoiceNo(invoiceNumber);
                data.setInvoiceDate(date);
                data.setPrintStyle(3);
                json.MerchantID = merchantID;
                Gson gson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE).create();
                json.RqHeader = header;
                String dataString = gson.toJson(data);
                json.Data = EcpayFunction.ECPayEncrypt(dataString, algorithm, key, IV);
                String jsonText = gson.toJson(json);

                try {
                    int index = 0;
                    while (index < 5 && !result.get()) {
                        String res = EcpayFunction.httpPost(getECPayUrl(test) + "/B2CInvoice/InvoicePrint", jsonText, "UTF-8");
                        if (res != null) {
                            JSONObject ret = new JSONObject(res);
                            JSONObject returnData = new JSONObject(EcpayFunction.ECPayDecrypt(ret.getString("Data"), algorithm, key, IV));
                            if (returnData.has("InvoiceHtml")) {
                                String url = returnData.getString("InvoiceHtml");
                                if (!url.isEmpty()) {
                                    Var<Bitmap> invoicePic = new Var<>();
                                    int i = 0;
                                    Thread tPrint = new Thread(() -> {
                                        activity.runOnUiThread(() -> {
                                            webViewVar.set(new WebView(activity));

                                            webViewVar.get().setPictureListener(new WebView.PictureListener() {
                                                boolean print = true;

                                                @Override
                                                public void onNewPicture(WebView view, @Nullable Picture picture) {
                                                    try {
                                                        if (print && view.getHeight() > 0 && view.getWidth() >= 100) {
                                                            view.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                                                                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
                                                            view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
                                                            view.setDrawingCacheEnabled(true);
                                                            view.buildDrawingCache();
                                                            Bitmap bitmap = Bitmap.createBitmap(view.getMeasuredWidth(), view.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
                                                            Canvas canvas = new Canvas(bitmap);
                                                            view.draw(canvas);
                                                            invoicePic.set(bitmap);
                                                            view.setVisibility(View.GONE);
                                                            print = false;
                                                            view.destroy();
                                                            webViewVar.set(null);
                                                        } else if (print) {
                                                            view.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                                                                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
                                                            view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
                                                        }
                                                    } catch (Exception e) {
                                                        e.printStackTrace();
                                                    }

                                                }
                                            });

                                            try {
                                                // 启用 JavaScript
                                                WebSettings webSettings = webViewVar.get().getSettings();
                                                webSettings.setJavaScriptEnabled(true);
                                                // 加载 HTML 内容
                                                webViewVar.get().loadUrl(url);
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        });
                                    });
                                    while (invoicePic.get() == null && i < 30) {
                                        i++;
                                        try {
                                            if (webViewVar.get() != null) {
                                                activity.runOnUiThread(() -> {
                                                    webViewVar.get().stopLoading();
                                                    webViewVar.get().destroy();
                                                    webViewVar.set(null);
                                                });
                                                Thread.sleep(1000);
                                            }
                                        } catch (Exception e) {
                                            webViewVar.set(null);
                                            e.printStackTrace();
                                        }
                                        try {
                                            tPrint.start();
                                            Thread.sleep(3000);
                                            if (webViewVar.get() != null) {
                                                activity.runOnUiThread(() -> {
                                                    try {
                                                        if (webViewVar.get() != null) {
                                                            webViewVar.get().destroy();
                                                        }
                                                    } catch (Exception e) {
                                                        e.printStackTrace();
                                                    }
                                                });
                                                webViewVar.set(null);
                                            }
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                    if (webViewVar.get() != null) {
                                        try {
                                            if (webViewVar.get() != null) {
                                                webViewVar.get().stopLoading();
                                                webViewVar.get().destroy();
                                                webViewVar.set(null);
                                            }
                                        } catch (Exception e) {
                                            webViewVar.set(null);
                                            e.printStackTrace();
                                        }
                                    }
                                    if (invoicePic.get() != null) {
                                        invoiceMachinePrint(activity, connector, cxt, invoicePic.get(), enterDate, printDetail);
                                        result.set(true);
                                    }
                                } else {
                                    //發票網址取得失敗
                                }
                            }

                        } else {
                            //發票網址取得失敗
                        }
                        index++;
                        Thread.sleep(2000);
                    }
                } catch (Exception ee) {
                    ee.printStackTrace();
                    //發票網址取得失敗
                }
            });
            try {
                t.start();
                t.join();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            //發票網址取得失敗
        }

        return result.get();
    }

    //print title bmp
    public static void invoiceTitlePrint(Activity activity, UsbConnector connector, UsbConnectionContext cxt) {
        String base64 = Util.getEnvoiceTitleBase64();
        byte[] decodedBytes = android.util.Base64.decode(base64, 0);
        Bitmap bmp = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
        if (bmp != null) {
            int targetWidth = bmp.getWidth();
            int targetHeight = bmp.getHeight();
            activity.runOnUiThread(() -> {
                try {
                    int s = 0, index = 0;
                    byte[] sendData = printDraw(bmp);
                    byte[] temp = new byte[8 + (targetWidth / 8)];

                    for (int i = 0; i < targetHeight; i++) {
                        if (i % 240 == 0) {
                            connector.WriteBytes(cxt, PrintCommand.reset, 0);
                        }
                        index = 0;
                        temp[index++] = 0x1D;
                        temp[index++] = 0x76;
                        temp[index++] = 0x30;
                        temp[index++] = 0x00;
                        temp[index++] = (byte) (targetWidth / 8);
                        temp[index++] = 0x00;
                        temp[index++] = (byte) 0x01;
                        temp[index++] = 0x00;
                        for (int j = 0; j < (targetWidth / 8); j++) {
                            temp[index++] = sendData[s++];
                        }
                        connector.WriteBytes(cxt, PrintCommand.position40, 0);
                        connector.WriteBytes(cxt, temp, 0);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
    }

    public static void invoiceMachinePrint(Activity activity, UsbConnector connector, UsbConnectionContext cxt, Bitmap invoicePic, String time, boolean printDetail) {
        int targetWidth = 456;
        int targetHeight = invoicePic.getHeight() * 456 / invoicePic.getWidth();
        // 缩放 Bitmap
        Bitmap scaledBitmap = Bitmap.createScaledBitmap(invoicePic, targetWidth, targetHeight, false);
        if (cxt != null) {
            connector.WriteBytes(cxt, new byte[]{0x1B, 0x6A, (byte) 0xFF}, 0);
            connector.WriteBytes(cxt, new byte[]{0x1B, 0x6A, 0x40}, 0);
            connector.WriteBytes(cxt, PrintCommand.rollback60, 0);
            connector.WriteBytes(cxt, PrintCommand.rollForward05, 0);
            invoiceTitlePrint(activity, connector, cxt);
            activity.runOnUiThread(() -> {
                try {
                    int s = 0, index = 0;
                    byte[] sendData = printDraw(scaledBitmap);
                    byte[] temp = new byte[8 + (targetWidth / 8)];
                    int h = printDetail ? targetHeight : targetHeight - 180;
                    for (int i = 0; i < 600; i++) {
                        if (i % 240 == 0) {
                            connector.WriteBytes(cxt, PrintCommand.reset, 0);
                        }
                        index = 0;
                        temp[index++] = 0x1D;
                        temp[index++] = 0x76;
                        temp[index++] = 0x30;
                        temp[index++] = 0x00;
                        temp[index++] = (byte) (targetWidth / 8);
                        temp[index++] = 0x00;
                        temp[index++] = (byte) 0x01;
                        temp[index++] = 0x00;
                        for (int j = 0; j < (targetWidth / 8); j++) {
                            temp[index++] = sendData[s++];
                        }
                        connector.WriteBytes(cxt, PrintCommand.position40, 0);
                        connector.WriteBytes(cxt, temp, 0);
                    }
                    //print detail
                    connector.WriteBytes(cxt, PrintCommand.reset, 0);
                    byte[] init = new byte[]{0x1b, 0x21, 0x00, 0x1c, 0x21, 0x00, 0x1d, 0x21, 0x00, 0x1b, 0x56, 0x00, 0x1b, 0x40, 0x1c, 0x26, 0x1B, 0x17, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x01, 0x1D, 0x4C, 0x50, 0x00};
                    connector.WriteBytes(cxt, init, 0);
                    byte[] detailSize = new byte[]{0x1d, 0x21, 0x00};
                    byte[] deadline = String.format("入 %s", time).getBytes("Big5");
                    connector.WriteBytes(cxt, PrintCommand.position80, 0);
                    connector.WriteBytes(cxt, detailSize, 0);
                    connector.WriteBytes(cxt, deadline, 0);

                    connector.WriteBytes(cxt, PrintCommand.blankA0, 0);
                    connector.WriteBytes(cxt, PrintCommand.cut, 0);
                    connector.WriteBytes(cxt, new byte[]{0x1B, 0x4A, (byte) 0xFF}, 0);
                    connector.WriteBytes(cxt, new byte[]{0x1B, 0x4A, 0x40}, 0);
//                    connector.WriteBytes(cxt, PrintCommand.rollback30, 0);
                    connector.WriteBytes(cxt, PrintCommand.reset, 0);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
            if (printDetail) {
                Util.setPrintSettingPaperMinus(1, 0);
            } else {
                Util.setPrintSettingPaperMinus(1, 0);
            }
        } else {
            //列印失敗
        }
    }

    public static byte[] printDraw(Bitmap nbm) {
        if (nbm.getHeight() == 0) {
            return null;
        } else {
            byte[] imgbuf = new byte[nbm.getWidth() / 8 * nbm.getHeight()];
            int s = 0;
            byte[] bitbuf = new byte[nbm.getWidth() / 8];
            try {
                for (int i = 0; i < nbm.getHeight(); ++i) {
                    int k;
                    for (k = 0; k < nbm.getWidth() / 8; ++k) {
                        int c0 = nbm.getPixel(k * 8 + 0, i);
                        byte p0;
                        if (c0 == -1) {
                            p0 = 0;
                        } else {
                            p0 = 1;
                        }

                        int c1 = nbm.getPixel(k * 8 + 1, i);
                        byte p1;
                        if (c1 == -1) {
                            p1 = 0;
                        } else {
                            p1 = 1;
                        }

                        int c2 = nbm.getPixel(k * 8 + 2, i);
                        byte p2;
                        if (c2 == -1) {
                            p2 = 0;
                        } else {
                            p2 = 1;
                        }

                        int c3 = nbm.getPixel(k * 8 + 3, i);
                        byte p3;
                        if (c3 == -1) {
                            p3 = 0;
                        } else {
                            p3 = 1;
                        }

                        int c4 = nbm.getPixel(k * 8 + 4, i);
                        byte p4;
                        if (c4 == -1) {
                            p4 = 0;
                        } else {
                            p4 = 1;
                        }

                        int c5 = nbm.getPixel(k * 8 + 5, i);
                        byte p5;
                        if (c5 == -1) {
                            p5 = 0;
                        } else {
                            p5 = 1;
                        }

                        int c6 = nbm.getPixel(k * 8 + 6, i);
                        byte p6;
                        if (c6 == -1) {
                            p6 = 0;
                        } else {
                            p6 = 1;
                        }

                        int c7 = nbm.getPixel(k * 8 + 7, i);
                        byte p7;
                        if (c7 == -1) {
                            p7 = 0;
                        } else {
                            p7 = 1;
                        }

                        int value = p0 * 128 + p1 * 64 + p2 * 32 + p3 * 16 + p4 * 8 + p5 * 4 + p6 * 2 + p7;
                        bitbuf[k] = (byte) value;
                    }
                    for (k = 0; k < nbm.getWidth() / 8; ++k) {
                        imgbuf[s] = bitbuf[k];
                        ++s;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return imgbuf;
        }
    }

    //check company ID
    public static boolean taxIDCheck(boolean test, String merchantID, String key, String IV, String taxID) {
        Var<Boolean> ret = new Var<>(false);
        Thread t = new Thread(() -> {
            long unixTime = System.currentTimeMillis() / 1000L;
            EnvoiceJson json = new EnvoiceJson();
            RqHeader header = new RqHeader();
            header.setTimestamp(unixTime);
            TaxIDCheckJson data = new TaxIDCheckJson();
            data.setMerchantID(merchantID);
            data.setUnifiedBusinessNo(taxID);
            json.MerchantID = merchantID;
            Gson gson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE).create();
            json.RqHeader = header;
            String dataString = gson.toJson(data);
            json.Data = EcpayFunction.ECPayEncrypt(dataString, algorithm, key, IV);
            String jsonText = gson.toJson(json);

            try {
                String res = EcpayFunction.httpPost(getECPayUrl(test) + "/B2CInvoice/GetCompanyNameByTaxID", jsonText, "UTF-8");
                if (res != null) {
                    JSONObject resJson = new JSONObject(res);
                    JSONObject returnData = new JSONObject(EcpayFunction.ECPayDecrypt(resJson.getString("Data"), algorithm, key, IV));
                    if (returnData.getInt("RtnCode") == 1) {
                        ret.set(true);
                    }
                }
            } catch (Exception ee) {
                ee.printStackTrace();
            }
        });
        try {
            t.start();
            t.join();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret.get();
    }

    //check carrier ID
    public static boolean barcodeCheck(boolean test, String merchantID, String key, String IV, String barcode) {
        Var<Boolean> ret = new Var<>(false);
        Thread t = new Thread(() -> {
            long unixTime = System.currentTimeMillis() / 1000L;
            EnvoiceJson json = new EnvoiceJson();
            RqHeader header = new RqHeader();
            header.setTimestamp(unixTime);
            BarcodeCheckJson data = new BarcodeCheckJson();
            data.setMerchantID(merchantID);
            data.setBarCode(barcode);
            json.MerchantID = merchantID;
            Gson gson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE).create();
            json.RqHeader = header;
            String dataString = gson.toJson(data);
            json.Data = EcpayFunction.ECPayEncrypt(dataString, algorithm, key, IV);
            String jsonText = gson.toJson(json);

            try {
                String res = EcpayFunction.httpPost(getECPayUrl(test) + "/B2CInvoice/CheckBarcode", jsonText, "UTF-8");
                if (res != null) {
                    JSONObject resObj = new JSONObject(res);
                    JSONObject returnData = new JSONObject(EcpayFunction.ECPayDecrypt(resObj.getString("Data"), algorithm, key, IV));
                    if (returnData.getInt("RtnCode") == 1 && "Y".equals(returnData.getString("IsExist"))) {
                        ret.set(true);
                    }
                }
            } catch (Exception ee) {
                ee.printStackTrace();
            }
        });
        try {
            t.start();
            t.join();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret.get();
    }
}
