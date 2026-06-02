from datetime import datetime

from airflow.decorators import dag, task
from airflow.providers.postgres.hooks.postgres import PostgresHook


@dag(
    dag_id="test_postgres_connections",
    start_date=datetime(2026, 1, 1),
    schedule=None,
    catchup=False,
    tags=["test", "postgres"],
)
def test_postgres_connections():
    @task
    def test_analytics_connection():
        hook = PostgresHook(postgres_conn_id="postgres_analytics")
        result = hook.get_first("SELECT COUNT(*) FROM audit.pipeline_runs;")
        print(f"Analytics connection OK. pipeline_runs count = {result[0]}")

    @task
    def test_source_connection():
        hook = PostgresHook(postgres_conn_id="postgres_source")
        result = hook.get_first("SELECT COUNT(*) FROM articles;")
        print(f"Source connection OK. articles count = {result[0]}")

    test_analytics_connection()
    test_source_connection()


test_postgres_connections()