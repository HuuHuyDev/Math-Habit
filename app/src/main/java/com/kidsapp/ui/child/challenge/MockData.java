package com.kidsapp.ui.child.challenge;

import com.kidsapp.data.model.Child;

import java.util.ArrayList;
import java.util.List;

/**
 * Mock data cho testing khi chưa có API
 */
public class MockData {

    public static List<Child> getFriends() {
        List<Child> friends = new ArrayList<>();

        friends.add(createChild("1", "Minh Anh", 5, 1250));
        friends.add(createChild("2", "Bảo Ngọc", 7, 2100));
        friends.add(createChild("3", "Đức Huy", 4, 980));
        friends.add(createChild("4", "Thu Hà", 6, 1680));
        friends.add(createChild("5", "Quang Minh", 8, 2450));

        return friends;
    }

    private static Child createChild(String id, String name, int level, int xp) {
        Child child = new Child();
        child.setId(id);
        child.setName(name);
        child.setLevel(level);
        child.setTotalPoints(xp);
        return child;
    }
}
