package com.langthang.services;

public interface IBookmarkServices {

    int bookmarkPost(int postId, String accEmail);

    int deleteBookmark(int postId, String accEmail);
}
