package rs.edu.raf.banka1.mapper;

import org.springframework.stereotype.Component;
import rs.edu.raf.banka1.model.OptionsModel;
import rs.edu.raf.banka1.model.dtos.OptionsDto;
@Component
public class OptionsMapper {
    public OptionsDto optionsModelToOptionsDto(OptionsModel optionsModel) {
        OptionsDto optionsDto = new OptionsDto();
        optionsDto.setOptionType(optionsModel.getOptionType());
        optionsDto.setCurrency(optionsModel.getCurrency());
        optionsDto.setTicker(optionsModel.getTicker());
        optionsDto.setOpenInterest(optionsModel.getOpenInterest());
        optionsDto.setStrikePrice(optionsModel.getStrikePrice());
        optionsDto.setExpirationDate(optionsModel.getExpirationDate());
        optionsDto.setImpliedVolatility(optionsModel.getImpliedVolatility());

        return optionsDto;
    }

    public OptionsModel optionsDtoToOptionsModel(OptionsDto optionsDto) {
        OptionsModel optionsModel = new OptionsModel();
        optionsModel.setOptionType(optionsDto.getOptionType());
        optionsModel.setCurrency(optionsDto.getCurrency());
        optionsModel.setTicker(optionsDto.getTicker());
        optionsModel.setOpenInterest(optionsDto.getOpenInterest());
        optionsModel.setStrikePrice(optionsDto.getStrikePrice());
        optionsModel.setExpirationDate(optionsDto.getExpirationDate());
        optionsModel.setImpliedVolatility(optionsDto.getImpliedVolatility());

        return optionsModel;
    }
}
