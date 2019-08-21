package com.dso30bt.project2019.potapp.utils;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.widget.Toast;

import com.dso30bt.project2019.potapp.R;
import com.dso30bt.project2019.potapp.models.Constructor;
import com.dso30bt.project2019.potapp.models.Person;
import com.dso30bt.project2019.potapp.models.Role;
import com.dso30bt.project2019.potapp.models.Status;
import com.dso30bt.project2019.potapp.models.User;
import com.dso30bt.project2019.potapp.models.enums.RoleEnum;
import com.dso30bt.project2019.potapp.models.enums.StatusEnum;

import br.com.simplepass.loading_button_lib.customViews.CircularProgressButton;

/**
 * Created by Joesta on 2019/06/15.
 */
public class Utils {
    public static void showToast(Context context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
    }


    public static Person generatePerson(String firstName, String lastName, String gender, String idNumber, String dob, Role role, String imageUrl, String password, String emailAddress, String cellNumber) {
        int result = checkRole(role);
        if (result == 1) {
            //@Todo query for auto id
            return new User(firstName, lastName, gender, idNumber, dob, password, emailAddress, role, imageUrl, cellNumber);
        } else {
            //@Todo query for auto id
            Status status = generatePersonStatus(StatusEnum.AVAILABLE.value); // create a constructor with default status set to available
            return new Constructor(firstName, lastName, gender, idNumber, dob, password, emailAddress, role, imageUrl, status, cellNumber);
        }
    }

    private static int checkRole(Role role) {
        //@Todo - query for auto id
        return role.getRoleDescription()
                .equals(RoleEnum.USER.role) ? 1 : 2;
    }

    private static Status generatePersonStatus(String statusDescription) {
        //@Todo - query for auto id
        return new Status(statusDescription);
    }

    public static Role generateRole(String roleDescription) {
        return new Role(roleDescription);
    }
}
