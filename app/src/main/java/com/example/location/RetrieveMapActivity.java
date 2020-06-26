package com.example.location;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class RetrieveMapActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    Marker mm;
    DatabaseReference reff1;
    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;
    public static final String TAG = "bottom_sheet";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_retrieve_map);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener(navListener);
    }


    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    Fragment selectedFragment = null;

                    switch (item.getItemId()) {
                        case R.id.nav_home:
                            selectedFragment = new Fragment();
                            break;
                        case R.id.nav_favorites:
                            selectedFragment = new FavoritesFragment();
                            break;
                        case R.id.nav_search:
                            selectedFragment = new SearchFragment();
                            break;
                    }

                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                            selectedFragment).commit();
                    return true;
                }
            };

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
        final String busno = getIntent().getStringExtra("busnumber");
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Location");
        ValueEventListener listener = databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Double latitude = dataSnapshot.child(busno).child("latitude").getValue(Double.class);
                Double longitude = dataSnapshot.child(busno).child("longitude").getValue(Double.class);

                LatLng location = new LatLng(latitude, longitude);
                if(mm==null) {
                    mm=mMap.addMarker(new MarkerOptions().position(location).title(dataSnapshot.child("Numplate").child(String.valueOf(busno)).getValue().toString().trim()).icon(bitmapDescriptorFromVector(getApplicationContext(), R.drawable.ic_directions_bus_black_24dp)));
                }
                else{
                    MarkerAnimation.animateMarkerToGB(mm, location, new LatLngInterpolator.Spherical());
                }
                mMap.moveCamera(CameraUpdateFactory.newLatLng(location));
                mMap.getUiSettings().setZoomControlsEnabled(true);
                mMap.setMinZoomPreference(8.0f);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
                reff1 = FirebaseDatabase.getInstance().getReference().child("User").child("bus-" + busno);
                reff1.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        int noofstops = Integer.parseInt(dataSnapshot.child(String.valueOf("noofstops")).getValue().toString().trim());
                        String name = "Marker";
                        for (int i = 1; i < noofstops * 2; i = i + 2) {
                            if (i == 1) {
                                createMarkerstart(Double.parseDouble(dataSnapshot.child(String.valueOf(i)).getValue().toString().trim()), Double.parseDouble(dataSnapshot.child(String.valueOf(i + 1)).getValue().toString().trim()), dataSnapshot.child(String.valueOf(i + "t")).getValue().toString().trim());
                            } else if (i == ((noofstops * 2) - 1)) {
                                createMarkerstop(Double.parseDouble(dataSnapshot.child(String.valueOf(i)).getValue().toString().trim()), Double.parseDouble(dataSnapshot.child(String.valueOf(i + 1)).getValue().toString().trim()), dataSnapshot.child(String.valueOf(i + "t")).getValue().toString().trim());
                            } else {
                                createMarker(Double.parseDouble(dataSnapshot.child(String.valueOf(i)).getValue().toString().trim()), Double.parseDouble(dataSnapshot.child(String.valueOf(i + 1)).getValue().toString().trim()), dataSnapshot.child(String.valueOf(i + "t")).getValue().toString().trim());
                            }
                            if (i < ((noofstops * 2) - 2)) {
                                createPolyline(Double.parseDouble(dataSnapshot.child(String.valueOf(i)).getValue().toString().trim()), Double.parseDouble(dataSnapshot.child(String.valueOf(i + 1)).getValue().toString().trim()), Double.parseDouble(dataSnapshot.child(String.valueOf(i + 2)).getValue().toString().trim()), Double.parseDouble(dataSnapshot.child(String.valueOf(i + 3)).getValue().toString().trim()));
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

            protected Polyline createPolyline(double flat, double flon, double slat, double slon) {
                return mMap.addPolyline(new PolylineOptions()
                        .add(new LatLng(flat, flon), new LatLng(slat, slon))
                        .width(15)
                        .color(R.color.routecolors)
                        .geodesic(true));
            }


            protected Marker createMarker(double latitude, double longitude, String title) {

                return mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(latitude, longitude))
                        .title(title).icon(bitmapDescriptorFromVector(getApplicationContext(), R.drawable.midmarker)));
            }

            protected Marker createMarkerstart(double latitude, double longitude, String title) {
                return mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(latitude, longitude))
                        .title(title).icon(bitmapDescriptorFromVector(getApplicationContext(), R.drawable.startmarker)));
            }

            protected Marker createMarkerstop(double latitude, double longitude, String title) {
                return mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(latitude, longitude))
                        .title(title).icon(bitmapDescriptorFromVector(getApplicationContext(), R.drawable.stopmarker)));
            }

            private BitmapDescriptor bitmapDescriptorFromVector(Context applicationContext, int ic_directions_bus_black_24dp) {
                Drawable vectorDrawable = ContextCompat.getDrawable(applicationContext, ic_directions_bus_black_24dp);
                vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
                Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(bitmap);
                vectorDrawable.draw(canvas);
                return BitmapDescriptorFactory.fromBitmap(bitmap);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        // Add a marker in Sydney and move the camera

    }
}
