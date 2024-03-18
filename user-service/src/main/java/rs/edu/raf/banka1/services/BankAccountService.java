package rs.edu.raf.banka1.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rs.edu.raf.banka1.model.DevizniRacun;
import rs.edu.raf.banka1.repositories.DevizniRacunRepository;

import java.util.List;
import java.util.Optional;

@Service
public class BankAccountService {

    private final DevizniRacunRepository devizniRacunRepository;

    @Autowired
    public BankAccountService(DevizniRacunRepository devizniRacunRepository) {
        this.devizniRacunRepository = devizniRacunRepository;
    }

    public DevizniRacun getDevizniRacunById(String id) {
        Optional<DevizniRacun> devizniRacun = devizniRacunRepository.findById(id);
        return devizniRacun.orElse(null);
    }

    public List<DevizniRacun> getAllDevizniRacuni() {
        return devizniRacunRepository.findAll();
    }

}
