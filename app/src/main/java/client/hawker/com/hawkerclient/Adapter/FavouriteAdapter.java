package client.hawker.com.hawkerclient.Adapter;

import android.content.Context;
import android.media.Image;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import client.hawker.com.hawkerclient.Database.ModelDB.Favourite;
import client.hawker.com.hawkerclient.R;

public class FavouriteAdapter extends RecyclerView.Adapter<FavouriteAdapter.FavouriteViewHolder> {



    Context context;
    List<Favourite> favouriteList;

    public FavouriteAdapter(Context context, List<Favourite> favouriteList) {
        this.context = context;
        this.favouriteList = favouriteList;
    }

    @NonNull
    @Override
    public FavouriteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(context).inflate(R.layout.fav_item_layout,parent,false);

        return new FavouriteViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull FavouriteViewHolder holder, int position) {
        Picasso.with(context)
                .load(favouriteList.get(position).link)
                .into(holder.img_product);

        holder.txt_price.setText(new StringBuilder("$").append(favouriteList.get(position).price));
        holder.txt_product_name.setText(favouriteList.get(position).name);

    }

    @Override
    public int getItemCount() {
        return favouriteList.size();
    }

    public class FavouriteViewHolder extends RecyclerView.ViewHolder
    {
        ImageView img_product;
        TextView txt_product_name,txt_price;

        public RelativeLayout view_background;
        public LinearLayout view_foreground;

        public FavouriteViewHolder(View itemView)
        {
            super(itemView);
            img_product = (ImageView)itemView.findViewById(R.id.img_product);
            txt_product_name = (TextView)itemView.findViewById(R.id.txt_product_name);
            txt_price = (TextView)itemView.findViewById(R.id.txt_price);

            view_background = (RelativeLayout)itemView.findViewById(R.id.view_background);
            view_foreground = (LinearLayout)itemView.findViewById(R.id.view_foreground);
        }
    }

    public void removeItem(int position)
    {
        favouriteList.remove(position);
        notifyItemRemoved(position);
    }

    public void restoreItem(Favourite item, int position)
    {
        favouriteList.add(position,item);
        notifyItemInserted(position);
    }
}
