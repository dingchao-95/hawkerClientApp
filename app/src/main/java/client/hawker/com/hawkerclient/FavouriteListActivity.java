package client.hawker.com.hawkerclient;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Button;

import java.util.List;

import client.hawker.com.hawkerclient.Adapter.FavouriteAdapter;
import client.hawker.com.hawkerclient.Database.ModelDB.Favourite;
import client.hawker.com.hawkerclient.Utils.Common;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class FavouriteListActivity extends AppCompatActivity {

    RecyclerView recycler_fav;


    CompositeDisposable compositeDisposable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourite_list);

        compositeDisposable = new CompositeDisposable();

        recycler_fav = (RecyclerView)findViewById(R.id.recycler_fav);
        recycler_fav.setLayoutManager(new LinearLayoutManager(this));
        recycler_fav.setHasFixedSize(true);

        loadFavouriteList();
    }

    private void loadFavouriteList(){
        compositeDisposable.add(Common.favouriteRepository.getFavItems()
        .observeOn(AndroidSchedulers.mainThread())
        .subscribeOn(Schedulers.io())
        .subscribe(new Consumer<List<Favourite>>() {
            @Override
            public void accept(List<Favourite> favourites) throws Exception {
                displayFavouriteItem(favourites);
            }
        }));
    }

    private void displayFavouriteItem(List<Favourite> favourites) {
        FavouriteAdapter favouriteAdapter = new FavouriteAdapter(this,favourites);
        recycler_fav.setAdapter(favouriteAdapter);

    }
}
