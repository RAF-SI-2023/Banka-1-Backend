package rs.edu.raf.banka1.configuration.authproviders;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import rs.edu.raf.banka1.exceptions.ForbiddenException;
import rs.edu.raf.banka1.model.Employee;
import rs.edu.raf.banka1.repositories.EmployeeRepository;

@Aspect
@Component
public class CurrentAuthAspect {

    private final EmployeeRepository employeeService;

    public CurrentAuthAspect(EmployeeRepository employeeService) {
        this.employeeService = employeeService;
    }

    @Before("@annotation(CurrentAuth) && args(.., @CurrentAuth currentAuth)")
    public void injectCurrentAuth(JoinPoint joinPoint, Employee currentAuth) {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails userDetails) {
            currentAuth = employeeService.findByEmail(userDetails.getUsername()).orElseThrow(ForbiddenException::new);
        }
    }
}
