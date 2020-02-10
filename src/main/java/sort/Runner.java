package sort;


import utils.FileUtils;
import dto.AbbreviationWithOrderNumber;
import lombok.NonNull;
import org.jdom2.Document;
import org.jdom2.Element;

import java.io.File;
import java.io.IOException;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Properties;

public class Runner {

    public static void main(String... args) throws IOException {
        FileUtils.createOrCleanDirs(new File("output"));
        final Properties properties = FileUtils.readPropertiesFile("config.properties");
        final SortingComponent sortingComponent = new SortingComponent();

        final List<AbbreviationWithOrderNumber> sortedAbbreviations = sortingComponent.sort();

        Runner.writeToInstallXML(
                sortedAbbreviations,
                new File("input/" + properties.getProperty("install_file"))
        );
    }

    private static void writeToInstallXML (@NonNull final List<AbbreviationWithOrderNumber> abbreviations,
                                           @NonNull final File file)
    {
        abbreviations.sort(Comparator.comparing(AbbreviationWithOrderNumber::getOrderNumber));
        final ListIterator<AbbreviationWithOrderNumber> abbreviationsIterator = abbreviations.listIterator();
        final String fileName = file.getName();
        FileUtils.copyFile(file, new File("output/back_"+ fileName));

        final Document document = FileUtils.parseXml(file);

        final Iterator iterator = XMLParser.getAllTagsWithAttribute(document.getRootElement(), "antcall");
        while (iterator.hasNext()) {
            final Element antcall = (Element) iterator.next();

            if (antcall.getAttributeValue("target").equals("install.zip")) {
                Element param = antcall.getChild("param");
                if (param.getAttributeValue("name").equals("zip_name")) {
                    param.setAttribute("value", abbreviationsIterator.next().getAbbreviation());
                }

            }
        }
        FileUtils.saveXml(document, new File("output/" + fileName));
    }
}
