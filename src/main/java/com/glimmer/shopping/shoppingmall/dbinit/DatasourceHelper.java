package com.glimmer.shopping.shoppingmall.dbinit;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class DatasourceHelper {

    @Value("${spring.datasource.dynamic.datasource.mysql.url}")
    private String mysqlUrl;

    @Value("${spring.datasource.dynamic.datasource.mysql.username}")
    private String mysqlUsername;

    @Value("${spring.datasource.dynamic.datasource.oracle.url}")
    private String oracleUrl;

    @Value("${spring.datasource.dynamic.datasource.oracle.username}")
    private String oracleUsername;

    @Value("${spring.datasource.dynamic.datasource.clickhouse.url}")
    private String clickhouseUrl;

    @Value("${spring.datasource.dynamic.datasource.clickhouse.username}")
    private String clickhouseUsername;

    @Value("${spring.data.mongodb.uri}")
    private String mongodbUri;

    @Value("${spring.redis.host}")
    private String redisHost;

    @Value("${spring.redis.port}")
    private int redisPort;

    private final DataSource dataSource;

    public List<String> getOracleTables() {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        return jdbcTemplate.queryForList("SELECT table_name FROM user_tables", String.class);
    }

    public List<String> getClickHouseTables() {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        return jdbcTemplate.queryForList("SHOW TABLES", String.class);
    }

    public String getMysqlUrl() { return mysqlUrl; }
    public String getMysqlUsername() { return mysqlUsername; }
    public String getOracleUrl() { return oracleUrl; }
    public String getOracleUsername() { return oracleUsername; }
    public String getClickhouseUrl() { return clickhouseUrl; }
    public String getClickhouseUsername() { return clickhouseUsername; }
    public String getMongodbUri() { return mongodbUri; }
    public String getRedisHost() { return redisHost; }
    public int getRedisPort() { return redisPort; }

    public String getDbNameFromUrl(String url) {
        if (url == null) return "unknown";
        int idx = url.lastIndexOf("/");
        if (idx > 0) {
            int qIdx = url.indexOf("?", idx);
            return qIdx > 0 ? url.substring(idx + 1, qIdx) : url.substring(idx + 1);
        }
        return "unknown";
    }

    public String getMongoDbName() {
        if (mongodbUri == null) return "unknown";
        int idx = mongodbUri.lastIndexOf("/");
        if (idx > 0) {
            int qIdx = mongodbUri.indexOf("?", idx);
            return qIdx > 0 ? mongodbUri.substring(idx + 1, qIdx) : mongodbUri.substring(idx + 1);
        }
        return "unknown";
    }
}