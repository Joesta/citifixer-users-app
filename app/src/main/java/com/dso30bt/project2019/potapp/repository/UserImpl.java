package com.dso30bt.project2019.potapp.repository;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.dso30bt.project2019.potapp.activities.LoginActivity;
import com.dso30bt.project2019.potapp.activities.MainActivity;
import com.dso30bt.project2019.potapp.interfaces.IFirebase;
import com.dso30bt.project2019.potapp.models.Constructor;
import com.dso30bt.project2019.potapp.models.LoginModel;
import com.dso30bt.project2019.potapp.models.Person;
import com.dso30bt.project2019.potapp.models.Pothole;
import com.dso30bt.project2019.potapp.models.Report;
import com.dso30bt.project2019.potapp.models.User;
import com.dso30bt.project2019.potapp.utils.Constants;
import com.dso30bt.project2019.potapp.utils.FirebaseHelper;
import com.dso30bt.project2019.potapp.utils.NavUtil;
import com.dso30bt.project2019.potapp.utils.SharedPreferenceManager;
import com.dso30bt.project2019.potapp.utils.Utils;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.UUID;

import de.mateware.snacky.Snacky;

/**
 * Created by Joesta on 2019/05/30.
 */
public class UserImpl implements IUserRepository {

    private static final String TAG = "UserImpl";

    FirebaseStorage mStorage = FirebaseStorage.getInstance();
    private Context context;
    private Person mCurrentUser;
    private String userEmail;
    private String mCollection;
    private String mDocument;
    private FirebaseFirestore mDatabase = FirebaseFirestore.getInstance();

    /***
     * constructor
     * @param context of activity instantiating this object
     */
    public UserImpl(Context context) {
        this.context = context;
        userEmail = SharedPreferenceManager.getEmail(context);
        mDocument = userEmail;
    }


