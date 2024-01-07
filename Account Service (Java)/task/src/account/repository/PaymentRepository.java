package account.repository;

import account.model.entity.AppUser;
import account.model.entity.Payment;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends CrudRepository<Payment, Long> {

    List<Payment> findPaymentsByEmployeeOrderByPeriodDesc(AppUser appUser);

    Optional<Payment> findPaymentByEmployeeAndPeriod(AppUser appUser, LocalDate period);


}
