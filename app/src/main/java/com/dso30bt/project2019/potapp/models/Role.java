package com.dso30bt.project2019.potapp.models;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * Created by Joesta on 2019/07/12.
 */

@Data
@ToString
@NoArgsConstructor
public class Role {
    private String id;
    private String roleDescription;

    public Role(String roleDescription) {
        this.roleDescription = roleDescription;
    }
}
