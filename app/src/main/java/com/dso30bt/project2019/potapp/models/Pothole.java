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
    private User user;
   // private String date;
   // private double radius;
   // private String imgUrl;
    private String description;
   // private boolean status;
    private Coordinates coordinates;
}
