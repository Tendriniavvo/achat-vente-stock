select
    source_id as article_id,
    code as article_code,
    nom as article_nom,
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
    date_creation,
    ingestion_run_id,
    ingested_at
from {{ source('raw', 'raw_articles') }}

