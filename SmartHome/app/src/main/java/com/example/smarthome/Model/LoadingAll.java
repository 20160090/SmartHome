package com.example.smarthome.model;

import java.util.ArrayList;
import java.util.concurrent.Callable;

public class LoadingAll implements Callable<ArrayList<Location>> {
    private final String input;
    public LoadingAll(String input){
        this.input = input;
    }
    @Override
    public ArrayList<Location> call() throws Exception {
        return null;
    }
}
