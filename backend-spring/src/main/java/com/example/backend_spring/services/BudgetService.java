package com.example.backend_spring.services;

import com.example.backend_spring.models.Budget;
import com.example.backend_spring.models.Departement;
import com.example.backend_spring.repositories.BudgetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BudgetService {
    private final BudgetRepository budgetRepository;

    public List<Budget> getAllBudgets() {
        return budgetRepository.findAll();
    }

    public Optional<Budget> getBudgetByDepartementAndAnnee(Departement departement, int annee) {
        return budgetRepository.findByDepartementAndAnnee(departement, annee);
    }

    @Transactional
    public Budget saveBudget(Budget budget) {
        // Vérifier si un budget existe déjà pour ce département et cette année
        Optional<Budget> existing = budgetRepository.findByDepartementAndAnnee(budget.getDepartement(),
                budget.getAnnee());
        if (existing.isPresent()) {
            throw new RuntimeException("Un budget existe déjà pour ce département en " + budget.getAnnee());
        }
        return budgetRepository.save(budget);
    }

    public boolean isFondsDisponibles(Departement departement, BigDecimal montant) {
        int anneeActuelle = LocalDate.now().getYear();
        Optional<Budget> budgetOpt = budgetRepository.findByDepartementAndAnnee(departement, anneeActuelle);

        if (budgetOpt.isPresent()) {
            Budget budget = budgetOpt.get();
            return budget.getMontantDisponible().compareTo(montant) >= 0;
        }
        return false;
    }

    @Transactional
    public void consommerBudget(Departement departement, BigDecimal montant) {
        int anneeActuelle = LocalDate.now().getYear();
        Budget budget = budgetRepository.findByDepartementAndAnnee(departement, anneeActuelle)
                .orElseThrow(() -> new RuntimeException("Aucun budget défini pour ce département en " + anneeActuelle));

        if (budget.getMontantDisponible().compareTo(montant) < 0) {
            throw new RuntimeException("Fonds insuffisants dans le budget du département " + departement.getNom());
        }

        budget.setMontantConsomme(budget.getMontantConsomme().add(montant));
        budgetRepository.save(budget);
    }

    @Transactional
    public void rechargerBudget(Departement departement, BigDecimal montant) {
        int anneeActuelle = LocalDate.now().getYear();
        Optional<Budget> budgetOpt = budgetRepository.findByDepartementAndAnnee(departement, anneeActuelle);

        if (budgetOpt.isPresent()) {
            Budget budget = budgetOpt.get();
            // Réduire le montant consommé augmente le montant disponible
            budget.setMontantConsomme(budget.getMontantConsomme().subtract(montant));
            budgetRepository.save(budget);
        }
        // Si aucun budget n'est défini, on ne fait rien (pas d'erreur bloquante pour
        // une recette)
    }

    @Transactional
    public void deleteBudget(int id) {
        budgetRepository.deleteById(id);
    }
}
