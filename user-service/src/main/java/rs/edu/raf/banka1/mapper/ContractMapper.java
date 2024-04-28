package rs.edu.raf.banka1.mapper;

import org.springframework.stereotype.Component;
import rs.edu.raf.banka1.dtos.ContractCreateDto;
import rs.edu.raf.banka1.model.Contract;
import rs.edu.raf.banka1.model.User;

@Component
public class ContractMapper {
    public Contract contractCreateDtoToContract(ContractCreateDto contractCreateDto, User buyer) {
        Contract contract = new Contract();
//        contract.setAmount(contractCreateDto.getAmountToBuy());
        return contract;
    }
}
