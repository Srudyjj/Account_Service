package account.service;

import account.model.dto.PaymentDTOIn;
import account.model.dto.PaymentDTOOut;
import account.model.entity.AppUser;
import account.model.entity.Payment;
import account.repository.AppUserRepository;
import account.repository.PaymentRepository;
import jakarta.transaction.Transactional;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final AppUserRepository userRepository;

    public PaymentService(PaymentRepository paymentRepository, AppUserRepository userRepository) {
        this.paymentRepository = paymentRepository;
        this.userRepository = userRepository;
    }

    @Transactional(rollbackOn = {SQLException.class, DataAccessException.class})
    public void savePayments(List<PaymentDTOIn> payments) {
        for (PaymentDTOIn payment : payments) {

            if (payment.salary() < 0) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Salary can not be negative!");
            }

            String email = payment.employee();
            AppUser user = userRepository
                    .findAppUserByEmailIgnoreCase(email)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));
            paymentRepository
                    .save(new Payment(user, payment.period(), payment.salary()));
        }
    }

    @Transactional(rollbackOn = {SQLException.class, DataAccessException.class})
    public void editPayment(PaymentDTOIn paymentDTOIn) {
        String email = paymentDTOIn.employee();
        AppUser user = userRepository
                .findAppUserByEmailIgnoreCase(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        Optional<Payment> payment = paymentRepository
                .findPaymentByEmployeeAndPeriod(user, paymentDTOIn.period());

        if (payment.isPresent()) {
            if (paymentDTOIn.salary() < 0) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Salary can not be negative!");
            }
            payment.get().setSalary(paymentDTOIn.salary());
        } else {
            paymentRepository
                    .save(new Payment(user, paymentDTOIn.period(), paymentDTOIn.salary()));
        }
    }

    public List<PaymentDTOOut> getPaymentsForUser(AppUser appUser) {
        return paymentRepository.findPaymentsByEmployeeOrderByPeriodDesc(appUser)
                .stream()
                .map(payment -> new PaymentDTOOut(
                        appUser.getName(),
                        appUser.getLastname(),
                        convertPeriod(payment.getPeriod()),
                        convertSalary(payment.getSalary())))
                .collect(Collectors.toList());
    }

    public PaymentDTOOut getPaymentsForUserAndPeriod(AppUser appUser, LocalDate period) {
        return paymentRepository.findPaymentByEmployeeAndPeriod(appUser, period)
                .map(payment -> new PaymentDTOOut(
                        appUser.getName(),
                        appUser.getLastname(),
                        convertPeriod(payment.getPeriod()),
                        convertSalary(payment.getSalary())))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Payment not found!"));
    }

    private static String convertSalary(Long salary) {
        return salary / 100 + " dollar(s) " + salary % 100 + " cent(s)";
    }


    private static String convertPeriod(LocalDate period) {
        return period.format(DateTimeFormatter.ofPattern("MMMM-yyyy"));
    }
}
