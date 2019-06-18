package com.dso30bt.project2019.potapp.repository;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.dso30bt.project2019.potapp.CustomApplication;
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
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Created by Joesta on 2019/05/30.
 */
public class UserImpl implements IUserRepository {

    private static final String TAG = "UserImpl";
    FirebaseStorage storage;
    DatabaseReference usersRef;
    // FirebaseDatabase database = FirebaseDatabase.getInstance();
    // DatabaseReference ref = database.getReference("user/");
    private Context context;
    private FirebaseFirestore db;
    private boolean flagUserExist;
    private User user;
    private String userEmail;

    public UserImpl(Context context) {
        this.context = context;
        final CustomApplication app = CustomApplication.getInstance();
        db = app.getDbInstance();
        userEmail = SharedPreferenceManager.getUserEmail(context);
    }

    @Override
    public void registerUser(User user) {
        //check if user exist
        int flag = searchUser(user.getEmail());
        if (flag == 1) {
            getDocumentRef(user.getEmail()).set(loadMap(user)).addOnSuccessListener(aVoid -> {
                Log.d(TAG, "User was successfully added");
                NavUtil.moveToNextActivity(context, LoginActivity.class);
            }).addOnFailureListener(e -> {
                Log.d(TAG, "Error has occurred " + e.getMessage());
                ErrorHandler.showToast(context, "There was a problem while trying to register!");
            });
        } else {
            Log.d(TAG, "registerUser: " + flagUserExist);
            ErrorHandler.showToast(context, "User already exist");
        }
    }

    @Override
    public int searchUser(String email) {
        getDocumentRef(email).get()
                .addOnSuccessListener(aVoid -> flagUserExist = true)
                .addOnFailureListener(e -> flagUserExist = false);
        Log.d(TAG, "searchUser: Flag is " + flagUserExist);
        return flagUserExist ? 0 : 1;
    }

    @Override
    public void deleteUser(String email) {
        getDocumentRef(email).delete()
                .addOnSuccessListener(aVoid -> Log.d(TAG, "deleteUser: User deleted"))
                .addOnFailureListener(e -> Log.d(TAG, "deleteUser: Failed to delete user " + e.getLocalizedMessage()));
    }

    @Override
    public void updateUser(Map<String, Object> userMap, String email) {
        getDocumentRef(email).update(userMap);
    }

    @Override
    public void loginUserByEmail(final LoginModel loginModel) {
        Task<DocumentSnapshot> snapShotTask = getDocSnapShotTask(loginModel.getEmailAddress());

        snapShotTask.addOnCompleteListener(task -> {

            DocumentSnapshot document = task.getResult();
            assert document != null;

            if (document.exists()) {
                Log.d(TAG, "onComplete: document exists " + document.getData());
                final String targetUserPassword = (String) Objects.requireNonNull(document.getData())
                        .get(Constants.DocumentFields.PASSWORD);

                if (Objects.equals(targetUserPassword, loginModel.getPassword())) {
                    //save user email to preferences
                    SharedPreferenceManager.saveUserEmail(context, loginModel.getEmailAddress());
                    NavUtil.moveToNextActivity(context, MainActivity.class);
                    // @Todo - uncomment the line to kill login activity after successful login
                    ((Activity) context).finish();

                } else {
                    ErrorHandler.showToast(context, "Check your login credentials and Re-try");
                }

            } else {
                Log.d(TAG, "onComplete: document does not exist ");
                ErrorHandler.showToast(context, "User not found in our records. Please register!");
            }
        });
    }

    @Override
    public User getUser(String email) {
        final List<User> userHolder = new ArrayList<>();
        /*
            since we are interested in one object in the  list holder, we need to
            clear it before adding
         */
        userHolder.clear();

        Task<DocumentSnapshot> docSnapShotTask = getDocSnapShotTask(email);

        DocumentSnapshot documentResults = docSnapShotTask.getResult();
        assert documentResults != null;

        Map<String, Object> data = documentResults.getData();
        assert data != null;
        userHolder.add(generateUserFromMap(data));
        System.out.println("UserHolder" + userHolder.get(0).getName());


        return userHolder.size() > 0 ? userHolder.get(0) : null;
    }

