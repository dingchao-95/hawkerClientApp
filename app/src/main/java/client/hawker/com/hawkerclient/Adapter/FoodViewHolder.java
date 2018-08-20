package client.hawker.com.hawkerclient.Adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import client.hawker.com.hawkerclient.Interface.IItemClickListener;
import client.hawker.com.hawkerclient.R;

public class FoodViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

    ImageView img_product;
    TextView txt_food_name,txt_price;

    IItemClickListener iItemClickListener;

    ImageView btn_add_to_cart,btn_favourites;

    public void setiItemClickListener(IItemClickListener iItemClickListener) {
        this.iItemClickListener = iItemClickListener;
    }

    public FoodViewHolder(View itemView) {
        super(itemView);

        img_product = (ImageView)itemView.findViewById(R.id.image_product);
        txt_food_name = (TextView)itemView.findViewById(R.id.txt_food_name);
        txt_price = (TextView)itemView.findViewById(R.id.txt_price);
        btn_add_to_cart = (ImageView) itemView.findViewById(R.id.btn_add_cart);
        btn_favourites = (ImageView) itemView.findViewById(R.id.btn_favourites);

        itemView.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {

        iItemClickListener.onClick(v);

    }
}
