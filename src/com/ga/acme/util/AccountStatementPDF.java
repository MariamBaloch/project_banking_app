package com.ga.acme.util;

import com.ga.acme.enums.AccountType;
import com.ga.acme.enums.TransactionType;
import com.ga.acme.models.Account;
import com.ga.acme.models.User;
import com.ga.acme.services.UserService;
import com.itextpdf.html2pdf.HtmlConverter;
import com.itextpdf.kernel.pdf.PdfWriter;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import java.awt.*;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AccountStatementPDF {
    private static String mapValuesToHTML(Map<String, Map<String, String>> record, User user, AccountType accountType) {
        Context context = new Context();
        List<Map<String, Object>> transactionList = new ArrayList<>();
        for (Map.Entry<String, Map<String, String>> entry : record.entrySet()) {
            Map<String, String> values = entry.getValue();
            String transactionType = values.get("transactionType");
            String amount = values.get("transactionAmount");
            String transactionDate = values.get("transactionDate");
            String formattedDate = LocalDateTime.parse(transactionDate).format(DateTimeFormatter.ofPattern("dd MMM yyyy"));
            boolean isDebit = false;
            if (transactionType != null) {
                if (transactionType.equals(TransactionType.DEPOSIT.getDisplayName()) || transactionType.equals("Deposit to own account") || transactionType.equals(TransactionType.OVERDRAFT_RESOLUTION.getDisplayName()) || transactionType.equals("Overdraft Resolution")) {
                    isDebit = true;
                }
            }
            Map<String, Object> processedValues = new HashMap<>();
            processedValues.put("date", formattedDate);
            processedValues.put("transactionType", transactionType);
            processedValues.put("debit", isDebit ? amount : "");
            processedValues.put("credit", !isDebit ? amount : "");
            processedValues.put("balance", values.get("balanceAfter"));
            transactionList.add(processedValues);
        }
        Account account = accountType == AccountType.CHECKING_ACCOUNT ? user.getCheckingAccount() : user.getSavingsAccount();
        context.setVariable("account_number", account.getId());
        context.setVariable("account_type", accountType.getDisplayName());
        context.setVariable("current_balance", account.getBalance());
        context.setVariable("name", user.getName());
        context.setVariable("transactions", transactionList);
        ClassLoaderTemplateResolver resolver = new ClassLoaderTemplateResolver();
        resolver.setPrefix("templates/");
        resolver.setSuffix(".html");
        resolver.setTemplateMode(TemplateMode.HTML);
        resolver.setCharacterEncoding("UTF-8");
        TemplateEngine templateEngine = new TemplateEngine();
        templateEngine.setTemplateResolver(resolver);
        return templateEngine.process("AccountStatementTemplate", context);
    }

    public static void generatePDF(Map<String, Map<String, String>> records, String userId, AccountType accountType) {
        User user = UserService.getUserById(userId);
        try {
            DateTimeFormatter fileFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");
            String timestamp = LocalDateTime.now().format(fileFormatter);
            String htmlContent = mapValuesToHTML(records, user, accountType);
            String destinationPath = "src/main/resources/pdf/" + user.getName() + " - " + accountType.getDisplayName() + " - " + timestamp + ".pdf";
            HtmlConverter.convertToPdf(htmlContent, new PdfWriter(destinationPath));
            java.io.File pdfFile = new java.io.File(destinationPath);
            Desktop.getDesktop().open(pdfFile);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}