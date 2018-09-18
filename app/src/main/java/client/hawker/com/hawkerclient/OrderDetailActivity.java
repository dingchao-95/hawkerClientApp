package client.hawker.com.hawkerclient;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.List;

import client.hawker.com.hawkerclient.Adapter.OrderDetailAdapter;
import client.hawker.com.hawkerclient.Database.ModelDB.Cart;
import client.hawker.com.hawkerclient.Utils.Common;

public class OrderDetailActivity extends AppCompatActivity {

    TextView txt_order_id, txt_order_price, txt_order_comment,txt_order_status;

    RecyclerView recycler_order_detail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);

        txt_order_id = (TextView)findViewById(R.id.txt_order_id);
        txt_order_price = (TextView)findViewById(R.id.txt_order_price);
        txt_order_comment = (TextView)findViewById(R.id.txt_order_comment);
        txt_order_status = (TextView)findViewById(R.id.txt_order_status);

        recycler_order_detail = (RecyclerView)findViewById(R.id.recycler_order_detail);
        recycler_order_detail.setLayoutManager(new LinearLayoutManager(this));
        recycler_order_detail.setHasFixedSize(true);

        txt_order_id.setText(new StringBuilder("#").append(Common.currentOrder.getOrderId()));
        txt_order_price.setText(new StringBuilder("$").append(Common.currentOrder.getOrderPrice()));
        txt_order_comment.setText(Common.currentOrder.getOrderComment());
        txt_order_status.setText(new StringBuilder("Order Status: ").append(Common.convertCodeToStatus(Common.currentOrder.getOrderStatus())));

        displayOrderDetail();
    }

    private void displayOrderDetail() {
        List<Cart> orderDetail = new Gson().fromJson(Common.currentOrder.getOrderDetail(),
                new TypeToken<List<Cart>>(){}.getType());

        recycler_order_detail.setAdapter(new OrderDetailAdapter(this,orderDetail));
    }
}
