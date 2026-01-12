<template>
  <MainLayout>
    <!--  Row 1 -->
    <div class="row">
      <div class="col-lg-12">
        <div class="card">
          <div class="card-body">
            <h5 class="card-title fw-semibold mb-4">Bienvenue sur le Dashboard</h5>
            <p>Système de Gestion Achat-Vente-Stock</p>
            <p v-if="loading" class="mb-0">Chargement...</p>
            <p v-else-if="error" class="mb-0 text-danger">{{ error }}</p>
          </div>
        </div>
      </div>
    </div>
    
    <!--  Row 2 -->
    <div class="row">
      <div class="col-lg-3 col-md-6">
        <div class="card">
          <div class="card-body">
            <div class="d-flex align-items-center">
              <div class="me-3">
                <span class="round-40 bg-light-primary rounded-circle d-flex align-items-center justify-content-center">
                  <i class="ti ti-shopping-cart text-primary fs-6"></i>
                </span>
              </div>
              <div>
                <p class="text-dark mb-0 fs-3">Valeur stock</p>
                <h5 class="fw-semibold mb-0">{{ displayValeurStock }}</h5>
              </div>
            </div>
          </div>
        </div>
      </div>
      
      <div class="col-lg-3 col-md-6">
        <div class="card">
          <div class="card-body">
            <div class="d-flex align-items-center">
              <div class="me-3">
                <span class="round-40 bg-light-success rounded-circle d-flex align-items-center justify-content-center">
                  <i class="ti ti-coin text-success fs-6"></i>
                </span>
              </div>
              <div>
                <p class="text-dark mb-0 fs-3">Commandes en cours</p>
                <h5 class="fw-semibold mb-0">{{ kpis?.nombreCommandesEnCours ?? '-' }}</h5>
              </div>
            </div>
          </div>
        </div>
      </div>
      
      <div class="col-lg-3 col-md-6">
        <div class="card">
          <div class="card-body">
            <div class="d-flex align-items-center">
              <div class="me-3">
                <span class="round-40 bg-light-warning rounded-circle d-flex align-items-center justify-content-center">
                  <i class="ti ti-package text-warning fs-6"></i>
                </span>
              </div>
              <div>
                <p class="text-dark mb-0 fs-3">Taux rotation</p>
                <h5 class="fw-semibold mb-0">{{ displayRotation }}</h5>
              </div>
            </div>
          </div>
        </div>
      </div>
      
      <div class="col-lg-3 col-md-6">
        <div class="card">
          <div class="card-body">
            <div class="d-flex align-items-center">
              <div class="me-3">
                <span class="round-40 bg-light-danger rounded-circle d-flex align-items-center justify-content-center">
                  <i class="ti ti-users text-danger fs-6"></i>
                </span>
              </div>
              <div>
                <p class="text-dark mb-0 fs-3">Factures (attente/payées)</p>
                <h5 class="fw-semibold mb-0">{{ displayFactures }}</h5>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <div class="achat-section rounded-4 p-4 p-md-5 mt-2">
      <div class="d-flex flex-column flex-md-row align-items-md-center justify-content-between gap-3">
        <div class="achat-section-title">
          <div class="d-flex align-items-center gap-3">
            <div class="achat-section-icon" aria-hidden="true">
              <i class="ti ti-shopping-cart"></i>
            </div>
            <div>
              <div class="achat-section-heading">MODULE ACHATS</div>
              <div class="achat-section-subheading">Gestion des approvisionnements et fournisseurs</div>
            </div>
          </div>
        </div>
        <div class="d-flex align-items-center gap-2 flex-wrap">
          <div class="btn-group" role="group" aria-label="Filtre période achats">
            <button type="button" class="btn btn-sm" :class="periode === 'jour' ? 'btn-primary' : 'btn-outline-primary'" @click="periode = 'jour'">Aujourd'hui</button>
            <button type="button" class="btn btn-sm" :class="periode === 'semaine' ? 'btn-primary' : 'btn-outline-primary'" @click="periode = 'semaine'">Semaine</button>
            <button type="button" class="btn btn-sm" :class="periode === 'mois' ? 'btn-primary' : 'btn-outline-primary'" @click="periode = 'mois'">Mois</button>
            <button type="button" class="btn btn-sm" :class="periode === 'annee' ? 'btn-primary' : 'btn-outline-primary'" @click="periode = 'annee'">Année</button>
          </div>
          <button type="button" class="btn btn-sm btn-light achat-refresh" @click="refreshAchats" :disabled="refreshing" :aria-label="refreshing ? 'Actualisation en cours' : 'Actualiser'">
            <span class="d-inline-flex align-items-center gap-2">
              <i class="ti" :class="refreshing ? 'ti-loader-2 achat-spin' : 'ti-refresh'"></i>
              <span>Refresh</span>
            </span>
          </button>
          <div class="achat-updated text-muted">MAJ: {{ lastUpdatedLabel }}</div>
        </div>
      </div>

      <div class="row g-3 g-md-4 mt-3">
        <div class="col-12 col-md-6 col-xl-3">
          <div class="achat-kpi-card achat-hover cursor-pointer" tabindex="0" role="button" aria-label="Voir les demandes d'achat en attente" @click="onCardClick('da_attente')" @keydown.enter="onCardClick('da_attente')">
            <div class="achat-kpi-left" style="border-left-color:#f97316">
              <div class="achat-kpi-icon" style="background:rgba(249,115,22,0.12);color:#f97316">
                <i class="ti ti-file-text"></i>
                <span v-if="achatKpis.demandesAttente.valeur > 0" class="achat-kpi-dot"></span>
              </div>
            </div>
            <div class="achat-kpi-content">
              <div class="d-flex align-items-start justify-content-between gap-2">
                <div class="achat-kpi-label">Demandes d'achat en attente</div>
                <span v-if="achatKpis.demandesAttente.joursMax > 5" class="badge text-bg-warning">Urgent</span>
              </div>
              <div class="achat-kpi-value">{{ achatKpis.demandesAttente.valeur }}</div>
              <div class="d-flex align-items-center justify-content-between gap-2">
                <div class="achat-kpi-desc">{{ achatKpis.demandesAttente.tendance }}</div>
                <div class="achat-kpi-trend text-warning">
                  <i class="ti ti-arrow-up-right"></i>
                </div>
              </div>
              <div class="achat-kpi-hint">Cliquer pour voir détails</div>
            </div>
          </div>
        </div>

        <div class="col-12 col-md-6 col-xl-3">
          <div class="achat-kpi-card achat-hover cursor-pointer" tabindex="0" role="button" aria-label="Voir les bons de commande" @click="onCardClick('bc_mois')" @keydown.enter="onCardClick('bc_mois')">
            <div class="achat-kpi-left" style="border-left-color:#3b82f6">
              <div class="achat-kpi-icon" style="background:rgba(59,130,246,0.12);color:#3b82f6">
                <i class="ti ti-shopping-bag"></i>
              </div>
            </div>
            <div class="achat-kpi-content">
              <div class="achat-kpi-label">Bons de commande ce mois</div>
              <div class="achat-kpi-value">{{ achatKpis.bcMois.valeur }}</div>
              <div class="d-flex align-items-center justify-content-between gap-2">
                <div class="achat-kpi-desc">{{ achatKpis.bcMois.montant }}</div>
                <div class="achat-kpi-trend text-success">
                  <i class="ti ti-trending-up"></i>
                  <span class="achat-kpi-trend-text">{{ achatKpis.bcMois.comparaison }}</span>
                </div>
              </div>
              <div class="achat-kpi-hint">Cliquer pour voir détails</div>
            </div>
          </div>
        </div>

        <div class="col-12 col-md-6 col-xl-3">
          <div class="achat-kpi-card achat-hover cursor-pointer" tabindex="0" role="button" aria-label="Voir les réceptions en attente" @click="onCardClick('receptions_attente')" @keydown.enter="onCardClick('receptions_attente')">
            <div class="achat-kpi-left" style="border-left-color:#8b5cf6">
              <div class="achat-kpi-icon" style="background:rgba(139,92,246,0.12);color:#8b5cf6">
                <i class="ti ti-package"></i>
                <span v-if="achatKpis.receptionsAttente.retards > 0" class="achat-kpi-badge">{{ achatKpis.receptionsAttente.retards }}</span>
              </div>
            </div>
            <div class="achat-kpi-content">
              <div class="achat-kpi-label">Réceptions en attente</div>
              <div class="achat-kpi-value">{{ achatKpis.receptionsAttente.valeur }}</div>
              <div class="d-flex align-items-center justify-content-between gap-2">
                <div class="achat-kpi-desc">{{ achatKpis.receptionsAttente.description }}</div>
                <div class="achat-kpi-trend" :class="achatKpis.receptionsAttente.retards > 0 ? 'text-danger' : 'text-muted'">
                  <i class="ti" :class="achatKpis.receptionsAttente.retards > 0 ? 'ti-alert-triangle' : 'ti-check'"></i>
                </div>
              </div>
              <div class="achat-kpi-hint">Cliquer pour voir détails</div>
            </div>
          </div>
        </div>

        <div class="col-12 col-md-6 col-xl-3">
          <div class="achat-kpi-card achat-hover cursor-pointer" tabindex="0" role="button" aria-label="Voir le taux de conformité" @click="onCardClick('conformite')" @keydown.enter="onCardClick('conformite')">
            <div class="achat-kpi-left" :style="{ borderLeftColor: conformiteColor }">
              <div class="achat-kpi-icon" :style="{ background: conformiteBg, color: conformiteColor }">
                <i class="ti ti-shield-check"></i>
              </div>
            </div>
            <div class="achat-kpi-content">
              <div class="d-flex align-items-start justify-content-between gap-2">
                <div class="achat-kpi-label">Taux de conformité</div>
                <span class="badge" :class="conformiteBadgeClass">{{ achatKpis.conformite.valeur }}%</span>
              </div>
              <div class="d-flex align-items-center gap-3">
                <div class="achat-gauge" :style="{ '--pct': achatKpis.conformite.valeur, '--gauge': conformiteColor }" aria-label="Gauge conformité" role="img">
                  <div class="achat-gauge-inner">
                    <div class="achat-gauge-value">{{ achatKpis.conformite.valeur }}%</div>
                    <div class="achat-gauge-sub">Conforme</div>
                  </div>
                </div>
                <div class="flex-grow-1">
                  <div class="achat-kpi-desc">{{ achatKpis.conformite.tendance }}</div>
                  <div class="achat-kpi-hint">Cliquer pour voir détails</div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <div class="row g-3 g-md-4 mt-1">
        <div class="col-12 col-md-6 col-xl-4">
          <div class="card achat-card h-100">
            <div class="card-body">
              <div class="d-flex align-items-start justify-content-between gap-2">
                <div>
                  <div class="fw-semibold">Demandes d'achat par statut</div>
                  <div class="text-muted small">Répartition actuelle</div>
                </div>
                <span v-if="achatCharts.daStatut.soumise > 5" class="badge text-bg-warning">{{ achatCharts.daStatut.soumise }} en attente</span>
              </div>
              <div class="d-flex flex-column align-items-center mt-3">
                <div class="achat-donut" aria-label="Donut demandes achat" role="img">
                  <div class="achat-donut-center">
                    <div class="achat-donut-total">{{ achatCharts.daStatut.total }}</div>
                    <div class="text-muted small">Total</div>
                  </div>
                </div>
                <div class="achat-legend mt-3 w-100">
                  <div class="achat-legend-row" v-for="item in achatCharts.daStatut.items" :key="item.label">
                    <span class="achat-dot" :style="{ background: item.color }" aria-hidden="true"></span>
                    <span class="flex-grow-1">{{ item.label }}</span>
                    <span class="fw-semibold">{{ item.value }}</span>
                  </div>
                </div>
                <div v-if="achatCharts.daStatut.soumise > 5" class="alert alert-warning w-100 mt-3 mb-0" role="status">
                  <strong>Attention</strong> : {{ achatCharts.daStatut.soumise }} DA en attente de validation
                </div>
              </div>
            </div>
          </div>
        </div>

        <div class="col-12 col-md-6 col-xl-4">
          <div class="card achat-card h-100">
            <div class="card-body">
              <div class="d-flex align-items-start justify-content-between gap-2">
                <div>
                  <div class="fw-semibold">Évolution des commandes</div>
                  <div class="text-muted small">6 derniers mois</div>
                </div>
                <span class="badge text-bg-light">Moyenne: {{ achatCharts.commandes.moyenne }}</span>
              </div>
              <div class="achat-area mt-3" aria-label="Zone évolution commandes" role="img">
                <div class="achat-area-grid">
                  <div class="achat-area-bar" v-for="m in achatCharts.commandes.mois" :key="m.label" :style="{ '--h': m.nb, '--h2': m.montantK }">
                    <div class="achat-area-tooltip">
                      <div class="fw-semibold">{{ m.label }}</div>
                      <div class="small text-muted">{{ m.nb }} commandes</div>
                      <div class="small text-muted">€ {{ m.montantK }}K</div>
                    </div>
                    <div class="achat-area-fill"></div>
                    <div class="achat-area-fill2"></div>
                  </div>
                </div>
              </div>
              <div class="text-muted small mt-2">Survoler une barre pour le détail</div>
            </div>
          </div>
        </div>

        <div class="col-12 col-xl-4">
          <div class="card achat-card h-100">
            <div class="card-body">
              <div class="d-flex align-items-start justify-content-between gap-2">
                <div>
                  <div class="fw-semibold">Conformité réceptions</div>
                  <div class="text-muted small">Qualité & écarts</div>
                </div>
                <span class="badge" :class="conformiteBadgeClass">{{ achatCharts.conformite.gauge }}%</span>
              </div>
              <div class="d-flex flex-column align-items-center mt-3">
                <div class="achat-gauge achat-gauge--semi" :style="{ '--pct': achatCharts.conformite.gauge, '--gauge': conformiteColor }" aria-label="Gauge semi-circulaire" role="img">
                  <div class="achat-gauge-inner">
                    <div class="achat-gauge-value">{{ achatCharts.conformite.gauge }}%</div>
                    <div class="achat-gauge-sub">Conforme</div>
                  </div>
                </div>
                <div class="w-100 mt-3">
                  <div class="achat-mini-bars">
                    <div class="achat-mini-bar" v-for="b in achatCharts.conformite.bars" :key="b.label">
                      <div class="d-flex align-items-center justify-content-between">
                        <span class="small">{{ b.label }}</span>
                        <span class="small fw-semibold">{{ b.value }}%</span>
                      </div>
                      <div class="progress" role="progressbar" :aria-valuenow="b.value" aria-valuemin="0" aria-valuemax="100">
                        <div class="progress-bar" :style="{ width: b.value + '%', background: b.color }"></div>
                      </div>
                    </div>
                  </div>
                </div>
                <div v-if="achatCharts.conformite.gauge < 80" class="alert alert-danger w-100 mt-3 mb-0" role="status">
                  <strong>Alerte</strong> : conformité inférieure à 80%
                </div>
              </div>
            </div>
          </div>
        </div>

        <div class="col-12 col-xl-8">
          <div class="card achat-card h-100">
            <div class="card-body">
              <div class="d-flex align-items-start justify-content-between gap-2">
                <div>
                  <div class="fw-semibold">Top 5 fournisseurs</div>
                  <div class="text-muted small">Montants & volume de commandes</div>
                </div>
                <span class="badge text-bg-light">{{ periodeLabel }}</span>
              </div>
              <div class="mt-3 achat-suppliers">
                <div class="achat-supplier-row" v-for="(s, idx) in achatCharts.topFournisseurs" :key="s.nom">
                  <div class="d-flex align-items-center gap-3">
                    <div class="achat-avatar" :style="{ background: s.avatarColor }" aria-hidden="true">{{ s.initiales }}</div>
                    <div class="flex-grow-1">
                      <div class="d-flex align-items-center gap-2 flex-wrap">
                        <div class="fw-semibold">{{ s.nom }}</div>
                        <span v-if="idx === 0" class="badge text-bg-primary">Top performer</span>
                      </div>
                      <div class="text-muted small">{{ s.nbCommandes }} commandes</div>
                      <div class="progress mt-2" role="progressbar" :aria-valuenow="s.pct" aria-valuemin="0" aria-valuemax="100">
                        <div class="progress-bar achat-supplier-bar" :style="{ width: s.pct + '%' }"></div>
                      </div>
                    </div>
                  </div>
                  <div class="fw-semibold">€ {{ s.montantK }}K</div>
                </div>
              </div>
            </div>
          </div>
        </div>

        <div class="col-12 col-md-6 col-xl-4">
          <div class="card achat-card h-100">
            <div class="card-body">
              <div class="d-flex align-items-start justify-content-between gap-2">
                <div>
                  <div class="fw-semibold">Délais de livraison</div>
                  <div class="text-muted small">En avance / À temps / En retard</div>
                </div>
                <div class="achat-kpi-big">{{ achatCharts.delais.kpiOnTime }}%</div>
              </div>
              <div class="mt-3">
                <div class="achat-stacked" v-for="row in achatCharts.delais.rows" :key="row.label">
                  <div class="small text-muted mb-1">{{ row.label }}</div>
                  <div class="achat-stacked-bar" role="img" aria-label="Barre empilée délais">
                    <div class="achat-stacked-seg" :style="{ width: row.avance + '%', background: '#10b981' }">{{ row.avance }}%</div>
                    <div class="achat-stacked-seg" :style="{ width: row.atemps + '%', background: '#3b82f6' }">{{ row.atemps }}%</div>
                    <div class="achat-stacked-seg" :style="{ width: row.retard + '%', background: '#ef4444' }">{{ row.retard }}%</div>
                  </div>
                </div>
                <div class="text-muted small mt-2">87% de livraisons à temps</div>
              </div>
            </div>
          </div>
        </div>

        <div class="col-12 col-md-6 col-xl-4">
          <div class="card achat-card h-100">
            <div class="card-body">
              <div class="d-flex align-items-start justify-content-between gap-2">
                <div>
                  <div class="fw-semibold">Pipeline achats</div>
                  <div class="text-muted small">Entonnoir du processus</div>
                </div>
              </div>
              <div class="achat-funnel mt-3" aria-label="Entonnoir pipeline achats" role="img">
                <div class="achat-funnel-step" v-for="(st, i) in achatCharts.pipeline" :key="st.label" :style="{ '--w': st.pct, '--c': st.color }">
                  <div class="d-flex align-items-center justify-content-between">
                    <div class="small fw-semibold">{{ i + 1 }}. {{ st.label }}</div>
                    <div class="small">{{ st.value }}</div>
                  </div>
                  <div class="achat-funnel-bar"></div>
                </div>
              </div>
              <div class="text-muted small mt-2">Conversion globale: {{ achatCharts.pipeline[achatCharts.pipeline.length - 1].pct }}%</div>
            </div>
          </div>
        </div>
      </div>

      <div class="row g-3 g-md-4 mt-1">
        <div class="col-12 col-xl-4">
          <div class="card achat-alert-card achat-alert-urgent h-100">
            <div class="card-body text-white">
              <div class="d-flex align-items-start justify-content-between">
                <div>
                  <div class="fw-bold fs-5">Actions urgentes</div>
                  <div class="opacity-75">À traiter rapidement</div>
                </div>
                <i class="ti ti-alert-triangle fs-2" aria-hidden="true"></i>
              </div>
              <div class="mt-3 achat-alert-list">
                <div class="achat-alert-item" v-for="it in achatActions.urgentes" :key="it.label" role="button" tabindex="0" @click="onActionClick(it.key)" @keydown.enter="onActionClick(it.key)">
                  <div class="d-flex align-items-center justify-content-between gap-2">
                    <div class="d-flex align-items-center gap-2">
                      <input class="form-check-input" type="checkbox" :aria-label="'Marquer ' + it.label" />
                      <span>{{ it.label }}</span>
                    </div>
                    <span class="badge" :class="it.badgeClass">{{ it.badge }}</span>
                  </div>
                </div>
              </div>
              <button type="button" class="btn btn-light w-100 mt-3" @click="onActionClick('traiter')">Traiter maintenant</button>
            </div>
          </div>
        </div>

        <div class="col-12 col-xl-4">
          <div class="card achat-alert-card achat-alert-activity h-100">
            <div class="card-body">
              <div class="d-flex align-items-start justify-content-between">
                <div>
                  <div class="fw-bold fs-5">Activité du jour</div>
                  <div class="text-muted">Planning & exécution</div>
                </div>
                <i class="ti ti-chart-bar fs-2 text-primary" aria-hidden="true"></i>
              </div>
              <div class="mt-3">
                <div class="mb-3" v-for="a in achatActions.activite" :key="a.label">
                  <div class="d-flex align-items-center justify-content-between">
                    <div class="fw-semibold">{{ a.label }}</div>
                    <div class="text-muted small">{{ a.done }}/{{ a.total }}</div>
                  </div>
                  <div class="progress" role="progressbar" :aria-valuenow="a.done" aria-valuemin="0" :aria-valuemax="a.total">
                    <div class="progress-bar" :style="{ width: (a.done / a.total * 100) + '%', background: a.color }"></div>
                  </div>
                </div>
              </div>
              <div class="achat-activity-mini" aria-label="Mini activité horaire" role="img">
                <div class="achat-activity-bar" v-for="(h, idx) in achatActions.horaire" :key="idx" :style="{ height: h + '%' }"></div>
              </div>
            </div>
          </div>
        </div>

        <div class="col-12 col-xl-4">
          <div class="card achat-alert-card achat-alert-performance h-100">
            <div class="card-body">
              <div class="d-flex align-items-start justify-content-between">
                <div>
                  <div class="fw-bold fs-5">Performance globale</div>
                  <div class="text-muted">Comparaison vs objectifs</div>
                </div>
                <i class="ti ti-trending-up fs-2 text-success" aria-hidden="true"></i>
              </div>
              <div class="row g-3 mt-1">
                <div class="col-6" v-for="m in achatActions.performance" :key="m.label">
                  <div class="achat-metric">
                    <div class="d-flex align-items-center gap-2">
                      <span class="achat-metric-icon" :style="{ background: m.bg, color: m.color }" aria-hidden="true">
                        <i class="ti" :class="m.icon"></i>
                      </span>
                      <div class="fw-semibold">{{ m.value }}</div>
                    </div>
                    <div class="text-muted small">{{ m.label }}</div>
                    <div class="small" :class="m.deltaClass">{{ m.delta }}</div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <div class="achat-insights rounded-4 p-4 p-md-5 mt-4">
        <div class="d-flex align-items-center justify-content-between flex-wrap gap-3">
          <div class="d-flex align-items-center gap-3">
            <div class="achat-insights-icon" aria-hidden="true">
              <i class="ti ti-bulb"></i>
            </div>
            <div>
              <div class="fw-bold fs-5 text-white">Insights et recommandations</div>
              <div class="text-white-50">Synthèse actionnable (données de démonstration)</div>
            </div>
          </div>
          <div class="d-flex align-items-center gap-2">
            <button type="button" class="btn btn-light" @click="onActionClick('rapport')">Générer rapport complet</button>
            <button type="button" class="btn btn-outline-light" @click="onActionClick('export')">Exporter données</button>
          </div>
        </div>
        <div class="row g-4 mt-2">
          <div class="col-12 col-lg-6">
            <div class="achat-insights-box">
              <div class="fw-semibold text-white">Points d'attention</div>
              <div class="mt-2 achat-insights-list">
                <div class="achat-insight" v-for="p in achatInsights.attention" :key="p">
                  <i class="ti ti-alert-circle text-warning" aria-hidden="true"></i>
                  <span class="text-white">{{ p }}</span>
                </div>
              </div>
            </div>
          </div>
          <div class="col-12 col-lg-6">
            <div class="achat-insights-box">
              <div class="fw-semibold text-white">Recommandations</div>
              <div class="mt-2 achat-insights-list">
                <div class="achat-insight" v-for="r in achatInsights.reco" :key="r">
                  <i class="ti ti-check text-success" aria-hidden="true"></i>
                  <span class="text-white">{{ r }}</span>
                </div>
              </div>
            </div>
          </div>
        </div>
        <div class="d-flex align-items-center justify-content-end mt-3">
          <div class="text-white-50 small">Dernière mise à jour : {{ lastUpdatedLabel }}</div>
        </div>
      </div>
    </div>
  </MainLayout>
