package ru.javaprojects.rewardcalculator;

import com.itextpdf.text.pdf.PdfReader;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import ru.javaprojects.rewardcalculator.web.json.JsonUtil;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

public class TestUtil {
    private TestUtil() {
    }

    public static String getContent(MvcResult result) throws UnsupportedEncodingException {
        return result.getResponse().getContentAsString();
    }

    public static <T> T readFromJson(ResultActions action, Class<T> clazz) throws UnsupportedEncodingException {
        return JsonUtil.readValue(getContent(action.andReturn()), clazz);
    }

    public static <T> T readFromJsonMvcResult(MvcResult result, Class<T> clazz) throws UnsupportedEncodingException {
        return JsonUtil.readValue(getContent(result), clazz);
    }

    public static <T> List<T> readListFromJsonMvcResult(MvcResult result, Class<T> clazz) throws UnsupportedEncodingException {
        return JsonUtil.readValues(getContent(result), clazz);
    }

    public static void checkPdf(byte[] pdfBytes, String pdfFileName) throws IOException {
        PdfReader bytesReader = new PdfReader(pdfBytes);
        PdfReader fileReader = new PdfReader(pdfFileName);
        for (int i = 1; i <= bytesReader.getNumberOfPages(); i++) {
            assertArrayEquals(fileReader.getPageContent(i), bytesReader.getPageContent(i));
        }
        bytesReader.close();
        fileReader.close();
    }
}