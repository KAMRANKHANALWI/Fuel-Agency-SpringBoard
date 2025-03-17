package com.faos.Booking.services;

import com.faos.Booking.exceptions.ResourceNotFoundException;
import com.faos.Booking.models.Bill;
import com.faos.Booking.models.Booking;
import com.faos.Booking.models.Customer;
import com.faos.Booking.repositories.BillRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class BillService {

    private final BillRepository billRepository;

    public BillService(BillRepository billRepository) {
        this.billRepository = billRepository;
    }

    // ✅ Admin: Get all bills
    public List<Bill> getAllBills() {
        return billRepository.findAll();
    }

    // ✅ Customer/Admin: Get bill by ID (Customers can only view their own bills)
    public Bill getBillById(Long id) {
        Bill bill = billRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Bill not found with ID: " + id));
        ensureCustomerOrAdminAccess(bill.getBooking().getCustomer());
        return bill;
    }

    // ✅ Admin: Update a bill
    @Transactional
    public Bill updateBill(Long id, Bill updatedBill) {
        Bill existingBill = getBillById(id);
        existingBill.setPrice(updatedBill.getPrice());
        existingBill.setGst(updatedBill.getGst());
        existingBill.setDeliveryCharge(updatedBill.getDeliveryCharge());
        existingBill.setCLECharge(updatedBill.getCLECharge());
        existingBill.setTotalPrice(updatedBill.getTotalPrice());
        return billRepository.save(existingBill);
    }

    // ✅ Admin: Delete a bill
    @Transactional
    public void deleteBill(Long id) {
        Bill bill = getBillById(id);

        // Disassociate from the booking side
        Booking parentBooking = bill.getBooking();
        if (parentBooking != null) {
            parentBooking.setBill(null);  // Remove Bill from Booking
        }
        bill.setBooking(null);            // Remove Booking from Bill

        // Now delete the Bill
        billRepository.delete(bill);
    }

    // ✅ Fetch the currently logged-in customer
    private Customer getCurrentCustomer() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return billRepository.findCustomerByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    // ✅ Ensure access control
    private void ensureCustomerOrAdminAccess(Customer customer) {
        Customer currentUser = getCurrentCustomer();
        if (!currentUser.getUsername().equals(customer.getUsername()) && !currentUser.getRole().name().equals("ADMIN")) {
            throw new RuntimeException("Access Denied: You are not authorized to perform this action");
        }
    }
}