</template>

<script setup>
import MainLayout from '@/layouts/MainLayout.vue';
import axios from 'axios';
import { computed, onMounted, ref } from 'vue';

const loading = ref(true);
const error = ref(null);
const kpis = ref(null);

const displayValeurStock = computed(() => {
  const val = kpis.value?.valeurTotaleStock;
  if (val === null || val === undefined) return '-';
  const n = Number(val);
  if (Number.isNaN(n)) return String(val);
  return n.toLocaleString(undefined, { minimumFractionDigits: 2, maximumFractionDigits: 2 });
});

const displayRotation = computed(() => {
  const val = kpis.value?.tauxRotationStocks;
  if (val === null || val === undefined) return '-';
  const n = Number(val);
  if (Number.isNaN(n)) return String(val);
  return n.toLocaleString(undefined, { minimumFractionDigits: 2, maximumFractionDigits: 2 });
});

const displayFactures = computed(() => {
  const attente = kpis.value?.facturesEnAttente;
  const payees = kpis.value?.facturesPayees;
  if (attente === null || attente === undefined || payees === null || payees === undefined) return '-';
  return `${attente}/${payees}`;
});

const periode = ref('mois');
const refreshing = ref(false);
const lastUpdated = ref(new Date());

const achatKpis = ref({
  demandesAttente: { valeur: 8, tendance: "3 nouvelles aujourd'hui", joursMax: 6 },
  bcMois: { valeur: 42, montant: "€ 245K", comparaison: "+12%" },
  receptionsAttente: { valeur: 15, description: "12 planifiées cette semaine", retards: 2 },
  conformite: { valeur: 85, tendance: "-3% ce mois" },
});

