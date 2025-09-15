package com.sbi.comtroller;

import com.sbi.gateway.EmployeeGateway;
import com.sbi.model.Employee;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/integrate")
public class EmployeeController {
    @Autowired
    public EmployeeGateway employeeGateway;

    // ### Service Activator ###
    @GetMapping(value = "{name}")
    public String getEmployeeNameFromService(@PathVariable("name") String name) {
        return employeeGateway.getEmployeeName(name);
    }

    @PostMapping("/hireEmployee")
    public Employee hireEmployee(@RequestBody Employee employee) {
        Message<Employee> reply = employeeGateway.hireEmployee(employee);
        Employee empResponse = reply.getPayload();
        return empResponse;
    }

    // ### for Transformer ###
    @GetMapping(value = "/processEmployeeStatus/{status}")
    public String processEmployeeStatus(@PathVariable("status") String status) {
        return employeeGateway.processEmployeeStatus(status);
    }

    @GetMapping(value = "/processSentence/{sentence}")
    public String processSentence(@PathVariable("sentence") String sentence) {
        return employeeGateway.processSentence(sentence);
    }

    // ### for Splitter ###
    @GetMapping(value = "/getManagerList/{managers}")
    public String getManagerList(@PathVariable("managers") String managers) {
        return employeeGateway.getManagerList(managers);
    }

    // ### for Filter ###
    @GetMapping(value = "/getEmployeeIfADeveloper/{empDesignation}")
    public String getEmployeeIfADeveloper(@PathVariable("empDesignation") String empDesignation) {
        return employeeGateway.getEmployeeIfADeveloper(empDesignation);
    }


}
