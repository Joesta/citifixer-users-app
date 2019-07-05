package com.dso30bt.project2019.potapp.activities;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

import com.dso30bt.project2019.potapp.R;
import com.dso30bt.project2019.potapp.models.LoginModel;
import com.dso30bt.project2019.potapp.repository.UserImpl;
import com.dso30bt.project2019.potapp.utils.ErrorHandler;
import com.dso30bt.project2019.potapp.utils.InternetConnectionHelper;
import com.dso30bt.project2019.potapp.utils.NavUtil;
import com.dso30bt.project2019.potapp.utils.SharedPreferenceManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;

/**
 * Created by Joesta on 2019/05/29.
 */
public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "LoginActivity";
    // login controls
    private EditText textInputEmailAddress;
    private EditText textInputPassword;
    private Button btnLogin;
    private Button btnSignUp;
    // members
    private String emailAddress;
    private String password;
    private boolean isValidUserInput;

    // firebase auth instance
    private FirebaseAuth mAuth;
    private DatabaseReference ref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // FirebaseApp.initializeApp(LoginActivity.this);
        // [START initialize_auth]
        // Initialize Firebase Auth
        //mAuth = FirebaseAuth.getInstance();
        // [END initialize_auth]

        // if (mAuth.getCurrentUser() != null) {
        //    gotoHome();
        // }

        initLoginControlViews();
        registerButtons();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart: It gets here then move on");
    }

    @Override
    protected void onResume() {
        super.onResume();
        isUserLoggedIn();
    }

    private void isUserLoggedIn() {
        String userEmail = SharedPreferenceManager.getUserEmail(LoginActivity.this);
        if (!TextUtils.isEmpty(userEmail) || userEmail != null) {
            NavUtil.moveToNextActivity(LoginActivity.this, MainActivity.class);
            finish();
        }
    }

    // initializes login control vies
    private void initLoginControlViews() {
        textInputEmailAddress = findViewById(R.id.textInputEmail);
        textInputPassword = findViewById(R.id.textInputPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnSignUp = findViewById(R.id.btnSignUp);
    }

    //register buttons to listener
    private void registerButtons() {
        this.btnLogin.setOnClickListener(this);
        this.btnSignUp.setOnClickListener(this);
    }

    //get inputs from controls
    private void getUserLoginInput() {
        emailAddress = textInputEmailAddress.getText().toString();
        password = textInputPassword.getText().toString();
    }

    private void validateUserLoginInput(String emailAddress, String password) {
        View focusView = null;
        boolean cancel = false;

        //check email validity
        if ((TextUtils.isEmpty(emailAddress)) || (TextUtils.getTrimmedLength(emailAddress) == 0)) {
            focusView = textInputEmailAddress;
            textInputEmailAddress.setError("Email is required");
            cancel = true;
        } else if (!isEmailAddress(emailAddress)) {
            focusView = textInputEmailAddress;
            textInputEmailAddress.setError("Invalid email");
            cancel = true;
        } else if ((TextUtils.isEmpty(password)) || (TextUtils.getTrimmedLength(password) == 0)) {
            focusView = textInputPassword;
            textInputPassword.setError("Password is required");
            cancel = true;
        } else if (!isPassword(password)) {
            focusView = textInputPassword;
            textInputPassword.setError("Password too short");
            cancel = true;
        }


        if (cancel) {
            // focus cursor on view with errors
            focusView.requestFocus();
        } else {
            // we have valid inputs
            isValidUserInput = true;
        }
    }

    private boolean isEmailAddress(String emailAddress) {
        return Patterns.EMAIL_ADDRESS.matcher(emailAddress).matches();
    }

    private boolean isPassword(String password) {
        return password.length() > 6; //todo - strengthen password
    }

    @Override
    public void onClick(View v) {
        //check for button clicked
        int tappedButtonId = v.getId();
        switch (tappedButtonId) {
            case R.id.btnLogin:
                Log.d(TAG, "onClick: attempting to login");
                attemptLogin();
                break;
            case R.id.btnSignUp:
                gotoSignUp();
                break;
            default:
                break;
        }
    }

    /**
     * goes to signUp activity
     */
    private void gotoSignUp() {
        Log.d(TAG, "gotoSignUp: We are going to signUp activity");
        NavUtil.moveToNextActivity(LoginActivity.this, SignUpActivity.class);
    }

    /**
     * goes to home screen
     */
    private void gotoHome() {
        NavUtil.moveToNextActivity(LoginActivity.this, MainActivity.class);
    }

    private void attemptLogin() {
        getUserLoginInput();
        validateUserLoginInput(this.emailAddress, this.password);
        if (isValidUserInput) {
            Log.d(TAG, "isValidUserInput: user inputs are acceptable. now check internet before anything stupid");
            //do we have internet?
            boolean hasInternetNetwork = InternetConnectionHelper.hasInternetConnection(LoginActivity.this);
            Log.d(TAG, "attemptLogin: checking internet");
            if (hasInternetNetwork) {
                Log.d(TAG, "attemptLogin: we have internet");
                //authenticate user
                authenticateUser(this.emailAddress, this.password);
            } else {
                Log.d(TAG, "hasInternetNetwork: We don't have internet. check your network");
                ErrorHandler.showToast(LoginActivity.this, "No internet access!");
            }
        }
    }

    private void authenticateUser(String emailAddress, String password) {
//        mAuth.signInWithEmailAndPassword(emailAddress, password).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
//            @Override
//            public void onComplete(@NonNull Task<AuthResult> task) {
//                if (!task.isSuccessful()) {
//                    ErrorHandler.showToast(LoginActivity.this, "Username or Password Incorrect");
//                } else {
//                    // we are suppose to login here bro. Just carryout your test though
//                    // Attach a listener to read the data at our users reference
//
//                    gotoHome();
//                }
//            }
//        });

        UserImpl userImp = new UserImpl(LoginActivity.this);
        userImp.loginUserByEmail(new LoginModel(emailAddress, password));
    }
}
