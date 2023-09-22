package hyperskill.antifraud.service;

import hyperskill.antifraud.dto.TransactionResponseDTO;
import hyperskill.antifraud.model.database.TransactionEntity;
import hyperskill.antifraud.repository.StolenCardRepository;
import hyperskill.antifraud.repository.SuspiciousIpRepository;
import hyperskill.antifraud.repository.TransactionBoundaryRepository;
import hyperskill.antifraud.repository.TransactionRepository;
import hyperskill.antifraud.service.enums.Region;
import hyperskill.antifraud.service.enums.TransactionRejectionReason;
import hyperskill.antifraud.service.enums.TransactionResult;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.*;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TransactionServiceTest {
    @Mock
    private TransactionRepository transactionRepository;
    @Mock
    private StolenCardRepository stolenCardRepository;
    @Mock
    private SuspiciousIpRepository suspiciousIpRepository;
    @Mock
    private TransactionBoundaryRepository transactionBoundaryRepository;

    @InjectMocks
    private TransactionService transactionService;

    Long maxBadRequest = 0L;
    Long maxAllowed = 200L;
    Long maxManualProcessing = 1500L;
    
    String NONE = "none";

    @Test
    public void testNewTransactionMaxBadRequestAmount() {
        String cardNumber = "4000008449433403";
        LocalDateTime transactionTimestampMinusOneHour = LocalDateTime.of(2022, 1, 22, 15, 4);
        LocalDateTime transactionTimestamp = LocalDateTime.of(2022, 1, 22, 16, 4);
        String ip = "192.168.1.1";

        TransactionEntity transaction = createNewTransactionEntity(maxBadRequest, ip, cardNumber, Region.EAP.getRegion(), transactionTimestamp);

        ResponseEntity<?> responseEntity = transactionService.processTransaction(transaction);

        Assertions.assertEquals(responseEntity.getStatusCode(), HttpStatus.BAD_REQUEST);
    }

    @Test
    public void testNewTransactionMinAllowed() {
        String cardNumber = "4000008449433403";
        LocalDateTime transactionTimestampMinusOneHour = LocalDateTime.of(2022, 1, 22, 15, 4);
        LocalDateTime transactionTimestamp = LocalDateTime.of(2022, 1, 22, 16, 4);
        String ip = "192.168.1.1";

        TransactionEntity transaction = createNewTransactionEntity(maxBadRequest + 1, ip, cardNumber, Region.EAP.getRegion(), transactionTimestamp);

        ResponseEntity<?> responseEntity = transactionService.processTransaction(transaction);

        TransactionResponseDTO expectedResponseBody = new TransactionResponseDTO(TransactionResult.ALLOWED.getResult(), NONE);

        Assertions.assertEquals(responseEntity.getStatusCode(), HttpStatus.OK);
        Assertions.assertEquals(responseEntity.getBody(), expectedResponseBody);
    }

    @Test
    public void testNewTransactionMaxAllowed() {
        String cardNumber = "4000008449433403";
        LocalDateTime transactionTimestampMinusOneHour = LocalDateTime.of(2022, 1, 22, 15, 4);
        LocalDateTime transactionTimestamp = LocalDateTime.of(2022, 1, 22, 16, 4);
        String ip = "192.168.1.1";

        TransactionEntity transaction = createNewTransactionEntity(maxAllowed, ip, cardNumber, Region.EAP.getRegion(), transactionTimestamp);

        ResponseEntity<?> responseEntity = transactionService.processTransaction(transaction);

        TransactionResponseDTO expectedResponseBody = new TransactionResponseDTO(TransactionResult.ALLOWED.getResult(), NONE);

        Assertions.assertEquals(responseEntity.getStatusCode(), HttpStatus.OK);
        Assertions.assertEquals(responseEntity.getBody(), expectedResponseBody);
    }

    @Test
    public void testNewTransactionMinManualProcessingAmount() {
        String cardNumber = "4000008449433403";
        LocalDateTime transactionTimestampMinusOneHour = LocalDateTime.of(2022, 1, 22, 15, 4);
        LocalDateTime transactionTimestamp = LocalDateTime.of(2022, 1, 22, 16, 4);
        String ip = "192.168.1.1";

        TransactionEntity transaction = createNewTransactionEntity(maxAllowed + 1, ip, cardNumber, Region.EAP.getRegion(), transactionTimestamp);

        ResponseEntity<?> responseEntity = transactionService.processTransaction(transaction);

        TransactionResponseDTO expectedResponseBody = new TransactionResponseDTO(
                TransactionResult.MANUAL_PROCESSING.getResult(), TransactionRejectionReason.AMOUNT.getReason());

        Assertions.assertEquals(responseEntity.getStatusCode(), HttpStatus.OK);
        Assertions.assertEquals(responseEntity.getBody(), expectedResponseBody);
    }

    @Test
    public void testNewTransactionMaxManualProcessingAmount() {
        String cardNumber = "4000008449433403";
        LocalDateTime transactionTimestampMinusOneHour = LocalDateTime.of(2022, 1, 22, 15, 4);
        LocalDateTime transactionTimestamp = LocalDateTime.of(2022, 1, 22, 16, 4);
        String ip = "192.168.1.1";

        TransactionEntity transaction = createNewTransactionEntity(maxManualProcessing, ip, cardNumber, Region.EAP.getRegion(), transactionTimestamp);

        ResponseEntity<?> responseEntity = transactionService.processTransaction(transaction);

        TransactionResponseDTO expectedResponseBody = new TransactionResponseDTO(
                TransactionResult.MANUAL_PROCESSING.getResult(), TransactionRejectionReason.AMOUNT.getReason());

        Assertions.assertEquals(responseEntity.getStatusCode(), HttpStatus.OK);
        Assertions.assertEquals(responseEntity.getBody(), expectedResponseBody);
    }

    @Test
    public void testNewTransactionMinProhibitedAmount() {
        String cardNumber = "4000008449433403";
        LocalDateTime transactionTimestampMinusOneHour = LocalDateTime.of(2022, 1, 22, 15, 4);
        LocalDateTime transactionTimestamp = LocalDateTime.of(2022, 1, 22, 16, 4);
        String ip = "192.168.1.1";

        TransactionEntity transaction = createNewTransactionEntity(maxManualProcessing + 1, ip, cardNumber, Region.EAP.getRegion(), transactionTimestamp);

        ResponseEntity<?> responseEntity = transactionService.processTransaction(transaction);

        TransactionResponseDTO expectedResponseBody = new TransactionResponseDTO(
                TransactionResult.PROHIBITED.getResult(), TransactionRejectionReason.AMOUNT.getReason());

        Assertions.assertEquals(responseEntity.getStatusCode(), HttpStatus.OK);
        Assertions.assertEquals(responseEntity.getBody(), expectedResponseBody);
    }

    @Test
    public void testNewTransactionManualProcessing2RegionsInLastHourIncludingTransactionRegion() {
        String cardNumber = "4000008449433403";
        LocalDateTime transactionTimestampMinusOneHour = LocalDateTime.of(2022, 1, 22, 15, 4);
        LocalDateTime transactionTimestamp = LocalDateTime.of(2022, 1, 22, 16, 4);
        String ip = "192.168.1.1";
        ArrayList<String> transactionsInLastHourByRegion = new ArrayList<>(Arrays.asList(Region.EAP.getRegion(), Region.HIC.getRegion(), Region.ECA.getRegion()));
        when(transactionRepository.findByNumberWithinOneHourOfTransactionGroupedByRegion(
                cardNumber, transactionTimestampMinusOneHour, transactionTimestamp)).thenReturn(transactionsInLastHourByRegion);

        TransactionEntity transaction = createNewTransactionEntity(maxAllowed, ip, cardNumber, Region.EAP.getRegion(), transactionTimestamp);

        ResponseEntity<?> responseEntity = transactionService.processTransaction(transaction);

        TransactionResponseDTO expectedResponseBody = new TransactionResponseDTO(
                TransactionResult.MANUAL_PROCESSING.getResult(), TransactionRejectionReason.REGION_CORRELATION.getReason());

        Assertions.assertEquals(responseEntity.getStatusCode(), HttpStatus.OK);
        Assertions.assertEquals(responseEntity.getBody(), expectedResponseBody);
    }

    @Test
    public void testNewTransactionManualProcessing2RegionsInLastHourNotIncludingTransactionRegion() {
        String cardNumber = "4000008449433403";
        LocalDateTime transactionTimestampMinusOneHour = LocalDateTime.of(2022, 1, 22, 15, 4);
        LocalDateTime transactionTimestamp = LocalDateTime.of(2022, 1, 22, 16, 4);
        String ip = "192.168.1.1";
        ArrayList<String> transactionsInLastHourByRegion = new ArrayList<>(Arrays.asList(Region.HIC.getRegion(), Region.ECA.getRegion()));
        when(transactionRepository.findByNumberWithinOneHourOfTransactionGroupedByRegion(
                cardNumber, transactionTimestampMinusOneHour, transactionTimestamp)).thenReturn(transactionsInLastHourByRegion);

        TransactionEntity transaction = createNewTransactionEntity(maxAllowed, ip, cardNumber, Region.EAP.getRegion(), transactionTimestamp);

        ResponseEntity<?> responseEntity = transactionService.processTransaction(transaction);

        TransactionResponseDTO expectedResponseBody = new TransactionResponseDTO(
                TransactionResult.MANUAL_PROCESSING.getResult(), TransactionRejectionReason.REGION_CORRELATION.getReason());

        Assertions.assertEquals(responseEntity.getStatusCode(), HttpStatus.OK);
        Assertions.assertEquals(responseEntity.getBody(), expectedResponseBody);
    }

    @Test
    public void testNewTransactionProhibitedMoreThan2RegionsInLastHourIncludingTransactionRegion() {
        String cardNumber = "4000008449433403";
        LocalDateTime transactionTimestampMinusOneHour = LocalDateTime.of(2022, 1, 22, 15, 4);
        LocalDateTime transactionTimestamp = LocalDateTime.of(2022, 1, 22, 16, 4);
        String ip = "192.168.1.1";
        ArrayList<String> transactionsInLastHourByRegion = new ArrayList<>(Arrays.asList(Region.EAP.getRegion(), Region.HIC.getRegion(), Region.ECA.getRegion(), Region.SA.getRegion()));
        when(transactionRepository.findByNumberWithinOneHourOfTransactionGroupedByRegion(
                cardNumber, transactionTimestampMinusOneHour, transactionTimestamp)).thenReturn(transactionsInLastHourByRegion);

        TransactionEntity transaction = createNewTransactionEntity(maxAllowed, ip, cardNumber, Region.EAP.getRegion(), transactionTimestamp);

        ResponseEntity<?> responseEntity = transactionService.processTransaction(transaction);

        TransactionResponseDTO expectedResponseBody = new TransactionResponseDTO(
                TransactionResult.PROHIBITED.getResult(), TransactionRejectionReason.REGION_CORRELATION.getReason());

        Assertions.assertEquals(responseEntity.getStatusCode(), HttpStatus.OK);
        Assertions.assertEquals(responseEntity.getBody(), expectedResponseBody);
    }

    @Test
    public void testNewTransactionProhibitedMoreThan2RegionsInLastHourNotIncludingTransactionRegion() {
        String cardNumber = "4000008449433403";
        LocalDateTime transactionTimestampMinusOneHour = LocalDateTime.of(2022, 1, 22, 15, 4);
        LocalDateTime transactionTimestamp = LocalDateTime.of(2022, 1, 22, 16, 4);
        String ip = "192.168.1.1";
        ArrayList<String> transactionsInLastHourByRegion = new ArrayList<>(Arrays.asList(Region.HIC.getRegion(), Region.ECA.getRegion(), Region.SA.getRegion()));
        when(transactionRepository.findByNumberWithinOneHourOfTransactionGroupedByRegion(
                cardNumber, transactionTimestampMinusOneHour, transactionTimestamp)).thenReturn(transactionsInLastHourByRegion);

        TransactionEntity transaction = createNewTransactionEntity(maxAllowed, ip, cardNumber, Region.EAP.getRegion(), transactionTimestamp);

        ResponseEntity<?> responseEntity = transactionService.processTransaction(transaction);

        TransactionResponseDTO expectedResponseBody = new TransactionResponseDTO(
                TransactionResult.PROHIBITED.getResult(), TransactionRejectionReason.REGION_CORRELATION.getReason());

        Assertions.assertEquals(responseEntity.getStatusCode(), HttpStatus.OK);
        Assertions.assertEquals(responseEntity.getBody(), expectedResponseBody);
    }

    @Test
    public void testNewTransactionAllowedOneRegionInLastHourIncludingTransactionRegion() {
        String cardNumber = "4000008449433403";
        LocalDateTime transactionTimestampMinusOneHour = LocalDateTime.of(2022, 1, 22, 15, 4);
        LocalDateTime transactionTimestamp = LocalDateTime.of(2022, 1, 22, 16, 4);
        String ip = "192.168.1.1";
        ArrayList<String> transactionsInLastHourByRegion = new ArrayList<>(Arrays.asList(Region.HIC.getRegion(), Region.EAP.getRegion()));
        when(transactionRepository.findByNumberWithinOneHourOfTransactionGroupedByRegion(
                cardNumber, transactionTimestampMinusOneHour, transactionTimestamp)).thenReturn(transactionsInLastHourByRegion);

        TransactionEntity transaction = createNewTransactionEntity(maxAllowed, ip, cardNumber, Region.EAP.getRegion(), transactionTimestamp);

        ResponseEntity<?> responseEntity = transactionService.processTransaction(transaction);

        TransactionResponseDTO expectedResponseBody = new TransactionResponseDTO(
                TransactionResult.ALLOWED.getResult(), NONE);

        Assertions.assertEquals(responseEntity.getStatusCode(), HttpStatus.OK);
        Assertions.assertEquals(responseEntity.getBody(), expectedResponseBody);
    }

    @Test
    public void testNewTransactionAllowedOneRegionInLastHourNotIncludingTransactionRegion() {
        String cardNumber = "4000008449433403";
        LocalDateTime transactionTimestampMinusOneHour = LocalDateTime.of(2022, 1, 22, 15, 4);
        LocalDateTime transactionTimestamp = LocalDateTime.of(2022, 1, 22, 16, 4);
        String ip = "192.168.1.1";
        ArrayList<String> transactionsInLastHourByRegion = new ArrayList<>(Collections.singletonList(Region.HIC.getRegion()));
        when(transactionRepository.findByNumberWithinOneHourOfTransactionGroupedByRegion(
                cardNumber, transactionTimestampMinusOneHour, transactionTimestamp)).thenReturn(transactionsInLastHourByRegion);

        TransactionEntity transaction = createNewTransactionEntity(maxAllowed, ip, cardNumber, Region.EAP.getRegion(), transactionTimestamp);

        ResponseEntity<?> responseEntity = transactionService.processTransaction(transaction);

        TransactionResponseDTO expectedResponseBody = new TransactionResponseDTO(
                TransactionResult.ALLOWED.getResult(), NONE);

        Assertions.assertEquals(responseEntity.getStatusCode(), HttpStatus.OK);
        Assertions.assertEquals(responseEntity.getBody(), expectedResponseBody);
    }

    private TransactionEntity createNewTransactionEntity(Long amount, String ip, String cardNumber, String region, LocalDateTime dateTime) {
        TransactionEntity transaction = new TransactionEntity();
        transaction.setAmount(amount);
        transaction.setIp(ip);
        transaction.setNumber(cardNumber);
        transaction.setRegion(region);
        transaction.setDate(dateTime);
        return transaction;
    }
}