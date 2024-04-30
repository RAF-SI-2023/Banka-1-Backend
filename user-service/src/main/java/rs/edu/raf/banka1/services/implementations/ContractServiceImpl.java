package rs.edu.raf.banka1.services.implementations;

import org.springframework.stereotype.Service;
import rs.edu.raf.banka1.dtos.ContractCreateDto;
import rs.edu.raf.banka1.dtos.ContractDto;
import rs.edu.raf.banka1.mapper.ContractMapper;
import rs.edu.raf.banka1.model.Contract;
import rs.edu.raf.banka1.model.Customer;
import rs.edu.raf.banka1.model.Employee;
import rs.edu.raf.banka1.model.User;
import rs.edu.raf.banka1.services.ContractService;

import java.util.List;

@Service
public class ContractServiceImpl implements ContractService {

    ContractMapper contractMapper;

    public ContractServiceImpl(ContractMapper contractMapper) {
        this.contractMapper = contractMapper;
    }

    @Override
    public ContractDto createContract(ContractCreateDto contractCreateDto, User buyer) {
        // mora provera za amount !
        this.contractMapper.contractCreateDtoToContract(contractCreateDto, buyer);
        return null;
    }

    @Override
    public Boolean denyContract(Long contractId, String comment) {
        return null;
    }

    @Override
    public Boolean acceptContract(Long contractId) {
        return null;
    }

    @Override
    public List<ContractDto> getAllContractsSupervisor(Employee currentAuth) {
        return null;
    }

    @Override
    public List<ContractDto> getAllContractsCustomer(Customer currentAuth) {
        return null;
    }
}
