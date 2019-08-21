package com.dso30bt.project2019.potapp.activities;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.dso30bt.project2019.potapp.R;
import com.dso30bt.project2019.potapp.models.LoginModel;
import com.dso30bt.project2019.potapp.repository.UserImpl;
import com.dso30bt.project2019.potapp.utils.NavUtil;
import com.dso30bt.project2019.potapp.utils.SharedPreferenceManager;

import androidx.appcompat.app.AppCompatActivity;

/**
 * Created by Joesta on 2019/05/29.
 */
public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "LoginActivity";
    public Button btnLogin;
    // login controls
    private EditText textInputEmailAddress;
    private EditText textInputPassword;
    private Button btnSignUp;
    // members
    private String emailAddress;
    private String password;
    private boolean isValidUserInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //this.getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        initUI();
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

    /***
     * check is user is logged in before using the app
     */
    private void isUserLoggedIn() {
        String userEmail = SharedPreferenceManager.getEmail(LoginActivity.this);
        if (!TextUtils.isEmpty(userEmail) || userEmail != null) {
            NavUtil.moveToNextActivity(LoginActivity.this, MainActivity.class);
            finish();
        }
    }

    /***
     * initializes UI widgets
     */
    private void initUI() {
        textInputEmailAddress = findViewById(R.id.textInputEmail);
        textInputPassword = findViewById(R.id.textInputPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnSignUp = findViewById(R.id.btnSignUp);
    }

    /***
     * register buttons to click listener
     */
    private void registerButtons() {
        this.btnLogin.setOnClickListener(this);
        this.btnSignUp.setOnClickListener(this);
    }

    /***
     * get user login input from login activity
     */
    private void getUserLoginInput() {
        emailAddress = textInputEmailAddress.getText().toString();
        password = textInputPassword.getText().toString();
    }

    /***
     * valudate user input
     * @param emailAddress user input email
     * @param password user input password
     */
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
        } else if (!isValidPasswordLength(password)) {
            focusView = textInputPassword;
            textInputPassword.setError("Invalid Password. Password must be at least 8 characters long ");
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

    /***
     * validate email address
     * @param emailAddress user email address
     * @return true if valid. false otherwise
     */
    private boolean isEmailAddress(String emailAddress) {
        return Patterns.EMAIL_ADDRESS.matcher(emailAddress).matches();
    }

    /***
     * check password length
     * @param password user password
     * @return true if password meet condition. false otherwise
     */
    private boolean isValidPasswordLength(String password) {
        return password.length() >= 8;
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

    private void attemptLogin() {
        getUserLoginInput();
        validateUserLoginInput(this.emailAddress, this.password);

        if (isValidUserInput) {
            Log.d(TAG, "isValidUserInput: user inputs are acceptable. now check internet before anything stupid");
            // user input are OK. Proceed
            authenticateUser(this.emailAddress, this.password);
        }
    }

    /***
     * login user by email and password
     * @param emailAddress user email
     * @param password user password
     */
    private void authenticateUser(String emailAddress, String password) {
        // start login button animation
        //Utils.startAnimation(true, btnLogin, LoginActivity.this);

        UserImpl userImp = new UserImpl(LoginActivity.this);
        userImp.loginUserByEmail(new LoginModel(emailAddress, password));

    }
}
