package com.co.eatupapi.repositories.payment.invoice;

import com.co.eatupapi.domain.payment.invoice.Invoice;
import com.co.eatupapi.domain.payment.invoice.InvoiceStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.UUID;

public interface InvoiceRepository extends JpaRepository<Invoice, UUID> {

    Page<Invoice> findByLocationIdOrderByInvoiceDateDesc(UUID locationId, Pageable pageable);

    Page<Invoice> findByLocationIdAndStatusOrderByInvoiceDateDesc(UUID locationId, InvoiceStatus status, Pageable pageable);

    boolean existsBySalesIdAndLocationIdAndStatusNotIn(
            UUID salesId,
            UUID locationId,
            Collection<InvoiceStatus> statuses
    );
}
