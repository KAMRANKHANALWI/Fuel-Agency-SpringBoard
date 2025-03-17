package com.faos.Booking.services;

import com.faos.Booking.models.Bill;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailSenderService {

    private static final Logger log = LoggerFactory.getLogger(EmailSenderService.class);
    private final JavaMailSender javaMailSender;
    private final BillPdfService billPdfService; // Inject PDF service

    /**
     * Send a simple text email.
     */
    public void sendEmail(String toEmail, String subject, String body) {
        if (toEmail == null || toEmail.isBlank()) {
            log.warn("Cannot send email: recipient address is null or blank.");
            return;
        }

        try {
            SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
            simpleMailMessage.setTo(toEmail);
            simpleMailMessage.setSubject(subject);
            simpleMailMessage.setText(body);

            javaMailSender.send(simpleMailMessage);
            log.info("Email sent successfully to {}", toEmail);

        } catch (Exception e) {
            log.error("Failed to send email to {}. Reason: {}", toEmail, e.getMessage(), e);
        }
    }

    /**
     * Send an email with a PDF invoice attachment.
     */
    public void sendEmailWithAttachment(Bill bill) {
        if (bill == null || bill.getBooking() == null || bill.getBooking().getCustomer() == null) {
            log.warn("Cannot send email: Bill, booking, or customer details are missing.");
            return;
        }

        String toEmail = bill.getBooking().getCustomer().getEmail();
        if (toEmail == null || toEmail.isBlank()) {
            log.warn("Cannot send email: recipient email is missing.");
            return;
        }

        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo(toEmail);
            helper.setSubject("Fuel Agency - Invoice #" + bill.getBillId());
            helper.setText("Dear " + bill.getBooking().getCustomer().getFirstName() + ",\n\n"
                    + "Please find attached the invoice for your recent fuel booking.\n\n"
                    + "Best Regards,\nFuel Agency Team");

            // Generate PDF invoice
            byte[] pdfBytes = billPdfService.generateBillPdf(bill);
            helper.addAttachment("Invoice_" + bill.getBillId() + ".pdf", new ByteArrayResource(pdfBytes));

            javaMailSender.send(message);
            log.info("Invoice email sent successfully to {}", toEmail);

        } catch (Exception e) {
            log.error("Failed to send invoice email to {}. Reason: {}", toEmail, e.getMessage(), e);
        }
    }

    // New method for sending confirmation mail with invoice
    public void sendEmailWithAttachment(String toEmail,
                                        String subject,
                                        String body,
                                        String filename,
                                        byte[] attachmentData) {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo(toEmail);
            helper.setSubject(subject);
            helper.setText(body);

            // Attach the PDF bytes
            helper.addAttachment(filename, new ByteArrayResource(attachmentData));

            javaMailSender.send(message);
            log.info("Email with attachment sent successfully to {}", toEmail);
        } catch (Exception e) {
            log.error("Failed to send email with attachment to {}. Reason: {}", toEmail, e.getMessage(), e);
        }
    }

}
