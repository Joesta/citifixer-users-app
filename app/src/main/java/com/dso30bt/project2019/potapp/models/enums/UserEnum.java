package com.dso30bt.project2019.potapp.models;

/**
 * Created by Joesta on 2019/07/15.
 */
public enum UserEnum {
    USER(1),
    CONSTRUCTOR(2),
    ENGINEER(3);

    public final int value;

    UserEnum(int value) {
        this.value = value;
    }
}
