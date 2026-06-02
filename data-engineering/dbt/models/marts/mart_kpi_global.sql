select
    (select count(*) from {{ ref('stg_articles') }}) as total_articles,
    (select count(*) from {{ ref('mart_dashboard_stock') }} where is_stock_alerte = true) as total_alertes_stock,
    (select coalesce(sum(valeur), 0) from {{ ref('mart_dashboard_stock') }}) as valeur_totale_stock,
    (select count(*) from {{ ref('mart_dashboard_budget') }} where is_budget_alerte = true) as total_alertes_budget,
    (select coalesce(sum(montant_initial), 0) from {{ ref('mart_dashboard_budget') }}) as budget_initial_total,
    (select coalesce(sum(montant_consomme), 0) from {{ ref('mart_dashboard_budget') }}) as budget_consomme_total,
    (select coalesce(sum(montant_disponible), 0) from {{ ref('mart_dashboard_budget') }}) as budget_disponible_total,
    (select count(*) from {{ ref('stg_demandes_achat') }}) as total_demandes_achat,
    (select count(*) from {{ ref('mart_dashboard_achats') }} where is_demande_en_attente = true) as total_demandes_achat_en_attente,
    current_timestamp as calculated_at

