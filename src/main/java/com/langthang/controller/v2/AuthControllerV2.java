package com.langthang.controller.v2;

import com.langthang.job.crawl.parser.DuLichVietNameParser;
import com.langthang.job.crawl.parser.IArticleParser;
import com.langthang.job.crawl.rss.item.RssItemModel;
import com.langthang.mapper.PostMapper;
import com.langthang.model.dto.request.AccountRegisterDTO;
import com.langthang.model.dto.v2.request.LoginCredential;
import com.langthang.services.IAuthServices;
import com.langthang.services.v2.AuthServiceV2;
import com.langthang.utils.MyStringUtils;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Date;
import java.util.Map;

@RestController
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
@Validated
@Slf4j
public class AuthControllerV2 {

    private final AuthServiceV2 authServices;

    private final IAuthServices authServicesV1;
    private final PostMapper mapper;

    @PostMapping("/auth/login")
    public ResponseEntity<Object> login(@Valid @RequestBody LoginCredential loginCredential,
                                        HttpServletResponse resp) {
        String token = authServices.login(loginCredential, resp);
        return ResponseEntity.ok(Map.of("token", token));
    }

    @PostMapping("/auth/register")
    public ResponseEntity<Object> register(@Valid @RequestBody AccountRegisterDTO accountRegisterDTO) {
        authServicesV1.registerAccount(accountRegisterDTO);
        return ResponseEntity.accepted().build();
    }


    @GetMapping("/test")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<?> test() throws IOException {

        var rssUrl = "https://dulichvietnam.com.vn/news/rss.feed";
        Resource source = new UrlResource(rssUrl);
        File outFile;
        try (var in = source.getInputStream()) {

            outFile = new File("src/main/resources/rss.xml");
            var out = outFile.toPath();
            Files.copy(in, out, StandardCopyOption.REPLACE_EXISTING);
        }

        return ResponseEntity.ok(outFile.getAbsolutePath());
    }

    private final JobLauncher jobLauncher;
    private final JobRegistry jobRegistry;

    @GetMapping(value = "/job", params = {"jobName"})
//    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<?> triggerJob(@RequestParam String jobName) throws Exception {
        var jobParams = new JobParametersBuilder()
                .addDate("run-time", new Date())
                .addString("resource-folder", String.format("rss/%s", MyStringUtils.getTodayString()))
                .toJobParameters();

        var job = jobRegistry.getJob(jobName);
        jobLauncher.run(job, jobParams);

        return ResponseEntity.ok("Job is running");
    }

    @GetMapping("/crawl")
    public ResponseEntity<?> parseContent() throws IOException {
        var targetUrl = "https://dulichvietnam.com.vn/nhung-trai-nghiem-mua-xuan-nhat-ban-thu-vi.html";
        var item = new RssItemModel();
        item.setLink(targetUrl);
        IArticleParser parser = new DuLichVietNameParser(item);
        var result = parser.parse();
        return ResponseEntity.ok(mapper.toReadOnlyDto(result));
    }

}
