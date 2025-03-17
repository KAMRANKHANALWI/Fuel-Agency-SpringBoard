package com.faos.Booking;

import com.faos.Booking.services.EmailSenderService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class BookingApplicationTests {

	@Autowired
	private EmailSenderService emailSenderService;

	@Test
	void contextLoads() {
		emailSenderService.sendEmail("khankamranalwi@gmail.com", "This is a testing email form FAOS", "Body of the mail");
	}

}
