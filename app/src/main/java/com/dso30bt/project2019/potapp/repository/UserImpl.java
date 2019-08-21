package com.dso30bt.project2019.potapp.repository;

import android.app.Activity;
import android.content.Context;

import com.dso30bt.project2019.potapp.activities.LoginActivity;
import com.dso30bt.project2019.potapp.activities.MainActivity;
import com.dso30bt.project2019.potapp.models.LoginModel;
import com.dso30bt.project2019.potapp.models.Person;
import com.dso30bt.project2019.potapp.models.User;
import com.dso30bt.project2019.potapp.models.enums.RoleEnum;
import com.dso30bt.project2019.potapp.utils.Constants;
import com.dso30bt.project2019.potapp.utils.NavUtil;
import com.dso30bt.project2019.potapp.utils.SharedPreferenceManager;
import com.dso30bt.project2019.potapp.utils.Utils;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import de.mateware.snacky.Snacky;

/**
 * Created by Joesta on 2019/05/30.
 */
public class UserImpl implements IUserRepository {

    private static final String TAG = "UserImpl";
    FirebaseStorage mStorage = FirebaseStorage.getInstance();
    private Context context;
    private Person mAppUser;
    private String userEmail;
    private String mCollection;
    private FirebaseFirestore mDatabase = FirebaseFirestore.getInstance();

    /***
     * constructor
     * @param context of activity instantiating this object
     */
    public UserImpl(Context context) {
        this.context = context;
        userEmail = SharedPreferenceManager.getEmail(context);
    }


    @Override
    public void registerUser(final Person appUser) {
        String collection = getCollection(appUser);
        DocumentReference documentRef = mDatabase.collection(collection.toLowerCase())
                .document(appUser.getEmailAddress());

        documentRef
                .get()
                .addOnSuccessListener((Activity) context, documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Utils.showToast(context, "User already exist!");
                    } else {
                        documentRef.set(appUser).addOnSuccessListener(aVoid -> {
                            Utils.showToast(context, "User Registered");
                            NavUtil.moveToNextActivity(context, LoginActivity.class);
                        }).addOnFailureListener(((Activity) context), error -> Utils.showToast(context, "Error " + error.getLocalizedMessage()));
                    }
                }).addOnFailureListener(((Activity) context), error -> Utils.showToast(context, "Error " + error.getLocalizedMessage()));

    }

    private String getCollection(Person appUser) {
        return (appUser instanceof User) ? Constants.USER_COLLECTION : Constants.CONSTRUCTOR_COLLECTION;
    }

    /***
     * loggin user by email address
     * @param loginModel object hold user login info
     */

    @Override
    public void loginUserByEmail(final LoginModel loginModel) {

        // @Todo - fix constructor login

        mDatabase
                .collection(Constants.USER_COLLECTION)
                .document(loginModel.getEmailAddress())
                .get()
                .addOnSuccessListener(documentSnapshot -> {

                    if (documentSnapshot.exists()) {
                        mAppUser = documentSnapshot.toObject(Person.class);

                        if (loginModel.getPassword().equals(mAppUser.getPassword())) {

                            // 1. User
                            // 2. Constructor
                            final int userType = mAppUser
                                    .getRole()
                                    .getRoleDescription()
                                    .equals(RoleEnum.USER.role) ? 1 : 2;

                            SharedPreferenceManager.saveEmail(context, this.mAppUser.getEmailAddress(), userType);
                            NavUtil.moveToNextActivity(context, MainActivity.class);
                            ((LoginActivity) context).finish();

                        } else {
                            Snacky
                                    .builder()
                                    .setActivity(((Activity) context))
                                    .error()
                                    .setDuration(BaseTransientBottomBar.LENGTH_LONG)
                                    .setText("Username or password incorrect")
                                    .show();

                        }

                    } else {

                        Snacky
                                .builder()
                                .setActivity(((Activity) context))
                                .info()
                                .setText("Account does not exist. Please click sing-up to create account!")
                                .setDuration(BaseTransientBottomBar.LENGTH_LONG)
                                .show();
                    }
                }).addOnFailureListener(((Activity) context), error -> Utils.showToast(context, "Error " + error.getLocalizedMessage()));
    }


//    public void addPotholeAndImage(Pothole pothole, File imageFile) {
//        FirebaseFirestore db = FirebaseFirestore.getInstance();
//        DocumentReference documentRef = db.collection(Constants.USER_COLLECTION)
//                .document(userEmail);
//        documentRef.get().addOnSuccessListener(documentSnapshot -> {
//            if (documentSnapshot.exists()) {
//                Log.d(TAG, "addPotholeAndImage: document exists. add pothole for the mUser");
//
//                if (imageFile != null && pothole != null && pothole.getCoordinates() != null) {
//                    Uri imageUri = Uri.fromFile(imageFile);
//                    String path = "potholes/" + UUID.randomUUID().toString() + imageUri.getLastPathSegment();
//
//                    StorageReference storageRef = mStorage.getReference(path);
//                    UploadTask uploadTask = storageRef.putFile(imageUri);
//                    uploadTask.addOnSuccessListener((Activity) context, taskSnapshot -> {
//
//                        User user = documentSnapshot.toObject(User.class);
//
//                        //get download url
//                        Task<Uri> downloadUrl = taskSnapshot.getStorage().getDownloadUrl();
//                        downloadUrl.addOnSuccessListener((Activity) context, uri -> {
//                            //add url to url pothole url list
//                            pothole.setPotholeUrl(uri.toString());
//
//                            final String reporterFullName = user.getName() + user.getSurname();
//                            pothole.setReportedBy(reporterFullName);
//
//                            //add mUser pothole
//                            user.getPotholes().add(pothole);
//                            Map<String, Object> userMap = getMap(user);
//                            updateUser(userMap, documentRef);
//
//                            Utils.showToast(context, "Report was send!");
//
//                        }).addOnFailureListener(error -> Utils.showToast(context, "Error: " + error.getLocalizedMessage()));
//
//                    }).addOnFailureListener(error -> Utils.showToast(context, "Error: " + error.getLocalizedMessage()));
//                } else {
//                    Utils.showToast(context, "An error occurred while trying to upload. Please retry!");
//                }
//
//            } else {
//                Log.d(TAG, "addPotholeAndImage: document for mUser does not exist");
//            }
//        }).addOnFailureListener(error -> Utils.showToast(context, "Error: " + error.getLocalizedMessage()));
//    }

}
