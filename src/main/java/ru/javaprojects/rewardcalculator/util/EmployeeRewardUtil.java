package ru.javaprojects.rewardcalculator.util;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import org.springframework.util.Assert;
import ru.javaprojects.rewardcalculator.model.DepartmentReward;
import ru.javaprojects.rewardcalculator.model.EmployeeReward;
import ru.javaprojects.rewardcalculator.to.EmployeeRewardTo;
import ru.javaprojects.rewardcalculator.util.exception.EmployeeRewardBadDataException;
import ru.javaprojects.rewardcalculator.util.exception.PdfException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import java.util.stream.Stream;

public class EmployeeRewardUtil {
    private static final double PREMIUM_RATE = 0.3;

    private static String employeeRewardsList;
    private static String indexNumber;
    private static String department;
    private static String position;
    private static String name;
    private static String reward;
    private static String salary;
    private static String rewardOfSalary;
    private static String depReward;
    private static String currency;
    private static DateTimeFormatter dateTimeFormatter;

    private static Font normalFont14;
    private static Font normalFont11;
    private static Font boldFont14;


    static {
        initStaticVariables();
    }

    private EmployeeRewardUtil() {
    }

    public static EmployeeReward updateFromTo(EmployeeReward employeeReward, EmployeeRewardTo employeeRewardTo, int hoursWorkedReward) {
        employeeReward.setHoursWorked(employeeRewardTo.getHoursWorked());
        employeeReward.setHoursWorkedReward(hoursWorkedReward);
        employeeReward.setAdditionalReward(employeeRewardTo.getAdditionalReward());
        employeeReward.setPenalty(employeeRewardTo.getPenalty());
        return employeeReward;
    }

    public static void checkToState(EmployeeRewardTo employeeRewardTo) {
        Assert.notNull(employeeRewardTo, "employeeRewardTo must not be null");
        Assert.notNull(employeeRewardTo.getHoursWorked(), "hours worked must not be null");
        Assert.notNull(employeeRewardTo.getAdditionalReward(), "additional reward must not be null");
        Assert.notNull(employeeRewardTo.getPenalty(), "penalty must not be null");
        Assert.isTrue(employeeRewardTo.getHoursWorked() >= 0, "hours worked must be greater than or equals to zero");
        Assert.isTrue(employeeRewardTo.getAdditionalReward() >= 0, "additional reward must be greater than or equals to zero");
        Assert.isTrue(employeeRewardTo.getPenalty() >= 0, "penalty must be greater than or equals to zero");
    }

    public static int calculateHoursWorkedReward(double hoursWorked, int salary, double requiredHoursWorked) {
        return (int) (hoursWorked * salary / requiredHoursWorked * PREMIUM_RATE);
    }

    public static int calculateFullReward(int hoursWorkedReward, int additionalReward, int penalty) {
        int fullReward = hoursWorkedReward + additionalReward - penalty;
        if (fullReward < 0) {
            throw new EmployeeRewardBadDataException("Employee reward must be greater than or equal zero");
        }
        return fullReward;
    }

    public static int calculateNewDistributedAmount(DepartmentReward departmentReward, int currentEmployeeFullReward, int newEmployeeFullReward) {
        int newDistributedAmount =  departmentReward.getDistributedAmount() - currentEmployeeFullReward + newEmployeeFullReward;
        if (newDistributedAmount > departmentReward.getAllocatedAmount()) {
            throw new EmployeeRewardBadDataException("Department reward allocated amount exceeded");
        }
        return newDistributedAmount;
    }

    public static byte[] createEmployeeRewardsPdfForm(List<EmployeeReward> employeeRewards, DepartmentReward departmentReward) {
        try {
            Document document = new Document(PageSize.A4.rotate());
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            PdfWriter.getInstance(document, outputStream);
            document.open();
            addFormHeader(document, departmentReward);
            addFormTable(document, employeeRewards, departmentReward);
            document.close();
            return outputStream.toByteArray();
        } catch (DocumentException e) {
            throw new PdfException("Failed to create pdf form");
        }
    }

    private static void addFormHeader(Document document, DepartmentReward departmentReward) throws DocumentException {
        addParagraph(document, employeeRewardsList, boldFont14, 10);
        addParagraph(document, departmentReward.getDepartment().getName(), normalFont14, 10);
        addParagraph(document, departmentReward.getPaymentPeriod().getPeriod().format(dateTimeFormatter), normalFont14, 15);
        addParagraph(document, depReward + ": " + departmentReward.getAllocatedAmount() + " " + currency, normalFont14, 10);
    }

    private static void addParagraph(Document document, String line, Font font, float spacingAfter) throws DocumentException {
        Chunk chunk = new Chunk(line, font);
        Paragraph paragraph = new Paragraph();
        paragraph.setAlignment(Element.ALIGN_CENTER);
        paragraph.setSpacingAfter(spacingAfter);
        paragraph.add(chunk);
        document.add(paragraph);
    }

