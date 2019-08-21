package com.dso30bt.project2019.potapp.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * Created by Joesta on 2019/07/16.
 */

@Data
@ToString
@NoArgsConstructor
public class Status {
    private int id;
    private String description;

    public Status(String description) {
        this.description = description;
    }
}
