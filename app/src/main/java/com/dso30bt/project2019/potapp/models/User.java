package com.dso30bt.project2019.potapp.models;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Created by Joesta on 2019/05/29.
 */

@Data
@ToString
@EqualsAndHashCode(callSuper=false)
public class User extends Person {
    private int id;
    private String imageUrl;

    public User() {
        super();
    }

    public User(String firstName, String lastName, String gender, String idNumber, String dob, String password, String emailAddress, Role role, String imageUrl, String cellNumber) {
        super(firstName, lastName, gender, idNumber, dob, password, emailAddress, role, cellNumber);
        this.imageUrl = imageUrl;
    }
}