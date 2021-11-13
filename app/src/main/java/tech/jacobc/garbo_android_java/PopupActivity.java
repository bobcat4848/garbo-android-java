package tech.jacobc.garbo_android_java;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;

import java.util.HashMap;
import java.util.Map;

public class PopupActivity extends AppCompatActivity {

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_popup);

        EditText trashcanName = findViewById(R.id.trashCanNameField);

        Button mButton = findViewById(R.id.button_close);

        Map<String, Object> trashcanData = new HashMap<>();

        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(PopupActivity.this, MapsActivity.class));
            }
        });

        Button imageButton = findViewById(R.id.button_image);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), 1);
            }
        });

        Button addTrashCan = findViewById(R.id.button_add);
        addTrashCan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // get geolocation and put it into trashcanData map
                LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

                if (ActivityCompat.checkSelfPermission(PopupActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(PopupActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(PopupActivity.this,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 99);
                    return;
                }
                Location locGPS = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

                trashcanData.put("geoloc", new GeoPoint(locGPS.getLatitude(), locGPS.getLongitude()));

                // get image
                trashcanData.put("img", "none");

                // get name
                trashcanData.put("name", trashcanName.getText().toString());

                // add data to firestore database
                db.collection("locations")
                        .add(trashcanData)
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                Toast.makeText(PopupActivity.this, "Success! Your trash can has been added!",
                                        Toast.LENGTH_LONG).show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(PopupActivity.this, "Failed! Your trash was not added for some reason.",
                                        Toast.LENGTH_LONG).show();
                            }
                        });
            }
        });
    }
}