select
    source_id as demande_achat_id,
    reference,
    demandeur_id,
    date_creation,
    statut,
    motif_rejet,
    historique_validations,
    ingestion_run_id,
    ingested_at
from {{ source('raw', 'raw_demandes_achat') }}

