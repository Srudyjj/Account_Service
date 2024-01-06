package account.model.entity;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.Objects;

@Entity
@Table(name = "payment",
        uniqueConstraints = @UniqueConstraint(
                columnNames = {"period", "employee_id"},
                name = "unique_period_and_employee_id"))
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "employee_id",
            referencedColumnName = "id",
            foreignKey = @ForeignKey(name = "user_payment_fk"))
    private AppUser employee;

    @Column(nullable = false)
    private LocalDate period;

    @Column(nullable = false)
    private Long salary;

    public Payment(AppUser employee, LocalDate period, Long salary) {
        this.employee = employee;
        this.period = period;
        this.salary = salary;
    }

    public Payment() {
    }

    public AppUser getEmployee() {
        return employee;
    }

    public void setEmployee(AppUser employee) {
        this.employee = employee;
    }

    public LocalDate getPeriod() {
        return period;
    }

    public void setPeriod(LocalDate period) {
        this.period = period;
    }

    public Long getSalary() {
        return salary;
    }

    public void setSalary(Long salary) {
        this.salary = salary;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Payment payment = (Payment) o;
        return Objects.equals(employee, payment.employee) && Objects.equals(period, payment.period) && Objects.equals(salary, payment.salary);
    }

    @Override
    public int hashCode() {
        return Objects.hash(employee, period, salary);
    }
}
