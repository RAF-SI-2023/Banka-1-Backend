package rs.edu.raf.banka1.services;

import com.fasterxml.jackson.databind.JsonNode;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import rs.edu.raf.banka1.mapper.OptionsMapper;
import rs.edu.raf.banka1.model.dtos.OptionsDto;
import rs.edu.raf.banka1.repositories.OptionsRepository;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OptionsServiceImpl implements OptionsService {
    private OptionsRepository optionsRepository;
    private OptionsMapper optionsMapper;

    public OptionsServiceImpl(OptionsRepository optionsRepository,
                              OptionsMapper optionsMapper) {
        this.optionsRepository = optionsRepository;
        this.optionsMapper = optionsMapper;
    }

    @Override
    @Cacheable(value = "optionsServiceOptionsByTicker", key = "#ticker")
    public List<OptionsDto> getOptionsByTicker(String ticker) {
        List<OptionsDto> options = this.optionsRepository.findByTicker(ticker).map(optionsModels ->
                        optionsModels.stream()
                                .map(optionsMapper::optionsModelToOptionsDto)
                                .collect(Collectors.toList()))
                .orElse(Collections.emptyList());
        if(options.isEmpty()) {
            // optionally fetch options
        }
        return options;
    }
}



