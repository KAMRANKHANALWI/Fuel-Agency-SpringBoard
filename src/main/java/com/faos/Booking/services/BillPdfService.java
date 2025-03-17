package com.faos.Booking.services;

import com.faos.Booking.models.Bill;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;

@Service
public class BillPdfService {

    public byte[] generateBillPdf(Bill bill) {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            Document document = new Document();
            PdfWriter.getInstance(document, outputStream);
            document.open();

            // Title
            Font titleFont = new Font(Font.HELVETICA, 18, Font.BOLD);
            Paragraph title = new Paragraph("Fuel Agency - Invoice", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);
            document.add(new Paragraph(" "));

            // Customer Details
            PdfPTable table = new PdfPTable(2);
            table.setWidthPercentage(100);
            table.addCell("Bill ID");
            table.addCell(String.valueOf(bill.getBillId()));

            if (bill.getBooking() != null && bill.getBooking().getCustomer() != null) {
                table.addCell("Customer Name");
                table.addCell(bill.getBooking().getCustomer().getFirstName() + " " + bill.getBooking().getCustomer().getLastName());

                table.addCell("Email");
                table.addCell(bill.getBooking().getCustomer().getEmail());

                table.addCell("Phone");
                table.addCell(bill.getBooking().getCustomer().getPhone());
            }

            // Booking Details
            if (bill.getBooking() != null) {
                table.addCell("Booking ID");
                table.addCell(String.valueOf(bill.getBooking().getBookingId()));

                table.addCell("Booking Date");
                table.addCell(String.valueOf(bill.getBooking().getBookingDate()));

                table.addCell("Delivery Date");
                table.addCell(String.valueOf(bill.getBooking().getDeliveryDate()));

                table.addCell("Delivery Option");
                table.addCell(bill.getBooking().getDeliveryOption().toString());

                table.addCell("Payment Mode");
                table.addCell(bill.getBooking().getPaymentMode().toString());
            }

            // Price Breakdown
            table.addCell("Base Price");
            table.addCell(String.valueOf(bill.getPrice()));

            table.addCell("GST");
            table.addCell(String.valueOf(bill.getGst()));

            table.addCell("Delivery Charge");
            table.addCell(String.valueOf(bill.getDeliveryCharge()));

            table.addCell("CLE Charge");
            table.addCell(String.valueOf(bill.getCLECharge()));

            table.addCell("Total Price");
            table.addCell(String.valueOf(bill.getTotalPrice()));

            document.add(table);
            document.close();

            return outputStream.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
