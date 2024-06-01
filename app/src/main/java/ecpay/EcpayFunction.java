package ecpay;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Picture;
import android.os.Build;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Document;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
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

/**
 * �@�Ψ禡���O
 *
 * @author mark.chiu
 */
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

    public static void invoiceIssueOffline(Activity activity, UsbConnector connector, UsbConnectionContext cxt, String merchantID, String machineID, String companyID, String carrierID, int amount, String key, String IV) {
        new Thread(() -> {
            Var<String> invoiceNo = new Var<>();
            Var<String> invoiceDate = new Var<>();
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
                data.setPrint((carrierID != null && !carrierID.isEmpty()) ? "1" : "0");
                data.setDonation("0");
                data.setLoveCode("");
                data.setCarrierType("");
                data.setCarrierNum(carrierID);
                data.setTaxType("1");
                data.setSpecialTaxType("0");
                data.setSalesAmount(30);
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
                    String res = EcpayFunction.httpPost("https://einvoice-stage.ecpay.com.tw/B2CInvoice/OfflineIssue", jsonText, "UTF-8");
                    if (res != null) {
                        JSONObject ret = new JSONObject(res);
                        if (!ret.getString("Data").isEmpty() && !ret.getString("Data").equals("null")) {
                            JSONObject returnData = new JSONObject(EcpayFunction.ECPayDecrypt(ret.getString("Data"), algorithm, key, IV));
                            if (returnData.getInt("RtnCode") == 1) {
                                invoiceNo.set(returnData.getString("InvoiceNo"));
                                invoiceDate.set(currentDate.split(" ")[0]);
                                invoicePrint(activity, connector, cxt, merchantID, algorithm, key, IV, invoiceNo.get(), invoiceDate.get());
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
        }).start();
    }

    public static void invoicePrint(Activity activity, UsbConnector connector, UsbConnectionContext cxt, String merchantID, String algorithm, String key, String IV, String invoiceNumber, String date) {
        if (invoiceNumber != null && date != null && !invoiceNumber.isEmpty() && !date.isEmpty()) {
            new Thread(() -> {
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
                System.out.println(jsonText);

                try {
                    String res = EcpayFunction.httpPost("https://einvoice-stage.ecpay.com.tw/B2CInvoice/InvoicePrint", jsonText, "UTF-8");
                    if (res != null) {
                        JSONObject ret = new JSONObject(res);
                        JSONObject returnData = new JSONObject(EcpayFunction.ECPayDecrypt(ret.getString("Data"), algorithm, key, IV));
                        String url = returnData.getString("InvoiceHtml");
                        if (!url.isEmpty()) {
                            activity.runOnUiThread(() -> {
                                WebView view = new WebView(activity);
                                view.setPictureListener(new WebView.PictureListener() {
                                    boolean print = true;

                                    @Override
                                    public void onNewPicture(WebView view, @Nullable Picture picture) {
                                        if (print && view.getHeight() > 0 && view.getWidth() >= 100) {
                                            view.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                                                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
                                            view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
                                            view.setDrawingCacheEnabled(true);
                                            view.buildDrawingCache();
                                            Bitmap bitmap = Bitmap.createBitmap(view.getMeasuredWidth(), view.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
                                            Canvas canvas = new Canvas(bitmap);
                                            view.draw(canvas);
                                            invoiceMachinePrint(activity, connector, cxt, bitmap);
                                            view.setVisibility(View.GONE);
                                            print = false;
                                            view.destroy();
                                        } else if (print) {
                                            view.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                                                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
                                            view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
                                        }

                                    }
                                });
                                // 启用 JavaScript
                                WebSettings webSettings = view.getSettings();
                                webSettings.setJavaScriptEnabled(true);

                                // 加载 HTML 内容
                                view.loadUrl(url);
                            });
                        } else {
                            //發票網址取得失敗
                        }

                    } else {
                        //發票網址取得失敗
                    }
                } catch (Exception ee) {
                    ee.printStackTrace();
                    //發票網址取得失敗
                }
            }).start();
        } else {
            //發票網址取得失敗
        }
    }

    public static void invoiceMachinePrint(Activity activity, UsbConnector connector, UsbConnectionContext cxt, Bitmap invoicePic) {
        int targetWidth = 456;
        int targetHeight = 720;

        // 缩放 Bitmap
        Bitmap scaledBitmap = Bitmap.createScaledBitmap(invoicePic, targetWidth, targetHeight, false);
        if (cxt != null) {
            try {
                activity.runOnUiThread(() -> {
                    int s = 0, index = 0;
                    byte[] sendData = printDraw(scaledBitmap);
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
                    connector.WriteBytes(cxt, PrintCommand.blank50, 0);
                    connector.WriteBytes(cxt, PrintCommand.cut, 0);
                    connector.WriteBytes(cxt, PrintCommand.rollback60, 0);
                    connector.WriteBytes(cxt, PrintCommand.rollForward05, 0);
                    connector.WriteBytes(cxt, PrintCommand.reset, 0);
                });

            } catch (Exception e) {
                e.printStackTrace();
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

    public static boolean taxIDCheck(String merchantID, String key, String IV, String taxID) {
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
            System.out.println(jsonText);

            try {
                String res = EcpayFunction.httpPost("https://einvoice-stage.ecpay.com.tw/B2CInvoice/GetCompanyNameByTaxID", jsonText, "UTF-8");
                if (res != null) {
                    JSONObject resJson = new JSONObject(res);
                    JSONObject returnData = new JSONObject(EcpayFunction.ECPayDecrypt(resJson.getString("Data"), algorithm, key, IV));
                    if(returnData.getInt("RtnCode") == 1){
                        ret.set(true);
                    }
                }
            } catch (Exception ee) {
                ee.printStackTrace();
            }
        });
        try{
            t.start();
            t.join();
        }catch (Exception e){
            e.printStackTrace();
        }
        return ret.get();
    }

    public static boolean barcodeCheck(String merchantID, String key, String IV, String barcode) {
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
            System.out.println(jsonText);

            try {
                String res = EcpayFunction.httpPost("https://einvoice-stage.ecpay.com.tw/B2CInvoice/CheckBarcode", jsonText, "UTF-8");
                if (res != null) {
                    JSONObject resObj = new JSONObject(res);
                    JSONObject returnData = new JSONObject(EcpayFunction.ECPayDecrypt(resObj.getString("Data"), algorithm, key, IV));
                    if(returnData.getInt("RtnCode") == 1 && "Y".equals(returnData.getString("IsExist"))){
                        ret.set(true);
                    }
                }
            } catch (Exception ee) {
                ee.printStackTrace();
            }
        });
        try{
            t.start();
            t.join();
        }catch (Exception e){
            e.printStackTrace();
        }
        return ret.get();
    }
}
