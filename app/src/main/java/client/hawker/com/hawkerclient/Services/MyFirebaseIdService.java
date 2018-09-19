package client.hawker.com.hawkerclient.Services;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import client.hawker.com.hawkerclient.Retrofit.IHawkerAPI;
import client.hawker.com.hawkerclient.Utils.Common;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyFirebaseIdService extends FirebaseInstanceIdService {

    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();
        if(Common.currentUser != null)
        {
            updateTokenToFireBase();
        }
    }

    private void updateTokenToFireBase() {
        IHawkerAPI mService = Common.getAPI();
        mService.updateToken(Common.currentUser.getPhone(),FirebaseInstanceId.getInstance().getToken(),"0")
        .enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                Log.d("DEBUG",response.toString());
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.d("DEBUG",t.getMessage());
            }
        });
    }
}
