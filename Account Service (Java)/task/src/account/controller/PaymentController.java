package account.controller;


import account.model.dto.PaymentDTOIn;
import account.model.dto.PaymentDTOOut;
import account.model.dto.Status;
import account.model.entity.AppUser;
import account.model.dto.SingUpDTO;
import account.model.entity.Payment;
import account.repository.PaymentRepository;
import account.service.PaymentService;
import account.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
public class PaymentController {

    private final UserService userService;
    private final PaymentService paymentService;

    private final PaymentRepository paymentRepository;

    public PaymentController(UserService userService, PaymentService paymentService, PaymentRepository paymentRepository) {
        this.userService = userService;
        this.paymentService = paymentService;
        this.paymentRepository = paymentRepository;
    }

    @GetMapping("/api/empl/payment")
    public ResponseEntity<?> payment(@AuthenticationPrincipal UserDetails details, @RequestParam(required = false) String period) {
        AppUser user = userService.findUserByEmail(details.getUsername());
        List<PaymentDTOOut> payments = paymentService.getPaymentsForUser(user);
        return ResponseEntity.ok(payments);
    }

    @PostMapping("api/acct/payments")
    public ResponseEntity<Status> addPayments(@RequestBody List<PaymentDTOIn> payments) {
        payments.forEach(System.out::println);
        paymentService.savePayments(payments);
        return ResponseEntity.ok(new Status("Added successfully!"));
    }

    @GetMapping("api/acct/payments")
    public ResponseEntity<List<Payment>> getPayments() {
        var list = new ArrayList<Payment>();
        Iterable<Payment> payments = paymentRepository.findAll();
        for (Payment payment : payments) {
            list.add(payment);
        }
        return ResponseEntity.ok(list);
    }
}
