package rs.edu.raf.banka1.configuration.authproviders;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class CustomAuthProvider implements AuthenticationProvider {

    private final UserDetailsService employeeDetailsService;
    private final UserDetailsService customerDetailsService;

    public CustomAuthProvider(UserDetailsService employeeDetailsService,
                              UserDetailsService customerDetailsService) {
        this.employeeDetailsService = employeeDetailsService;
        this.customerDetailsService = customerDetailsService;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        try {
            UserDetails customerDetails = this.customerDetailsService.loadUserByUsername(authentication.getName());

            if (customerDetails != null) {
                return new UsernamePasswordAuthenticationToken(customerDetails.getUsername(),
                        null,
                        customerDetails.getAuthorities());
            }
        } catch (UsernameNotFoundException e) {
            try {
                return authenticateEmployee(authentication);
            } catch (UsernameNotFoundException unf){
                throw unf;
            }
        }

        return null;
    }

    private Authentication authenticateEmployee(Authentication authentication) throws UsernameNotFoundException {
        UserDetails employeeDetails = this.employeeDetailsService.loadUserByUsername(authentication.getName());

        if (employeeDetails == null) {
            throw new UsernameNotFoundException("User with email " + authentication.getName() + " cannot be found");
        }

        Authentication auth = new UsernamePasswordAuthenticationToken(employeeDetails.getUsername(),
                    null,
                    employeeDetails.getAuthorities());

        return auth;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
}
