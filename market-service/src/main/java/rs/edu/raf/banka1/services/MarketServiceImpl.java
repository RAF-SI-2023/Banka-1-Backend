package rs.edu.raf.banka1.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MarketServiceImpl implements MarketService {

    private ExampleMapper exampleMapper;

    @Autowired
    public MarketServiceImpl(ExampleMapper exampleMapper) {
        this.exampleMapper = exampleMapper;
    }

    @Override
    public int exampleMethod() {
        return 0;
    }
}
