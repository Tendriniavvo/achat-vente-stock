# achat-vente-stock

ato

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
