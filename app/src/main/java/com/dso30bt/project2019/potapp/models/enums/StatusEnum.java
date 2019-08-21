package com.dso30bt.project2019.potapp.models.enums;

/**
 * Created by Joesta on 2019/07/16.
 */
public enum StatusEnum {
    AVAILABLE("Available"),
    BUSY("Busy"),
    ON_LEAVE("On-Leave");

    public final String value;

    StatusEnum(String value) {
        this.value = value;
    }
}
