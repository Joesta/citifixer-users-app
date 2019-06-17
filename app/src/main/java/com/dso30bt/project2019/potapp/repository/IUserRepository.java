package com.dso30bt.project2019.potapp.repository;


import android.net.Uri;

import com.dso30bt.project2019.potapp.models.LoginModel;
import com.dso30bt.project2019.potapp.models.Pothole;
import com.dso30bt.project2019.potapp.models.User;

import java.io.File;
import java.util.Map;

/**
 * Created by Joesta on 2019/05/30.
 */
public interface IUserRepository {

    void registerUser(User user);

    int searchUser(String email);

    void deleteUser(String email);

    void updateUser(Map<String, Object> userMap, String email);

    void loginUserByEmail(LoginModel loginModel);

    User getUser(String email);

    void addPothole(Pothole pothole, File imageFile);
}
