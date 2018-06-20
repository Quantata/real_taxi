package mobileapp.taxiproject.Application;

import android.app.Application;
import mobileapp.taxiproject.Network.NetworkService;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApplicationController extends Application {
    private static ApplicationController instance;

    private static String baseUrl = "http://222.122.30.53:8000";


    private NetworkService networkService;
    public static ApplicationController getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        ApplicationController.instance = this;
        buildService();
    }

    public void buildService() {
        Retrofit.Builder builder = new Retrofit.Builder();
        Retrofit retrofit = builder
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        networkService = retrofit.create(NetworkService.class);
    }  public NetworkService getNetworkService() {
        return networkService;
    }

}
