package com.finaltica.application.service;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.stereotype.Service;

import com.finaltica.application.entity.Transaction;
import com.finaltica.application.entity.User;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;

@Service
public class PdfService {

	private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("MMM dd, yyyy")
			.withZone(ZoneId.systemDefault());

	public byte[] generateMonthlyReport(User user, List<Transaction> transactions, String month) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		PdfWriter writer = new PdfWriter(baos);
		PdfDocument pdf = new PdfDocument(writer);
		Document document = new Document(pdf);

		document.add(new Paragraph("Finaltica - Monthly Financial Report").setFontSize(20).setBold()
				.setTextAlignment(TextAlignment.CENTER));

		document.add(new Paragraph("Report Period: " + month).setFontSize(12).setTextAlignment(TextAlignment.CENTER));

		document.add(new Paragraph("User: " + user.getFirstName() + " " + user.getLastName()).setFontSize(10)
				.setTextAlignment(TextAlignment.CENTER).setMarginBottom(20));

		BigDecimal totalIncome = BigDecimal.ZERO;
		BigDecimal totalExpense = BigDecimal.ZERO;

		for (Transaction t : transactions) {
			if (t.getType().toString().equals("INCOME")) {
				totalIncome = totalIncome.add(t.getAmount());
			} else if (t.getType().toString().equals("EXPENSE")) {
				totalExpense = totalExpense.add(t.getAmount().abs());
			}
		}

		document.add(new Paragraph("Summary").setFontSize(14).setBold());
		document.add(new Paragraph("Total Income: $" + totalIncome));
		document.add(new Paragraph("Total Expenses: $" + totalExpense));
		document.add(new Paragraph("Net: $" + totalIncome.subtract(totalExpense)).setMarginBottom(20));

		document.add(new Paragraph("Transactions").setFontSize(14).setBold());

		Table table = new Table(UnitValue.createPercentArray(new float[] { 2, 3, 2, 2, 3 })).useAllAvailableWidth();

		table.addHeaderCell("Date");
		table.addHeaderCell("Description");
		table.addHeaderCell("Type");
		table.addHeaderCell("Category");
		table.addHeaderCell("Amount");

		for (Transaction t : transactions) {
			table.addCell(DATE_FORMATTER.format(t.getTransactionDate()));
			table.addCell(t.getDescription() != null ? t.getDescription() : "");
			table.addCell(t.getType().toString());
			table.addCell(t.getCategory() != null ? t.getCategory().getName() : "");
			table.addCell("$" + t.getAmount().abs().toString());
		}

		document.add(table);

		document.close();

		return baos.toByteArray();
	}
}