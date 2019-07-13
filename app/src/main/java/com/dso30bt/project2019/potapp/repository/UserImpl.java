package com.dso30bt.project2019.potapp.repository;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.dso30bt.project2019.potapp.activities.LoginActivity;
import com.dso30bt.project2019.potapp.activities.MainActivity;
import com.dso30bt.project2019.potapp.models.Constructor;
import com.dso30bt.project2019.potapp.models.LoginModel;
import com.dso30bt.project2019.potapp.models.Pothole;
import com.dso30bt.project2019.potapp.models.User;
import com.dso30bt.project2019.potapp.utils.Constants;
import com.dso30bt.project2019.potapp.utils.ErrorHandler;
import com.dso30bt.project2019.potapp.utils.NavUtil;
import com.dso30bt.project2019.potapp.utils.SharedPreferenceManager;
import com.dso30bt.project2019.potapp.utils.Utils;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Created by Joesta on 2019/05/30.
 */
public class UserImpl implements IUserRepository {

    private static final String TAG = "UserImpl";
    FirebaseStorage mStorage = FirebaseStorage.getInstance();
    private Context context;
    private User mUser;
    private String userEmail;

    /***
     * constructor
     * @param context of activity instantiating this object
     */
    public UserImpl(Context context) {
        this.context = context;
        userEmail = SharedPreferenceManager.getEmail(context);
    }

