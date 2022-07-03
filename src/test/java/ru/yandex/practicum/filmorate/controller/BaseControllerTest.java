package ru.yandex.practicum.filmorate.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

public class BaseControllerTest {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    protected final String path;

    static protected int expectedId = 1;

    public BaseControllerTest(String path) {
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
