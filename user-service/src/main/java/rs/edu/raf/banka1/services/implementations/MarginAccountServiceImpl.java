package rs.edu.raf.banka1.services.implementations;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import rs.edu.raf.banka1.repositories.MarginAccountRepository;
import rs.edu.raf.banka1.services.MarginAccountService;

@Service
@RequiredArgsConstructor
public class MarginAccountServiceImpl implements MarginAccountService {
    private final MarginAccountRepository marginAccountRepository;
}