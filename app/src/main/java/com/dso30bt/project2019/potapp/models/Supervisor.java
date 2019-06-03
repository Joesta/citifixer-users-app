package com.dso30bt.project2019.potapp.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by Joesta on 2019/05/29.
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Supervisor {
    private int empId;
    private Pothole pothole;
    private String name;
    private String surname;
    private Constructor constructor;
}
