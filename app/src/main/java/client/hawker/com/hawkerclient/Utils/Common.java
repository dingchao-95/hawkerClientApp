package client.hawker.com.hawkerclient.Utils;

import client.hawker.com.hawkerclient.Database.DataSource.CartRepository;
import client.hawker.com.hawkerclient.Database.Local.CartDatabase;
import client.hawker.com.hawkerclient.Model.Category;
import client.hawker.com.hawkerclient.Model.User;
import client.hawker.com.hawkerclient.Retrofit.IHawkerAPI;
import client.hawker.com.hawkerclient.Retrofit.RetrofitClient;

public class Common {

    //in emulator localhost is 10.0.2.2
    private static final String BASE_URL = "http://10.0.2.2/hawker/";


    public static User currentUser = null;
    public static Category currentCategory = null;

    //Hold Field
    public static int mealTakeaway = -1; //No choose = -1(error), 0 : eating here, 1 : takeaway

    //Local db
    public static CartDatabase cartDatabase;
    public static CartRepository cartRepository;


    public static IHawkerAPI getAPI()
    {
        return RetrofitClient.getClient(BASE_URL).create(IHawkerAPI.class);
    }
}
