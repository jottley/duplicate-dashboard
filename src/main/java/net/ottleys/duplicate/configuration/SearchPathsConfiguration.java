package net.ottleys.duplicate.configuration;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

@Configuration
public class SearchPathsConfiguration {

    @Bean(name="indexedPaths")
    @Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
    public List<Path> indexedPaths() {
        return Collections.synchronizedList(new LinkedList<>());
    }
    
    @Bean(name="searchPaths")
    @Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
    public List<Path> searchPaths() {
        return Collections.synchronizedList(new ArrayList<>());
    }

    @Bean(name="excludedPaths")
    @Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
    public List<Path> excludedPaths() {
        return Collections.synchronizedList(new ArrayList<>());
    }
}