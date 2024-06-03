package rs.edu.raf.banka1.services.implementations;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import rs.edu.raf.banka1.repositories.MarginTransactionRepository;
import rs.edu.raf.banka1.services.MarginAccountService;

@Service
@RequiredArgsConstructor
public class MarginTransactionServiceImpl implements MarginAccountService {
    private final MarginTransactionRepository marginTransactionRepository;

}
