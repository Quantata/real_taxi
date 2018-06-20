package mobileapp.taxiproject.Network;

import mobileapp.taxiproject.Lpr_result;
import mobileapp.taxiproject.TexiResult;
import mobileapp.taxiproject.TexiResult;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface NetworkService {
//    @Multipart
//    @POST("/lpr")
//    Call<> getTaxiResult(@Part("auth_key") RequestBody auth_key,
//                                   @Part MultipartBody.Part file);

    @Multipart
    @POST("/lpr")
    Call<Lpr_result> getTexiResult(@Part("auth_key") RequestBody auth_key,
                                   @Part MultipartBody.Part file);
}
