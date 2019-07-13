package com.dso30bt.project2019.potapp.models;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by Joesta on 2019/05/29.*
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Constructor {
//    private String id;
    private String firstName;
    private String lastName;
    private String role;
    private String cellNumber;
    private String idNumber;
    private String emailAddress;
    private String password;
    private String gender;
    private boolean assigned = false;
    private List<Pothole> potholeList;
}
