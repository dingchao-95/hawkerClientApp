package client.hawker.com.hawkerclient.Database.Local;

import android.arch.persistence.db.SupportSQLiteOpenHelper;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.DatabaseConfiguration;
import android.arch.persistence.room.InvalidationTracker;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;
import android.support.annotation.NonNull;

import client.hawker.com.hawkerclient.Database.ModelDB.Cart;
import client.hawker.com.hawkerclient.Database.ModelDB.Favourite;

@Database(entities = {Cart.class, Favourite.class}, version = 1)
public abstract class HawkerRoomDatabase extends RoomDatabase {

    public abstract CartDAO cartDAO();
    public abstract FavouriteDAO favouriteDAO();

    private static HawkerRoomDatabase instance;

    public static HawkerRoomDatabase getInstance(Context context){
        if(instance == null)
            instance = Room.databaseBuilder(context,HawkerRoomDatabase.class,"EDMT_DrinkShopDB")
                    .allowMainThreadQueries()
                    .build();
        return instance;
    }



}
