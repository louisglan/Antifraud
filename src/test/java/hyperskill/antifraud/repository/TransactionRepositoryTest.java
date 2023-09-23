package hyperskill.antifraud.repository;

import hyperskill.antifraud.model.database.TransactionEntity;
import hyperskill.antifraud.repository.TransactionRepository;
import hyperskill.antifraud.service.enums.Region;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;


@SpringBootTest
public class TransactionRepositoryTest {
    @Autowired
    TransactionRepository transactionRepository;

    LocalDateTime startTimestamp = LocalDateTime.of(2022, 6, 6, 10, 0, 30);
    LocalDateTime endTimestamp = LocalDateTime.of(2022, 6, 6, 11, 0, 30);

    @Test
    @Transactional
    public void testMultipleTransactionsSameRegionWithinStartAndEndTimeReturnedGroupedByRegion() {
        createTransaction(1L, "1.1.1.1", "1000000000000008", Region.EAP.getRegion(), LocalDateTime.of(
                2022, 6, 6, 10, 30, 30));
        createTransaction(1L, "1.1.1.1", "1000000000000008", Region.EAP.getRegion(), LocalDateTime.of(
                2022, 6, 6, 10, 30, 30));
        createTransaction(1L, "1.1.1.1", "1000000000000008", Region.EAP.getRegion(), LocalDateTime.of(
                2022, 6, 6, 10, 30, 30));
        List<String> transactionRegionsWithinLastHour =
                transactionRepository.findByNumberWithinOneHourOfTransactionGroupedByRegion("1000000000000008", startTimestamp, endTimestamp);
        ArrayList<String> expectedRegions = new ArrayList<>(Collections.singletonList(Region.EAP.getRegion()));
        Assertions.assertEquals(expectedRegions, transactionRegionsWithinLastHour);
    }

    @Test
    @Transactional
    public void testMultipleTransactionsDifferentRegionsWithinStartAndEndTimeReturnedGroupedByRegion() {
        createTransaction(1L, "1.1.1.1", "1000000000000008", Region.EAP.getRegion(), LocalDateTime.of(
                2022, 6, 6, 10, 30, 30));
        createTransaction(1L, "1.1.1.1", "1000000000000008", Region.HIC.getRegion(), LocalDateTime.of(
                2022, 6, 6, 10, 30, 30));
        createTransaction(1L, "1.1.1.1", "1000000000000008", Region.SA.getRegion(), LocalDateTime.of(
                2022, 6, 6, 10, 30, 30));
        List<String> transactionRegionsWithinLastHour =
                transactionRepository.findByNumberWithinOneHourOfTransactionGroupedByRegion("1000000000000008", startTimestamp, endTimestamp);
        ArrayList<String> expectedRegions = new ArrayList<>(Arrays.asList(Region.EAP.getRegion(), Region.HIC.getRegion(), Region.SA.getRegion()));
        Assertions.assertEquals(expectedRegions, transactionRegionsWithinLastHour);
    }

    @Test
    @Transactional
    public void testOneTransactionWithinStartAndEndTimeReturnedGroupedByRegion() {
        createTransaction(1L, "1.1.1.1", "1000000000000008", Region.EAP.getRegion(), LocalDateTime.of(
                2022, 6, 6, 10, 30, 30));
        List<String> transactionRegionsWithinLastHour =
                transactionRepository.findByNumberWithinOneHourOfTransactionGroupedByRegion("1000000000000008", startTimestamp, endTimestamp);
        ArrayList<String> expectedRegions = new ArrayList<>(Collections.singletonList(Region.EAP.getRegion()));
        Assertions.assertEquals(expectedRegions, transactionRegionsWithinLastHour);
    }

    @Test
    @Transactional
    public void testOneTransactionOutsideStartAndEndTimeNotReturnedGroupedByRegion() {
        createTransaction(1L, "1.1.1.1", "1000000000000008", Region.EAP.getRegion(), LocalDateTime.of(
                2022, 6, 6, 9, 30, 30));
        List<String> transactionRegionsWithinLastHour =
                transactionRepository.findByNumberWithinOneHourOfTransactionGroupedByRegion("1000000000000008", startTimestamp, endTimestamp);
        Assertions.assertEquals(new ArrayList<>(), transactionRegionsWithinLastHour);
    }

