package client.hawker.com.hawkerclient.Database.Local;

import java.util.List;

import client.hawker.com.hawkerclient.Database.DataSource.IFavouriteDataSource;
import client.hawker.com.hawkerclient.Database.ModelDB.Favourite;
import io.reactivex.Flowable;

public class FavouriteDataSource implements IFavouriteDataSource {

    public FavouriteDAO favouriteDAO;
    private static IFavouriteDataSource instance;

    public FavouriteDataSource(FavouriteDAO favouriteDAO) {
        this.favouriteDAO = favouriteDAO;
    }

    public static IFavouriteDataSource getInstance(FavouriteDAO favouriteDAO)
    {
        if(instance == null)
            instance = new FavouriteDataSource(favouriteDAO);
        return instance;
    }

    @Override
    public Flowable<List<Favourite>> getFavItems() {
        return favouriteDAO.getFavItems();
    }

    @Override
    public int isFavourite(int itemId) {
        return favouriteDAO.isFavourite(itemId);
    }

    @Override
    public void delete(Favourite favourite) {
        favouriteDAO.delete(favourite);
    }
}
