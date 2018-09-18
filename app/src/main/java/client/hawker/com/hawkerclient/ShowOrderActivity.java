package client.hawker.com.hawkerclient;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import java.util.List;

import client.hawker.com.hawkerclient.Adapter.OrderAdapter;
import client.hawker.com.hawkerclient.Model.Order;
import client.hawker.com.hawkerclient.Retrofit.IHawkerAPI;
import client.hawker.com.hawkerclient.Utils.Common;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class ShowOrderActivity extends AppCompatActivity {

    IHawkerAPI mService;
    RecyclerView recycler_orders;
    CompositeDisposable compositeDisposable = new CompositeDisposable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_order);

        mService = Common.getAPI();

        recycler_orders = (RecyclerView)findViewById(R.id.recycler_orders);
        recycler_orders.setLayoutManager(new LinearLayoutManager(this));
        recycler_orders.setHasFixedSize(true);

        loadOrder();
    }

    private void loadOrder() {
        if(Common.currentUser != null) {
            compositeDisposable.add(mService.getOrder(Common.currentUser.getPhone(), "0")
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(new Consumer<List<Order>>() {
                        @Override
                        public void accept(List<Order> orders) throws Exception {
                            displayOrders(orders);
                        }
                    }));
        }
        else
        {
            Toast.makeText(this, "Please Login and try again.", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void displayOrders(List<Order> orders) {
        OrderAdapter adapter = new OrderAdapter(this,orders);
        recycler_orders.setAdapter(adapter);
    }


    @Override
    protected void onResume() {
        super.onResume();
        loadOrder();
    }

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
}
