package client.hawker.com.hawkerclient.Database.DataSource;

import java.util.List;

import client.hawker.com.hawkerclient.Database.ModelDB.Favourite;
import io.reactivex.Flowable;

public class FavouriteRepository implements IFavouriteDataSource {

    private IFavouriteDataSource favouriteDataSource;


    public FavouriteRepository(IFavouriteDataSource favouriteDataSource)
    {
        this.favouriteDataSource = favouriteDataSource;
    }

    private static FavouriteRepository instance;
    public static FavouriteRepository getInstance(IFavouriteDataSource favouriteDataSource)
    {
        if(instance == null)
            instance = new FavouriteRepository(favouriteDataSource);
        return instance;
    }

    @Override
    public Flowable<List<Favourite>> getFavItems() {
        return favouriteDataSource.getFavItems();
    }


    @Override
    public int isFavourite(int itemId) {
        return favouriteDataSource.isFavourite(itemId);
    }

    @Override
    public void delete(Favourite favourite) {
            favouriteDataSource.delete(favourite);
    }
}
