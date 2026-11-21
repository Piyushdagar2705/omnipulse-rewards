package com.example.springsocial.model;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "customer_feedback")
public class Feedback {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String customerEmail;
    private String channel; // "WEB", "MOBILE", or "WHATSAPP"
    private String message;
    private int pointsAwarded = 50;
    private LocalDateTime createdAt = LocalDateTime.now();

    // Standard getters and setters
    public Long getId() { return id; }
    public String getCustomerEmail() { return customerEmail; }
    public void setCustomerEmail(String customerEmail) { this.customerEmail = customerEmail; }
    public String getChannel() { return channel; }
    public void setChannel(String channel) { this.channel = channel; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public int getPointsAwarded() { return pointsAwarded; }
    public void setPointsAwarded(int pointsAwarded) { this.pointsAwarded = pointsAwarded; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}