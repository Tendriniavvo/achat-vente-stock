with demandes as (
    select
        da.demande_achat_id,
        da.reference,
        da.demandeur_id,
        da.date_creation,
        da.statut,
        coalesce(sum(lda.montant_estime), 0) as montant_estime
    from {{ ref('stg_demandes_achat') }} da
    left join {{ ref('stg_lignes_demandes_achat') }} lda
        on da.demande_achat_id = lda.demande_achat_id
    group by
        da.demande_achat_id,
        da.reference,
        da.demandeur_id,
        da.date_creation,
        da.statut
),

commandes as (
    select
        demande_achat_id,
        count(*) as nombre_bons_commande,
        sum(montant_total) as montant_commandes
    from {{ ref('stg_bons_commande_fournisseur') }}
    group by demande_achat_id
)

select
    d.demande_achat_id,
    d.reference,
    d.demandeur_id,
    d.date_creation,
    d.statut,
    d.montant_estime,
    coalesce(c.nombre_bons_commande, 0) as nombre_bons_commande,
    coalesce(c.montant_commandes, 0) as montant_commandes,
    case when d.statut in ('soumise', 'en attente') then true else false end as is_demande_en_attente
from demandes d
left join commandes c
    on d.demande_achat_id = c.demande_achat_id

