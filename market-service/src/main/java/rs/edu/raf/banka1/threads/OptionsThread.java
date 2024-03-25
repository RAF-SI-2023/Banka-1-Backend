package rs.edu.raf.banka1.threads;

import rs.edu.raf.banka1.services.OptionsService;

public class OptionsThread implements Runnable{

    private final OptionsService optionsService;

    public OptionsThread(OptionsService optionsService){
        this.optionsService = optionsService;
    }
    @Override
    public void run() {
        valuesForConstantUpdating();
    }
    private void valuesForConstantUpdating(){
        this.optionsService.truncateAndFetch();
    }

}


