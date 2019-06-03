package com.dso30bt.project2019.potapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;

import com.dso30bt.project2019.potapp.R;
import com.dso30bt.project2019.potapp.models.Pothole;
import com.dso30bt.project2019.potapp.models.User;
import com.dso30bt.project2019.potapp.models.UserReport;
import com.dso30bt.project2019.potapp.repository.UserImpl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/**
 * Created by Joesta on 2019/05/29.
 */
public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "SignUpActivity";

    //ui
    private EditText textInputName;
    private EditText textInputSurname;
    private EditText textInputEmailAddress;
    private EditText textInputPassword;
    private EditText textInputIdNumber;
    private EditText textInputCell;
    private Spinner spinner;
    private Button btnSingUp;
    private DatePicker datePicker;

    //members
    private String name;
    private String surname;
    private String emailAddress;
    private String password;
    private String idNumber;
    private String cellNumber;
    private String gender;
    private SimpleDateFormat simpleDateFormat;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        initUI();
        registerButton();
    }

    private void registerButton() {
        btnSingUp.setOnClickListener(this);
    }

    private void initUI() {
        textInputName = findViewById(R.id.textInputName);
        textInputSurname = findViewById(R.id.textInputSurname);
        textInputEmailAddress = findViewById(R.id.textInputRegEmail);
        textInputCell = findViewById(R.id.textInputCellNumber);
        textInputPassword = findViewById(R.id.textInputPassword);
        textInputIdNumber = findViewById(R.id.textInputIdNumber);
        spinner = findViewById(R.id.spinnerGender);
        btnSingUp = findViewById(R.id.btnSignUp);
        datePicker = findViewById(R.id.datePicker);
    }

    private void getUserRegistrationInput() {
        name = textInputName.getText().toString();
        surname = textInputSurname.getText().toString();
        emailAddress = textInputEmailAddress.getText().toString();
        password = textInputPassword.getText().toString();
        idNumber = textInputIdNumber.getText().toString();
        cellNumber = textInputCell.getText().toString();
        gender = spinner.getSelectedItem().toString();

        validateInput(name, surname, emailAddress, password, idNumber, cellNumber);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        goHome();
    }

    //goes back to login window
    private void goHome() {
        startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
    }

    @Override
    public void onClick(View v) {
        getUserRegistrationInput();

    }

    private void validateInput(String name, String surname, String emailAddress, String password, String idNumber, String cellNumber) {
        View focusView = null;
        boolean cancel = false;

        if (TextUtils.isEmpty(name) || TextUtils.getTrimmedLength(name) == 0) {
            focusView = textInputName;
            cancel = true;
            textInputName.setError("Name is required");
        } else if (TextUtils.isEmpty(surname) || TextUtils.getTrimmedLength(surname) == 0) {
            focusView = textInputSurname;
            cancel = true;
            textInputSurname.setError("Surname is required");
        } else if (TextUtils.isEmpty(idNumber) || TextUtils.getTrimmedLength(idNumber) == 0) {
            focusView = textInputIdNumber;
            cancel = true;
            textInputIdNumber.setError("ID Number is required");
        } else if (!isIdNumber(idNumber)) {
            focusView = textInputIdNumber;
            cancel = true;
            textInputIdNumber.setError("ID Number must be 13 digits long");
        } else if (!isAdult()) {
            focusView = textInputIdNumber;
            cancel = true;
            textInputIdNumber.setError("You must be 18 year older.");
        } else if (!isYearOfBirthValid(TextUtils.substring(idNumber, 0, 6))
                && isSACitizen(idNumber)) {

            focusView = textInputIdNumber;
            cancel = true;
            textInputIdNumber.setError("Invalid ID Number!");

        } else if (TextUtils.isEmpty(cellNumber) || TextUtils.getTrimmedLength(cellNumber) == 0) {
            focusView = textInputCell;
            cancel = true;
            textInputCell.setError("Cell number is required");
        } else if (!isCellNumber(cellNumber)) {
            focusView = textInputCell;
            cancel = true;
            textInputCell.setError("Cell must be 10 digits long");
        } else if (TextUtils.isEmpty(emailAddress) || TextUtils.getTrimmedLength(emailAddress) == 0) {
            focusView = textInputEmailAddress;
            cancel = true;
            textInputEmailAddress.setError("Email is required");
        } else if (!isEmail(emailAddress)) {
            focusView = textInputEmailAddress;
            cancel = true;
            textInputEmailAddress.setError("Invalid Email Address");
        } else if (TextUtils.isEmpty(password) || TextUtils.getTrimmedLength(password) == 0) {
            focusView = textInputPassword;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            // let the genius stuff happen
            int id = 0;
            List<Pothole> potholesList = new ArrayList<>();
            List<UserReport> userReports = new ArrayList<>();
            User user = new User(id, name, emailAddress, gender, surname, idNumber, password, cellNumber, potholesList, userReports);
            UserImpl userImpl = new UserImpl(SignUpActivity.this);
            userImpl.registerUser(user);
        }
    }

    private boolean isYearOfBirthValid(String yearOfBirth) {
        simpleDateFormat = new SimpleDateFormat("yy", Locale.getDefault());
        int year = datePicker.getYear();
        int formattedYear = Integer.valueOf(simpleDateFormat.format(year));
        int month = datePicker.getMonth();
        int day = datePicker.getDayOfMonth();

        return (formattedYear == Integer.valueOf(TextUtils.substring(yearOfBirth, 0, 2)))
                && (month == Integer.valueOf(TextUtils.substring(yearOfBirth, 2, 4)))
                && (day == Integer.valueOf(TextUtils.substring(yearOfBirth, 4, 6)));
    }

    private boolean isSACitizen(String idNumber) {
        return Integer.valueOf(TextUtils.substring(idNumber, 10, 11)) == 0;
    }

    private boolean isIdGenderFemale(String idNumber) {
        // male 5000 - 9999
        // female 0000 - 4999
        // ssss
        int ssss = 4999;
        return ssss <= Integer.valueOf(TextUtils.substring(idNumber, 6, 10));

    }

    private boolean isAdult() {
        Calendar calendar = Calendar.getInstance();
        int currentYear = calendar.get(Calendar.YEAR);
        int yearOBirth = datePicker.getYear();
        Log.d(TAG, "isAdult: " + yearOBirth);
        Log.d(TAG, "isAdult: " + (currentYear - yearOBirth >= 18));
        return currentYear - yearOBirth >= 18; //true if 18 years and older
    }

    private boolean isIdNumber(String idNumber) {
        return idNumber.length() == 13;
    }

    private boolean isEmail(String emailAddress) {
        return Patterns.EMAIL_ADDRESS.matcher(emailAddress).matches();
    }

    private boolean isCellNumber(String cellNumber) {
        return cellNumber.length() == 10;
    }
}
