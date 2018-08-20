package client.hawker.com.hawkerclient;

import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.List;

import client.hawker.com.hawkerclient.Adapter.FavouriteAdapter;
import client.hawker.com.hawkerclient.Database.DataSource.CartRepository;
import client.hawker.com.hawkerclient.Database.DataSource.FavouriteRepository;
import client.hawker.com.hawkerclient.Database.Local.CartDataSource;
import client.hawker.com.hawkerclient.Database.Local.FavouriteDataSource;
import client.hawker.com.hawkerclient.Database.Local.HawkerRoomDatabase;
import client.hawker.com.hawkerclient.Database.ModelDB.Favourite;
import client.hawker.com.hawkerclient.Utils.Common;
import client.hawker.com.hawkerclient.Utils.RecyclerItemTouchHelper;
import client.hawker.com.hawkerclient.Utils.RecyclerItemTouchHelperListener;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class FavouriteListActivity extends AppCompatActivity implements RecyclerItemTouchHelperListener{

    RecyclerView recycler_fav;


    CompositeDisposable compositeDisposable;

    RelativeLayout rootLayout;
    FavouriteAdapter favouriteAdapter;
    List<Favourite> localFavourites = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourite_list);

        compositeDisposable = new CompositeDisposable();

        rootLayout = (RelativeLayout)findViewById(R.id.rootLayout);

        recycler_fav = (RecyclerView)findViewById(R.id.recycler_fav);
        recycler_fav.setLayoutManager(new LinearLayoutManager(this));
        recycler_fav.setHasFixedSize(true);


        ItemTouchHelper.SimpleCallback simpleCallback = new RecyclerItemTouchHelper(0,ItemTouchHelper.LEFT,this);

        new ItemTouchHelper(simpleCallback).attachToRecyclerView(recycler_fav);

        initDB();

        loadFavouriteList();
    }

    private void initDB() {

        Common.hawkerRoomDatabase = HawkerRoomDatabase.getInstance(this);
        Common.cartRepository = CartRepository.getInstance(CartDataSource.getInstance(Common.hawkerRoomDatabase.cartDAO()));
        Common.favouriteRepository = FavouriteRepository.getInstance(FavouriteDataSource.getInstance(Common.hawkerRoomDatabase.favouriteDAO()));
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadFavouriteList();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        compositeDisposable.clear();
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
        localFavourites = favourites;
        favouriteAdapter = new FavouriteAdapter(this,favourites);
        recycler_fav.setAdapter(favouriteAdapter);


    }

    @Override
    public void onSwipe(RecyclerView.ViewHolder viewHolder, int direction, int position) {
        if(viewHolder instanceof FavouriteAdapter.FavouriteViewHolder)
        {
            String name = localFavourites.get(viewHolder.getAdapterPosition()).name;

            final Favourite deletedItem = localFavourites.get(viewHolder.getAdapterPosition());
            final int deletedIndex = viewHolder.getAdapterPosition();

            //Delete items from adapter
            favouriteAdapter.removeItem(deletedIndex);
            //delete item from roomdb
            Common.favouriteRepository.delete(deletedItem);

            Snackbar snackbar = Snackbar.make(rootLayout,new StringBuilder(name).append(" removed from favourites").toString(),
                    Snackbar.LENGTH_LONG);
            snackbar.setAction("UNDO", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    favouriteAdapter.restoreItem(deletedItem,deletedIndex);
                    Common.favouriteRepository.insertFavourite(deletedItem);
                }
            });
            snackbar.setActionTextColor(Color.YELLOW);
            snackbar.show();
        }

    }
}
