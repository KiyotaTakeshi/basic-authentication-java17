package com.kiyotakeshi.basicauthentication;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    @Override
    public List<Employee> getEmployees() {
        return new ArrayList(Arrays.asList(
                new Employee(1, "mike", "sales"),
                new Employee(2, "popcorn", "human resources")
        ));
    }
}
