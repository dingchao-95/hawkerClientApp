package client.hawker.com.hawkerclient.Utils;

import client.hawker.com.hawkerclient.Database.DataSource.CartRepository;
import client.hawker.com.hawkerclient.Database.DataSource.FavouriteRepository;
import client.hawker.com.hawkerclient.Database.Local.HawkerRoomDatabase;
import client.hawker.com.hawkerclient.Model.Category;
import client.hawker.com.hawkerclient.Model.Order;
import client.hawker.com.hawkerclient.Model.User;
import client.hawker.com.hawkerclient.Retrofit.IHawkerAPI;
import client.hawker.com.hawkerclient.Retrofit.RetrofitClient;
import client.hawker.com.hawkerclient.Retrofit.RetrofitScalarsClient;

public class Common {

    //in emulator localhost is 10.0.2.2
    public static final String BASE_URL = "http://192.168.1.22/hawker/";
    public static final String API_TOKEN_URL = "http://192.168.1.22/hawker/braintree/main.php";


    public static User currentUser = null;
    public static Category currentCategory = null;
    public static Order currentOrder = null;

    //Hold Field
    public static int mealTakeaway = -1; //No choose = -1(error), 0 : eating here, 1 : takeaway

    //Local db
    public static HawkerRoomDatabase hawkerRoomDatabase;
    public static CartRepository cartRepository;
    public static FavouriteRepository favouriteRepository;


    public static IHawkerAPI getAPI()
    {
        return RetrofitClient.getClient(BASE_URL).create(IHawkerAPI.class);
    }

    public static IHawkerAPI getScalarsAPI()
    {
        return RetrofitScalarsClient.getScalarsClient(BASE_URL).create(IHawkerAPI.class);
    }

    public static String convertCodeToStatus(int orderStatus) {
        switch (orderStatus)
        {
            case 0:
                return "Placed";
            case 1:
                return "Processing";
            case 2:
                return "Food is done";
            case -1:
                return "Cancelled";
                default:
                    return "Error in orders.";

        }
    }
}