const achatCharts = ref({
  daStatut: {
    brouillon: 12,
    soumise: 8,
    validee: 25,
    rejetee: 3,
    get total() {
      return this.brouillon + this.soumise + this.validee + this.rejetee;
    },
    get items() {
      return [
        { label: 'Brouillon', value: this.brouillon, color: '#94a3b8' },
        { label: 'Soumise', value: this.soumise, color: '#fbbf24' },
        { label: 'Validée', value: this.validee, color: '#10b981' },
        { label: 'Rejetée', value: this.rejetee, color: '#ef4444' },
      ];
    },
  },
  commandes: {
    moyenne: '53 commandes',
    mois: [
      { label: 'Août', nb: 42, montantK: 180 },
      { label: 'Sep', nb: 51, montantK: 205 },
      { label: 'Oct', nb: 58, montantK: 260 },
      { label: 'Nov', nb: 49, montantK: 230 },
      { label: 'Déc', nb: 61, montantK: 275 },
      { label: 'Jan', nb: 57, montantK: 245 },
    ],
  },
  conformite: {
    gauge: 85,
    bars: [
      { label: 'Conforme', value: 85, color: '#10b981' },
      { label: 'Écarts mineurs', value: 12, color: '#fbbf24' },
      { label: 'Écarts majeurs', value: 3, color: '#ef4444' },
    ],
  },
  topFournisseurs: [
    { nom: 'Fournisseur A', initiales: 'FA', avatarColor: 'linear-gradient(135deg, #8b5cf6, #ec4899)', montantK: 78, nbCommandes: 14, pct: 100 },
    { nom: 'Fournisseur B', initiales: 'FB', avatarColor: 'linear-gradient(135deg, #3b82f6, #10b981)', montantK: 62, nbCommandes: 11, pct: 80 },
    { nom: 'Fournisseur C', initiales: 'FC', avatarColor: 'linear-gradient(135deg, #f97316, #ef4444)', montantK: 45, nbCommandes: 9, pct: 58 },
    { nom: 'Fournisseur D', initiales: 'FD', avatarColor: 'linear-gradient(135deg, #06b6d4, #3b82f6)', montantK: 38, nbCommandes: 7, pct: 49 },
    { nom: 'Fournisseur E', initiales: 'FE', avatarColor: 'linear-gradient(135deg, #22c55e, #10b981)', montantK: 22, nbCommandes: 5, pct: 28 },
  ],
  delais: {
    kpiOnTime: 87,
    rows: [
      { label: 'Semaine', avance: 10, atemps: 87, retard: 3 },
      { label: 'Mois', avance: 8, atemps: 79, retard: 13 },
    ],
  },
  pipeline: [
    { label: 'Demandes créées', value: 48, pct: 100, color: 'linear-gradient(90deg, #3b82f6, #60a5fa)' },
    { label: 'Demandes validées', value: 38, pct: 79, color: 'linear-gradient(90deg, #3b82f6, #10b981)' },
    { label: 'BC émis', value: 32, pct: 67, color: 'linear-gradient(90deg, #10b981, #22c55e)' },
    { label: 'Réceptions', value: 28, pct: 58, color: 'linear-gradient(90deg, #22c55e, #a3e635)' },
    { label: 'Factures payées', value: 25, pct: 52, color: 'linear-gradient(90deg, #a3e635, #fbbf24)' },
  ],
});

