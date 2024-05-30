package rs.edu.raf.banka1.services;

<<<<<<< HEAD
import org.springframework.cache.annotation.Cacheable;
=======
>>>>>>> 4380c86e99aa5b355c1a87acb9cf2b6f4432149f
import rs.edu.raf.banka1.model.OptionsModel;
import rs.edu.raf.banka1.model.dtos.OptionsDto;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

public interface OptionsService {
    List<OptionsDto> getOptionsByTicker(String ticker);
    Optional<OptionsModel> findById(Long id);
    List<OptionsModel> getAllOptions();
<<<<<<< HEAD
    Optional<List<OptionsModel>> getAllCallOptions();
    Optional<OptionsModel> getCallOptionById(Long id);
    Optional<List<OptionsModel>> getAllPutOptions();
    Optional<OptionsModel> getPutOptionById(Long id);
=======
>>>>>>> 4380c86e99aa5b355c1a87acb9cf2b6f4432149f
}
