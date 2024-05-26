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
}
