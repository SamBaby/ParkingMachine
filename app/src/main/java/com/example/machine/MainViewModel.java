package com.example.machine;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.Vector;

import datamodel.CarInside;

public class MainViewModel extends ViewModel {
    private final MutableLiveData<Vector<CarInside>> cars = new MutableLiveData<>();
    private final MutableLiveData<CarInside> selectedCar = new MutableLiveData<>();
    private final MutableLiveData<Integer> shouldPayMoney = new MutableLiveData<>();
    private final MutableLiveData<Integer> discountMoney = new MutableLiveData<>();
    private final MutableLiveData<Integer> totalMoney = new MutableLiveData<>();
    private final MutableLiveData<String> payTime = new MutableLiveData<>();
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

    public MutableLiveData<String> getPayTime(){
        return payTime;
    }
    public void setPayTime(String payTime){
        this.payTime.postValue(payTime);
    }

}
