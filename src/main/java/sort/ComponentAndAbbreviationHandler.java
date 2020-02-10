package sort;

import utils.CollectionUtils;
import utils.FileUtils;
import dto.ComponentAndAbbreviation;
import dto.ComponentsAndAbbreviationSet;
import lombok.NonNull;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Для обработки данных о компонентах и их аббревиатурах.
 */
public class ComponentAndAbbreviationHandler {

    /**
     * @return сформированный на основе componentsMap.txt список, содержащий имя компонента
     * и соответствующую ему аббревиатуру.
     */
    public ComponentsAndAbbreviationSet getComponentsAndAbbreviations () {
        final Collection<String> concatenatedComponentsAndAbbreviations =
                FileUtils.getFileAsStrings(new File("componentsMap.txt"));

        final ComponentsAndAbbreviationSet componentsAndAbbreviations = concatenatedComponentsAndAbbreviations.stream()
                .map(this::splitComponentsAndAbbreviations)
                .collect(Collectors.toCollection(ComponentsAndAbbreviationSet::new));

        this.validateComponentsAndAbbreviations(componentsAndAbbreviations);

        return componentsAndAbbreviations;
    }

    /**
     *
     * @param concatenatedComponentAndAbbreviation склеенные вместе имя компонента и аббревиатуры вида
     *                                             name;abbreviation
     * @return экземпляр {@link ComponentAndAbbreviation}, созданный из полученных разделением имени компонента и его
     * аббревиатуры.
     */
    private ComponentAndAbbreviation splitComponentsAndAbbreviations(
            @NonNull final String concatenatedComponentAndAbbreviation)
    {
        final String componentName = concatenatedComponentAndAbbreviation.replaceFirst(";.*", "");
        final String componentAbbreviation = concatenatedComponentAndAbbreviation.replaceFirst(".*;", "");
        return new ComponentAndAbbreviation(componentName, componentAbbreviation);
    }

    private void validateComponentsAndAbbreviations(
            @NonNull final ComponentsAndAbbreviationSet componentsAndAbbreviations)
    {
        final Set<String> duplicatedComponentNames =
                CollectionUtils.getDuplicates(componentsAndAbbreviations.getComponentNames());
        final Set<String> duplicatedComponentAbbreviations =
                CollectionUtils.getDuplicates(componentsAndAbbreviations.getComponentAbbreviations());

        if(!duplicatedComponentNames.isEmpty() || !duplicatedComponentAbbreviations.isEmpty()) {
            String errorMessage = StringUtils.EMPTY;
            if (!duplicatedComponentNames.isEmpty()) {
                errorMessage = "ERROR!!! componentsMap.txt contains records with same component name "
                        +"and different component abbreviations."
                        + "Component names are: " + StringUtils.join(duplicatedComponentNames, ",")
                        + ". Please delete wrong records";
            }
            if (!duplicatedComponentAbbreviations.isEmpty()) {
                errorMessage = "ERROR!!! componentsMap.txt contains records with same component abbreviation "
                        +"and different component names."
                        + "Component abbreviations are: " + StringUtils.join(duplicatedComponentAbbreviations, ",")
                        + ". Please delete wrong records";
            }
            throw new RuntimeException(errorMessage);
        }
    }
}
