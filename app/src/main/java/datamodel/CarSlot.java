package datamodel;

public class CarSlot {
    private int car_slot;
    private int pregnant_slot;
    private int disabled_slot;
    private int charging_slot;
    private int reserved_slot;

    public CarSlot(int carSlot, int pregnantSlot, int disabledSlot, int chargingSlot, int reservedSlot) {
        setCar_slot(carSlot);
        setPregnant_slot(pregnantSlot);
        setDisabled_slot(disabledSlot);
        setCharging_slot(chargingSlot);
        setReserved_slot(reservedSlot);
    }

    public int getCar_slot() {
        return car_slot;
    }

    public void setCar_slot(int car_slot) {
        this.car_slot = car_slot;
    }

    public int getPregnant_slot() {
        return pregnant_slot;
    }

    public void setPregnant_slot(int pregnant_slot) {
        this.pregnant_slot = pregnant_slot;
    }

    public int getDisabled_slot() {
        return disabled_slot;
    }

    public void setDisabled_slot(int disabled_slot) {
        this.disabled_slot = disabled_slot;
    }

    public int getCharging_slot() {
        return charging_slot;
    }

    public void setCharging_slot(int charging_slot) {
        this.charging_slot = charging_slot;
    }

    public int getReserved_slot() {
        return reserved_slot;
    }

    public void setReserved_slot(int reserved_slot) {
        this.reserved_slot = reserved_slot;
    }
}
