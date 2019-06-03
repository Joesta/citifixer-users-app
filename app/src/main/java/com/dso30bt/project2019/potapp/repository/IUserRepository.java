package com.dso30bt.project2019.potapp.repository;


import com.dso30bt.project2019.potapp.models.LoginModel;
import com.dso30bt.project2019.potapp.models.User;

/**
 * Created by Joesta on 2019/05/30.
 */
public interface IUserRepository {
    void doesUserExist(String email);
    void registerUser(User user);
    void loginUserByEmail(LoginModel loginModel);
}
