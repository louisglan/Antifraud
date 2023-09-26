package hyperskill.antifraud.integration;

import hyperskill.antifraud.controller.TransactionController;
import hyperskill.antifraud.dto.TransactionResponseDTO;
import hyperskill.antifraud.model.database.TransactionEntity;
import hyperskill.antifraud.repository.TransactionRepository;
import hyperskill.antifraud.service.TransactionService;
import hyperskill.antifraud.service.enums.Region;
import hyperskill.antifraud.service.enums.TransactionResult;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
public class NewTransactionIntegrationTests {
    @Autowired
    TransactionController transactionController;

    @Autowired
    ResourceLoader resourceLoader;

    @Autowired
    MockMvc mockMvc;

    @Test
    public void testPostRequestSuccess() throws Exception {
        Resource resource = resourceLoader.getResource("classpath:transactionRequestSuccessAllowed.json");
        String requestBody = new String(resource.getInputStream().readAllBytes());
        mockMvc.perform(post("/api/antifraud/transaction")
                        .content(requestBody).contentType("application/json"))
                .andExpect(status().is(200))
                .andExpect(content().json("{\"result\":\"ALLOWED\",\"info\":\"none\"}"));
    }

    @Test
    public void testPostRequestFailureAmountBelowZero() throws Exception {
        Resource resource = resourceLoader.getResource("classpath:transactionRequestAmountBelowZero.json");
        String requestBody = new String(resource.getInputStream().readAllBytes());
        mockMvc.perform(post("/api/antifraud/transaction")
                        .content(requestBody).contentType("application/json"))
                .andExpect(status().is(400));
    }
}
