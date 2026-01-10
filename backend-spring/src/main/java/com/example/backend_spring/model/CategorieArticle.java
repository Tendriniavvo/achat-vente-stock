package com.example.backend_spring.model;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "categories_articles")
public class CategorieArticle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true, length = 100)
    private String nom;

    @Column(columnDefinition = "TEXT")
    private String description;

    @ManyToOne
    @JoinColumn(name = "parent_id")
    private CategorieArticle parent;

    @OneToMany(mappedBy = "parent")
    private Set<CategorieArticle> sousCategories = new HashSet<>();

    @OneToMany(mappedBy = "categorie")
    private Set<Article> articles = new HashSet<>();

    // Constructeurs
    public CategorieArticle() {
    }

    public CategorieArticle(String nom, String description) {
        this.nom = nom;
        this.description = description;
    }

    // Getters et Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public CategorieArticle getParent() {
        return parent;
    }

    public void setParent(CategorieArticle parent) {
        this.parent = parent;
    }

    public Set<CategorieArticle> getSousCategories() {
        return sousCategories;
    }

    public void setSousCategories(Set<CategorieArticle> sousCategories) {
        this.sousCategories = sousCategories;
    }

    public Set<Article> getArticles() {
        return articles;
    }

    public void setArticles(Set<Article> articles) {
        this.articles = articles;
    }
}
