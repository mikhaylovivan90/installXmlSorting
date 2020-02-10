package dto;

import lombok.NonNull;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ComponentsAndAbbreviationSet extends HashSet<ComponentAndAbbreviation> {

    public List<String> getComponentNames() {
        return this.stream()
                .map(ComponentAndAbbreviation::getComponentName)
                .collect(Collectors.toList());
    }

    public List<String> getComponentAbbreviations() {
        return this.stream()
                .map(ComponentAndAbbreviation::getComponentAbbreviation)
                .collect(Collectors.toList());
    }

    public Optional<ComponentAndAbbreviation> getElementWithEquivalentComponentName(
            @NonNull final String componentName)
    {
        return this.stream()
                .filter(it -> it.getComponentName().equals(componentName))
                .findFirst();
    }
}
