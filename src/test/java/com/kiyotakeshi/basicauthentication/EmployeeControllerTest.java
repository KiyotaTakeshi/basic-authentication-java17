package com.kiyotakeshi.basicauthentication;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.doReturn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
@AutoConfigureMockMvc
class EmployeeControllerTest {

    private final String BASE_PATH = "/api/employees/";

    @MockBean
    private EmployeeService employeeService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithMockUser
    void shouldReturnEmployees() throws Exception {

        List<Employee> employees = new ArrayList<>(Arrays.asList(
                new Employee(1, "mike", "sales"),
                new Employee(2, "popcorn", "human resources")
        ));

        doReturn(employees).when(employeeService).getEmployees();

        this.mockMvc.perform(get(BASE_PATH)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json("""
                        [{"id":1,"name":"mike","department":"sales"},{"id":2,"name":"popcorn","department":"human resources"}]
                        """));
    }
}