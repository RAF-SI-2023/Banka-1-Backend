package rs.edu.raf.banka1.threads;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import rs.edu.raf.banka1.services.OptionsService;

import org.mockito.MockitoAnnotations;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;

class OptionsThreadTest {

    @Mock
    private OptionsService optionsService;
    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testRun() {
        OptionsThread optionsThread = new OptionsThread(optionsService);

        optionsThread.run();

        verify(optionsService, times(1)).truncateAndFetch();
    }

}