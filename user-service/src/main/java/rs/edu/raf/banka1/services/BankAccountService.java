package rs.edu.raf.banka1.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rs.edu.raf.banka1.dtos.DevizniRacunDto;
import rs.edu.raf.banka1.model.DevizniRacun;
import rs.edu.raf.banka1.model.StatusRacuna;
import rs.edu.raf.banka1.model.User;
import rs.edu.raf.banka1.repositories.DevizniRacunRepository;
import rs.edu.raf.banka1.responses.UserResponse;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Service
public class BankAccountService {

    private final DevizniRacunRepository devizniRacunRepository;
    private final UserService userService;

    @Autowired
    public BankAccountService(DevizniRacunRepository devizniRacunRepository, UserService userService) {
        this.devizniRacunRepository = devizniRacunRepository;
        this.userService = userService;
    }

    public DevizniRacun getDevizniRacunById(Long id) {
        Optional<DevizniRacun> devizniRacun = devizniRacunRepository.findById(id);
        return devizniRacun.orElse(null);
    }

    public List<DevizniRacun> getAllDevizniRacuni() {
        return devizniRacunRepository.findAll();
    }


}
