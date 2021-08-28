package com.aline.core.util;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.metamodel.Attribute;
import java.util.Arrays;

/**
 * The SearchSpecification class is used to return
 * a specified list of elements based on the search term.
 * The search term searches through all columns of the entity
 * that are strings and if at least one term is matched in any
 * of the string columns, it will return a list with that entity
 * in it.
 * @param <T> The entity type that a search is being applied to.
 */
@RequiredArgsConstructor
public class SimpleSearchSpecification<T> implements Specification<T> {

    private final String search;

    @Override
    public Predicate toPredicate(Root<T> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder cb) {
        String[] searchTerms = search.split("[\\s,]");

        Predicate[] predicates = Arrays.stream(searchTerms)
                .map(String::toLowerCase)
                .flatMap(searchTerm -> root.getModel().getAttributes().stream()
                        .filter(attribute -> attribute.getJavaType().getSimpleName().equals("String"))
                        .map(Attribute::getName)
                        .map(attributeName -> cb.like(cb.lower(root.get(attributeName)), "%" + searchTerm + "%")))
                .toArray(Predicate[]::new);

        return cb.or(predicates);
    }
}
