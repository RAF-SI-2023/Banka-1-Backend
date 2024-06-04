package rs.edu.raf.banka1.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import rs.edu.raf.banka1.mapper.OptionsMapper;
import rs.edu.raf.banka1.model.OptionsModel;
import rs.edu.raf.banka1.model.dtos.OptionsDto;
import rs.edu.raf.banka1.repositories.OptionsRepository;
import rs.edu.raf.banka1.utils.Constants;

import java.io.File;
import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpHeaders;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;


class OptionsServiceImplTest {
    private OptionsServiceImpl optionsService;
    private OptionsRepository optionsRepository;
    private OptionsMapper optionsMapper;


    @BeforeEach
    public void setUp() {
        optionsRepository = mock(OptionsRepository.class);
        optionsMapper = new OptionsMapper();
        optionsService = new OptionsServiceImpl(optionsRepository, optionsMapper);
    }

    @Test
    public void getOptionsByTickerTest_Success(){
        OptionsModel option1 = new OptionsModel();
        option1.setTicker("AAPL");
        OptionsModel option2 = new OptionsModel();
        option2.setTicker("AAPL");


        when(optionsRepository.findByTicker("AAPL")).thenReturn(Optional.of(List.of(option1, option2)));

        List<OptionsDto> options = optionsService.getOptionsByTicker("AAPL");
        verify(optionsRepository, times(1)).findByTicker("AAPL");
        Assertions.assertEquals(2, options.size());
    }

    @Test
    public void getOptionsByTickerTest_EmptyDB_noCrumb(){
        when(optionsRepository.findByTicker("AAPL")).thenReturn(Optional.of(List.of()));

        List<OptionsDto> options = optionsService.getOptionsByTicker("AAPL");
        verify(optionsRepository, times(1)).findByTicker("AAPL");
        Assertions.assertEquals(0, options.size());
    }

    @Test
    public void getOptionsByIdTest(){
        OptionsModel option1 = new OptionsModel();
        option1.setListingId(1l);
        Optional<OptionsModel> optionsModel = Optional.of(option1);
        when(optionsRepository.findById(1l)).thenReturn(optionsModel);
        Optional<OptionsModel> result = optionsService.findById(1l);
        assertEquals(optionsModel,result);
    }

    @Test
    public void getOptionsByIdTest_IdNotFound(){
        when(optionsRepository.findById(1l)).thenReturn(Optional.empty());
        Optional<OptionsModel> result = optionsService.findById(1l);
        assertEquals(Optional.empty(),result);

    }
    @Test
    public void getAllOptionsTest(){
        List<OptionsModel> options = new ArrayList<>();
        OptionsModel option1 = new OptionsModel();
        option1.setTicker("APPL");
        OptionsModel option2 = new OptionsModel();
        option1.setTicker("ORCL");
        options.addAll(List.of(option1,option2));

        when(optionsRepository.findAll()).thenReturn(options);
        List<OptionsModel> optionsModels = optionsService.getAllOptions();
        assertEquals(2,optionsModels.size());
    }

    @Test
    public void getAllCallOptionsTest(){
        OptionsModel option1 = new OptionsModel();
        option1.setTicker("APPL");
        OptionsModel option2 = new OptionsModel();
        option1.setTicker("ORCL");
        List<OptionsModel> optionsList = Arrays.asList(option1, option2);
        Optional<List<OptionsModel>> expectedOptions = Optional.of(optionsList);

        when(optionsRepository.getAllCallsOptions()).thenReturn(expectedOptions);

        Optional<List<OptionsModel>> actualOptions = optionsService.getAllCallOptions();

        assertTrue(actualOptions.isPresent());
        assertEquals(expectedOptions.get(), actualOptions.get());
        verify(optionsRepository, times(1)).getAllCallsOptions();

    }
    @Test
    public void getAllPutOptionsTest(){
        OptionsModel option1 = new OptionsModel();
        option1.setTicker("APPL");
        OptionsModel option2 = new OptionsModel();
        option1.setTicker("ORCL");
        List<OptionsModel> optionsList = Arrays.asList(option1, option2);
        Optional<List<OptionsModel>> expectedOptions = Optional.of(optionsList);

        when(optionsRepository.getAllPutsOptions()).thenReturn(expectedOptions);

        Optional<List<OptionsModel>> actualOptions = optionsService.getAllPutOptions();

        assertTrue(actualOptions.isPresent());
        assertEquals(expectedOptions.get(), actualOptions.get());
        verify(optionsRepository, times(1)).getAllPutsOptions();

    }

    @Test
    public void getCallOptionById(){
        Long id = 1L;
        OptionsModel expectedOption = new OptionsModel();
        Optional<OptionsModel> expectedOptional = Optional.of(expectedOption);

        when(optionsRepository.getCallOptionById(id)).thenReturn(expectedOptional);
        Optional<OptionsModel> actualOptional = optionsService.getCallOptionById(id);
        assertTrue(actualOptional.isPresent());
        assertEquals(expectedOption, actualOptional.get());
        verify(optionsRepository, times(1)).getCallOptionById(id);

    }
    @Test
    public void getPutOptionById(){
        Long id = 1L;
        OptionsModel expectedOption = new OptionsModel();
        Optional<OptionsModel> expectedOptional = Optional.of(expectedOption);

        when(optionsRepository.getPutOptionById(id)).thenReturn(expectedOptional);
        Optional<OptionsModel> actualOptional = optionsService.getPutOptionById(id);
        assertTrue(actualOptional.isPresent());
        assertEquals(expectedOption, actualOptional.get());
        verify(optionsRepository, times(1)).getPutOptionById(id);

    }


}
