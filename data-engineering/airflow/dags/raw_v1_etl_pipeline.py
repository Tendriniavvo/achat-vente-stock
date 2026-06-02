from __future__ import annotations

from datetime import datetime

from airflow.decorators import dag, task
from airflow.exceptions import AirflowException
from airflow.operators.empty import EmptyOperator
from airflow.operators.python import get_current_context
from airflow.providers.postgres.hooks.postgres import PostgresHook


SOURCE_CONN_ID = "postgres_source"
ANALYTICS_CONN_ID = "postgres_analytics"


TABLE_CONFIGS = [
    {
        "name": "departements",
        "target_table": "raw.raw_departements",
        "columns": ["source_id", "code", "nom", "description", "actif"],
        "source_sql": """
            SELECT
                id AS source_id,
                code,
                nom,
                description,
                actif
            FROM departements
        """,
    },
    {
        "name": "articles",
        "target_table": "raw.raw_articles",
        "columns": [
            "source_id",
            "code",
            "nom",
            "description",
            "categorie_id",
            "unite_id",
            "taxe_id",
            "prix_achat",
            "prix_vente",
            "methode_valorisation",
            "stock_min",
            "stock_max",
            "traceable_lot",
            "stock_strategy",
            "actif",
            "historique",
            "date_creation",
        ],
        "source_sql": """
            SELECT
                id AS source_id,
                code,
                nom,
                description,
                categorie_id,
                unite_id,
                taxe_id,
                prix_achat,
                prix_vente,
                methode_valorisation,
                stock_min,
                stock_max,
                traceable_lot,
                stock_strategy,
                actif,
                historique,
                date_creation
            FROM articles
        """,
    },
    {
        "name": "depots",
        "target_table": "raw.raw_depots",
        "columns": [
            "source_id",
            "nom",
            "code",
            "adresse",
            "responsable",
            "capacite",
            "type_entreposage",
            "horaires_ouverture",
            "actif",
        ],
        "source_sql": """
            SELECT
                id AS source_id,
                nom,
                code,
                adresse,
                responsable,
                capacite,
                type_entreposage,
                horaires_ouverture,
                actif
            FROM depots
        """,
    },
    {
        "name": "fournisseurs",
        "target_table": "raw.raw_fournisseurs",
        "columns": [
            "source_id",
            "nom",
            "adresse",
            "email",
            "telephone",
            "conditions",
            "actif",
            "historique",
            "date_creation",
        ],
        "source_sql": """
            SELECT
                id AS source_id,
                nom,
                adresse,
                email,
                telephone,
                conditions,
                actif,
                historique,
                date_creation
            FROM fournisseurs
        """,
    },
    {
        "name": "budgets",
        "target_table": "raw.raw_budgets",
        "columns": [
            "source_id",
            "departement_id",
            "annee",
            "montant_initial",
            "montant_consomme",
            "montant_disponible",
            "date_creation",
        ],
        "source_sql": """
            SELECT
                id AS source_id,
                departement_id,
                annee,
                montant_initial,
                montant_consomme,
                montant_disponible,
                date_creation
            FROM budgets
        """,
    },
    {
        "name": "demandes_achat",
        "target_table": "raw.raw_demandes_achat",
        "columns": [
            "source_id",
            "reference",
            "demandeur_id",
            "date_creation",
            "statut",
            "motif_rejet",
            "historique_validations",
        ],
        "source_sql": """
            SELECT
                id AS source_id,
                reference,
                demandeur_id,
                date_creation,
                statut,
                motif_rejet,
                historique_validations
            FROM demandes_achat
        """,
    },
    {
        "name": "lignes_demandes_achat",
        "target_table": "raw.raw_lignes_demandes_achat",
        "columns": [
            "source_id",
            "demande_achat_id",
            "article_id",
            "quantite",
            "prix_estime",
        ],
        "source_sql": """
            SELECT
                id AS source_id,
                demande_achat_id,
                article_id,
                quantite,
                prix_estime
            FROM lignes_demandes_achat
        """,
    },
    {
        "name": "bons_commande_fournisseur",
        "target_table": "raw.raw_bons_commande_fournisseur",
        "columns": [
            "source_id",
            "reference",
            "demande_achat_id",
            "fournisseur_id",
            "date_commande",
            "date_livraison_prevue",
            "statut",
            "montant_total",
            "utilisateur_id",
        ],
        "source_sql": """
            SELECT
                id AS source_id,
                reference,
                demande_achat_id,
                fournisseur_id,
                date_commande,
                date_livraison_prevue,
                statut,
                montant_total,
                utilisateur_id
            FROM bons_commande_fournisseur
        """,
    },
    {
        "name": "stocks",
        "target_table": "raw.raw_stocks",
        "columns": [
            "source_id",
            "article_id",
            "depot_id",
            "emplacement_id",
            "quantite",
            "cout_unitaire",
            "valeur",
            "date_maj",
        ],
        "source_sql": """
            SELECT
                id AS source_id,
                article_id,
                depot_id,
                emplacement_id,
                quantite,
                cout_unitaire,
                valeur,
                date_maj
            FROM stocks
        """,
    },
    {
        "name": "mouvements_stock",
        "target_table": "raw.raw_mouvements_stock",
        "columns": [
            "source_id",
            "type",
            "article_id",
            "quantite",
            "cout",
            "date_mouvement",
            "depot_id",
            "emplacement_id",
            "lot_id",
            "reference_document",
            "utilisateur_id",
            "motif",
        ],
        "source_sql": """
            SELECT
                id AS source_id,
                type,
                article_id,
                quantite,
                cout,
                date_mouvement,
                depot_id,
                emplacement_id,
                lot_id,
                reference_document,
                utilisateur_id,
                motif
            FROM mouvements_stock
        """,
    },
]


