package ru.javaprojects.rewardcalculator.util;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import org.springframework.util.Assert;
import ru.javaprojects.rewardcalculator.model.*;
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

import static ru.javaprojects.rewardcalculator.model.Rate.HALF_RATE;
import static ru.javaprojects.rewardcalculator.model.Rate.QUARTER_RATE;

public class EmployeeRewardUtil {
    private static final double PREMIUM_RATE = 0.3;
    private static final int MAX_PERCENTAGE_OF_SALARY = 80;
    private static final double HOURS_WORK_ROUNDING_COMPENSATION_COEFFICIENT = 0.25;

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
    private static String sumOfRewards;
    private static String agreed;
    private static DateTimeFormatter dateTimeFormatter;

    private static Font normalFont11;
    private static Font boldFont11;

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

    public static int calculateHoursWorkedReward(double hoursWorked, int salary, Rate rate, double requiredHoursWorked) {
        int hoursWorkedReward = (int) (hoursWorked * salary / requiredHoursWorked * PREMIUM_RATE);
        return compensateHoursWorkedRoundingError(hoursWorkedReward, salary, rate);
    }

    private static int compensateHoursWorkedRoundingError(int hoursWorkedReward, int salary, Rate rate) {
        switch (rate) {
            case FULL_RATE -> {
                return hoursWorkedReward;
            }
            case HALF_RATE -> {
                int maxHoursWorkedReward = (int) (salary * HALF_RATE.getCoefficient() * PREMIUM_RATE);
                if (maxHoursWorkedReward < hoursWorkedReward
                        || Math.abs(maxHoursWorkedReward - hoursWorkedReward) < salary * HOURS_WORK_ROUNDING_COMPENSATION_COEFFICIENT / 100) {
                    return maxHoursWorkedReward;
                } else {
                    return hoursWorkedReward;
                }
            }
            case QUARTER_RATE -> {
                int maxHoursWorkedReward = (int) (salary * QUARTER_RATE.getCoefficient() * PREMIUM_RATE);
                if (maxHoursWorkedReward < hoursWorkedReward
                        || Math.abs(maxHoursWorkedReward - hoursWorkedReward) < salary * (HOURS_WORK_ROUNDING_COMPENSATION_COEFFICIENT / 2) / 100) {
                    return maxHoursWorkedReward;
                } else {
                    return hoursWorkedReward;
                }
            }
            default -> {
                throw new IllegalArgumentException("Unknown Position Rate");
            }
        }
    }

