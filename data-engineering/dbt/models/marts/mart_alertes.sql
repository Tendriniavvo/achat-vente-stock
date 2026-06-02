select
    'stock' as alerte_type,
    case
        when quantite = 0 then 'critical'
        else 'warning'
    end as severity,
    cast(stock_id as varchar) as business_key,
    article_nom || ' - stock faible dans ' || coalesce(depot_nom, 'depot inconnu') as message,
    article_id,
    depot_id,
    null::integer as departement_id,
    quantite,
    stock_min,
    null::numeric as taux_utilisation,
    ingested_at
from {{ ref('mart_dashboard_stock') }}
where is_stock_alerte = true

union all

select
    'budget' as alerte_type,
    case
        when taux_utilisation >= 1 then 'critical'
        else 'warning'
    end as severity,
    cast(budget_id as varchar) as business_key,
    departement_nom || ' - budget utilise a ' || round((taux_utilisation * 100)::numeric, 2)::varchar || '%' as message,
    null::integer as article_id,
    null::integer as depot_id,
    departement_id,
    null::integer as quantite,
    null::integer as stock_min,
    taux_utilisation,
    ingested_at
from {{ ref('mart_dashboard_budget') }}
where is_budget_alerte = true

