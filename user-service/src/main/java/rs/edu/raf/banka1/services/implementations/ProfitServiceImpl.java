package rs.edu.raf.banka1.services.implementations;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import rs.edu.raf.banka1.repositories.StockProfitRepository;
import rs.edu.raf.banka1.services.ProfitService;

@Service
@AllArgsConstructor
public class ProfitServiceImpl implements ProfitService {
    StockProfitRepository stockProfitRepository;
    @Override
    public Double getStockProfitBank() {
        return stockProfitRepository.findAll()
                .stream()
                .map(p -> p.getTransactionProfit())
                .reduce(0.0, Double::sum);
    }

    @Override
    public Double getStockProfitAgent(Long agentId) {
        return stockProfitRepository.findAll()
                .stream()
                .filter(p -> p.getEmployee().getUserId().equals(agentId))
                .map(p -> p.getTransactionProfit())
                .reduce(0.0, Double::sum);
    }
}
