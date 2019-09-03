package com.dso30bt.project2019.potapp.models.enums;

/**
 * Created by Joesta on 2019/07/16.
 */
public enum ConstructorStatusEnum {
    AVAILABLE("Available"),
    BUSY("Busy"),
    ON_LEAVE("On-Leave");

    public final String value;

    ConstructorStatusEnum(String value) {
        this.value = value;
    }
}
