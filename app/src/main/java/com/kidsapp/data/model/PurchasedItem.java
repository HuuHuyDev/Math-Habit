package com.kidsapp.data.model;

import com.google.gson.annotations.SerializedName;

/**
 * Model cho vật phẩm đã mua
 * Match với PurchasedItemResponse từ API
 */
public class PurchasedItem {
    @SerializedName("id")
    private String id;
    
    @SerializedName("itemId")
    private String itemId;
    
    @SerializedName("itemName")
    private String itemName;
    
    @SerializedName("itemType")
    private String itemType; // AVATAR, BOOSTER
    
    @SerializedName("imageUrl")
    private String imageUrl;
    
    @SerializedName("purchasePrice")
    private int purchasePrice;
    
    @SerializedName("purchasedAt")
    private String purchasedAt;
    
    @SerializedName("isEquipped")
    private boolean isEquipped;
    
    // Booster specific fields
    @SerializedName("boosterType")
    private String boosterType;
    
    @SerializedName("boosterValue")
    private Double boosterValue;
    
    @SerializedName("durationMinutes")
    private Integer durationMinutes;
    
    @SerializedName("isActive")
    private boolean isActive;
    
    @SerializedName("activatedAt")
    private String activatedAt;
    
    @SerializedName("expiresAt")
    private String expiresAt;
    
    @SerializedName("remainingMinutes")
    private Long remainingMinutes;

    // Getters
    public String getId() { return id; }
    public String getItemId() { return itemId; }
    public String getItemName() { return itemName; }
    public String getItemType() { return itemType; }
    public String getImageUrl() { return imageUrl; }
    public int getPurchasePrice() { return purchasePrice; }
    public String getPurchasedAt() { return purchasedAt; }
    public boolean isEquipped() { return isEquipped; }
    public String getBoosterType() { return boosterType; }
    public Double getBoosterValue() { return boosterValue; }
    public Integer getDurationMinutes() { return durationMinutes; }
    public boolean isActive() { return isActive; }
    public String getActivatedAt() { return activatedAt; }
    public String getExpiresAt() { return expiresAt; }
    public Long getRemainingMinutes() { return remainingMinutes; }

    // Setters
    public void setId(String id) { this.id = id; }
    public void setItemId(String itemId) { this.itemId = itemId; }
    public void setItemName(String itemName) { this.itemName = itemName; }
    public void setItemType(String itemType) { this.itemType = itemType; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public void setPurchasePrice(int purchasePrice) { this.purchasePrice = purchasePrice; }
    public void setPurchasedAt(String purchasedAt) { this.purchasedAt = purchasedAt; }
    public void setEquipped(boolean equipped) { isEquipped = equipped; }
    public void setBoosterType(String boosterType) { this.boosterType = boosterType; }
    public void setBoosterValue(Double boosterValue) { this.boosterValue = boosterValue; }
    public void setDurationMinutes(Integer durationMinutes) { this.durationMinutes = durationMinutes; }
    public void setActive(boolean active) { isActive = active; }
    public void setActivatedAt(String activatedAt) { this.activatedAt = activatedAt; }
    public void setExpiresAt(String expiresAt) { this.expiresAt = expiresAt; }
    public void setRemainingMinutes(Long remainingMinutes) { this.remainingMinutes = remainingMinutes; }

    // Helper methods
    public boolean isAvatar() {
        return "AVATAR".equals(itemType);
    }

    public boolean isBooster() {
        return "BOOSTER".equals(itemType);
    }

    public String getStatusText() {
        if (isAvatar()) {
            return isEquipped ? "Đang sử dụng" : "Đã mua";
        } else if (isBooster()) {
            if (isActive && remainingMinutes != null && remainingMinutes > 0) {
                return "Còn " + remainingMinutes + " phút";
            }
            return isActive ? "Đang hoạt động" : "Chưa kích hoạt";
        }
        return "";
    }
}