    public static int calculateFullReward(int hoursWorkedReward, int additionalReward, int penalty, int salary, Rate rate) {
        int fullReward = hoursWorkedReward + additionalReward - penalty;
        if (fullReward < 0) {
            throw new EmployeeRewardBadDataException("Employee reward must be greater than or equal zero");
        }
        int percentageOfSalary = (int) (fullReward * 100 / salary / rate.getCoefficient());
        if (percentageOfSalary > MAX_PERCENTAGE_OF_SALARY) {
            throw new EmployeeRewardBadDataException("Employee reward must be less than or equal " + MAX_PERCENTAGE_OF_SALARY + " %");
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

    public static byte[] createEmployeeRewardsPdfForm(List<EmployeeReward> employeeRewards, DepartmentReward departmentReward,
                                                      EmployeeSignature approvingSignature) {
        try {
            Document document = new Document(PageSize.A4.rotate());
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            PdfWriter.getInstance(document, outputStream);
            document.open();
            addEmployeeRewardsPdfFormHeader(document, departmentReward);
            addEmployeeRewardsPdfFormTable(document, employeeRewards, departmentReward);
            addSumOfRewardsLine(document, employeeRewards);
            EmployeeSignature chiefSignature = getDepartmentChiefSignature(employeeRewards);
            addEmployeeRewardsPdfFormSignatures(document, chiefSignature, approvingSignature);
            document.close();
            return outputStream.toByteArray();
        } catch (DocumentException e) {
            throw new PdfException("Failed to create pdf form");
        }
    }

    private static void addEmployeeRewardsPdfFormHeader(Document document, DepartmentReward departmentReward) throws DocumentException {
        addParagraph(document, employeeRewardsList, boldFont11, Element.ALIGN_CENTER, 0,  2);
        addParagraph(document, departmentReward.getDepartment().getName(), normalFont11, Element.ALIGN_CENTER, 0, 2);
        addParagraph(document, departmentReward.getPaymentPeriod().getPeriod().format(dateTimeFormatter), normalFont11, Element.ALIGN_CENTER, 0, 10);
        addParagraph(document, depReward + ": " + departmentReward.getAllocatedAmount() + " " + currency, normalFont11, Element.ALIGN_CENTER, 0, 10);
    }

    private static void addEmployeeRewardsPdfFormTable(Document document, List<EmployeeReward> employeeRewards, DepartmentReward departmentReward) throws DocumentException {
        PdfPTable table = createTable();
        addTableHeader(table);
        fillTable(table, employeeRewards, departmentReward);
        document.add(table);
    }

    private static void addSumOfRewardsLine(Document document, List<EmployeeReward> employeeRewards) throws DocumentException {
        int rewardsSum = employeeRewards.stream()
                .mapToInt(EmployeeReward::getFullReward)
                .sum();
        addParagraph(document, sumOfRewards + ": " + rewardsSum, normalFont11, Element.ALIGN_CENTER, 300,  40);
    }

    private static void addEmployeeRewardsPdfFormSignatures(Document document, EmployeeSignature chiefSignature, EmployeeSignature approvingSignature) throws DocumentException {
        if (Objects.nonNull(chiefSignature)) {
            PdfPTable table = createSignatureTable(chiefSignature);
            table.setSpacingAfter(15);
            document.add(table);
        }
        if (Objects.nonNull(approvingSignature) && !approvingSignature.position.isBlank()) {
            addParagraph(document, agreed + ":", normalFont11, Element.ALIGN_LEFT, 118,   5);
            PdfPTable table = createSignatureTable(approvingSignature);
            document.add(table);
        }
    }

    private static void addParagraph(Document document, String line, Font font, int alignment, float indentLeft, float spacingAfter) throws DocumentException {
        Chunk chunk = new Chunk(line, font);
        Paragraph paragraph = new Paragraph();
        paragraph.setAlignment(alignment);
        paragraph.setIndentationLeft(indentLeft);
        paragraph.setSpacingAfter(spacingAfter);
        paragraph.add(chunk);
        document.add(paragraph);
    }

    private static PdfPTable createTable() {
        float[] columnWidths = {1, 4, 6, 5, 3, 3, 3};
        PdfPTable table = new PdfPTable(columnWidths);
        table.setWidthPercentage(100);
        table.setSpacingAfter(2);
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
            PdfPCell indexCell = new PdfPCell(createAlignmentCellWithPhrase(Element.ALIGN_CENTER, Element.ALIGN_MIDDLE, new Phrase(String.valueOf(i + 1), normalFont11)));
            indexCell.setMinimumHeight(36f);
            table.addCell(indexCell);
            PdfPCell departmentNameCell = new PdfPCell(createAlignmentCellWithPhrase(Element.ALIGN_CENTER, Element.ALIGN_MIDDLE, new Phrase(departmentReward.getDepartment().getName(), normalFont11)));
            departmentNameCell.setMinimumHeight(36f);
            table.addCell(departmentNameCell);
            PdfPCell positionNameCell = new PdfPCell(createAlignmentCellWithPhrase(Element.ALIGN_LEFT, Element.ALIGN_MIDDLE, new Phrase(employeeReward.getCurrentPositionName(), normalFont11)));
            positionNameCell.setMinimumHeight(36f);
            table.addCell(positionNameCell);
            PdfPCell employeeNameCell = new PdfPCell(createAlignmentCellWithPhrase(Element.ALIGN_LEFT, Element.ALIGN_MIDDLE, new Phrase(employeeReward.getEmployee().getName(), normalFont11)));
            employeeNameCell.setMinimumHeight(36f);
            table.addCell(employeeNameCell);
            PdfPCell rewardCell = new PdfPCell(new Phrase(employeeReward.getFullReward().toString(), normalFont11));
            rewardCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            rewardCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            rewardCell.setBackgroundColor(BaseColor.LIGHT_GRAY);
            rewardCell.setMinimumHeight(36f);
            table.addCell(rewardCell);
            PdfPCell salaryCell = new PdfPCell(createAlignmentCellWithPhrase(Element.ALIGN_CENTER, Element.ALIGN_MIDDLE, new Phrase(calculateSalaryWithRateCoefficient(employeeReward), normalFont11)));
            salaryCell.setMinimumHeight(36f);
            table.addCell(salaryCell);
            PdfPCell rewardPercentageCell = new PdfPCell(createAlignmentCellWithPhrase(Element.ALIGN_CENTER, Element.ALIGN_MIDDLE, new Phrase(calculateRewardOfSalaryPercent(employeeReward), normalFont11)));
            rewardPercentageCell.setMinimumHeight(36f);
            table.addCell(rewardPercentageCell);
        }
    }

    static EmployeeSignature getDepartmentChiefSignature(List<EmployeeReward> employeeRewards) {
        return employeeRewards.stream()
                .filter(employeeReward -> employeeReward.getEmployee().getPosition().isChiefPosition())
                .findFirst()
                .map(employeeReward -> {
                    Employee chief = employeeReward.getEmployee();
                    Position chiefPosition = chief.getPosition();
                    return new EmployeeSignature(chiefPosition.getName(), formatChiefName(chief.getName()));
                }).orElse(new EmployeeSignature("", ""));
    }

    private static String formatChiefName(String name) {
        String[] nameParts = name.split(" ");
        if (nameParts.length == 3) {
            StringBuilder builder = new StringBuilder();
            builder
                    .append(nameParts[1].charAt(0))
                    .append(".")
                    .append(nameParts[2].charAt(0))
                    .append(".")
                    .append(" ")
                    .append(nameParts[0]);
            return builder.toString();
        } else {
            return name;
        }
    }

    public static class EmployeeSignature {
        private final String position;
        private final String name;

        public EmployeeSignature(String position, String name) {
            this.position = position;
            this.name = name;
        }

        public EmployeeSignature() {
            this.position = "";
            this.name = "";
        }

        public String getPosition() {
            return position;
        }

        public String getName() {
            return name;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            EmployeeSignature that = (EmployeeSignature) o;
            return Objects.equals(position, that.position) &&
                    Objects.equals(name, that.name);
        }

        @Override
        public int hashCode() {
            return Objects.hash(position, name);
        }
    }

    private static PdfPTable createSignatureTable(EmployeeSignature employeeSignature) {
        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(70);

        PdfPCell positionCell = createAlignmentCellWithPhrase(Element.ALIGN_LEFT, Element.ALIGN_BOTTOM, new Phrase(employeeSignature.position, normalFont11));
        positionCell.setBorder(Rectangle.NO_BORDER);
        table.addCell(positionCell);
        PdfPCell nameCell = createAlignmentCellWithPhrase(Element.ALIGN_RIGHT, Element.ALIGN_BOTTOM, new Phrase(employeeSignature.name, normalFont11));
        nameCell.setBorder(Rectangle.NO_BORDER);
        table.addCell(nameCell);
        return table;
    }

    private static PdfPCell createAlignmentCellWithPhrase(int horizontalAlignment, int verticalAlignment, Phrase phrase) {
        PdfPCell cell = new PdfPCell();
        cell.setHorizontalAlignment(horizontalAlignment);
        cell.setVerticalAlignment(verticalAlignment);
        cell.setPhrase(phrase);
        return cell;
    }

    private static String calculateSalaryWithRateCoefficient(EmployeeReward employeeReward) {
        Integer salary = employeeReward.getCurrentPositionSalary();
        Rate rate = employeeReward.getCurrentEmployeeRate();
        int salaryWithRate = (int) (salary * rate.getCoefficient());
        return String.valueOf(salaryWithRate);
    }

    private static String calculateRewardOfSalaryPercent(EmployeeReward employeeReward) {
        Integer reward = employeeReward.getFullReward();
        Integer salary = employeeReward.getCurrentPositionSalary();
        Rate rate = employeeReward.getCurrentEmployeeRate();
        int rewardOfSalary = (int) (reward * 100 / salary / rate.getCoefficient());
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
            sumOfRewards = pdfFormProperties.getProperty("sumOfRewards");
            agreed = pdfFormProperties.getProperty("agreed");
            dateTimeFormatter = DateTimeFormatter.ofPattern(pdfFormProperties.getProperty("dateTimeFormatterPattern"));

            BaseFont baseFont = BaseFont.createFont(fontFileName, BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
            normalFont11 = new Font(baseFont, 11, Font.NORMAL);
            boldFont11 = new Font(baseFont, 11, Font.BOLD);
        } catch (DocumentException | IOException e) {
            e.printStackTrace();
            System.exit(0);
        }
    }
}