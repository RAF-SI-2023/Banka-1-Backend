package rs.edu.raf.banka1.services.implementations;

import org.springframework.stereotype.Service;
import rs.edu.raf.banka1.dtos.ContractCreateDto;
import rs.edu.raf.banka1.mapper.ContractMapper;
import rs.edu.raf.banka1.model.Contract;
import rs.edu.raf.banka1.services.ContractService;

@Service
public class ContractServiceImpl implements ContractService {

    ContractMapper contractMapper;

    public ContractServiceImpl(ContractMapper contractMapper) {
        this.contractMapper = contractMapper;
    }

    @Override
    public Contract createContract(ContractCreateDto contractCreateDto, Long buyerId) {
//        this.contractMapper.
        return null;
    }
}
