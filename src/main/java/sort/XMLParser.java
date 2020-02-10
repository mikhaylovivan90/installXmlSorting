package sort;


import lombok.NonNull;
import org.jdom2.Element;
import org.jdom2.filter.ElementFilter;
import org.jdom2.util.IteratorIterable;

public class XMLParser {

    public static IteratorIterable<Element> getAllTagsWithAttribute (@NonNull final Element element,
                                                                     @NonNull final String attributeName)
    {
        final ElementFilter elementFilter = new ElementFilter(attributeName);
        return element.getDescendants(elementFilter);
    }
}
