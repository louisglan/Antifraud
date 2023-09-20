package hyperskill.antifraud.service;

import hyperskill.antifraud.dto.ChangeTransactionDTO;
import hyperskill.antifraud.dto.TransactionResponseDTO;
import hyperskill.antifraud.model.database.StolenCardEntity;
import hyperskill.antifraud.model.database.SuspiciousIpEntity;
import hyperskill.antifraud.model.database.TransactionBoundaryEntity;
import hyperskill.antifraud.model.database.TransactionEntity;
import hyperskill.antifraud.repository.StolenCardRepository;
import hyperskill.antifraud.repository.SuspiciousIpRepository;
import hyperskill.antifraud.repository.TransactionBoundaryRepository;
import hyperskill.antifraud.repository.TransactionRepository;
import hyperskill.antifraud.service.enums.TransactionRejectionReason;
import hyperskill.antifraud.service.enums.TransactionResult;
import hyperskill.antifraud.service.utils.Validator;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class TransactionService {
    private final StolenCardRepository stolenCardRepository;
    private final SuspiciousIpRepository suspiciousIpRepository;
    private final TransactionRepository transactionRepository;
    private final TransactionBoundaryRepository transactionBoundaryRepository;

    public TransactionService(StolenCardRepository stolenCardRepository,
                              SuspiciousIpRepository suspiciousIpRepository,
                              TransactionRepository transactionRepository,
                              TransactionBoundaryRepository transactionBoundaryRepository) {
        this.stolenCardRepository = stolenCardRepository;
        this.suspiciousIpRepository = suspiciousIpRepository;
        this.transactionRepository = transactionRepository;
        this.transactionBoundaryRepository = transactionBoundaryRepository;
    }

    public ResponseEntity<TransactionResponseDTO> processTransaction(
            TransactionEntity transaction) throws JsonProcessingException {
        Long amount = transaction.getAmount();
        if (amount == null) {
            return ResponseEntity.badRequest().build();
        }
        Optional<TransactionBoundaryEntity> transactionBoundaryResponse = transactionBoundaryRepository.findByNumber(transaction.getNumber());
        TransactionBoundaryEntity transactionBoundary = transactionBoundaryResponse.orElseGet(() -> new TransactionBoundaryEntity(transaction.getNumber(), 200L, 1500L));
        long maxAllowed = transactionBoundary.getMaxAllowed();
        long maxManual = transactionBoundary.getMaxManual();
        String transactionResult;
        List<String> transactionRejectionReasons = new ArrayList<>();
        if (amount <= 0 || Validator.isIpInvalid(transaction.getIp()) || transaction.getNumber().length() != 16) {
            return ResponseEntity.badRequest().build();
        } else if (amount <= maxAllowed) {
            transactionResult = TransactionResult.ALLOWED.getResult();
        } else if (amount <= maxManual) {
            transactionResult = TransactionResult.MANUAL_PROCESSING.getResult();
            transactionRejectionReasons.add(TransactionRejectionReason.AMOUNT.getReason());
        } else {
            transactionResult = TransactionResult.PROHIBITED.getResult();
            transactionRejectionReasons.add(TransactionRejectionReason.AMOUNT.getReason());
        }

        List<String> transactionsInLastHourByRegion = transactionRepository.
                findByNumberWithinOneHourOfTransactionGroupedByRegion(
                        transaction.getNumber(), transaction.getDate().minusHours(1), transaction.getDate());
        List<String> filteredByRegionTransactions = transactionsInLastHourByRegion.stream().filter(region -> !region.equals(transaction.getRegion())).toList();
        if (filteredByRegionTransactions.size() > 2) {
            transactionResult = TransactionResult.PROHIBITED.getResult();
            transactionRejectionReasons.add(TransactionRejectionReason.REGION_CORRELATION.getReason());
        }
        if (filteredByRegionTransactions.size() == 2) {
            transactionResult = TransactionResult.MANUAL_PROCESSING.getResult();
            transactionRejectionReasons.add(TransactionRejectionReason.REGION_CORRELATION.getReason());
        }
        List<String> transactionsInLastHourByIp = transactionRepository.
                findByNumberWithinOneHourOfTransactionGroupedByIp(
                        transaction.getNumber(), transaction.getDate().minusHours(1), transaction.getDate());
        List<String> filteredByIpTransactions = transactionsInLastHourByIp.stream().filter(ip -> !ip.equals(transaction.getIp())).toList();
        if (filteredByIpTransactions.size() > 2) {
            transactionResult = TransactionResult.PROHIBITED.getResult();
            transactionRejectionReasons.add(TransactionRejectionReason.IP_CORRELATION.getReason());
        }
        if (filteredByIpTransactions.size() == 2) {
            transactionResult = TransactionResult.MANUAL_PROCESSING.getResult();
            transactionRejectionReasons.add(TransactionRejectionReason.IP_CORRELATION.getReason());
        }

        Optional<StolenCardEntity> stolenCardEntityOptional = stolenCardRepository.findByNumber(transaction.getNumber());
        Optional<SuspiciousIpEntity> suspicousIpEntityOptional = suspiciousIpRepository.findByIp(transaction.getIp());
        if (stolenCardEntityOptional.isPresent()) {
            transactionResult = TransactionResult.PROHIBITED.getResult();
            transactionRejectionReasons.add(TransactionRejectionReason.CARD_NUMBER.getReason());
        }
        if (suspicousIpEntityOptional.isPresent()) {
            transactionResult = TransactionResult.PROHIBITED.getResult();
            transactionRejectionReasons.add(TransactionRejectionReason.IP.getReason());
        }
        if (transactionResult.equals(TransactionResult.PROHIBITED.getResult()) && amount > maxAllowed && amount <= maxManual) {
            transactionRejectionReasons = transactionRejectionReasons.stream().filter(reason -> !reason.equals("amount")).collect(Collectors.toList());
        }
        transactionRejectionReasons.sort(null);
        String info = transactionRejectionReasons.isEmpty() ? "none" : String.join(", ", transactionRejectionReasons);
        transaction.setResult(transactionResult);
        transaction.setFeedback("");
        transactionRepository.save(transaction);
        return  ResponseEntity.ok().body(
                new TransactionResponseDTO(transactionResult, info));
    }

    public ResponseEntity<TransactionEntity> modifyTransaction(ChangeTransactionDTO transactionChangeBody) {
        Optional<TransactionEntity> transactionEntityResponse = transactionRepository.findByTransactionId(transactionChangeBody.transactionId());
        if (Arrays.stream(TransactionResult.values())
                    .map(TransactionResult::name)
                    .noneMatch(transactionResult-> transactionResult.equals(transactionChangeBody.feedback()))) {
            return ResponseEntity.badRequest().build();
        }
        if (transactionEntityResponse.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        TransactionEntity transactionEntity = transactionEntityResponse.get();
        if (transactionEntity.getResult().equals(transactionChangeBody.feedback())) {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).build();
        }
        if (!transactionEntity.getFeedback().isEmpty()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }

        transactionEntity.setFeedback(transactionChangeBody.feedback());
        transactionRepository.save(transactionEntity);
        Optional<TransactionBoundaryEntity> transactionBoundaryResponse =
                transactionBoundaryRepository.findByNumber(transactionEntity.getNumber());
        TransactionBoundaryEntity transactionBoundary = transactionBoundaryResponse.orElseGet(
                () -> new TransactionBoundaryEntity(transactionEntity.getNumber(), 200L, 1500L));
        modifyTransactionBoundaries(transactionEntity, transactionBoundary);
        return ResponseEntity.ok().body(transactionEntity);
    }

    public ResponseEntity<List<TransactionEntity>> getAllTransactions() {
        List<TransactionEntity> allTransactions = transactionRepository.findByOrderByTransactionId();
        return ResponseEntity.ok().body(allTransactions);
    }

    public ResponseEntity<List<TransactionEntity>> getAllTransactionsForCard(String number) {
        if (Validator.isCardNumberInvalid(number)) {
            return ResponseEntity.badRequest().build();
        }

        List<TransactionEntity> allTransactionsForCard = transactionRepository.findByNumber(number);
        if (allTransactionsForCard.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok().body(allTransactionsForCard);
    }

    private void modifyTransactionBoundaries(TransactionEntity transaction, TransactionBoundaryEntity transactionBoundary) {
        String number = transaction.getNumber();
        String feedback = transaction.getFeedback();
        String result = transaction.getResult();
        Long amount = transaction.getAmount();

        String ALLOWED = TransactionResult.ALLOWED.getResult();
        String PROHIBITED = TransactionResult.PROHIBITED.getResult();
        if (feedback.equals(ALLOWED) && !result.equals(ALLOWED)) {
            transactionBoundary.setMaxAllowed((long) Math.ceil(calculateNewBoundary(BoundaryModification.INCREASE, transactionBoundary.getMaxAllowed(), amount)));
        }
        if (!feedback.equals(ALLOWED) && result.equals(ALLOWED)) {
            transactionBoundary.setMaxAllowed((long) Math.ceil(calculateNewBoundary(BoundaryModification.DECREASE, transactionBoundary.getMaxAllowed(), amount)));
        }
        if (feedback.equals(PROHIBITED) && !result.equals(PROHIBITED)) {
            transactionBoundary.setMaxManual((long) Math.ceil(calculateNewBoundary(BoundaryModification.DECREASE, transactionBoundary.getMaxManual(), amount)));
        }
        if (!feedback.equals(PROHIBITED) && result.equals(PROHIBITED)) {
            transactionBoundary.setMaxManual((long) Math.ceil(calculateNewBoundary(BoundaryModification.INCREASE, transactionBoundary.getMaxManual(), amount)));
        }
        transactionBoundaryRepository.save(transactionBoundary);
    }

    private double calculateNewBoundary(BoundaryModification boundaryModificationType, long currentLimit, long amount) {
        return 0.8 * currentLimit + (boundaryModificationType.equals(BoundaryModification.INCREASE) ? 0.2 : -0.2) * amount;
    }

    private enum BoundaryModification {
        INCREASE,
        DECREASE;
    }
}
