package com.example.smarthome.Model;

import java.util.ArrayList;

public class Location {
    private ArrayList<Device> devices;
    private String name;
    private int zip;

    public Location(){
        this.devices=new ArrayList<Device>();
        this.zip=0;
        this.name="unknown";
    }
    public Location(String name,int zip){
        this.zip=zip;
        this.name=name;
    }

    public Location(String name,int zip, ArrayList<Device> devices){
        this(name,zip);
        this.devices=devices;
    }


    public void addDevice(Device device){
        this.devices.add(device);
        //Server??
    }
    public int getRunningNum(){
        int count=0;
        for(int i=0; i<devices.size(); i++){
            if(devices.get(i).getState()==Device.RUNNING){
                count++;
            }
        }
        return count;
    }

    public ArrayList<Device> getDevices() {
        return this.devices;
    }
    public void setDevices(ArrayList<Device> devices) {
        this.devices = devices;
    }
    public int getZip() {
        return this.zip;
    }
    public void setZip(int zip) {
        this.zip = zip;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
