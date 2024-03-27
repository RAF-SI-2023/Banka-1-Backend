package rs.edu.raf.banka1.generators;

import rs.edu.raf.banka1.model.Loan;
import rs.edu.raf.banka1.model.LoanType;

import java.time.Instant;
import java.util.Random;

public class LoanGenerators {
    private static final Random random = new Random();

    public static Loan generateLoan() {
        Loan loan = new Loan();
        loan.setLoanType(generateRandomLoanType());
        loan.setAccountNumber(generateRandomAccountNumber());
        loan.setLoanAmount(generateRandomLoanAmount());
        loan.setRepaymentPeriod(generateRandomRepaymentPeriod());
        loan.setNominalInterestRate(generateRandomInterestRate());
        loan.setEffectiveInterestRate(generateRandomInterestRate());
        loan.setAgreementDate(generateRandomInstant());
        loan.setMaturityDate(generateRandomInstant());
        loan.setInstallmentAmount(generateRandomLoanAmount());
        loan.setNextInstallmentDate(generateRandomInstant());
        loan.setRemainingDebt(generateRandomLoanAmount());
        loan.setCurrency("RSD");
        return loan;
    }

    public static Loan generateLoan(final String accountNumber) {
        Loan loan = new Loan();
        loan.setLoanType(generateRandomLoanType());
        loan.setAccountNumber(accountNumber);
        loan.setLoanAmount(generateRandomLoanAmount());
        loan.setRepaymentPeriod(generateRandomRepaymentPeriod());
        loan.setNominalInterestRate(generateRandomInterestRate());
        loan.setEffectiveInterestRate(generateRandomInterestRate());
        loan.setAgreementDate(generateRandomInstant());
        loan.setMaturityDate(generateRandomInstant());
        loan.setInstallmentAmount(generateRandomLoanAmount());
        loan.setNextInstallmentDate(generateRandomInstant());
        loan.setRemainingDebt(generateRandomLoanAmount());
        loan.setCurrency("RSD");
        return loan;
    }

    public static LoanType generateRandomLoanType() {
        LoanType[] loanTypes = LoanType.values();
        return loanTypes[random.nextInt(loanTypes.length)];
    }

    public static String generateRandomAccountNumber() {
        return "123456789";
    }

    public static double generateRandomLoanAmount() {
        return 10000 + (20000 - 10000) * random.nextDouble();
    }

    public static int generateRandomRepaymentPeriod() {
        return random.nextInt(120) + 1;
    }

    public static double generateRandomInterestRate() {
        return 5 + 15 * random.nextDouble();
    }

    public static Long generateRandomInstant() {
        long minDay = Instant.parse("2019-01-01T10:15:30.00Z").getEpochSecond();
        long maxDay = Instant.now().getEpochSecond();
        return minDay + random.nextLong() % (maxDay - minDay);
    }
}
