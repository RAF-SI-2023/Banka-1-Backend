package rs.edu.raf.banka1.configuration.authproviders;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
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

    @Before("execution(* *(.., @CurrentAuth (*), ..))")
    public void injectCurrentAuth(JoinPoint joinPoint) throws Throwable {
        Object[] args = joinPoint.getArgs();
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails userDetails) {
            for (int i = 0; i < args.length; i++) {
                if (args[i] instanceof Employee) {
                    args[i] = employeeService.findByEmail(userDetails.getUsername()).orElseThrow(ForbiddenException::new);
                    break;
                }
            }
        }
        ((ProceedingJoinPoint) joinPoint).proceed(args);
    }
}
