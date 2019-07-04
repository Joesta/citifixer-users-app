package com.dso30bt.project2019.potapp.repository;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.dso30bt.project2019.potapp.activities.LoginActivity;
import com.dso30bt.project2019.potapp.activities.MainActivity;
import com.dso30bt.project2019.potapp.models.LoginModel;
import com.dso30bt.project2019.potapp.models.Pothole;
import com.dso30bt.project2019.potapp.models.User;
import com.dso30bt.project2019.potapp.models.UserReport;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * Created by Joesta on 2019/05/30.
 */
public class UserImpl implements IUserRepository {

    private static final String TAG = "UserImpl";
    FirebaseStorage mStorage = FirebaseStorage.getInstance();
    private Context context;
    private User user;
    private String userEmail;

    public UserImpl(Context context) {
        this.context = context;
        userEmail = SharedPreferenceManager.getUserEmail(context);
    }

    @Override
    public void registerUser(User user) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference documentRef = db.collection(Constants.USER_COLLECTION).document(user.getEmail());

        documentRef.get()
                .addOnSuccessListener((Activity) context, documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Utils.showToast(context, "User already exist!");
                    } else {
                        documentRef.set(loadMap(user)).addOnSuccessListener(aVoid -> {
                            Utils.showToast(context, "User Registered");
                        }).addOnFailureListener(error -> Utils.showToast(context, "Error " + error.getLocalizedMessage()));
                    }
                });
    }

    @Override
    public void deleteUser(String email) {
//        getDocumentRef(email).delete()
//                .addOnSuccessListener(aVoid -> Log.d(TAG, "deleteUser: User deleted"))
//                .addOnFailureListener(e -> Log.d(TAG, "deleteUser: Failed to delete user " + e.getLocalizedMessage()));
    }

    @Override
    public void updateUser(Map<String, Object> userMap, DocumentReference documentRef) {
        documentRef.update(userMap);
    }

    @Override
    public void loginUserByEmail(final LoginModel loginModel) {

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference document = db.collection(Constants.USER_COLLECTION).document(loginModel.getEmailAddress());
        document.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                this.user = documentSnapshot.toObject(User.class);
                if (loginModel.getPassword().equals(user.getPassword())) {
                    Utils.showToast(context, "User logged in");
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

    public void addPotholeAndImage(Pothole pothole, File imageFile) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference documentRef = db.collection(Constants.USER_COLLECTION)
                .document(SharedPreferenceManager.getUserEmail(context));
        documentRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                Log.d(TAG, "addPotholeAndImage: document exists. add pothole for the user");

                if (imageFile != null) {
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
                            pothole.getDownloadUrlList().add(uri.toString());
                            //add user pothole
                            user.getPotholes().add(pothole);
                            Map<String, Object> userMap = loadMap(user);
                            updateUser(userMap, documentRef);

                        }).addOnFailureListener(error -> Utils.showToast(context, "Error: " + error.getLocalizedMessage()));;
                    }).addOnFailureListener(error -> Utils.showToast(context, "Error: " + error.getLocalizedMessage()));
                }

            } else {
                Log.d(TAG, "addPotholeAndImage: document for user does not exist");
            }
        }).addOnFailureListener(error -> Utils.showToast(context, "Error: " + error.getLocalizedMessage()));
    }

    private Map<String, Object> loadMap(User user) {
        Map<String, Object> userMap = new HashMap<>();
        userMap.put(Constants.DocumentFields.NAME, user.getName());
        userMap.put(Constants.DocumentFields.CELL_NUMBER, user.getCellNumber());
        userMap.put(Constants.DocumentFields.PASSWORD, user.getPassword());
        userMap.put(Constants.DocumentFields.EMAIL, user.getEmail());
        userMap.put(Constants.DocumentFields.GENDER, user.getGender());
        userMap.put(Constants.DocumentFields.SURNAME, user.getSurname());
        userMap.put(Constants.DocumentFields.ID_NUMBER, user.getIdNumber());
        userMap.put(Constants.DocumentFields.POTHOLES, user.getPotholes());
        userMap.put(Constants.DocumentFields.REPORTS, user.getUserReport());

        return userMap;
    }

    /**
     * @param map - map containing user data
     * @return user object
     */
    private User generateUserFromMap(Map<String, Object> map) {

        User user = new User();
        user.setName(Objects.requireNonNull(map.get(Constants.DocumentFields.NAME)).toString());
        System.out.println(user.getName());
        user.setSurname(Objects.requireNonNull(map.get(Constants.DocumentFields.SURNAME)).toString());
        System.out.println(user.getSurname());
        user.setGender(Objects.requireNonNull(map.get(Constants.DocumentFields.GENDER)).toString());
        System.out.println(user.getGender());
        user.setIdNumber(Objects.requireNonNull(map.get(Constants.DocumentFields.ID_NUMBER)).toString());
        System.out.println(user.getIdNumber());
        user.setCellNumber(Objects.requireNonNull(map.get(Constants.DocumentFields.CELL_NUMBER)).toString());
        System.out.println(user.getCellNumber());
        user.setEmail(Objects.requireNonNull(map.get(Constants.DocumentFields.EMAIL)).toString());
        System.out.println(user.getEmail());
        user.setPassword(Objects.requireNonNull(map.get(Constants.DocumentFields.PASSWORD)).toString());
        System.out.println(user.getPassword());
        user.setPotholes((List<Pothole>) map.get(Constants.DocumentFields.POTHOLES));
        System.out.println("Pothole list size " + user.getPotholes().size());
        user.setUserReport((List<UserReport>) map.get(Constants.DocumentFields.REPORTS));
        System.out.println("Report list size " + user.getUserReport().size());

        return user;

    }

    public Map<String, Object> getUserMap(String userEmail) {

//        Task<DocumentSnapshot> docSnapShotTask = getDocSnapShotTask(userEmail);
//        final Map<String, Object> userMap = new HashMap<>();
//
//        if (docSnapShotTask.isSuccessful()) {
//            docSnapShotTask.addOnSuccessListener(documentSnapshot -> {
//                if (documentSnapshot.exists()) {
//                    userMap.putAll(Objects.requireNonNull(documentSnapshot.getData()));
//                } else {
//                    Log.d(TAG, "getUserMap: document does not exist");
//                }
//            }).addOnFailureListener(error -> {
//                Log.d(TAG, "getUserMap: something wrong happened: " + error.getLocalizedMessage());
//            });
//        }
//
//        Log.d(TAG, "getUserMap: The user is: " + userMap.get(Constants.DocumentFields.NAME));
//        return userMap;
        return null;
    }
//
//    /**
//     * @param email email referencing a user's document
//     * @return document task
//     */
//    private DocumentReference getDocumentRef(String email) {
//        return FirebaseFirestore.getInstance().collection(Constants.USER_COLLECTION).document(email);
//    }
//
//    /**
//     * @param email email of user
//     * @return document snapshot task
//     */
//    private Task<DocumentSnapshot> getDocSnapShotTask(String email) {
//        System.out.println("getDocSnapShotTask " + getDocumentRef(email).get());
//        return getDocumentRef(email).get();
//    }
}
