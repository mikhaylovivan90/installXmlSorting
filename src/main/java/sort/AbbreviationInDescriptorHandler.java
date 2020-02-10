package sort;

import utils.CollectionUtils;
import utils.FileUtils;
import dto.AbbreviationInDescriptor;
import dto.AbbreviationWithOrderNumberList;
import dto.AbbreviationsInDescriptorList;
import lombok.NonNull;
import org.apache.commons.lang3.StringUtils;
import org.jdom2.Document;
import org.jdom2.Element;

import java.io.File;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Для обработки порядка аббревиатур в дескрипторе install.xml.
 */
public class AbbreviationInDescriptorHandler {

    /**
     * @return данные об аббревиатурах компонентов в виде удобном для дальнейшей сортировки.
     * Как не подлежащие сортировке будут отмечены те компоненты, которые вызов которых (antcall) лежит не в корне
     * <target name="process">, например внутри if.
     */
    protected AbbreviationsInDescriptorList getAbbreviationOrderInDescriptor() {

        final Properties properties = FileUtils.readPropertiesFile("config.properties");
        final Document document = FileUtils.parseXml(new File("input/" + properties.getProperty("install_file")));

        Integer orderNumber = 1;
        final Iterator iterator =
                XMLParser.getAllTagsWithAttribute(document.getRootElement(), "antcall");

        final AbbreviationsInDescriptorList result = new AbbreviationsInDescriptorList();
        while (iterator.hasNext()) {
            final Element antcallElement = (Element) iterator.next();

            if (antcallElement.getAttributeValue("target").equals("install.zip")) {
                final Element param = antcallElement.getChild("param");
                if (param.getAttributeValue("name").equals("zip_name")) {
                    final String componentAbbreviation = param.getAttributeValue("value");
                    final AbbreviationInDescriptor abbreviationInDescriptor = new AbbreviationInDescriptor(
                            orderNumber, componentAbbreviation
                    );
                    final Boolean isComponentSortingNeeded =
                            this.isComponentSortingNeeded(orderNumber, antcallElement, componentAbbreviation);
                    abbreviationInDescriptor.setNeedToSorting(isComponentSortingNeeded);
                    result.add(abbreviationInDescriptor);
                    orderNumber++;
                }

            }
        }
        return result;
    }

    /**
     * Помечает дублирующиеся аббревиатуры как не требующие сортировки.
     * @param abbreviationsInDescriptorLIst данные об аббревиатурах компонентов.
     */
    public void markDuplicatedAbbreviationsAsNotNeededForSorting(
            @NonNull final AbbreviationsInDescriptorList abbreviationsInDescriptorLIst)
    {
        final List<String> abbreviationsWOVersion = abbreviationsInDescriptorLIst.getAbbreviationsWOVersion();
        final Set<String> duplicates = CollectionUtils.getDuplicates(abbreviationsWOVersion);
        abbreviationsInDescriptorLIst.stream()
                .filter(AbbreviationInDescriptor::getNeedToSorting)
                .filter(it -> duplicates.contains(it.getAbbreviationWOVersion()))
                .forEach(abbreviationInDescriptor -> {
                    this.printWarningMessageForDuplicateComponents(abbreviationInDescriptor);
                    abbreviationInDescriptor.setNeedToSorting(false);
                });
    }

    /**
     * Помечает дублирующиеся аббревиатуры как не требующие сортировки.
     * @param abbreviationsInDescriptorLIst данные об аббревиатурах компонентов.
     * @param abbreviationsRightOrder правильный порядок аббревиатур.
     */
    public void markAbbreviationsWORightOrderAsNotNeededForSorting(
            @NonNull final AbbreviationsInDescriptorList abbreviationsInDescriptorLIst,
            @NonNull final AbbreviationWithOrderNumberList abbreviationsRightOrder)
    {
        final List<String> orderedAbbreviations = abbreviationsRightOrder.getAbbreviations();
        final List<String> nonOrderedAbbreviations = abbreviationsInDescriptorLIst.stream()
                .filter(AbbreviationInDescriptor::getNeedToSorting)
                .filter(it -> !orderedAbbreviations.contains(it.getAbbreviationWOVersion()))
                .map(abbreviationInDescriptor -> {
                    abbreviationInDescriptor.setNeedToSorting(false);
                    return abbreviationInDescriptor.getAbbreviation();
                }).collect(Collectors.toList());
        System.out.println("Next components did not find in order file: "
                + StringUtils.join(nonOrderedAbbreviations)
                + ". They will be put on the same position."
                + "You can add needed components to componentsMap.txt file" +
                "if you sure that this components exist in order file." +
                "Format of adding is: component_name;abbreviation.");
        FileUtils.writeLines(new File("output/unFoundPatches.txt"), nonOrderedAbbreviations);
    }

    /**
     * @param orderNumber порядковый номер аббревиатуры компонента в файле install.xml.
     * @param antcallElement данные об xml эдементе antcall.
     * @param componentAbbreviation имя аббревиатуры компонента.
     * @return требуется ли сортировка для данного компонента.
     */
    private Boolean isComponentSortingNeeded(@NonNull final Integer orderNumber,
                                             @NonNull final Element antcallElement,
                                             @NonNull final String componentAbbreviation)
    {
        final Element antcallParent = antcallElement.getParentElement();
        if (antcallParent.getName().equals("target")
                && antcallParent.getAttributeValue("name").equals("process"))
        {
            return true;
        } else {
            System.out.println("Component " + componentAbbreviation + " has a big nesting level." +
                    "This component will be put on the same position." +
                    "Component position is : " + orderNumber);
            return false;
        }
    }

    /**
     * Печатает сообщение для дублирующихся компонентов в дескрипторе.
     * @param duplicatedAbbreviationInDescriptor дублирующаяся аббревиатура.
     */
    private void printWarningMessageForDuplicateComponents(
            @NonNull final AbbreviationInDescriptor duplicatedAbbreviationInDescriptor)
    {
        final String abbreviationWOVersion = duplicatedAbbreviationInDescriptor.getAbbreviationWOVersion();
        if (abbreviationWOVersion.matches("[0-9]+")) {
            // Если аббревиатура начинается с цифр, например 60_10.5.3_DT.NGSSM.SMK.Fulfillment.RC18.1.Upgrade,
            // то это компонент проекта, оставляем его на том же месте.
            System.out.println("Project components will be put on the same position.");
        } else {
            System.out.println("WARNING!!! There are duplicates components" +
                    "in package descriptor files with name: " + abbreviationWOVersion);
        }
    }
}