const achatActions = ref({
  urgentes: [
    { key: 'da', label: '8 DA en attente >3 jours', badge: 'URGENT', badgeClass: 'text-bg-danger' },
    { key: 'bc', label: '5 BC en retard de livraison', badge: 'RETARD', badgeClass: 'text-bg-warning' },
    { key: 'factures', label: '3 factures bloquées', badge: 'BLOQUÉ', badgeClass: 'text-bg-danger' },
    { key: 'ecarts', label: '2 écarts majeurs à traiter', badge: 'À FAIRE', badgeClass: 'text-bg-warning' },
  ],
  activite: [
    { label: 'Réceptions planifiées', done: 8, total: 12, color: '#3b82f6' },
    { label: 'BC à envoyer', done: 2, total: 6, color: '#8b5cf6' },
    { label: 'DA créées', done: 4, total: 4, color: '#10b981' },
    { label: 'Factures validées', done: 9, total: 15, color: '#f97316' },
  ],
  horaire: [12, 20, 35, 60, 40, 25, 55, 70, 45, 30, 22, 15],
  performance: [
    { label: 'Taux respect délais', value: '87%', icon: 'ti-trending-up', bg: 'rgba(59,130,246,0.12)', color: '#3b82f6', delta: '+2% vs obj', deltaClass: 'text-success' },
    { label: 'Taux conformité', value: '85%', icon: 'ti-circle-check', bg: 'rgba(16,185,129,0.12)', color: '#10b981', delta: '-5% vs obj', deltaClass: 'text-warning' },
    { label: 'Délai moyen paiement', value: '32j', icon: 'ti-clock', bg: 'rgba(249,115,22,0.12)', color: '#f97316', delta: '+1j vs obj', deltaClass: 'text-warning' },
    { label: 'Fournisseurs actifs', value: '45', icon: 'ti-users', bg: 'rgba(139,92,246,0.12)', color: '#8b5cf6', delta: '+3 ce mois', deltaClass: 'text-success' },
  ],
});

