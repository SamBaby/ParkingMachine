package com.example.machine;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.os.Handler;

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

import java.util.TimerTask;

import ecpay.EcpayFunction;
import usb.UsbConnectionContext;
import usb.UsbConnector;

public class MainActivity extends AppCompatActivity {
    private MainViewModel model;
    private D2xxManager coinInputManager;
    private FT_Device coinInputDevice;
    private FT_Device paperInputDevice;
    private FT_Device coin50Device;
    private FT_Device coin10Device;
    private UsbConnector printConnector;
    private UsbConnectionContext printCxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        ViewPager viewPager = findViewById(R.id.view_pager);
        MachinePagerAdapter adapter = new MachinePagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);
        model = new ViewModelProvider(this).get(MainViewModel.class);
        try {
            coinInputManager = D2xxManager.getInstance(this);
            handleFT4232H(coinInputManager);
            handlePrintMachine();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void handlePrintMachine() {
        printConnector = new UsbConnector(this);
        printCxt = printConnector.ConnectUsb(0, 0, 0, 0);
        model.setInvoiceConnector(printConnector);
        model.setInvoiceCxt(printCxt);
    }

    private void handleFT4232H(D2xxManager manager) {
        int devCount = 0;
        devCount = manager.createDeviceInfoList(this);
        if (devCount == 4 && coinInputDevice == null) {
            coinInputDevice = manager.openByIndex(this, 0);
            setCoinMachineConfig(coinInputDevice);
            paperInputDevice = manager.openByIndex(this, 1);
            setPaperMachineConfig(paperInputDevice);

            coin10Device = manager.openByIndex(this, 2);
            setPaperMachineConfig(coin10Device);
            initRefund(coin10Device);

            coin50Device = manager.openByIndex(this, 3);
            setPaperMachineConfig(coin50Device);
            initRefund(coin50Device);
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
                handleFT4232H(coinInputManager);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}