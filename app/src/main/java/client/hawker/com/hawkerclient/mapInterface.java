package client.hawker.com.hawkerclient;


import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class mapInterface extends Fragment implements OnMapReadyCallback , bannerFragment.OnFragmentInteractionListener{

    private static final String TAG = "mapInterface";
    private Boolean mLocationPermissionsGranted = false;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;

    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocation;
    private static final float DEFAULT_ZOOM = 15f;
    private Marker myMarker;

    public mapInterface() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_map_interface, container, false);

        getLocationPermission();

        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment = (SupportMapFragment)getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(final GoogleMap googleMap)
    {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        //LatLng sydney = new LatLng(-34, 151);
        //mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

        getDeviceLocation();
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) !=
                        PackageManager.PERMISSION_GRANTED)
        {
            return;
        }

        googleMap.setMyLocationEnabled(true);

        googleMap.addMarker(new MarkerOptions()
                .position(new LatLng(1.390470, 103.898831))
                .title("Hawker center here.")
                .snippet("Great crabs here!"));

        googleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                FragmentManager fragmentManager= getFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.mainLayout
                                ,new bannerFragment())
                        .commit();
            }
        });


    }


    private void initMap()
    {
        Log.d(TAG,"map is ready");
        SupportMapFragment mapFragment = (SupportMapFragment)getChildFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    private void getLocationPermission()
    {
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION};

        if(ContextCompat.checkSelfPermission(this.getActivity().getApplicationContext(),Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
        {
            if(ContextCompat.checkSelfPermission(this.getActivity().getApplicationContext(),Manifest.permission.ACCESS_COARSE_LOCATION)==PackageManager.PERMISSION_GRANTED)
            {
                mLocationPermissionsGranted = true;
                initMap();
            }
            else
            {
                ActivityCompat.requestPermissions(getActivity(),permissions,LOCATION_PERMISSION_REQUEST_CODE);
            }
        }
        else
        {
            ActivityCompat.requestPermissions(getActivity(),permissions,LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        mLocationPermissionsGranted = false;

        switch (requestCode)
        {
            case LOCATION_PERMISSION_REQUEST_CODE : {
                if(grantResults.length > 0 )
                {
                    for(int i = 0; i < grantResults.length; i++)
                    {
                        if(grantResults[i] == PackageManager.PERMISSION_GRANTED)
                        {
                            mLocationPermissionsGranted = false ;
                            return;
                        }
                    }
                    mLocationPermissionsGranted = true;
                    //initialize map
                    initMap();
                }
            }
        }

    }

    private void geoLocate()
    {
        Log.d(TAG,"geolocating...");

        Geocoder geocoder = new Geocoder(getActivity());
        List<Address> list = new ArrayList<>();

        if(list.size() > 0)
        {
            Address address = list.get(0);
            moveCamera(new LatLng(address.getLatitude(),address.getLongitude()),DEFAULT_ZOOM);
        }
    }

    private void getDeviceLocation(){
        Log.d(TAG,"getting device location....");

        mFusedLocation = LocationServices.getFusedLocationProviderClient(getActivity());

        try{
            if(mLocationPermissionsGranted)
            {
                Task location = mFusedLocation.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if(task.isSuccessful() && task.getResult() != null){
                            Log.d(TAG,"found location");
                            Location currentLocation = (Location)task.getResult();

                            moveCamera(new LatLng(currentLocation.getLatitude(),currentLocation.getLongitude()),
                                    DEFAULT_ZOOM);
                        }
                        else
                        {
                            Toast.makeText(getContext(), "unable to get location. please try again.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }

        }
        catch(SecurityException e){
            Log.e(TAG,"Security exception" + e.getMessage());
        }
    }

    private void moveCamera(LatLng latLng, float zoom){
        Log.d(TAG, latLng.latitude + ", " +latLng.longitude);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,zoom));




    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

}
