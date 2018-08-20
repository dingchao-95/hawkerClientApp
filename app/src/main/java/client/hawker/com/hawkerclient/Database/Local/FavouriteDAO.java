package client.hawker.com.hawkerclient.Database.Local;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Query;

import java.util.List;

import client.hawker.com.hawkerclient.Database.ModelDB.Favourite;
import io.reactivex.Flowable;

@Dao
public interface FavouriteDAO {

    @Query("SELECT * FROM Favourite")
    Flowable<List<Favourite>> getFavItems();

    @Query("SELECT EXISTS (SELECT 1 FROM Favourite WHERE id=:itemId)")
    int isFavourite(int itemId);

    @Delete
    void delete(Favourite favourite);
}
