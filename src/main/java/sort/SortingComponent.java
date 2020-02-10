package sort;


import utils.CollectionUtils;
import dto.AbbreviationInDescriptor;
import dto.AbbreviationWithOrderNumber;
import dto.AbbreviationWithOrderNumberList;
import dto.AbbreviationsInDescriptorList;
import dto.ComponentsAndAbbreviationSet;
import lombok.NonNull;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class SortingComponent {

    private final ComponentAndAbbreviationHandler componentAndAbbreviationHandler;
    private final AbbreviationRightOrderHandler abbreviationRightOrderHandler;
    private final AbbreviationInDescriptorHandler abbreviationInDescriptorHandler;

    public SortingComponent() {
        this.componentAndAbbreviationHandler = new ComponentAndAbbreviationHandler();
        this.abbreviationRightOrderHandler = new AbbreviationRightOrderHandler();
        this.abbreviationInDescriptorHandler = new AbbreviationInDescriptorHandler();
    }

    public List<AbbreviationWithOrderNumber> sort () {

        final AbbreviationWithOrderNumberList abbreviationsRightOrder = this.getAbbreviationsRightOrder();
        final AbbreviationsInDescriptorList abbreviationsInDescriptor =
                this.getAbbreviationsInDescriptor(abbreviationsRightOrder);

        final List<AbbreviationWithOrderNumber> result = this.sort(abbreviationsRightOrder, abbreviationsInDescriptor);
        result.addAll(abbreviationsInDescriptor.getNotNeededToSortRecords());

        return result;
    }

    private AbbreviationWithOrderNumberList getAbbreviationsRightOrder() {
        final ComponentsAndAbbreviationSet componentsAndAbbreviations =
                this.componentAndAbbreviationHandler.getComponentsAndAbbreviations();
        return this.abbreviationRightOrderHandler.getAbbreviationsRightOrder(componentsAndAbbreviations);
    }

    private AbbreviationsInDescriptorList getAbbreviationsInDescriptor(
            @NonNull final AbbreviationWithOrderNumberList abbreviationsRightOrder)
    {
        final AbbreviationsInDescriptorList abbreviationsInDescriptor =
                this.abbreviationInDescriptorHandler.getAbbreviationOrderInDescriptor();
        this.abbreviationInDescriptorHandler
                .markDuplicatedAbbreviationsAsNotNeededForSorting(abbreviationsInDescriptor);
        this.abbreviationInDescriptorHandler.markAbbreviationsWORightOrderAsNotNeededForSorting(
                abbreviationsInDescriptor,
                abbreviationsRightOrder
        );
        return abbreviationsInDescriptor;
    }

    /**
     * @param abbreviationsRightOrder правильный порядок аббревиатур.
     *                                список включает лишние аббревиатуры, которые не указаны в install.xml
     * @param abbreviationsInDescriptor порядок аббревиатур из дескриптора install.xml.
     * @return правильный порядок аббревиатур в том порядке в котором его нужно будет записать в файл install.xml.
     */
    private List<AbbreviationWithOrderNumber> sort(
            @NonNull final AbbreviationWithOrderNumberList abbreviationsRightOrder,
            @NonNull final AbbreviationsInDescriptorList abbreviationsInDescriptor)
    {
        final AbbreviationsInDescriptorList neededToSortAbbreviations =
                abbreviationsInDescriptor.getNeededToSortRecords();
        final AbbreviationWithOrderNumberList neededRightOrderAbbreviations =
                this.getNeededRightOrderAbbreviations(abbreviationsRightOrder, neededToSortAbbreviations);

        neededToSortAbbreviations.sort(Comparator.comparing(AbbreviationWithOrderNumber::getOrderNumber));
        neededRightOrderAbbreviations.sort(Comparator.comparing(AbbreviationWithOrderNumber::getOrderNumber));

        /**
         * Далее происходит следующее:
         * Полученные данные сшиваются в следующую структуру
         * Pair (имя сортируемой аббревиатуры, порядковый номер аббревиатуры в install.xml) (имя другой аббревиатуры(или той же), порядковый номер другой аббревиатуры в orderlist.txt)
         * ...............................
         * Pair(имя еще какой-то аббревиатуры, нужный порядковый номер в install.xml сортируемой аббревиатуры) (имя сортируемой аббревиатуры, порядковый номер сортируемой аббревиатуры в orderlist.txt)
         * Из этих двух пар, отыскивая их по имени сортируемой аббревиатуры мы вытаскиваем имя сортируемой аббревиатуры и ее нужный порядковый номер в install.xml.
         * Так делаем для всех аббревиатур.
         */
        final Collection<Pair<AbbreviationInDescriptor, AbbreviationWithOrderNumber>> pairsForSorting =
                CollectionUtils.zipToPairs(neededToSortAbbreviations, neededRightOrderAbbreviations);
        return pairsForSorting.stream()
                .map(it -> {
                    final Integer rightOrderNumber = this.getRightOrderNumber(
                            pairsForSorting,
                            it.getLeft().getAbbreviationWOVersion()
                    );
                    return new AbbreviationWithOrderNumber(rightOrderNumber, it.getLeft().getAbbreviation());
                }).collect(Collectors.toList());
    }

    /**
     *
     * @param abbreviationsRightOrder правильный порядок аббревиатур.
     * @param neededToSortAbbreviations аббревиатуры из install.xml, которые необходимо перемешать.
     * @return правильный порядок аббревиатур, который содержит только те аббревиатуры, которые будут сортироваться.
     */
    private AbbreviationWithOrderNumberList getNeededRightOrderAbbreviations(
            @NonNull final AbbreviationWithOrderNumberList abbreviationsRightOrder,
            @NonNull final AbbreviationsInDescriptorList neededToSortAbbreviations)
    {
        return neededToSortAbbreviations.stream()
                    .map(it -> {
                        final Optional<AbbreviationWithOrderNumber> rightOrderByAbbreviation =
                                abbreviationsRightOrder.getByAbbreviation(it.getAbbreviationWOVersion());
                        return rightOrderByAbbreviation.get();
                    }).collect(Collectors.toCollection(AbbreviationWithOrderNumberList::new));
    }

    private Integer getRightOrderNumber(
            @NonNull final Collection<Pair<AbbreviationInDescriptor, AbbreviationWithOrderNumber>> pairs,
            @NonNull final String abbreviationName)
    {
        return pairs.stream()
                .filter(it -> abbreviationName.equals(it.getRight().getAbbreviation()))
                .findFirst().get().getLeft().getOrderNumber();
    }

}
