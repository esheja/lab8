package seg3x02.employeeGql.resolvers

import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.graphql.data.method.annotation.MutationMapping
import org.springframework.graphql.data.method.annotation.QueryMapping
import org.springframework.stereotype.Controller
import seg3x02.employeeGql.entity.Employee
import seg3x02.employeeGql.repository.EmployeesRepository
import seg3x02.employeeGql.resolvers.types.CreateEmployeeInput
import java.util.*

@Controller
class EmployeesResolver(private val employeesRepository: EmployeesRepository) {

    @QueryMapping
    fun employees(): List<Employee> {
        return employeesRepository.findAll()
    }

    @QueryMapping
    fun employeeById(@Argument id: String): Employee? {
        return employeesRepository.findById(id).orElse(null)
    }

    @MutationMapping
    fun newEmployee(@Argument("createEmployeeInput") input: CreateEmployeeInput): Employee {
        if (input.name != null &&
            input.dateOfBirth != null &&
            input.city != null &&
            input.salary != null) {
            val employee = Employee(input.name, input.dateOfBirth, input.city, input.salary,
                input.gender, input.email)
            employee.id = UUID.randomUUID().toString()
            employeesRepository.save(employee)
            return employee
        } else {
            throw Exception("Invalid input")
        }
    }

    @MutationMapping
    fun deleteEmployee(@Argument id: String): Boolean {
        employeesRepository.deleteById(id)
        return true
    }

    @MutationMapping
    fun updateEmployee(@Argument id: String,
                       @Argument("createEmployeeInput") input: CreateEmployeeInput): Employee {
        val existing = employeesRepository.findById(id)
            .orElseThrow { Exception("Employee not found") }
        // Employee fields are immutable (val), so build an updated copy and keep the same id
        val updated = existing.copy(
            name = input.name ?: existing.name,
            dateOfBirth = input.dateOfBirth ?: existing.dateOfBirth,
            city = input.city ?: existing.city,
            salary = input.salary ?: existing.salary,
            gender = input.gender ?: existing.gender,
            email = input.email ?: existing.email
        )
        updated.id = existing.id
        employeesRepository.save(updated)
        return updated
    }
}
