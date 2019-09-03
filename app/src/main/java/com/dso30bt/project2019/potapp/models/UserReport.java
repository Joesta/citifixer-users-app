package com.dso30bt.project2019.potapp.models;

import java.io.Serializable;
import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * Created by Joesta on 2019/06/01.
 */
@Data
@ToString
@NoArgsConstructor
public class UserReport implements Serializable {
    private int userReportId;
    private int userId;
    private User user;
    private List<Report> reportList;
}
