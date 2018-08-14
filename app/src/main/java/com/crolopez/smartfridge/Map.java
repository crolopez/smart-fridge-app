package com.crolopez.smartfridge;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Map extends Fragment implements OnMapReadyCallback {
    private String TAG = "MAP";
    private String cache_pins;
    private String separator = "!=!";
    private LayoutInflater inflater;
    private View myFragmentView;
    private MapView map_view = null;
    private GoogleMap map;
    private Context context;
    private LatLng map_lat = null;
    private static final String URL_API = "http://maps.googleapis.com/maps/api/geocode/xml";
    private ImageButton button_zoom_more;
    private ImageButton button_zoom_less;
    private ImageButton map_type;
    private float zoom;
    private float zoom_inc;
    ArrayList<Pair<String,LatLng>> pin_list = null;
    /*
            *** Map types ***
            *    MAP_TYPE_NONE
            *    MAP_TYPE_NORMAL
            *    MAP_TYPE_SATELLITE
            *    MAP_TYPE_TERRAIN
            *    MAP_TYPE_HYBRID
            *****************
    */
    private int selected_map_type;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        String location;
        int status;

        Log.d(TAG, "Entering on onCreateView().");
        myFragmentView = inflater.inflate(R.layout.activity_map, container, false);
        this.inflater = inflater;
        context = MainActivity.get_application_context();
        cache_pins = MainActivity.get_application_cache_dir() + "pins.cache";
        status = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(context);

        if(status != ConnectionResult.SUCCESS){ // Google Play Services are not available
            ToastMsg.show_toast_msg(context, "Google Play Services are not available. Error: " + status);
        } else {
            selected_map_type = Setting.getMapType();
            zoom = Setting.getDefaultZoom();
            zoom_inc = Setting.getDefaultZoomInc();
            MapsInitializer.initialize(this.getActivity());
            map_view = (MapView) myFragmentView.findViewById(R.id.id_mapview);
            map_view.onCreate(savedInstanceState);
            map_view.getMapAsync(this);

            init_buttons();

            // Get the initial location
            location = Setting.getDefaultPlace();
            if (!set_map_lat(location)) {
                Log.d(TAG, "The location '" + location + "'could not be set.");
                ToastMsg.show_toast_msg(context, "Could not find '" + location + "'.");
            }
        }

        return myFragmentView;
    }

    private void init_pin_list() {
        if (pin_list == null) {
            String buffer;
            BufferedReader file_input;
            String []splitted;
            String str_lat, str_long, tittle;

            pin_list = new ArrayList<>();

            if (new File(cache_pins).exists()) {
                try {
                    file_input = new BufferedReader(new FileReader(cache_pins));
                } catch (FileNotFoundException e) {
                    Log.d(TAG, "Exception: init_pin_list(): 1");
                    e.printStackTrace();
                    return;
                }

                while (true) {
                    try {
                        buffer = file_input.readLine();
                    } catch (IOException e) {
                        Log.d(TAG, "Exception: init_pin_list(): 2");
                        e.printStackTrace();
                        return;
                    }

                    if (buffer == null) {
                        break;
                    }

                    Log.d(TAG, "Read '" + buffer + "' from pin list cache.");

                    splitted = buffer.split(separator);
                    tittle = splitted[0];
                    str_lat = splitted[1];
                    str_long = splitted[2];
                    pin_list.add(new Pair<>(tittle, new LatLng(Double.parseDouble(str_lat), Double.parseDouble(str_long))));
                }
            }
        }

        for (int i = 0; i < pin_list.size(); i++) {
            add_pin(pin_list.get(i).second, pin_list.get(i).first, false);
        }
    }

    private void map_click (final LatLng location) {
        View prompts_view;
        AlertDialog.Builder dialog_builder;
        AlertDialog alert_dialog;
        final EditText place_name;

        prompts_view = inflater.inflate(R.layout.activity_map_place, null);
        dialog_builder = new AlertDialog.Builder(getActivity());
        dialog_builder.setView(prompts_view);
        place_name = (EditText) prompts_view.findViewById(R.id.id_map_place_name);

        dialog_builder
                .setCancelable(false)
                .setPositiveButton("Add",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                String name = place_name.getText().toString();

                                if (name != null) {
                                    add_pin(location, name, true);
                                }
                            }
                        })
                .setNeutralButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                dialog.cancel();
                            }
                        });

        alert_dialog = dialog_builder.create();
        alert_dialog.show();
    }

    private void add_pin(LatLng location, String place_tittle, boolean store) {
        MarkerOptions new_mark = new MarkerOptions();
        new_mark.position(location);
        new_mark.title(place_tittle);
        map.addMarker(new_mark);

        if (store) {
            PrintWriter file_output;
            String buffer;

            pin_list.add(new Pair<>(place_tittle, location));

            try {
                file_output = new PrintWriter(new FileOutputStream(new File(cache_pins), true));
            } catch (FileNotFoundException e) {
                Log.d(TAG, "Exception: add_pin(): 1");
                e.printStackTrace();
                return;
            }

            buffer = place_tittle + separator +
                    location.latitude + separator +
                    location.longitude + separator;

            file_output.println(buffer);
            file_output.close();
        }
    }

    private void init_buttons() {
        button_zoom_more = (ImageButton) myFragmentView.findViewById(R.id.id_zoom_more);
        button_zoom_less = (ImageButton) myFragmentView.findViewById(R.id.id_zoom_less);
        map_type = (ImageButton) myFragmentView.findViewById(R.id.id_map_type);

        button_zoom_more.setImageResource(R.mipmap.map_icons_zoom_more);
        button_zoom_less.setImageResource(R.mipmap.map_icons_zoom_less);
        map_type.setImageResource(R.mipmap.map_icons_type);

        button_zoom_more.setOnClickListener(new View.OnClickListener() {
            //@Override
            public void onClick(View v) {
                zoom_in();
            }
        });
        button_zoom_less.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                zoom_out();
            }
        });
        map_type.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                change_map_type();
            }
        });
    }

    private void zoom_out() {
        Log.d(TAG, "Entering on zoom_out().");
        CameraPosition position = map.getCameraPosition();
        if (zoom > zoom_inc + 1) {
            zoom = zoom - zoom_inc;
            Log.d(TAG, "Setting " + zoom + " zoom...");
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(position.target, zoom));
        }
    }

    private void zoom_in() {
        Log.d(TAG, "Entering on zoom_in().");
        CameraPosition position = map.getCameraPosition();
        zoom = zoom + zoom_inc;
        Log.d(TAG, "Setting " + zoom + " zoom.");
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(position.target, zoom));
    }

    private void change_map_type() {
        Log.d(TAG, "Entering on change_map_type().");
        selected_map_type = (selected_map_type + 1) % 5;
        selected_map_type = (selected_map_type == 0) ? 1 : selected_map_type;
        Log.d(TAG, "Setting " + selected_map_type + " map type...");
        map.setMapType(selected_map_type);
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

        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener()  {
            @Override
            public void onMapClick(LatLng location) {
                map_click(location);
            }
        });

        map = googleMap;
        map.moveCamera(CameraUpdateFactory.newLatLng(map_lat));
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(map.getCameraPosition().target, zoom));
        map.setMapType(selected_map_type);

        init_pin_list();

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