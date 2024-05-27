package rs.edu.raf.banka1.services;

import rs.edu.raf.banka1.dtos.ContractCreateDto;
import rs.edu.raf.banka1.dtos.ContractDto;
import rs.edu.raf.banka1.model.Contract;
import rs.edu.raf.banka1.model.Customer;
import rs.edu.raf.banka1.model.Employee;
import rs.edu.raf.banka1.model.User;

import java.util.List;

public interface ContractService {
    ContractDto createContract(ContractCreateDto contractCreateDto, User buyerId);
    Boolean denyContract(Long contractId, String comment);
    Boolean acceptContract(Long contractId);
    Boolean approveContract(Long contractId);

    List<ContractDto> getAllContractsSupervisor(Employee currentAuth);

    List<ContractDto> getAllContractsCustomer(Customer currentAuth);
}
