package com.langthang.controller.v2;

import com.langthang.model.dto.v2.request.PostStatsRequest;
import com.langthang.model.dto.v2.response.PostStatsDto;
import com.langthang.services.v2.PostServiceV2;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
@Validated
public class PostStatController {

    private final PostServiceV2 postServiceV2;

    @PostMapping("/postStats")
    public List<PostStatsDto> getPostStats(@RequestBody PostStatsRequest request) {
        return postServiceV2.getPostStats(request);
    }
}
