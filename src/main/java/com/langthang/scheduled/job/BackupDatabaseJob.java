package com.langthang.scheduled.job;

import com.langthang.services.IStorageServices;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.Date;

@RequiredArgsConstructor(onConstructor_ = {@Autowired})
@Component
@Slf4j
public class BackupDatabaseJob {

    private final IStorageServices storageServices;
    @Value("${spring.datasource.username}")
    private String username;
    @Value("${spring.datasource.password}")
    private String password;

    public void run() throws IOException {
        String outputFile = "backup_db.sql";
        String command = String.format("mysqldump -u%s -p%s --databases %s -r %s",
                username, password, "do-an-lang-thang", outputFile);

        Process process = Runtime.getRuntime().exec(command);
        try {
            if (process.waitFor() == 0) {
                File copiedFile = copyFile(outputFile);
                storageServices.uploadFile(copiedFile.getAbsolutePath());
            }
        } catch (InterruptedException e) {
            log.error("Backup database error", e);
        }
    }

    private File copyFile(String sourcePath) throws IOException {
        String currentDate = new SimpleDateFormat("MM_dd_yyyy").format(new Date());
        String fileName = currentDate + "_" + sourcePath;
        File newFile = new File(fileName);

        Files.copy(new File(sourcePath).toPath(), newFile.toPath());
        return newFile;
    }
}