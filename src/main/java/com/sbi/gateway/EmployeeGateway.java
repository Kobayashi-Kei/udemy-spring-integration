package com.sbi.gateway;

import com.sbi.model.Employee;
import org.springframework.integration.annotation.Gateway;
import org.springframework.messaging.Message;
import org.springframework.integration.annotation.MessagingGateway;

@MessagingGateway
public interface EmployeeGateway {

    // ### Service Activator ###
    // Get call
    @Gateway(requestChannel = "request-emp-name-channel")
    public String getEmployeeName (String name);

    // Post call
    @Gateway(requestChannel = "request-hire-emp-channel")
    public Message<Employee> hireEmployee(Employee employee);

    // ### for Transformer ###
    @Gateway(requestChannel = "emp-status-processing-channel")
    public String processEmployeeStatus(String name);

    // ### for Transformer ###
    @Gateway(requestChannel = "word-processing-channel")
    public String processSentence(String sentence);

    // ### for Splitter ###
    @Gateway(requestChannel = "emp-managers-channel")
    public String getManagerList(String managers);

    // ### for Filter ###
    @Gateway(requestChannel = "dev-emp-channel")
    public String getEmployeeIfADeveloper(String empDesignation);


}
