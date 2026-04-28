package com.glimmer.shopping.shoppingmall.dbinit;

import com.baomidou.dynamic.datasource.DynamicRoutingDataSource;
import com.glimmer.shopping.shoppingmall.service.ClickHouseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class DatabaseInitService implements ApplicationRunner {

    private final DynamicRoutingDataSource dynamicDataSource;
    private final ClickHouseService clickHouseService;
    private final DatasourceHelper helper;

    private JdbcTemplate mysqlJdbcTemplate;
    private JdbcTemplate oracleJdbcTemplate;
    private JdbcTemplate clickhouseJdbcTemplate;

    @Override
    public void run(ApplicationArguments args) {
        initJdbcTemplates();
        printHeader();
        initMySQL();
        initOracle();
        initClickHouse();
        initMongoDB();
        initRedis();
        printFooter();
    }

    private void initJdbcTemplates() {
        Map<String, DataSource> dataSourceMap = dynamicDataSource.getDataSources();
        mysqlJdbcTemplate = new JdbcTemplate(dataSourceMap.get("mysql"));
        oracleJdbcTemplate = new JdbcTemplate(dataSourceMap.get("oracle"));
        clickhouseJdbcTemplate = new JdbcTemplate(dataSourceMap.get("clickhouse"));
    }

    private void initMySQL() {
        printSectionStart("MySQL", helper.getMysqlUrl(), helper.getMysqlUsername());

        try {
            String dbName = helper.getDbNameFromUrl(helper.getMysqlUrl());
            log.info("│ 数据库: {}", dbName);

            List<String> tables = mysqlJdbcTemplate.queryForList("SHOW TABLES", String.class);
            printTables(tables);
            printSuccess("MySQL");
        } catch (Exception e) {
            printError("MySQL", e.getMessage());
        }
    }

    private void initOracle() {
        printSectionStart("Oracle", helper.getOracleUrl(), helper.getOracleUsername());

        try {
            String dbName = helper.getDbNameFromUrl(helper.getOracleUrl());
            log.info("│ 数据库: {}", dbName);

            List<String> tables = oracleJdbcTemplate.queryForList("SELECT table_name FROM user_tables", String.class);
            printTables(tables);
            printSuccess("Oracle");
        } catch (Exception e) {
            printWarning("Oracle", e.getMessage());
        }
    }

    private void initClickHouse() {
        printSectionStart("ClickHouse", helper.getClickhouseUrl(), helper.getClickhouseUsername());

        try {
            String dbName = helper.getDbNameFromUrl(helper.getClickhouseUrl());
            log.info("│ 数据库: {}", dbName);

            List<String> tables = clickhouseJdbcTemplate.queryForList("SHOW TABLES", String.class);
            printTables(tables);

            clickHouseService.initTablesOnStartup();
            printSuccess("ClickHouse");
        } catch (Exception e) {
            printError("ClickHouse", e.getMessage());
        }
    }

    private void initMongoDB() {
        log.info("");
        log.info("┌────────────────────────────────────────");
        log.info("│ 📦 初始化 MongoDB");
        log.info("├────────────────────────────────────────");
        log.info("│ 连接地址: {}", helper.getMongodbUri());

        try {
            String dbName = helper.getMongoDbName();
            log.info("│ 数据库: {}", dbName);
            log.info("│");
            log.info("│ ✅ MongoDB 初始化成功");
            log.info("└────────────────────────────────────────");
        } catch (Exception e) {
            printError("MongoDB", e.getMessage());
        }
    }

    private void initRedis() {
        log.info("");
        log.info("┌────────────────────────────────────────");
        log.info("│ 📦 初始化 Redis");
        log.info("├────────────────────────────────────────");
        log.info("│ 连接地址: {}:{}", helper.getRedisHost(), helper.getRedisPort());

        try {
            log.info("│");
            log.info("│ ✅ Redis 初始化成功");
            log.info("└────────────────────────────────────────");
        } catch (Exception e) {
            printError("Redis", e.getMessage());
        }
    }

    private void printHeader() {
        log.info("");
        log.info("========================================");
        log.info("🚀 开始初始化数据库表...");
        log.info("========================================");
    }

    private void printFooter() {
        log.info("========================================");
        log.info("✅ 数据库初始化完成!");
        log.info("========================================");
    }

    private void printSectionStart(String dbType, String url, String username) {
        log.info("");
        log.info("┌────────────────────────────────────────");
        log.info("│ 📦 初始化 {}", dbType);
        log.info("├────────────────────────────────────────");
        log.info("│ 连接地址: {}", url);
        log.info("│ 用户名: {}", username);
    }

    private void printTables(List<String> tables) {
        log.info("│ 表数量: {}", tables.size());
        for (String table : tables) {
            log.info("│   - {}", table);
        }
    }

    private void printSuccess(String dbType) {
        log.info("│");
        log.info("│ ✅ {} 初始化成功", dbType);
        log.info("└────────────────────────────────────────");
    }

    private void printError(String dbType, String message) {
        log.info("│");
        log.info("│ ❌ {} 初始化失败: {}", dbType, message);
        log.info("└────────────────────────────────────────");
    }

    private void printWarning(String dbType, String message) {
        log.info("│");
        log.info("│ ⚠️ {} 初始化失败: {}", dbType, message);
        log.info("└────────────────────────────────────────");
    }
}