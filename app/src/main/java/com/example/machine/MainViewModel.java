package com.example.machine;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.ftdi.j2xx.FT_Device;

import java.util.Arrays;
import java.util.Vector;

import datamodel.CarInside;

public class MainViewModel extends ViewModel {
    private final MutableLiveData<Vector<CarInside>> cars = new MutableLiveData<>();
    private final MutableLiveData<CarInside> selectedCar = new MutableLiveData<>();
    private final MutableLiveData<Integer> shouldPayMoney = new MutableLiveData<>(0);
    private final MutableLiveData<Integer> discountMoney = new MutableLiveData<>(0);
    private final MutableLiveData<Integer> totalMoney = new MutableLiveData<>(0);
    private final MutableLiveData<Integer> payWay = new MutableLiveData<>(0);
    private final MutableLiveData<String> payTime = new MutableLiveData<>();
    private final MutableLiveData<FT_Device> coinInputDevice = new MutableLiveData<>();
    private final MutableLiveData<FT_Device> paperInputDevice = new MutableLiveData<>();
    private final MutableLiveData<FT_Device> coin5Device = new MutableLiveData<>();
    private final MutableLiveData<FT_Device> coin10Device = new MutableLiveData<>();
    private final MutableLiveData<FT_Device> coin50Device = new MutableLiveData<>();

    public MutableLiveData<Vector<CarInside>> getCars() {
        return cars;
    }

    public void setCars(Vector<CarInside> cars) {
        this.cars.setValue(cars);
    }

    public MutableLiveData<CarInside> getSelectedCars() {
        return selectedCar;
    }

    public void setSelectedCars(CarInside car) {
        this.selectedCar.postValue(car);
    }

    public MutableLiveData<Integer> getShouldPayMoney() {
        return shouldPayMoney;
    }

    public void setShouldPayMoney(Integer shouldPayMoney) {
        this.shouldPayMoney.postValue(shouldPayMoney);
    }

    public MutableLiveData<Integer> getDiscountMoney() {
        return discountMoney;
    }

    public void setDiscountMoney(Integer discountMoney) {
        this.discountMoney.postValue(discountMoney);
    }

    public MutableLiveData<Integer> getTotalMoney() {
        return totalMoney;
    }

    public void setTotalMoney(Integer totalMoney) {
        this.totalMoney.postValue(totalMoney);
    }

    public MutableLiveData<String> getPayTime() {
        return payTime;
    }

    public void setPayTime(String payTime) {
        this.payTime.postValue(payTime);
    }

    public MutableLiveData<Integer> getPayWay() {
        return payWay;
    }

    public void setPayWay(int payWay) {
        this.payWay.postValue(payWay);
        switch (payWay) {
            case 0:
                startCoinPay = true;
                coinReady = setCoinInputEnable();
                if (coinReady) {
                    readCoinInput();
                }
                setPaperEnable();
                break;
            case 1:
                break;
            case 2:
                break;
            default:
                break;
        }
    }

    public MutableLiveData<FT_Device> getCoinInputDevice() {
        return coinInputDevice;
    }

    public MutableLiveData<FT_Device> getPaperInputDevice() {
        return paperInputDevice;
    }

    public MutableLiveData<FT_Device> getCoin5Device() {
        return coin5Device;
    }

    public MutableLiveData<FT_Device> getCoin10Device() {
        return coin10Device;
    }

    public MutableLiveData<FT_Device> getCoin50Device() {
        return coin50Device;
    }

    public void setCoinInputDevice(FT_Device device) {
        this.coinInputDevice.postValue(device);
    }

    public void setPaperInputDevice(FT_Device device) {
        this.paperInputDevice.postValue(device);
        readPaperInput();
    }

    public void setCoin5Device(FT_Device device) {
        this.coin5Device.postValue(device);
    }

    public void setCoin10Device(FT_Device device) {
        this.coin10Device.postValue(device);
    }

    public void setCoin50Device(FT_Device device) {
        this.coin50Device.postValue(device);
    }

