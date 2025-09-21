package com.sbi.service;

import com.sbi.model.Employee;
import org.springframework.integration.annotation.*;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

import javax.print.DocFlavor;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

@Service
public class EmployeeService {

    // Get call
    @ServiceActivator(inputChannel = "request-emp-name-channel")
    public void getEmployeename(Message<String> name) {
        MessageChannel replyChannel = (MessageChannel) name.getHeaders().getReplyChannel();
        replyChannel.send(name);
    }

    // Post call
    @ServiceActivator(inputChannel = "request-hire-emp-channel", outputChannel = "process-emp-channel")
    public Message<Employee> hireEmployee(Message<Employee> employee) {
        return employee;
    }

    @ServiceActivator(inputChannel = "process-emp-channel", outputChannel = "get-emp-status-channel")
    public Message<Employee> processEmployee(Message<Employee> employee) {
        employee.getPayload().setEmployeeStatus("Permanent Role");
//        System.out.println(employee.getPayload());
        return employee;
    }

    @ServiceActivator(inputChannel = "get-emp-status-channel")
    public void getEmployeeStatus(Message<Employee> employee) {
        MessageChannel replyChannel = (MessageChannel) employee.getHeaders().getReplyChannel();
        replyChannel.send(employee);
    }

    //### TRANSFORMER ###
    @Transformer(inputChannel = "emp-status-processing-channel", outputChannel = "output-channel")
    public Message<String> ConvertToUppercase(Message<String> message) {
        String payload = message.getPayload();
        Message<String> messageInUppercase = MessageBuilder.withPayload(payload.toUpperCase())
                .copyHeaders(message.getHeaders())
                .build();
        return messageInUppercase;
    }

    @Transformer(inputChannel = "word-processing-channel", outputChannel = "output-channel")
    public Message<String> AppendDolorSignUppercase(Message<String> message) {
        String payload = message.getPayload();
        Message<String> messageInUppercase = MessageBuilder.withPayload("$" + payload + "$")
                .copyHeaders(message.getHeaders())
                .build();
        return messageInUppercase;
    }

    //### SPLITTER ###
//    @Splitter(inputChannel = "emp-managers-channel", outputChannel = "output-channel")
    @Splitter(inputChannel = "emp-managers-channel", outputChannel = "managers-channel")
    public List<Message<String>> splitMessage(Message<String> message) {
        List<Message<String>> messages = new ArrayList<Message<String>>();
        String[] msgSplits = message.getPayload().toString().split(",");

        for (String split: msgSplits) {
            Message<String> msg = MessageBuilder.withPayload(split)
                    .copyHeaders(message.getHeaders())
                    .build();
            messages.add(msg);
        }
        return messages;
    }

    //### AGGREGATOR ###
    @Aggregator(inputChannel = "managers-channel", outputChannel = "output-channel")
    Message<String> getAllManagers(List<Message<String>> messages) {
        StringJoiner joiner = new StringJoiner(" & ", "[", "]");
        for (Message<String> message: messages) {
            joiner.add(message.getPayload());
        }
        String managers = joiner.toString();
        System.out.println("Managers: " + managers);
        Message<String> updateMsg = MessageBuilder.withPayload(managers)
                .build();
        return updateMsg;
    }

    //### FILTER ###
    @Filter(inputChannel = "dev-emp-channel", outputChannel = "output-channel")
    boolean filter(Message<?> message){
        String msg = message.getPayload().toString();
        return  msg.contains("Dev");
    }

    //### ROUTER ###
    @Router(inputChannel = "emp-dept-channel")
    public String getEmployeeDepartment(Message<Employee> message){
        String deptRoute = null;

        switch (message.getPayload().getEmployeeDepartment()) {
            case "SALES":
                deptRoute = "sales-channel";
                break;
            case "MARKETING":
                deptRoute = "marketing-channel";
                break;
        }
        return deptRoute;
    }

    @ServiceActivator(inputChannel = "sales-channel")
    public void getSalesDepartment(Message<Employee> employee) {
        Message<String> message = MessageBuilder.withPayload("SALES DEPARTMENT").build();
        System.out.println("Received message from :" + message.toString());
        MessageChannel replyChannel = (MessageChannel) employee.getHeaders().getReplyChannel();
        replyChannel.send(message);
    }

    @ServiceActivator(inputChannel = "marketing-channel")
    public void getMarketingDepartment(Message<Employee> employee) {
        Message<String> message = MessageBuilder.withPayload("MARKETING DEPARTMENT").build();
        System.out.println("Received message from :" + message.toString());
        MessageChannel replyChannel = (MessageChannel) employee.getHeaders().getReplyChannel();
        replyChannel.send(message);
    }

    // ### COMMON OUTPUT CHANNEL
    @ServiceActivator(inputChannel = "output-channel")
    public void consumeStringMessage(Message<Employee> message) {
        System.out.println("Received message from output channel: " + message.getPayload());
        MessageChannel replyChannel = (MessageChannel) message.getHeaders().getReplyChannel();
        replyChannel.send(message);
    }

}
