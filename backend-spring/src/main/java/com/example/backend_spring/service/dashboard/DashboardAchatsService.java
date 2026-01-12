package com.example.backend_spring.service.dashboard;

import com.example.backend_spring.dto.dashboard.DashboardAchatsResponse;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Service
public class DashboardAchatsService {

    @PersistenceContext
    private EntityManager em;

    public DashboardAchatsResponse getAchats(String periode) {
        String p = (periode == null || periode.isBlank()) ? "mois" : periode;

        DateRange current = computeRange(p, LocalDateTime.now());
        DateRange previous = computePreviousRange(current);

        DashboardAchatsResponse res = new DashboardAchatsResponse();
        res.setPeriode(p);
        res.setLastUpdated(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));

        res.setAchatKpis(buildKpis(current, previous));
        res.setAchatCharts(buildCharts(current));
        res.setAchatActions(buildActions(current));
        res.setAchatInsights(buildInsights(current));

        return res;
    }

    private DashboardAchatsResponse.AchatKpis buildKpis(DateRange current, DateRange previous) {
        DashboardAchatsResponse.AchatKpis k = new DashboardAchatsResponse.AchatKpis();

        long daSoumises = singleLong(
                "SELECT COUNT(*) FROM demandes_achat d WHERE d.statut = 'soumise' AND d.date_creation BETWEEN :debut AND :fin",
                current);
        long joursMax = singleLong(
                "SELECT COALESCE(MAX(EXTRACT(DAY FROM (CURRENT_TIMESTAMP - d.date_creation))), 0) " +
                        "FROM demandes_achat d WHERE d.statut = 'soumise'",
                current);

        long daSoumisesPrev = singleLong(
                "SELECT COUNT(*) FROM demandes_achat d WHERE d.statut = 'soumise' AND d.date_creation BETWEEN :debut AND :fin",
                previous);
        long deltaDa = daSoumises - daSoumisesPrev;

        DashboardAchatsResponse.DemandesAttente da = new DashboardAchatsResponse.DemandesAttente();
        da.setValeur(daSoumises);
        if (deltaDa > 0) {
            da.setTendance(deltaDa + " nouvelles sur la période");
        } else if (deltaDa < 0) {
            da.setTendance(Math.abs(deltaDa) + " de moins sur la période");
        } else {
            da.setTendance("stable sur la période");
        }
        da.setJoursMax(joursMax);
        k.setDemandesAttente(da);

        long bcCount = singleLong(
                "SELECT COUNT(*) FROM bons_commande_fournisseur b WHERE b.date_commande BETWEEN :debut AND :fin",
                current);
        BigDecimal bcMontant = singleBigDecimal(
                "SELECT COALESCE(SUM(b.montant_total), 0) FROM bons_commande_fournisseur b WHERE b.date_commande BETWEEN :debut AND :fin",
                current);

        long bcCountPrev = singleLong(
                "SELECT COUNT(*) FROM bons_commande_fournisseur b WHERE b.date_commande BETWEEN :debut AND :fin",
                previous);
        String comparaison = pctDeltaLabel(bcCountPrev, bcCount);

        DashboardAchatsResponse.BcMois bc = new DashboardAchatsResponse.BcMois();
        bc.setValeur(bcCount);
        bc.setMontant(formatMoneyShort(bcMontant));
        bc.setComparaison(comparaison);
        k.setBcMois(bc);

        long receptionsAttente = singleLong(
                "SELECT COUNT(*) FROM receptions r WHERE r.statut IN ('partielle','litige') AND r.date_reception BETWEEN :debut AND :fin",
                current);

        long bcRetard = singleLong(
                "SELECT COUNT(*) FROM bons_commande_fournisseur b " +
                        "WHERE b.date_livraison_prevue IS NOT NULL " +
                        "AND b.date_livraison_prevue < CURRENT_TIMESTAMP " +
                        "AND b.statut NOT IN ('recu','annule')",
                current);

        DashboardAchatsResponse.ReceptionsAttente ra = new DashboardAchatsResponse.ReceptionsAttente();
        ra.setValeur(receptionsAttente);
        ra.setRetards(bcRetard);
        ra.setDescription("Réceptions part./litige sur la période");
        k.setReceptionsAttente(ra);

        ConformiteResult conf = computeConformite(current);
        long confPrev = computeConformite(previous).pct;
        long confDelta = conf.pct - confPrev;

        DashboardAchatsResponse.Conformite c = new DashboardAchatsResponse.Conformite();
        c.setValeur(conf.pct);
        if (confDelta > 0) {
            c.setTendance("+" + confDelta + "% vs période précédente");
        } else if (confDelta < 0) {
            c.setTendance(confDelta + "% vs période précédente");
        } else {
            c.setTendance("stable vs période précédente");
        }
        k.setConformite(c);

        return k;
    }

    private DashboardAchatsResponse.AchatCharts buildCharts(DateRange current) {
        DashboardAchatsResponse.AchatCharts c = new DashboardAchatsResponse.AchatCharts();

        DashboardAchatsResponse.DaStatut da = new DashboardAchatsResponse.DaStatut();
        da.setBrouillon(singleLong("SELECT COUNT(*) FROM demandes_achat d WHERE d.statut = 'brouillon' AND d.date_creation BETWEEN :debut AND :fin", current));
        da.setSoumise(singleLong("SELECT COUNT(*) FROM demandes_achat d WHERE d.statut = 'soumise' AND d.date_creation BETWEEN :debut AND :fin", current));
        da.setValidee(singleLong("SELECT COUNT(*) FROM demandes_achat d WHERE d.statut = 'validee' AND d.date_creation BETWEEN :debut AND :fin", current));
        da.setRejetee(singleLong("SELECT COUNT(*) FROM demandes_achat d WHERE d.statut = 'rejetee' AND d.date_creation BETWEEN :debut AND :fin", current));
        c.setDaStatut(da);

        DashboardAchatsResponse.Commandes commandes = new DashboardAchatsResponse.Commandes();
        List<DashboardAchatsResponse.MoisCommande> mois = buildSixMonthsBc();
        commandes.setMois(mois);
        commandes.setMoyenne(computeAverageLabel(mois));
        c.setCommandes(commandes);

        ConformiteResult conf = computeConformite(current);
        DashboardAchatsResponse.ConformiteChart confChart = new DashboardAchatsResponse.ConformiteChart();
        confChart.setGauge(conf.pct);
        List<DashboardAchatsResponse.ConformiteBar> bars = new ArrayList<>();
        bars.add(new DashboardAchatsResponse.ConformiteBar("Conforme", conf.pct, "#10b981"));
        bars.add(new DashboardAchatsResponse.ConformiteBar("Écarts", 100 - conf.pct, "#ef4444"));
        confChart.setBars(bars);
        c.setConformite(confChart);

        c.setTopFournisseurs(buildTopFournisseurs(current));
        c.setDelais(buildDelais(current));
        c.setPipeline(buildPipeline(current));

        return c;
    }

    private DashboardAchatsResponse.AchatActions buildActions(DateRange current) {
        DashboardAchatsResponse.AchatActions a = new DashboardAchatsResponse.AchatActions();

        long daEnAttente = singleLong("SELECT COUNT(*) FROM demandes_achat d WHERE d.statut='soumise'", current);
        long bcRetard = singleLong(
                "SELECT COUNT(*) FROM bons_commande_fournisseur b " +
                        "WHERE b.date_livraison_prevue IS NOT NULL " +
                        "AND b.date_livraison_prevue < CURRENT_TIMESTAMP " +
                        "AND b.statut NOT IN ('recu','annule')",
                current);
        long facturesBloquees = singleLong("SELECT COUNT(*) FROM factures_fournisseur f WHERE f.statut='bloquee'", current);
        long ecarts = singleLong(
                "SELECT COUNT(*) FROM lignes_receptions lr WHERE lr.ecart IS NOT NULL AND TRIM(lr.ecart) <> ''",
                current);

        List<DashboardAchatsResponse.ActionItem> urg = new ArrayList<>();
        urg.add(new DashboardAchatsResponse.ActionItem("da", daEnAttente + " DA en attente", daEnAttente > 0 ? "URGENT" : "OK", daEnAttente > 0 ? "text-bg-danger" : "text-bg-success"));
        urg.add(new DashboardAchatsResponse.ActionItem("bc", bcRetard + " BC en retard", bcRetard > 0 ? "RETARD" : "OK", bcRetard > 0 ? "text-bg-warning" : "text-bg-success"));
        urg.add(new DashboardAchatsResponse.ActionItem("factures", facturesBloquees + " factures bloquées", facturesBloquees > 0 ? "BLOQUÉ" : "OK", facturesBloquees > 0 ? "text-bg-danger" : "text-bg-success"));
        urg.add(new DashboardAchatsResponse.ActionItem("ecarts", ecarts + " écarts réception", ecarts > 0 ? "À FAIRE" : "OK", ecarts > 0 ? "text-bg-warning" : "text-bg-success"));
        a.setUrgentes(urg);

        List<DashboardAchatsResponse.ActiviteItem> activite = new ArrayList<>();
        long recToday = singleLong("SELECT COUNT(*) FROM receptions r WHERE r.date_reception::date = CURRENT_DATE", current);
        long bcToday = singleLong("SELECT COUNT(*) FROM bons_commande_fournisseur b WHERE b.date_commande::date = CURRENT_DATE", current);
        long daToday = singleLong("SELECT COUNT(*) FROM demandes_achat d WHERE d.date_creation::date = CURRENT_DATE", current);
        long facToday = singleLong("SELECT COUNT(*) FROM factures_fournisseur f WHERE f.date_facture::date = CURRENT_DATE", current);

        activite.add(new DashboardAchatsResponse.ActiviteItem("Réceptions du jour", recToday, Math.max(recToday, 1), "#3b82f6"));
        activite.add(new DashboardAchatsResponse.ActiviteItem("BC créés aujourd'hui", bcToday, Math.max(bcToday, 1), "#8b5cf6"));
        activite.add(new DashboardAchatsResponse.ActiviteItem("DA créées aujourd'hui", daToday, Math.max(daToday, 1), "#10b981"));
        activite.add(new DashboardAchatsResponse.ActiviteItem("Factures aujourd'hui", facToday, Math.max(facToday, 1), "#f97316"));
        a.setActivite(activite);

        List<Long> horaire = new ArrayList<>();
        for (int i = 0; i < 12; i++) {
            horaire.add(10L);
        }
        a.setHoraire(horaire);

        List<DashboardAchatsResponse.PerformanceMetric> perf = new ArrayList<>();
        DashboardAchatsResponse.Delais delais = buildDelais(current);
        perf.add(new DashboardAchatsResponse.PerformanceMetric("Taux respect délais", delais.getKpiOnTime() + "%", "ti-trending-up", "rgba(59,130,246,0.12)", "#3b82f6", "-", "text-muted"));
        ConformiteResult conf = computeConformite(current);
        perf.add(new DashboardAchatsResponse.PerformanceMetric("Taux conformité", conf.pct + "%", "ti-circle-check", "rgba(16,185,129,0.12)", "#10b981", "-", "text-muted"));
        perf.add(new DashboardAchatsResponse.PerformanceMetric("Factures en attente", singleLong("SELECT COUNT(*) FROM factures_fournisseur f WHERE f.statut <> 'payee'", current) + "", "ti-file-invoice", "rgba(249,115,22,0.12)", "#f97316", "-", "text-muted"));
        perf.add(new DashboardAchatsResponse.PerformanceMetric("Fournisseurs actifs", singleLong("SELECT COUNT(*) FROM fournisseurs f WHERE f.actif = true", current) + "", "ti-users", "rgba(139,92,246,0.12)", "#8b5cf6", "-", "text-muted"));
        a.setPerformance(perf);

        return a;
    }

    private DashboardAchatsResponse.AchatInsights buildInsights(DateRange current) {
        DashboardAchatsResponse.AchatInsights i = new DashboardAchatsResponse.AchatInsights();

        List<String> attention = new ArrayList<>();
        long daEnAttente = singleLong("SELECT COUNT(*) FROM demandes_achat d WHERE d.statut='soumise'", current);
        long bcRetard = singleLong(
                "SELECT COUNT(*) FROM bons_commande_fournisseur b " +
                        "WHERE b.date_livraison_prevue IS NOT NULL " +
                        "AND b.date_livraison_prevue < CURRENT_TIMESTAMP " +
                        "AND b.statut NOT IN ('recu','annule')",
                current);
        BigDecimal factMontantAtt = singleBigDecimal(
                "SELECT COALESCE(SUM(f.montant_total),0) FROM factures_fournisseur f WHERE f.statut <> 'payee'",
                current);

        attention.add(daEnAttente + " demandes en attente → risque de blocage validation");
        attention.add(bcRetard + " commandes en retard → relance fournisseurs");
        attention.add("Factures non payées (montant): " + formatMoneyShort(factMontantAtt));

        List<String> reco = new ArrayList<>();
        reco.add("Prioriser la validation des DA 'soumise'");
        reco.add("Traiter les BC en retard (date_livraison_prevue dépassée)");
        reco.add("Analyser les écarts de réception (lignes_receptions.ecart)");

        i.setAttention(attention);
        i.setReco(reco);
        return i;
    }

    private DashboardAchatsResponse.Delais buildDelais(DateRange current) {
        // On calcule sur les réceptions de la période en comparant à date_livraison_prevue du BC.
        // Hypothèse: une réception est "à temps" si date_reception <= date_livraison_prevue.
        // Si date_livraison_prevue est NULL => on exclut du calcul.

        Object[] row = (Object[]) em.createNativeQuery(
                        "SELECT " +
                                "SUM(CASE WHEN r.date_reception < b.date_livraison_prevue THEN 1 ELSE 0 END) AS avance, " +
                                "SUM(CASE WHEN r.date_reception = b.date_livraison_prevue THEN 1 ELSE 0 END) AS atemps_exact, " +
                                "SUM(CASE WHEN r.date_reception > b.date_livraison_prevue THEN 1 ELSE 0 END) AS retard, " +
                                "COUNT(*) AS total " +
                                "FROM receptions r " +
                                "JOIN bons_commande_fournisseur b ON b.id = r.bon_commande_id " +
                                "WHERE b.date_livraison_prevue IS NOT NULL " +
                                "AND r.date_reception BETWEEN :debut AND :fin")
                .setParameter("debut", current.debut)
                .setParameter("fin", current.fin)
                .getSingleResult();

        long avance = ((Number) row[0]).longValue();
        long atempsExact = ((Number) row[1]).longValue();
        long retard = ((Number) row[2]).longValue();
        long total = ((Number) row[3]).longValue();

        long atemps = avance + atempsExact;

        DashboardAchatsResponse.Delais del = new DashboardAchatsResponse.Delais();
        if (total <= 0) {
            del.setKpiOnTime(0);
            List<DashboardAchatsResponse.DelaiRow> rows = new ArrayList<>();
            rows.add(new DashboardAchatsResponse.DelaiRow("Période", 0, 0, 0));
            del.setRows(rows);
            return del;
        }

        long pctOnTime = BigDecimal.valueOf(atemps * 100.0 / total).setScale(0, RoundingMode.HALF_UP).longValue();
        del.setKpiOnTime(pctOnTime);

        long pctAvance = BigDecimal.valueOf(avance * 100.0 / total).setScale(0, RoundingMode.HALF_UP).longValue();
        long pctAtemps = BigDecimal.valueOf(atemps * 100.0 / total).setScale(0, RoundingMode.HALF_UP).longValue();
        long pctRetard = Math.max(0, 100 - pctAtemps);

        List<DashboardAchatsResponse.DelaiRow> rows = new ArrayList<>();
        rows.add(new DashboardAchatsResponse.DelaiRow("Période", pctAvance, pctAtemps, pctRetard));
        del.setRows(rows);
        return del;
    }

    private List<DashboardAchatsResponse.PipelineStep> buildPipeline(DateRange current) {
        long daCrees = singleLong("SELECT COUNT(*) FROM demandes_achat d WHERE d.date_creation BETWEEN :debut AND :fin", current);
        long daValidees = singleLong("SELECT COUNT(*) FROM demandes_achat d WHERE d.statut='validee' AND d.date_creation BETWEEN :debut AND :fin", current);
        long bcEmis = singleLong("SELECT COUNT(*) FROM bons_commande_fournisseur b WHERE b.statut IN ('valide','envoye') AND b.date_commande BETWEEN :debut AND :fin", current);
        long rec = singleLong("SELECT COUNT(*) FROM receptions r WHERE r.date_reception BETWEEN :debut AND :fin", current);
        long factPayees = singleLong("SELECT COUNT(*) FROM factures_fournisseur f WHERE f.statut='payee' AND f.date_facture BETWEEN :debut AND :fin", current);

        List<DashboardAchatsResponse.PipelineStep> steps = new ArrayList<>();
        steps.add(new DashboardAchatsResponse.PipelineStep("Demandes créées", daCrees, 100, "linear-gradient(90deg, #3b82f6, #60a5fa)"));
        steps.add(new DashboardAchatsResponse.PipelineStep("Demandes validées", daValidees, pct(daCrees, daValidees), "linear-gradient(90deg, #3b82f6, #10b981)"));
        steps.add(new DashboardAchatsResponse.PipelineStep("BC émis", bcEmis, pct(daCrees, bcEmis), "linear-gradient(90deg, #10b981, #22c55e)"));
        steps.add(new DashboardAchatsResponse.PipelineStep("Réceptions", rec, pct(daCrees, rec), "linear-gradient(90deg, #22c55e, #a3e635)"));
        steps.add(new DashboardAchatsResponse.PipelineStep("Factures payées", factPayees, pct(daCrees, factPayees), "linear-gradient(90deg, #a3e635, #fbbf24)"));
        return steps;
    }

    private List<DashboardAchatsResponse.TopFournisseur> buildTopFournisseurs(DateRange current) {
        @SuppressWarnings("unchecked")
        List<Object[]> rows = em.createNativeQuery(
                        "SELECT f.nom, COUNT(b.id) AS nb, COALESCE(SUM(b.montant_total),0) AS montant " +
                                "FROM bons_commande_fournisseur b " +
                                "JOIN fournisseurs f ON f.id = b.fournisseur_id " +
                                "WHERE b.date_commande BETWEEN :debut AND :fin " +
                                "GROUP BY f.nom " +
                                "ORDER BY montant DESC " +
                                "LIMIT 5")
                .setParameter("debut", current.debut)
                .setParameter("fin", current.fin)
                .getResultList();

        List<DashboardAchatsResponse.TopFournisseur> list = new ArrayList<>();
        BigDecimal max = BigDecimal.ZERO;
        for (Object[] r : rows) {
            BigDecimal montant = (r[2] instanceof BigDecimal) ? (BigDecimal) r[2] : new BigDecimal(String.valueOf(r[2]));
            if (montant.compareTo(max) > 0) {
                max = montant;
            }
        }

        for (Object[] r : rows) {
            String nom = String.valueOf(r[0]);
            long nb = ((Number) r[1]).longValue();
            BigDecimal montant = (r[2] instanceof BigDecimal) ? (BigDecimal) r[2] : new BigDecimal(String.valueOf(r[2]));

            DashboardAchatsResponse.TopFournisseur tf = new DashboardAchatsResponse.TopFournisseur();
            tf.setNom(nom);
            tf.setNbCommandes(nb);
            tf.setMontantK(montant.divide(BigDecimal.valueOf(1000), 0, RoundingMode.HALF_UP).longValue());
            tf.setInitiales(initiales(nom));
            tf.setAvatarColor("linear-gradient(135deg, #3b82f6, #10b981)");
            tf.setPct(max.compareTo(BigDecimal.ZERO) == 0 ? 0 : montant.multiply(BigDecimal.valueOf(100)).divide(max, 0, RoundingMode.HALF_UP).longValue());
            list.add(tf);
        }
        return list;
    }

    private List<DashboardAchatsResponse.MoisCommande> buildSixMonthsBc() {
        List<DashboardAchatsResponse.MoisCommande> out = new ArrayList<>();
        LocalDate now = LocalDate.now();

        for (int i = 5; i >= 0; i--) {
            LocalDate d = now.minusMonths(i).withDayOfMonth(1);
            LocalDateTime start = d.atStartOfDay();
            LocalDateTime end = d.plusMonths(1).atStartOfDay().minusNanos(1);

            long nb = singleLong("SELECT COUNT(*) FROM bons_commande_fournisseur b WHERE b.date_commande BETWEEN :debut AND :fin",
                    new DateRange(start, end));
            BigDecimal montant = singleBigDecimal(
                    "SELECT COALESCE(SUM(b.montant_total),0) FROM bons_commande_fournisseur b WHERE b.date_commande BETWEEN :debut AND :fin",
                    new DateRange(start, end));
            long montantK = montant.divide(BigDecimal.valueOf(1000), 0, RoundingMode.HALF_UP).longValue();

            String label = d.getMonth().getDisplayName(java.time.format.TextStyle.SHORT, Locale.FRENCH);
            label = label.substring(0, 1).toUpperCase(Locale.FRENCH) + label.substring(1);
            out.add(new DashboardAchatsResponse.MoisCommande(label, nb, montantK));
        }

        return out;
    }

    private String computeAverageLabel(List<DashboardAchatsResponse.MoisCommande> mois) {
        if (mois == null || mois.isEmpty()) {
            return "0 commandes";
        }
        long sum = 0;
        for (DashboardAchatsResponse.MoisCommande m : mois) {
            sum += m.getNb();
        }
        long avg = BigDecimal.valueOf(sum).divide(BigDecimal.valueOf(mois.size()), 0, RoundingMode.HALF_UP).longValue();
        return avg + " commandes";
    }

    private ConformiteResult computeConformite(DateRange current) {
        Object[] row = (Object[]) em.createNativeQuery(
                        "SELECT " +
                                "COUNT(lr.id) AS total, " +
                                "SUM(CASE WHEN lr.ecart IS NOT NULL AND TRIM(lr.ecart) <> '' THEN 1 ELSE 0 END) AS ecarts " +
                                "FROM lignes_receptions lr " +
                                "JOIN receptions r ON r.id = lr.reception_id " +
                                "WHERE r.date_reception BETWEEN :debut AND :fin")
                .setParameter("debut", current.debut)
                .setParameter("fin", current.fin)
                .getSingleResult();

        long total = ((Number) row[0]).longValue();
        long ecarts = row[1] == null ? 0L : ((Number) row[1]).longValue();
        if (total <= 0) {
            return new ConformiteResult(0, total, ecarts);
        }
        long conforme = Math.max(0, total - ecarts);
        long pct = BigDecimal.valueOf(conforme * 100.0 / total).setScale(0, RoundingMode.HALF_UP).longValue();
        return new ConformiteResult(pct, total, ecarts);
    }

    private long pct(long base, long value) {
        if (base <= 0) {
            return value > 0 ? 100 : 0;
        }
        return BigDecimal.valueOf(value * 100.0 / base).setScale(0, RoundingMode.HALF_UP).longValue();
    }

    private long singleLong(String sql, DateRange range) {
        Object o = em.createNativeQuery(sql)
                .setParameter("debut", range.debut)
                .setParameter("fin", range.fin)
                .getSingleResult();
        if (o == null) {
            return 0L;
        }
        return ((Number) o).longValue();
    }

    private BigDecimal singleBigDecimal(String sql, DateRange range) {
        Object o = em.createNativeQuery(sql)
                .setParameter("debut", range.debut)
                .setParameter("fin", range.fin)
                .getSingleResult();
        if (o == null) {
            return BigDecimal.ZERO;
        }
        if (o instanceof BigDecimal) {
            return (BigDecimal) o;
        }
        return new BigDecimal(String.valueOf(o));
    }

    private DateRange computeRange(String periode, LocalDateTime now) {
        LocalDateTime debut;
        LocalDateTime fin = now;

        if ("jour".equalsIgnoreCase(periode)) {
            debut = now.toLocalDate().atStartOfDay();
        } else if ("semaine".equalsIgnoreCase(periode)) {
            LocalDate d = now.toLocalDate();
            while (d.getDayOfWeek() != DayOfWeek.MONDAY) {
                d = d.minusDays(1);
            }
            debut = d.atStartOfDay();
        } else if ("annee".equalsIgnoreCase(periode)) {
            debut = LocalDate.of(now.getYear(), 1, 1).atStartOfDay();
        } else {
            // mois par défaut
            debut = LocalDate.of(now.getYear(), now.getMonth(), 1).atStartOfDay();
        }

        return new DateRange(debut, fin);
    }

    private DateRange computePreviousRange(DateRange current) {
        LocalDateTime debut = current.debut;
        LocalDateTime fin = current.fin;
        long seconds = java.time.Duration.between(debut, fin).getSeconds();
        if (seconds <= 0) {
            return new DateRange(debut.minusDays(1), debut);
        }
        LocalDateTime prevFin = debut;
        LocalDateTime prevDebut = prevFin.minusSeconds(seconds);
        return new DateRange(prevDebut, prevFin);
    }

    private String pctDeltaLabel(long prev, long now) {
        if (prev <= 0) {
            return now > 0 ? "+100%" : "0%";
        }
        BigDecimal delta = BigDecimal.valueOf(now - prev)
                .multiply(BigDecimal.valueOf(100))
                .divide(BigDecimal.valueOf(prev), 0, RoundingMode.HALF_UP);
        long d = delta.longValue();
        return (d >= 0 ? "+" + d : String.valueOf(d)) + "%";
    }

    private String formatMoneyShort(BigDecimal amount) {
        if (amount == null) {
            return "0";
        }
        BigDecimal k = amount.divide(BigDecimal.valueOf(1000), 0, RoundingMode.HALF_UP);
        return "€ " + k.toPlainString() + "K";
    }

    private String initiales(String nom) {
        if (nom == null || nom.isBlank()) {
            return "";
        }
        String[] parts = nom.trim().split("\\s+");
        StringBuilder sb = new StringBuilder();
        for (String p : parts) {
            if (!p.isBlank()) {
                sb.append(Character.toUpperCase(p.charAt(0)));
            }
            if (sb.length() == 2) {
                break;
            }
        }
        return sb.toString();
    }

    private static class DateRange {
        final LocalDateTime debut;
        final LocalDateTime fin;

        DateRange(LocalDateTime debut, LocalDateTime fin) {
            this.debut = debut;
            this.fin = fin;
        }
    }

    private static class ConformiteResult {
        final long pct;
        final long total;
        final long ecarts;

        ConformiteResult(long pct, long total, long ecarts) {
            this.pct = pct;
            this.total = total;
            this.ecarts = ecarts;
        }
    }
}
