package com.dso30bt.project2019.potapp.models;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Created by Joesta on 2019/05/31.
 */

@Data
@AllArgsConstructor
public class LoginModel {
    private String emailAddress;
    private String password;
}
