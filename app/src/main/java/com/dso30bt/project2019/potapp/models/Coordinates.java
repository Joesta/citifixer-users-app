package com.dso30bt.project2019.potapp.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by Joesta on 2019/06/01.
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Coordinates {
    private int id;
    private double latitutde;
    private double longitute;
}
