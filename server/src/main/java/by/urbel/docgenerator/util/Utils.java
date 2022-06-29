package by.urbel.docgenerator.util;

import by.urbel.docgenerator.template.DocumentType;
import lombok.extern.slf4j.Slf4j;

import java.nio.file.Path;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class Utils {

    public static boolean isMatchFound(String patternString, String text) {
        Pattern pattern = Pattern.compile(patternString);
        Matcher matcher = pattern.matcher(text);
        return matcher.find();
    }

    public static DocumentType extractDocumentType(Path path) {
        String fileName = path.getFileName().toString().toLowerCase();
        for (DocumentType type: DocumentType.values()) {
            String pattern = type.name().toLowerCase();
            if (isMatchFound(pattern, fileName)) {
                return type;
            }
        }
        throw new RuntimeException(String.format("Unknown document type, path: %s", path));
    }

    public static int extractIndex(Path path, String patternPrefix) {
        String fileName = path.getFileName().toString();
        String formatPattern = String.format("%s[0-9]+", patternPrefix);
        Pattern pattern = Pattern.compile(formatPattern);
        Matcher matcher = pattern.matcher(fileName);
        if (matcher.find()) {
            int index = Integer.parseInt(matcher.group().substring(patternPrefix.length()));
            return index - 1;
        } else {
            return -1;
        }
    }

    public static String formatSentence(String sentence) {
        sentence = sentence.toLowerCase();
        return Character.toUpperCase(sentence.charAt(0)) +
                sentence.substring(1);
    }

}
