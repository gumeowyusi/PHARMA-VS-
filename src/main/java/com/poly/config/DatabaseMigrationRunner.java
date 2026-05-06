package com.poly.config;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

/**
 * Creates new tables introduced after initial schema deployment.
 * Each statement is idempotent (uses IF NOT EXISTS / IF OBJECT_ID checks).
 */
@Component
public class DatabaseMigrationRunner implements ApplicationRunner {

    private final JdbcTemplate jdbc;

    public DatabaseMigrationRunner(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public void run(ApplicationArguments args) {
        // LIEN_HE – contact messages from website visitors
        jdbc.execute("""
            IF OBJECT_ID(N'dbo.LIEN_HE', N'U') IS NULL
            CREATE TABLE dbo.LIEN_HE (
                id        INT IDENTITY(1,1) PRIMARY KEY,
                ho_ten    NVARCHAR(150) NOT NULL,
                sdt       NVARCHAR(20)  NULL,
                noi_dung  NVARCHAR(MAX) NOT NULL,
                ngay_gui  DATETIME      NOT NULL DEFAULT GETDATE(),
                da_doc    BIT           NOT NULL DEFAULT 0
            )
            """);
    }
}
