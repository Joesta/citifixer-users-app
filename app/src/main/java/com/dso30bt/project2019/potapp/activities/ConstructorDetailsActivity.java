package com.dso30bt.project2019.potapp.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.dso30bt.project2019.potapp.R;
import com.dso30bt.project2019.potapp.models.Constructor;
import com.dso30bt.project2019.potapp.models.Pothole;
import com.dso30bt.project2019.potapp.models.User;
import com.dso30bt.project2019.potapp.utils.Constants;
import com.dso30bt.project2019.potapp.utils.NavUtil;
import com.dso30bt.project2019.potapp.utils.Utils;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

/**
 * Created by Joesta on 2019/07/15.
 */
public class ConstructorDetailsActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    /*var*/
    private static final String TAG = "ConstructorDetailsActiv";
    private String mEmail = null;
    private String mFullName = null;

    private List<Pothole> potholeList = null;

    /*widgets*/
    private TextView tvConstructorFirstName;
    private TextView tvConstructorLastName;
    private TextView tvConstructorCell;
    private TextView tvConstructorEmail;
    private TextView tvConstructorStatus;
    private LinearLayout linearLayoutBottomSheet;
    private ListView lvPotholes;

    private ArrayAdapter<String> adapter;
    private List<String> list = new ArrayList<>();
    private Constructor mConstructor;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_constructor_details);

        mEmail = getIntent().getStringExtra(Constants.EXTRA_EMAIL);
        mFullName = getIntent().getStringExtra(Constants.EXTRA_FULLNAME);

        intiUI();
        // getConstructorDetails();
        getPotholesFromDb();
    }

    private void intiUI() {
        tvConstructorFirstName = findViewById(R.id.tvConstructorFirstName);
        tvConstructorLastName = findViewById(R.id.tvConstructorLastName);
        tvConstructorCell = findViewById(R.id.tvConstructorCellNumber);
        tvConstructorEmail = findViewById(R.id.tvConstructorEmail);
        tvConstructorStatus = findViewById(R.id.tvConstructorStatus);
        linearLayoutBottomSheet = findViewById(R.id.bottom_sheet);
        lvPotholes = findViewById(R.id.lvPotholes);
    }

//    private void getConstructorDetails() {
//        FirebaseFirestore db = FirebaseFirestore.getInstance();
//        db.collection(Constants.CONSTRUCTOR_COLLECTION).document(mEmail)
//                .get()
//                .addOnSuccessListener(documentSnapshot -> {
//                    if (documentSnapshot.exists()) {
//
//                        Log.d(TAG, "getConstructorDetails: Document id is " + documentSnapshot.getReference().getId());
//                        mConstructor = documentSnapshot.toObject(Constructor.class);
//                        tvConstructorFirstName.setText(mConstructor.getName());
//                        tvConstructorLastName.setText(mConstructor.getSurname());
//                        tvConstructorCell.setText(mConstructor.getCellNumber());
//                        tvConstructorEmail.setText(mConstructor.getEmail());
//                        tvConstructorStatus.setText(mConstructor.getStatus());
//
//                    } else {
//                        Utils.showToast(ConstructorDetailsActivity.this, "Error reading user data");
//                    }
//
//                })
//                .addOnFailureListener(e -> ErrorHandler.showToast(this, e.getLocalizedMessage()));
//    }

    private void getPotholesFromDb() {
        // initializes pothole list
        potholeList = new ArrayList<>();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(Constants.USER_COLLECTION)
                .get()
                .addOnSuccessListener(this, querySnaps -> {
                    if (!querySnaps.isEmpty() && querySnaps.size() > 0) {
                        for (DocumentSnapshot docSnapShot : querySnaps) {
                            final User user = docSnapShot.toObject(User.class);
                            //final List<Pothole> localPotholes = user.getPotholes();
//                            if (!localPotholes.isEmpty()) {
//                                potholeList.addAll(localPotholes);
//                            }
                        }
                    }

//                    final String API_KEY = getString(R.string.API_KEY);
//                    final double lat = potholeList.get(0).getCoordinates().getLatitude();
//                    final double lng = potholeList.get(0).getCoordinates().getLongitude();
//
//                    Log.d(TAG, "getPotholesFromDb: " + lat);
//                    Log.d(TAG, "getPotholesFromDb: " + lng);
//
//                    String requestUrl = getString(R.string.geocode_request, lat, lng) + getString(R.string.API_KEY);
//                    Log.d(TAG, "getPotholesFromDb: Request URI is " + requestUrl);

//                    new Thread(new Runnable() {
//                        @Override
//                        public void run() {
//                            GeocodeAddress geocode = new GeocodeAddress(requestUrl);
//                            geocode.loadAddress();
//
//                            Log.d(TAG, "getPotholesFromDb: " + geocode.getAddress1());
//                            Log.d(TAG, "getPotholesFromDb: " + geocode.getAddress2());
//                            Log.d(TAG, "getPotholesFromDb: " + geocode.getCity());
//                            Log.d(TAG, "getPotholesFromDb: " + geocode.getCountry());
//                        }
//                    }).start();     f


                    loadPotholeList();

                }).addOnFailureListener(this, (e) -> {
            Utils.showToast(this, "Error " + e.getLocalizedMessage());
        });
    }

    private void loadPotholeList() {
//        for (Pothole p : potholeList) {
//            list.add(p.getCoordinates().getDate().toString());
//        }
//        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, list);
//        lvPotholes.setAdapter(adapter);
//        lvPotholes.setOnItemClickListener(this);
    }

    @Override
    public void onBackPressed() {
        NavUtil.moveToNextActivity(ConstructorDetailsActivity.this, MainActivity.class);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//        boolean isAvailable = isConstructorAvailable();
//
//        if (!isAvailable) {
//            assignConstructorToReport(position);
//        } else {
//            Utils.showToast(this, mConstructor.getName() + " appears " + mConstructor.getStatus() + ". Please try again later!");
//            return;
//        }
//
//        boolean isAssigned = isPotholeAlreadyAssigned(position);
//        if (isAssigned) {
//            Utils.showToast(this, "Report already assigned");
//            return;
//        }
    }

    private boolean isPotholeAlreadyAssigned(final int position) {
        //return potholeList.get(position).getAssignedBy() != null;
        return false;
    }

    private boolean isConstructorAvailable() {
        //return !mConstructor.getStatus().equalsIgnoreCase(StatusEnum.AVAILABLE.value);

        return false;
    }

    private void assignConstructorToReport(int position) {
//        List<Pothole> updatePotholeList = mConstructor.getPotholes();
//        Pothole pothole = potholeList.get(position);
//        pothole.setAssignedBy(mFullName);
//        updatePotholeList.add(pothole);
//
//        FirebaseFirestore db = FirebaseFirestore.getInstance();
//        Task<Void> update = db.collection(Constants.CONSTRUCTOR_COLLECTION)
//                .document(mEmail)
//                .update(Constants.DocumentFields.POTHOLES, updatePotholeList);
//        update.addOnCompleteListener(this, task -> {
//            if (task.isSuccessful()) {
//                Utils.showToast(this, "Updated Successfully");
//            } else {
//                Utils.showToast(this, "Failed to update");
//            }
//        });
    }
}
