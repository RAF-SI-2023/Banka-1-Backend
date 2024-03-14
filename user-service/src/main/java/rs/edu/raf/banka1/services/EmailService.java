package rs.edu.raf.banka1.services;

public interface EmailService {
    void sendActivationEmail(String to, String subject, String body);
}
