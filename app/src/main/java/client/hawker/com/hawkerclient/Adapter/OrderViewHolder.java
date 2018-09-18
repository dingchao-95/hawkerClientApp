package client.hawker.com.hawkerclient.Adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import org.w3c.dom.Text;

import client.hawker.com.hawkerclient.R;

public class OrderViewHolder extends RecyclerView.ViewHolder {

    public TextView txt_order_id, txt_order_price, txt_order_comment,txt_order_status;

    public OrderViewHolder(View itemView) {
        super(itemView);

        txt_order_id = (TextView)itemView.findViewById(R.id.txt_order_id);
        txt_order_price = (TextView)itemView.findViewById(R.id.txt_order_price);
        txt_order_comment = (TextView)itemView.findViewById(R.id.txt_order_comment);
        txt_order_status = (TextView)itemView.findViewById(R.id.txt_order_status);

    }



}
