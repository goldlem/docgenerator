package by.urbel.docgenerator.service;

import by.urbel.docgenerator.data.InvoiceData;
import by.urbel.docgenerator.data.RemittanceData;
import by.urbel.docgenerator.entity.Mappable;
import by.urbel.docgenerator.entity.Summary;
import by.urbel.docgenerator.util.Randomizer;

import java.time.LocalDate;
/** переносит данные из invoice data в remittance data **/
public class DataMapper {

    public static void mapTemplateData(Mappable from, Mappable to) {
        if (from instanceof InvoiceData && to instanceof RemittanceData) {
            invoiceToRemittance(((InvoiceData) from), ((RemittanceData) to));
        }
    }

    private static void invoiceToRemittance(InvoiceData invoiceData, RemittanceData remittanceData) {
        LocalDate invoiceDate = invoiceData.getDate();
        LocalDate paymentDate = Randomizer.getDate(invoiceDate, 0, Randomizer.getInt(0, 5));
        LocalDate remittanceDate = Randomizer.getDate(paymentDate, 0, Randomizer.getInt(0, 1));
        Summary invoiceSummary = invoiceData.getSummary();

        remittanceData.setCompanyTo(invoiceData.getCompanyFrom());
        remittanceData.setCompanyFrom(invoiceData.getCompanyTo());
        remittanceData.setInvoiceNumber(invoiceData.getInvoiceNumber());
        remittanceData.setDueDate(invoiceData.getDueDate());
        remittanceData.setDate(remittanceDate);
        remittanceData.setInvoiceDate(invoiceDate);
        remittanceData.setPaymentDate(paymentDate);
        remittanceData.setSummary(invoiceSummary);
        remittanceData.setPaid(invoiceSummary.getTotal());
        remittanceData.setOwing(0.0);
    }
}
