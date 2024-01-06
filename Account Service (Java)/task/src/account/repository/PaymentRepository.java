package account.repository;

import account.model.entity.AppUser;
import account.model.entity.Payment;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

public interface PaymentRepository extends CrudRepository<Payment, Long> {

    List<Payment> findPaymentsByEmployeeOrderByPeriodDesc(AppUser appUser);
}
