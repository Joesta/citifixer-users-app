package com.dso30bt.project2019.potapp.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by Joesta on 2019/07/11.
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Engineer {
    private String id;
    private String firstName;
    private String lastName;
    private String emailAddress;
    private String cellNumber;
    private String idNumber;
    private String role;
}
