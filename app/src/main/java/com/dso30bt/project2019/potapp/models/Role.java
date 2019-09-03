package com.dso30bt.project2019.potapp.models;

import java.io.Serializable;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * Created by Joesta on 2019/07/12.
 */

@Data
@ToString
@NoArgsConstructor
public class Role implements Serializable {
    private int roleId;
    private String roleDescription;

    public Role(int roleId, String roleDescription) {
        this.roleId = roleId;
        this.roleDescription = roleDescription;
    }
}