    @Test
    @Transactional
    public void testMultipleTransactionsWithinStartAndEndTimeReturnedGroupedByRegion() {
        createTransaction(1L, "1.1.1.1", "1000000000000008", Region.EAP.getRegion(), LocalDateTime.of(
                2022, 6, 6, 9, 30, 30));
        createTransaction(1L, "1.1.1.1", "1000000000000008", Region.EAP.getRegion(), LocalDateTime.of(
                2022, 6, 6, 9, 30, 30));
        createTransaction(1L, "1.1.1.1", "1000000000000008", Region.HIC.getRegion(), LocalDateTime.of(
                2022, 6, 6, 10, 30, 30));
        createTransaction(1L, "1.1.1.1", "1000000000000008", Region.HIC.getRegion(), LocalDateTime.of(
                2022, 6, 6, 9, 30, 30));
        createTransaction(1L, "1.1.1.1", "1000000000000008", Region.HIC.getRegion(), LocalDateTime.of(
                2022, 6, 6, 9, 30, 30));
        createTransaction(1L, "1.1.1.1", "1000000000000008", Region.SA.getRegion(), LocalDateTime.of(
                2022, 6, 6, 10, 30, 30));
        createTransaction(1L, "1.1.1.1", "1000000000000008", Region.SA.getRegion(), LocalDateTime.of(
                2022, 6, 6, 10, 30, 30));
        createTransaction(1L, "1.1.1.1", "1000000000000008", Region.LAC.getRegion(), LocalDateTime.of(
                2022, 6, 6, 10, 30, 30));
        createTransaction(1L, "1.1.1.1", "1000000000000008", Region.MENA.getRegion(), LocalDateTime.of(
                2022, 6, 6, 9, 30, 30));
        createTransaction(1L, "1.1.1.1", "1000000000000008", Region.MENA.getRegion(), LocalDateTime.of(
                2022, 6, 6, 9, 30, 30));
        createTransaction(1L, "1.1.1.1", "1000000000000008", Region.MENA.getRegion(), LocalDateTime.of(
                2022, 6, 6, 9, 30, 30));
        List<String> transactionRegionsWithinLastHour =
                transactionRepository.findByNumberWithinOneHourOfTransactionGroupedByRegion("1000000000000008", startTimestamp, endTimestamp);
        ArrayList<String> expectedRegions = new ArrayList<>(Arrays.asList(Region.HIC.getRegion(), Region.LAC.getRegion(), Region.SA.getRegion()));
        Assertions.assertEquals(expectedRegions, transactionRegionsWithinLastHour);
    }

    @Test
    @Transactional
    public void testMultipleTransactionsWithinStartAndEndTimeReturnedGroupedByRegionMultipleCardNumbers() {
        createTransaction(1L, "1.1.1.1", "2000000000000006", Region.EAP.getRegion(), LocalDateTime.of(
                2022, 6, 6, 10, 30, 30));
        createTransaction(1L, "1.1.1.1", "2000000000000006", Region.EAP.getRegion(), LocalDateTime.of(
                2022, 6, 6, 10, 30, 30));
        createTransaction(1L, "1.1.1.1", "1000000000000008", Region.HIC.getRegion(), LocalDateTime.of(
                2022, 6, 6, 10, 30, 30));
        createTransaction(1L, "1.1.1.1", "2000000000000006", Region.HIC.getRegion(), LocalDateTime.of(
                2022, 6, 6, 10, 30, 30));
        createTransaction(1L, "1.1.1.1", "2000000000000006", Region.HIC.getRegion(), LocalDateTime.of(
                2022, 6, 6, 10, 30, 30));
        createTransaction(1L, "1.1.1.1", "1000000000000008", Region.SA.getRegion(), LocalDateTime.of(
                2022, 6, 6, 10, 30, 30));
        createTransaction(1L, "1.1.1.1", "2000000000000006", Region.SA.getRegion(), LocalDateTime.of(
                2022, 6, 6, 10, 30, 30));
        createTransaction(1L, "1.1.1.1", "1000000000000008", Region.LAC.getRegion(), LocalDateTime.of(
                2022, 6, 6, 10, 30, 30));
        createTransaction(1L, "1.1.1.1", "2000000000000006", Region.MENA.getRegion(), LocalDateTime.of(
                2022, 6, 6, 10, 30, 30));
        createTransaction(1L, "1.1.1.1", "2000000000000006", Region.MENA.getRegion(), LocalDateTime.of(
                2022, 6, 6, 10, 30, 30));
        createTransaction(1L, "1.1.1.1", "2000000000000006", Region.MENA.getRegion(), LocalDateTime.of(
                2022, 6, 6, 10, 30, 30));
        List<String> transactionRegionsWithinLastHour =
                transactionRepository.findByNumberWithinOneHourOfTransactionGroupedByRegion("1000000000000008", startTimestamp, endTimestamp);
        ArrayList<String> expectedRegions = new ArrayList<>(Arrays.asList(Region.HIC.getRegion(), Region.LAC.getRegion(), Region.SA.getRegion()));
        Assertions.assertEquals(expectedRegions, transactionRegionsWithinLastHour);
    }

