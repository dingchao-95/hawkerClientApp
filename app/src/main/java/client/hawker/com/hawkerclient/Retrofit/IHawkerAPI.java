package client.hawker.com.hawkerclient.Retrofit;

import java.util.List;
import java.util.Observable;

import client.hawker.com.hawkerclient.Model.Banner;
import client.hawker.com.hawkerclient.Model.Category;
import client.hawker.com.hawkerclient.Model.CheckUserResponse;
import client.hawker.com.hawkerclient.Model.Food;
import client.hawker.com.hawkerclient.Model.Order;
import client.hawker.com.hawkerclient.Model.OrderResult;
import client.hawker.com.hawkerclient.Model.Token;
import client.hawker.com.hawkerclient.Model.User;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface IHawkerAPI {
    @FormUrlEncoded
    @POST("checkuser.php")
    Call<CheckUserResponse> checkExistsUser(@Field("phone") String phone);

    @FormUrlEncoded
    @POST("register.php")
    Call<User> registerNewUser(@Field("phone") String phone,
                               @Field("name") String name,
                               @Field("address") String address,
                               @Field("birthdate") String birthdate);

    @FormUrlEncoded
    @POST("getfood.php")
    io.reactivex.Observable<List<Food>> getFood(@Field("menuid") String menuID);

    @FormUrlEncoded
    @POST("getuser.php")
    Call<User> getUserInformation(@Field("phone") String phone);

    @GET("getbanner.php")
    io.reactivex.Observable<List<Banner>> getBanners();

    @GET("getmenu.php")
    io.reactivex.Observable<List<Category>> getMenu();

    @FormUrlEncoded
    @POST("submitorder.php")
    Call<OrderResult> submitOrder(@Field("price") float orderPrice,
                                  @Field("orderDetail") String orderDetail,
                                  @Field("comment") String comment ,
                                  @Field("phone") String phone);

    @FormUrlEncoded
    @POST("braintree/checkout.php")
    Call<String> payment(@Field("nonce") String nonce,
                             @Field("amount") String amount);

    @FormUrlEncoded
    @POST("getorders.php")
    io.reactivex.Observable<List<Order>> getOrder(@Field("userPhone") String userPhone,
                                                  @Field("status") String status);

    @FormUrlEncoded
    @POST("updatetoken.php")
    Call<String> updateToken(@Field("phone") String phone,
                             @Field("token") String token,
                             @Field("isServerToken") String isServerToken);

    @FormUrlEncoded
    @POST("cancelorder.php")
    Call<String> cancelOrder(@Field("orderId") String orderId,
                             @Field("userPhone") String userPhone);

    @FormUrlEncoded
    @POST("gettoken.php")
    Call<Token> getToken(@Field("phone") String phone,
                         @Field("isServerToken") String isServerToken);

}
