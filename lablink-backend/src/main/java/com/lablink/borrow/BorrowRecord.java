package com.lablink.borrow;

import com.lablink.auth.User;
import com.lablink.equipment.Equipment;
import jakarta.persistence.*;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "borrow_records")
public class BorrowRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "equipment_id", nullable = false)
    private Equipment equipment;

    @Column(name = "borrow_date", nullable = false, updatable = false)
    private Instant borrowDate = Instant.now();

    @Column(name = "expected_return_date", nullable = false)
    private LocalDate expectedReturnDate;

    @Column(name = "actual_return_date")
    private Instant actualReturnDate;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "borrow_status", nullable = false)
    private BorrowStatus status = BorrowStatus.ACTIVE;

    @Column(columnDefinition = "text")
    private String purpose;

    @Column(columnDefinition = "text")
    private String remarks;

    public BorrowRecord() {}

    public UUID getId()                        { return id; }
    public User getUser()                      { return user; }
    public void setUser(User v)                { this.user = v; }
    public Equipment getEquipment()            { return equipment; }
    public void setEquipment(Equipment v)      { this.equipment = v; }
    public Instant getBorrowDate()             { return borrowDate; }
    public LocalDate getExpectedReturnDate()   { return expectedReturnDate; }
    public void setExpectedReturnDate(LocalDate v) { this.expectedReturnDate = v; }
    public Instant getActualReturnDate()       { return actualReturnDate; }
    public void setActualReturnDate(Instant v) { this.actualReturnDate = v; }
    public BorrowStatus getStatus()            { return status; }
    public void setStatus(BorrowStatus v)      { this.status = v; }
    public String getPurpose()                 { return purpose; }
    public void setPurpose(String v)           { this.purpose = v; }
    public String getRemarks()                 { return remarks; }
    public void setRemarks(String v)           { this.remarks = v; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private User user;
        private Equipment equipment;
        private LocalDate expectedReturnDate;
        private BorrowStatus status = BorrowStatus.ACTIVE;
        private String purpose, remarks;

        public Builder user(User v)                      { this.user = v; return this; }
        public Builder equipment(Equipment v)            { this.equipment = v; return this; }
        public Builder expectedReturnDate(LocalDate v)   { this.expectedReturnDate = v; return this; }
        public Builder status(BorrowStatus v)            { this.status = v; return this; }
        public Builder purpose(String v)                 { this.purpose = v; return this; }
        public Builder remarks(String v)                 { this.remarks = v; return this; }

        public BorrowRecord build() {
            BorrowRecord r = new BorrowRecord();
            r.user               = this.user;
            r.equipment          = this.equipment;
            r.expectedReturnDate = this.expectedReturnDate;
            r.status             = this.status != null ? this.status : BorrowStatus.ACTIVE;
            r.purpose            = this.purpose;
            r.remarks            = this.remarks;
            r.borrowDate         = Instant.now();
            return r;
        }
    }
}
