package client.hawker.com.hawkerclient;

import android.content.DialogInterface;
import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import client.hawker.com.hawkerclient.Adapter.CartAdapter;
import client.hawker.com.hawkerclient.Adapter.FavouriteAdapter;
import client.hawker.com.hawkerclient.Database.ModelDB.Cart;
import client.hawker.com.hawkerclient.Database.ModelDB.Favourite;
import client.hawker.com.hawkerclient.Retrofit.IHawkerAPI;
import client.hawker.com.hawkerclient.Utils.Common;
import client.hawker.com.hawkerclient.Utils.RecyclerItemTouchHelper;
import client.hawker.com.hawkerclient.Utils.RecyclerItemTouchHelperListener;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CartActivity extends AppCompatActivity implements RecyclerItemTouchHelperListener{

    RecyclerView recycler_cart;
    Button btn_place_order;

    List<Cart> cartList = new ArrayList<>();

    CompositeDisposable compositeDisposable;

    IHawkerAPI mService;

    CartAdapter cartAdapter;

    RelativeLayout rootLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        compositeDisposable = new CompositeDisposable();

        mService = Common.getAPI();

        recycler_cart = (RecyclerView)findViewById(R.id.recycler_cart);
        recycler_cart.setLayoutManager(new LinearLayoutManager(this));
        recycler_cart.setHasFixedSize(true);

        ItemTouchHelper.SimpleCallback simpleCallback = new RecyclerItemTouchHelper(0,ItemTouchHelper.LEFT,this);
        new ItemTouchHelper(simpleCallback).attachToRecyclerView(recycler_cart);

        btn_place_order = (Button)findViewById(R.id.btn_place_order);
        btn_place_order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                placeOrder();
            }
        });

        rootLayout = (RelativeLayout)findViewById(R.id.rootLayout);

        loadCartItems();
    }

    private void placeOrder() {
        //create dialog to show order submission
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Submit Order");

        View submit_order_layout = LayoutInflater.from(this).inflate(R.layout.submit_order_layout,null);

        final EditText edt_comment = (EditText)submit_order_layout.findViewById(R.id.edt_comment);

        builder.setView(submit_order_layout);

        builder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).setPositiveButton("submit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                final String orderComment = edt_comment.getText().toString();

                //submit orders
                compositeDisposable.add(
                        Common.cartRepository.getCartItems()
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe(new Consumer<List<Cart>>() {
                            @Override
                            public void accept(List<Cart> carts) throws Exception {
                                sendOrderToServer(Common.cartRepository.sumPrice(),
                                        carts,orderComment);
                            }
                        })
                );
            }
        });

        builder.show();
    }

    private void sendOrderToServer(float sumPrice, List<Cart> carts, String orderComment) {
            if(carts.size() > 0)
            {
                String orderDetail = new Gson().toJson(carts);

                mService.submitOrder(sumPrice,orderDetail,orderComment,Common.currentUser.getPhone())
                        .enqueue(new Callback<String>() {
                            @Override
                            public void onResponse(Call<String> call, Response<String> response) {
                                Toast.makeText(CartActivity.this, "submit orders", Toast.LENGTH_SHORT).show();

                                //clear the cart once confirmed
                                Common.cartRepository.emptyCart();
                            }

                            @Override
                            public void onFailure(Call<String> call, Throwable t) {
                                Log.e("ERROR",t.getMessage());
                            }
                        });
            }
    }

    private void loadCartItems() {
        compositeDisposable.add(
                Common.cartRepository.getCartItems()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Consumer<List<Cart>>() {
                    @Override
                    public void accept(List<Cart> carts) throws Exception {
                        displayCartItems(carts);
                    }
                })
        );

    }

    private void displayCartItems(List<Cart> carts)
    {
        cartList = carts;
        cartAdapter = new CartAdapter(this,carts);
        recycler_cart.setAdapter(cartAdapter);
    }

    //Ctrl O

    @Override
    protected void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }

    @Override
    protected void onStop() {
        compositeDisposable.clear();
        super.onStop();
    }



    @Override
    protected void onResume() {
        super.onResume();
        loadCartItems();
    }

    @Override
    public void onSwipe(RecyclerView.ViewHolder viewHolder, int direction, int position) {
        if(viewHolder instanceof CartAdapter.CartViewHolder)
        {
            String name = cartList.get(viewHolder.getAdapterPosition()).name;

            final Cart deletedItem = cartList.get(viewHolder.getAdapterPosition());
            final int deletedIndex = viewHolder.getAdapterPosition();

            //Delete items from adapter
            cartAdapter.removeItem(deletedIndex);
            //delete item from roomdb
            Common.cartRepository.deleteCartItems(deletedItem);

            cartAdapter.updateItems(cartList);

            Snackbar snackbar = Snackbar.make(rootLayout,new StringBuilder(name).append(" removed from favourites").toString(),
                    Snackbar.LENGTH_LONG);
            snackbar.setAction("UNDO", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    cartAdapter.restoreItem(deletedItem,deletedIndex);
                    Common.cartRepository.insertToCart(deletedItem);
                }
            });
            snackbar.setActionTextColor(Color.YELLOW);
            snackbar.show();
        }
    }


}
