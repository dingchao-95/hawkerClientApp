package client.hawker.com.hawkerclient.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.util.List;

import client.hawker.com.hawkerclient.Database.ModelDB.Cart;
import client.hawker.com.hawkerclient.Interface.IItemClickListener;
import client.hawker.com.hawkerclient.Model.Food;
import client.hawker.com.hawkerclient.R;
import client.hawker.com.hawkerclient.Utils.Common;

public class FoodAdapter extends RecyclerView.Adapter<FoodViewHolder>{

    Context context;
    List<Food> foodList;

    public FoodAdapter(Context context, List<Food> foodList) {
        this.context = context;
        this.foodList = foodList;
    }

    @NonNull
    @Override
    public FoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.food_item_layout,null);
        return new FoodViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull FoodViewHolder holder, final int position) {

        holder.txt_price.setText(new StringBuilder("$").append(foodList.get(position).Price));
        holder.txt_food_name.setText(foodList.get(position).Name);

        holder.btn_add_to_cart.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v)
            {
                showAddToCartDialog(position);
            }
        });

        Picasso.with(context)
                .load(foodList.get(position).Link)
                .into(holder.img_product);

        holder.setiItemClickListener(new IItemClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context,"Clicked",Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showAddToCartDialog(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View itemView = LayoutInflater.from(context)
                .inflate(R.layout.add_to_cart_layout,null);

        //view
        ImageView img_product_dialog = (ImageView)itemView.findViewById(R.id.img_cart_product);
        final ElegantNumberButton txt_count = (ElegantNumberButton)itemView.findViewById(R.id.txt_count);
        TextView txt_product_dialog = (TextView)itemView.findViewById(R.id.txt_cart_product_name);

        EditText edt_comment = (EditText)itemView.findViewById(R.id.edt_comment);

        RadioButton rdi_standard = (RadioButton)itemView.findViewById(R.id.rdi_standard);
        RadioButton rdi_more = (RadioButton)itemView.findViewById(R.id.rdi_more);

        rdi_standard.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                {
                    Common.mealTakeaway = 0;
                }
            }
        });

        rdi_more.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                {
                    Common.mealTakeaway = 1;
                }
            }
        });

        //set data
        Picasso.with(context)
                .load(foodList.get(position).Link)
                .into(img_product_dialog);

        txt_product_dialog.setText(foodList.get(position).Name);

        builder.setView(itemView);
        builder.setNegativeButton("ADD TO CART", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if(Common.mealTakeaway == -1)
                {
                    Toast.makeText(context, "Please choose your options", Toast.LENGTH_SHORT).show();
                    return;
                }



                showConfirmDialog(position,txt_count.getNumber());
                dialog.dismiss();
            }
        });

        builder.show();
    }

    private void showConfirmDialog(final int position, final String number) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            View itemView = LayoutInflater.from(context)
                    .inflate(R.layout.confirm_add_to_cart_layout,null);

            //view
        ImageView img_product_dialog = (ImageView)itemView.findViewById(R.id.img_product);
        final TextView txt_product_dialog = (TextView)itemView.findViewById(R.id.txt_cart_product_name);
        final TextView txt_product_price = (TextView)itemView.findViewById(R.id.txt_cart_product_price);

        //set data
        Picasso.with(context)
                .load(foodList.get(position).Link)
                .into(img_product_dialog);

        txt_product_dialog.setText(new StringBuilder(foodList.get(position).Name).append(" x")
        .append(number)
        .append(Common.mealTakeaway == 0 ? " Having here":" Takeaway").toString());

        double price = (Double.parseDouble(foodList.get(position).Price)* Double.parseDouble(number));

        if(Common.mealTakeaway == 1)
        {
            price+=1.0;
        }

        txt_product_price.setText(new StringBuilder("$").append(price));

        final double finalPrice = price;
        builder.setNegativeButton("Confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                    //Add to SQLite
                    //add later
                    dialog.dismiss();

                    try {
                        //Cart items
                        Cart cartItem = new Cart();
                        cartItem.name = txt_product_dialog.getText().toString();
                        cartItem.amount = Integer.parseInt(number);
                        cartItem.price = finalPrice;
                        cartItem.link = foodList.get(position).Link;

                        //Add to db
                        Common.cartRepository.insertToCart(cartItem);
                        Log.d("DB_DEBUG", new Gson().toJson(cartItem));

                        Toast.makeText(context, "Successfully added", Toast.LENGTH_SHORT).show();
                    }
                    catch (Exception ex){
                        Toast.makeText(context, ex.getMessage(), Toast.LENGTH_SHORT).show();
                    }

            }
        });

        builder.setView(itemView);
        builder.show();
    }


    @Override
    public int getItemCount() {
        return foodList.size();
    }
}
