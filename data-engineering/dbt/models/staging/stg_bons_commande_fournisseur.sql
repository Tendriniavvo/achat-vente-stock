select
    source_id as bon_commande_fournisseur_id,
    reference,
    demande_achat_id,
    fournisseur_id,
    date_commande,
    date_livraison_prevue,
    statut,
    montant_total,
    utilisateur_id,
    ingestion_run_id,
    ingested_at
from {{ source('raw', 'raw_bons_commande_fournisseur') }}

