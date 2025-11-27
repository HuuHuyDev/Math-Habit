package com.kidsapp.data.model;

/**
 * Model cho vật phẩm trong Shop
 */
public class ShopItem {
    private int id;
    private String name;
    private int price;
    private int imageRes;
    private boolean isPurchased;
    private boolean isSelected;

    public ShopItem(int id, String name, int price, int imageRes) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.imageRes = imageRes;
        this.isPurchased = false;
        this.isSelected = false;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getPrice() {
        return price;
    }

    public int getImageRes() {
        return imageRes;
    }

    public boolean isPurchased() {
        return isPurchased;
    }

    public void setPurchased(boolean purchased) {
        isPurchased = purchased;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
