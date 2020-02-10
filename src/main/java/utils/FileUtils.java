package utils;


import org.jdom2.Document;
import org.jdom2.input.DOMBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import org.xml.sax.SAXException;
import sort.PrintableException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;


public class FileUtils {

    public static Collection<String> getFileAsStrings(File file) {
        final String fileContent = getFileAsString(file);
        final String[] fileStings = fileContent.split("\\n");
        final Collection<String> result = Arrays.stream(fileStings)
                .map(it -> it.replaceAll("[\r\n]+", ""))
                .collect(Collectors.toList());

        return result;
    }

    public static String getFileAsString(File file) {
        if (!file.isFile()) {
            throw new RuntimeException("ERROR. File \"" + file.getPath() + "\" does not exist");
        }
        try {
            return org.apache.commons.io.FileUtils.readFileToString(file, Charset.defaultCharset());
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static Document parseXml(File file) {
        try {
            return new DOMBuilder().build(DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(file));
        }
        catch (SAXException e) {
            throw new RuntimeException(e);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
        catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        }
    }

    public static void saveXml(Document document, File file) {
        XMLOutputter xmlOutput = new XMLOutputter();
        Format format = Format.getRawFormat();
        format.setIndent("    ");
        format.setTextMode(Format.TextMode.TRIM);
        xmlOutput.setFormat(format);
        File tempFile = new File(file.getParent(), file.getName() + ".tmp");
        FileWriter fileWriter = null;
        try {
            fileWriter = new FileWriter(tempFile);
            xmlOutput.output(document, fileWriter);
            fileWriter.close();
        }
        catch (IOException e) {
            FileUtils.closeQuietly(fileWriter);
            throw new RuntimeException(e);
        }
        FileUtils.renameFile(tempFile.getAbsolutePath(), file.getAbsolutePath());
    }

    public static void copyFile(File source, File target) {
        try {
            org.apache.commons.io.FileUtils.copyFile(source, target);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void writeLines(File target, List<String> lines) {
        writeLines(target, lines, false);
    }

    private static void writeLines(File target, List<String> lines, boolean append) {
        FileWriter writer = null;
        try {
            writer = new FileWriter(target, append);
            for (String line : lines) {
                writer.append(line).append("\n");
            }
            writer.close();
        }
        catch (IOException e) {
            closeQuietly(writer);
            throw new RuntimeException(e);
        }
    }

    public static void writeDocumentToXMLFile(Document document, File file) {
        try {
            XMLOutputter xmlOutputter = new XMLOutputter();
            xmlOutputter.setFormat(Format.getPrettyFormat());
            xmlOutputter.output(document,
                    new FileOutputStream(file.getAbsolutePath()));
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static Properties readPropertiesFile(String filePath) {
        try {
            Properties result = new Properties();
            FileInputStream fileReader = new FileInputStream(filePath);
            result.load(fileReader);
            fileReader.close();
            return result;
        }
        catch (FileNotFoundException e) {
            String message = "File was not found " + filePath;
            throw new RuntimeException(message, e);
        }
        catch (IOException e) {
            String message = "Error reading file " + filePath;
            throw new RuntimeException(message, e);
        }
    }

    public static void closeQuietly(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            }
            catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    public static void renameFile(String sourceFilePath, String targetFilePath) {
        File sourceFile = new File(sourceFilePath);
        File targetFile = new File(targetFilePath);
        if (targetFile.exists() && !targetFile.delete()) {
            throw new RuntimeException("Cannot remove file " + targetFile.getAbsolutePath());
        }
        if (!sourceFile.renameTo(targetFile)) {
            throw new RuntimeException("Cannot move file " + sourceFile.getAbsolutePath() + " to " + targetFile.getAbsolutePath());
        }
    }

    public static void createOrCleanDirs(File dir) {
        if (dir.isFile()) {
            throw new PrintableException("Cannot create directory. A file with the same name already exists: " + dir.getAbsolutePath());
        }

        recursiveDelete(dir);

        if (!dir.exists() && !dir.mkdirs()) {
            throw new PrintableException("Cannot create directory: " + dir.getAbsolutePath());
        }
    }

    public static void recursiveDelete(File file) {

        if (!file.exists())
            return;

        if (file.isDirectory()) {
            for (File f : file.listFiles()) {
                recursiveDelete(f);
            }
        }

        file.delete();

    }

}