const achatInsights = ref({
  attention: [
    "8 demandes en attente depuis >3 jours → Risque de rupture",
    "Conformité en baisse de 3% → Auditer Fournisseur B",
    "5 commandes en retard → Relancer livraisons",
    "245K€ de factures en attente → Impact trésorerie",
  ],
  reco: [
    "Valider les 8 DA prioritaires avant vendredi",
    "Négocier délais avec Fournisseur D (3 retards)",
    "Prévoir +15% de commandes le mois prochain (tendance)",
    "Objectif conformité 90% : former équipe réception",
  ],
});

const conformiteColor = computed(() => {
  const v = achatKpis.value.conformite.valeur;
  if (v > 80) return '#10b981';
  if (v >= 60) return '#f97316';
  return '#ef4444';
});

const conformiteBg = computed(() => {
  const c = conformiteColor.value;
  const rgb = c === '#10b981' ? '16,185,129' : c === '#f97316' ? '249,115,22' : '239,68,68';
  return `rgba(${rgb},0.12)`;
});

const conformiteBadgeClass = computed(() => {
  const v = achatKpis.value.conformite.valeur;
  if (v > 80) return 'text-bg-success';
  if (v >= 60) return 'text-bg-warning';
  return 'text-bg-danger';
});

const periodeLabel = computed(() => {
  if (periode.value === 'jour') return "Aujourd'hui";
  if (periode.value === 'semaine') return 'Cette semaine';
  if (periode.value === 'mois') return 'Ce mois';
  return 'Cette année';
});