    @Override
    public void registerUser(Person appUser) {
        String collection = getCollection(appUser);

        /*start id increment*/
        new FirebaseHelper(context)
                .incrementId("id", Constants.UUID_COLLECTION, Constants.UIDDOC);
        /*end id increment*/

        FirebaseHelper helper = new FirebaseHelper(context);
        helper.incrementId(updatedFieldId -> {
            DocumentReference documentRef = mDatabase.collection(collection.toLowerCase())
                    .document(appUser.getEmailAddress());

            documentRef
                    .get()
                    .addOnSuccessListener((Activity) context, documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            Utils.showToast(context, "User already exist!");

                        } else {

                            appUser.setUserId(updatedFieldId);
                            documentRef.set(appUser);
                            Utils.showToast(context, "User Registered");
                            NavUtil.moveToNextActivity(context, LoginActivity.class);
                        }
                    }).addOnFailureListener(((Activity) context), error -> Utils.showToast(context, "Error " + error.getLocalizedMessage()));
        }, "id", Constants.UUID_COLLECTION, Constants.UIDDOC);


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
                        mCurrentUser = documentSnapshot.toObject(Person.class);

                        if (loginModel.getPassword().equals(mCurrentUser.getPassword())) {

                            SharedPreferenceManager.saveEmail(context, this.mCurrentUser.getEmailAddress(), 0);
                            NavUtil.moveToNextActivity(context, MainActivity.class);
                            ((LoginActivity) context).finish();

                        } else {
                            showSnackBar("Username or password incorrect", false);
                        }

                    } else {

                        mDatabase
                                .collection(Constants.CONSTRUCTOR_COLLECTION)
                                .document(loginModel.getEmailAddress())
                                .get()
                                .addOnSuccessListener(((Activity) context), snapshot -> {
                                    if (!snapshot.exists()) {
                                        showSnackBar("Account does not exist. Please click sign-up to create account", true);
                                    } else {
                                        mCurrentUser = snapshot.toObject(Person.class);
                                        if (loginModel.getPassword().equals(mCurrentUser.getPassword())) {
                                            SharedPreferenceManager.saveEmail(context, this.mCurrentUser.getEmailAddress(), 1);
                                            NavUtil.moveToNextActivity(context, MainActivity.class);
                                            ((LoginActivity) context).finish();
                                        }
                                    }
                                }).addOnFailureListener(((Activity) context), error -> Utils.showToast(context, "Error " + error.getLocalizedMessage()));
                    }
                }).addOnFailureListener(((Activity) context), error -> Utils.showToast(context, "Error " + error.getLocalizedMessage()));
    }

    private void showSnackBar(String msg, boolean error) {

        Snacky.Builder builder = Snacky.builder();
        builder.setActivity(((Activity) context));
        builder.setText(msg);
        builder.setDuration(Snacky.LENGTH_LONG);

        if (error) {
            builder.error().show();
        } else {
            builder.info().show();
        }
    }

    public void prepareReport(Report report) {
        Log.i(TAG, "prepareReport: method started running");

        //@Todo - user dynamic collections for users
        // fetch user
        // user is fetched using a callback method, since calls to firebase are Async
        // we then want to make sure we get the user through a callback
        fetchUser(user -> {
            final String fullName = user.getFirstName() + " " + user.getLastName();
            report.setUser(user);
            report.setUserId(user.getUserId());
            report.setReportedBy(fullName);

            saveReport(report, user);

        }, Constants.USER_COLLECTION, mDocument); // end fetch user
    }


    private void saveReport(final Report report, final User user) {

        FirebaseFirestore.getInstance()
                .collection(Constants.REPORT_COLLECTION)
                .document(UUID.randomUUID().toString())
                .set(report)
                .addOnCompleteListener(((Activity) context), task -> {
                    Log.i(TAG, "prepareReport: attached onSuccessful listener");
                    if (task.isComplete()) {

                        Pothole pothole = report.getPothole(); // user callback
                        preparePothole(pothole, user);
                        Log.i(TAG, "prepareReport: report saved, now trying to save pothole");

                        updateUserReport(report);

                    } else {
                        Log.i(TAG, "prepareReport: failed to save report");
                        Utils.showToast(context, "Failed " + task.getException().getLocalizedMessage());
                    }
                }); // end fetch report
    }

    private void preparePothole(Pothole pothole, User user) {

        pothole.setUser(user);
        if (pothole != null) {
            final File imageFile = new File(pothole.getPotholeUrl()); //@Todo - no side-effects, but fix it. Not making sense!!!!

            // get pothole download url
            if (imageFile != null && pothole != null && pothole.getCoordinates() != null) {
                Uri imageUri = Uri.fromFile(imageFile);
                String path = "potholes/" + UUID.randomUUID().toString() + imageUri.getLastPathSegment();

                StorageReference storageRef = mStorage.getReference(path);
                UploadTask uploadTask = storageRef.putFile(imageUri);
                uploadTask.addOnSuccessListener((Activity) context, taskSnapshot -> {

                    //get download url
                    Task<Uri> downloadUrl = taskSnapshot.getStorage().getDownloadUrl();
                    downloadUrl.addOnSuccessListener((Activity) context, uri -> {
                        //add url to url pothole url list
                        Log.i(TAG, "preparePothole: pothole download url is " + uri.toString());
                        pothole.setPotholeUrl(uri.toString());
                        savePothole(pothole);

                    }).addOnFailureListener(error -> Utils.showToast(context, "Error: " + error.getLocalizedMessage()));

                }).addOnFailureListener(error -> Utils.showToast(context, "Error: " + error.getLocalizedMessage()));
            }
        } else {
            Utils.showToast(context, "An error occurred while trying to upload. Please retry!");
        }

    }

    private void savePothole(Pothole pothole) {
        Log.i(TAG, "savePothole: started running");
        FirebaseFirestore.getInstance()
                .collection(Constants.POTHOLE_COLLECTION)
                .document(UUID.randomUUID().toString())
                .set(pothole)
                .addOnCompleteListener(((Activity) context), task -> {
                    if (task.isComplete()) {
                        Log.i(TAG, "preparePothole: pothole saved");
                    } else {
                        Log.e(TAG, "preparePothole: failed to save pothole. Reason:", task.getException());
                    }
                });
    }

    private void updateUserReport(Report report) {
        Log.i(TAG, "updateUserReport: program control transferred to updateUserReport");
        FirebaseFirestore
                .getInstance()
                .collection(Constants.USER_COLLECTION)
                .document(userEmail)
                .update("reportList", FieldValue.arrayUnion(report))
                .addOnSuccessListener(((Activity) context), aVoid -> Log.i(TAG, "updateUserReport: User report updated"))
                .addOnFailureListener(((Activity) context), error -> Log.e(TAG, "updateUserReport: failed to update user report"));
    }


    private void fetchUser(IFirebase.UserCallback callback, String collection, String document) {
        Log.d(TAG, "fetchUser: fetching user from database");
        FirebaseFirestore
                .getInstance()
                .collection(collection)
                .document(document).get()
                .addOnCompleteListener(((Activity) context), task -> {
                    Log.i(TAG, "fetchUser: addOnCompleteListener - fetching user");
                    if (task.isComplete()) {
                        Log.i(TAG, "fetchUser: task is completed successfully");
                        DocumentSnapshot result = task.getResult();
                        User user = result.toObject(User.class);
                        callback.onFetchedUser(user);
                        Log.i(TAG, "fetchUser: users first name " + user.getFirstName());
                    }

                });
        Log.i(TAG, "fetchUser: task finished. User is fetched.");
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
