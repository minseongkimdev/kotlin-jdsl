package com.linecorp.kotlinjdsl.query.clause.where

import com.linecorp.kotlinjdsl.query.spec.Froms
import com.linecorp.kotlinjdsl.query.spec.predicate.PredicateSpec
import com.linecorp.kotlinjdsl.test.WithKotlinJdslAssertions
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import javax.persistence.criteria.CriteriaBuilder
import javax.persistence.criteria.CriteriaQuery
import javax.persistence.criteria.Predicate
import javax.persistence.criteria.Subquery

@ExtendWith(MockKExtension::class)
internal class WhereClauseTest : WithKotlinJdslAssertions {
    @MockK
    private lateinit var froms: Froms

    @MockK
    private lateinit var subquery: Subquery<Int>

    @MockK
    private lateinit var criteriaQuery: CriteriaQuery<Int>

    @MockK
    private lateinit var criteriaBuilder: CriteriaBuilder

    @Test
    fun `apply criteria query`() {
        // given
        val predicateSpec: PredicateSpec = mockk()
        val predicate: Predicate = mockk()

        every { predicateSpec.isEmpty() } returns false
        every { predicateSpec.toCriteriaPredicate(froms, criteriaQuery, criteriaBuilder) } returns predicate
        every { criteriaQuery.where(predicate) } returns criteriaQuery

        // when
        WhereClause(predicateSpec).apply(froms, criteriaQuery, criteriaBuilder)

        // then
        verify(exactly = 1) {
            predicateSpec.isEmpty()
            predicateSpec.toCriteriaPredicate(froms, criteriaQuery, criteriaBuilder)
            criteriaQuery.where(predicate)

            criteriaQuery.where(predicate)
        }

        confirmVerified(predicateSpec, froms, subquery, criteriaQuery, criteriaBuilder)
    }

    @Test
    fun `apply criteria query - if predicate is empty then do nothing`() {
        // when
        WhereClause(PredicateSpec.empty).apply(froms, criteriaQuery, criteriaBuilder)

        // then
        confirmVerified(froms, subquery, criteriaQuery, criteriaBuilder)
    }

    @Test
    fun `apply subquery`() {
        // given
        val predicateSpec: PredicateSpec = mockk()
        val predicate: Predicate = mockk()

        every { predicateSpec.isEmpty() } returns false
        every { predicateSpec.toCriteriaPredicate(froms, subquery, criteriaBuilder) } returns predicate
        every { subquery.where(predicate) } returns subquery

        // when
        WhereClause(predicateSpec).apply(froms, subquery, criteriaBuilder)

        // then
        verify(exactly = 1) {
            predicateSpec.isEmpty()
            predicateSpec.toCriteriaPredicate(froms, subquery, criteriaBuilder)
            subquery.where(predicate)

            subquery.where(predicate)
        }

        confirmVerified(predicateSpec, froms, subquery, subquery, criteriaBuilder)
    }

    @Test
    fun `apply subquery - if predicate is empty then do nothing`() {
        // when
        WhereClause(PredicateSpec.empty).apply(froms, subquery, criteriaBuilder)

        // then
        confirmVerified(froms, subquery, subquery, criteriaBuilder)
    }
}