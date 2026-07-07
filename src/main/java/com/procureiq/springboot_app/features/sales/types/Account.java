package com.procureiq.springboot_app.features.sales.types;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "accounts", schema = "blute")
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "accountcode", length = 15)
    private String accountCode;

    @Column(name = "account_name", nullable = false, length = 90)
    private String accountName;

    @Column(name = "displayname", nullable = false, length = 90)
    private String displayName;

    @Column(name = "email", length = 45)
    private String email;

    @Column(name = "phone", length = 12)
    private String phone;

    @Column(name = "description", length = 4000)
    private String description;

    @Column(name = "isactive", nullable = false)
    private Boolean isActive;

    @Column(name = "isdeleted", nullable = false)
    private Boolean isDeleted;

    @Column(name = "entrydatetime", nullable = false)
    private LocalDateTime entryDateTime;

    @Column(name = "updateentrydatetime", nullable = false)
    private LocalDateTime updateEntryDateTime;

    @Column(name = "username", nullable = false, length = 100)
    private String username;

    @Column(name = "updateusername", nullable = false, length = 100)
    private String updateUsername;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getAccountCode() { return accountCode; }
    public void setAccountCode(String accountCode) { this.accountCode = accountCode; }

    public String getAccountName() { return accountName; }
    public void setAccountName(String accountName) { this.accountName = accountName; }

    public String getDisplayName() { return displayName; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }

    public Boolean getIsDeleted() { return isDeleted; }
    public void setIsDeleted(Boolean isDeleted) { this.isDeleted = isDeleted; }

    public LocalDateTime getEntryDateTime() { return entryDateTime; }
    public void setEntryDateTime(LocalDateTime entryDateTime) { this.entryDateTime = entryDateTime; }

    public LocalDateTime getUpdateEntryDateTime() { return updateEntryDateTime; }
    public void setUpdateEntryDateTime(LocalDateTime updateEntryDateTime) { this.updateEntryDateTime = updateEntryDateTime; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getUpdateUsername() { return updateUsername; }
    public void setUpdateUsername(String updateUsername) { this.updateUsername = updateUsername; }
}
