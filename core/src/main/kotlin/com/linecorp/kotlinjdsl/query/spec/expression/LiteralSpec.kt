package com.linecorp.kotlinjdsl.query.spec.expression

import com.linecorp.kotlinjdsl.query.spec.Froms
import javax.persistence.criteria.AbstractQuery
import javax.persistence.criteria.CriteriaBuilder
import javax.persistence.criteria.Expression

data class LiteralSpec<T : Any>(
    val value: T,
) : ExpressionSpec<T> {
    override fun toCriteriaExpression(
        froms: Froms,
        query: AbstractQuery<*>,
        criteriaBuilder: CriteriaBuilder
    ): Expression<T> = criteriaBuilder.literal(value)
}
