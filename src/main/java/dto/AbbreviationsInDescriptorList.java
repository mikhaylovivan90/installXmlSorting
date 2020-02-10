package dto;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class AbbreviationsInDescriptorList extends ArrayList<AbbreviationInDescriptor> {

    public List<String> getAbbreviationsWOVersion() {
        return this.stream()
                .map(AbbreviationInDescriptor::getAbbreviationWOVersion)
                .collect(Collectors.toList());
    }

    public AbbreviationsInDescriptorList getNeededToSortRecords() {
        return this.stream()
                .filter(AbbreviationInDescriptor::getNeedToSorting)
                .collect(Collectors.toCollection(AbbreviationsInDescriptorList::new));
    }

    public AbbreviationsInDescriptorList getNotNeededToSortRecords() {
        return this.stream()
                .filter(it -> !it.getNeedToSorting())
                .collect(Collectors.toCollection(AbbreviationsInDescriptorList::new));
    }
}
