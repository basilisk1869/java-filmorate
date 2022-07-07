package ru.yandex.practicum.filmorate.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

public class BaseControllerTest {

    protected final MockMvc mockMvc;

    protected final ObjectMapper objectMapper;

    protected final String path;

    static protected long expectedId = 1;

    public BaseControllerTest(MockMvc mockMvc, ObjectMapper objectMapper, String path) {
        this.mockMvc = mockMvc;
        this.objectMapper = objectMapper;
        this.path = path;
    }

    MockHttpServletRequestBuilder makeGetRequest() {
        return MockMvcRequestBuilders.get(path)
                .accept(MediaType.APPLICATION_JSON);
    }

    MockHttpServletRequestBuilder makePostRequest(String json) {
        return MockMvcRequestBuilders.post(path)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json);
    }

    MockHttpServletRequestBuilder makePutRequest(String json) {
        return MockMvcRequestBuilders.put(path)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json);
    }

    @BeforeAll
    static void beforeAll() {
        expectedId = 1;
    }
}
