package client.hawker.com.hawkerclient.Adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import client.hawker.com.hawkerclient.Interface.IItemClickListener;
import client.hawker.com.hawkerclient.R;


public class CategoryViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    ImageView img_product;
    TextView txt_menu_name;

    IItemClickListener iItemClickListener;

    public void setiItemClickListener(IItemClickListener iItemClickListener) {
        this.iItemClickListener = iItemClickListener;
    }

    public CategoryViewHolder(View itemView) {
        super(itemView);

        img_product = (ImageView)itemView.findViewById(R.id.image_product);
        txt_menu_name = (TextView)itemView.findViewById(R.id.txt_menu_name);

        itemView.setOnClickListener(this);

    }

    @Override
    public void onClick(View v)
    {
        iItemClickListener.onClick(v);
    }
}
