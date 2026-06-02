select
    source_id as ligne_demande_achat_id,
    demande_achat_id,
    article_id,
    quantite,
    prix_estime,
    quantite * prix_estime as montant_estime,
    ingestion_run_id,
    ingested_at
from {{ source('raw', 'raw_lignes_demandes_achat') }}

