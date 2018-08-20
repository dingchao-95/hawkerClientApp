package client.hawker.com.hawkerclient;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.nex3z.notificationbadge.NotificationBadge;

import java.util.HashMap;
import java.util.List;

import client.hawker.com.hawkerclient.Adapter.CategoryAdapter;
import client.hawker.com.hawkerclient.Database.DataSource.CartRepository;
import client.hawker.com.hawkerclient.Database.DataSource.FavouriteRepository;
import client.hawker.com.hawkerclient.Database.Local.CartDataSource;
import client.hawker.com.hawkerclient.Database.Local.FavouriteDataSource;
import client.hawker.com.hawkerclient.Database.Local.HawkerRoomDatabase;
import client.hawker.com.hawkerclient.Model.Banner;
import client.hawker.com.hawkerclient.Model.Category;
import client.hawker.com.hawkerclient.Retrofit.IHawkerAPI;
import client.hawker.com.hawkerclient.Utils.Common;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link bannerFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link bannerFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class bannerFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    SliderLayout sliderLayout;

    IHawkerAPI mService;

    RecyclerView lst_menu;

    NotificationBadge badge;

    //Rxjavav2
    CompositeDisposable compositeDisposable = new CompositeDisposable();

    private OnFragmentInteractionListener mListener;

    public bannerFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment bannerFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static bannerFragment newInstance(String param1, String param2) {
        bannerFragment fragment = new bannerFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        mService = Common.getAPI();



    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_banner, container, false);


        //sliderLayout = (SliderLayout)getView().findViewById(R.id.slider);

        //get banner images
        getBannerImage();

        //get item images
        getMenu();

        initDB();

        return v;
    }

    private void initDB() {

        Common.hawkerRoomDatabase = HawkerRoomDatabase.getInstance(getContext());
        Common.cartRepository = CartRepository.getInstance(CartDataSource.getInstance(Common.hawkerRoomDatabase.cartDAO()));
        Common.favouriteRepository = FavouriteRepository.getInstance(FavouriteDataSource.getInstance(Common.hawkerRoomDatabase.favouriteDAO()));
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        sliderLayout = (SliderLayout)getView().findViewById(R.id.slider);
        lst_menu = (RecyclerView)getView().findViewById(R.id.lst_menu);
        lst_menu.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL,false));
        lst_menu.setHasFixedSize(true);


        //updateCartCount();
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

    private void getMenu()
    {
        compositeDisposable.add(mService.getMenu()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<Category>>() {
                    @Override
                    public void accept(List<Category> categories) throws Exception {
                        displayMenu(categories);
                    }
                }));
    }

    private void displayMenu(List<Category> categories)
    {
        CategoryAdapter adapter = new CategoryAdapter(getContext(),categories);
        lst_menu.setAdapter(adapter);
    }

    private void displayImage(List<Banner> banners){
        HashMap<String,String> bannerMap = new HashMap<>();
        for(Banner item:banners)
        {
            bannerMap.put(item.getName(),item.getLink());
        }

        for(String name:bannerMap.keySet())
        {
            TextSliderView textSliderView = new TextSliderView(getContext());
            textSliderView.description(name)
                    .image(bannerMap.get(name))
                    .setScaleType(BaseSliderView.ScaleType.Fit);

            sliderLayout.addSlider(textSliderView);
        }
    }

    @Override
    public void onDestroy() {
        compositeDisposable.dispose();
        super.onDestroy();
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    private void updateCartCount() {
        if(badge == null) return;
        if(isAdded())
        getActivity().runOnUiThread(new Runnable() {
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

    //on resume

    @Override
    public void onResume() {
        super.onResume();
        updateCartCount();
    }
}
