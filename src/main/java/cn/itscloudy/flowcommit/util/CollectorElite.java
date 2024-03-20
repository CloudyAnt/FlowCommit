package cn.itscloudy.flowcommit.util;

import java.util.*;
import java.util.function.*;

/**
 * Collection Utils
 */
public class CollectorElite {

    private CollectorElite() {
    }

    public static <O, K, V> Map<K, V> map(Collection<O> collection, Function<O, K> keyMapper,
                                          Function<O, V> valueMapper) {
        HashMap<K, V> map = new HashMap<>(collection.size());
        for (O o : collection) {
            map.put(keyMapper.apply(o), valueMapper.apply(o));
        }
        return map;
    }

    public static <O, K> Map<K, O> map(Collection<O> collection, Function<O, K> keyMapper) {
        return map(collection, keyMapper, v -> v);
    }

    public static <O, K> Map<K, List<O>> classify(Collection<O> collection, Function<O, K> keyMapper) {
        return classify(collection, keyMapper, o -> o);
    }

    public static <O, K, V> Map<K, List<V>> classify(Collection<O> collection, Function<O, K> keyMapper,
                                                     Function<O, V> valueMapper) {
        Set<K> keys = mapSet(collection, keyMapper);
        HashMap<K, List<V>> map = new HashMap<>(keys.size());
        for (K key : keys) {
            map.put(key, new ArrayList<>());
        }

        for (O o : collection) {
            K key = keyMapper.apply(o);
            map.get(key).add(valueMapper.apply(o));
        }

        return map;
    }

    public static <A, B> List<B> mapList(Collection<A> collection, Function<A, B> bMapper) {
        ArrayList<B> list = new ArrayList<>(collection.size());
        for (A a : collection) {
            list.add(bMapper.apply(a));
        }

        return list;
    }

    public static <A, B> List<B> mapList(Collection<A> collection, Function<A, B> bMapper, Predicate<A> predicate) {
        ArrayList<B> list = new ArrayList<>(collection.size());
        for (A a : collection) {
            if (predicate.test(a)) {
                list.add(bMapper.apply(a));
            }
        }

        return list;
    }

    public static <A, B> List<B> mapFlatList(Collection<A> collection, Function<A, Collection<B>> bsMapper) {
        ArrayList<B> list = new ArrayList<>(collection.size());
        for (A a : collection) {
            list.addAll(bsMapper.apply(a));
        }
        return list;
    }

    public static <A, B> Set<B> mapSet(Collection<A> collection, Function<A, B> bMapper) {
        HashSet<B> set = new HashSet<>(collection.size());
        for (A a : collection) {
            set.add(bMapper.apply(a));
        }

        return set;
    }

    public static <A, B> Set<B> mapFlatSet(Collection<A> collection, Function<A, Collection<B>> bsMapper) {
        HashSet<B> set = new HashSet<>(collection.size());
        for (A a : collection) {
            set.addAll(bsMapper.apply(a));
        }
        return set;
    }

    public static <A> List<A> cyclicSubList(List<A> list, int from, int amount) {
        if (list.isEmpty()) {
            return Collections.emptyList();
        }

        int collectionSize = list.size();
        from = from % collectionSize;
        int to = from + amount;

        List<A> sub = list.subList(from, Math.min(collectionSize, to));
        while (sub.size() < amount) {
            sub.addAll(list.subList(0, Math.min(collectionSize, amount - sub.size())));
        }
        return sub;
    }

    /**
     * Different from {@link List#subList(int, int)}, this method will never throw an exception,
     * and always return a list that contains existent elements <p>
     * Presuming a list has elements [1, 2, 3], subAmount(list, -1, 3) would return [1, 2],
     * and subAmount(list, 3, 3) would return []
     */
    public static <A> List<A> subAmount(List<A> list, int from, int amount) {
        if (list.isEmpty()) {
            return Collections.emptyList();
        }

        int collectionSize = list.size();
        int to = from + amount;
        return list.subList(Math.min(collectionSize, from), Math.min(collectionSize, to));
    }

    public static <A> List<A> intersect(Collection<A> collection, Collection<A> otherCollection) {
        HashSet<A> distinctCollection = new HashSet<>(collection);
        ArrayList<A> intersection = new ArrayList<>();
        for (A a : distinctCollection) {
            if (otherCollection.contains(a)) {
                intersection.add(a);
            }
        }
        return intersection;
    }

    public static <A> List<A> intersect(Collection<A> collection, Collection<A> otherCollection,
                                        BiPredicate<A, A> comparator) {
        List<A> distinctCollection = distinctByComparator(collection, comparator);
        ArrayList<A> intersection = new ArrayList<>();
        iterateAndFindMatch(distinctCollection, otherCollection, comparator, (match, a) -> {
            if (match) {
                intersection.add(a);
            }
        });
        return intersection;
    }

    public static <A> List<A> difference(Collection<A> collection, Collection<A> otherCollection) {
        ArrayList<A> collectionList = new ArrayList<>(collection);
        for (A a : otherCollection) {
            collectionList.remove(a);
        }
        return collectionList;
    }

