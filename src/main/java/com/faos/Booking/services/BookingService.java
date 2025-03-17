//package com.faos.Booking.services;
//
//import com.faos.Booking.exceptions.InvalidBookingOperationException;
//import com.faos.Booking.exceptions.ResourceNotFoundException;
//import com.faos.Booking.models.*;
//import com.faos.Booking.models.enums.*;
//import com.faos.Booking.repositories.*;
//import com.faos.Booking.strategies.SupplierSelectionStrategy;
//import com.faos.Booking.strategies.SupplierStrategyFactory;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//import java.math.BigDecimal;
//import java.time.LocalDate;
//import java.util.List;
//
//@Service
//public class BookingService {
//    private static final Logger logger = LoggerFactory.getLogger(BookingService.class);
//    private final BookingRepository bookingRepository;
//    private final CustomerRepository customerRepository;
//    private final CylinderRepository cylinderRepository;
//    private final SupplierRepository supplierRepository;
//    private final BillRepository billRepository;
//    private final EmailSenderService emailSenderService;
//    private final BillPdfService billPdfService;
//
//    private static final int CYLINDER_LIMIT = 6;
//    private final String strategyType = "MOST_STOCK";
//
//    public BookingService(BookingRepository bookingRepository, CustomerRepository customerRepository,
//                          CylinderRepository cylinderRepository, SupplierRepository supplierRepository,
//                          BillRepository billRepository, EmailSenderService emailSenderService, BillPdfService billPdfService) {
//        this.bookingRepository = bookingRepository;
//        this.customerRepository = customerRepository;
//        this.cylinderRepository = cylinderRepository;
//        this.supplierRepository = supplierRepository;
//        this.billRepository = billRepository;
//        this.emailSenderService = emailSenderService;
//        this.billPdfService = billPdfService;
//    }
//
//    // ✅ Admin: Get all bookings
//    public List<Booking> getAllBooking() {
//        return bookingRepository.findAllWithBill();
//    }
//
//    // ✅ Customer/Admin: Get booking by ID (Customers can only fetch their own bookings)
//    public Booking getBookingById(Long id) {
//        Booking booking = bookingRepository.findById(id)
//                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with ID: " + id));
//        ensureCustomerOrAdminAccess(booking.getCustomer());
//        return booking;
//    }
//
//    // ✅ Get All Booking For Current Customer
//    public List<Booking> getBookingsForCurrentCustomer() {
//        Customer currentCustomer = getCurrentCustomer();
//        return bookingRepository.findByCustomer(currentCustomer);
//    }
//
//    // ✅ Customer: Add a new booking (with dynamic delivery date)
//    @Transactional
//    public Booking addBooking(Booking booking) {
//        Customer customer = getCurrentCustomer();
//        Supplier supplier = selectSupplier();
//        Cylinder cylinder = allocateCylinder(supplier);
//
//        setupBookingDetails(booking, customer, cylinder);
//        // Set dynamic delivery date based on the delivery option
//        setDynamicDeliveryDate(booking);
//        generateBillForBooking(booking);
//        Booking savedBooking = bookingRepository.save(booking);
//
//        sendBookingConfirmationEmail(savedBooking);
//        return savedBooking;
//    }
//
//    // ✅ Customer/Admin: Update booking (Customers can only modify their own bookings)
//    @Transactional
//    public Booking updateBooking(Long id, Booking updatedBooking) {
//        Booking existingBooking = getBookingById(id);
//        ensureCustomerOrAdminAccess(existingBooking.getCustomer());
//
//        existingBooking.setTimeSlot(updatedBooking.getTimeSlot());
//        existingBooking.setDeliveryOption(updatedBooking.getDeliveryOption());
//        existingBooking.setPaymentMode(updatedBooking.getPaymentMode());
//        existingBooking.setPaymentStatus(updatedBooking.getPaymentStatus());
//        existingBooking.setBookingStatus(updatedBooking.getBookingStatus());
//
//        return bookingRepository.save(existingBooking);
//    }
//
//    // ✅ Customer/Admin: Cancel booking (Customers can only cancel their own bookings)
//    @Transactional
//    public Booking cancelBooking(Long bookingId) {
//        Booking booking = getBookingById(bookingId);
//        ensureCustomerOrAdminAccess(booking.getCustomer());
//
//        booking.setBookingStatus(BookingStatus.CANCELLED);
//        releaseCylinder(booking.getCylinder());
//
//        Booking savedBooking = bookingRepository.save(booking);
//        sendBookingCancellationEmail(savedBooking);
//        return savedBooking;
//    }
//
//    // ✅ Customer/Admin: Delete booking (Customers can only delete their own bookings)
//    @Transactional
//    public void deleteBooking(Long id) {
//        Booking booking = getBookingById(id);
//        ensureCustomerOrAdminAccess(booking.getCustomer());
//        bookingRepository.delete(booking);
//    }
//
//    // ✅ Admin: Get all cancelled bookings
//    public List<Booking> getAllCancelledBookings() {
//        return bookingRepository.findByBookingStatus(BookingStatus.CANCELLED);
//    }
//
//    // ✅ Modular Function: Fetch the currently logged-in customer
//    private Customer getCurrentCustomer() {
//        String username = SecurityContextHolder.getContext().getAuthentication().getName();
//        return customerRepository.findByUsername(username)
//                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
//    }
//
//    // ✅ Modular Function: Ensure correct access control
//    private void ensureCustomerOrAdminAccess(Customer customer) {
//        Customer currentUser = getCurrentCustomer();
//        if (!currentUser.getUsername().equals(customer.getUsername()) && !currentUser.getRole().name().equals("ADMIN")) {
//            throw new RuntimeException("Access Denied: You are not authorized to perform this action");
//        }
//    }
//
//    // ✅ Modular Function: Select a supplier based on the defined strategy
//    private Supplier selectSupplier() {
//        List<Supplier> activeSuppliers = supplierRepository.findBySupplierStatus(SupplierStatus.ACTIVE);
//        if (activeSuppliers.isEmpty()) {
//            throw new ResourceNotFoundException("No active suppliers available!");
//        }
//        SupplierSelectionStrategy strategy = SupplierStrategyFactory.getStrategy(strategyType);
//        return strategy.selectSupplier(activeSuppliers);
//    }
//
//    // ✅ Modular Function: Allocate a cylinder from the selected supplier
//    private Cylinder allocateCylinder(Supplier supplier) {
//        return cylinderRepository.findTopBySupplierAndStatus(supplier, CylinderStatus.AVAILABLE)
//                .orElseThrow(() -> new ResourceNotFoundException("No available cylinders for supplier ID: " + supplier.getSupplierId()));
//    }
//
//    // ✅ Modular Function: Release cylinder upon booking cancellation
//    private void releaseCylinder(Cylinder cylinder) {
//        cylinder.setStatus(CylinderStatus.AVAILABLE);
//        cylinderRepository.save(cylinder);
//    }
//
//    // ✅ Modular Function: Set up booking details
//    private void setupBookingDetails(Booking booking, Customer customer, Cylinder cylinder) {
//        booking.setCustomer(customer);
//        booking.setCylinder(cylinder);
//        booking.setBookingDate(LocalDate.now());
//        booking.setCylinderCount(1);
//        booking.setPaymentStatus(PaymentStatus.PAID);
//        booking.setBookingStatus(BookingStatus.CONFIRMED);
//    }
//
//    // ✅ Modular Function: Set dynamic delivery date based on delivery option
//    private void setDynamicDeliveryDate(Booking booking) {
//        logger.info("Validating and setting delivery date based on delivery option: {}", booking.getDeliveryOption());
//        DeliveryOption deliveryOption = booking.getDeliveryOption();
//        if (deliveryOption == null) {
//            logger.error("Delivery option not specified for booking.");
//            throw new InvalidBookingOperationException("Delivery option must be specified.");
//        }
//        switch (deliveryOption) {
//            case STANDARD -> {
//                LocalDate standardDate = booking.getBookingDate().plusDays(3);
//                booking.setDeliveryDate(standardDate);
//                logger.info("Delivery option is STANDARD, setting delivery date to: {}", standardDate);
//            }
//            case EXPRESS -> {
//                LocalDate expressDate = booking.getBookingDate().plusDays(1);
//                booking.setDeliveryDate(expressDate);
//                logger.info("Delivery option is EXPRESS, setting delivery date to: {}", expressDate);
//            }
//            case SAME_DAY -> {
//                booking.setDeliveryDate(booking.getBookingDate());
//                logger.info("Delivery option is SAME_DAY, setting delivery date to booking date: {}", booking.getDeliveryDate());
//            }
//            case SCHEDULED -> {
//                // For scheduled deliveries, we expect the booking to already have a future date set
//                if (booking.getDeliveryDate() == null || booking.getDeliveryDate().isBefore(booking.getBookingDate())) {
//                    logger.error("Invalid scheduled delivery date: {}", booking.getDeliveryDate());
//                    throw new InvalidBookingOperationException("Scheduled delivery requires a valid future date.");
//                }
//                logger.info("Delivery option is SCHEDULED, delivery date is set to: {}", booking.getDeliveryDate());
//            }
//        }
//    }
//
//    // ✅ Modular Function: Generate bill for booking
//    private void generateBillForBooking(Booking booking) {
//        Bill bill = new Bill();
//        bill.setPrice(BigDecimal.valueOf(1000));
//        bill.setGst(BigDecimal.valueOf(50));
//        bill.setDeliveryCharge(BigDecimal.valueOf(100));
//        bill.setTotalPrice(BigDecimal.valueOf(1150));
//        // Set both sides of the bidirectional relationship:
//        booking.setBill(bill);
//        bill.setBooking(booking);
//    }
//
//    // ✅ Modular Function: Send booking confirmation email
//    private void sendBookingConfirmationEmail(Booking booking) {
//        emailSenderService.sendEmail(booking.getCustomer().getEmail(), "Booking Confirmation", "Your booking has been confirmed.");
//    }
//
//    // ✅ Modular Function: Send booking cancellation email
//    private void sendBookingCancellationEmail(Booking booking) {
//        emailSenderService.sendEmail(booking.getCustomer().getEmail(), "Booking Cancelled", "Your booking has been cancelled.");
//    }
//}

