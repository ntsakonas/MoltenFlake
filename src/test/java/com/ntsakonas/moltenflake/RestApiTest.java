package com.ntsakonas.moltenflake;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.text.IsEmptyString.emptyString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class RestApiTest {

    @Autowired
    MockMvc mockHost;

    @MockBean
    private UIDGenerator uidGenerator;

    @Test
    public void testApiResponseOnUIDGenerated() throws Exception {
        Mockito.when(uidGenerator.generateUid()).thenReturn(10007852613634048L);
        mockHost.perform(get("/uid"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/plain;charset=UTF-8"))
                .andExpect(content().string("10007852613634048"));
    }

    @Test
    public void testApiResponseOnUIDExhausted() throws Exception {
        Mockito.when(uidGenerator.generateUid())
                .thenThrow(new RuntimeException("The system is overwhelmed and ran out of IDs for this millisecond."));

        mockHost.perform(get("/uid"))
                .andExpect(status().isNoContent())
                .andExpect(content().string(is(emptyString())));
    }
}
