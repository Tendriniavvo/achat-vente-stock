select
    source_id as departement_id,
    code as departement_code,
    nom as departement_nom,
    description,
    actif,
    ingestion_run_id,
    ingested_at
from {{ source('raw', 'raw_departements') }}

