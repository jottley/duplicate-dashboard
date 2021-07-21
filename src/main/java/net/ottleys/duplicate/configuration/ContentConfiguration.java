package net.ottleys.duplicate.configuration;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

@Configuration
public class ContentConfiguration {

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
    public MessageDigest methodDigest() throws NoSuchAlgorithmException {
        //return MessageDigest.getInstance("SHA3-256");
        return MessageDigest.getInstance("MD5");
    }
    
}
