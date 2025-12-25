package com.kidsapp.ui.child.chat;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.kidsapp.R;
import com.kidsapp.databinding.FragmentChatListBinding;

import java.util.ArrayList;
import java.util.List;

/**
 * Fragment hiển thị danh sách chat (dùng cho cả tab Phụ huynh và Bạn bè)
 */
public class ChatListFragment extends Fragment implements ConversationAdapter.OnConversationClickListener {

    private static final String ARG_TYPE = "type";
    public static final int TYPE_PARENT = 0;
    public static final int TYPE_FRIENDS = 1;

    private FragmentChatListBinding binding;
    private ConversationAdapter adapter;
    private int type;

    public static ChatListFragment newInstance(int type) {
        ChatListFragment fragment = new ChatListFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_TYPE, type);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentChatListBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        type = getArguments() != null ? getArguments().getInt(ARG_TYPE, TYPE_PARENT) : TYPE_PARENT;
        
        setupRecyclerView();
        loadConversations();
    }

    private void setupRecyclerView() {
        adapter = new ConversationAdapter(this);
        binding.rvChats.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvChats.setAdapter(adapter);
    }

    private void loadConversations() {
        // Mock data
        List<Conversation> conversations = new ArrayList<>();

        if (type == TYPE_PARENT) {
            conversations.add(new Conversation(
                    "parent_1", "Bố", null,
                    "Con học giỏi lắm! 💪", "10:30",
                    2, true, Conversation.TYPE_PARENT
            ));
            conversations.add(new Conversation(
                    "parent_2", "Mẹ", null,
                    "Nhớ ăn cơm đúng giờ nhé con", "Hôm qua",
                    0, false, Conversation.TYPE_PARENT
            ));
        } else {
            conversations.add(new Conversation(
                    "friend_1", "Minh Anh", null,
                    "Đấu một trận không? 🎮", "09:15",
                    3, true, Conversation.TYPE_FRIEND
            ));
            conversations.add(new Conversation(
                    "friend_2", "Bảo Ngọc", null,
                    "Bài toán này khó quá!", "Hôm qua",
                    0, true, Conversation.TYPE_FRIEND
            ));
            conversations.add(new Conversation(
                    "friend_3", "Đức Huy", null,
                    "Cảm ơn bạn nhé!", "2 ngày trước",
                    0, false, Conversation.TYPE_FRIEND
            ));
        }

        adapter.setConversations(conversations);
        updateEmptyState();
    }

    private void updateEmptyState() {
        boolean isEmpty = adapter.isEmpty();
        binding.layoutEmpty.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
        binding.rvChats.setVisibility(isEmpty ? View.GONE : View.VISIBLE);

        if (isEmpty) {
            if (type == TYPE_PARENT) {
                binding.txtEmptyTitle.setText("Chưa có tin nhắn từ phụ huynh");
                binding.txtEmptyMessage.setText("Tin nhắn từ bố mẹ sẽ hiển thị ở đây");
            } else {
                binding.txtEmptyTitle.setText("Chưa có bạn bè nào");
                binding.txtEmptyMessage.setText("Tìm bạn mới để bắt đầu trò chuyện!");
            }
        }
    }

    @Override
    public void onConversationClick(Conversation conversation) {
        // Mở màn hình chat
        Bundle args = new Bundle();
        args.putString("chat_id", conversation.getId());
        args.putString("chat_name", conversation.getName());
        args.putInt("chat_type", conversation.getType());

        ChatRoomFragment fragment = new ChatRoomFragment();
        fragment.setArguments(args);

        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.childHomeHost, fragment)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
