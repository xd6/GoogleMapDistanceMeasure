package com.xd6.googlemapstoy;

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    public Marker m1,m2,mLast;

    protected ArrayList<Marker> markers;

    protected Toast distanceToast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        markers = new ArrayList<Marker>();
    }


    protected void addMarker(GoogleMap mMap,LatLng latLng, String title) {
        if(latLng == null)
            return;

        if(title == null || title.isEmpty())
            title = latLng.toString();

        MarkerOptions newM = new MarkerOptions().position(latLng).title(title);

        markers.add(mMap.addMarker(newM));

        updateDistance();
    }

    protected void removeMarker(Marker marker) {
        if(marker == null)
            return;

        markers.remove(marker);
        marker.remove();

        updateDistance();
    }

    public static double calculateDistance(LatLng from, LatLng to) {

        if(from == null || to == null)
            return 0.0;

        double R = 6372.8;

        double lat1 = from.latitude;
        double lat2 = to.latitude;

        double lon1 = from.longitude;
        double lon2 = to.longitude;

        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        lat1 = Math.toRadians(lat1);
        lat2 = Math.toRadians(lat2);

        double a = Math.pow(Math.sin(dLat / 2),2) + Math.pow(Math.sin(dLon / 2),2) * Math.cos(lat1) * Math.cos(lat2);
        double c = 2 * Math.asin(Math.sqrt(a));

        return Math.round(kmToMi(R * c));

    }
    public static double kmToMi(double km) {
        return km * 0.621371;
    }

    protected void updateDistance() {

        if(markers.size() <= 1)
            return;

        double totalDistance = 0.0;

        LatLng lastLatLng = null;
        for(Marker m : markers) {

            if(lastLatLng == null) {
                lastLatLng = m.getPosition();
                continue;
            }

            LatLng currLatLng = m.getPosition();

            totalDistance += calculateDistance(lastLatLng, currLatLng);

            lastLatLng = currLatLng;
        }
        DecimalFormat df = new DecimalFormat("#.#");
        df.setRoundingMode(RoundingMode.CEILING);

        if(distanceToast != null)
            distanceToast.cancel();
        distanceToast = Toast.makeText(this,String.format("%s%s",df.format(totalDistance),"mi"),Toast.LENGTH_LONG);
        distanceToast.show();
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;


        LatLng tampa = new LatLng(27.9681,82.4764);
        mMap.addMarker(new MarkerOptions().position(tampa).title("Marker in Tampa, FL"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(tampa));

        //Add Marker on clicking the map
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                MapsActivity.this.addMarker(mMap, latLng, null);
            }
        });

        //Remove Marker on clicking it
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                MapsActivity.this.removeMarker(marker);
                return false;
            }
        });
    }
}
