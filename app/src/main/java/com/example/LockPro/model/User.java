package com.example.LockPro.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class User implements Serializable {
    private String uid;
    private int coin;

    public String getUid() {
        return uid;
    }

    public int getCoin() {
        return coin;
    }

    public User(){}

    public User(String uid, int coin) {
        this.uid = uid;
        this.coin = coin;
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("uid", uid);
        result.put("coin", coin);
        return result;
    }
}
