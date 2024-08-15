package com.example.machine;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager.widget.ViewPager;

import com.android.machine.R;
import com.ftdi.j2xx.D2xxManager;
import com.ftdi.j2xx.FT_Device;

import org.json.JSONArray;
import org.json.JSONObject;

import event.Var;
import usb.UsbConnectionContext;
import usb.UsbConnector;
import util.ApacheServerRequest;
import util.CustomExceptionHandler;

public class MainActivity extends AppCompatActivity {
    private MainViewModel model;
    private D2xxManager coinInputManager;
    private FT_Device coinInputDevice;
    private FT_Device paperInputDevice;
    private FT_Device coin50Device;
    private FT_Device coin10Device;
    private UsbConnector printConnector;
    private UsbConnectionContext printCxt;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        // 设置自定义的UncaughtExceptionHandler
        Thread.setDefaultUncaughtExceptionHandler(new CustomExceptionHandler(this, MainActivity.class));

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        Var<Boolean> keepTry = new Var<>(true);
        while (keepTry.get()) {
            Thread t = new Thread(() -> {
                String ret = ApacheServerRequest.getUsers();
                if (ret != null && !ret.isEmpty()) {
                    keepTry.set(false);
                }
            });
            try {
                t.start();
                t.join();
                Thread.sleep(1000);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        viewPager = findViewById(R.id.view_pager);
        MachinePagerAdapter adapter = new MachinePagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);
        model = new ViewModelProvider(this).get(MainViewModel.class);
        handler = new Handler();
        countdownHandler = new Handler();

        try {
            coinInputManager = D2xxManager.getInstance(this);
            System.out.println("main try FTDI");
            handleFT4232H(coinInputManager);
            checkDeviceThread();
            model.startCheckThread();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Thread t = new Thread(() -> {
            try {
                String res = ApacheServerRequest.getCompanyInformation();
                if (res != null && !res.isEmpty()) {
                    JSONArray array = new JSONArray(res);
                    if (array.length() > 0) {
                        JSONObject obj = array.getJSONObject(0);
                        if (obj.has("lot_name")) {
                            String lotName = obj.getString("lot_name");
                            model.setLotName(lotName);
                        }
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
    }

    private void checkDeviceThread() {
        Thread t = new Thread(() -> {
            while (!detroyed) {
                try {
                    Thread.sleep(5000);
                    handleFT4232H(coinInputManager);
                    handlePrintMachine();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        t.start();
    }

    private void handlePrintMachine() {
        if (printCxt == null) {
            printConnector = new UsbConnector(this);
            printCxt = printConnector.ConnectUsb(0, 1137, 85, 0);
            model.setInvoiceConnector(printConnector);
            model.setInvoiceCxt(printCxt);
        }
    }

    private void handleFT4232H(D2xxManager manager) {
        if (model.getCoinInputDevice() == null || model.getPaperInputDevice() == null || model.getCoin10Device() == null || model.getCoin50Device() == null) {
            int devCount = 0;
            manager.setVIDPID(1027, 24593);
            devCount = manager.createDeviceInfoList(this);

            if (devCount >= 4) {
                try {
                    System.out.println("connecting FTDI");
                    if (model.getCoinInputDevice() == null) {
                        coinInputDevice = manager.openByIndex(this, 0);
                    }
                    if (model.getPaperInputDevice() == null) {
                        paperInputDevice = manager.openByIndex(this, 1);
                    }
                    if (model.getCoin10Device() == null) {
                        coin10Device = manager.openByIndex(this, 2);
                    }
                    if (model.getCoin50Device() == null) {
                        coin50Device = manager.openByIndex(this, 3);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (coinInputDevice != null && paperInputDevice != null && coin10Device != null && coin50Device != null) {
                    System.out.println("success FTDI");
                    setCoinMachineConfig(coinInputDevice);

                    setPaperMachineConfig(paperInputDevice);

                    setPaperMachineConfig(coin10Device);
                    initRefund(coin10Device);

                    setPaperMachineConfig(coin50Device);
                    initRefund(coin50Device);

                    model.setCoinInputDevice(coinInputDevice);
                    model.setPaperInputDevice(paperInputDevice);
                    model.setCoin10Device(coin10Device);
                    model.setCoin50Device(coin50Device);
                } else {
                    System.out.println("fail FTDI");
                    if (coinInputDevice == null) {
                        System.out.println("coin fail");
                    }
                    if (paperInputDevice == null) {
                        System.out.println("paper fail");
                    }
                    if (coin10Device == null) {
                        System.out.println("10 fail");
                    }
                    if (coin50Device == null) {
                        System.out.println("50 fail");
                    }
                }
            }
        }
    }

    private void setCoinMachineConfig(FT_Device ftDev) {
        ftDev.setBaudRate(9600);
        ftDev.setDataCharacteristics(D2xxManager.FT_DATA_BITS_8, D2xxManager.FT_STOP_BITS_1, D2xxManager.FT_PARITY_NONE);
        ftDev.setFlowControl(D2xxManager.FT_FLOW_NONE, (byte) 0x00, (byte) 0x00);
    }

    private void setPaperMachineConfig(FT_Device ftDev) {
        ftDev.setBaudRate(9600);
        ftDev.setDataCharacteristics(D2xxManager.FT_DATA_BITS_8, D2xxManager.FT_STOP_BITS_1, D2xxManager.FT_PARITY_EVEN);
        ftDev.setFlowControl(D2xxManager.FT_FLOW_NONE, (byte) 0x00, (byte) 0x00);
    }

    private void initRefund(FT_Device ftDev) {
        ftDev.write(new byte[]{(byte) 0x80});
    }

    @Override
    public void onResume() {
        super.onResume();

        Intent intent = getIntent();
        String action = intent.getAction();

        String hotplug = "android.intent.action.MAIN";
        if (hotplug.equals(action)) {
            try {
                System.out.println(hotplug);
//                handleFT4232H(coinInputManager);
                handlePrintMachine();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private boolean detroyed = false;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        detroyed = true;
        System.out.println("main activity destroy");
        if (coinInputDevice != null) {
            coinInputDevice.close();
        }
        if (paperInputDevice != null) {
            paperInputDevice.close();
        }
        if (coin10Device != null) {
            coin10Device.close();
        }
        if (coin50Device != null) {
            coin50Device.close();
        }
        if (printConnector != null && printCxt != null) {
            System.out.println("disconnect print machine");
            printConnector.Disconnect(1027, 24593);
        }
    }

    private Handler handler;
    private int currentPage = 0;
    private int numPages;
    private int countdownSeconds = 50;
    private Handler countdownHandler;
    private Runnable countdownRunnable;

    // 启动倒计时
    public void startCountdown(int seconds, int nextNumber) {
        countdownSeconds = seconds; // 重置倒计时时间
        countdownRunnable = new countdownRunnable(nextNumber);
        countdownHandler.postDelayed(countdownRunnable, 1000);
    }

    // 重置倒计时
    public void resetCountdown(int seconds, int nextNumber) {
        if (countdownRunnable != null) {
            countdownHandler.removeCallbacks(countdownRunnable);
        }
        startCountdown(seconds, nextNumber);
    }

    public void cancelCountdown() {
        if (countdownRunnable != null) {
            countdownHandler.removeCallbacks(countdownRunnable);
            countdownRunnable = null;
        }
    }

    // 按钮点击事件调用此方法
    public void goToPage(int number, int nextNumber, int seconds) {
        cancelCountdown();
        currentPage = viewPager.getCurrentItem();
        handler.post(new pageSwitcher(number, nextNumber, seconds));
    }

    public class pageSwitcher implements Runnable {
        private final int number;
        private final int nextNumber;
        private final int seconds;

        public pageSwitcher(int number, int nextNumber, int seconds) {
            this.number = number;
            this.nextNumber = nextNumber;
            this.seconds = seconds;
        }

        @Override
        public void run() {
            viewPager.setCurrentItem(number, false);
            currentPage = number;
            if (seconds > 0) {
                model.setCountdownSeconds(seconds);
                startCountdown(seconds, nextNumber);
            }
        }
    }

    public class countdownRunnable implements Runnable {
        private final int number;

        public countdownRunnable(int number) {
            this.number = number;
        }

        @Override
        public void run() {
            countdownSeconds--;
            model.setCountdownSeconds(countdownSeconds);
            if (countdownSeconds <= 0) {
                viewPager.setCurrentItem(number, false);
                currentPage = number;
            } else {
                countdownHandler.postDelayed(this, 1000);
            }
        }
    }

    public int getCurrentPage() {
        return currentPage;
    }
}