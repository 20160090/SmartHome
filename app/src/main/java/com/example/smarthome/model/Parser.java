package com.example.smarthome.model;

import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.HttpsCallableResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.function.Function;


public class Parser {
    private final User user;
    private final FirebaseFunctions mFunction;

    public Parser() {
        this.user = User.getInstance();
        this.mFunction = FirebaseFunctions.getInstance();
    }

    public void callDevices(Location location) {
        Map<String, String> data = new HashMap<>();
        data.put("locationID", location.getId());
        data.put("email", this.user.getFirebaseUser().getEmail());

        this.mFunction
                .getHttpsCallable("getConsumers")
                .call(data)
                .addOnSuccessListener(task -> {
                    try {
                        JSONObject object = new JSONObject(task.getData().toString());
                        JSONArray array = object.getJSONArray("Consumers");
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject obj = array.getJSONObject(i);
                            JSONObject act = obj.getJSONObject("Consumer");
                            location.addDevice(this.parseDevice(act));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                });
    }

    public void callGenerator(Location location) {
        Map<String, String> data = new HashMap<>();
        data.put("email", this.user.getFirebaseUser().getEmail());
        data.put("locationID", location.getId());
        this.mFunction
                .getHttpsCallable("getGenerators")
                .call(data)
                .addOnSuccessListener(result -> {
                    try {
                        JSONObject object = new JSONObject(result.getData().toString());
                        JSONArray array = object.getJSONArray("Generators");
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject obj = array.getJSONObject(i);
                            JSONObject act = obj.getJSONObject("Generator");
                            location.addProducer(this.parseProducer(act));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                });

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void parseGetLocations(JSONArray array) {
        try {
            for (int i = 0; i < array.length(); i++) {
                JSONObject act = array.getJSONObject(i);
                JSONObject object = act.getJSONObject("Location");
                Location location = this.parseLocation(object);
                parseOneLocation(location);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void parseOneLocation(Location location) {
        callGetWeather(location);
        callDevices(location);
        callGenerator(location);
        this.user.addLocation(location);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void callGetWeather(Location location) {
        Map<String, String> data = new HashMap<>();
        data.put("locationID", location.getId());
        data.put("city", location.getCity());
        this.mFunction.getHttpsCallable("getWeather")
                .call(data)
                .addOnSuccessListener(task -> {
                    try {
                        //TODO: unterminated object at character 15 of {Error=Cannot read property '0' of undefined}
                        JSONObject object = new JSONObject(task.getData().toString());
                        setWeather(object, location);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        location.setWeather(new Weather());
                    }
                })
                .addOnFailureListener(Throwable::printStackTrace);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void callGetLocations(Function<HttpsCallableResult, HttpsCallableResult> callBack) {
        Map<String, String> data = new HashMap<>();
        data.put("email", User.getInstance().getFirebaseUser().getEmail());
        this.mFunction.getHttpsCallable("getLocations")
                .call(data)
                .continueWith(task -> {
                    try {
                        JSONObject object = new JSONObject(task.getResult().getData().toString());
                        JSONArray array = object.getJSONArray("Locations");
                        parseGetLocations(array);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    return task;
                })
                .addOnCompleteListener(task -> {
                    callBack.apply(task.getResult().getResult());
                });

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void setWeather(JSONObject object, Location location) {
        location.setWeather(this.parseWeather(object));
    }

    public Location parseLocation(JSONObject object) {
        Location location = new Location();
        try {
            location.setId(object.getString("locationID"));
            location.setName(object.getString("name"));
            location.setZip(object.getInt("zip"));
            location.setCity(object.getString("city"));
            location.setCountry(object.getString("country"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return location;
    }

    public Producer parseProducer(JSONObject object) {
        Producer producer = new Producer();
        try {
            producer.setId(object.getString("pvID"));
            producer.setType(object.getString("generatorType"));
            Map<String, String> data = new HashMap<>();
            data.put("pvID", producer.getId());
            this.mFunction
                    .getHttpsCallable("getPVData")
                    .call(data)
                    .addOnSuccessListener(result -> {
                        try {
                            JSONObject object1 = new JSONObject(result.getData().toString());
                            JSONArray array = object1.getJSONArray("PVData");
                            JSONObject act = array.getJSONObject(3);
                            try {
                                producer.setCurrentlyProduced(act.getDouble("value"));
                            } catch (JSONException e) {
                                producer.setCurrentlyProduced(0.0);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    });
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return producer;
    }

    public Device parseDevice(JSONObject object) {
        Device device = new Device();
        try {
            device.setId(object.getString("consumerID"));
            device.setName(object.getString("consumerName"));
            device.setSerialNumber(object.getString("consumerSerial"));
            device.setState(device.switchState(object.getString("consumerState").toUpperCase()));
            device.setCompany(object.getString("companyName"));
            device.setPossibleDeviceType(object.getString("consumerType"));
            device.setAverageConsumption(object.getDouble("consumerAverageConsumption"));

            callConsumerData(device.getPossibleDeviceType(), device);


        } catch (JSONException e) {
            e.printStackTrace();
        }
        return device;
    }

    public void callConsumerData(String type, Device device) {
        Map<String, String> data = new HashMap<>();
        data.put("consumerType", type);
        mFunction.getHttpsCallable("getConsumerData")
                .call(data)
                .addOnSuccessListener(result -> {
                    try {
                        JSONObject obj = new JSONObject(result.getData().toString());
                        JSONObject object = obj.getJSONArray("Consumers").getJSONObject(0);
                        device.setConsumption(object.getDouble("consumption"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                        device.setConsumption(-1.0);
                    }
                })
                .addOnFailureListener(e -> e.printStackTrace());
    }

    public PossibleDeviceType parsePossibleDeviceType(JSONObject object) {
        PossibleDeviceType type = new PossibleDeviceType();
        try {
            type.setId(object.getString("id"));
            type.setType(object.getString("type"));
            type.setAverageConsumption(object.getDouble("averageConsumption"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return type;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public Weather parseWeather(JSONObject object) {
        Weather weather = new Weather();
        try {
            long unixTime = object.getLong("dt");
            Instant instant = Instant.ofEpochSecond(unixTime);
            weather.setDate(LocalDateTime.ofInstant(instant, ZoneOffset.UTC));

            long unix_Secons = object.getLong("sunset");
            Date date = new Date(unix_Secons * 1000L);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z");
            sdf.setTimeZone(TimeZone.getTimeZone("MEZ"));
            weather.setSunset(date.toInstant().atZone(ZoneId.systemDefault()).toLocalTime());

            unix_Secons = object.getLong("sunrise");
            date = new Date(unix_Secons * 1000L);
            weather.setSunrise(date.toInstant().atZone(ZoneId.systemDefault()).toLocalTime());
            weather.setDescription(object.getString("description"));
            weather.setTemp(object.getDouble("temp"));

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return weather;
    }

    public void callFunctionAddPV(String locationID, String pvID) {
        Map<String, String> data = new HashMap<>();
        data.put("email", this.user.getFirebaseUser().getEmail());
        data.put("locationID", locationID);
        data.put("pvID", pvID);

        FirebaseFunctions mFunction = FirebaseFunctions.getInstance();
        mFunction
                .getHttpsCallable("addPV")
                .call(data)
                .addOnSuccessListener(httpsCallableResult -> {
                });

    }

    public void callCompanies() {
        FirebaseFunctions mFunctions = FirebaseFunctions.getInstance();
        mFunctions
                .getHttpsCallable("getPossibleCompanies")
                .call()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        try {
                            JSONObject object = new JSONObject(task.getResult().getData().toString());
                            parseCompanies(object.getJSONArray("Companies"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        task.getException().printStackTrace();
                    }
                });
    }

    public void parseCompanies(JSONArray array) {
        try {
            this.user.getCompanies().clear();
            FirebaseFunctions mFunctions = FirebaseFunctions.getInstance();
            for (int i = 0; i < array.length(); i++) {
                JSONObject act = array.getJSONObject(i);
                JSONObject object = act.getJSONObject("Company");
                Company company = new Company(object.getString("companyID"), object.getString("companyName"));
                Map<String, String> data = new HashMap<>();
                data.put("companyID", company.getId());
                mFunctions
                        .getHttpsCallable("getPossibleConsumers")
                        .call(data)
                        .addOnSuccessListener(task -> {
                            try {
                                JSONObject types = new JSONObject(task.getData().toString());
                                company.setDevices(parsePossibleTypes(types.getJSONArray("Consumers")));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        })
                        .addOnFailureListener(Throwable::printStackTrace);
                this.user.getCompanies().add(company);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<PossibleDeviceType> parsePossibleTypes(JSONArray array) {
        ArrayList<PossibleDeviceType> types = new ArrayList<>();
        try {
            for (int i = 0; i < array.length(); i++) {
                JSONObject act = array.getJSONObject(i);
                JSONObject object = act.getJSONObject("Consumer");
                PossibleDeviceType deviceType = new PossibleDeviceType(object.getString("consumerID"), object.getString("consumerType"), object.getDouble("averageConsumption"));
                types.add(deviceType);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return types;
    }
}
