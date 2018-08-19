package client.hawker.com.hawkerclient.Database.DataSource;

import java.util.List;

import client.hawker.com.hawkerclient.Database.ModelDB.Cart;
import io.reactivex.Flowable;

public interface ICartDataSource {

    Flowable<List<Cart>> getCartItems();
    Flowable<List<Cart>> getCartItemById(int cartItemId);
    int countCartItems();
    float sumPrice();
    void emptyCart();
    void insertToCart(Cart... carts);
    void updateCart(Cart... carts);
    void deleteCartItems(Cart carts);
}
