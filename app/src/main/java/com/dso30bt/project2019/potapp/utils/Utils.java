package com.dso30bt.project2019.potapp.utils;

import android.content.Context;
import android.widget.Toast;

import com.dso30bt.project2019.potapp.models.Constructor;
import com.dso30bt.project2019.potapp.models.Coordinates;
import com.dso30bt.project2019.potapp.models.Person;
import com.dso30bt.project2019.potapp.models.Pothole;
import com.dso30bt.project2019.potapp.models.Report;
import com.dso30bt.project2019.potapp.models.Role;
import com.dso30bt.project2019.potapp.models.Status;
import com.dso30bt.project2019.potapp.models.User;
import com.dso30bt.project2019.potapp.models.enums.ConstructorStatusEnum;
import com.dso30bt.project2019.potapp.models.enums.ReportStatusEnum;
import com.dso30bt.project2019.potapp.models.enums.RoleEnum;

import java.sql.Timestamp;
import java.util.ArrayList;

/**
 * Created by Joesta on 2019/06/15.
 */
public class Utils {
    public static void showToast(Context context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
    }


    public static Person generatePerson(String firstName, String lastName, String gender, String idNumber, String dob, Role role, String imageUrl, String password, String emailAddress, String cellNumber) {
        int result = checkRole(role);
        if (result == 0) {
            return new User(firstName, lastName, gender, idNumber, dob, password, emailAddress, role, imageUrl, cellNumber, new ArrayList<>());
        } else {
            // create a constructor with default status set to available
            Status status = generateConstructorOrReportStatus(0, ConstructorStatusEnum.AVAILABLE.value);
            return new Constructor(firstName, lastName, gender, idNumber, dob, password, emailAddress, role, imageUrl, status, cellNumber, new ArrayList<>());
        }
    }

    private static int checkRole(Role role) {
        return role
                .getRoleDescription().equals(RoleEnum.User.role) ? 0 : 1;
    }

    public static Role generateRole(String roleDescription) {
        if (roleDescription.equalsIgnoreCase(RoleEnum.User.role)) {
            return new Role(0, roleDescription);
        }
        return new Role(1, roleDescription);
    }

    private static Status generateConstructorOrReportStatus(int statusId, String statusDescription) {
        return new Status(statusId, statusDescription);
    }

    public static Report generateReport(int reportId, String description, User user, Pothole pothole) {

        // create report with default status set to Available
        Status status = generateConstructorOrReportStatus(0, ReportStatusEnum.Available.toString());
        // current system timestamp
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        // reporter's fullname
        //String fullName = user.getFirstName() + " " + user.getLastName();

        Report report = new Report();
        report.setReportId(reportId);
        report.setDescription(description);
        //report.setReportedBy("Joesta"); // get user from param
        //report.setUserId(user.getUserId());
        //report.setUser(new User());
        report.setPotholeId(pothole.getPotholeId());
        report.setPothole(pothole);
        report.setReportDate(timestamp);
        report.setStatus(status);
        report.setStatusId(status.getStatusId());
        report.setAssignedBy("not-assigned");

        return report;
    }

    public static Pothole generatePothole(String description, String imageUrl, Coordinates coordinates) {
        Pothole pothole = new Pothole();
        pothole.setPotholeUrl(imageUrl);
        pothole.setDescription(description);
        pothole.setCoordinates(coordinates);

        return pothole;
    }
}
