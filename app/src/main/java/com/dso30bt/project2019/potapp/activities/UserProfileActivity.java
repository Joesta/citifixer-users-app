package com.dso30bt.project2019.potapp.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.dso30bt.project2019.potapp.R;
import com.dso30bt.project2019.potapp.models.Constructor;
import com.dso30bt.project2019.potapp.models.Person;
import com.dso30bt.project2019.potapp.models.Role;
import com.dso30bt.project2019.potapp.models.User;
import com.dso30bt.project2019.potapp.models.enums.RoleEnum;
import com.dso30bt.project2019.potapp.models.enums.UserEnum;
import com.dso30bt.project2019.potapp.utils.Constants;
import com.dso30bt.project2019.potapp.utils.NavUtil;
import com.dso30bt.project2019.potapp.utils.SharedPreferenceManager;
import com.dso30bt.project2019.potapp.utils.Utils;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import de.hdodenhof.circleimageview.CircleImageView;
import de.mateware.snacky.Snacky;

import static android.view.View.GONE;

/**
 * Created by Joesta on 2019/07/07.
 */
public class UserProfileActivity extends AppCompatActivity implements View.OnClickListener {

    /*activity members*/
    private static final String TAG = "UserProfileActivity";
    private static final int REQUEST_TAKE_PHOTO = 2;

    /*firebase*/
    FirebaseStorage mStorage = FirebaseStorage.getInstance();

    //widgets
    /*text views*/
    private TextView mTvFirstName;
    private TextView mTvLastName;
    private TextView mTvCellNumber;
    private TextView mTvEmail;
    /*edit text*/
    private EditText mFirstNameEntry;
    private EditText mLastNameEntry;
    private EditText mCellNumberEntry;
    private EditText mEmailEntry;

    /*progress bar*/
    private ProgressBar mProgressBar;

    /*spinner*/
    private Spinner mSpinnerStatus;

    /*buttons*/
    private Button mBtnStatus;
    private Button mBtnProfileEdit;

    /*circular image*/
    private CircleImageView user_propic;

    /*adapter*/
    private ArrayAdapter<CharSequence> mSpinnerAdapter;

