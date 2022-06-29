package by.urbel.docgenerator.template;

import by.urbel.docgenerator.entity.Mappable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import org.apache.poi.xwpf.usermodel.XWPFDocument;

import java.nio.file.Path;
import java.util.Map;

@AllArgsConstructor
@Getter
@ToString
public class Template {
    private final Path path;
    private final DocumentType documentType;
    private final Mappable data;
    private final XWPFDocument document;

    public String getFileName() {
        return path.getFileName().toString().split("\\.")[0];
    }

    public Map<String, String> mapping() {
        return data.mapping();
    }
}
