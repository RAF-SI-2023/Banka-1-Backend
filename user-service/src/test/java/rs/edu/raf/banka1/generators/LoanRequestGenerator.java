package rs.edu.raf.banka1.generators;

import rs.edu.raf.banka1.model.LoanRequest;
import rs.edu.raf.banka1.model.LoanRequestStatus;

import java.util.Random;

import static rs.edu.raf.banka1.generators.LoanGenerators.generateRandomLoanAmount;
import static rs.edu.raf.banka1.generators.LoanGenerators.generateRandomLoanType;

public class LoanRequestGenerator {

    private static final Random random = new Random();

    public static LoanRequest generateLoanRequest() {
        LoanRequest loanRequest = new LoanRequest();
        loanRequest.setLoanType(generateRandomLoanType());
        loanRequest.setLoanAmount(generateRandomLoanAmount());
        loanRequest.setCurrency("RSD");
        loanRequest.setLoanPurpose("Some purpose");
        loanRequest.setMonthlyIncomeAmount(generateRandomIncomeAmount());
        loanRequest.setMonthlyIncomeCurrency("RSD");
        loanRequest.setPermanentEmployee(random.nextBoolean());
        loanRequest.setEmploymentPeriod(generateRandomEmploymentPeriod());
        loanRequest.setLoanTerm(generateRandomLoanTerm());
        loanRequest.setBranchOffice("Branch");
        loanRequest.setPhoneNumber("123456789");
        loanRequest.setAccountNumber("123456789");
        loanRequest.setStatus(LoanRequestStatus.PENDING);
        return loanRequest;
    }

    public static LoanRequest generateLoanRequest(final String curNum) {
        LoanRequest loanRequest = new LoanRequest();
        loanRequest.setLoanType(generateRandomLoanType());
        loanRequest.setLoanAmount(generateRandomLoanAmount());
        loanRequest.setCurrency("RSD");
        loanRequest.setLoanPurpose("Some purpose");
        loanRequest.setMonthlyIncomeAmount(generateRandomIncomeAmount());
        loanRequest.setMonthlyIncomeCurrency("RSD");
        loanRequest.setPermanentEmployee(random.nextBoolean());
        loanRequest.setEmploymentPeriod(generateRandomEmploymentPeriod());
        loanRequest.setLoanTerm(generateRandomLoanTerm());
        loanRequest.setBranchOffice("Branch");
        loanRequest.setPhoneNumber("123456789");
        loanRequest.setAccountNumber(curNum);
        loanRequest.setStatus(LoanRequestStatus.PENDING);
        return loanRequest;
    }

    private static double generateRandomIncomeAmount() {
        return 40000 + 100000 * random.nextDouble();
    }

    private static Long generateRandomEmploymentPeriod() {
        return (long) (12 + (36 - 12) * random.nextDouble());
    }

    private static Long generateRandomLoanTerm() {
        return (long) (24 + (60 - 24) * random.nextDouble());
    }
}