package com.faos.Booking.services;

import com.faos.Booking.exceptions.InvalidBookingOperationException;
import com.faos.Booking.exceptions.ResourceNotFoundException;
import com.faos.Booking.models.*;
import com.faos.Booking.models.enums.*;
import com.faos.Booking.repositories.*;
import com.faos.Booking.strategies.SupplierSelectionStrategy;
import com.faos.Booking.strategies.SupplierStrategyFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class BookingService {
    private static final Logger logger = LoggerFactory.getLogger(BookingService.class);
    private final BookingRepository bookingRepository;
    private final CustomerRepository customerRepository;
    private final CylinderRepository cylinderRepository;
    private final SupplierRepository supplierRepository;
    private final BillRepository billRepository;
    private final EmailSenderService emailSenderService;
    private final BillPdfService billPdfService;

    private static final int CYLINDER_LIMIT = 6;
    private final String strategyType = "MOST_STOCK";

    public BookingService(BookingRepository bookingRepository, CustomerRepository customerRepository,
                          CylinderRepository cylinderRepository, SupplierRepository supplierRepository,
                          BillRepository billRepository, EmailSenderService emailSenderService, BillPdfService billPdfService) {
        this.bookingRepository = bookingRepository;
        this.customerRepository = customerRepository;
        this.cylinderRepository = cylinderRepository;
        this.supplierRepository = supplierRepository;
        this.billRepository = billRepository;
        this.emailSenderService = emailSenderService;
        this.billPdfService = billPdfService;
    }

    // ✅ Admin: Get all bookings
    public List<Booking> getAllBooking() {
        return bookingRepository.findAllWithBill();
    }

    // ✅ Customer/Admin: Get booking by ID (Customers can only fetch their own bookings)
    public Booking getBookingById(Long id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with ID: " + id));
        ensureCustomerOrAdminAccess(booking.getCustomer());
        return booking;
    }

    // ✅ Get All Booking For Current Customer
    public List<Booking> getBookingsForCurrentCustomer() {
        Customer currentCustomer = getCurrentCustomer();
        return bookingRepository.findByCustomer(currentCustomer);
    }

    // ✅ Customer: Add a new booking (with dynamic delivery date)
    @Transactional
    public Booking addBooking(Booking booking) {
        Customer customer = getCurrentCustomer();
        Supplier supplier = selectSupplier();
        Cylinder cylinder = allocateCylinder(supplier);

        setupBookingDetails(booking, customer, cylinder);
        // Set dynamic delivery date based on the delivery option
        setDynamicDeliveryDate(booking);
        generateBillForBooking(booking);
        Booking savedBooking = bookingRepository.save(booking);

        sendBookingConfirmationEmail(savedBooking);
        return savedBooking;
    }

    // ✅ Customer/Admin: Update booking (Customers can only modify their own bookings)
    @Transactional
    public Booking updateBooking(Long id, Booking updatedBooking) {
        Booking existingBooking = getBookingById(id);
        ensureCustomerOrAdminAccess(existingBooking.getCustomer());

        existingBooking.setTimeSlot(updatedBooking.getTimeSlot());
        existingBooking.setDeliveryOption(updatedBooking.getDeliveryOption());
        existingBooking.setPaymentMode(updatedBooking.getPaymentMode());
        existingBooking.setPaymentStatus(updatedBooking.getPaymentStatus());
        existingBooking.setBookingStatus(updatedBooking.getBookingStatus());

        return bookingRepository.save(existingBooking);
    }

    // ✅ Customer/Admin: Cancel booking (Customers can only cancel their own bookings)
    @Transactional
    public Booking cancelBooking(Long bookingId) {
        Booking booking = getBookingById(bookingId);
        ensureCustomerOrAdminAccess(booking.getCustomer());

        booking.setBookingStatus(BookingStatus.CANCELLED);
        releaseCylinder(booking.getCylinder());

        Booking savedBooking = bookingRepository.save(booking);
        sendBookingCancellationEmail(savedBooking);
        return savedBooking;
    }

    // ✅ Customer/Admin: Delete booking (Customers can only delete their own bookings)
    @Transactional
    public void deleteBooking(Long id) {
        Booking booking = getBookingById(id);
        ensureCustomerOrAdminAccess(booking.getCustomer());
        bookingRepository.delete(booking);
    }

    // ✅ Admin: Get all cancelled bookings
    public List<Booking> getAllCancelledBookings() {
        return bookingRepository.findByBookingStatus(BookingStatus.CANCELLED);
    }

    // ✅ Modular Function: Fetch the currently logged-in customer
    private Customer getCurrentCustomer() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return customerRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    // ✅ Modular Function: Ensure correct access control
    private void ensureCustomerOrAdminAccess(Customer customer) {
        Customer currentUser = getCurrentCustomer();
        if (!currentUser.getUsername().equals(customer.getUsername()) && !currentUser.getRole().name().equals("ADMIN")) {
            throw new RuntimeException("Access Denied: You are not authorized to perform this action");
        }
    }

    // ✅ Modular Function: Select a supplier based on the defined strategy
    private Supplier selectSupplier() {
        List<Supplier> activeSuppliers = supplierRepository.findBySupplierStatus(SupplierStatus.ACTIVE);
        if (activeSuppliers.isEmpty()) {
            throw new ResourceNotFoundException("No active suppliers available!");
        }
        SupplierSelectionStrategy strategy = SupplierStrategyFactory.getStrategy(strategyType);
        return strategy.selectSupplier(activeSuppliers);
    }

    // ✅ Modular Function: Allocate a cylinder from the selected supplier
    private Cylinder allocateCylinder(Supplier supplier) {
        return cylinderRepository.findTopBySupplierAndStatus(supplier, CylinderStatus.AVAILABLE)
                .orElseThrow(() -> new ResourceNotFoundException("No available cylinders for supplier ID: " + supplier.getSupplierId()));
    }

    // ✅ Modular Function: Release cylinder upon booking cancellation
    private void releaseCylinder(Cylinder cylinder) {
        cylinder.setStatus(CylinderStatus.AVAILABLE);
        cylinderRepository.save(cylinder);
    }

    // ✅ Modular Function: Set up booking details
    private void setupBookingDetails(Booking booking, Customer customer, Cylinder cylinder) {
        booking.setCustomer(customer);
        booking.setCylinder(cylinder);
        booking.setBookingDate(LocalDate.now());
        booking.setCylinderCount(1);
        booking.setPaymentStatus(PaymentStatus.PAID);
        booking.setBookingStatus(BookingStatus.CONFIRMED);
    }

    // ✅ Modular Function: Set dynamic delivery date based on delivery option
    private void setDynamicDeliveryDate(Booking booking) {
        logger.info("Validating and setting delivery date based on delivery option: {}", booking.getDeliveryOption());
        DeliveryOption deliveryOption = booking.getDeliveryOption();
        if (deliveryOption == null) {
            logger.error("Delivery option not specified for booking.");
            throw new InvalidBookingOperationException("Delivery option must be specified.");
        }
        switch (deliveryOption) {
            case STANDARD -> {
                LocalDate standardDate = booking.getBookingDate().plusDays(3);
                booking.setDeliveryDate(standardDate);
                logger.info("Delivery option is STANDARD, setting delivery date to: {}", standardDate);
            }
            case EXPRESS -> {
                LocalDate expressDate = booking.getBookingDate().plusDays(1);
                booking.setDeliveryDate(expressDate);
                logger.info("Delivery option is EXPRESS, setting delivery date to: {}", expressDate);
            }
            case SAME_DAY -> {
                booking.setDeliveryDate(booking.getBookingDate());
                logger.info("Delivery option is SAME_DAY, setting delivery date to booking date: {}", booking.getDeliveryDate());
            }
            case SCHEDULED -> {
                // For scheduled deliveries, we expect the booking to already have a future date set
                if (booking.getDeliveryDate() == null || booking.getDeliveryDate().isBefore(booking.getBookingDate())) {
                    logger.error("Invalid scheduled delivery date: {}", booking.getDeliveryDate());
                    throw new InvalidBookingOperationException("Scheduled delivery requires a valid future date.");
                }
                logger.info("Delivery option is SCHEDULED, delivery date is set to: {}", booking.getDeliveryDate());
            }
        }
    }

    // ✅ Modular Function: Generate bill for booking
    private void generateBillForBooking(Booking booking) {
        Bill bill = new Bill();
        bill.setPrice(BigDecimal.valueOf(1000));
        bill.setGst(BigDecimal.valueOf(50));
        bill.setDeliveryCharge(BigDecimal.valueOf(100));
        bill.setTotalPrice(BigDecimal.valueOf(1150));
        // Set both sides of the bidirectional relationship:
        booking.setBill(bill);
        bill.setBooking(booking);
    }

    // ✅ Modular Function: Send detailed booking confirmation email with invoice attachment
    private void sendBookingConfirmationEmail(Booking booking) {
        if (booking == null || booking.getCustomer() == null) {
            logger.warn("Booking or customer is null; not sending confirmation email.");
            return;
        }
        String email = booking.getCustomer().getEmail();
        String subject = "Booking Confirmation - ID: " + booking.getBookingId();
        String body = "Dear " + booking.getCustomer().getFirstName() + ",\n\n"
                + "Thank you for your booking!\n\n"
                + "Booking ID: " + booking.getBookingId() + "\n"
                + "Booking Date: " + booking.getBookingDate() + "\n"
                + "Delivery Date: " + booking.getDeliveryDate() + "\n"
                + "Payment Status: " + booking.getPaymentStatus() + "\n"
                + "Cylinder Type: " + booking.getCylinder().getCylinderType() + "\n\n"
                + "Please find your invoice attached.\n\n"
                + "Regards,\nFuel Agency, India";
        try {
            // Generate the PDF invoice as a byte array
            byte[] invoicePdf = billPdfService.generateBillPdf(booking.getBill());
            // Send the email with the invoice attached.
            emailSenderService.sendEmailWithAttachment(
                    email,
                    subject,
                    body,
                    "Invoice_" + booking.getBookingId() + ".pdf",
                    invoicePdf
            );
            logger.info("Booking confirmation email with invoice sent successfully to {}", email);
        } catch (Exception e) {
            logger.error("Error sending booking confirmation email with invoice: {}", e.getMessage());
        }
    }

    // ✅ Modular Function: Send detailed booking cancellation email
    private void sendBookingCancellationEmail(Booking booking) {
        if (booking == null || booking.getCustomer() == null) {
            logger.warn("Booking or customer is null; not sending cancellation email.");
            return;
        }
        String email = booking.getCustomer().getEmail();
        String subject = "Booking Cancelled - ID: " + booking.getBookingId();
        String body = "Hello " + booking.getCustomer().getFirstName() + ",\n\n"
                + "Your booking (ID: " + booking.getBookingId() + ") has been cancelled.\n\n"
                + "If you have any questions, please contact support.\n\n"
                + "Regards,\nFuel Agency";
        emailSenderService.sendEmail(email, subject, body);
    }
}