package dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ComponentAndAbbreviation {


    /**
     * Полное название компонента. Используется в файле, задающем правильный порядок компонент orderlist.txt.
     */
    private String componentName;

    /**
     * Сокращенное название компонента. Используется в дескрипторе install.xml.
     */
    private String componentAbbreviation;
}