    @Test
    @Transactional
    public void testMultipleTransactionsSameIpWithinStartAndEndTimeReturnedGroupedByIp() {
        createTransaction(1L, "1.1.1.1", "1000000000000008", Region.EAP.getRegion(), LocalDateTime.of(
                2022, 6, 6, 10, 30, 30));
        createTransaction(1L, "1.1.1.1", "1000000000000008", Region.EAP.getRegion(), LocalDateTime.of(
                2022, 6, 6, 10, 30, 30));
        createTransaction(1L, "1.1.1.1", "1000000000000008", Region.EAP.getRegion(), LocalDateTime.of(
                2022, 6, 6, 10, 30, 30));
        List<String> transactionIpsWithinLastHour =
                transactionRepository.findByNumberWithinOneHourOfTransactionGroupedByIp("1000000000000008", startTimestamp, endTimestamp);
        ArrayList<String> expectedIps = new ArrayList<>(Collections.singletonList("1.1.1.1"));
        Assertions.assertEquals(expectedIps, transactionIpsWithinLastHour);
    }

    @Test
    @Transactional
    public void testMultipleTransactionsDifferentIpsWithinStartAndEndTimeReturnedGroupedByIp() {
        createTransaction(1L, "1.1.1.0", "1000000000000008", Region.EAP.getRegion(), LocalDateTime.of(
                2022, 6, 6, 10, 30, 30));
        createTransaction(1L, "1.1.1.1", "1000000000000008", Region.EAP.getRegion(), LocalDateTime.of(
                2022, 6, 6, 10, 30, 30));
        createTransaction(1L, "1.1.1.2", "1000000000000008", Region.EAP.getRegion(), LocalDateTime.of(
                2022, 6, 6, 10, 30, 30));
        List<String> transactionIpsWithinLastHour =
                transactionRepository.findByNumberWithinOneHourOfTransactionGroupedByIp("1000000000000008", startTimestamp, endTimestamp);
        ArrayList<String> expectedIps = new ArrayList<>(Arrays.asList("1.1.1.0", "1.1.1.1", "1.1.1.2"));
        Assertions.assertEquals(expectedIps, transactionIpsWithinLastHour);
    }

    @Test
    @Transactional
    public void testOneTransactionWithinStartAndEndTimeReturnedGroupedByIp() {
        createTransaction(1L, "1.1.1.1", "1000000000000008", Region.EAP.getRegion(), LocalDateTime.of(
                2022, 6, 6, 10, 30, 30));
        List<String> transactionIpsWithinLastHour =
                transactionRepository.findByNumberWithinOneHourOfTransactionGroupedByIp("1000000000000008", startTimestamp, endTimestamp);
        ArrayList<String> expectedIps = new ArrayList<>(Collections.singletonList("1.1.1.1"));
        Assertions.assertEquals(expectedIps, transactionIpsWithinLastHour);
    }

    @Test
    @Transactional
    public void testOneTransactionOutsideStartAndEndTimeNotReturnedGroupedByIp() {
        createTransaction(1L, "1.1.1.1", "1000000000000008", Region.EAP.getRegion(), LocalDateTime.of(
                2022, 6, 6, 9, 30, 30));
        List<String> transactionIpsWithinLastHour =
                transactionRepository.findByNumberWithinOneHourOfTransactionGroupedByIp("1000000000000008", startTimestamp, endTimestamp);
        Assertions.assertEquals(new ArrayList<>(), transactionIpsWithinLastHour);
    }

