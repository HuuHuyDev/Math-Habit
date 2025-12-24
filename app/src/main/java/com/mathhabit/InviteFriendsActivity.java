package com.kidsapp.ui.challenge;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.kidsapp.adapter.FriendInviteAdapter;
import com.kidsapp.databinding.ActivityInviteFriendsBinding;
import com.kidsapp.databinding.BottomsheetShareInviteBinding;
import com.kidsapp.databinding.DialogChallengeModeBinding;
import com.kidsapp.model.Friend;

import java.util.ArrayList;
import java.util.List;

public class InviteFriendsActivity extends AppCompatActivity {
    
    private ActivityInviteFriendsBinding binding;
    private FriendInviteAdapter friendAdapter;
    private List<Friend> friendsList;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityInviteFriendsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        
        setupViews();
        setupRecyclerView();
        loadFriends();
    }
    
    private void setupViews() {
        binding.btnBack.setOnClickListener(v -> finish());
        
        binding.cardInvitePhone.setOnClickListener(v -> showPhoneInviteDialog());
        binding.cardInviteLink.setOnClickListener(v -> showShareInviteBottomSheet());
        
        // Search functionality
        binding.edtSearch.setOnEditorActionListener((v, actionId, event) -> {
            String query = binding.edtSearch.getText().toString().trim();
            filterFriends(query);
            return true;
        });
    }
    
    private void setupRecyclerView() {
        friendsList = new ArrayList<>();
        friendAdapter = new FriendInviteAdapter(friendsList, this::onFriendInviteClick);
        
        binding.recyclerFriends.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerFriends.setAdapter(friendAdapter);
    }
    
    private void loadFriends() {
        // Mock data - thay thế bằng API call thực tế
        friendsList.clear();
        friendsList.add(new Friend("1", "Nguyễn Văn A", "Level 5", "Đang online", true));
        friendsList.add(new Friend("2", "Trần Thị B", "Level 3", "2 phút trước", false));
        friendsList.add(new Friend("3", "Lê Văn C", "Level 7", "Đang online", true));
        friendsList.add(new Friend("4", "Phạm Thị D", "Level 4", "1 giờ trước", false));
        friendsList.add(new Friend("5", "Hoàng Văn E", "Level 6", "Đang online", true));
        
        friendAdapter.notifyDataSetChanged();
    }
    
    private void filterFriends(String query) {
        // Implement search logic
        List<Friend> filteredList = new ArrayList<>();
        for (Friend friend : friendsList) {
            if (friend.getName().toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(friend);
            }
        }
        friendAdapter.updateList(filteredList);
    }
    
    private void onFriendInviteClick(Friend friend) {
        showChallengeModeDialog(friend);
    }
    
    private void showChallengeModeDialog(Friend friend) {
        DialogChallengeModeBinding dialogBinding = DialogChallengeModeBinding.inflate(getLayoutInflater());
        
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);
        builder.setView(dialogBinding.getRoot());
        
        androidx.appcompat.app.AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        
        // Setup click listeners
        dialogBinding.btnClose.setOnClickListener(v -> dialog.dismiss());
        dialogBinding.btnCancel.setOnClickListener(v -> dialog.dismiss());
        
        dialogBinding.cardQuickChallenge.setOnClickListener(v -> {
            sendChallenge(friend, "quick");
            dialog.dismiss();
        });
        
        dialogBinding.cardStandardChallenge.setOnClickListener(v -> {
            sendChallenge(friend, "standard");
            dialog.dismiss();
        });
        
        dialogBinding.cardMarathonChallenge.setOnClickListener(v -> {
            sendChallenge(friend, "marathon");
            dialog.dismiss();
        });
        
        dialogBinding.btnSendChallenge.setOnClickListener(v -> {
            sendChallenge(friend, "standard"); // Default mode
            dialog.dismiss();
        });
        
        dialog.show();
    }
    
    private void sendChallenge(Friend friend, String mode) {
        // Implement challenge sending logic
        String modeText = "";
        switch (mode) {
            case "quick":
                modeText = "Thách đấu nhanh";
                break;
            case "standard":
                modeText = "Thách đấu tiêu chuẩn";
                break;
            case "marathon":
                modeText = "Thách đấu marathon";
                break;
        }
        
        Toast.makeText(this, "Đã gửi " + modeText + " đến " + friend.getName(), Toast.LENGTH_SHORT).show();
        
        // TODO: Implement actual API call to send challenge
    }
    
    private void showPhoneInviteDialog() {
        // Implement phone number invite dialog
        Toast.makeText(this, "Tính năng mời qua số điện thoại đang phát triển", Toast.LENGTH_SHORT).show();
    }
    
    private void showShareInviteBottomSheet() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        BottomsheetShareInviteBinding sheetBinding = BottomsheetShareInviteBinding.inflate(getLayoutInflater());
        bottomSheetDialog.setContentView(sheetBinding.getRoot());
        
        // Generate invite link
        String inviteLink = "https://mathhabit.app/invite/" + generateInviteCode();
        sheetBinding.txtInviteLink.setText(inviteLink);
        
        // Setup click listeners
        sheetBinding.btnCopyLink.setOnClickListener(v -> {
            copyToClipboard(inviteLink);
            Toast.makeText(this, "Đã sao chép link!", Toast.LENGTH_SHORT).show();
        });
        
        sheetBinding.layoutShareWhatsApp.setOnClickListener(v -> shareToApp("com.whatsapp", inviteLink));
        sheetBinding.layoutShareMessenger.setOnClickListener(v -> shareToApp("com.facebook.orca", inviteLink));
        sheetBinding.layoutShareZalo.setOnClickListener(v -> shareToApp("com.zing.zalo", inviteLink));
        sheetBinding.layoutShareMore.setOnClickListener(v -> shareToOtherApps(inviteLink));
        
        sheetBinding.btnCancel.setOnClickListener(v -> bottomSheetDialog.dismiss());
        sheetBinding.btnGenerateNewLink.setOnClickListener(v -> {
            String newLink = "https://mathhabit.app/invite/" + generateInviteCode();
            sheetBinding.txtInviteLink.setText(newLink);
            Toast.makeText(this, "Đã tạo link mới!", Toast.LENGTH_SHORT).show();
        });
        
        bottomSheetDialog.show();
    }
    
    private String generateInviteCode() {
        // Generate random invite code
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < 8; i++) {
            code.append(chars.charAt((int) (Math.random() * chars.length())));
        }
        return code.toString();
    }
    
    private void copyToClipboard(String text) {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("Invite Link", text);
        clipboard.setPrimaryClip(clip);
    }
    
    private void shareToApp(String packageName, String link) {
        try {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.setPackage(packageName);
            intent.putExtra(Intent.EXTRA_TEXT, "Hãy tham gia thách đấu toán học cùng mình! " + link);
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(this, "Ứng dụng chưa được cài đặt", Toast.LENGTH_SHORT).show();
        }
    }
    
    private void shareToOtherApps(String link) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, "Hãy tham gia thách đấu toán học cùng mình! " + link);
        startActivity(Intent.createChooser(intent, "Chia sẻ qua"));
    }
}