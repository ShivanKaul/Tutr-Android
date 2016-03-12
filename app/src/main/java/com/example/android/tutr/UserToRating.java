package com.example.android.tutr;

import com.parse.ParseObject;

public class UserToRating {
    private ParseObject user;
    private ParseObject rating;

    UserToRating (ParseObject u, ParseObject r) {
        this.user = u;
        this.rating = r;
    }

    public ParseObject getRating() {
        return rating;
    }

    public ParseObject getUser() {
        return user;
    }
}