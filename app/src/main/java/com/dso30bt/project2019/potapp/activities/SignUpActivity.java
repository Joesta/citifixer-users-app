package com.dso30bt.project2019.potapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;

import com.dso30bt.project2019.potapp.R;
import com.dso30bt.project2019.potapp.models.Person;
import com.dso30bt.project2019.potapp.models.Role;
import com.dso30bt.project2019.potapp.repository.UserImpl;
import com.dso30bt.project2019.potapp.utils.Constants;
import com.dso30bt.project2019.potapp.utils.IDNumberValidatorUtility;
import com.dso30bt.project2019.potapp.utils.PasswordValidator;
import com.dso30bt.project2019.potapp.utils.Utils;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.regex.Pattern;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;


/**
 * Created by Joesta on 2019/05/29.
 */
public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "SignUpActivity";

    // widgets
    /*edit text*/
    private EditText textInputName;
    private EditText textInputSurname;
    private EditText textInputEmailAddress;
    private EditText textInputPassword;
    private EditText textInputIdNumber;
    private EditText textInputCell;
    private EditText textInputConfirmPassword;

    /*spinner*/
    private Spinner spinnerGender;
    private Spinner spinnerRole;

    /*button*/
    private Button btnSingUp;

    /*date picker*/
    private DatePicker datePicker;

    /*vars*/
    private String gender;
    private String role;
    private String status;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        //gives constructor default status
        status = getResources()
                .getStringArray(R.array.constructor_status)[0];

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
        textInputConfirmPassword = findViewById(R.id.textInputConfirmPassword);
        spinnerGender = findViewById(R.id.spinnerGender);
        spinnerRole = findViewById(R.id.spinnerRole);
        btnSingUp = findViewById(R.id.btnSignUp);
        datePicker = findViewById(R.id.datePicker);
    }

    private void getUserRegistrationInput() {
        //members
        String firstName = textInputName.getText().toString();
        String lastName = textInputSurname.getText().toString();
        gender = spinnerGender.getSelectedItem().toString();
        String idNumber = textInputIdNumber.getText().toString();
        String emailAddress = textInputEmailAddress.getText().toString();
        String password = textInputPassword.getText().toString();
        String confirmPassword = textInputConfirmPassword.getText().toString();
        String cellNumber = textInputCell.getText().toString();
        role = spinnerRole.getSelectedItem().toString();

        validateInput(firstName, lastName, emailAddress, password, confirmPassword, idNumber, cellNumber);
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
        int id = v.getId();
        switch (id) {
            case R.id.btnSignUp:
                getUserRegistrationInput();
                break;
            default:
                break;
        }
    }

    private void validateInput(String firstName, String lastName, String emailAddress, String password, String confirmPassword, String idNumber, String cellNumber) {
        View focusView = null;
        boolean cancel = false;

        PasswordValidator passwordValidator = new PasswordValidator();

        if (TextUtils.isEmpty(firstName) || TextUtils.getTrimmedLength(firstName) == 0) {
            focusView = textInputName;
            cancel = true;
            textInputName.setError("Name is required");

        } else if (!isFirstNameValid(firstName)) {
            focusView = textInputName;
            cancel = true;
            textInputName.setError("First name must contain only alphanumeric and at least 3 characters long");

        } else if (TextUtils.isEmpty(lastName) || TextUtils.getTrimmedLength(lastName) == 0) {
            focusView = textInputSurname;
            cancel = true;
            textInputSurname.setError("Surname is required");

        } else if (!isLastNameValid(lastName)) {
            focusView = textInputSurname;
            cancel = true;
            textInputSurname.setError("Last name must contain only alphanumeric and at least 3 characters long. E.g Doe");

        } else if (!isAdult()) {
            focusView = textInputIdNumber;
            cancel = true;
            textInputIdNumber.setError("You must be 18 year older.");

        } else if (TextUtils.isEmpty(idNumber) || TextUtils.getTrimmedLength(idNumber) == 0) {
            focusView = textInputIdNumber;
            cancel = true;
            textInputIdNumber.setError("ID Number is required");

        } else if (!isIdNumber(idNumber)) {
            focusView = textInputIdNumber;
            cancel = true;
            textInputIdNumber.setError("ID Number must be 13 digits long");

        } else if (!IDNumberValidatorUtility.extractInformation(idNumber).isValid()) {
            focusView = textInputIdNumber;
            cancel = true;
            textInputIdNumber.setError("Invalid ID Number");

        } else if (!gender.equals(getGender(idNumber))) {
            Utils.showToast(this, "Gender does not match!");
            return;

        } else if (IDNumberValidatorUtility.extractInformation(idNumber).getBirthDate()
                .compareTo(IDNumberValidatorUtility.compareDateTo(getUserDoB())) != 0) {
            Utils.showToast(this, "Date of Birth does not match ID!");
            return;

        } else if (TextUtils.isEmpty(cellNumber) || TextUtils.getTrimmedLength(cellNumber) == 0) {
            focusView = textInputCell;
            cancel = true;
            textInputCell.setError("Cell number is required");

        } else if (!isCellNumberLength(cellNumber)) {
            focusView = textInputCell;
            cancel = true;
            textInputCell.setError("Cell must be 10 digits long");

        } else if (!isCellNumberPrefix(cellNumber)) {
            focusView = textInputCell;
            cancel = true;
            textInputCell.setError("Invalid cell number!");

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
            textInputPassword.setError("Password is required");

        } else if (!passwordValidator.validate(password)) {
            focusView = textInputPassword;
            cancel = true;
            textInputPassword.setError("Invalid password combination. \nPassword require at least 8 mixed characters. E.g n!k@sn1Kos");
        } else {
            if (!password.equalsIgnoreCase(confirmPassword)) {
                focusView = textInputConfirmPassword;
                cancel = true;
                textInputConfirmPassword.setError("Password do not match!");
            }
        }

        if (cancel) {
            focusView.requestFocus();

        } else {
            // create role object
            Role userRole = Utils.generateRole(role);
            String dob = getUserDoB();
            // create a person [ Road User or Constructor based on role ]
            Person person = Utils.generatePerson(firstName, lastName, gender, idNumber, dob, userRole, "", password, emailAddress, cellNumber);
            UserImpl userImpl = new UserImpl(SignUpActivity.this);
            userImpl.registerUser(person);

        }
    }

    private String getUserDoB() {
        int year = datePicker.getYear();
        int month = datePicker.getMonth() + 1;
        int day = datePicker.getDayOfMonth();

        // prefix month 1 - 9 with 0;
        String strMonth = (month < 10) ? "0" + month : String.valueOf(month);

        // prefix day 1 - 9 with 0;
        String strDay = (day < 10) ? "0" + day : String.valueOf(day);

        return year + "" + strMonth + "" + strDay;
    }

    private String getGender(String idNumber) {
        boolean isMale = IDNumberValidatorUtility.extractInformation(idNumber).isMale();
        return isMale ? "Male" : "Female";
    }


    private boolean isAdult() {
        Calendar calendar = Calendar.getInstance();
        int currentYear = calendar.get(Calendar.YEAR);
        int yearOBirth = datePicker.getYear();
        Log.d(TAG, "isAdult: " + yearOBirth);
        Log.d(TAG, "isAdult: " + currentYear + " - " + yearOBirth + " = " + (currentYear - yearOBirth >= 18));
        return currentYear - yearOBirth >= 18; //true if 18 years and older
    }

    private boolean isIdNumber(String idNumber) {
        return idNumber.length() == 13;
    }

    private boolean isEmail(String emailAddress) {
        return Patterns.EMAIL_ADDRESS.matcher(emailAddress).matches();
    }

    private boolean isCellNumberLength(String cellNumber) {
        return cellNumber.length() == 10;
    }

    private boolean isCellNumberPrefix(String cellNumber) {
        List<String> cellPrefixList = Arrays.asList(getResources().getStringArray(R.array.cellphone_prefix));
        return cellPrefixList.contains(cellNumber.substring(0, 3));
    }

    private boolean isFirstNameValid(String firstName) {
        Pattern pattern = Pattern.compile(Constants.NAMES_PATTERN);
        return pattern.matcher(firstName).matches();

    }

    private boolean isLastNameValid(String lastName) {
        Pattern pattern = Pattern.compile(Constants.NAMES_PATTERN);
        return pattern.matcher(lastName).matches();

    }
}
