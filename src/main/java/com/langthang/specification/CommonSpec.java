package com.langthang.specification;

import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;

import java.util.HashMap;
import java.util.Map;

public class CommonSpec {

    /**
     * @param associations List of association to be fetched
     * @return Specification to fetch all association
     */
    public static <T> Specification<T> eagerLoad(String... associations) {
        var keyValueAssociations = new HashMap<String, JoinType>();
        for (String association : associations) {
            keyValueAssociations.put(association, JoinType.INNER);
        }
        return eagerLoad(keyValueAssociations);
    }

    public static <T> Specification<T> eagerLoad(Map<String, JoinType> associations) {
        return (root, query, builder) -> {
            associations.keySet().forEach(attributeName -> root.fetch(attributeName, associations.get(attributeName)));
            return builder.conjunction();
        };
    }
}
