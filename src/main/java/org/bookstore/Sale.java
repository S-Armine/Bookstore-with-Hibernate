package org.bookstore;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "sales")
public class Sale {
    @Id
    @Column(name = "sale_id", nullable = false)
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer saleID;
    @ManyToOne
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;
    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;
    @Column(name = "date_of_sale")
    private LocalDate dateOfSale;
    @Column(name = "quantity_sold", nullable = false, columnDefinition = "INTEGER CHECK (quantity_sold >= 0)")
    private Integer quantitySold;

    @Column(name = "total_price", nullable = false, columnDefinition = "REAL CHECK (total_price >= 0)")
    private Float totalPrice;

    public Sale() {
    }

    public Sale(Book book, Customer customer, LocalDate dateOfSale, Integer quantitySold, Float totalPrice) {
        this.book = book;
        this.customer = customer;
        this.dateOfSale = dateOfSale;
        this.quantitySold = quantitySold;
        this.totalPrice = totalPrice;
    }

    public void setBook(Book book) {
        this.book = book;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public void setDateOfSale(LocalDate dateOfSale) {
        this.dateOfSale = dateOfSale;
    }

    public void setQuantitySold(Integer quantitySold) {
        this.quantitySold = quantitySold;
    }

    public void setTotalPrice(Float totalPrice) {
        this.totalPrice = totalPrice;
    }

    public Integer getSaleID() {
        return saleID;
    }

    public Book getBook() {
        return book;
    }

    public Customer getCustomer() {
        return customer;
    }

    public LocalDate getDateOfSale() {
        return dateOfSale;
    }

    public Integer getQuantitySold() {
        return quantitySold;
    }

    public Float getTotalPrice() {
        return totalPrice;
    }
}
