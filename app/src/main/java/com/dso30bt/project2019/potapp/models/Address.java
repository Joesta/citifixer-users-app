package com.dso30bt.project2019.potapp.models;

import java.io.Serializable;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by Joesta on 2019/07/17.
 */

@Data
@NoArgsConstructor
public class Address implements Serializable {
    private int id;
    private String city;
    private String state;
    private String zip;
    private String country;
}
