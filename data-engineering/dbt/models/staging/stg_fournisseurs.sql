select
    source_id as fournisseur_id,
    nom as fournisseur_nom,
    adresse,
    email,
    telephone,
    conditions,
    actif,
    date_creation,
    ingestion_run_id,
    ingested_at
from {{ source('raw', 'raw_fournisseurs') }}

