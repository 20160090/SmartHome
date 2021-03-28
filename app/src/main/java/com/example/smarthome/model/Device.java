package com.example.smarthome.model;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class Device implements Comparable<Device> {

    public enum State {
        RUNNING,
        NOT_RUNNING,
        SHOULD_BE_RUNNING,
        SHOULD_NOT_BE_RUNNING
    }


    private String id, name, serialNumber;
    private Company company;
    private PossibleDeviceType possibleDeviceType;
    private State state;
    private double averageConsumption, consumption;


    @Override
    public int compareTo(Device device) {
        int sol = 0;
        switch (this.state) {
            case SHOULD_BE_RUNNING:
                if (!device.getState().equals(State.SHOULD_BE_RUNNING)) {
                    sol = 1;
                    break;
                }
            case RUNNING:
                switch (device.getState()) {
                    case NOT_RUNNING:
                    case SHOULD_NOT_BE_RUNNING:
                        sol = 1;
                        break;
                    case SHOULD_BE_RUNNING:
                        sol = -1;
                        break;
                }
            case SHOULD_NOT_BE_RUNNING:
                switch (device.getState()) {
                    case NOT_RUNNING:
                        sol = 1;
                        break;
                    case RUNNING:
                    case SHOULD_BE_RUNNING:
                        sol = -1;
                        break;
                }
            case NOT_RUNNING:
                if (!device.getState().equals(State.NOT_RUNNING)) {
                    sol = 1;
                    break;
                }
        }
        return sol;
    }


    public Device() {
        this.id = "";
        this.name = "";
        this.serialNumber = "";
        this.possibleDeviceType = new PossibleDeviceType();
        this.averageConsumption = 0.0;
        this.state = State.NOT_RUNNING;
        this.consumption = 0.0;
    }

    public Device(Device device) {
        this(device.getId(), device.getName(), device.getPossibleDeviceType(), device.getState(), device.getSerialNumber(), device.getCompany(), device.getAverageConsumption(), device.getConsumption());
    }

    public Device(String id, String name, PossibleDeviceType possibleDeviceType, State state, String serialNumber, Company company, Double averageConsumption) {
        this.id = id;
        this.name = name;
        this.serialNumber = serialNumber;
        this.possibleDeviceType = possibleDeviceType;
        this.state = state;
        this.company = company;
        this.averageConsumption = averageConsumption;
    }
    public Device(String id, String name, PossibleDeviceType possibleDeviceType, State state, String serialNumber, Company company, Double averageConsumption, Double consumption) {
        this.id = id;
        this.name = name;
        this.serialNumber = serialNumber;
        this.possibleDeviceType = possibleDeviceType;
        this.state = state;
        this.company = company;
        this.averageConsumption = averageConsumption;
        this.consumption = consumption;
    }
    public Device(String id, String name, PossibleDeviceType possibleDeviceType, String state, String serialNumber, Company company, Double averageConsumption) {

        this.id = id;
        this.name = name;
        this.serialNumber = serialNumber;
        this.possibleDeviceType = possibleDeviceType;
        this.state = switchState(state);
        this.company = company;
        this.averageConsumption = averageConsumption;
    }


    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public PossibleDeviceType getPossibleDeviceType() {
        return possibleDeviceType;
    }

    public void setPossibleDeviceType(PossibleDeviceType possibleDeviceType) {
        this.possibleDeviceType = possibleDeviceType;
    }

    public String getSerialNumber() {
        return this.serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public State getState() {
        return this.state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public Company getCompany() {
        return this.company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    public double getAverageConsumption() {
        return this.averageConsumption;
    }

    public void setAverageConsumption(double averageConsumption) {
        this.averageConsumption = averageConsumption;
    }

    public State switchState(String string) {
        switch (string) {
            case "RUNNING":
                return State.RUNNING;
            case "NOT_RUNNING":
                return State.NOT_RUNNING;
            case "SHOULD_BE_RUNNING":
                return State.SHOULD_BE_RUNNING;
            case "SHOULD_NOT_BE_RUNNING":
                return State.SHOULD_NOT_BE_RUNNING;
        }
        return null;
    }

    public double getConsumption() {
        return this.consumption;
    }

    public void setConsumption(double consumption) {
        this.consumption = consumption;
    }
}

