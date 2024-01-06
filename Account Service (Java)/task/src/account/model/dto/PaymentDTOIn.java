package account.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.YearMonth;

public class PaymentDTOIn {

    @NotNull
    @Email
    private String employee;

    @JsonFormat(pattern = "MM-yyyy")
    private LocalDate period;

    @Min(value = 0, message = "Salary must be non negative")
    private long salary;

    public PaymentDTOIn() {
    }

    public String employee() {
        return employee;
    }

    public void setEmployee(String employee) {
        this.employee = employee;
    }

    public LocalDate period() {
        return period;
    }

    public void setPeriod(YearMonth period) {
        this.period = LocalDate.of(period.getYear(), period.getMonth(), 1);
    }

    public long salary() {
        return salary;
    }

    public void setSalary(long salary) {
        this.salary = salary;
    }

    @Override
    public String toString() {
        return "PaymentDTO{" +
                "employee='" + employee + '\'' +
                ", period=" + period +
                ", salary=" + salary +
                '}';
    }
}
