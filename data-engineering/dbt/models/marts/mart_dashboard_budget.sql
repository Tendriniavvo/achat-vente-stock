select
    b.budget_id,
    b.departement_id,
    d.departement_code,
    d.departement_nom,
    b.annee,
    b.montant_initial,
    b.montant_consomme,
    b.montant_disponible,
    b.taux_utilisation,
    case
        when b.taux_utilisation >= 1 then 'depasse'
        when b.taux_utilisation >= 0.9 then 'alerte'
        when b.taux_utilisation >= 0.75 then 'surveillance'
        else 'normal'
    end as statut_budget,
    case
        when b.taux_utilisation >= 0.9 then true
        else false
    end as is_budget_alerte,
    b.date_creation,
    b.ingested_at
from {{ ref('stg_budgets') }} b
left join {{ ref('stg_departements') }} d
    on b.departement_id = d.departement_id

