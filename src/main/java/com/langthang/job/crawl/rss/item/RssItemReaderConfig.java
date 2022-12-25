package com.langthang.job.crawl.rss.item;

import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.file.MultiResourceItemReader;
import org.springframework.batch.item.support.SynchronizedItemStreamReader;
import org.springframework.batch.item.xml.StaxEventItemReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;

import java.io.IOException;

@Configuration
public class RssItemReaderConfig {

    @Bean
    @StepScope
    public ItemReader<RssItemDto> rssItemStaxEventItemReader(@Value("#{jobParameters['resource-folder']}") String folder) throws IOException {
        var xmlFileReader = new StaxEventItemReader<RssItemDto>();
        Jaxb2Marshaller rssItemMarshaller = new Jaxb2Marshaller();
        rssItemMarshaller.setClassesToBeBound(RssItemDto.class);
        xmlFileReader.setFragmentRootElementName("item");
        xmlFileReader.setUnmarshaller(rssItemMarshaller);

        MultiResourceItemReader<RssItemDto> multiResourceItemReader = new MultiResourceItemReader<>();

        ResourcePatternResolver patternResolver = new PathMatchingResourcePatternResolver();
        var pattern = String.format("file:%s/*.xml", folder);
        Resource[] resources = patternResolver.getResources(pattern);

        multiResourceItemReader.setDelegate(xmlFileReader);
        multiResourceItemReader.setResources(resources);

        SynchronizedItemStreamReader<RssItemDto> synchronizedItemStreamReader = new SynchronizedItemStreamReader<>();
        synchronizedItemStreamReader.setDelegate(multiResourceItemReader);

        return synchronizedItemStreamReader;
    }
}
