package com.dso30bt.project2019.potapp.repository;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.dso30bt.project2019.potapp.CustomApplication;
import com.dso30bt.project2019.potapp.activities.LoginActivity;
import com.dso30bt.project2019.potapp.activities.MainActivity;
import com.dso30bt.project2019.potapp.models.LoginModel;
import com.dso30bt.project2019.potapp.models.User;
import com.dso30bt.project2019.potapp.utils.Constants;
import com.dso30bt.project2019.potapp.utils.ErrorHandler;
import com.dso30bt.project2019.potapp.utils.NavUtil;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Created by Joesta on 2019/05/30.
 */
public class UserImpl implements IUserRepository {

    private static final String TAG = UserImpl.class.getSimpleName();
    private Context context;
    private FirebaseFirestore db;
    private boolean flagUserExist;

    public UserImpl(Context context) {
        Log.d(TAG, "UserImpl: Constructor - Instantiating object");
        this.context = context;
        final CustomApplication app = CustomApplication.getInstance();
        db = app.getDbInstance();
        Log.d(TAG, "UserImpl: Constructor - Instantiation done");
    }

    @Override
    public void doesUserExist(String email) {
        Task<DocumentSnapshot> docSnapshot
                = db.collection(Constants.USER_COLLECTION).document(email).get();

        DocumentSnapshot result = docSnapshot.getResult();

        docSnapshot.addOnCompleteListener((documentSnapShot) -> {
            if (documentSnapShot.isSuccessful()) {
                assert result != null;
                final String targetEmailAddress = (String) Objects.requireNonNull(result.getData())
                        .get(Constants.DocumentFields.EMAIL);
                if (Objects.equals(email, targetEmailAddress)) {
                    flagUserExist = true;
                }
            }
        });
    }

    @Override
    public void registerUser(User user) {
        //check if user exist
        doesUserExist(user.getEmail());
        if (!flagUserExist) { // if flagUserExist = false
            //add user to database
            Task<Void> newUser = db.collection(Constants.USER_COLLECTION).document(user.getEmail()).set(getUserMap(user));
            Log.d(TAG, "registerUser: is the task successful? " + Boolean.toString(newUser.isSuccessful()));
            Log.d(TAG, "registerUser: created newUser Object");

            newUser.addOnSuccessListener(aVoid -> {
                Log.d(TAG, "User was successfully added");
                NavUtil.moveToNextActivity(context, LoginActivity.class);
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d(TAG, "Error has occurred " + e.getMessage());
                    ErrorHandler.showToast(context, "There was a problem while trying to register!");
                }
            });
        } else {
            Log.d(TAG, "registerUser: " + flagUserExist);
            ErrorHandler.showToast(context, "User already exist");
        }
    }

    @Override
    public void loginUserByEmail(final LoginModel loginModel) {

        DocumentReference docRef
                = db.collection(Constants.USER_COLLECTION).document(loginModel.getEmailAddress());

        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {

                DocumentSnapshot document = task.getResult();
                assert document != null;

                if (document.exists()) {
                    Log.d(TAG, "onComplete: document exists " + document.getData());
                    final String targetUserPassword = (String) Objects.requireNonNull(document.getData())
                            .get(Constants.DocumentFields.PASSWORD);

                    if (Objects.equals(targetUserPassword, loginModel.getPassword())) {
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

            } else {
                Log.d(TAG, "Error reading user data ");
                String exception = Objects.requireNonNull(task.getException()).getMessage();
                Log.d(TAG, "Error reading user data " + exception);
                ErrorHandler.showToast(context, "Error reading user data " + exception);
            }
        });
    }

    private Map<String, Object> getUserMap(User user) {
        Map<String, Object> userMap = new HashMap<>();
        userMap.put(Constants.DocumentFields.NAME, user.getName());
        userMap.put(Constants.DocumentFields.CELL_NUMBER, user.getCellNumber());
        userMap.put(Constants.DocumentFields.PASSWORD, user.getPassword());
        userMap.put(Constants.DocumentFields.EMAIL, user.getEmail());
        userMap.put(Constants.DocumentFields.USER_ID, user.getUserId());
        userMap.put(Constants.DocumentFields.GENDER, user.getGender());
        userMap.put(Constants.DocumentFields.SURNAME, user.getSurname());
        userMap.put(Constants.DocumentFields.ID_NUMBER, user.getIdNumber());
        userMap.put(Constants.DocumentFields.POTHOLES, user.getPotholeList());
        userMap.put(Constants.DocumentFields.REPORTS, user.getUserReport());

        return userMap;
    }
}
