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
    private int statusId;
    private String description;

    public Status(int statusId, String description) {
        this.statusId = statusId;
        this.description = description;
    }
}
