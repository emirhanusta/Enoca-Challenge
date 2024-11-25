package Enoca_Challenge.controller;

import Enoca_Challenge.dto.request.CustomerRequest;
import Enoca_Challenge.dto.response.CustomerResponse;
import Enoca_Challenge.service.CustomerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class CustomerControllerTest {

    @Mock
    private CustomerService customerService;

    @InjectMocks
    private CustomerController customerController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(customerController).build();
    }

    @Test
    void addCustomer_ShouldReturnCreatedStatus_WhenValidRequest() throws Exception {
        // Arrange
        CustomerResponse response = new CustomerResponse(1L, "John", "johndoe@example.com");

        when(customerService.addCustomer(any(CustomerRequest.class))).thenReturn(response);

        // Act & Assert
        mockMvc.perform(post("/api/v1/customers")
                        .contentType("application/json")
                        .content("{\"name\": \"John\", \"email\": \"johndoe@example.com\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("John"))
                .andExpect(jsonPath("$.email").value("johndoe@example.com"));

        verify(customerService, times(1)).addCustomer(any(CustomerRequest.class));
    }

    @Test
    void addCustomer_ShouldReturnBadRequest_WhenInvalidRequest() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/api/v1/customers")
                        .contentType("application/json")
                        .content("{\"firstName\": \"\", \"lastName\": \"\", \"email\": \"invalid-email\"}"))
                .andExpect(status().isBadRequest());

        verify(customerService, times(0)).addCustomer(any(CustomerRequest.class));
    }
}