    private boolean readCoinInput = false;
    private boolean readPaperInput = false;
    private static final byte[] enableCoinInput = new byte[]{(byte) 0x90, 0x05, 0x01, 0x03, (byte) 0x99};
    private static final byte[] disableCoinInput = new byte[]{(byte) 0x90, 0x05, 0x02, 0x03, (byte) 0x9a};
    private static final byte[] enableCoinInputSuccess = new byte[]{(byte) 0x90, 0x05, 0x50, 0x03, (byte) 0xe8};
    private static final byte[] getCoin5 = new byte[]{(byte) 0x90, 0x06, 0x12, 0x02, 0x03, (byte) 0xAD};
    private static final byte[] getCoin10 = new byte[]{(byte) 0x90, 0x06, 0x12, 0x02, 0x03, (byte) 0xAE};
    private static final byte[] getCoin50 = new byte[]{(byte) 0x90, 0x06, 0x12, 0x02, 0x03, (byte) 0xAF};
    private static final byte[] paperPower = new byte[]{(byte) 0x80, (byte) 0x8F};
    private static final byte[] paperPowerReply = new byte[]{0x02};
    private static final byte[] enablePaperInput = new byte[]{0x3e};
    private static final byte[] disablePaperInput = new byte[]{0x5e};
    private static final byte[] checkPaperStatus = new byte[]{0x0c};
    private static final byte[] getPaper = new byte[]{(byte) 0x81, 0x40};
    private static final byte[] getPaperConfirm = new byte[]{0x02};
    private static final byte[] getPaperReject = new byte[]{0x0F};
    private int pay = 0;
    private boolean startCoinPay = false;
    private boolean coinReady = false;
    private boolean paperReady = false;
    private boolean startEZPay = false;
    private boolean startLinePay = false;

    private void setInputDisable() {
        setCoinInputDisable();
        setPaperDisable();
        coinReady = false;
        paperReady = false;
        startCoinPay = false;
    }

    private boolean setCoinInputEnable() {
        boolean read = true;
        byte[] data = new byte[5];
        coinInputDevice.getValue().write(enableCoinInput);
        while (read) {
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            // readData = new byte[readLength];
            int len = coinInputDevice.getValue().getQueueStatus();

            if (len >= 5) {
                coinInputDevice.getValue().read(data, 5);
                read = false;
            }
        }
        return Arrays.equals(data, enableCoinInputSuccess);
    }

    private boolean setCoinInputDisable() {
        readCoinInput = false;
        coinInputDevice.getValue().write(disableCoinInput);
        boolean read = true;
        byte[] data = new byte[5];
        while (read) {
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            // readData = new byte[readLength];
            int len = coinInputDevice.getValue().getQueueStatus();

            if (len >= 5) {
                coinInputDevice.getValue().read(data, 5);
                read = false;
            }
        }
        return Arrays.equals(data, enableCoinInputSuccess);
    }

    private void readCoinInput() {
        readCoinInput = true;
        new Thread(() -> {
            while (readCoinInput) {
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                // readData = new byte[readLength];
                int len = coinInputDevice.getValue().getQueueStatus();
                byte[] data = new byte[5];
                if (len >= 5) {
                    coinInputDevice.getValue().read(data, 6);
                    if (Arrays.equals(data, getCoin5)) {
                        pay += 5;
                    } else if (Arrays.equals(data, getCoin10)) {
                        pay += 10;
                    } else if (Arrays.equals(data, getCoin50)) {
                        pay += 50;
                    }
                    if (pay >= shouldPayMoney.getValue()) {
                        setInputDisable();
                    }
                }
            }
        }).start();
    }

    private void setPaperEnable() {
        paperInputDevice.getValue().write(enablePaperInput);
        paperInputDevice.getValue().write(checkPaperStatus);
    }

    private void setPaperDisable() {
        paperInputDevice.getValue().write(disablePaperInput);
    }

    private void readPaperInput() {
        new Thread(() -> {
            while (readPaperInput) {
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                int len = paperInputDevice.getValue().getQueueStatus();
                byte[] data = new byte[1];
                if (len >= 1) {
                    paperInputDevice.getValue().read(data, 1);
                    if (Arrays.equals(data, new byte[]{(byte) 0x80})) {
                        len = 0;
                        while (len <= 0) {
                            len = paperInputDevice.getValue().getQueueStatus();
                        }
                        paperInputDevice.getValue().read(data, 1);
                        if (Arrays.equals(data, new byte[]{(byte) 0x8F})) {
                            paperInputDevice.getValue().write(paperPowerReply);
                        }
                    } else if (Arrays.equals(data, new byte[]{(byte) 0x81})) {
                        len = 0;
                        while (len <= 0) {
                            len = paperInputDevice.getValue().getQueueStatus();
                        }
                        paperInputDevice.getValue().read(data, 1);
                        if (Arrays.equals(data, new byte[]{(byte) 0x40}) && paperReady && startCoinPay) {
                            paperInputDevice.getValue().write(getPaperConfirm);
                            pay += 100;
                            if (pay >= shouldPayMoney.getValue()) {
                                setInputDisable();
                            }
                        } else {
                            paperInputDevice.getValue().write(getPaperReject);
                        }
                    } else if (Arrays.equals(data, enablePaperInput)) {
                        paperReady = true;
                    } else if (Arrays.equals(data, disablePaperInput)) {
                        paperReady = false;
                    }
                }
            }
        }).start();
    }
}
