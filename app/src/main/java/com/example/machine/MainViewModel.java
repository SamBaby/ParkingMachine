package com.example.machine;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.ftdi.j2xx.FT_Device;

import java.util.Arrays;
import java.util.Vector;

import datamodel.CarInside;
import usb.UsbConnectionContext;
import usb.UsbConnector;

public class MainViewModel extends ViewModel {
    private final MutableLiveData<Vector<CarInside>> cars = new MutableLiveData<>();
    private final MutableLiveData<CarInside> selectedCar = new MutableLiveData<>();
    private final MutableLiveData<Integer> shouldPayMoney = new MutableLiveData<>(0);
    private final MutableLiveData<Integer> discountMoney = new MutableLiveData<>(0);
    private final MutableLiveData<Integer> totalMoney = new MutableLiveData<>(0);
    private final MutableLiveData<Integer> payWay = new MutableLiveData<>(0);
    private final MutableLiveData<String> payTime = new MutableLiveData<>();
    private FT_Device coinInputDevice;
    private FT_Device paperInputDevice;
    private FT_Device coin10Device;
    private FT_Device coin50Device;
    private final MutableLiveData<Integer> totalPay = new MutableLiveData<>(0);
    private final MutableLiveData<android.os.Handler> changePageHandler = new MutableLiveData<>(new android.os.Handler());
    private final MutableLiveData<Runnable> changePageRunnable = new MutableLiveData<>();
    private UsbConnector invoiceConnector;
    private UsbConnectionContext invoiceCxt;

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
                setCoinInputEnable();
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

    public FT_Device getCoinInputDevice() {
        return coinInputDevice;
    }

    public FT_Device getPaperInputDevice() {
        return paperInputDevice;
    }


    public FT_Device getCoin10Device() {
        return coin10Device;
    }

    public FT_Device getCoin50Device() {
        return coin50Device;
    }

    public void setCoinInputDevice(FT_Device device) {
        this.coinInputDevice = device;
        readCoinInput();
    }

    public void setPaperInputDevice(FT_Device device) {
        this.paperInputDevice = device;
        readPaperInput();
    }

    public void setCoin10Device(FT_Device device) {
        this.coin10Device = device;
        read10Input();
    }

    public void setCoin50Device(FT_Device device) {
        this.coin50Device = device;
        read50Input();
    }

    private boolean readCoinInput = false;
    private boolean readPaperInput = false;
    private static final byte[] enableCoinInput = new byte[]{(byte) 0x90, 0x05, 0x01, 0x03, (byte) 0x99};
    private static final byte[] disableCoinInput = new byte[]{(byte) 0x90, 0x05, 0x02, 0x03, (byte) 0x9a};
    private static final byte[] enableCoinInputSuccess = new byte[]{(byte) 0x90, 0x05, 0x50, 0x03, (byte) 0xe8};
    private static final byte[] getCoin5 = new byte[]{(byte) 0x90, 0x06, 0x12, 0x02, 0x03, (byte) 0xAD};
    private static final byte[] getCoin10 = new byte[]{(byte) 0x90, 0x06, 0x12, 0x03, 0x03, (byte) 0xAE};
    private static final byte[] getCoin50 = new byte[]{(byte) 0x90, 0x06, 0x12, 0x04, 0x03, (byte) 0xAF};
    private static final byte[] paperPower = new byte[]{(byte) 0x80, (byte) 0x8F};
    private static final byte[] paperPowerReply = new byte[]{0x02};
    private static final byte[] enablePaperInput = new byte[]{0x3e};
    private static final byte[] disablePaperInput = new byte[]{0x5e};
    private static final byte[] checkPaperStatus = new byte[]{0x0c};
    private static final byte[] getPaper = new byte[]{(byte) 0x81, 0x40};
    private static final byte[] getPaperConfirm = new byte[]{0x02};
    private static final byte[] getPaperReject = new byte[]{0x0F};
    private boolean startCoinPay = false;
    private boolean coinReady = false;
    private boolean paperReady = false;
    private boolean startEZPay = false;
    private boolean startLinePay = false;

    public boolean isStartCoinPay() {
        return startCoinPay;
    }

    public void stopCoinPay() {
        setCoinInputDisable();
        setPaperDisable();
        startCoinPay = false;
        coinReady = false;
        paperReady = false;

        //check refund
        int refund = totalPay.getValue() - totalMoney.getValue();
        if (refund > 0) {
            int fifty = refund / 50;
            while (fifty > 0) {
                if (fifty >= 10) {
                    fifty -= 10;
                    refund50Coin10();
                } else if (fifty >= 5) {
                    fifty -= 5;
                    refund50Coin5();
                } else if (fifty >= 2) {
                    fifty -= 2;
                    refund50Coin2();
                } else {
                    fifty -= 1;
                    refund50Coin1();
                }
            }
            int ten = refund % 50 / 10;
            while (ten > 0) {
                if (ten >= 10) {
                    ten -= 10;
                    refund10Coin10();
                } else if (ten >= 5) {
                    ten -= 5;
                    refund10Coin5();
                } else if (ten >= 2) {
                    ten -= 2;
                    refund10Coin2();
                } else {
                    ten -= 1;
                    refund10Coin1();
                }
            }
        }
    }

    private void setCoinInputEnable() {
        coinInputDevice.write(enableCoinInput, enableCoinInput.length);
    }

    private void setCoinInputDisable() {
        coinInputDevice.write(disableCoinInput);
    }

