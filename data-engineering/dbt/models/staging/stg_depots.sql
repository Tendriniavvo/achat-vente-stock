select
    source_id as depot_id,
    code as depot_code,
    nom as depot_nom,
    adresse,
    responsable,
    capacite,
    type_entreposage,
    horaires_ouverture,
    actif,
    ingestion_run_id,
    ingested_at
from {{ source('raw', 'raw_depots') }}

