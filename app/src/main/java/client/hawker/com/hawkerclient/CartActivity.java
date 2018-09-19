package client.hawker.com.hawkerclient;

import android.content.DialogInterface;
import android.content.Intent;
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

import com.braintreepayments.api.dropin.DropInActivity;
import com.braintreepayments.api.dropin.DropInRequest;
import com.braintreepayments.api.dropin.DropInResult;
import com.braintreepayments.api.models.PaymentMethodNonce;
import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.TextHttpResponseHandler;
import com.paypal.android.sdk.onetouch.core.Result;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import client.hawker.com.hawkerclient.Adapter.CartAdapter;
import client.hawker.com.hawkerclient.Adapter.FavouriteAdapter;
import client.hawker.com.hawkerclient.Database.ModelDB.Cart;
import client.hawker.com.hawkerclient.Database.ModelDB.Favourite;
import client.hawker.com.hawkerclient.Model.DataMessage;
import client.hawker.com.hawkerclient.Model.MyResponse;
import client.hawker.com.hawkerclient.Model.OrderResult;
import client.hawker.com.hawkerclient.Model.Token;
import client.hawker.com.hawkerclient.Retrofit.IFCMService;
import client.hawker.com.hawkerclient.Retrofit.IHawkerAPI;
import client.hawker.com.hawkerclient.Utils.Common;
import client.hawker.com.hawkerclient.Utils.RecyclerItemTouchHelper;
import client.hawker.com.hawkerclient.Utils.RecyclerItemTouchHelperListener;
import cz.msebera.android.httpclient.Header;
import dmax.dialog.SpotsDialog;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CartActivity extends AppCompatActivity implements RecyclerItemTouchHelperListener{

    private static final int PAYMENT_REQUEST_CODE = 7777;
    RecyclerView recycler_cart;
    Button btn_place_order;

    List<Cart> cartList = new ArrayList<>();

    CompositeDisposable compositeDisposable;

    IHawkerAPI mService;
    IHawkerAPI mServiceScalar;

    CartAdapter cartAdapter;

    RelativeLayout rootLayout;

    //global string variable
    String token,amount,orderComment;
    HashMap<String,String> params;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        compositeDisposable = new CompositeDisposable();

        mService = Common.getAPI();
        mServiceScalar = Common.getScalarsAPI();

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

        loadToken();
    }

    private void loadToken() {

        final android.app.AlertDialog waitingDialog = new SpotsDialog(CartActivity.this);
        waitingDialog.show();
        waitingDialog.setMessage("Please hold on...");

        AsyncHttpClient client = new AsyncHttpClient();
        client.get(Common.API_TOKEN_URL, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                waitingDialog.dismiss();

                btn_place_order.setEnabled(false);
                Toast.makeText(CartActivity.this, throwable.getMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                    waitingDialog.dismiss();

                    token = responseString;
                    btn_place_order.setEnabled(true);
            }
        });
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
                orderComment = edt_comment.getText().toString();

                //payment
                DropInRequest dropInRequest = new DropInRequest().clientToken(token);
                startActivityForResult(dropInRequest.getIntent(CartActivity.this),PAYMENT_REQUEST_CODE);


            }
        });

        builder.show();
    }

    //Ctril O


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == PAYMENT_REQUEST_CODE)
        {
            if(resultCode == RESULT_OK)
            {
                DropInResult result = data.getParcelableExtra(DropInResult.EXTRA_DROP_IN_RESULT);
                PaymentMethodNonce nonce = result.getPaymentMethodNonce();
                String strNonce = nonce.getNonce();

                if(Common.cartRepository.sumPrice() > 0)
                {
                    amount = String.valueOf(Common.cartRepository.sumPrice());
                    params = new HashMap<>();

                    params.put("amount",amount);
                    params.put("nonce", strNonce);

                    sendPayment();
                }
                else
                {
                    Toast.makeText(this, "Payment is 0", Toast.LENGTH_SHORT).show();
                }
            }
            else if(resultCode == RESULT_CANCELED)
            {
                Toast.makeText(this, "Payment cancelled.", Toast.LENGTH_SHORT).show();
            }
            else
            {
                Exception error = (Exception)data.getSerializableExtra(DropInActivity.EXTRA_ERROR);
                Log.e("client error", error.getMessage());
            }
        }
    }

    private void sendPayment() {
        mServiceScalar.payment(params.get("nonce"),params.get("amount"))
                .enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        if(response.body().toString().contains("Successful"))
                        {
                            Toast.makeText(CartActivity.this, "Transaction successful", Toast.LENGTH_SHORT).show();

                            //submit payment code
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
                        else
                        {
                            Toast.makeText(CartActivity.this, "Transaction failed", Toast.LENGTH_SHORT).show();
                        }

                        Log.d("EDMT_INFO", response.body());
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        Log.d("EDMT_INFO", t.getMessage());
                    }
                });

    }

    private void sendOrderToServer(float sumPrice, List<Cart> carts, String orderComment) {
            if(carts.size() > 0)
            {
                String orderDetail = new Gson().toJson(carts);

                mService.submitOrder(sumPrice,orderDetail,orderComment,Common.currentUser.getPhone())
                        .enqueue(new Callback<OrderResult>() {
                            @Override
                            public void onResponse(Call<OrderResult> call, Response<OrderResult> response) {

                                sendNotificationToServer(response.body());
                            }

                            @Override
                            public void onFailure(Call<OrderResult> call, Throwable t) {
                                Log.e("ERROR",t.getMessage());
                            }
                        });
            }
    }

    private void sendNotificationToServer(final OrderResult orderResult) {
        //get server token
        mService.getToken("Server_App_01","1")
        .enqueue(new Callback<Token>() {
            @Override
            public void onResponse(Call<Token> call, Response<Token> response) {
                //when token is received send notification to this token
                Map<String,String> contentSend = new HashMap<>();
                contentSend.put("title","HawkerApp");
                contentSend.put("message","You have a new order "+orderResult.getOrderId());
                DataMessage dataMessage = new DataMessage();
                if(response.body().getToken() != null)
                {
                    dataMessage.setTo(response.body().getToken());
                }
                dataMessage.setData(contentSend);

                IFCMService ifcmService = Common.getFCMService();
                ifcmService.sendNotification(dataMessage)
                        .enqueue(new Callback<MyResponse>() {
                            @Override
                            public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                                if(response.code() == 200)
                                {
                                    if(response.body().success == 1)
                                    {
                                        Toast.makeText(CartActivity.this, "Order is placed. thank you", Toast.LENGTH_SHORT).show();

                                        //clear cart;
                                        Common.cartRepository.emptyCart();
                                        finish();
                                    }
                                    else
                                    {
                                        Toast.makeText(CartActivity.this, "Sending of notification failed.", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }

                            @Override
                            public void onFailure(Call<MyResponse> call, Throwable t) {
                                Toast.makeText(CartActivity.this, ""+t.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });

            }

            @Override
            public void onFailure(Call<Token> call, Throwable t) {
                Toast.makeText(CartActivity.this, ""+t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


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
