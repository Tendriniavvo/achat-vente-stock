DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_roles WHERE rolname = 'analytics_etl_role') THEN
        CREATE ROLE analytics_etl_role NOLOGIN;
    END IF;

    IF NOT EXISTS (SELECT 1 FROM pg_roles WHERE rolname = 'analytics_dbt_role') THEN
        CREATE ROLE analytics_dbt_role NOLOGIN;
    END IF;

    IF NOT EXISTS (SELECT 1 FROM pg_roles WHERE rolname = 'analytics_bi_role') THEN
        CREATE ROLE analytics_bi_role NOLOGIN;
    END IF;
END
$$;

GRANT USAGE ON SCHEMA raw TO analytics_etl_role, analytics_dbt_role;
GRANT USAGE ON SCHEMA staging TO analytics_dbt_role, analytics_bi_role;
GRANT USAGE ON SCHEMA marts TO analytics_dbt_role, analytics_bi_role;
GRANT USAGE ON SCHEMA audit TO analytics_etl_role, analytics_dbt_role;

GRANT SELECT, INSERT, UPDATE, DELETE ON ALL TABLES IN SCHEMA raw TO analytics_etl_role;
GRANT SELECT ON ALL TABLES IN SCHEMA raw TO analytics_dbt_role;

GRANT SELECT, INSERT, UPDATE, DELETE ON ALL TABLES IN SCHEMA staging TO analytics_dbt_role;
GRANT SELECT, INSERT, UPDATE, DELETE ON ALL TABLES IN SCHEMA marts TO analytics_dbt_role;
GRANT SELECT, INSERT, UPDATE, DELETE ON ALL TABLES IN SCHEMA audit TO analytics_etl_role, analytics_dbt_role;

GRANT SELECT ON ALL TABLES IN SCHEMA staging TO analytics_bi_role;
GRANT SELECT ON ALL TABLES IN SCHEMA marts TO analytics_bi_role;

ALTER DEFAULT PRIVILEGES IN SCHEMA raw
GRANT SELECT, INSERT, UPDATE, DELETE ON TABLES TO analytics_etl_role;

ALTER DEFAULT PRIVILEGES IN SCHEMA raw
GRANT SELECT ON TABLES TO analytics_dbt_role;

ALTER DEFAULT PRIVILEGES IN SCHEMA staging
GRANT SELECT, INSERT, UPDATE, DELETE ON TABLES TO analytics_dbt_role;

ALTER DEFAULT PRIVILEGES IN SCHEMA marts
GRANT SELECT, INSERT, UPDATE, DELETE ON TABLES TO analytics_dbt_role;

ALTER DEFAULT PRIVILEGES IN SCHEMA marts
GRANT SELECT ON TABLES TO analytics_bi_role;

ALTER DEFAULT PRIVILEGES IN SCHEMA audit
GRANT SELECT, INSERT, UPDATE, DELETE ON TABLES TO analytics_etl_role, analytics_dbt_role;