    /***
     * register new user
     * @param object new user
     */
    @Override
    public void registerUser(Object object) {
        User user = null;
        Constructor constructor = null;
        String role = null;
        String document = null;

        final List<Object> obj = new ArrayList<>();

        if (object instanceof User) {
            user = (User) object;
            obj.add(user);
            role = user.getRole();
            document = user.getEmail();
        } else {
            constructor = (Constructor) object;
            obj.add(constructor);
            role = constructor.getRole();
            document = constructor.getEmailAddress();
        }

        String collection =
                role.equalsIgnoreCase("User") ? Constants.USER_COLLECTION : Constants.CONSTRUCTOR_COLLECTION;
        Log.d(TAG, "registerUser: to collection of " + collection);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference documentRef =
                db.collection(collection).document(document);

        documentRef.get()
                .addOnSuccessListener((Activity) context, documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Utils.showToast(context, "User already exist!");
                    } else {
                        documentRef.set(getMap(obj.get(0))).addOnSuccessListener(aVoid -> {
                            Utils.showToast(context, "User Registered");
                        }).addOnFailureListener(error -> Utils.showToast(context, "Error " + error.getLocalizedMessage()));
                    }
                });
    }

    @Override
    public void deleteUser(String email) {
//        getDocumentRef(email).delete()
//                .addOnSuccessListener(aVoid -> Log.d(TAG, "deleteUser: User deleted"))
//                .addOnFailureListener(e -> Log.d(TAG, "deleteUser: Failed to delete mUser " + e.getLocalizedMessage()));
    }

    /***
     * update user
     * @param userMap holding user's information
     * @param documentRef user's document to update
     */
    @Override
    public void updateUser(Map<String, Object> userMap, DocumentReference documentRef) {
        documentRef.update(userMap);
    }

    /***
     * loggin user by email address
     * @param loginModel object hold user login info
     */
    @Override
    public void loginUserByEmail(final LoginModel loginModel) {

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference document = db.collection(Constants.USER_COLLECTION).document(loginModel.getEmailAddress());
        document.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                this.mUser = documentSnapshot.toObject(User.class);
                if (loginModel.getPassword().equals(mUser.getPassword())) {

                    Utils.showToast(context, "User logged in");
                    SharedPreferenceManager.saveEmail(context, this.mUser.getEmail());
                    NavUtil.moveToNextActivity(context, MainActivity.class);
                    ((LoginActivity) context).finish();

                } else {
                    Utils.showToast(context, "Check your login credentials and Re-try");
                }
            } else {
                ErrorHandler.showToast(context, "User not found in our records. Please register!");
            }
        }).addOnFailureListener(e -> {
            ErrorHandler.showToast(context, e.getLocalizedMessage());
        });
    }

    /***
     * add user's reported pothole and pothole image
     * @param pothole user pothole
     * @param imageFile pothole image file
     */
    public void addPotholeAndImage(Pothole pothole, File imageFile) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference documentRef = db.collection(Constants.USER_COLLECTION)
                .document(userEmail);
        documentRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                Log.d(TAG, "addPotholeAndImage: document exists. add pothole for the mUser");

                if (imageFile != null && pothole != null && pothole.getCoordinates() != null) {
                    Uri imageUri = Uri.fromFile(imageFile);
                    String path = "potholes/" + UUID.randomUUID().toString() + imageUri.getLastPathSegment();

                    StorageReference storageRef = mStorage.getReference(path);
                    UploadTask uploadTask = storageRef.putFile(imageUri);
                    uploadTask.addOnSuccessListener((Activity) context, taskSnapshot -> {

                        User user = documentSnapshot.toObject(User.class);

                        //get download url
                        Task<Uri> downloadUrl = taskSnapshot.getStorage().getDownloadUrl();
                        downloadUrl.addOnSuccessListener((Activity) context, uri -> {
                            //add url to url pothole url list
                            pothole.setPotholeUrl(uri.toString());
                            //add mUser pothole
                            user.getPotholes().add(pothole);
                            Map<String, Object> userMap = getMap(user);
                            updateUser(userMap, documentRef);

                            Utils.showToast(context, "Report was send!");

                        }).addOnFailureListener(error -> Utils.showToast(context, "Error: " + error.getLocalizedMessage()));

                    }).addOnFailureListener(error -> Utils.showToast(context, "Error: " + error.getLocalizedMessage()));
                } else {
                    Utils.showToast(context, "An error occurred while trying to upload. Please retry!");
                }

            } else {
                Log.d(TAG, "addPotholeAndImage: document for mUser does not exist");
            }
        }).addOnFailureListener(error -> Utils.showToast(context, "Error: " + error.getLocalizedMessage()));
    }

    /***
     * create map from user object
     * @param obj to create map from
     * @return Map
     */
    private Map<String, Object> getMap(Object obj) {
        Map<String, Object> userMap = new HashMap<>();

        /**
         * this is a very baaaad way of implementing things
         * @todo - will change this to an efficient implementation
         */
        if (obj instanceof User) {
            User user = (User) obj;

            userMap.put(Constants.DocumentFields.NAME, user.getName());
            userMap.put(Constants.DocumentFields.CELL_NUMBER, user.getCellNumber());
            userMap.put(Constants.DocumentFields.PASSWORD, user.getPassword());
            userMap.put(Constants.DocumentFields.EMAIL, user.getEmail());
            userMap.put(Constants.DocumentFields.GENDER, user.getGender());
            userMap.put(Constants.DocumentFields.ROLE, user.getRole());
            userMap.put(Constants.DocumentFields.SURNAME, user.getSurname());
            userMap.put(Constants.DocumentFields.ID_NUMBER, user.getIdNumber());
            userMap.put(Constants.DocumentFields.POTHOLES, user.getPotholes());
        } else {

            Constructor constructor = (Constructor) obj;
            userMap.put(Constants.DocumentFields.NAME, constructor.getFirstName());
            userMap.put(Constants.DocumentFields.CELL_NUMBER, constructor.getCellNumber());
            userMap.put(Constants.DocumentFields.PASSWORD, constructor.getPassword());
            userMap.put(Constants.DocumentFields.EMAIL, constructor.getEmailAddress());
            userMap.put(Constants.DocumentFields.GENDER, constructor.getGender());
            userMap.put(Constants.DocumentFields.ASSIGNED, constructor.isAssigned());
            userMap.put(Constants.DocumentFields.ROLE, constructor.getRole());
            userMap.put(Constants.DocumentFields.SURNAME, constructor.getLastName());
            userMap.put(Constants.DocumentFields.ID_NUMBER, constructor.getIdNumber());
            userMap.put(Constants.DocumentFields.POTHOLES, constructor.getPotholeList());

        }


        return userMap;
    }

}
