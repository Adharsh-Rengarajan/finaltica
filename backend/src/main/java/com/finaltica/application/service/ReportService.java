package com.finaltica.application.service;

import java.time.Instant;
import java.time.YearMonth;
import java.time.ZoneId;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.finaltica.application.entity.Transaction;
import com.finaltica.application.entity.User;
import com.finaltica.application.repository.TransactionRepository;

@Service
public class ReportService {

	@Autowired
	private TransactionRepository transactionRepository;

	@Autowired
	private PdfService pdfService;

	@Autowired
	private S3Service s3Service;

	public String generateMonthlyReport(User user, int year, int month) {
		YearMonth yearMonth = YearMonth.of(year, month);
		Instant startDate = yearMonth.atDay(1).atStartOfDay(ZoneId.systemDefault()).toInstant();
		Instant endDate = yearMonth.atEndOfMonth().atTime(23, 59, 59).atZone(ZoneId.systemDefault()).toInstant();

		List<Transaction> transactions = transactionRepository.findByUserIdAndDateRange(user.getId(), startDate,
				endDate);

		String monthName = yearMonth.getMonth().toString() + " " + year;

		byte[] pdfBytes = pdfService.generateMonthlyReport(user, transactions, monthName);

		String fileName = "reports/" + user.getId() + "/" + year + "-" + String.format("%02d", month)
				+ "-monthly-report.pdf";

		s3Service.uploadFile(fileName, pdfBytes, "application/pdf");

		return s3Service.generatePresignedUrl(fileName);
	}

	public String generateCustomReport(User user, Instant startDate, Instant endDate) {
		List<Transaction> transactions = transactionRepository.findByUserIdAndDateRange(user.getId(), startDate,
				endDate);

		String period = startDate.toString() + " to " + endDate.toString();

		byte[] pdfBytes = pdfService.generateMonthlyReport(user, transactions, period);

		String fileName = "reports/" + user.getId() + "/custom-" + UUID.randomUUID() + ".pdf";

		s3Service.uploadFile(fileName, pdfBytes, "application/pdf");

		return s3Service.generatePresignedUrl(fileName);
	}
}