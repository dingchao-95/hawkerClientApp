package client.hawker.com.hawkerclient;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import java.util.List;

import client.hawker.com.hawkerclient.Adapter.FoodAdapter;
import client.hawker.com.hawkerclient.Model.Food;
import client.hawker.com.hawkerclient.Retrofit.IHawkerAPI;
import client.hawker.com.hawkerclient.Utils.Common;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class FoodActivity extends AppCompatActivity {

    IHawkerAPI mService;

    RecyclerView lst_food;

    TextView txt_banner_name;

    //Rxjavav2
    CompositeDisposable compositeDisposable = new CompositeDisposable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food);

        mService = Common.getAPI();


        lst_food = (RecyclerView)findViewById(R.id.recycler_food);
        lst_food.setLayoutManager(new GridLayoutManager(this, 2));
        lst_food.setHasFixedSize(true);

        txt_banner_name = (TextView)findViewById(R.id.txt_menu_name);
        txt_banner_name.setText(Common.currentCategory.Name);

        loadListFood(Common.currentCategory.ID);

    }

    private void loadListFood(String menuId)
    {
        compositeDisposable.add(mService.getFood(menuId)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Consumer<List<Food>>() {
                                @Override
                                public void accept(List<Food> foods) throws Exception {
                                    displayFoodList(foods);
                                }
                            }));
    }

    private void displayFoodList(List<Food> foods) {
        FoodAdapter adapter = new FoodAdapter(this,foods);
        lst_food.setAdapter(adapter);
    }
}
