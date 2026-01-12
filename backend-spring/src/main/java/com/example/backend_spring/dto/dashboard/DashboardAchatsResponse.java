package com.example.backend_spring.dto.dashboard;

import java.util.List;

public class DashboardAchatsResponse {

    private String periode;
    private String lastUpdated;

    private AchatKpis achatKpis;
    private AchatCharts achatCharts;
    private AchatActions achatActions;
    private AchatInsights achatInsights;

    public DashboardAchatsResponse() {
    }

    public String getPeriode() {
        return periode;
    }

    public void setPeriode(String periode) {
        this.periode = periode;
    }

    public String getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(String lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public AchatKpis getAchatKpis() {
        return achatKpis;
    }

    public void setAchatKpis(AchatKpis achatKpis) {
        this.achatKpis = achatKpis;
    }

    public AchatCharts getAchatCharts() {
        return achatCharts;
    }

    public void setAchatCharts(AchatCharts achatCharts) {
        this.achatCharts = achatCharts;
    }

    public AchatActions getAchatActions() {
        return achatActions;
    }

    public void setAchatActions(AchatActions achatActions) {
        this.achatActions = achatActions;
    }

    public AchatInsights getAchatInsights() {
        return achatInsights;
    }

    public void setAchatInsights(AchatInsights achatInsights) {
        this.achatInsights = achatInsights;
    }

    public static class AchatKpis {
        private DemandesAttente demandesAttente;
        private BcMois bcMois;
        private ReceptionsAttente receptionsAttente;
        private Conformite conformite;

        public AchatKpis() {
        }

        public DemandesAttente getDemandesAttente() {
            return demandesAttente;
        }

        public void setDemandesAttente(DemandesAttente demandesAttente) {
            this.demandesAttente = demandesAttente;
        }

        public BcMois getBcMois() {
            return bcMois;
        }

        public void setBcMois(BcMois bcMois) {
            this.bcMois = bcMois;
        }

        public ReceptionsAttente getReceptionsAttente() {
            return receptionsAttente;
        }

        public void setReceptionsAttente(ReceptionsAttente receptionsAttente) {
            this.receptionsAttente = receptionsAttente;
        }

        public Conformite getConformite() {
            return conformite;
        }

        public void setConformite(Conformite conformite) {
            this.conformite = conformite;
        }
    }

    public static class DemandesAttente {
        private long valeur;
        private String tendance;
        private long joursMax;

        public DemandesAttente() {
        }

        public long getValeur() {
            return valeur;
        }

        public void setValeur(long valeur) {
            this.valeur = valeur;
        }

        public String getTendance() {
            return tendance;
        }

        public void setTendance(String tendance) {
            this.tendance = tendance;
        }

        public long getJoursMax() {
            return joursMax;
        }

        public void setJoursMax(long joursMax) {
            this.joursMax = joursMax;
        }
    }

    public static class BcMois {
        private long valeur;
        private String montant;
        private String comparaison;

        public BcMois() {
        }

        public long getValeur() {
            return valeur;
        }

        public void setValeur(long valeur) {
            this.valeur = valeur;
        }

        public String getMontant() {
            return montant;
        }

        public void setMontant(String montant) {
            this.montant = montant;
        }

        public String getComparaison() {
            return comparaison;
        }

        public void setComparaison(String comparaison) {
            this.comparaison = comparaison;
        }
    }

    public static class ReceptionsAttente {
        private long valeur;
        private String description;
        private long retards;

        public ReceptionsAttente() {
        }

        public long getValeur() {
            return valeur;
        }

        public void setValeur(long valeur) {
            this.valeur = valeur;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public long getRetards() {
            return retards;
        }

        public void setRetards(long retards) {
            this.retards = retards;
        }
    }

    public static class Conformite {
        private long valeur;
        private String tendance;

        public Conformite() {
        }

        public long getValeur() {
            return valeur;
        }

        public void setValeur(long valeur) {
            this.valeur = valeur;
        }

        public String getTendance() {
            return tendance;
        }

        public void setTendance(String tendance) {
            this.tendance = tendance;
        }
    }

    public static class AchatCharts {
        private DaStatut daStatut;
        private Commandes commandes;
        private ConformiteChart conformite;
        private List<TopFournisseur> topFournisseurs;
        private Delais delais;
        private List<PipelineStep> pipeline;

        public AchatCharts() {
        }

        public DaStatut getDaStatut() {
            return daStatut;
        }

        public void setDaStatut(DaStatut daStatut) {
            this.daStatut = daStatut;
        }

        public Commandes getCommandes() {
            return commandes;
        }

        public void setCommandes(Commandes commandes) {
            this.commandes = commandes;
        }

        public ConformiteChart getConformite() {
            return conformite;
        }

        public void setConformite(ConformiteChart conformite) {
            this.conformite = conformite;
        }

        public List<TopFournisseur> getTopFournisseurs() {
            return topFournisseurs;
        }

        public void setTopFournisseurs(List<TopFournisseur> topFournisseurs) {
            this.topFournisseurs = topFournisseurs;
        }

        public Delais getDelais() {
            return delais;
        }

        public void setDelais(Delais delais) {
            this.delais = delais;
        }

        public List<PipelineStep> getPipeline() {
            return pipeline;
        }

        public void setPipeline(List<PipelineStep> pipeline) {
            this.pipeline = pipeline;
        }
    }

    public static class DaStatut {
        private long brouillon;
        private long soumise;
        private long validee;
        private long rejetee;

        public DaStatut() {
        }

        public long getBrouillon() {
            return brouillon;
        }

        public void setBrouillon(long brouillon) {
            this.brouillon = brouillon;
        }

        public long getSoumise() {
            return soumise;
        }

        public void setSoumise(long soumise) {
            this.soumise = soumise;
        }

        public long getValidee() {
            return validee;
        }

        public void setValidee(long validee) {
            this.validee = validee;
        }

        public long getRejetee() {
            return rejetee;
        }

        public void setRejetee(long rejetee) {
            this.rejetee = rejetee;
        }

        public long getTotal() {
            return brouillon + soumise + validee + rejetee;
        }
    }

    public static class Commandes {
        private String moyenne;
        private List<MoisCommande> mois;

        public Commandes() {
        }

        public String getMoyenne() {
            return moyenne;
        }

        public void setMoyenne(String moyenne) {
            this.moyenne = moyenne;
        }

        public List<MoisCommande> getMois() {
            return mois;
        }

        public void setMois(List<MoisCommande> mois) {
            this.mois = mois;
        }
    }

    public static class MoisCommande {
        private String label;
        private long nb;
        private long montantK;

        public MoisCommande() {
        }

        public MoisCommande(String label, long nb, long montantK) {
            this.label = label;
            this.nb = nb;
            this.montantK = montantK;
        }

        public String getLabel() {
            return label;
        }

        public void setLabel(String label) {
            this.label = label;
        }

        public long getNb() {
            return nb;
        }

        public void setNb(long nb) {
            this.nb = nb;
        }

        public long getMontantK() {
            return montantK;
        }

        public void setMontantK(long montantK) {
            this.montantK = montantK;
        }
    }

    public static class ConformiteChart {
        private long gauge;
        private List<ConformiteBar> bars;

        public ConformiteChart() {
        }

        public long getGauge() {
            return gauge;
        }

        public void setGauge(long gauge) {
            this.gauge = gauge;
        }

        public List<ConformiteBar> getBars() {
            return bars;
        }

        public void setBars(List<ConformiteBar> bars) {
            this.bars = bars;
        }
    }

    public static class ConformiteBar {
        private String label;
        private long value;
        private String color;

        public ConformiteBar() {
        }

        public ConformiteBar(String label, long value, String color) {
            this.label = label;
            this.value = value;
            this.color = color;
        }

        public String getLabel() {
            return label;
        }

        public void setLabel(String label) {
            this.label = label;
        }

        public long getValue() {
            return value;
        }

        public void setValue(long value) {
            this.value = value;
        }

        public String getColor() {
            return color;
        }

        public void setColor(String color) {
            this.color = color;
        }
    }

    public static class TopFournisseur {
        private String nom;
        private String initiales;
        private String avatarColor;
        private long montantK;
        private long nbCommandes;
        private long pct;

        public TopFournisseur() {
        }

        public String getNom() {
            return nom;
        }

        public void setNom(String nom) {
            this.nom = nom;
        }

        public String getInitiales() {
            return initiales;
        }

        public void setInitiales(String initiales) {
            this.initiales = initiales;
        }

        public String getAvatarColor() {
            return avatarColor;
        }

        public void setAvatarColor(String avatarColor) {
            this.avatarColor = avatarColor;
        }

        public long getMontantK() {
            return montantK;
        }

        public void setMontantK(long montantK) {
            this.montantK = montantK;
        }

        public long getNbCommandes() {
            return nbCommandes;
        }

        public void setNbCommandes(long nbCommandes) {
            this.nbCommandes = nbCommandes;
        }

        public long getPct() {
            return pct;
        }

        public void setPct(long pct) {
            this.pct = pct;
        }
    }

    public static class Delais {
        private long kpiOnTime;
        private List<DelaiRow> rows;

        public Delais() {
        }

        public long getKpiOnTime() {
            return kpiOnTime;
        }

        public void setKpiOnTime(long kpiOnTime) {
            this.kpiOnTime = kpiOnTime;
        }

        public List<DelaiRow> getRows() {
            return rows;
        }

        public void setRows(List<DelaiRow> rows) {
            this.rows = rows;
        }
    }

    public static class DelaiRow {
        private String label;
        private long avance;
        private long atemps;
        private long retard;

        public DelaiRow() {
        }

        public DelaiRow(String label, long avance, long atemps, long retard) {
            this.label = label;
            this.avance = avance;
            this.atemps = atemps;
            this.retard = retard;
        }

        public String getLabel() {
            return label;
        }

        public void setLabel(String label) {
            this.label = label;
        }

        public long getAvance() {
            return avance;
        }

        public void setAvance(long avance) {
            this.avance = avance;
        }

        public long getAtemps() {
            return atemps;
        }

        public void setAtemps(long atemps) {
            this.atemps = atemps;
        }

        public long getRetard() {
            return retard;
        }

        public void setRetard(long retard) {
            this.retard = retard;
        }
    }

    public static class PipelineStep {
        private String label;
        private long value;
        private long pct;
        private String color;

        public PipelineStep() {
        }

        public PipelineStep(String label, long value, long pct, String color) {
            this.label = label;
            this.value = value;
            this.pct = pct;
            this.color = color;
        }

        public String getLabel() {
            return label;
        }

        public void setLabel(String label) {
            this.label = label;
        }

        public long getValue() {
            return value;
        }

        public void setValue(long value) {
            this.value = value;
        }

        public long getPct() {
            return pct;
        }

        public void setPct(long pct) {
            this.pct = pct;
        }

        public String getColor() {
            return color;
        }

        public void setColor(String color) {
            this.color = color;
        }
    }

    public static class AchatActions {
        private List<ActionItem> urgentes;
        private List<ActiviteItem> activite;
        private List<Long> horaire;
        private List<PerformanceMetric> performance;

        public AchatActions() {
        }

        public List<ActionItem> getUrgentes() {
            return urgentes;
        }

        public void setUrgentes(List<ActionItem> urgentes) {
            this.urgentes = urgentes;
        }

        public List<ActiviteItem> getActivite() {
            return activite;
        }

        public void setActivite(List<ActiviteItem> activite) {
            this.activite = activite;
        }

        public List<Long> getHoraire() {
            return horaire;
        }

        public void setHoraire(List<Long> horaire) {
            this.horaire = horaire;
        }

        public List<PerformanceMetric> getPerformance() {
            return performance;
        }

        public void setPerformance(List<PerformanceMetric> performance) {
            this.performance = performance;
        }
    }

    public static class ActionItem {
        private String key;
        private String label;
        private String badge;
        private String badgeClass;

        public ActionItem() {
        }

        public ActionItem(String key, String label, String badge, String badgeClass) {
            this.key = key;
            this.label = label;
            this.badge = badge;
            this.badgeClass = badgeClass;
        }

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public String getLabel() {
            return label;
        }

        public void setLabel(String label) {
            this.label = label;
        }

        public String getBadge() {
            return badge;
        }

        public void setBadge(String badge) {
            this.badge = badge;
        }

        public String getBadgeClass() {
            return badgeClass;
        }

        public void setBadgeClass(String badgeClass) {
            this.badgeClass = badgeClass;
        }
    }

    public static class ActiviteItem {
        private String label;
        private long done;
        private long total;
        private String color;

        public ActiviteItem() {
        }

        public ActiviteItem(String label, long done, long total, String color) {
            this.label = label;
            this.done = done;
            this.total = total;
            this.color = color;
        }

        public String getLabel() {
            return label;
        }

        public void setLabel(String label) {
            this.label = label;
        }

        public long getDone() {
            return done;
        }

        public void setDone(long done) {
            this.done = done;
        }

        public long getTotal() {
            return total;
        }

        public void setTotal(long total) {
            this.total = total;
        }

        public String getColor() {
            return color;
        }

        public void setColor(String color) {
            this.color = color;
        }
    }

    public static class PerformanceMetric {
        private String label;
        private String value;
        private String icon;
        private String bg;
        private String color;
        private String delta;
        private String deltaClass;

        public PerformanceMetric() {
        }

        public PerformanceMetric(String label, String value, String icon, String bg, String color, String delta, String deltaClass) {
            this.label = label;
            this.value = value;
            this.icon = icon;
            this.bg = bg;
            this.color = color;
            this.delta = delta;
            this.deltaClass = deltaClass;
        }

        public String getLabel() {
            return label;
        }

        public void setLabel(String label) {
            this.label = label;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public String getIcon() {
            return icon;
        }

        public void setIcon(String icon) {
            this.icon = icon;
        }

        public String getBg() {
            return bg;
        }

        public void setBg(String bg) {
            this.bg = bg;
        }

        public String getColor() {
            return color;
        }

        public void setColor(String color) {
            this.color = color;
        }

        public String getDelta() {
            return delta;
        }

        public void setDelta(String delta) {
            this.delta = delta;
        }

        public String getDeltaClass() {
            return deltaClass;
        }

        public void setDeltaClass(String deltaClass) {
            this.deltaClass = deltaClass;
        }
    }

    public static class AchatInsights {
        private List<String> attention;
        private List<String> reco;

        public AchatInsights() {
        }

        public List<String> getAttention() {
            return attention;
        }

        public void setAttention(List<String> attention) {
            this.attention = attention;
        }

        public List<String> getReco() {
            return reco;
        }

        public void setReco(List<String> reco) {
            this.reco = reco;
        }
    }
}