const lastUpdatedLabel = computed(() => {
  return lastUpdated.value.toLocaleString();
});

const refreshAchats = async () => {
  try {
    refreshing.value = true;
    await new Promise((r) => setTimeout(r, 700));
    lastUpdated.value = new Date();
  } finally {
    refreshing.value = false;
  }
};

const onCardClick = (key) => {
  console.log('Dashboard achats card click:', key);
};

const onActionClick = (key) => {
  console.log('Dashboard achats action click:', key);
};

onMounted(async () => {
  try {
    const { data } = await axios.get('/api/dashboard/kpis');
    kpis.value = data;
  } catch (e) {
    error.value = "Impossible de charger les KPIs";
  } finally {
    loading.value = false;
  }
});
</script>

<style scoped>
.round-40 {
  width: 40px;
  height: 40px;
}

.cursor-pointer {
  cursor: pointer;
}

.achat-section {
  background: linear-gradient(180deg, rgba(59, 130, 246, 0.12), rgba(59, 130, 246, 0.04));
  box-shadow: 0 8px 30px rgba(0, 0, 0, 0.06);
}

.achat-section-icon {
  width: 44px;
  height: 44px;
  border-radius: 14px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  color: #1d4ed8;
  background: rgba(59, 130, 246, 0.12);
  font-size: 20px;
}

