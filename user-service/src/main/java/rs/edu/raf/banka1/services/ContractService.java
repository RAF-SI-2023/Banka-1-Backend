package rs.edu.raf.banka1.services;

import rs.edu.raf.banka1.dtos.ContractCreateDto;
import rs.edu.raf.banka1.model.Contract;

public interface ContractService {

    Contract createContract(ContractCreateDto contractCreateDto, Long buyerId);
}
