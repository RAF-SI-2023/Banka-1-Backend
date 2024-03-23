package rs.edu.raf.banka1.services;

public interface EmailService {
    Boolean sendActivationEmail(String to, String subject, String body);
}
