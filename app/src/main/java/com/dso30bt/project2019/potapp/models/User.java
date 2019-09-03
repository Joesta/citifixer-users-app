package com.dso30bt.project2019.potapp.models;

import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Created by Joesta on 2019/05/29.
 */

@Data
@ToString
@EqualsAndHashCode(callSuper = false)
public class User extends Person {
    private String imageUrl;
    private List<Report> reportList;

    public User() {
        super();
    }

    public User(String firstName, String lastName, String gender, String idNumber,
                String dob, String password, String emailAddress, Role role, String imageUrl, String cellNumber, List<Report> reportList) {

        super(0, firstName, lastName, gender, idNumber, dob, password, emailAddress, role, cellNumber);
        this.imageUrl = imageUrl;
        this.reportList = reportList;
    }
}