    @Override
    public void addPothole(Pothole pothole, File imageFile) {
        user = getUser(userEmail);
        //pothole.setUser(user);

        if (user != null) {
            user.getPotholeList().add(pothole);
            updateUser(loadMap(user), user.getEmail());
            // uploadPotholeImage(imageFile);
        } else {
            System.out.println("Failed to update");
        }
    }

//    private void uploadPotholeImage(File imageFile) {
//        if (imageFile != null) {
//            storage = FirebaseStorage.getInstance();
//
//            StorageReference storageRef = storage.getReference();
//            StorageReference imageRef = storageRef.child("potholeImages/" + userEmail + imageFile.getName());
//            Uri uri = Uri.fromFile(imageFile);
//
//            try {
//                UploadTask uploadTask = imageRef.putFile(uri);
//                uploadTask.addOnSuccessListener(taskSnapshot -> {
//
//                    Task<Uri> downloadUrl = taskSnapshot.getStorage().getDownloadUrl();
//                    if (downloadUrl.isSuccessful()) {
//                        System.out.println("Upload task successful");
//                        downloadUrl.addOnSuccessListener(uri1 -> {
//                            usersRef.child("potholeImages/" + userEmail);
//                            Map<String, String> potholePicPath = new HashMap<>();
//                            if (uri1 != null) {
//                                potholePicPath.put("potholeImages", uri1.toString());
//                                usersRef.setValue(potholePicPath);
//                            }
//                        });
//                    }
//
//                }).addOnFailureListener(e -> {
//                    System.out.println("Image upload failed" + e.getLocalizedMessage());
//                });
//
//            } catch (NullPointerException npe) {
//                System.out.println(npe.getLocalizedMessage());
//            }
//        }
//    }

    private Map<String, Object> loadMap(User user) {
        Map<String, Object> userMap = new HashMap<>();
        userMap.put(Constants.DocumentFields.NAME, user.getName());
        userMap.put(Constants.DocumentFields.CELL_NUMBER, user.getCellNumber());
        userMap.put(Constants.DocumentFields.PASSWORD, user.getPassword());
        userMap.put(Constants.DocumentFields.EMAIL, user.getEmail());
        userMap.put(Constants.DocumentFields.GENDER, user.getGender());
        userMap.put(Constants.DocumentFields.SURNAME, user.getSurname());
        userMap.put(Constants.DocumentFields.ID_NUMBER, user.getIdNumber());
        userMap.put(Constants.DocumentFields.POTHOLES, user.getPotholeList());
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
        user.setPotholeList((List<Pothole>) map.get(Constants.DocumentFields.POTHOLES));
        System.out.println("Pothole list size " + user.getPotholeList().size());
        user.setUserReport((List<UserReport>) map.get(Constants.DocumentFields.REPORTS));
        System.out.println("Report list size " + user.getUserReport().size());

        return user;

    }

    public Map<String, Object> getUserMap(String userEmail) {

        Task<DocumentSnapshot> docSnapShotTask = getDocSnapShotTask(userEmail);
        final Map<String, Object> userMap = new HashMap<>();

        if (docSnapShotTask.isSuccessful()) {
            docSnapShotTask.addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    userMap.putAll(Objects.requireNonNull(documentSnapshot.getData()));
                } else {
                    Log.d(TAG, "getUserMap: document does not exist");
                }
            }).addOnFailureListener(error -> {
                Log.d(TAG, "getUserMap: something wrong happened: " + error.getLocalizedMessage());
            });
        }

        Log.d(TAG, "getUserMap: The user is: " + userMap.get(Constants.DocumentFields.NAME));
        return userMap;
    }

    /**
     * @param email email referencing a user's document
     * @return document task
     */
    private DocumentReference getDocumentRef(String email) {
        return db.collection(Constants.USER_COLLECTION).document(email);
    }

    /**
     * @param email email of user
     * @return document snapshot task
     */
    private Task<DocumentSnapshot> getDocSnapShotTask(String email) {
        return getDocumentRef(email).get();
    }
}
