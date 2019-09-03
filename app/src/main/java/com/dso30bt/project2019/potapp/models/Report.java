package com.dso30bt.project2019.potapp.models;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * Created by Joesta on 2019/08/02.
 */

@Data
@ToString
@NoArgsConstructor
public class Report implements Serializable {
    private int reportId;
    private int userId;
    private int statusId;
    private int constructorId;
    private int potholeId;
    private String reportedBy;
    private String assignedBy;
    private String description;
    private User user;
    private Constructor constructor;
    private Status status;
    private Date reportDate;
    private Pothole pothole;
}
