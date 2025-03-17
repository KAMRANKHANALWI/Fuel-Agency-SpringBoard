package com.faos.Booking.repositories;

import com.faos.Booking.models.Booking;
import com.faos.Booking.models.Customer;
import com.faos.Booking.models.enums.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    @Query("SELECT COALESCE(SUM(b.cylinderCount), 0) FROM Booking b WHERE YEAR(b.bookingDate) = :year")
    Integer countCylindersBookedThisYear(@Param("year") int year);

    // ✅ Ensure Bill is fetched with Booking
    @Query("SELECT DISTINCT b FROM Booking b LEFT JOIN FETCH b.bill")
    List<Booking> findAllWithBill();

    // New method to find cancelled bookings
    List<Booking> findByBookingStatus(BookingStatus bookingStatus);

    List<Booking> findByCustomer(Customer customer);

}
