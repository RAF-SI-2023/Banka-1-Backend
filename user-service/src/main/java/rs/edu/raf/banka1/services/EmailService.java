package rs.edu.raf.banka1.services;

public interface EmailService {
    Boolean sendEmail(String to, String subject, String body);
}
