package com.alphamax.covifight.UI.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.alphamax.covifight.R;
import com.alphamax.covifight.UI.activity.NavigationActivity;
import com.alphamax.covifight.UI.activity.ProfileActivity;
import com.alphamax.covifight.UI.activity.StartActivity;
import com.alphamax.covifight.helper.QRCodeHelper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


public class ProfileFragment extends Fragment {

    private TextView name,place,dob,email;
    private static final String TAG = "ProfileFragment";
    private FirebaseFirestore db;
    private ImageView barcode;
    private DocumentSnapshot document;
    private Map<String,Object> users=new HashMap<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_profile,container,false);
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        NavigationActivity.navigationHeading.setText(getResources().getString(R.string.navigationProfile));

        name=view.findViewById(R.id.nameProfile);
        place=view.findViewById(R.id.placeProfile);
        dob=view.findViewById(R.id.dobProfile);
        email=view.findViewById(R.id.emailProfile);
        barcode=view.findViewById(R.id.barcodeProfile);

        db=FirebaseFirestore.getInstance();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        assert user != null;
        final String number=user.getPhoneNumber();

        DocumentReference docRef = db.collection("Profile").document(number);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    document = task.getResult();
                    assert document != null;
                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                        name.setText(""+document.get("Name"));
                        dob.setText(""+document.get("DateOfBirth"));
                        email.setText(""+document.get("Email"));
                        place.setText(""+document.get("Home"));
                        users.put("Name",document.get("Name"));
                        users.put("Email",document.get("Email"));
                        users.put("DateOfBirth",document.get("DateOfBirth"));
                        users.put("Mobile",document.get("Mobile"));
                        users.put("Home",document.get("Home"));
                        users.put("Probability",document.get("Probability"));
                        users.put("ID",document.get("ID"));
                    }
                    else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });

        String serializeString = new Gson().toJson(number);
        Bitmap bitmap = QRCodeHelper.newInstance(getContext()).setContent(serializeString)
                .setErrorCorrectionLevel(ErrorCorrectionLevel.Q)
                .setMargin(2).getQRCOde();
        barcode.setImageBitmap(bitmap);

        Button PrivateKey=view.findViewById(R.id.keyProfile);
        PrivateKey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences prefs = requireActivity().getSharedPreferences(requireActivity().getPackageName(), Context.MODE_PRIVATE);
                String privateKey=prefs.getString("PrivateKey","");
                users.put("PrivateKey",privateKey);
                db.collection("Profile").document(number).set(users);
            }
        });

        Button logout=view.findViewById(R.id.logoutProfile);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intent=new Intent(getContext(), StartActivity.class);
                startActivity(intent);
            }
        });

    }
}
