select
    source_id as budget_id,
    departement_id,
    annee,
    montant_initial,
    montant_consomme,
    montant_disponible,
    case
        when montant_initial = 0 then null
        else montant_consomme / montant_initial
    end as taux_utilisation,
    date_creation,
    ingestion_run_id,
    ingested_at
from {{ source('raw', 'raw_budgets') }}

