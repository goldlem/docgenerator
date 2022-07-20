package by.urbel.docgenerator.util;

import by.urbel.docgenerator.data.FinancialData;
import by.urbel.docgenerator.entity.Mappable;
import by.urbel.docgenerator.exception.ApiRequestException;
import by.urbel.docgenerator.template.Template;
import com.opencsv.CSVReader;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.exceptions.CsvValidationException;
import fr.opensagres.poi.xwpf.converter.core.ImageManager;
import fr.opensagres.poi.xwpf.converter.pdf.PdfConverter;
import fr.opensagres.poi.xwpf.converter.pdf.PdfOptions;
import fr.opensagres.poi.xwpf.converter.xhtml.XHTMLConverter;
import fr.opensagres.poi.xwpf.converter.xhtml.XHTMLOptions;
import lombok.extern.slf4j.Slf4j;
import net.lingala.zip4j.ZipFile;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.http.HttpStatus;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Slf4j
public class FileUtils {
    public static InputStream getResourceAsIOStream(final String path) throws FileNotFoundException {
        InputStream ioStream = FileUtils.class.getResourceAsStream(path);
        if (ioStream == null) {
            File file = new File(path);
            ioStream = new FileInputStream(file);
        }
        return ioStream;
    }

    public static List<String> readLines(String path) {
        try {
            try (InputStream inputStream = getResourceAsIOStream(path);
                 InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                 BufferedReader reader = new BufferedReader(inputStreamReader);) {
                return reader.lines().collect(Collectors.toList());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static Document readDocumentByName(String fileName) {
        Path path = null;
        try {
            path = getPathFromString(Constants.HTML_TEMPLATE_DIR + fileName);
        } catch (IOException e) {
            throw new ApiRequestException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        try (InputStream inputStream = getResourceAsIOStream(path.toString())) {
            Document document = Jsoup.parse(new String(inputStream.readAllBytes(), StandardCharsets.UTF_8));
            document.charset(Charset.defaultCharset());
            return document;
        } catch (FileNotFoundException e) {
            throw new ApiRequestException(String.format("File %s is not found!", fileName), HttpStatus.NOT_FOUND);
        } catch (IOException e) {
            throw new ApiRequestException("Exception message: "+e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public static Map<String, List<String>> csvToMap(String dataPath) {
        String[] header;
        String[] line;
        List<List<String>> values = new ArrayList<>();
        Map<String, List<String>> data = new HashMap<>();

        try (InputStream stream = FileUtils.getResourceAsIOStream(dataPath); Reader reader = new InputStreamReader(stream); CSVReader csvReader = new CSVReader(reader)) {
            if ((header = csvReader.readNext()) != null) {
                for (int i = 0; i < header.length; i++) {
                    values.add(new ArrayList<>());
                }
                while ((line = csvReader.readNext()) != null) {
                    for (int i = 0; i < header.length; i++) {
                        values.get(i).add(line[i]);
                    }
                }
                for (int i = 0; i < header.length; i++) {
                    data.put(header[i], values.get(i));
                }
            }
        } catch (IOException | CsvValidationException e) {
            throw new RuntimeException(e);
        }
        if (data.isEmpty()) {
            throw new RuntimeException("CSV file with data is empty");
        }
        return data;
    }

    public static <T> List<T> csvToBeans(String path, Class<T> clazz) {
        try (InputStream stream = getResourceAsIOStream(path);
             Reader reader = new InputStreamReader(stream);) {
            List<T> data = new CsvToBeanBuilder(reader).withType(clazz).withIgnoreLeadingWhiteSpace(true).build().parse();
            if (data.isEmpty()) {
                throw new Exception("CSV file is empty, path: " + path);
            }
            return data;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static Path getPathFromString(String path) throws IOException {
        FileSystem fileSystem = null;
        URI uri = null;
        try {
            uri = FileUtils.class.getResource(path).toURI();

            if ("jar".equals(uri.getScheme())) {
                fileSystem = getFileSystem(uri);
                return fileSystem.getPath("/BOOT-INF/classes" + path);
            } else {
                return Paths.get(uri);
            }
        } catch (URISyntaxException e) {
            throw new ApiRequestException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        } finally {
            if (fileSystem != null) {
                fileSystem.close();
            }
        }
    }

    public static Path findTemplatePath(String dirPath, String templateType) throws IOException, URISyntaxException {
        String fileExtension = ".docx";
        URI uri = FileUtils.class.getResource(dirPath).toURI();
        Path myPath;
        FileSystem fileSystem = null;
        if ("jar".equals(uri.getScheme())) {
            fileSystem = getFileSystem(uri);
            myPath = fileSystem.getPath("/BOOT-INF/classes" + dirPath);
        } else {
            myPath = Paths.get(uri);
        }

        try (Stream<Path> paths = Files.walk(myPath, 1);) {
            List<Path> templatePaths = paths.filter(Files::isRegularFile).filter(p -> p.getFileName().toString().toLowerCase().startsWith(templateType))
                    .filter(p -> p.getFileName().toString().toLowerCase().endsWith(fileExtension)).collect(Collectors.toList());
            if (templatePaths.isEmpty()) {
                throw new ApiRequestException(String.format("There isn't selected template type: %s", templateType),
                        HttpStatus.NOT_FOUND);
            }
            return templatePaths.get(0);
        }
    }

    private static FileSystem getFileSystem(URI uri) throws IOException {
        try {
            return FileSystems.getFileSystem(uri);
        } catch (FileSystemNotFoundException e) {
            return FileSystems.newFileSystem(uri, Collections.<String, String>emptyMap());
        }
    }

    public static void prepareOutputDir(Path dirPath) {
        File outputDir = dirPath.toFile();
        if (!outputDir.exists()) {
            outputDir.mkdirs();
        }
    }

    public static File saveDocuments(List<Template> templates, Path outDirPath, List<String> fileExtensions) throws IOException {
        FileUtils.prepareOutputDir(outDirPath);
        File zipFile = outDirPath.resolve("invoice.zip").toFile();
        XWPFDocument document = null;
        Path imagesDir = null;
        try (ByteArrayOutputStream byteArrOutStream = new ByteArrayOutputStream();
             FileOutputStream out = new FileOutputStream(zipFile);
             ZipOutputStream zipOutput = new ZipOutputStream(out)) {
            for (Template template : templates) {
                byteArrOutStream.reset();
                Mappable templateData = template.getData();
                String fileNumber = templateData instanceof FinancialData ?
                        ((FinancialData) templateData).getInvoiceNumber() : Randomizer.getStrNumber(5);
                String templateName = template.getFileName().split("-")[0];
                for (String fileExtension : fileExtensions) {
                    String outputName = String.format("%s-%s.%s", templateName, fileNumber, fileExtension);
                    document = template.getDocument();

                    ZipEntry zipEntry = new ZipEntry(outputName);
                    zipOutput.putNextEntry(zipEntry);
                    switch (fileExtension) {
                        case "pdf": {
                            PdfConverter.getInstance().convert(document, byteArrOutStream, PdfOptions.getDefault());
                            zipOutput.write(byteArrOutStream.toByteArray());
                            break;
                        }
                        case "html": {
                            imagesDir = convertToHtml(template, byteArrOutStream, outDirPath);
                            zipOutput.write(byteArrOutStream.toByteArray());
                            break;
                        }
                        case "docx": {
                            document.write(zipOutput);
                            break;
                        }
                    }
                    zipOutput.closeEntry();
                }
                document.close();
            }
        } catch (IOException e) {
            log.warn(String.format("Failed to save document in zip file with path: '%s', cause: %s", zipFile, e.getCause()));
        } finally {
            if (document != null) {
                document.close();
            }
            if (imagesDir != null) {
                ZipFile zip = new ZipFile(zipFile);
                zip.addFolder(imagesDir.toFile());
                zip.close();
                org.apache.commons.io.FileUtils.deleteDirectory(imagesDir.toFile());
            }
        }
        return zipFile;
    }

    public static File saveDocuments(List<File> files, Path outDirPath) throws IOException {
        FileUtils.prepareOutputDir(outDirPath);
        File zipFile = outDirPath.resolve("html.zip").toFile();

        try (FileOutputStream out = new FileOutputStream(zipFile);
             ZipOutputStream zipOutput = new ZipOutputStream(out)) {
            for (File file : files) {
                ZipEntry zipEntry = new ZipEntry(file.getName());
                zipOutput.putNextEntry(zipEntry);
                zipOutput.write(org.apache.commons.io.FileUtils.readFileToByteArray(file));
                zipOutput.closeEntry();
            }
        }
        org.apache.commons.io.FileUtils.deleteDirectory(Constants.OUT_HTML_PATH.toFile());
        return zipFile;
    }

    private static Path convertToHtml(Template template, ByteArrayOutputStream out, Path outDirPath) throws IOException {
        XHTMLOptions options = XHTMLOptions.create().setImageManager(
                new ImageManager(outDirPath.toFile(),
                        String.format("images/%s", template.getFileName().split("-")[0].toLowerCase())));
        XHTMLConverter.getInstance().convert(template.getDocument(), out, options);
        return outDirPath.resolve("images");
    }
}
