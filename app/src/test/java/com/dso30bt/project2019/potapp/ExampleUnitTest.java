package com.dso30bt.project2019.potapp;

import com.dso30bt.project2019.potapp.utils.IDNumberValidatorUtility;

import org.junit.Test;

import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {

    private String idNo = "9607021197085";
    private static final String PASSWORD_PATTERN = "((?=.*[a-z])(?=.*\\d)(?=.*[A-Z])(?=.*[@#$%!]).{8,40})";
    //public static final String STRING_PATTERN = "[a-zA-Z]*";
    public static final String STRING_PATTERN ="^[a-zA-Z]{3,25}$";

    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void isCorrectIDNumber() {
        assertTrue(IDNumberValidatorUtility.extractInformation("9607021197085").isValid());
    }

    @Test
    public void dob_isCorrect() {
        Date birthDate = IDNumberValidatorUtility.extractInformation("9204145958087").getBirthDate();
        Date comparableDate = IDNumberValidatorUtility.compareDateTo("19920414");


        assertEquals(comparableDate, birthDate);

    }

    @Test
    public void is_Male() {
        boolean male = IDNumberValidatorUtility.extractInformation(idNo).isMale();
        assertTrue(male);
    }

    @Test
    public void is_Citizen() {
        boolean citizen = IDNumberValidatorUtility.extractInformation(idNo).isCitizen();
        assertTrue(citizen);
    }

    @Test
    public void is_Password_correct() {
        Pattern pattern = Pattern.compile(PASSWORD_PATTERN);
        Matcher matcher = pattern.matcher("Skack#1");
        assertTrue(matcher.matches());
    }

    @Test
    public void isFirstName_valid() {
        String firstName = "1Joe";
        Pattern pattern = Pattern.compile(STRING_PATTERN);
        boolean validFirstName = pattern.matcher(firstName).matches();
        assertTrue(validFirstName);
    }
}