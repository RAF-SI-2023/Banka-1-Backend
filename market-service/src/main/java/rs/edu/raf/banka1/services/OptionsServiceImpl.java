package rs.edu.raf.banka1.services;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import rs.edu.raf.banka1.mapper.OptionsMapper;
import rs.edu.raf.banka1.model.OptionsModel;
import rs.edu.raf.banka1.model.dtos.OptionsDto;
import rs.edu.raf.banka1.repositories.OptionsRepository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
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

    @Override
    @Cacheable(value = "optionsServiceFindById", key = "#id")
    public Optional<OptionsModel> findById(Long id) {
        return optionsRepository.findById(id);
    }

    @Override
    @Cacheable(value = "optionsServiceAllOptions")
    public List<OptionsModel> getAllOptions(){
        return optionsRepository.findAll();
    }
    @Override
    @Cacheable(value = "optionsServiceAllCallOptions")
    public Optional<List<OptionsModel>> getAllCallOptions(){
        return optionsRepository.getAllCallsOptions();
    }

    @Override
    @Cacheable(value = "optionsServiceFindCallOptionById", key = "#id")
    public Optional<OptionsModel> getCallOptionById(Long id) {
        return optionsRepository.getCallOptionById(id);
    }

    @Override
    @Cacheable(value = "optionsServiceAllPutOptions")
    public Optional<List<OptionsModel>> getAllPutOptions(){
        return optionsRepository.getAllPutsOptions();
    }

    @Override
    @Cacheable(value = "optionsServiceFindPutOptionById", key = "#id")
    public Optional<OptionsModel> getPutOptionById(Long id) {
        return optionsRepository.getPutOptionById(id);
    }

}




