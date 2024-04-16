package rs.edu.raf.banka1.services;

import com.mysql.cj.protocol.Message;
import com.mysql.cj.protocol.MessageSender;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.testcontainers.shaded.org.checkerframework.checker.units.qual.C;
import rs.edu.raf.banka1.dtos.ExchangeRateDto;
import rs.edu.raf.banka1.dtos.TransferDto;
import rs.edu.raf.banka1.mapper.TransferMapper;
import rs.edu.raf.banka1.repositories.BankAccountRepository;
import rs.edu.raf.banka1.repositories.CurrencyRepository;
import rs.edu.raf.banka1.repositories.TransferRepository;
import rs.edu.raf.banka1.requests.CreateTransferRequest;
import rs.edu.raf.banka1.services.implementations.TransferServiceImpl;
import rs.edu.raf.banka1.model.*;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpResponse;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
import java.util.Optional;

import static junit.framework.TestCase.assertNotNull;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class TransferServiceImplTest {
    @Mock
    private BankAccountRepository bankAccountRepository;
    @Mock
    private TransferRepository transferRepository;
    @Mock
    private CurrencyRepository currencyRepository;
    @Mock
    private TransferMapper transferMapper;
    @InjectMocks
    private TransferServiceImpl transferService;

    @Test
    public void testCreateTransferSuccessful() {
        BankAccount senderAccount = new BankAccount();
        BankAccount recipientAccount = new BankAccount();

        when(bankAccountRepository.findBankAccountByAccountNumber("123")).thenReturn(Optional.of(senderAccount));
        when(bankAccountRepository.findBankAccountByAccountNumber("456")).thenReturn(Optional.of(recipientAccount));

        CreateTransferRequest request = new CreateTransferRequest();
        request.setSenderAccountNumber("123");
        request.setRecipientAccountNumber("456");
        request.setAmount(100.00);

        Long result = transferService.createTransfer(request);

        assertNotEquals(-1L, result);
        verify(transferRepository, times(1)).save(any(Transfer.class));
    }

    @Test
    public void testCreateTransferAccountsNotFound() {
        when(bankAccountRepository.findBankAccountByAccountNumber("1234567890")).thenReturn(Optional.empty());
        when(bankAccountRepository.findBankAccountByAccountNumber("0987654321")).thenReturn(Optional.empty());

        CreateTransferRequest request = new CreateTransferRequest();
        request.setSenderAccountNumber("1234567890");
        request.setRecipientAccountNumber("0987654321");
        request.setAmount(100.00);

        Long transferId = transferService.createTransfer(request);

        assertEquals(-1L, transferId.longValue());
        verify(transferRepository, never()).save(any(Transfer.class));
    }

    @Test
    public void getTranferByIdTestTransferValid(){
        Long transferId = 1L;
        Transfer transfer = new Transfer();
        transfer.setId(transferId);

        TransferDto expectedDto = new TransferDto();
        expectedDto.setId(transferId);

        when(transferRepository.findById(transferId)).thenReturn(Optional.of(transfer));
        when(transferMapper.transferToTransferDto(transfer)).thenReturn(expectedDto);

        TransferDto resultDto = transferService.getTransferById(transferId);
        assertNotNull(resultDto);
        assertEquals(expectedDto.getId(),resultDto.getId());
    }

    @Test
    public void testGetTransferById_NonExistingTransfer() {
        Long invalidPaymentId = 100L;
        when(transferRepository.findById(invalidPaymentId)).thenReturn(Optional.empty());

        TransferDto resultDto  = transferService.getTransferById(invalidPaymentId);

        assertNull(resultDto);
    }

    @Test
    public void testGetAllTransfersForAccountNumberValidAccountWithTransfers() {
        String accountNumber = "123456789";
        BankAccount bankAccount = new BankAccount();
        bankAccount.setAccountNumber(accountNumber);

        Transfer transfer1 = new Transfer();
        transfer1.setId(1l);
        Transfer transfer2 = new Transfer();
        transfer2.setId(2l);
        List<Transfer> transfers = new ArrayList<>();
        transfers.add(transfer1);
        transfers.add(transfer2);
        bankAccount.setTransfers(transfers);

        when(bankAccountRepository.findBankAccountByAccountNumber("123456789")).thenReturn(Optional.of(bankAccount));
        when(transferMapper.transferToTransferDto(any(Transfer.class)))
                .thenAnswer(invocation->{
                    Transfer transfer = invocation.getArgument(0);
                    return new TransferDto();
                });


        List<TransferDto> result = transferService.getAllTransfersForAccountNumber("123456789");


        assertNotNull(result);
        assertEquals(2, result.size());

    }

    @Test
    public void testGetAllTransfersForAccountNumberInvalidAccount() {
        String invalidAccountNumber = "987654321";
        when(bankAccountRepository.findBankAccountByAccountNumber("987654321"))
                .thenReturn(Optional.empty());

        List<TransferDto> result = transferService.getAllTransfersForAccountNumber("nonExistingAccount");

        assertEquals(0, result.size());
    }

    @Test
    public void testGetAllTransfersForAccountNumberWithNoTransfers() {
        BankAccount emptyBankAccount = new BankAccount();
        emptyBankAccount.setTransfers(new ArrayList<>());
        when(bankAccountRepository.findBankAccountByAccountNumber("123456")).thenReturn(Optional.of(emptyBankAccount));

        List<TransferDto> result = transferService.getAllTransfersForAccountNumber("emptyAccount");

        assertEquals(0, result.size());
    }

    @Test
    public void testSuccessfulTransfer() {
        Customer customer = new Customer();
        customer.setUserId(1L);

        Currency currency1 = new Currency();
        currency1.setId(1L);
        currency1.setActive(true);
        currency1.setCurrencyCode("rsd");
        currency1.setFromRSD(1.0);
        currency1.setToRSD(1.0);
        String accountNumber = "1";
        BankAccount senderBankAccount = new BankAccount();
        senderBankAccount.setCustomer(customer);
        senderBankAccount.setAccountNumber(accountNumber);
        senderBankAccount.setCurrency(currency1);
        senderBankAccount.setAvailableBalance(1000.0);
        senderBankAccount.setBalance(1000.0);
        senderBankAccount.setAccountType(AccountType.CURRENT);

        Currency currency2 = new Currency();
        currency2.setId(2L);
        currency2.setActive(true);
        currency2.setCurrencyCode("usd");
        currency2.setFromRSD(1.0);
        currency2.setToRSD(1.0);
        String accountNumber2 = "2";
        BankAccount recipientBankAccount = new BankAccount();
        recipientBankAccount.setCustomer(customer);
        recipientBankAccount.setCurrency(currency2);
        recipientBankAccount.setAccountNumber(accountNumber2);
        recipientBankAccount.setAvailableBalance(1000.0);
        recipientBankAccount.setBalance(1000.0);
        recipientBankAccount.setAccountType(AccountType.FOREIGN_CURRENCY);

        BankAccount bank = new BankAccount();
        bank.setAvailableBalance(1000.0);
        bank.setBalance(1000.0);
        when(bankAccountRepository.findBankByCurrencyCode(anyString()))
                .thenReturn(Optional.of(bank));


        Transfer transfer = new Transfer();
        transfer.setStatus(TransactionStatus.PROCESSING);
        transfer.setId(1l);
        transfer.setAmount(5.0);
        transfer.setSenderBankAccount(senderBankAccount);
        transfer.setRecipientBankAccount(recipientBankAccount);
        when(transferRepository.findById(5L)).thenReturn(Optional.of(transfer));

        transferService.processTransfer(5L);

        assertEquals(transfer.getStatus(), TransactionStatus.COMPLETE);
    }

    @Test
    public void testInsufficentBalanceSender() {
        Customer customer = new Customer();
        customer.setUserId(1L);

        Currency currency1 = new Currency();
        currency1.setId(1L);
        currency1.setActive(true);
        currency1.setCurrencyCode("rsd");
        currency1.setFromRSD(1.0);
        currency1.setToRSD(1.0);
        String accountNumber = "1";
        BankAccount senderBankAccount = new BankAccount();
        senderBankAccount.setCustomer(customer);
        senderBankAccount.setAccountNumber(accountNumber);
        senderBankAccount.setCurrency(currency1);
        senderBankAccount.setAvailableBalance(0.0);
        senderBankAccount.setBalance(0.0);
        senderBankAccount.setAccountType(AccountType.CURRENT);

        Currency currency2 = new Currency();
        currency2.setId(2L);
        currency2.setActive(true);
        currency2.setCurrencyCode("usd");
        currency2.setFromRSD(1.0);
        currency2.setToRSD(1.0);
        String accountNumber2 = "2";
        BankAccount recipientBankAccount = new BankAccount();
        recipientBankAccount.setCustomer(customer);
        recipientBankAccount.setCurrency(currency2);
        recipientBankAccount.setAccountNumber(accountNumber2);
        recipientBankAccount.setAvailableBalance(1000.0);
        recipientBankAccount.setBalance(1000.0);
        recipientBankAccount.setAccountType(AccountType.FOREIGN_CURRENCY);

        BankAccount bank = new BankAccount();
        bank.setAvailableBalance(1000.0);
        bank.setBalance(1000.0);
        when(bankAccountRepository.findBankByCurrencyCode(anyString()))
                .thenReturn(Optional.of(bank));


        Transfer transfer = new Transfer();
        transfer.setStatus(TransactionStatus.PROCESSING);
        transfer.setId(1l);
        transfer.setAmount(5.0);
        transfer.setSenderBankAccount(senderBankAccount);
        transfer.setRecipientBankAccount(recipientBankAccount);
        when(transferRepository.findById(5L)).thenReturn(Optional.of(transfer));

        transferService.processTransfer(5L);

        assertEquals(transfer.getStatus(), TransactionStatus.DENIED);
    }

    @Test
    public void testSameCurrencyOnSenderAndRecipient() {
        Customer customer = new Customer();
        customer.setUserId(1L);

        Currency currency1 = new Currency();
        currency1.setId(1L);
        currency1.setActive(true);
        currency1.setCurrencyCode("rsd");
        currency1.setFromRSD(1.0);
        currency1.setToRSD(1.0);
        String accountNumber = "1";
        BankAccount senderBankAccount = new BankAccount();
        senderBankAccount.setCustomer(customer);
        senderBankAccount.setAccountNumber(accountNumber);
        senderBankAccount.setCurrency(currency1);
        senderBankAccount.setAvailableBalance(1000.0);
        senderBankAccount.setBalance(1000.0);
        senderBankAccount.setAccountType(AccountType.CURRENT);

        String accountNumber2 = "2";
        BankAccount recipientBankAccount = new BankAccount();
        recipientBankAccount.setCustomer(customer);
        recipientBankAccount.setCurrency(currency1);
        recipientBankAccount.setAccountNumber(accountNumber2);
        recipientBankAccount.setAvailableBalance(1000.0);
        recipientBankAccount.setBalance(1000.0);
        recipientBankAccount.setAccountType(AccountType.FOREIGN_CURRENCY);

        BankAccount bank = new BankAccount();
        bank.setAvailableBalance(1000.0);
        bank.setBalance(1000.0);
        when(bankAccountRepository.findBankByCurrencyCode(anyString()))
                .thenReturn(Optional.of(bank));


        Transfer transfer = new Transfer();
        transfer.setStatus(TransactionStatus.PROCESSING);
        transfer.setId(1l);
        transfer.setAmount(5.0);
        transfer.setSenderBankAccount(senderBankAccount);
        transfer.setRecipientBankAccount(recipientBankAccount);
        when(transferRepository.findById(5L)).thenReturn(Optional.of(transfer));

        transferService.processTransfer(5L);

        assertEquals(transfer.getStatus(), TransactionStatus.DENIED);
    }

    @Test
    public void testInvalidAccountType() {
        Customer customer = new Customer();
        customer.setUserId(1L);

        Currency currency1 = new Currency();
        currency1.setId(1L);
        currency1.setActive(true);
        currency1.setCurrencyCode("rsd");
        currency1.setFromRSD(1.0);
        currency1.setToRSD(1.0);
        String accountNumber = "1";
        BankAccount senderBankAccount = new BankAccount();
        senderBankAccount.setCustomer(customer);
        senderBankAccount.setAccountNumber(accountNumber);
        senderBankAccount.setCurrency(currency1);
        senderBankAccount.setAvailableBalance(1000.0);
        senderBankAccount.setBalance(1000.0);
        senderBankAccount.setAccountType(AccountType.CURRENT);

        Currency currency2 = new Currency();
        currency2.setId(2L);
        currency2.setActive(true);
        currency2.setCurrencyCode("usd");
        currency2.setFromRSD(1.0);
        currency2.setToRSD(1.0);
        String accountNumber2 = "2";
        BankAccount recipientBankAccount = new BankAccount();
        recipientBankAccount.setCustomer(customer);
        recipientBankAccount.setCurrency(currency2);
        recipientBankAccount.setAccountNumber(accountNumber2);
        recipientBankAccount.setAvailableBalance(1000.0);
        recipientBankAccount.setBalance(1000.0);
        recipientBankAccount.setAccountType(AccountType.BUSINESS);

        BankAccount bank = new BankAccount();
        bank.setAvailableBalance(1000.0);
        bank.setBalance(1000.0);
        when(bankAccountRepository.findBankByCurrencyCode(anyString()))
                .thenReturn(Optional.of(bank));


        Transfer transfer = new Transfer();
        transfer.setStatus(TransactionStatus.PROCESSING);
        transfer.setId(1l);
        transfer.setAmount(5.0);
        transfer.setSenderBankAccount(senderBankAccount);
        transfer.setRecipientBankAccount(recipientBankAccount);
        when(transferRepository.findById(5L)).thenReturn(Optional.of(transfer));

        transferService.processTransfer(5L);

        assertEquals(transfer.getStatus(), TransactionStatus.DENIED);
    }

    @Test
    public void testDifferentCustomerTransfer() {
        Customer customer = new Customer();
        customer.setUserId(1L);

        Customer customer2 = new Customer();
        customer2.setUserId(2L);

        Currency currency1 = new Currency();
        currency1.setId(1L);
        currency1.setActive(true);
        currency1.setCurrencyCode("rsd");
        currency1.setFromRSD(1.0);
        currency1.setToRSD(1.0);
        String accountNumber = "1";
        BankAccount senderBankAccount = new BankAccount();
        senderBankAccount.setCustomer(customer);
        senderBankAccount.setAccountNumber(accountNumber);
        senderBankAccount.setCurrency(currency1);
        senderBankAccount.setAvailableBalance(1000.0);
        senderBankAccount.setBalance(1000.0);
        senderBankAccount.setAccountType(AccountType.CURRENT);

        Currency currency2 = new Currency();
        currency2.setId(2L);
        currency2.setActive(true);
        currency2.setCurrencyCode("usd");
        currency2.setFromRSD(1.0);
        currency2.setToRSD(1.0);
        String accountNumber2 = "2";
        BankAccount recipientBankAccount = new BankAccount();
        recipientBankAccount.setCustomer(customer2);
        recipientBankAccount.setCurrency(currency2);
        recipientBankAccount.setAccountNumber(accountNumber2);
        recipientBankAccount.setAvailableBalance(1000.0);
        recipientBankAccount.setBalance(1000.0);
        recipientBankAccount.setAccountType(AccountType.BUSINESS);

        BankAccount bank = new BankAccount();
        bank.setAvailableBalance(1000.0);
        bank.setBalance(1000.0);
        when(bankAccountRepository.findBankByCurrencyCode(anyString()))
                .thenReturn(Optional.of(bank));


        Transfer transfer = new Transfer();
        transfer.setStatus(TransactionStatus.PROCESSING);
        transfer.setId(1l);
        transfer.setAmount(5.0);
        transfer.setSenderBankAccount(senderBankAccount);
        transfer.setRecipientBankAccount(recipientBankAccount);
        when(transferRepository.findById(5L)).thenReturn(Optional.of(transfer));

        transferService.processTransfer(5L);

        assertEquals(transfer.getStatus(), TransactionStatus.DENIED);
    }

    @Test
    public void testSeedExchangeRatesSuccessful() throws IOException, InterruptedException {
        HttpClient httpClientMock = mock(HttpClient.class);
        HttpResponse httpResponseMock = mock(HttpResponse.class);
        Currency rsdCurrency = new Currency();
        rsdCurrency.setId(1L);
        rsdCurrency.setCurrencyCode("RSD");
        rsdCurrency.setActive(true);
        rsdCurrency.setCurrencySymbol("RSD");

        transferService.setExchangeRateAPIToken("40f2aa4e59165c6aab10cd02");
        transferService.setExchangeRateApiUrl("https://v6.exchangerate-api.com/v6/");
        transferService.setHttpClient(httpClientMock);

        when(currencyRepository.findCurrencyByCurrencyCode(anyString())).thenReturn(Optional.of(rsdCurrency));
        when(httpClientMock.send(any(), any())).thenReturn(httpResponseMock);
        when(httpResponseMock.statusCode()).thenReturn(200);
        when(httpResponseMock.body()).thenReturn(mockResponseJson);

        // Calling the method under test
        transferService.seedExchangeRates();

        // Verifying repository interactions
        verify(currencyRepository, times(15)).save(any());
    }

    @Test
    public void testSeedExchangeRates_InitRsdCurrencyNoRSDInDatabase() {
        when(currencyRepository.findCurrencyByCurrencyCode("RSD")).thenReturn(Optional.empty());

        transferService.seedExchangeRates();

        verify(currencyRepository, never()).save(any());
    }

    @Test
    public void testGetExchangeRates() {
        Currency currency = new Currency();
        currency.setFromRSD(1.0);
        currency.setToRSD(1.0);
        when(currencyRepository.findCurrencyByCurrencyCode(anyString())).thenReturn(Optional.of(currency));

        List<ExchangeRateDto> res = transferService.getExchangeRates();

        assertEquals(res.size(), 56);
    }

    private final String mockResponseJson = "" +
            "{\n" +
            "    \"result\": \"success\",\n" +
            "    \"documentation\": \"https://www.exchangerate-api.com/docs\",\n" +
            "    \"terms_of_use\": \"https://www.exchangerate-api.com/terms\",\n" +
            "    \"time_last_update_unix\": 1712793601,\n" +
            "    \"time_last_update_utc\": \"Thu, 11 Apr 2024 00:00:01 +0000\",\n" +
            "    \"time_next_update_unix\": 1712880001,\n" +
            "    \"time_next_update_utc\": \"Fri, 12 Apr 2024 00:00:01 +0000\",\n" +
            "    \"base_code\": \"RSD\",\n" +
            "    \"conversion_rates\": {\n" +
            "        \"RSD\": 1,\n" +
            "        \"AED\": 0.03406,\n" +
            "        \"AFN\": 0.6602,\n" +
            "        \"ALL\": 0.8692,\n" +
            "        \"AMD\": 3.6103,\n" +
            "        \"ANG\": 0.0166,\n" +
            "        \"AOA\": 7.7998,\n" +
            "        \"ARS\": 8.0196,\n" +
            "        \"AUD\": 0.01407,\n" +
            "        \"AWG\": 0.0166,\n" +
            "        \"AZN\": 0.01576,\n" +
            "        \"BAM\": 0.0167,\n" +
            "        \"BBD\": 0.01855,\n" +
            "        \"BDT\": 1.0173,\n" +
            "        \"BGN\": 0.0167,\n" +
            "        \"BHD\": 0.003487,\n" +
            "        \"BIF\": 26.5334,\n" +
            "        \"BMD\": 0.009274,\n" +
            "        \"BND\": 0.01247,\n" +
            "        \"BOB\": 0.06412,\n" +
            "        \"BRL\": 0.04646,\n" +
            "        \"BSD\": 0.009274,\n" +
            "        \"BTN\": 0.7715,\n" +
            "        \"BWP\": 0.1268,\n" +
            "        \"BYN\": 0.03012,\n" +
            "        \"BZD\": 0.01855,\n" +
            "        \"CAD\": 0.01258,\n" +
            "        \"CDF\": 25.7629,\n" +
            "        \"CHF\": 0.008374,\n" +
            "        \"CLP\": 8.7389,\n" +
            "        \"CNY\": 0.06706,\n" +
            "        \"COP\": 34.9568,\n" +
            "        \"CRC\": 4.6964,\n" +
            "        \"CUP\": 0.2226,\n" +
            "        \"CVE\": 0.9416,\n" +
            "        \"CZK\": 0.2164,\n" +
            "        \"DJF\": 1.6482,\n" +
            "        \"DKK\": 0.06371,\n" +
            "        \"DOP\": 0.5485,\n" +
            "        \"DZD\": 1.2474,\n" +
            "        \"EGP\": 0.441,\n" +
            "        \"ERN\": 0.1391,\n" +
            "        \"ETB\": 0.5267,\n" +
            "        \"EUR\": 0.00854,\n" +
            "        \"FJD\": 0.02077,\n" +
            "        \"FKP\": 0.007308,\n" +
            "        \"FOK\": 0.06371,\n" +
            "        \"GBP\": 0.007308,\n" +
            "        \"GEL\": 0.02476,\n" +
            "        \"GGP\": 0.007308,\n" +
            "        \"GHS\": 0.1241,\n" +
            "        \"GIP\": 0.007308,\n" +
            "        \"GMD\": 0.6286,\n" +
            "        \"GNF\": 79.5851,\n" +
            "        \"GTQ\": 0.07214,\n" +
            "        \"GYD\": 1.9397,\n" +
            "        \"HKD\": 0.07262,\n" +
            "        \"HNL\": 0.2287,\n" +
            "        \"HRK\": 0.06434,\n" +
            "        \"HTG\": 1.2296,\n" +
            "        \"HUF\": 3.3248,\n" +
            "        \"IDR\": 147.4142,\n" +
            "        \"ILS\": 0.03436,\n" +
            "        \"IMP\": 0.007308,\n" +
            "        \"INR\": 0.7715,\n" +
            "        \"IQD\": 12.1381,\n" +
            "        \"IRR\": 389.1999,\n" +
            "        \"ISK\": 1.2862,\n" +
            "        \"JEP\": 0.007308,\n" +
            "        \"JMD\": 1.4325,\n" +
            "        \"JOD\": 0.006575,\n" +
            "        \"JPY\": 1.4079,\n" +
            "        \"KES\": 1.2052,\n" +
            "        \"KGS\": 0.8266,\n" +
            "        \"KHR\": 37.4436,\n" +
            "        \"KID\": 0.01401,\n" +
            "        \"KMF\": 4.2012,\n" +
            "        \"KRW\": 12.5499,\n" +
            "        \"KWD\": 0.00285,\n" +
            "        \"KYD\": 0.007728,\n" +
            "        \"KZT\": 4.1495,\n" +
            "        \"LAK\": 195.6042,\n" +
            "        \"LBP\": 830.017,\n" +
            "        \"LKR\": 2.7664,\n" +
            "        \"LRD\": 1.7987,\n" +
            "        \"LSL\": 0.1719,\n" +
            "        \"LYD\": 0.04478,\n" +
            "        \"MAD\": 0.09296,\n" +
            "        \"MDL\": 0.1635,\n" +
            "        \"MGA\": 40.418,\n" +
            "        \"MKD\": 0.5249,\n" +
            "        \"MMK\": 19.4581,\n" +
            "        \"MNT\": 31.4961,\n" +
            "        \"MOP\": 0.0748,\n" +
            "        \"MRU\": 0.368,\n" +
            "        \"MUR\": 0.4288,\n" +
            "        \"MVR\": 0.143,\n" +
            "        \"MWK\": 16.0629,\n" +
            "        \"MXN\": 0.1514,\n" +
            "        \"MYR\": 0.04402,\n" +
            "        \"MZN\": 0.5945,\n" +
            "        \"NAD\": 0.1719,\n" +
            "        \"NGN\": 11.5486,\n" +
            "        \"NIO\": 0.3412,\n" +
            "        \"NOK\": 0.09927,\n" +
            "        \"NPR\": 1.2343,\n" +
            "        \"NZD\": 0.01532,\n" +
            "        \"OMR\": 0.003566,\n" +
            "        \"PAB\": 0.009274,\n" +
            "        \"PEN\": 0.03423,\n" +
            "        \"PGK\": 0.03536,\n" +
            "        \"PHP\": 0.5243,\n" +
            "        \"PKR\": 2.5809,\n" +
            "        \"PLN\": 0.0364,\n" +
            "        \"PYG\": 68.2372,\n" +
            "        \"QAR\": 0.03376,\n" +
            "        \"RON\": 0.04243,\n" +
            "        \"RUB\": 0.864,\n" +
            "        \"RWF\": 11.9269,\n" +
            "        \"SAR\": 0.03478,\n" +
            "        \"SBD\": 0.07816,\n" +
            "        \"SCR\": 0.1262,\n" +
            "        \"SDG\": 5.4322,\n" +
            "        \"SEK\": 0.09822,\n" +
            "        \"SGD\": 0.01247,\n" +
            "        \"SHP\": 0.007308,\n" +
            "        \"SLE\": 0.2096,\n" +
            "        \"SLL\": 209.5567,\n" +
            "        \"SOS\": 5.2941,\n" +
            "        \"SRD\": 0.3239,\n" +
            "        \"SSP\": 14.4273,\n" +
            "        \"STN\": 0.2092,\n" +
            "        \"SYP\": 120.4153,\n" +
            "        \"SZL\": 0.1719,\n" +
            "        \"THB\": 0.3365,\n" +
            "        \"TJS\": 0.1015,\n" +
            "        \"TMT\": 0.03242,\n" +
            "        \"TND\": 0.02888,\n" +
            "        \"TOP\": 0.02187,\n" +
            "        \"TRY\": 0.2989,\n" +
            "        \"TTD\": 0.06285,\n" +
            "        \"TVD\": 0.01401,\n" +
            "        \"TWD\": 0.2964,\n" +
            "        \"TZS\": 23.8986,\n" +
            "        \"UAH\": 0.3617,\n" +
            "        \"UGX\": 35.1402,\n" +
            "        \"USD\": 0.009274,\n" +
            "        \"UYU\": 0.3587,\n" +
            "        \"UZS\": 117.5263,\n" +
            "        \"VES\": 0.3356,\n" +
            "        \"VND\": 231.6753,\n" +
            "        \"VUV\": 1.1159,\n" +
            "        \"WST\": 0.02541,\n" +
            "        \"XAF\": 5.6017,\n" +
            "        \"XCD\": 0.02504,\n" +
            "        \"XDR\": 0.006989,\n" +
            "        \"XOF\": 5.6017,\n" +
            "        \"XPF\": 1.0191,\n" +
            "        \"YER\": 2.3191,\n" +
            "        \"ZAR\": 0.1719,\n" +
            "        \"ZMW\": 0.229,\n" +
            "        \"ZWL\": 0.1252\n" +
            "    }\n" +
            "}";

}