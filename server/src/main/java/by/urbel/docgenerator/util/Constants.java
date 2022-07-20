package by.urbel.docgenerator.util;

import java.nio.file.Path;

public class Constants {
    public static final String  TEMPLATE_DIR = "/template";
    public static final String  HTML_TEMPLATE_DIR = "/htmlTemplates/";
    public static final Path OUT_PATH = Path.of("out/");
    public static final Path OUT_HTML_PATH = Path.of("out/html/");

    public static final String COMPANY_DATA_PATH = "/data/company.csv";
    public static final String PERSON_DATA_PATH = "/data/person.csv";
    public static final String CITIES_PATH = "/data/worldcities.csv";
    public static final String PRODUCT_NAME_PATH = "/data/product_name.txt";
    public static final String PRODUCT_DESC_PATH = "/data/product_description.txt";
}
