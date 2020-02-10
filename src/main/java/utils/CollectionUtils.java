package utils;

import com.codepoetics.protonpack.StreamUtils;
import lombok.NonNull;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class CollectionUtils {

    public static <T> Set<T> getDuplicates(@NonNull final List<T> list) {
        final Set<T> uniqueElements = new HashSet<>();
        return list.stream()
                .filter(element -> !uniqueElements.add(element))
                .collect(Collectors.toSet());
    }

    /**
     * Сшивает две коллекции одинаковой размерности в коллекцию пар соответствующих элементов.
     */
    public static<First, Second> Collection<Pair<First, Second>> zipToPairs(@NonNull final Collection<First> first,
                                                                            @NonNull final Collection<Second> second)
    {
        return StreamUtils
                .zip(first.stream(), second.stream(), Pair::of)
                .collect(Collectors.toList());

    }
}