    @Test
    @Transactional
    public void testMultipleTransactionsWithinStartAndEndTimeReturnedGroupedByIp() {
        createTransaction(1L, "1.1.1.0", "1000000000000008", Region.EAP.getRegion(), LocalDateTime.of(
                2022, 6, 6, 9, 30, 30));
        createTransaction(1L, "1.1.1.0", "1000000000000008", Region.EAP.getRegion(), LocalDateTime.of(
                2022, 6, 6, 9, 30, 30));
        createTransaction(1L, "1.1.1.1", "1000000000000008", Region.EAP.getRegion(), LocalDateTime.of(
                2022, 6, 6, 10, 30, 30));
        createTransaction(1L, "1.1.1.1", "1000000000000008", Region.EAP.getRegion(), LocalDateTime.of(
                2022, 6, 6, 9, 30, 30));
        createTransaction(1L, "1.1.1.1", "1000000000000008", Region.EAP.getRegion(), LocalDateTime.of(
                2022, 6, 6, 9, 30, 30));
        createTransaction(1L, "1.1.1.2", "1000000000000008", Region.EAP.getRegion(), LocalDateTime.of(
                2022, 6, 6, 10, 30, 30));
        createTransaction(1L, "1.1.1.2", "1000000000000008", Region.EAP.getRegion(), LocalDateTime.of(
                2022, 6, 6, 10, 30, 30));
        createTransaction(1L, "1.1.1.3", "1000000000000008", Region.EAP.getRegion(), LocalDateTime.of(
                2022, 6, 6, 10, 30, 30));
        createTransaction(1L, "1.1.1.4", "1000000000000008", Region.EAP.getRegion(), LocalDateTime.of(
                2022, 6, 6, 9, 30, 30));
        createTransaction(1L, "1.1.1.4", "1000000000000008", Region.EAP.getRegion(), LocalDateTime.of(
                2022, 6, 6, 9, 30, 30));
        createTransaction(1L, "1.1.1.4", "1000000000000008", Region.EAP.getRegion(), LocalDateTime.of(
                2022, 6, 6, 9, 30, 30));
        List<String> transactionIpsWithinLastHour =
                transactionRepository.findByNumberWithinOneHourOfTransactionGroupedByIp("1000000000000008", startTimestamp, endTimestamp);
        ArrayList<String> expectedIps = new ArrayList<>(Arrays.asList("1.1.1.1", "1.1.1.2", "1.1.1.3"));
        Assertions.assertEquals(expectedIps, transactionIpsWithinLastHour);
    }

    @Test
    @Transactional
    public void testMultipleTransactionsWithinStartAndEndTimeReturnedGroupedByIpMultipleCardNumbers() {
        createTransaction(1L, "1.1.1.0", "2000000000000006", Region.EAP.getRegion(), LocalDateTime.of(
                2022, 6, 6, 10, 30, 30));
        createTransaction(1L, "1.1.1.0", "2000000000000006", Region.EAP.getRegion(), LocalDateTime.of(
                2022, 6, 6, 10, 30, 30));
        createTransaction(1L, "1.1.1.1", "1000000000000008", Region.EAP.getRegion(), LocalDateTime.of(
                2022, 6, 6, 10, 30, 30));
        createTransaction(1L, "1.1.1.1", "2000000000000006", Region.EAP.getRegion(), LocalDateTime.of(
                2022, 6, 6, 10, 30, 30));
        createTransaction(1L, "1.1.1.1", "2000000000000006", Region.EAP.getRegion(), LocalDateTime.of(
                2022, 6, 6, 10, 30, 30));
        createTransaction(1L, "1.1.1.2", "1000000000000008", Region.EAP.getRegion(), LocalDateTime.of(
                2022, 6, 6, 10, 30, 30));
        createTransaction(1L, "1.1.1.2", "2000000000000006", Region.EAP.getRegion(), LocalDateTime.of(
                2022, 6, 6, 10, 30, 30));
        createTransaction(1L, "1.1.1.3", "1000000000000008", Region.EAP.getRegion(), LocalDateTime.of(
                2022, 6, 6, 10, 30, 30));
        createTransaction(1L, "1.1.1.4", "2000000000000006", Region.EAP.getRegion(), LocalDateTime.of(
                2022, 6, 6, 10, 30, 30));
        createTransaction(1L, "1.1.1.4", "2000000000000006", Region.EAP.getRegion(), LocalDateTime.of(
                2022, 6, 6, 10, 30, 30));
        createTransaction(1L, "1.1.1.4", "2000000000000006", Region.EAP.getRegion(), LocalDateTime.of(
                2022, 6, 6, 10, 30, 30));
        List<String> transactionIpsWithinLastHour =
                transactionRepository.findByNumberWithinOneHourOfTransactionGroupedByIp("1000000000000008", startTimestamp, endTimestamp);
        ArrayList<String> expectedIps = new ArrayList<>(Arrays.asList("1.1.1.1", "1.1.1.2", "1.1.1.3"));
        Assertions.assertEquals(expectedIps, transactionIpsWithinLastHour);
    }

    private void createTransaction(Long amount, String ip, String number, String region, LocalDateTime timestamp) {
        TransactionEntity transaction = new TransactionEntity();
        transaction.setAmount(amount);
        transaction.setIp(ip);
        transaction.setNumber(number);
        transaction.setRegion(region);
        transaction.setDate(timestamp);
        transactionRepository.save(transaction);
    }
}
