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
    private int id;
    private int userId;
    private String potholeUrl;
    private Coordinates coordinates;
}
