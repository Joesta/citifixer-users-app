package com.dso30bt.project2019.potapp.models;

import java.io.Serializable;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Created by Joesta on 2019/08/02.
 */
@Data
@ToString
@EqualsAndHashCode
public class Person implements Serializable {
    private int userId;
    private String firstName;
    private String lastName;
    private String gender;
    private String idNumber;
    private String dob;
    private String password;
    private String emailAddress;
    private String cellNumber;
    private Role role;

    public Person() {
        super();
    }

    public Person(String firstName, String lastName, String gender, String idNumber, String dob, String password, String emailAddress, Role role, String cellNumber) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.gender = gender;
        this.idNumber = idNumber;
        this.dob = dob;
        this.password = password;
        this.emailAddress = emailAddress;
        this.role = role;
        this.cellNumber = cellNumber;
    }
}