    private static void addFormTable(Document document, List<EmployeeReward> employeeRewards, DepartmentReward departmentReward) throws DocumentException {
        PdfPTable table = createTable();
        addTableHeader(table);
        fillTable(table, employeeRewards, departmentReward);
        document.add(table);
    }

    private static PdfPTable createTable() {
        float[] columnWidths = {1, 4, 6, 5, 3, 3, 3};
        PdfPTable table = new PdfPTable(columnWidths);
        table.setWidthPercentage(100);
        table.setSpacingBefore(0f);
        table.setSpacingAfter(0f);
        table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
        table.getDefaultCell().setVerticalAlignment(Element.ALIGN_MIDDLE);
        table.setHeaderRows(1);
        return table;
    }

    private static void addTableHeader(PdfPTable table) {
        Stream.of(indexNumber, department, position, name, reward, salary, rewardOfSalary)
                .forEach(columnTitle  -> {
                    PdfPCell header = new PdfPCell();
                    header.setBackgroundColor(BaseColor.LIGHT_GRAY);
                    header.setBorderWidth(2);
                    header.setMinimumHeight(45);
                    header.setHorizontalAlignment(Element.ALIGN_CENTER);
                    header.setVerticalAlignment(Element.ALIGN_MIDDLE);
                    header.setPhrase(new Phrase(columnTitle));
                    table.addCell(header);
                });
    }

    private static void fillTable(PdfPTable table, List<EmployeeReward> employeeRewards, DepartmentReward departmentReward) {
        for (int i = 0; i < employeeRewards.size(); i++) {
            EmployeeReward employeeReward = employeeRewards.get(i);
            table.addCell(new Phrase(String.valueOf(i + 1), normalFont11));
            table.addCell(new Phrase(departmentReward.getDepartment().getName(), normalFont11));
            table.addCell(createAlignmentCellWithPhrase(Element.ALIGN_LEFT, new Phrase(employeeReward.getEmployee().getPosition().getName(), normalFont11)));
            table.addCell(createAlignmentCellWithPhrase(Element.ALIGN_LEFT, new Phrase(employeeReward.getEmployee().getName(), normalFont11)));
            PdfPCell rewardCell = new PdfPCell(new Phrase(employeeReward.getFullReward().toString(), normalFont11));
            rewardCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            rewardCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            rewardCell.setBackgroundColor(BaseColor.LIGHT_GRAY);
            table.addCell(rewardCell);
            table.addCell(new Phrase(employeeReward.getEmployee().getPosition().getSalary().toString(), normalFont11));
            table.addCell(new Phrase(calculateRewardOfSalaryPercent(employeeReward), normalFont11));

        }
    }

    private static PdfPCell createAlignmentCellWithPhrase(int horizontalAlignment, Phrase phrase) {
        PdfPCell cell = new PdfPCell();
        cell.setHorizontalAlignment(horizontalAlignment);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setPhrase(phrase);
        return cell;
    }

    private static String calculateRewardOfSalaryPercent(EmployeeReward employeeReward) {
        Integer reward = employeeReward.getFullReward();
        Integer salary = employeeReward.getEmployee().getPosition().getSalary();
        int rewardOfSalary = reward * 100 / salary;
        return String.valueOf(rewardOfSalary);
    }

    private static void initStaticVariables() {
        try (Reader reader = new InputStreamReader(Objects.requireNonNull(EmployeeRewardUtil.class.getClassLoader().getResourceAsStream("pdf/pdfForm.properties")), StandardCharsets.UTF_8)) {
            Properties pdfFormProperties = new Properties();
            pdfFormProperties.load(reader);

            String fontFileName = pdfFormProperties.getProperty("fontFileName");
            employeeRewardsList = pdfFormProperties.getProperty("employeeRewardsList");
            indexNumber = pdfFormProperties.getProperty("indexNumber");
            department = pdfFormProperties.getProperty("department");
            position = pdfFormProperties.getProperty("position");
            name = pdfFormProperties.getProperty("name");
            reward = pdfFormProperties.getProperty("reward");
            salary = pdfFormProperties.getProperty("salary");
            rewardOfSalary = pdfFormProperties.getProperty("rewardOfSalary");
            depReward = pdfFormProperties.getProperty("depReward");
            currency = pdfFormProperties.getProperty("currency");
            dateTimeFormatter = DateTimeFormatter.ofPattern(pdfFormProperties.getProperty("dateTimeFormatterPattern"));

            BaseFont baseFont = BaseFont.createFont(fontFileName, BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
            normalFont14 = new Font(baseFont, 14, Font.NORMAL);
            normalFont11 = new Font(baseFont, 11, Font.NORMAL);
            boldFont14 = new Font(baseFont, 14, Font.BOLD);
        } catch (DocumentException | IOException e) {
            e.printStackTrace();
            System.exit(0);
        }
    }
}