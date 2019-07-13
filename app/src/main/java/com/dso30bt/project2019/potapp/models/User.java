package com.dso30bt.project2019.potapp.models;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by Joesta on 2019/05/29.
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {
    private String name;
    private String email;
    private String gender;
    private String role;
    private String surname;
    private String IdNumber;
    private String password;
    private String cellNumber;
    private List<Pothole> potholes;
    private List<UserReport> userReport;
}
