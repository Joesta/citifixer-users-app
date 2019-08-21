package com.dso30bt.project2019.potapp.repository;


import com.dso30bt.project2019.potapp.models.LoginModel;
import com.dso30bt.project2019.potapp.models.Person;
import com.dso30bt.project2019.potapp.models.Pothole;
import com.dso30bt.project2019.potapp.models.User;
import com.google.firebase.firestore.DocumentReference;

import java.io.File;
import java.util.Map;

/**
 * Created by Joesta on 2019/05/30.
 */
public interface IUserRepository {

    void registerUser(Person appUser);

    void loginUserByEmail(final LoginModel loginModel);
/*
    void deleteUser(String email);

    void updateUser(Map<String, Object> userMap, DocumentReference documentRef);


    void addPotholeAndImage(Pothole pothole, File imageFile);

    */
}
