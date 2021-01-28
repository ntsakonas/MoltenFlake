/*
    MoltenFlake - Practicing on the design of a distributed unique identifier generator.

    Copyright (C) 2021, Nick Tsakonas


    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.
    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.
    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

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
