package com.example.smarthome.model;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.example.smarthome.R;
import com.github.pwittchen.weathericonview.WeatherIconView;
import com.google.android.gms.common.util.JsonUtils;
import com.google.android.gms.tasks.Task;
import com.google.firebase.functions.FirebaseFunctions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.CountDownLatch;
import java.util.function.Function;


public class Parser {
    private final User user;
    private final FirebaseFunctions mFunction;
    private static final Parser parser = new Parser();
    private CountDownLatch countDownLatch;

    public Parser() {
        this.user = User.getInstance();
        this.mFunction = FirebaseFunctions.getInstance();
    }

    public static Parser getInstance() {
        return parser;
    }


    //region Location

    public void loadLocations(Function<Task, Object> callback) {
        countDownLatch = new CountDownLatch(1000);
        Map<String, String> data = new HashMap<>();
        data.put("email", user.getFirebaseUser().getEmail());
        Task callTask = this.mFunction.getHttpsCallable("getLocations")
                .call(data)
                .addOnSuccessListener(result -> {
                    try {
                        JSONArray array = new JSONObject(result.getData().toString()).getJSONArray("Locations");
                        //pro location 4 tasks
                        countDownLatch = new CountDownLatch(array.length() * 4);
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject object = array.getJSONObject(i).getJSONObject("Location");
                            Location location = new Location();

                            location.setId(object.getString("locationID"));
                            location.setName(object.getString("name"));
                            location.setZip(object.getInt("zip"));
                            location.setCity(object.getString("city"));
                            location.setCountry(object.getString("country"));

                            parser.callGetGeneratorCallback(location, countDownLatch, null);
                            parser.callGetDevicesCallback(location, t -> {
                                countDownLatch.countDown();
                                return 0;
                            });
                            parser.callGetWeatherCallback(location, t -> {
                                countDownLatch.countDown();
                                return 0;
                            });
                            parser.callGetForecastCallback(location, t -> {
                                countDownLatch.countDown();
                                return 0;
                            });
                            user.addLocation(location);
                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                });
        new Thread(() -> {
            try {

                countDownLatch.await();
                if (callback != null) {
                    callTask.continueWith(result -> callback.apply(result));
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();


    }


    /* public void callGetLocationsCallback(Function<Task, Object> callback) {
         Map<String, String> data = new HashMap<>();
         data.put("email", User.getInstance().getFirebaseUser().getEmail());
         Task callTask = this.mFunction.getHttpsCallable("getLocations")
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
                 });
         if (callback != null) {
             callTask.continueWith(result -> callback.apply(result));
         }
     }*/
/*
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void parseGetLocations(JSONArray array) {
        try {
            for (int i = 0; i < array.length(); i++) {
                JSONObject act = array.getJSONObject(i);
                JSONObject object = act.getJSONObject("Location");
                Location location = this.parseLocation(object);
                callWeatherDeviceGenerator(location);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }*/
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
            return null;
        }
        return location;
    }

    /*
        public boolean callWeatherDeviceGenerator(Location location) {
            callGetGeneratorCallback(location, t1 -> {
                callGetWeatherCallback(location, t2 -> {
                    callGetForecastCallback(location, t3 -> {
                        callgetDevicesCallback(location, t4 -> {
                            this.user.addLocation(location);
                            return 0;
                        });
                        return 0;
                    });
                    return 0;
                });
                return 0;
            });
            return true;
        }*/
    public void callWeatherDeviceGenerator(Location location) {
        callGetGeneratorCallback(location, null, null);
        callGetDevicesCallback(location, null);
        callGetForecastCallback(location, null);
        callGetWeatherCallback(location, null);
    }
    //endregion

    //region Weather&Forecast
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void callGetWeatherCallback(Location location, Function<Task, Object> callback) {
        Map<String, String> data = new HashMap<>();
        data.put("locationID", location.getId());
        data.put("zip", location.getZipString());
        Task callTask = this.mFunction.getHttpsCallable("getWeather")
                .call(data)
                .addOnSuccessListener(task -> {
                    try {
                        //TODO: unterminated object at character 15 of {Error=Cannot read property '0' of undefined}
                        JSONObject object = new JSONObject(task.getData().toString());
                        location.setWeather(this.parseWeather(object));

                    } catch (JSONException e) {
                        e.printStackTrace();

                        location.setWeather(new Weather());
                    }
                });
        if (callback != null) {
            callTask.continueWith(result -> callback.apply(result));
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public Weather parseWeather(JSONObject object) {
        Weather weather = new Weather();
        try {
            weather.setWeather(parseOneForecast(object));
            long unix_Secons = object.getLong("sunset");
            Date date = new Date(unix_Secons * 1000L);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z");
            sdf.setTimeZone(TimeZone.getTimeZone("MEZ"));
            weather.setSunset(date.toInstant().atZone(ZoneId.systemDefault()).toLocalTime());
            unix_Secons = object.getLong("sunrise");
            date = new Date(unix_Secons * 1000L);
            weather.setSunrise(date.toInstant().atZone(ZoneId.systemDefault()).toLocalTime());

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return weather;
    }

    public void callGetForecastCallback(Location location, Function<Task, Object> callback) {
        Map<String, String> data = new HashMap<>();
        data.put("locationID", location.getId());
        data.put("zip", location.getZipString());
        Task callTask = this.mFunction.getHttpsCallable("getForecast")
                .call(data)
                .addOnSuccessListener(task -> {
                    try {
                        //TODO: unterminated object at character 15 of {Error=Cannot read property '0' of undefined}
                        JSONObject object = new JSONObject(task.getData().toString());
                        ArrayList<Forecast> forecast = new ArrayList<>();
                        forecast.add(location.getWeather().getWeather());
                        forecast.addAll(parseForecast(object.getJSONArray("forcast")));
                        location.setForecast(forecast);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        location.setWeather(new Weather());
                    }
                });
        if (callback != null) {
            callTask.continueWith(result -> callback.apply(result));
        }

    }

    public ArrayList<Forecast> parseForecast(JSONArray array) {

        ArrayList<Forecast> forecast = new ArrayList<>();
        try {
            for (int i = 0; i < array.length(); i++) {
                JSONObject obj = array.getJSONObject(i);
                forecast.add(parseOneForecast(obj));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return forecast;
    }

    public Forecast parseOneForecast(JSONObject object) {
        Forecast forecast = new Forecast();
        try {
            try {
                long unixTime = object.getLong("dt");
                Instant instant = Instant.ofEpochSecond(unixTime);
                forecast.setTime(LocalDateTime.ofInstant(instant, ZoneOffset.UTC));
            } catch (Exception e) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                forecast.setTime(LocalDateTime.parse(object.getString("dt"), formatter));
            }

            forecast.setDescription(object.getString("description"));
            forecast.setTemp(object.getDouble("temp"));

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return forecast;
    }

    public int weatherDescriptionIcon(LocalTime sunrise, LocalTime sunset, Forecast forecast) {
        if (sunrise.isBefore(forecast.getTime().toLocalTime()) && sunset.isAfter(forecast.getTime().toLocalTime())) {
            switch (forecast.getDescription()) {
                case "clear sky":
                case "sunny":
                    return R.string.wi_day_sunny;
                case "scattered clouds":
                case "overcast clouds":
                case "broken clouds":
                case "few clouds":
                    return R.string.wi_day_cloudy;
                case "clouds":
                    return R.string.wi_cloud;
                case "light rain":
                    return R.string.wi_raindrops;
                case "rain":
                    return R.string.wi_day_rain;
                case "fog":
                case "mist":
                    return R.string.wi_day_fog;
                case "light snow":
                    return R.string.wi_day_snow;
                default:
                    return R.string.wi_alien;
            }
        } else {
            switch (forecast.getDescription()) {
                case "clear sky":
                case "sunny":
                    return R.string.wi_night_clear;
                case "scattered clouds":
                case "overcast clouds":
                case "broken clouds":
                case "few clouds":
                    return R.string.wi_night_alt_cloudy;
                case "clouds":
                    return R.string.wi_cloud;
                case "light rain":
                    return R.string.wi_raindrops;
                case "rain":
                    return R.string.wi_night_alt_rain;
                case "fog":
                case "mist":
                    return R.string.wi_night_fog;
                case "light snow":
                    return R.string.wi_night_snow;
                default:
                    return R.string.wi_alien;
            }
        }
    }
//endregion

    //region Devices
    public void callGetDevicesCallback(Location location, Function<Task, Object> callback) {
        Map<String, String> data = new HashMap<>();
        data.put("locationID", location.getId());
        data.put("email", this.user.getFirebaseUser().getEmail());

        Task callTask = this.mFunction
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
        if (callback != null) {
            callTask.continueWith(result -> callback.apply(result));
        }
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
                        //TODO: leerzeichen Waschmasichine
                        JSONObject obj = new JSONObject(result.getData().toString());
                        JSONObject object = obj.getJSONArray("Consumers").getJSONObject(0);
                        device.setConsumption(object.getDouble("consumption"));
                        device.switchState(object.getString("state"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                        device.setConsumption(0.0);
                    }
                })
                .addOnFailureListener(e -> e.printStackTrace());
    }
    //endregion

    //region Producer
    public void callGetGeneratorCallback(Location location, CountDownLatch countDownLatch, Function<Task, Object> callback) {
        Map<String, String> data = new HashMap<>();
        data.put("email", this.user.getFirebaseUser().getEmail());
        data.put("locationID", location.getId());
        Task callTask = this.mFunction
                .getHttpsCallable("getGenerators")
                .call(data)
                .addOnSuccessListener(result -> {
                    try {
                        JSONObject object = new JSONObject(result.getData().toString());
                        JSONArray array = object.getJSONArray("Generators");
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject obj = array.getJSONObject(i);
                            JSONObject act = obj.getJSONObject("Generator");
                            this.parseProducer(location, act);
                        }
                        if (countDownLatch != null) {
                            countDownLatch.countDown();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                })
                .addOnFailureListener(e -> e.printStackTrace());
        if (callback != null) {
            callTask.continueWith(result -> callback.apply(result));
        }
    }

    public Producer parseProducer(Location location, JSONObject object) {
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

                            producer.setCurrentlyProduced(act.getDouble("value"));
                        } catch (JSONException e) {
                            producer.setCurrentlyProduced(0.0);
                        }
                        location.addProducer(producer);
                    });
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return producer;
    }
//endregion

    //region Companies
    public void callCompanies() {
        callCompaniesCallback(null);
    }

    public void callCompaniesCallback(Function<Task, Object> callback) {
        FirebaseFunctions mFunctions = FirebaseFunctions.getInstance();
        Task companiesTask = mFunctions
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
        if (callback != null) {
            companiesTask.continueWith(result -> callback.apply(result));
        }
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
    //endregion
}
