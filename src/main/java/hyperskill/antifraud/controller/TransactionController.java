package hyperskill.antifraud.controller;

import hyperskill.antifraud.dto.ChangeTransactionDTO;
import hyperskill.antifraud.dto.TransactionResponseDTO;
import hyperskill.antifraud.model.database.TransactionEntity;
import hyperskill.antifraud.service.TransactionService;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/antifraud")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping(value = "/transaction", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TransactionResponseDTO> postTransaction(@RequestBody TransactionEntity transaction) throws JsonProcessingException {
        return transactionService.processTransaction(transaction);
    }

    @PutMapping(value = "/transaction")
    public ResponseEntity<TransactionEntity> modifyTransaction(@RequestBody ChangeTransactionDTO transactionChangeBody) throws JsonProcessingException {
        return transactionService.modifyTransaction(transactionChangeBody);
    }

    @GetMapping(value = "/history")
    public ResponseEntity<List<TransactionEntity>> getTransactionHistory() {
        return transactionService.getAllTransactions();
    }

    @GetMapping(value = "/history/{number}")
    public ResponseEntity<List<TransactionEntity>> getTransactionHistoryForCard(@PathVariable String number) {
        return transactionService.getAllTransactionsForCard(number);
    }
}