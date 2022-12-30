package com.langthang.controller.v2;

import com.langthang.model.dto.request.AccountRegisterDTO;
import com.langthang.model.dto.v2.request.GoogleLoginCredential;
import com.langthang.model.dto.v2.request.LoginCredential;
import com.langthang.model.dto.v2.response.LoginResponse;
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
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@RestController
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
@Validated
@Slf4j
public class AuthControllerV2 {

    private final AuthServiceV2 authServices;

    private final IAuthServices authServicesV1;

    @PostMapping("/auth/login")
    public LoginResponse login(
            @Valid @RequestBody LoginCredential loginCredential,
            HttpServletResponse resp
    ) {
        String token = authServices.login(loginCredential, resp);
        return new LoginResponse(token);
    }

    @PostMapping("/auth/register")
    public ResponseEntity<Object> register(@Valid @RequestBody AccountRegisterDTO accountRegisterDTO) {
        authServicesV1.registerAccount(accountRegisterDTO);
        return ResponseEntity.accepted().build();
    }

    @PostMapping("/auth/google")
    public LoginResponse loginWithGoogle(
            @Valid @RequestBody GoogleLoginCredential credential,
            HttpServletResponse response
    ) {
        String token = authServices.loginWithGoogle(credential, response);
        return new LoginResponse(token);
    }


    private final JobLauncher jobLauncher;
    private final JobRegistry jobRegistry;

    @GetMapping(value = "/job", params = {"jobName"})
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<?> triggerJob(@RequestParam String jobName) throws Exception {
        var jobParams = new JobParametersBuilder()
                .addDate("run-time", new Date())
                .addString("resource-folder", String.format("rss/%s", MyStringUtils.getTodayString()))
                .toJobParameters();

        var job = jobRegistry.getJob(jobName);
        jobLauncher.run(job, jobParams);

        return ResponseEntity.ok("Job is running");
    }
}
