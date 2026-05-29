package org.example.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class DataSourceConfig {

    @Value("${spring.datasource.url}")
    private String url;

    @Value("${spring.datasource.username}")
    private String username;

    @Value("${spring.datasource.password}")
    private String password;

    @Bean
    public DataSource dataSource() {
        TenantRoutingDataSource routingDataSource = new TenantRoutingDataSource();
        Map<Object, Object> targetDataSources = new HashMap<>();
        
        // TODO: This is a static configuration and not suitable for a dynamic multi-tenant environment.
        // A more robust solution would involve dynamically creating and configuring data sources
        // as new tenants are onboarded. This could be achieved by using a tenant management service
        // and a custom implementation of `AbstractRoutingDataSource` that can add new data sources at runtime.
        targetDataSources.put("tenant1", tenant1DataSource());
        targetDataSources.put("tenant2", tenant2DataSource());

        routingDataSource.setTargetDataSources(targetDataSources);
        routingDataSource.setDefaultTargetDataSource(defaultDataSource());
        return routingDataSource;
    }

    private DataSource defaultDataSource() {
        return DataSourceBuilder.create()
                .url(url)
                .username(username)
                .password(password)
                .build();
    }

    private DataSource tenant1DataSource() {
        return DataSourceBuilder.create()
                .url(url.replace("postgres", "tenant1"))
                .username(username)
                .password(password)
                .build();
    }

    private DataSource tenant2DataSource() {
        return DataSourceBuilder.create()
                .url(url.replace("postgres", "tenant2"))
                .username(username)
                .password(password)
                .build();
    }
}