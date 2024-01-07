package account.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.h2.jdbc.JdbcSQLIntegrityConstraintViolationException;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.logging.Logger;

@ControllerAdvice
public class ErrorHandlingControllerAdvice {

    Logger logger = Logger.getLogger(this.getClass().getName());
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> onMethodArgumentNotValidException(
            MethodArgumentNotValidException e,
            HttpServletRequest request) {
        logger.info(e.getMessage());

        String defaultMessage = Optional.ofNullable(
                e.getBindingResult().getFieldError())
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .orElse(e.getMessage());

        ErrorDTO body = new ErrorDTO(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                defaultMessage,
                request.getRequestURI());

        return ResponseEntity
                .badRequest()
                .contentType(MediaType.APPLICATION_JSON)
                .body(body);
    }

    @ExceptionHandler(JdbcSQLIntegrityConstraintViolationException.class)
    public ResponseEntity<?> onJdbcSQLIntegrityConstraintViolationException(
            JdbcSQLIntegrityConstraintViolationException e,
            HttpServletRequest request) {
        logger.info(e.getMessage());

        ErrorDTO body = new ErrorDTO(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                "Duplicated entry in payment list",
                request.getRequestURI());

        return ResponseEntity
                .badRequest()
                .contentType(MediaType.APPLICATION_JSON)
                .body(body);
    }

}
