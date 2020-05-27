package Fragments;

import notifications.MyResponse;
import notifications.Sender;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
    @Headers(

            {
                    "Content-Type:application/json",
                    "Authorization:key= AAAAJQ54MfA:APA91bEl_tjsrdBTpKX7Ehre6Lh9_nrhfOOzkOktHvLoQhWLHIkGXzkzh0WC3Qw_lWRW3h9knr9jyTq8LAEn_UwLvaJb0gly6ESus5EHE6LJYUGsk36dL0ypHMZyWreqyOpi5id0ok13"

            }
    )
    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender body);
}
