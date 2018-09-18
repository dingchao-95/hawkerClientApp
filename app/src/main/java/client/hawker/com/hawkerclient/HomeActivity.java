package client.hawker.com.hawkerclient;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.facebook.accountkit.AccountKit;
import com.nex3z.notificationbadge.NotificationBadge;

import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.List;

import client.hawker.com.hawkerclient.Database.DataSource.CartRepository;
import client.hawker.com.hawkerclient.Database.Local.CartDataSource;
import client.hawker.com.hawkerclient.Database.Local.HawkerRoomDatabase;
import client.hawker.com.hawkerclient.Model.Banner;
import client.hawker.com.hawkerclient.Retrofit.IHawkerAPI;
import client.hawker.com.hawkerclient.Utils.Common;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, bannerFragment.OnFragmentInteractionListener, favouriteFragment.OnFragmentInteractionListener {

    TextView txt_name, txt_phone;
    SliderLayout sliderLayout;

    IHawkerAPI mService;

    NotificationBadge badge;

    ImageView cart_icon;

    mapInterface mapFragment = new mapInterface();
    favouriteFragment favFrag = new favouriteFragment();

    //Rxjavav2
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //mService = Common.getAPI();

        //sliderLayout = (SliderLayout)findViewById(R.id.slider);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View headerView = navigationView.getHeaderView(0);
        txt_name = (TextView)headerView.findViewById(R.id.txt_name);
        txt_phone = (TextView)headerView.findViewById(R.id.txt_phone);

        //get info
        txt_name.setText(Common.currentUser.getName());
        txt_phone.setText(Common.currentUser.getPhone());

        android.support.v4.app.FragmentManager fragmentManager= getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.mainLayout
                        ,new mapInterface())
                .commit();

        //getBanner images
        Common.hawkerRoomDatabase = HawkerRoomDatabase.getInstance(this);
        Common.cartRepository = CartRepository.getInstance(CartDataSource.getInstance(Common.hawkerRoomDatabase.cartDAO()));

    }

    private void getBannerImage()
    {
        compositeDisposable.add(mService.getBanners()
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Consumer<List<Banner>>()
                            {
                                @Override
                                public void accept(List<Banner> banners) throws Exception {
                                    displayImage(banners);
                                }

                            }));
    }

    private void displayImage(List<Banner> banners){
        HashMap<String,String> bannerMap = new HashMap<>();
        for(Banner item:banners)
        {
            bannerMap.put(item.getName(),item.getLink());
        }

        for(String name:bannerMap.keySet())
        {
            TextSliderView textSliderView = new TextSliderView(this);
            textSliderView.description(name)
                            .image(bannerMap.get(name))
                            .setScaleType(BaseSliderView.ScaleType.Fit);

            sliderLayout.addSlider(textSliderView);
        }
    }

    @Override
    protected void onDestroy() {
        compositeDisposable.dispose();
        super.onDestroy();
    }


    //Exits the application when pressing the 'back' button in android
    boolean isBackClicked = false;

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if(isBackClicked) {
                super.onBackPressed();
                return;
            }
            this.isBackClicked = true;
            Toast.makeText(this, "Tap back again to exit the app.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_action_bar, menu);
        View view = menu.findItem(R.id.cart_menu).getActionView();
        badge = (NotificationBadge)view.findViewById(R.id.badge);
        cart_icon = (ImageView)view.findViewById(R.id.cart_icon);
        cart_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeActivity.this,CartActivity.class));
            }
        });
        updateCartCount();
        return true;
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.cart_menu) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Fragment fragment = null;

        if (id == R.id.nav_sign_out) {
            //Create dialog for logout
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Logging Out");
            builder.setMessage("Do you want to log out of the application?");

            builder.setNegativeButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    AccountKit.logOut();//Facebook API logout

                    //clear all intents
                    Intent intent = new Intent(HomeActivity.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                }
            });

            builder.setPositiveButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                }
            });
            //show dialog for exit
            builder.show();


        } else if (id == R.id.nav_map) {

            android.support.v4.app.FragmentManager manager = getSupportFragmentManager();
            manager.beginTransaction().replace(R.id.mainLayout,mapFragment).commit();
        }
        else if(id == R.id.nav_fav) {
            startActivity(new Intent(HomeActivity.this,FavouriteListActivity.class));

//            android.support.v4.app.FragmentManager manager = getSupportFragmentManager();
//            manager.beginTransaction().replace(R.id.mainLayout,favFrag).commit();

        }
        else if(id == R.id.nav_show_orders) {
            startActivity(new Intent(HomeActivity.this,ShowOrderActivity.class));
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    private void updateCartCount() {
        if(badge == null) return;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (Common.cartRepository.countCartItems() == 0)
                        badge.setVisibility(View.INVISIBLE);
                    else {
                        badge.setVisibility(View.VISIBLE);
                        badge.setText(String.valueOf(Common.cartRepository.countCartItems()));
                    }
                }
            });
    }

    //Ctrl O onResume

    @Override
    protected void onResume() {
        super.onResume();
        updateCartCount();
        isBackClicked = false;
    }


}
