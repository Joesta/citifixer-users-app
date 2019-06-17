package com.dso30bt.project2019.potapp.models;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Created by Joesta on 2019/06/01.
 */
@Data
@AllArgsConstructor
public class UserReport {
    private String reportId;
    private Date timeStamp;
    //private Address address;
    private String description;
    private Coordinates coordinates;
}