.achat-section-heading {
  font-size: 22px;
  font-weight: 800;
  letter-spacing: 0.02em;
}

.achat-section-subheading {
  color: rgba(42, 53, 71, 0.7);
}

.achat-updated {
  font-size: 12px;
}

.achat-refresh {
  border: 1px solid rgba(0, 0, 0, 0.08);
}

.achat-spin {
  animation: spin 0.9s linear infinite;
}

.achat-kpi-card {
  background: #fff;
  border-radius: 16px;
  box-shadow: 0 12px 30px rgba(0, 0, 0, 0.08);
  overflow: hidden;
  display: flex;
  min-height: 130px;
}

.achat-kpi-left {
  width: 10px;
  border-left: 4px solid transparent;
}

.achat-kpi-icon {
  width: 44px;
  height: 44px;
  border-radius: 14px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  position: relative;
  font-size: 20px;
}

.achat-kpi-dot {
  position: absolute;
  top: -4px;
  right: -4px;
  width: 10px;
  height: 10px;
  border-radius: 999px;
  background: #ef4444;
  border: 2px solid #fff;
}

.achat-kpi-badge {
  position: absolute;
  top: -6px;
  right: -6px;
  min-width: 18px;
  height: 18px;
  border-radius: 999px;
  background: #ef4444;
  color: #fff;
  font-size: 11px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  padding: 0 5px;
  border: 2px solid #fff;
}

.achat-kpi-content {
  padding: 16px;
  display: flex;
  flex-direction: column;
  gap: 4px;
  flex-grow: 1;
}

.achat-kpi-label {
  font-size: 13px;
  color: rgba(42, 53, 71, 0.75);
  font-weight: 700;
}

.achat-kpi-value {
  font-size: 34px;
  font-weight: 800;
  line-height: 1.1;
  color: #111827;
}

.achat-kpi-desc {
  font-size: 13px;
  color: rgba(42, 53, 71, 0.75);
}

.achat-kpi-trend {
  font-weight: 700;
  display: inline-flex;
  align-items: center;
  gap: 6px;
}

.achat-kpi-trend-text {
  font-size: 12px;
}

.achat-kpi-hint {
  font-size: 12px;
  color: rgba(42, 53, 71, 0.6);
}

.achat-hover {
  transition: transform 180ms ease, box-shadow 180ms ease;
}

.achat-hover:hover,
.achat-hover:focus {
  transform: translateY(-2px) scale(1.01);
  box-shadow: 0 18px 38px rgba(0, 0, 0, 0.12);
  outline: none;
}

.achat-card {
  border-radius: 16px;
  box-shadow: 0 12px 30px rgba(0, 0, 0, 0.08);
}

