package com.langthang.services.v2;

import com.langthang.model.dto.v2.response.UserStatsDto;
import com.langthang.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class UserServiceV2 {

    private final AccountRepository userRepo;

    public UserStatsDto getUserStats(Integer userId) {
        var followCount = userRepo.countFollowing(userId);
        var postCount = userRepo.countPublishedPost(userId);
        var receivedBookmarkCount = userRepo.countBookmarkOnMyPost(userId);
        var receivedCommentCount = userRepo.countCommentOnMyPost(userId);

        return new UserStatsDto(postCount, followCount, receivedBookmarkCount, receivedCommentCount);
    }
}
