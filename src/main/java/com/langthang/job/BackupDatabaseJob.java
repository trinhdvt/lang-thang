package com.langthang.job;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class BackupDatabaseJob {

    @Value("${spring.datasource.username}")
    private String username;

    @Value("${spring.datasource.password}")
    private String password;

    public void run() throws IOException {
        String outputFile = "backup_db.sql";
        String command = String.format("mysqldump -u%s -p%s --databases %s -r %s",
                username, password, "do-an-lang-thang", outputFile);

        Runtime.getRuntime().exec(command);
    }
}