.achat-donut {
  width: 220px;
  height: 220px;
  border-radius: 999px;
  background: conic-gradient(#10b981 0 56%, #fbbf24 56% 74%, #94a3b8 74% 92%, #ef4444 92% 100%);
  position: relative;
}

.achat-donut-center {
  position: absolute;
  inset: 34px;
  background: #fff;
  border-radius: 999px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  box-shadow: inset 0 0 0 1px rgba(0, 0, 0, 0.06);
}

.achat-donut-total {
  font-size: 28px;
  font-weight: 800;
}

.achat-legend {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.achat-legend-row {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 6px 10px;
  border-radius: 10px;
  background: rgba(255, 255, 255, 0.65);
  border: 1px solid rgba(0, 0, 0, 0.05);
}

.achat-dot {
  width: 10px;
  height: 10px;
  border-radius: 999px;
}

.achat-area {
  border-radius: 14px;
  border: 1px solid rgba(0, 0, 0, 0.06);
  background: linear-gradient(180deg, rgba(59, 130, 246, 0.12), rgba(16, 185, 129, 0.05));
  padding: 12px;
  overflow: hidden;
}

.achat-area-grid {
  height: 220px;
  display: grid;
  grid-template-columns: repeat(6, 1fr);
  gap: 10px;
  align-items: end;
}

.achat-area-bar {
  position: relative;
  height: 100%;
}

.achat-area-fill {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  height: calc(var(--h) * 1.8px);
  border-radius: 10px;
  background: rgba(59, 130, 246, 0.55);
}

.achat-area-fill2 {
  position: absolute;
  bottom: 0;
  left: 6px;
  right: 6px;
  height: calc(var(--h2) * 0.55px);
  border-radius: 10px;
  background: rgba(16, 185, 129, 0.55);
}

.achat-area-tooltip {
  position: absolute;
  bottom: 100%;
  left: 50%;
  transform: translate(-50%, -10px);
  width: 120px;
  padding: 10px;
  border-radius: 12px;
  background: rgba(17, 24, 39, 0.92);
  color: #fff;
  opacity: 0;
  pointer-events: none;
  transition: opacity 150ms ease;
}

.achat-area-bar:hover .achat-area-tooltip {
  opacity: 1;
}

.achat-mini-bars {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.achat-kpi-big {
  font-size: 28px;
  font-weight: 800;
  color: #111827;
}

.achat-stacked-bar {
  display: flex;
  border-radius: 12px;
  overflow: hidden;
  border: 1px solid rgba(0, 0, 0, 0.06);
}

.achat-stacked-seg {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  font-size: 12px;
  color: #fff;
  font-weight: 700;
  padding: 6px 0;
}

.achat-funnel {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.achat-funnel-step {
  border-radius: 14px;
  border: 1px solid rgba(0, 0, 0, 0.06);
  background: rgba(255, 255, 255, 0.6);
  padding: 10px;
}

.achat-funnel-bar {
  height: 10px;
  border-radius: 999px;
  width: calc(var(--w) * 1%);
  background: var(--c);
  margin-top: 8px;
}

.achat-suppliers {
  display: flex;
  flex-direction: column;
  gap: 14px;
}

.achat-supplier-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 14px;
  padding: 12px;
  border-radius: 14px;
  border: 1px solid rgba(0, 0, 0, 0.05);
  background: rgba(255, 255, 255, 0.6);
}

.achat-avatar {
  width: 44px;
  height: 44px;
  border-radius: 999px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  font-weight: 800;
}

.achat-supplier-bar {
  background: linear-gradient(90deg, #8b5cf6, #ec4899);
}

.achat-alert-card {
  border-radius: 18px;
  box-shadow: 0 12px 30px rgba(0, 0, 0, 0.09);
  overflow: hidden;
}

.achat-alert-urgent {
  background: linear-gradient(135deg, #ef4444, #f97316);
}

.achat-alert-activity {
  background: linear-gradient(180deg, rgba(59, 130, 246, 0.08), rgba(59, 130, 246, 0.02));
}

.achat-alert-performance {
  background: linear-gradient(180deg, rgba(16, 185, 129, 0.08), rgba(16, 185, 129, 0.02));
}

.achat-alert-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.achat-alert-item {
  padding: 10px 12px;
  border-radius: 14px;
  background: rgba(255, 255, 255, 0.15);
  border: 1px solid rgba(255, 255, 255, 0.25);
}

.achat-activity-mini {
  height: 60px;
  display: flex;
  align-items: flex-end;
  gap: 4px;
  margin-top: 8px;
}

.achat-activity-bar {
  width: 100%;
  border-radius: 8px;
  background: linear-gradient(180deg, rgba(59, 130, 246, 0.8), rgba(59, 130, 246, 0.25));
}

.achat-metric {
  padding: 12px;
  border-radius: 14px;
  border: 1px solid rgba(0, 0, 0, 0.05);
  background: rgba(255, 255, 255, 0.6);
}

.achat-metric-icon {
  width: 32px;
  height: 32px;
  border-radius: 12px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
}

.achat-insights {
  background: linear-gradient(135deg, #1e3a8a, #6d28d9);
  box-shadow: 0 16px 42px rgba(0, 0, 0, 0.18);
}

.achat-insights-icon {
  width: 44px;
  height: 44px;
  border-radius: 14px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  background: rgba(255, 255, 255, 0.14);
  font-size: 20px;
}

.achat-insights-box {
  border-radius: 16px;
  padding: 16px;
  background: rgba(255, 255, 255, 0.08);
  border: 1px solid rgba(255, 255, 255, 0.18);
}

.achat-insights-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.achat-insight {
  display: flex;
  align-items: flex-start;
  gap: 10px;
}

.achat-gauge {
  --pct: 0;
  --gauge: #10b981;
  width: 96px;
  height: 96px;
  border-radius: 999px;
  background: conic-gradient(var(--gauge) calc(var(--pct) * 1%), rgba(0,0,0,0.08) 0);
  position: relative;
  flex: 0 0 auto;
}

.achat-gauge--semi {
  width: 220px;
  height: 110px;
  border-radius: 220px 220px 0 0;
  overflow: hidden;
}

.achat-gauge--semi::before {
  content: '';
  position: absolute;
  inset: 0;
  background: conic-gradient(from 180deg, var(--gauge) calc(var(--pct) * 1%), rgba(0,0,0,0.08) 0);
}

.achat-gauge--semi .achat-gauge-inner {
  position: absolute;
  left: 50%;
  bottom: 0;
  transform: translateX(-50%);
  width: 180px;
  height: 90px;
  border-radius: 180px 180px 0 0;
  background: #fff;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  box-shadow: inset 0 0 0 1px rgba(0, 0, 0, 0.06);
}

.achat-gauge-inner {
  position: absolute;
  inset: 10px;
  background: #fff;
  border-radius: 999px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  box-shadow: inset 0 0 0 1px rgba(0, 0, 0, 0.06);
}

.achat-gauge-value {
  font-weight: 800;
}

.achat-gauge-sub {
  font-size: 12px;
  color: rgba(42, 53, 71, 0.7);
}

@keyframes spin {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}
</style>
