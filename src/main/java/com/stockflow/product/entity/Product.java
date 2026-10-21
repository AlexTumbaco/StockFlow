package com.stockflow.product.entity;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import org.hibernate.annotations.Nationalized;

import com.stockflow.category.entity.Category;

@Entity
@Table(name = "products", uniqueConstraints =
        @UniqueConstraint(name = "uk_products_sku", columnNames = "sku"))
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 160)
    @Nationalized
    private String name;

    @Column(nullable = false, length = 64)
    @Nationalized
    private String sku;

    @Column(length = 2000)
    @Nationalized
    private String description;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal price;

    @Column(nullable = false)
    private Integer stock;

    @Column(name = "minimum_stock", nullable = false)
    private Integer minimumStock;

    @Column(nullable = false)
    private Boolean active;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "category_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_products_category"))
    private Category category;

    protected Product() {
    }

    public Product(String name, String sku, String description, BigDecimal price,
                   Integer minimumStock, Category category) {
        this.name = name;
        this.sku = sku;
        this.description = description;
        this.price = price;
        this.stock = 0;
        this.minimumStock = minimumStock;
        this.active = true;
        this.category = category;
    }

    public void update(String name, String sku, String description, BigDecimal price,
                       Integer minimumStock, Category category) {
        this.name = name;
        this.sku = sku;
        this.description = description;
        this.price = price;
        this.minimumStock = minimumStock;
        this.category = category;
    }

    public void deactivate() {
        this.active = false;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getSku() {
        return sku;
    }

    public String getDescription() {
        return description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public Integer getStock() {
        return stock;
    }

    public Integer getMinimumStock() {
        return minimumStock;
    }

    public Boolean getActive() {
        return active;
    }

    public Category getCategory() {
        return category;
    }
}
