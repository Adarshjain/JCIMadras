package com.jcimadras.jcimadras.Maps;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.jcimadras.jcimadras.Extras.Message;
import com.jcimadras.jcimadras.R;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,
        View.OnClickListener {

    private GoogleMap mMap;
    private double longitude;
    private double latitude;
    private PlaceAutocompleteFragment autocompleteFragment;
    private CardView search;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Button positive = (Button) findViewById(R.id.positive);
        Button negetive = (Button) findViewById(R.id.negative);
        positive.setOnClickListener(this);
        negetive.setOnClickListener(this);

        search = (CardView) findViewById(R.id.search);
        autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);


    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        search.setVisibility(View.VISIBLE);
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                LatLng latLng = place.getLatLng();
                latitude = latLng.latitude;
                longitude = latLng.longitude;
                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
            }

            @Override
            public void onError(Status status) {
                Message.ts(getApplicationContext(), "Error!!", getLayoutInflater(), findViewById(R.id.toastbg));
                Message.L("Maps Error", status.getStatusMessage());
            }
        });

        LatLng latLng = new LatLng(13.0733114, 80.255941);
        latitude = 13.0733114;
        longitude = 80.255941;
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
        mMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
                LatLng latLng = mMap.getCameraPosition().target;
                latitude = latLng.latitude;
                longitude = latLng.longitude;
            }
        });
    }

    @Override
    public void onClick(View view) {
        Intent returnIntent = new Intent();
        switch (view.getId()) {
            case R.id.positive:
                returnIntent.putExtra("result", latitude + "," + longitude);
                setResult(Activity.RESULT_OK, returnIntent);
                finish();
                break;
            case R.id.negative:
                setResult(Activity.RESULT_CANCELED, returnIntent);
                finish();
                break;
        }

    }

}
