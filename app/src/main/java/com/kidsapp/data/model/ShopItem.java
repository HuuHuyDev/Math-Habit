package com.kidsapp.data.model;

import com.google.gson.annotations.SerializedName;

/**
 * Model cho vật phẩm trong Shop
 * Match với ShopItemResponse từ API
 */
public class ShopItem {
    @SerializedName("id")
    private String id;
    
    @SerializedName("name")
    private String name;
    
    @SerializedName("description")
    private String description;
    
    @SerializedName("itemType")
    private String itemType; // AVATAR, BOOSTER
    
    @SerializedName("imageUrl")
    private String imageUrl;
    
    @SerializedName("price")
    private int price;
    
    @SerializedName("requiredLevel")
    private Integer requiredLevel;
    
    @SerializedName("isPurchased")
    private boolean isPurchased;
    
    @SerializedName("isEquipped")
    private boolean isEquipped;
    
    @SerializedName("isLimited")
    private Boolean isLimited;
    
    @SerializedName("stockQuantity")
    private Integer stockQuantity;
    
    // Booster specific fields
    @SerializedName("boosterType")
    private String boosterType; // XP_MULTIPLIER, COIN_MULTIPLIER, TIME_REDUCER
    
    @SerializedName("boosterValue")
    private Double boosterValue;
    
    @SerializedName("durationMinutes")
    private Integer durationMinutes;
    
    // Local state (not from API)
    private transient boolean isSelected;
    private transient int imageRes; // For local drawable

    // Default constructor for Gson
    public ShopItem() {}

    // Constructor for local items (backward compatibility)
    public ShopItem(int id, String name, int price, int imageRes) {
        this.id = String.valueOf(id);
        this.name = name;
        this.price = price;
        this.imageRes = imageRes;
        this.isPurchased = false;
        this.isSelected = false;
    }

    // Getters
    public String getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getItemType() { return itemType; }
    public String getImageUrl() { return imageUrl; }
    public int getPrice() { return price; }
    public Integer getRequiredLevel() { return requiredLevel; }
    public boolean isPurchased() { return isPurchased; }
    public boolean isEquipped() { return isEquipped; }
    public Boolean getIsLimited() { return isLimited; }
    public Integer getStockQuantity() { return stockQuantity; }
    public String getBoosterType() { return boosterType; }
    public Double getBoosterValue() { return boosterValue; }
    public Integer getDurationMinutes() { return durationMinutes; }
    public boolean isSelected() { return isSelected; }
    public int getImageRes() { return imageRes; }

    // Setters
    public void setId(String id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setDescription(String description) { this.description = description; }
    public void setItemType(String itemType) { this.itemType = itemType; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public void setPrice(int price) { this.price = price; }
    public void setRequiredLevel(Integer requiredLevel) { this.requiredLevel = requiredLevel; }
    public void setPurchased(boolean purchased) { isPurchased = purchased; }
    public void setEquipped(boolean equipped) { isEquipped = equipped; }
    public void setIsLimited(Boolean isLimited) { this.isLimited = isLimited; }
    public void setStockQuantity(Integer stockQuantity) { this.stockQuantity = stockQuantity; }
    public void setBoosterType(String boosterType) { this.boosterType = boosterType; }
    public void setBoosterValue(Double boosterValue) { this.boosterValue = boosterValue; }
    public void setDurationMinutes(Integer durationMinutes) { this.durationMinutes = durationMinutes; }
    public void setSelected(boolean selected) { isSelected = selected; }
    public void setImageRes(int imageRes) { this.imageRes = imageRes; }

    // Helper methods
    public boolean isAvatar() {
        return "AVATAR".equals(itemType);
    }

    public boolean isBooster() {
        return "BOOSTER".equals(itemType);
    }

    public String getBoosterDescription() {
        if (boosterType == null) return "";
        switch (boosterType) {
            case "XP_MULTIPLIER":
                return "x" + boosterValue + " XP trong " + durationMinutes + " phút";
            case "COIN_MULTIPLIER":
                return "x" + boosterValue + " Coins trong " + durationMinutes + " phút";
            case "TIME_REDUCER":
                return "Giảm " + (int)((1 - boosterValue) * 100) + "% thời gian trong " + durationMinutes + " phút";
            default:
                return "";
        }
    }
}
