package account.controller;


import account.model.SingUpDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/empl")
public class PaymentController {

    @GetMapping("/payment")
    public ResponseEntity<SingUpDTO> payment(@RequestBody SingUpDTO singUpDTO) {
        return ResponseEntity.ok(singUpDTO);
    }
}
