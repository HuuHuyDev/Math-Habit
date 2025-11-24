package com.kidsapp.data.model;

public class Skin {
    private int iconRes;
    private String name;

    public Skin(int iconRes, String name) {
        this.iconRes = iconRes;
        this.name = name;
    }

    public int getIconRes() { return iconRes; }
    public String getName() { return name; }
}
