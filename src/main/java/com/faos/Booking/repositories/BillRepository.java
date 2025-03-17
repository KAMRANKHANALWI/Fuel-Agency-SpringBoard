package com.faos.Booking.repositories;

import com.faos.Booking.models.Bill;
import com.faos.Booking.models.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface BillRepository extends JpaRepository<Bill, Long> {

    @Query("SELECT b FROM Bill b JOIN FETCH b.booking")
    List<Bill> findAllWithBooking();

    @Query("SELECT b FROM Bill b JOIN FETCH b.booking WHERE b.billId = :billId")
    Optional<Bill> findByIdWithBooking(@Param("billId") Long billId);

    @Query("SELECT b.booking.customer FROM Bill b WHERE b.booking.customer.username = :username")
    Optional<Customer> findCustomerByUsername(@Param("username") String username);
}