    private void setPaperEnable() {
        paperInputDevice.write(enablePaperInput);
        paperInputDevice.write(checkPaperStatus);
    }

    private void setPaperDisable() {
        paperInputDevice.write(disablePaperInput);
    }

    private void readCoinInput() {
        new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                // readData = new byte[readLength];
                int len = coinInputDevice.getQueueStatus();
                if (startCoinPay) {
                    if (coinReady) {
                        byte[] data = new byte[6];
                        if (len >= 6) {
                            coinInputDevice.read(data, 6);
                            if (Arrays.equals(data, getCoin5)) {
                                getTotalPay().postValue(getTotalPay().getValue() + 5);
                            } else if (Arrays.equals(data, getCoin10)) {
                                getTotalPay().postValue(getTotalPay().getValue() + 10);
                            } else if (Arrays.equals(data, getCoin50)) {
                                getTotalPay().postValue(getTotalPay().getValue() + 50);
                            }
                        }
                    } else {
                        byte[] data = new byte[5];
                        if (len >= 5) {
                            coinInputDevice.read(data, 5);
                            if (Arrays.equals(enableCoinInputSuccess, data)) {
                                coinReady = true;
                            }
                        }
                    }
                } else if (len > 0) {
                    coinInputDevice.read(new byte[len], len);
                }

            }
        }).start();
    }

    private void readPaperInput() {
        new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                int len = paperInputDevice.getQueueStatus();
                byte[] data = new byte[1];
                if (len >= 1) {
                    paperInputDevice.read(data, 1);
                    if (Arrays.equals(data, new byte[]{(byte) 0x80})) {
                        len = 0;
                        while (len <= 0) {
                            len = paperInputDevice.getQueueStatus();
                        }
                        paperInputDevice.read(data, 1);
                        if (Arrays.equals(data, new byte[]{(byte) 0x8F})) {
                            paperInputDevice.write(paperPowerReply);
                        }
                    } else if (Arrays.equals(data, new byte[]{(byte) 0x81})) {
                        len = 0;
                        while (len <= 0) {
                            len = paperInputDevice.getQueueStatus();
                        }
                        paperInputDevice.read(data, 1);
                        if (Arrays.equals(data, new byte[]{(byte) 0x40}) && paperReady) {
                            paperInputDevice.write(getPaperConfirm);
                            getTotalPay().postValue(getTotalPay().getValue() + 100);
                        } else {
                            paperInputDevice.write(getPaperReject);
                        }
                    } else if (Arrays.equals(data, new byte[]{(byte) 0x3E})) {
                        paperReady = true;
                    } else if (Arrays.equals(data, new byte[]{(byte) 0x5E})) {
                        paperReady = false;
                    }
                }
            }
        }).start();
    }

    private void read10Input() {
        new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                int len = coin10Device.getQueueStatus();
                if (len > 0) {
                    byte[] data = new byte[1];
                    coin10Device.read(data, 1);
                    if (Arrays.equals(data, new byte[]{0x02})) {
                        coin10Device.write(new byte[]{0x10});
                    } else {
                        coin10Device.write(new byte[]{0x11});
                    }
                }

            }
        }).start();
    }

    private void read50Input() {
        new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                int len = coin50Device.getQueueStatus();
                if (len > 0) {
                    byte[] data = new byte[1];
                    coin50Device.read(data, 1);
                    if (Arrays.equals(data, new byte[]{0x02})) {
                        coin50Device.write(new byte[]{0x10});
                    } else {
                        coin50Device.write(new byte[]{0x11});
                    }
                }

            }
        }).start();
    }

    public MutableLiveData<Integer> getTotalPay() {
        return totalPay;
    }

    public void resetTotalPay() {
        totalPay.postValue(0);
    }

    public MutableLiveData<android.os.Handler> getChangePageHandler() {
        return changePageHandler;
    }

    public MutableLiveData<Runnable> getChangePageRunnable() {
        return changePageRunnable;
    }

    public UsbConnector getInvoiceConnector() {
        return invoiceConnector;
    }

    public UsbConnectionContext getInvoiceCxt() {
        return invoiceCxt;
    }

    public void setInvoiceConnector(UsbConnector connector) {
        this.invoiceConnector = connector;
    }

    public void setInvoiceCxt(UsbConnectionContext cxt) {
        this.invoiceCxt = cxt;
    }

    public void refund10Coin1() {
        coin10Device.write(new byte[]{(byte) 0x81});
        coin10Device.write(new byte[]{0x40});
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void refund10Coin2() {
        coin10Device.write(new byte[]{(byte) 0x81});
        coin10Device.write(new byte[]{0x41});
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void refund10Coin5() {
        coin10Device.write(new byte[]{(byte) 0x81});
        coin10Device.write(new byte[]{0x42});
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void refund10Coin10() {
        coin10Device.write(new byte[]{(byte) 0x81});
        coin10Device.write(new byte[]{0x43});
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void refund50Coin1() {
        coin10Device.write(new byte[]{(byte) 0x81});
        coin10Device.write(new byte[]{0x40});
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void refund50Coin2() {
        coin10Device.write(new byte[]{(byte) 0x81});
        coin10Device.write(new byte[]{0x41});
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void refund50Coin5() {
        coin10Device.write(new byte[]{(byte) 0x81});
        coin10Device.write(new byte[]{0x42});
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void refund50Coin10() {
        coin50Device.write(new byte[]{(byte) 0x81});
        coin50Device.write(new byte[]{0x43});
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
