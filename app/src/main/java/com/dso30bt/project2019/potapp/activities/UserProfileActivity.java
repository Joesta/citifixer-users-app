package com.dso30bt.project2019.potapp.activities;

import android.os.Bundle;
import android.widget.TextView;

import com.dso30bt.project2019.potapp.R;
import com.dso30bt.project2019.potapp.models.User;
import com.dso30bt.project2019.potapp.utils.Constants;
import com.dso30bt.project2019.potapp.utils.NavUtil;
import com.dso30bt.project2019.potapp.utils.SharedPreferenceManager;
import com.dso30bt.project2019.potapp.utils.Utils;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

/**
 * Created by Joesta on 2019/07/07.
 */
public class UserProfileActivity extends AppCompatActivity {

    private static final String TAG = "UserProfileActivity";
    //widgets
    private TextView tvFirstName;
    private TextView tvLastName;
    private TextView tvCellNumber;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        initUI();
        loadUserDetails();
    }

    /***
     * get user info from db and set them to widgets
     */
    private void loadUserDetails() {
        DocumentReference documentRef = FirebaseFirestore.getInstance()
                .collection(Constants.USER_COLLECTION)
                .document(SharedPreferenceManager.getUserEmail(UserProfileActivity.this));

        documentRef
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    User user = documentSnapshot.toObject(User.class);
                    //set user info to wigets
                    tvFirstName.setText(user.getName());
                    tvLastName.setText(user.getSurname());
                    tvCellNumber.setText(user.getCellNumber());

                    Utils.showToast(UserProfileActivity.this, "User profile loaded successfully");

                }).addOnFailureListener(error -> Utils.showToast(UserProfileActivity.this, "Error " + error.getLocalizedMessage()));
    }

    /***
     * initializes UI widgets
     */
    private void initUI() {
        tvFirstName = findViewById(R.id.tvFirstName);
        tvLastName = findViewById(R.id.tvLastName);
        tvCellNumber = findViewById(R.id.tvCellnumber);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        NavUtil.moveToNextActivity(UserProfileActivity.this, MainActivity.class);
    }
}
