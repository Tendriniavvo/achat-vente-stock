select
    s.stock_id,
    s.article_id,
    a.article_code,
    a.article_nom,
    s.depot_id,
    d.depot_code,
    d.depot_nom,
    s.quantite,
    a.stock_min,
    a.stock_max,
    s.cout_unitaire,
    s.valeur,
    case
        when a.stock_min is not null and s.quantite < a.stock_min then true
        else false
    end as is_stock_alerte,
    case
        when a.stock_min is not null and s.quantite < a.stock_min then a.stock_min - s.quantite
        else 0
    end as quantite_a_reapprovisionner,
    s.date_maj,
    s.ingested_at
from {{ ref('stg_stocks') }} s
left join {{ ref('stg_articles') }} a
    on s.article_id = a.article_id
left join {{ ref('stg_depots') }} d
    on s.depot_id = d.depot_id

