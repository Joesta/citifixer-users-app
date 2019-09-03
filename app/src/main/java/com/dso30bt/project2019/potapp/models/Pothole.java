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
public class Pothole {
    private int potholeId;
    private int userId;
    private User user;
    private String potholeUrl;
    private String description;
    private Coordinates coordinates;
}
