package client.hawker.com.hawkerclient.Database.DataSource;

import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Query;

import java.util.List;

import client.hawker.com.hawkerclient.Database.ModelDB.Favourite;
import io.reactivex.Flowable;

public interface IFavouriteDataSource {


    Flowable<List<Favourite>> getFavItems();


    int isFavourite(int itemId);


    void insertFavourite(Favourite...favourites);

    void delete(Favourite favourite);


}