    /*vars*/
    private String mCollection;
    private String mCurrentPhotoPath;
    private int mSelectedItemPosition;
    private Bitmap mScaled;
    private boolean validEditInput = true;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        initUI();
        loadUserDetails();
    }

    /***
     * initializes UI widgets
     */
    private void initUI() {
        mTvFirstName = findViewById(R.id.tvFirstName);
        mTvLastName = findViewById(R.id.tvLastName);
        mTvCellNumber = findViewById(R.id.tvCellnumber);
        mTvEmail = findViewById(R.id.tvEmail);
        mProgressBar = findViewById(R.id.progressbar);
        mSpinnerStatus = findViewById(R.id.spinnerStatus);
        user_propic = findViewById(R.id.user_propic);
        mBtnProfileEdit = findViewById(R.id.btnProfileEdit);

        mSpinnerStatus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mSelectedItemPosition = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                return;
            }
        });

        mBtnStatus = findViewById(R.id.btnStatus);
        mBtnStatus.setOnClickListener(UserProfileActivity.this);
        mBtnProfileEdit.setOnClickListener(this);
    }

    /***
     * get user info from db and set them to widgets
     */
    private void loadUserDetails() {
        Log.i(TAG, "loadUserDetails: loading user details");

        /*show or hide views*/
        showOrHideEditButton(View.GONE);
        showOrHideProgressBar(View.VISIBLE);

        int flag = SharedPreferenceManager.getFlag(UserProfileActivity.this);

        if (flag == UserEnum.USER.ordinal()) {
            mCollection = Constants.USER_COLLECTION;
            hideViews();

        } else if (flag == UserEnum.CONSTRUCTOR.ordinal()) {
            mCollection = Constants.CONSTRUCTOR_COLLECTION;
        }

        FirebaseFirestore.getInstance()
                .collection(mCollection)
                .document(SharedPreferenceManager.getEmail(this))
                /*listen ro changes in the document in real time*/
                .addSnapshotListener(UserProfileActivity.this, (snapshot, error) -> {

                    // handle error
                    if (error != null) {
                        Utils.showToast(this, error.getLocalizedMessage());
                        return;
                    }

                    if (snapshot != null && snapshot.exists()) {
                        // get role
                        Role role = snapshot.get("role", Role.class);
                        // save check if user is not deleted while still using the App
                        if (role == null) {
                            // take user to login. Account must have been deleted while using the app
                            takeUserToLogin();

                        } else {

                            if (role.getRoleDescription().equalsIgnoreCase(RoleEnum.User.role)) {
                                //user
                                User user = snapshot.toObject(User.class);
                                assert user != null;
                                setUserProfileDetails(user);
                            } else {
                                // constructor
                                Constructor constructor = snapshot.toObject(Constructor.class);
                                assert constructor != null;
                                setUserProfileDetails(constructor);
                            }
                        }

                    } else {

                        Utils.showToast(this, "User profile not found");
                    }

                });
    }

    /**
     * sleeps thread for 5 seconds before logging out
     */
    private void takeUserToLogin() {
        new Thread(() -> {
            try {
                Snacky
                        .builder()
                        .setActivity(this)
                        .error()
                        .setText("Error while trying to update profile! User will be logged out in 5 Seconds")
                        .show();
                // sleeps for 5 seconds before initiating logout
                Thread.sleep(5000);
                // logout user
                logout();

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start(); // starts a thread in the background
    }

    private void setUserProfileDetails(Person person) {
        Log.i(TAG, "setUserProfileDetails: setting user information");

        mTvFirstName.setText(person.getFirstName());
        mTvLastName.setText(person.getLastName());
        mTvCellNumber.setText(person.getCellNumber());
        mTvEmail.setText(person.getEmailAddress());

        if (person instanceof User) {
            User user = (User) person;
            if (user.getImageUrl().equals("")) {
                user_propic.setImageResource(R.drawable.imageholder);
            } else {
                loadProfilePic(user.getImageUrl());
            }

        } else {
            Constructor constructor = ((Constructor) person);
            if (constructor.getImageUrl().equals("")) {
                user_propic.setImageResource(R.drawable.imageholder);
            } else {
                loadProfilePic(constructor.getImageUrl());
            }
        }

        showOrHideProgressBar(View.GONE); /*hide progress bar*/
        showOrHideEditButton(View.VISIBLE); /*show edit profile button after loading user profile*/
    }

    private void showProfileEditDialog() {
        // set dialog properties
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(this);
        mBuilder.setTitle("Edit Profile");
        mBuilder.setCancelable(false);
        mBuilder.setIcon(R.drawable.assign);

        // get dialog layout reference
        View dialogView = getDialogLayoutView();

        // set basic information of the current person [user | constructor]
        getEditTextReference(dialogView);

        mBuilder
                .setView(dialogView)
                .setPositiveButton("Save", (dialog, which) -> {
                    Log.i(TAG, "showProfileEditDialog: can save info? " + validEditInput);
                    if (validEditInput) {
                        saveUpdatedInfo();

                    } else {
                        Utils.showToast(this, "Failed to save info. Please provide valid input and retry!");
                    }

                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        // show dialog
        mBuilder.create().show();
    }

    /**
     * get dialog layout
     *
     * @return view - dialog layout
     */
    private View getDialogLayoutView() {
        return getLayoutInflater().inflate(R.layout.layout_dialog, null);
    }

    /**
     * get editText reference from dialog view
     *
     * @param dialogView to find editText info from
     */
    private void getEditTextReference(View dialogView) {
        Log.i(TAG, "getEditTextReference: setting user profile information");

        // find view reference
        mFirstNameEntry = dialogView.findViewById(R.id.etFirstName);
        mLastNameEntry = dialogView.findViewById(R.id.etLastName);
        mCellNumberEntry = dialogView.findViewById(R.id.etCellNumber);
        mEmailEntry = dialogView.findViewById(R.id.etEmail);

        registerTextChangeListener();

        setDialogEditInfo();
    }

    /**
     * set dialog edit info
     */
    private void setDialogEditInfo() {
        // set user basic information
        mFirstNameEntry.setText(mTvFirstName.getText().toString());
        mLastNameEntry.setText(mTvLastName.getText().toString());
        mCellNumberEntry.setText(mTvCellNumber.getText().toString());
        mEmailEntry.setText(mTvEmail.getText().toString());
        /* cannot edit emailAddress*/
        mEmailEntry.setEnabled(false);
    }

    /**
     * register listener to editText
     */
    private void registerTextChangeListener() {

        //@Todo - source out a way to reduce this code block

        /* first name */
        mFirstNameEntry.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String firstName = s.toString();
                Log.i(TAG, "onTextChanged: start " + start + " before " + before + " count " + count);

                if (!TextUtils.isEmpty(firstName) && TextUtils.getTrimmedLength(firstName) != 0) {

                    boolean matches = firstName.matches(Constants.NAMES_PATTERN);
                    if (!matches) {
                        validEditInput = false;
                        mFirstNameEntry.setError("Must be at least 3 characters long. \n" +
                                "Must contain letter A-Z.\n" +
                                "Must not contain special characters");
                        mFirstNameEntry.requestFocus();
                    } else {
                        mFirstNameEntry.clearFocus();
                        validEditInput = true;
                    }

                } else {
                    validEditInput = false;
                    mFirstNameEntry.setError("First name is Required");
                    mFirstNameEntry.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        /* last name */
        mLastNameEntry.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String lastName = s.toString();
                Log.i(TAG, "onTextChanged: start " + start + " before " + before + " count " + count);

                if (!TextUtils.isEmpty(lastName) && TextUtils.getTrimmedLength(lastName) != 0) {

                    boolean matches = lastName.matches(Constants.NAMES_PATTERN);
                    if (!matches) {
                        validEditInput = false;
                        mLastNameEntry.setError("Must be at least 3 characters long. \n" +
                                "Must contain letter A-Z.\n" +
                                "Must not contain special characters");
                        mLastNameEntry.requestFocus();
                    } else {
                        validEditInput = true;
                        mLastNameEntry.clearFocus();
                    }

                } else {
                    validEditInput = false;
                    mLastNameEntry.setError("Last name is Required");
                    mLastNameEntry.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        /* cell number */
        mCellNumberEntry.addTextChangedListener(new TextWatcher() {
            List<String> cellPrefixList = Arrays.asList(getResources().getStringArray(R.array.cellphone_prefix));

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String cellNumber = s.toString();

                if (!TextUtils.isEmpty(cellNumber) && TextUtils.getTrimmedLength(cellNumber) != 0) {
                    if (cellNumber.length() > 3) {
                        boolean validCellNumberPrefix = cellPrefixList.contains(cellNumber.substring(0, 3));
                        if (!validCellNumberPrefix) {
                            validEditInput = false;
                            mCellNumberEntry.setError("Invalid cell number");
                            mCellNumberEntry.requestFocus();
                        } else {
                            mCellNumberEntry.clearFocus();
                            validEditInput = false;
                        }
                    }

                } else {
                    validEditInput = false;
                    mCellNumberEntry.setError("Cell number is required!");
                    mCellNumberEntry.requestFocus();
                }

                if (cellNumber.length() != 10) {
                    validEditInput = false;
                    mCellNumberEntry.setError("Cell number must be 10 digits long");
                    mCellNumberEntry.requestFocus();
                } else {
                    validEditInput = true;
                    mCellNumberEntry.clearFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    /**
     * saves updated information to database
     */
    public void saveUpdatedInfo() {

        String firstName = mFirstNameEntry.getText().toString();
        String lastName = mLastNameEntry.getText().toString();
        String cellNumber = mCellNumberEntry.getText().toString();

        // show or hide views
        showOrHideProgressBar(View.VISIBLE);
        showOrHideEditButton(View.GONE);

        FirebaseFirestore.getInstance()
                .collection(mCollection)
                .document(SharedPreferenceManager.getEmail(this))
                .update(Constants.DocumentFields.FIST_NAME, firstName,
                        Constants.DocumentFields.LAST_NAME, lastName,
                        Constants.DocumentFields.CELL_NUMBER, cellNumber)
                /*attaching onSuccessListener*/
                .addOnSuccessListener(UserProfileActivity.this, aVoid -> {

                    Snacky
                            .builder()
                            .setActivity(this)
                            .success()
                            .setText("Profile updated")
                            .setDuration(BaseTransientBottomBar.LENGTH_SHORT)
                            .show();

                    showOrHideProgressBar(View.GONE);
                    showOrHideEditButton(View.VISIBLE);

                })
                /* attaching onFailureListener*/
                .addOnFailureListener(UserProfileActivity.this, error -> {
                    Snacky
                            .builder()
                            .setActivity(this)
                            .error()
                            .setText("Failed to update profile." +
                                    "\n Reason: " + error.getLocalizedMessage())
                            .setDuration(BaseTransientBottomBar.LENGTH_LONG)
                            .show();
                    Log.e(TAG, "saveUpdatedInfo: " + error.getLocalizedMessage());

                    showOrHideProgressBar(View.GONE);
                    showOrHideEditButton(View.VISIBLE);

                });

    }

    /**
     * load user profile
     *
     * @param proPicUrl user profile picture
     */
    private void loadProfilePic(String proPicUrl) {
        Picasso
                .get()
                .load(proPicUrl)
                .into(user_propic);
    }

    private void hideViews() {
        /*hide parent view*/
        mSpinnerStatus.getRootView()
                .findViewById(R.id.card_constructor_status)
                .setVisibility(GONE);

        mBtnStatus.setVisibility(GONE);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        NavUtil.moveToNextActivity(UserProfileActivity.this, MainActivity.class);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnStatus:
                updateStatus();
                break;
            case R.id.btnProfileEdit:
                showProfileEditDialog();
                break;
            case R.id.etEmail:
                Utils.showToast(this, "Email field is read only.");
                break;
            default:
                break;
        }
    }

    /**
     * update constructor status
     */
    private void updateStatus() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Task<Void> status = db.collection(Constants.CONSTRUCTOR_COLLECTION).document(SharedPreferenceManager.getEmail(UserProfileActivity.this))
                .update("status", getResources().getStringArray(R.array.constructor_status)[mSelectedItemPosition]);
        status.addOnSuccessListener(UserProfileActivity.this, aVoid -> {
            Utils.showToast(UserProfileActivity.this, "Status updated");

        }).addOnFailureListener(UserProfileActivity.this, e -> {
            Utils.showToast(UserProfileActivity.this, "Error " + e.getLocalizedMessage());
        });
    }

    public void eventChangeProfile(View view) {
        dispatchTakePictureIntent();
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                Log.d(TAG, "dispatchTakePictureIntent: Error occurred while creating file: " + ex.getLocalizedMessage());
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.dso30bt.project2019.potapp.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        try {
            switch (requestCode) {
                case REQUEST_TAKE_PHOTO: {
                    if (resultCode == RESULT_OK) {
                        File file = new File(mCurrentPhotoPath);
                        Bitmap bitmap = MediaStore.Images.Media
                                .getBitmap(getContentResolver(), Uri.fromFile(file));
                        if (bitmap != null) {
                            int nh = (int) (bitmap.getHeight() * (512.0 / bitmap.getWidth()));
                            mScaled = Bitmap.createScaledBitmap(bitmap, 512, nh, true);

                            //setProfilePic(mScaled);
                            uploadImage(file);
                        } // end bitmap nullable check
                    } // end resultCode check
                    break;
                } // end case
            } // end switch

        } catch (Exception error) {
            error.printStackTrace();
            Log.d(TAG, "onActivityResult: " + error.getLocalizedMessage());
        }
    }

    /**
     * update profile pic
     *
     * @param url profile pic
     */
    private void updateImageUrl(String url) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(mCollection).document(SharedPreferenceManager.getEmail(this))
                .update(Constants.DocumentFields.USERS_IMAGE_URL, url)
                .addOnSuccessListener(this, aVoid -> {
                    Utils.showToast(this, "Profile picture updated");
                    updateUserProfilePic();
                }).addOnFailureListener(this, e -> Utils.showToast(this, e.getLocalizedMessage()));

    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",   /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    /**
     * upload image to database
     *
     * @param imageFile to upload
     */
    private void uploadImage(File imageFile) {
        if (imageFile != null) {
            Uri imageUri = Uri.fromFile(imageFile);
            String path = "propics/" + UUID.randomUUID().toString() + imageUri.getLastPathSegment();

            StorageReference storageRef = mStorage.getReference(path);
            UploadTask uploadTask = storageRef.putFile(imageUri);
            uploadTask.addOnSuccessListener(this, taskSnapshot -> {
                //get download url
                Task<Uri> downloadUrl = taskSnapshot.getStorage().getDownloadUrl();
                downloadUrl.addOnSuccessListener(UserProfileActivity.this, uri -> {
                    updateImageUrl(uri.toString());
                });
            }).addOnFailureListener(this, e -> Utils.showToast(this, e.getLocalizedMessage()));
        }
    }

    /**
     * update user profile
     */
    private void updateUserProfilePic() {
        user_propic.setImageBitmap(mScaled);
    }

    /**
     * shows or hide edit button
     *
     * @param action to be taken[show | gone]
     */
    private void showOrHideEditButton(int action) {
        mBtnProfileEdit.setVisibility(action);
    }

    /**
     * shows or hide progress bar from loading
     *
     * @param action to be taken[show | gone]
     */
    private void showOrHideProgressBar(int action) {
        mProgressBar.setVisibility(action);
    }

    private void logout() {
        NavUtil.moveToNextActivity(UserProfileActivity.this, LoginActivity.class);
        SharedPreferenceManager.clearSavedLoginInfo(this);
    }
}
