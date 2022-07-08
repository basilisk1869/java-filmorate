package ru.yandex.practicum.filmorate.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.junit.jupiter.api.Assertions.assertNotNull;

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

    MockHttpServletRequestBuilder makePostRequest(String suffix, String json) {
        return makeRequest("POST", path + suffix, json);
    }

    MockHttpServletRequestBuilder makePutRequest(String suffix, String json) {
        return makeRequest("PUT", path + suffix, json);
    }

    MockHttpServletRequestBuilder makeDeleteRequest(String suffix) {
        return makeRequest("DELETE", path + suffix, null);
    }

    MockHttpServletRequestBuilder makeGetRequest(String suffix) {
        return makeRequest("GET", path + suffix, null);
    }

    MockHttpServletRequestBuilder makeRequest(String method, String path, String json) {
        MockHttpServletRequestBuilder request = null;
        switch (method) {
            case "GET":
                request = MockMvcRequestBuilders.get(path);
                break;
            case "POST":
                request = MockMvcRequestBuilders.post(path);
                break;
            case "PUT":
                request = MockMvcRequestBuilders.put(path);
                break;
            case "DELETE":
                request = MockMvcRequestBuilders.delete(path);
                break;
        }
        assertNotNull(request);
        request.accept(MediaType.APPLICATION_JSON);
        if (json != null) {
            request.contentType(MediaType.APPLICATION_JSON).content(json);
        }
        return request;
    }

}

