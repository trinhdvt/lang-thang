package com.langthang.job;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ChunkListener;
import org.springframework.batch.core.scope.context.ChunkContext;

@Slf4j
public class ChunkProcessTimeLogger implements ChunkListener {

    private long startTime;

    @Override
    public void beforeChunk(@NonNull ChunkContext context) {
        startTime = System.currentTimeMillis();
        log.debug("Start processing chunk");
        ChunkListener.super.beforeChunk(context);
    }

    @Override
    public void afterChunk(@NonNull ChunkContext context) {
        log.debug("Finish chunk in [{}] ms",
                System.currentTimeMillis() - startTime);
        ChunkListener.super.afterChunk(context);
    }
}
