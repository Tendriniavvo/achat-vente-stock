select
    source_id as mouvement_stock_id,
    type as mouvement_type,
    article_id,
    quantite,
    cout,
    date_mouvement,
    depot_id,
    emplacement_id,
    lot_id,
    reference_document,
    utilisateur_id,
    motif,
    ingestion_run_id,
    ingested_at
from {{ source('raw', 'raw_mouvements_stock') }}

