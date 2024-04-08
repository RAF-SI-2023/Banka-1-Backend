package rs.edu.raf.banka1.mapper;

import org.springframework.stereotype.Component;
import rs.edu.raf.banka1.dtos.LimitDto;
import rs.edu.raf.banka1.dtos.NewLimitDto;
import rs.edu.raf.banka1.model.Employee;
@Component
public class LimitMapper {
    public LimitDto toLimitDto(Employee employee) {
        LimitDto limitDto = new LimitDto();
        limitDto.setLimit(employee.getOrderlimit());
        limitDto.setUsedLimit(employee.getLimitNow());
        limitDto.setEmail(employee.getEmail());
        limitDto.setApprovalRequired(employee.getRequireApproval());
        return limitDto;
    }

    public NewLimitDto toNewLimitDto(Employee employee) {
        NewLimitDto newLimitDto = new NewLimitDto();
        newLimitDto.setUserId(employee.getUserId());
        newLimitDto.setLimit(employee.getOrderlimit());
        newLimitDto.setApprovalRequired(employee.getRequireApproval());
        return newLimitDto;
    }
}
