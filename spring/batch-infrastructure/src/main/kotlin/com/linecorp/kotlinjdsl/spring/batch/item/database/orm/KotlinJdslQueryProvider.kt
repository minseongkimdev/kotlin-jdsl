package com.linecorp.kotlinjdsl.spring.batch.item.database.orm

import com.linecorp.kotlinjdsl.query.creator.CriteriaQueryCreatorImpl
import com.linecorp.kotlinjdsl.query.creator.SubqueryCreatorImpl
import com.linecorp.kotlinjdsl.spring.data.SpringDataQueryFactory
import com.linecorp.kotlinjdsl.spring.data.SpringDataQueryFactoryImpl
import com.linecorp.kotlinjdsl.spring.data.querydsl.SpringDataCriteriaQueryDsl
import com.linecorp.kotlinjdsl.spring.data.typedQuery
import org.springframework.batch.item.database.orm.JpaQueryProvider
import javax.persistence.EntityManager
import javax.persistence.Query

class KotlinJdslQueryProvider(
    private val createQuery: (queryFactory: SpringDataQueryFactory) -> Query
) : JpaQueryProvider {
    companion object {
        inline fun <reified T> typedQuery(
            noinline dsl: SpringDataCriteriaQueryDsl<T>.() -> Unit
        ): KotlinJdslQueryProvider {
            return KotlinJdslQueryProvider { it.typedQuery(dsl) }
        }
    }

    private var entityManager: EntityManager? = null

    private val queryFactory: SpringDataQueryFactory by lazy(LazyThreadSafetyMode.NONE) {
        if (entityManager == null) {
            throw IllegalStateException("There is no entityManager. Please set entityManager before create query")
        }

        SpringDataQueryFactoryImpl(
            criteriaQueryCreator = CriteriaQueryCreatorImpl(entityManager!!),
            subqueryCreator = SubqueryCreatorImpl(),
        )
    }

    override fun createQuery(): Query {
        return createQuery(queryFactory)
    }

    override fun setEntityManager(entityManager: EntityManager) {
        this.entityManager = entityManager
    }
}