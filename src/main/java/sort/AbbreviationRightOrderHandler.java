package sort;

import utils.FileUtils;
import dto.AbbreviationWithOrderNumber;
import dto.AbbreviationWithOrderNumberList;
import dto.ComponentsAndAbbreviationSet;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.Collection;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * Для получения данных о правильном порядке аббревиатур.
 */
public class AbbreviationRightOrderHandler {

    /**
     * Получаем правильный порядок аббревиатур (тот который должен быть в install.xml).
     * Делаем это на основе правильного порядка компонент (orderlist.txt), связывая их между собой на основе
     * списка componentsAndAbbreviations.
     * @param componentsAndAbbreviations список компонент и связанных с ними аббревиатур.
     * @return правильный порядок аббревиатур.
     */
    public AbbreviationWithOrderNumberList getAbbreviationsRightOrder(
            final ComponentsAndAbbreviationSet componentsAndAbbreviations)
    {
        final Properties properties = FileUtils.readPropertiesFile("config.properties");
        final Collection<String> componentsInRightOrder = FileUtils.getFileAsStrings(
                new File("input/" + properties.getProperty("order_file"))
        );

        final AtomicInteger orderNumber = new AtomicInteger(1);
        return componentsInRightOrder.stream()
                .map(componentsAndAbbreviations::getElementWithEquivalentComponentName)
                .filter(componentAndAbbreviation -> componentAndAbbreviation.isPresent()
                        && !StringUtils.isEmpty(componentAndAbbreviation.get().getComponentAbbreviation()))
                .map(
                        componentAndAbbreviation -> new AbbreviationWithOrderNumber(
                                orderNumber.getAndIncrement(),
                                componentAndAbbreviation.get().getComponentAbbreviation()
                        )
                ).collect(Collectors.toCollection(AbbreviationWithOrderNumberList::new));
    }
}
