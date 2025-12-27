package com.seowon.coding.domain.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders") // "order" is a reserved keyword in SQL
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Order {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String customerName;
    
    private String customerEmail;
    
    @Enumerated(EnumType.STRING)
    private OrderStatus status;
    
    private LocalDateTime orderDate;
    
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<OrderItem> items = new ArrayList<>();
    
    private BigDecimal totalAmount;
    
    // Business logic    
    public void addItem(OrderItem item) {
        items.add(item);
        item.setOrder(this);
        recalculateTotalAmount();
    }
    
    public void removeItem(OrderItem item) {
        items.remove(item);
        item.setOrder(null);
        recalculateTotalAmount();
    }
    
    public List<Long> getIds(List<OrderItem> item) {
    	List<Long> Ids = new ArrayList<Long>();
    	for(int i=0; i<item.size(); i++) {
    		Long id = item.get(i).getId();
    		Ids.add(id);
    	}
    	
    	return Ids;
    }
    
    public List<Integer> getQuantities(List<OrderItem> item) {
    	List<Integer> quantities = new ArrayList<Integer>();
    	for(int i=0; i<item.size(); i++) {
    		int quantity = item.get(i).getQuantity();
    		quantities.add(quantity);
    	}	
    	
    	return quantities;
    }
    
    public void recalculateTotalAmount() {
        this.totalAmount = items.stream()
                .map(OrderItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    
    public void markAsProcessing() {
        this.status = OrderStatus.PROCESSING;
    }
    
    public void markAsShipped() {
        this.status = OrderStatus.SHIPPED;
    }
    
    public void markAsDelivered() {
        this.status = OrderStatus.DELIVERED;
    }
    
    public void markAsCancelled() {
        this.status = OrderStatus.CANCELLED;
    }
    
    public enum OrderStatus {
        PENDING, PROCESSING, SHIPPED, DELIVERED, CANCELLED
    }
}