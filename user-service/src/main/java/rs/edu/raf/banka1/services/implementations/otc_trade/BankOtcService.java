package rs.edu.raf.banka1.services.implementations.otc_trade;

import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import rs.edu.raf.banka1.dtos.otc_trade.*;
import rs.edu.raf.banka1.model.listing.BankOTCStock;
import rs.edu.raf.banka1.model.listing.MyStock;
import rs.edu.raf.banka1.model.offer.MyOffer;
import rs.edu.raf.banka1.model.offer.Offer;
import rs.edu.raf.banka1.model.offer.OfferStatus;
import rs.edu.raf.banka1.repositories.BankAccountRepository;
import rs.edu.raf.banka1.repositories.otc_trade.BankOTCStockRepository;
import rs.edu.raf.banka1.repositories.otc_trade.MyOfferRepository;
import rs.edu.raf.banka1.repositories.otc_trade.MyStockRepository;
import rs.edu.raf.banka1.repositories.otc_trade.OfferRepository;
import rs.edu.raf.banka1.services.BankAccountService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BankOtcService {
    private final OfferRepository offerRepository;
    private final MyStockRepository myStockRepository;
    private final BankOTCStockRepository bankOTCStockRepository;
    private final MyOfferRepository myOfferRepository;
    private final BankAccountService bankAccountService;
    private RestTemplate restTemplate = new RestTemplate();

    public void setRestTemplate(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    //URL
    private static final String URL_TO_BANK3 =  "https://banka-3-dev.si.raf.edu.rs/exchange-service/api/v1/otcTrade";
//    private static final String URL_TO_BANK3 =  "http://localhost:8083/api/v1/otcTrade";

    //GET: /getOurStocks
    //dohvatamo sve Stocks koje mi nudimo
    public List<MyStockDto> findAllStocks(){
        List<MyStock> myStocks =  myStockRepository.findAllByCompanyIdAndPublicAmountGreaterThan(1L, 0);
        List<MyStockDto> dtos = new ArrayList<>();
        for(MyStock myStock: myStocks){
            MyStockDto dto = new MyStockDto();
            dto.setAmount(myStock.getAmount());
            dto.setTicker(myStock.getTicker());
            dto.setPublicAmount(myStock.getPublicAmount());
            dtos.add(dto);
        }
        return dtos;
    }

    //POST: /sendOffer/bank
    //primamo ponude od drugih banaka
    public Offer receiveOffer(OfferDto offerDto){
        Offer offer = new Offer();
        offer.setTicker(offerDto.getTicker());
        offer.setAmount(offerDto.getAmount());
        offer.setPrice(offerDto.getPrice());
        offer.setIdBank(offerDto.getIdBank());

        MyStock myStock = myStockRepository.findByTickerAndCompanyId(offer.getTicker(), 1l);

        //provera da li mi imamo taj Stock
        if(myStock != null && myStock.getPublicAmount() >= offer.getAmount() && offer.getAmount() >= 0) {
            offer.setOfferStatus(OfferStatus.PROCESSING);
        } else {
            offer.setOfferStatus(OfferStatus.DECLINED);
        }

        offerRepository.save(offer);
        return offer;
    }

    //POST: /offerAccepted/bank/{id}
    //stize poruka da su nam prihvatili ponudu
    public boolean offerAccepted(Long id){
        Optional<MyOffer> myOfferOptional = myOfferRepository.findById(id);

        if(myOfferOptional.isPresent()){
            MyOffer myOffer = myOfferOptional.get();
            myOffer.setOfferStatus(OfferStatus.ACCEPTED);

            //provera ukoliko taj Stock ne postoji kod nas
            if(myStockRepository.findByTicker(myOffer.getTicker()) == null) {
                MyStock myStock = new MyStock();
                myStock.setTicker(myOffer.getTicker());
                myStock.setCompanyId(1l);
                myStock.setAmount(myOffer.getAmount());
                myStock.setPrivateAmount(0);
                myStock.setPublicAmount(myOffer.getAmount());
                myStock.setCurrencyMark("RSD");
                double minimumPrice = myOffer.getPrice()/myOffer.getAmount();
                myStock.setMinimumPrice(minimumPrice);
                myStockRepository.save(myStock);
            }else {
                MyStock myStock = myStockRepository.findByTickerAndCompanyId(myOffer.getTicker(), 1l);
                myStock.setAmount(myStock.getAmount() + myOffer.getAmount());
                myStock.setPublicAmount(myStock.getPublicAmount() + myOffer.getAmount());
                myStockRepository.save(myStock);
            }

            //TODO: skidamo pare sa naseg racuna
            bankAccountService.removeBalance(bankAccountService.getDefaultBankAccount(), myOffer.getPrice());

            myOfferRepository.save(myOffer);
            return true;
        }

        return false;
    }

    //POST: /offerDeclined/bank/{id}
    //stize poruka da su nam odbili ponudu
    public boolean offerDeclined(Long id){
        Optional<MyOffer> myOfferOptional = myOfferRepository.findById(id);
        if(myOfferOptional.isPresent()){
            MyOffer myOffer = myOfferOptional.get();
            myOffer.setOfferStatus(OfferStatus.DECLINED);
            myOfferRepository.save(myOffer);
            return true;
        }

        return false;
    }

    ///////////////////////FRONTEND/////////////////////////////////////////////////

    public boolean editMyStock(EditMyStockDto stockDto){
        try {
            MyStock stock = myStockRepository.findByTickerAndCompanyId(stockDto.getTicker(), 1L);

            stock.setPublicAmount(stockDto.getPublicAmount());
            stock.setPrivateAmount(stock.getAmount() - stockDto.getPublicAmount());
            stock.setMinimumPrice(stockDto.getPrice());

            myStockRepository.save(stock);
        }catch (Exception e){
            return false;
        }

        return true;
    }

    //GET: /getBanksStocks
    //dohvatamo sve Stocks od drugih banaka
    public List<BankOTCStock> getAllStocksForBanks(){
        return bankOTCStockRepository.findAll();
    }

    //GET: /getOffers
    //pohvatamo sve ponude koje su nam stigle
    public List<Offer> findAllOffers(){
        return offerRepository.findAll();
    }

    //GET: /getOurOffers
    //dohvatamo sve ponude koje smo mi poslali
    public List<MyOffer> getMyOffers(){
        return myOfferRepository.findAll();
    }

    //PUT: /refresh
    //pozivi ka banci 3
    public void getBankStocks(){
        bankOTCStockRepository.deleteAll();
        getStocksFromBank3();
    }

    private void getStocksFromBank3(){
        try {
//            RestTemplate restTemplate = new RestTemplate();
            String url = URL_TO_BANK3 + "/getOurStocks";

            ResponseEntity<List<MyStockDto>> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<MyStockDto>>() {
                    });

            if (response.getStatusCode() == HttpStatus.OK){
                List<MyStockDto> dtos = response.getBody();

                for(MyStockDto myStockDto: dtos){
                    BankOTCStock stock = new BankOTCStock();
                    stock.setTicker(myStockDto.getTicker());
                    stock.setAmount(myStockDto.getAmount());
                    bankOTCStockRepository.save(stock);
                }
            }else {
                System.out.println("ne radi banka 3");
            }
        } catch (Exception e){
            System.out.println("ne radi banka 3");
        }
    }

    //POST: /makeOffer
    //sa frontenda nam stize ponuda koju treba proslediti
    public boolean makeOffer(FrontendOfferDto frontendOfferDto){
        MyOffer myOffer = new MyOffer();
        myOffer.setTicker(frontendOfferDto.getTicker());
        myOffer.setAmount(frontendOfferDto.getAmount());
        myOffer.setPrice(frontendOfferDto.getPrice());
        myOffer.setOfferStatus(OfferStatus.PROCESSING);
        MyOffer myOffer1 = myOfferRepository.save(myOffer);

        MyOfferDto myOfferDto = new MyOfferDto();
        myOfferDto.setTicker(myOffer1.getTicker());
        myOfferDto.setAmount(myOffer1.getAmount());
        myOfferDto.setPrice(myOffer1.getPrice());
        myOfferDto.setIdBank(myOffer1.getMyOfferId());

        String url = URL_TO_BANK3 + "/sendOffer/bank1";

//        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");

        try {
            HttpEntity<MyOfferDto> requestEntity = new HttpEntity<>(myOfferDto, headers);

            ResponseEntity<MyOfferDto> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    requestEntity,
                    new ParameterizedTypeReference<MyOfferDto>() {}
            );

            if (response.getStatusCode() == HttpStatus.OK){
                return true;
            }
        }catch (Exception e){
            return false;
        }

        return false;
    }

    //POST: /acceptOffer/{id}
    //kad mi prihvatamo njihovu ponudu
    public boolean acceptOffer(Long id){
        Optional<Offer> offer = offerRepository.findById(id);
        if(offer.isPresent()){
            Offer offer1 = offer.get();
            offer1.setOfferStatus(OfferStatus.ACCEPTED);
            offerRepository.save(offer1);

            //smanjujemo kolicinu, uzimamo pare
            MyStock myStock = myStockRepository.findByTickerAndCompanyId(offer1.getTicker(), 1l);
            myStock.setAmount(myStock.getAmount() - offer1.getAmount());
            myStock.setPublicAmount(myStock.getPublicAmount() - offer1.getAmount());
            myStockRepository.save(myStock);

            //TODO: dodajemo pare na nas racun
            bankAccountService.addBalance(bankAccountService.getDefaultBankAccount(), offer1.getPrice());

            offerRepository.save(offer1);
            return true;
        }

        return false;
    }

    //POST: /declineOffer/{id}
    //kad mi odbijemo njihovu ponudu
    public boolean declineOffer(Long id){
        Optional<Offer> offer = offerRepository.findById(id);
        if(offer.isPresent()){
            Offer offer1 = offer.get();
            offer1.setOfferStatus(OfferStatus.DECLINED);

            offerRepository.save(offer1);
            return true;
        }

        return false;
    }

    //DELETE: /deleteMyOffer/id
    //kada treba neka nasu ponuda da obrisemo iz baze
    public boolean deleteMyOffer(Long id){
        Optional<MyOffer> myOfferOptional = myOfferRepository.findById(id);
        if(myOfferOptional.isPresent()) {
            MyOffer myOffer = myOfferOptional.get();
            myOfferRepository.delete(myOffer);
            return true;
        }

        return false;
    }

    //DELETE: /deleteOffer/id
    //kada treba neka tudju ponuda da obrisemo iz baze
    public boolean deleteOffer(Long id){
        Optional<Offer> offerOptional = offerRepository.findById(id);
        if(offerOptional.isPresent()) {
            Offer offer = offerOptional.get();
            offerRepository.delete(offer);
            return true;
        }

        return false;
    }

    @Scheduled(fixedRate = 10000)
    private void sendAcceptedOffers() {
        List<Offer> offers = offerRepository.findAllByOfferStatus(OfferStatus.ACCEPTED);
        if (!offers.isEmpty()) {
            for (Offer offer : offers) {
                String url = URL_TO_BANK3 + "/offerAccepted/bank1/" + offer.getIdBank();

                try {
//                    RestTemplate restTemplate = new RestTemplate();
                    ResponseEntity<String> response = restTemplate.exchange(
                            url,
                            HttpMethod.POST,
                            new HttpEntity<>(offer),
                            new ParameterizedTypeReference<String>() {
                            });

                    if (response.getStatusCode() == HttpStatus.OK) {
                        //zavrsavamo sa ponudom
                        offer.setOfferStatus(OfferStatus.FINISHED_ACCEPTED);
                        offerRepository.save(offer);
                    }
                } catch (Exception e) {
                    return;
                }
            }
        }
    }

    @Scheduled(fixedRate = 10000)
    private void sendDeclinedOffers(){
        List<Offer> offers = offerRepository.findAllByOfferStatus(OfferStatus.DECLINED);
        if(!offers.isEmpty()){
            for(Offer offer : offers){
                String url = URL_TO_BANK3 + "/offerDeclined/bank1/" + offer.getIdBank();
                try {
//                    RestTemplate restTemplate = new RestTemplate();
                    ResponseEntity<String> response = restTemplate.exchange(
                            url,
                            HttpMethod.POST,
                            new HttpEntity<>(offer),
                            new ParameterizedTypeReference<String>() {
                            });

                    if (response.getStatusCode() == HttpStatus.OK){
                        //zavrsavamo sa ponudom
                        offer.setOfferStatus(OfferStatus.FINISHED_DECLINED);
                        offerRepository.save(offer);
                    }
                }catch (Exception e){
                    return;
                }
            }
        }
    }

}
