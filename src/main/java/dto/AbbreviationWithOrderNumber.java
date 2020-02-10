package dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Компонент с порядковым номером.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AbbreviationWithOrderNumber {

    private Integer orderNumber;
    /**
     * Имя аббревиатуры компонента.
     */
    private String abbreviation;
}
