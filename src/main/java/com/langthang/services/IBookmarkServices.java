package com.langthang.services;

import com.langthang.model.dto.v2.response.PostStatsDto;
import com.langthang.model.entity.Account;

public interface IBookmarkServices {

    PostStatsDto bookmarkPost(Integer postId, Account user);

    PostStatsDto deleteBookmark(Integer postId, Account user);
}
