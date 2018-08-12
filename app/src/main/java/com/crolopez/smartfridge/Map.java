package com.crolopez.smartfridge;

import android.Manifest;
import android.app.Fragment;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Locale;

public class Map extends Fragment implements OnMapReadyCallback {
    private String TAG = "MAP";
    private MapView map_view;
    private GoogleMap map;
    private Context context;
    private LatLng map_lat = null;
    private static final String URL_API = "http://maps.googleapis.com/maps/api/geocode/xml";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        String location;
        View myFragmentView = inflater.inflate(R.layout.activity_map, container, false);

        context = MainActivity.get_application_context();
        MapsInitializer.initialize(this.getActivity());
        map_view = (MapView) myFragmentView.findViewById(R.id.id_mapview);
        map_view.onCreate(savedInstanceState);
        map_view.getMapAsync(this);

        // Get the initial location
        location = Setting.getDefaultPlace();
        if (!set_map_lat(location)) {
            Log.d(TAG, "The location '" + location + "'could not be set.");
            ToastMsg.show_toast_msg(context, "Could not find '" + location + "'.");
        }

        return myFragmentView;
    }

    private Address get_place_address(String place) {
        List<Address> addresses;
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());

        try {
            addresses = geocoder.getFromLocationName(place, 1);
            if (addresses.size() > 0) {
                return addresses.get(0);
            }
        } catch (IOException e) {
            Log.d(TAG, "Exception: get_place_address(): 1");
            e.printStackTrace();
        }
        return null;
    }

    private boolean set_map_lat(String place) {
        Address address = get_place_address(place);

        Log.d(TAG, "Trying to set '" + place + "' location.");
        if (address == null) {
            Log.d(TAG, "Exception: get_place_address(): 1");
            return false;
        }

        map_lat = new LatLng(address.getLatitude(), address.getLongitude());
        return true;
    }

    @Override
    public void onDetach() {
        Log.d(TAG, "Entering on onDetach().");
        super.onDetach();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "Entering on onCreate().");
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onPause() {
        Log.d(TAG, "Entering on onPause().");
        super.onPause();
        map_view.onPause();
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "Entering on onDestroy().");
        super.onDestroy();
        map_view.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        Log.d(TAG, "Entering on onSaveInstanceState().");
        super.onSaveInstanceState(outState);
        map_view.onSaveInstanceState(outState);
    }

    @Override
    public void onLowMemory() {
        Log.d(TAG, "Entering on onLowMemory().");
        super.onLowMemory();
        map_view.onLowMemory();
    }

    @Override
    public void onStart() {
        Log.d(TAG, "Entering on onStart().");
        super.onStart();
        map_view.onStart();
    }

    @Override
    public void onStop() {
        Log.d(TAG, "Entering on onStop().");
        super.onStop();
        map_view.onStop();
    }

    @Override
    public void onResume() {
        Log.d(TAG, "Entering on onResume().");
        super.onResume();
        map_view.onResume();
    }

    @Override
    public void onDestroyView() {
        Log.d(TAG, "Entering on onDestroyView().");
        super.onDestroyView();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d(TAG, "Entering on onMapReady().");
        if (map_lat == null) {
            Log.d(TAG, "The coordinates of the default place are not set.");
            return;
        }
        map = googleMap;
        map.setMinZoomPreference(Setting.getDefaultZoom());
        map.moveCamera(CameraUpdateFactory.newLatLng(map_lat));

        if (Setting.getGeolocation()) {
            if (!set_permissions()) {
                return;
            }
        }
    }

    private boolean set_permissions() {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return false;
        }
        map.setMyLocationEnabled(true);
        return true;
    }

}