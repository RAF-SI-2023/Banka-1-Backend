package rs.edu.raf.banka1.services;

import rs.edu.raf.banka1.model.dtos.OptionsDto;

import java.util.List;

public interface OptionsService {
    List<OptionsDto> getOptionsByTicker(String ticker);
}
