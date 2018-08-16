package client.hawker.com.hawkerclient.Database.Local;

import java.util.List;

import client.hawker.com.hawkerclient.Database.DataSource.ICartDataSource;
import client.hawker.com.hawkerclient.Database.ModelDB.Cart;
import io.reactivex.Flowable;

public class CartDataSource implements ICartDataSource {

    private CartDAO cartDAO;
    private static CartDataSource instance;

    public CartDataSource(CartDAO cartDAO)
    {
        this.cartDAO = cartDAO;
    }

    public static CartDataSource getInstance(CartDAO cartDAO)
    {
        if(instance == null)
            instance = new CartDataSource(cartDAO);
        return instance;
    }

    @Override
    public Flowable<List<Cart>> getCartItems() {
        return cartDAO.getCartItems();
    }

    @Override
    public Flowable<List<Cart>> getCartItemById(int cartItemId) {
        return cartDAO.getCartItemById(cartItemId);
    }

    @Override
    public int countCartItems() {
        return cartDAO.countCartItems();
    }

    @Override
    public void emptyCart() {
        cartDAO.emptyCart();
    }

    @Override
    public void insertToCart(Cart... carts) {
        cartDAO.insertToCart(carts);
    }

    @Override
    public void updateCart(Cart... carts) {
        cartDAO.updateCart(carts);
    }

    @Override
    public void deleteCartItems(Cart carts) {
        cartDAO.deleteCartItems(carts);
    }
}