    public static <A> List<A> difference(Collection<A> collection, Collection<A> otherCollection,
                                         BiPredicate<A, A> comparator) {

        ArrayList<A> collectionList = new ArrayList<>(collection);
        for (A a : otherCollection) {
            collectionList.removeIf(b -> comparator.test(a, b));
        }
        return collectionList;
    }

    public static <A> List<A> filter(Collection<A> collection, Predicate<A> filter) {
        ArrayList<A> matchedItems = new ArrayList<>();
        for (A a : collection) {
            if (filter.test(a)) {
                matchedItems.add(a);
            }
        }
        return matchedItems;
    }

    public static <A> List<A> filter(Collection<A> collection, Predicate<A> filter,
                                     Collection<A> restCollection) {
        ArrayList<A> matchedItems = new ArrayList<>();
        for (A a : collection) {
            if (filter.test(a)) {
                matchedItems.add(a);
            } else {
                restCollection.add(a);
            }
        }
        return matchedItems;
    }

    public static <A, F> int count(Collection<A> collection, Function<A, F> fMapper) {
        Set<F> fs = new HashSet<>();
        for (A a : collection) {
            F f = fMapper.apply(a);
            fs.add(f);
        }
        return fs.size();
    }

    public static <A, B> Set<A> mapKeysByValue(Map<A, B> map, Predicate<B> bPredicate) {
        HashSet<A> set = new HashSet<>();
        map.forEach((key, value) -> {
            if (bPredicate.test(value)) {
                set.add(key);
            }
        });
        return set;
    }

    public static <A, B> B reduce(Collection<A> as, Predicate<A> aPredicate, B identity,
                                  BiFunction<A, B, B> accumulator) {
        for (A a : as) {
            if (aPredicate.test(a)) {
                identity = accumulator.apply(a, identity);
            }
        }
        return identity;
    }

    public static <A, B> B reduce(Collection<A> as, B identity, BiFunction<A, B, B> accumulator) {
        for (A a : as) {
            identity = accumulator.apply(a, identity);
        }
        return identity;
    }

    public static String join(Collection<?> collection, String separator) {
        boolean firstSet = false;
        StringBuilder sb = new StringBuilder();

        for (Object o : collection) {
            if (firstSet) {
                sb.append(separator);
            } else {
                firstSet = true;
            }
            sb.append(o);
        }
        return sb.toString();
    }

    public static <A> List<A> distinctByComparator(Collection<A> collection, BiPredicate<A, A> comparator) {
        ArrayList<A> distinctCollection = new ArrayList<>();
        iterateAndFindMatch(collection, distinctCollection, comparator, (match, a) -> {
            if (!match) {
                distinctCollection.add(a);
            }
        });
        return distinctCollection;
    }

    /**
     * for each provider item, find the matched item in base
     */
    private static <A> void iterateAndFindMatch(Collection<A> provider, Collection<A> base,
                                                BiPredicate<A, A> comparator,
                                                ResultConsumer<A> resultConsumer) {
        for (A a : provider) {
            boolean match = false;
            for (A a1 : base) {
                if (comparator.test(a, a1)) {
                    match = true;
                    break;
                }
            }
            resultConsumer.accept(match, a);
        }
    }

    @FunctionalInterface
    private interface ResultConsumer<A> {
        void accept(boolean result, A a);
    }

    public static <A> Optional<A> findAny(Collection<A> as, Predicate<A> predicate) {
        for (A a : as) {
            if (predicate.test(a)) {
                return Optional.of(a);
            }
        }
        return Optional.empty();
    }

    public static <A, F extends Comparable<F>> F findMaxField(Collection<A> as, Function<A, F> fieldGetter) {
        return findUltra(as, fieldGetter, i -> i > 0);
    }

    public static <A, F extends Comparable<F>> F findMinField(Collection<A> as, Function<A, F> fieldGetter) {
        return findUltra(as, fieldGetter, i -> i < 0);
    }

    public static <A, F extends Comparable<F>> F findUltra(Collection<A> as, Function<A, F> fieldGetter,
                                                           IntPredicate predicate) {
        F ultra = null;
        for (A a : as) {
            F f = fieldGetter.apply(a);
            if (f != null && (ultra == null || predicate.test(f.compareTo(ultra)))) {
                ultra = f;
            }
        }
        return ultra;
    }

    public static <A, F extends Comparable<? super F>> List<A> ascend(Collection<A> as, Function<A, F> fieldGetter) {
        ArrayList<A> aArrayList = new ArrayList<>(as);
        aArrayList.sort(Comparator.comparing(fieldGetter));
        return aArrayList;
    }

    public static <A, F extends Comparable<? super F>> List<A> descend(Collection<A> as, Function<A, F> fieldGetter) {
        ArrayList<A> aArrayList = new ArrayList<>(as);
        aArrayList.sort(Comparator.comparing(fieldGetter).reversed());
        return aArrayList;
    }
}