package com.example.backend_spring.service.dashboard;

import com.example.backend_spring.dto.dashboard.DashboardKpisResponse;
import com.example.backend_spring.model.ValorisationStock;
import com.example.backend_spring.repository.achat.BonCommandeFournisseurRepository;
import com.example.backend_spring.repository.achat.FactureFournisseurRepository;
import com.example.backend_spring.repository.inventaire.ValorisationStockRepository;
import com.example.backend_spring.repository.stock.MouvementStockRepository;
import com.example.backend_spring.repository.stock.StockRepository;
import com.example.backend_spring.repository.vente.CommandeClientRepository;
import com.example.backend_spring.repository.vente.FactureClientRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class DashboardService {

    private final ValorisationStockRepository valorisationStockRepository;
    private final StockRepository stockRepository;
    private final BonCommandeFournisseurRepository bonCommandeFournisseurRepository;
    private final CommandeClientRepository commandeClientRepository;
    private final MouvementStockRepository mouvementStockRepository;
    private final FactureFournisseurRepository factureFournisseurRepository;
    private final FactureClientRepository factureClientRepository;

    public DashboardService(ValorisationStockRepository valorisationStockRepository,
            StockRepository stockRepository,
            BonCommandeFournisseurRepository bonCommandeFournisseurRepository,
            CommandeClientRepository commandeClientRepository,
            MouvementStockRepository mouvementStockRepository,
            FactureFournisseurRepository factureFournisseurRepository,
            FactureClientRepository factureClientRepository) {
        this.valorisationStockRepository = valorisationStockRepository;
        this.stockRepository = stockRepository;
        this.bonCommandeFournisseurRepository = bonCommandeFournisseurRepository;
        this.commandeClientRepository = commandeClientRepository;
        this.mouvementStockRepository = mouvementStockRepository;
        this.factureFournisseurRepository = factureFournisseurRepository;
        this.factureClientRepository = factureClientRepository;
    }

    public DashboardKpisResponse getKpis() {
        BigDecimal valeurTotaleStock = getValeurTotaleStock();
        Long commandesEnCours = getNombreCommandesEnCours();
        BigDecimal tauxRotation = getTauxRotationStocks();

        long totalFacturesFournisseur = factureFournisseurRepository.count();
        long payeesFournisseur = factureFournisseurRepository.countByStatut("payee");

        long totalFacturesClient = factureClientRepository.count();
        long payeesClient = factureClientRepository.countByStatut("payee");

        long facturesPayees = payeesFournisseur + payeesClient;
        long facturesEnAttente = (totalFacturesFournisseur - payeesFournisseur) + (totalFacturesClient - payeesClient);

        return new DashboardKpisResponse(
                valeurTotaleStock,
                commandesEnCours,
                tauxRotation,
                facturesEnAttente,
                facturesPayees);
    }

    private BigDecimal getValeurTotaleStock() {
        Optional<ValorisationStock> lastValorisation = valorisationStockRepository.findTopByOrderByDateValorisationDesc();
        if (lastValorisation.isPresent() && lastValorisation.get().getValeurTotale() != null) {
            return lastValorisation.get().getValeurTotale();
        }

        BigDecimal sumStocks = stockRepository.sumValeurTotale();
        return sumStocks != null ? sumStocks : BigDecimal.ZERO;
    }

    private Long getNombreCommandesEnCours() {
        long achatsEnCours = bonCommandeFournisseurRepository.countEnCours();
        long ventesEnCours = commandeClientRepository.countEnCours();
        return achatsEnCours + ventesEnCours;
    }

    private BigDecimal getTauxRotationStocks() {
        LocalDateTime dateFin = LocalDateTime.now();
        LocalDateTime dateDebut = dateFin.minusDays(30);

        long mouvements30j = mouvementStockRepository.countByDateMouvementBetween(dateDebut, dateFin);
        double stockMoyen = stockRepository.avgQuantite();

        if (stockMoyen <= 0d) {
            return BigDecimal.ZERO;
        }

        return BigDecimal.valueOf(mouvements30j)
                .divide(BigDecimal.valueOf(stockMoyen), 2, RoundingMode.HALF_UP);
    }
}
