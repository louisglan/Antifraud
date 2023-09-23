package hyperskill.antifraud.controller;

import hyperskill.antifraud.dto.TransactionResponseDTO;
import hyperskill.antifraud.model.database.TransactionEntity;
import hyperskill.antifraud.service.TransactionService;
import hyperskill.antifraud.service.enums.Region;
import hyperskill.antifraud.service.enums.TransactionResult;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@AutoConfigureMockMvc(addFilters = false)
public class TransactionControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ResourceLoader resourceLoader;

    @MockBean
    private TransactionService transactionService;

    private final String NONE = "none";

    @Test
    public void testPostRequestReturnsResponse() throws Exception {
        TransactionEntity transaction = new TransactionEntity();
        transaction.setAmount(150L);
        transaction.setIp("192.168.1.1");
        transaction.setNumber("4000008449433403");
        transaction.setRegion(Region.EAP.getRegion());
        transaction.setDate(LocalDateTime.of(2022, 1, 22, 16, 4));

        Resource resource = resourceLoader.getResource("classpath:transactionRequest.json");
        String requestBody = new String(resource.getInputStream().readAllBytes());
        when(transactionService.processTransaction(transaction)).thenReturn(
                ResponseEntity.ok().body(new TransactionResponseDTO(TransactionResult.ALLOWED.getResult(), NONE)));
        mockMvc.perform(post("/api/antifraud/transaction")
                .content(requestBody).contentType("application/json"))
                .andExpect(status().is(200))
                .andExpect(content().json("{\"result\":\"ALLOWED\",\"info\":\"none\"}"));
    }

}
