package dto;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;

@EqualsAndHashCode(callSuper = true)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AbbreviationInDescriptor extends AbbreviationWithOrderNumber {

    /**
     * Требуется ли сортировать аббревиатуру компонента.
     */
    private Boolean needToSorting;

    public AbbreviationInDescriptor(@NonNull final Integer orderNumber, @NonNull final String component) {
        super(orderNumber, component);
    }

    /**
     * @return аббревиатура компонента без версии например configuration-summary_9.3.1.1 -> configuration-summary.
     */
    public String getAbbreviationWOVersion() {
        return this.getAbbreviation().replaceFirst("_[\\d].*","");
    }
}
