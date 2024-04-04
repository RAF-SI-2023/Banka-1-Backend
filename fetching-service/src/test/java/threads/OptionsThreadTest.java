package threads;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import rs.edu.raf.banka1.services.OptionsService;
import rs.edu.raf.banka1.threads.OptionsThread;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

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

