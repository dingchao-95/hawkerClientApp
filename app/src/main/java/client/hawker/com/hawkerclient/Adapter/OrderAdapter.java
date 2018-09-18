package client.hawker.com.hawkerclient.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import client.hawker.com.hawkerclient.Model.Order;
import client.hawker.com.hawkerclient.R;
import client.hawker.com.hawkerclient.Utils.Common;

public class OrderAdapter extends RecyclerView.Adapter<OrderViewHolder> {

    Context context;
    List<Order> orderList;

    public OrderAdapter(Context context, List<Order> orderList) {
        this.context = context;
        this.orderList = orderList;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemview = LayoutInflater.from(context).inflate(R.layout.order_layout,parent,false);
        return new OrderViewHolder(itemview);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        holder.txt_order_id.setText(new StringBuilder("#").append(orderList.get(position).getOrderId()));
        holder.txt_order_price.setText(new StringBuilder("$").append(orderList.get(position).getOrderPrice()));
        holder.txt_order_comment.setText(orderList.get(position).getOrderComment());
        holder.txt_order_status.setText(new StringBuilder("Order Status: ").append(Common.convertCodeToStatus(orderList.get(position).getOrderStatus())));
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }
}
