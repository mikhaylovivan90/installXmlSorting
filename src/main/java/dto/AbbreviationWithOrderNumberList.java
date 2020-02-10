package dto;

import lombok.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class AbbreviationWithOrderNumberList extends ArrayList<AbbreviationWithOrderNumber> {

    public List<String> getAbbreviations() {
        return this.stream()
                .map(AbbreviationWithOrderNumber::getAbbreviation)
                .collect(Collectors.toList());
    }

    public Optional<AbbreviationWithOrderNumber> getByAbbreviation(@NonNull final String abbreviation) {
        return this.stream()
                .filter(it -> it.getAbbreviation().equals(abbreviation))
                .findFirst();
    }
}
