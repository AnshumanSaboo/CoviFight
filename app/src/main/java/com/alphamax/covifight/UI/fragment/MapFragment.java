package com.alphamax.covifight.UI.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alphamax.covifight.R;
import com.alphamax.covifight.UI.activity.NavigationActivity;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;

import java.util.Objects;


public class MapFragment extends Fragment {

    private DocumentSnapshot doc;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_map,container,false);
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        NavigationActivity.navigationHeading.setText(getResources().getString(R.string.navigationMap));

        SupportMapFragment mapFragment;
        mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        assert mapFragment != null;
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(final GoogleMap mMap) {
               if(NavigationActivity.listLocations.size()>0)
               {
                   for (int i=0;i<NavigationActivity.listLocations.size();i++)
                   {
                       final String document=NavigationActivity.listLocations.get(i);
                       FirebaseFirestore.getInstance().collection("HighRiskPlaces").document(document).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                           @Override
                           public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                               if (task.isSuccessful()) {
                                   doc = task.getResult();
                                   assert doc != null;
                                   if (doc.exists()){
                                       GeoPoint geoPoint= (GeoPoint) doc.get("location");
                                       assert geoPoint != null;
                                       LatLng loc = new LatLng(geoPoint.getLatitude(), geoPoint.getLongitude());
                                       mMap.addMarker(new MarkerOptions().position(loc));
                                   }
                           }
                       }
                   });
                   }
               }
            }
        });
    }


}