def _audit_pipeline_run(
    analytics_hook: PostgresHook,
    *,
    dag_id: str,
    task_id: str,
    run_id: str,
    status: str,
    rows_processed: int,
    started_at: datetime,
    finished_at: datetime,
    details: str | None = None,
) -> None:
    analytics_hook.run(
        """
        INSERT INTO audit.pipeline_runs (
            dag_id,
            task_id,
            run_id,
            status,
            started_at,
            finished_at,
            rows_processed,
            details
        )
        VALUES (%s, %s, %s, %s, %s, %s, %s, %s)
        """,
        parameters=(
            dag_id,
            task_id,
            run_id,
            status,
            started_at,
            finished_at,
            rows_processed,
            details,
        ),
    )


@dag(
    dag_id="raw_v1_etl_pipeline",
    start_date=datetime(2026, 1, 1),
    schedule=None,
    catchup=False,
    tags=["etl", "raw", "v1", "postgres"],
)
def raw_v1_etl_pipeline():
    start = EmptyOperator(task_id="start")
    end = EmptyOperator(task_id="end")

    @task
    def load_raw_table(config: dict) -> int:
        from psycopg2.extras import execute_values

        context = get_current_context()
        dag_id = context["dag"].dag_id
        task_id = context["task"].task_id
        run_id = context["run_id"]
        started_at = datetime.utcnow()

        source_hook = PostgresHook(postgres_conn_id=SOURCE_CONN_ID)
        analytics_hook = PostgresHook(postgres_conn_id=ANALYTICS_CONN_ID)

        target_table = config["target_table"]
        source_columns = config["columns"]
        insert_columns = source_columns + ["ingestion_run_id"]
        column_sql = ", ".join(insert_columns)

        try:
            source_rows = source_hook.get_records(config["source_sql"])
            rows_to_insert = [tuple(row) + (run_id,) for row in source_rows]

            analytics_conn = analytics_hook.get_conn()
            try:
                with analytics_conn.cursor() as cursor:
                    cursor.execute(f"TRUNCATE TABLE {target_table};")

                    if rows_to_insert:
                        insert_sql = f"""
                            INSERT INTO {target_table} ({column_sql})
                            VALUES %s
                        """
                        execute_values(cursor, insert_sql, rows_to_insert, page_size=1000)

                analytics_conn.commit()
            finally:
                analytics_conn.close()

            finished_at = datetime.utcnow()
            row_count = len(rows_to_insert)
            _audit_pipeline_run(
                analytics_hook,
                dag_id=dag_id,
                task_id=task_id,
                run_id=run_id,
                status="success",
                rows_processed=row_count,
                started_at=started_at,
                finished_at=finished_at,
                details=f"Loaded {row_count} rows into {target_table}",
            )
            return row_count
        except Exception as exc:
            finished_at = datetime.utcnow()
            _audit_pipeline_run(
                analytics_hook,
                dag_id=dag_id,
                task_id=task_id,
                run_id=run_id,
                status="failed",
                rows_processed=0,
                started_at=started_at,
                finished_at=finished_at,
                details=str(exc),
            )
            raise AirflowException(f"Failed loading {target_table}: {exc}") from exc

    previous_task = start
    for table_config in TABLE_CONFIGS:
        current_task = load_raw_table.override(
            task_id=f"load_{table_config['name']}"
        )(table_config)
        previous_task >> current_task
        previous_task = current_task

    previous_task >> end


raw_v1_etl_pipeline()
