package com.jm.online_store.service.impl;

import com.jm.online_store.enums.ExceptionEnums;
import com.jm.online_store.exception.EmployeeNotFoundException;
import com.jm.online_store.exception.constants.ExceptionConstants;
import com.jm.online_store.model.Employee;
import com.jm.online_store.model.Feedback;
import com.jm.online_store.model.Role;
import com.jm.online_store.model.dto.EmployeeDto;
import com.jm.online_store.repository.EmployeeRepository;
import com.jm.online_store.service.interf.EmployeeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {
    private final EmployeeRepository employeeRepository;
    private final ModelMapper modelMapper;
    private final Type listType = new TypeToken<List<EmployeeDto>>() {}.getType();
    private final Type rolesType = new TypeToken<List<Role>>() {}.getType();

    @Override
    public List<EmployeeDto> findAllEmployees() {
        List<Employee> employees = employeeRepository.findAll();
        return modelMapper.map(employees, listType);
    }

    @Override
    public EmployeeDto findEmployeeById(Long id) {
        Employee employee = employeeRepository.findById(id).orElseThrow(()
                -> new EmployeeNotFoundException(ExceptionEnums.EMPLOYEE.getText() + ExceptionConstants.NOT_FOUND));
        return modelMapper.map(employee, EmployeeDto.class);
    }

    @Override
    public EmployeeDto updateEmployee(EmployeeDto employeeReq) {
        Employee employee = employeeRepository.findById(employeeReq.getId()).orElseThrow(()
                -> new EmployeeNotFoundException(ExceptionEnums.EMPLOYEE.getText() + ExceptionConstants.NOT_FOUND));

        return getEmployeeDto(employeeReq, employee);
    }

    @Override
    public EmployeeDto updateEmployeeById(Long id, EmployeeDto employeeReq) {
        Employee employee = employeeRepository.findById(id).orElseThrow(()
                -> new EmployeeNotFoundException(ExceptionEnums.EMPLOYEE.getText() + ExceptionConstants.NOT_FOUND));
        return getEmployeeDto(employeeReq, employee);
    }


    @Override
    public Employee createEmployee(Employee employeeReq) {
        return employeeRepository.save(employeeReq);
    }

    @Override
    public void deleteEmployeeById(Long id) {
        employeeRepository.findById(id).orElseThrow(()
                -> new EmployeeNotFoundException(ExceptionEnums.EMPLOYEE.getText() + ExceptionConstants.NOT_FOUND));
        employeeRepository.deleteById(id);
    }


    private EmployeeDto getEmployeeDto(EmployeeDto employeeReq, Employee employee) {
        Set<Role> roles = employeeReq.getRoles();
        Set<Role> rolesToSave =  modelMapper.map(roles, rolesType);
        employee.setRoles(rolesToSave);
        return modelMapper.map(employee, EmployeeDto.class);
    }
}
