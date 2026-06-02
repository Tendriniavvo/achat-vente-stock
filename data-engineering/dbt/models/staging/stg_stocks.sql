select
    source_id as stock_id,
    article_id,
    depot_id,
    emplacement_id,
    quantite,
    cout_unitaire,
    valeur,
    date_maj,
    ingestion_run_id,
    ingested_at
from {{ source('raw', 'raw_stocks') }}

