package client.hawker.com.hawkerclient.Retrofit;

import client.hawker.com.hawkerclient.Model.DataMessage;
import client.hawker.com.hawkerclient.Model.MyResponse;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface IFCMService {
    @Headers(
            {

        "Content-Type:application/json",
            "Authorization:key=AAAAaN_5zJg:APA91bGcYtROL_hT1Hc3IuWgPYwYCksQsFoTZ6PMHQreGlQ41huveihYgZHcaNgD_CDEZvs2PSFnn46ZQx_zoQe_6_7mpIBORoezKpkd-Oapg7kmCblroWJzGEJo6J_d9bTut5cJS9dFlD9Nn0YlbklyEeepgWyYfw"
    }
    )
    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body DataMessage body);
}
