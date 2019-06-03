package com.dso30bt.project2019.potapp.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by Joesta on 2019/05/29.*
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
class Constructor {
    private int userId;
    private String name;
    private String surname;
    private Pothole pothole;
    private boolean workStatus;
}
