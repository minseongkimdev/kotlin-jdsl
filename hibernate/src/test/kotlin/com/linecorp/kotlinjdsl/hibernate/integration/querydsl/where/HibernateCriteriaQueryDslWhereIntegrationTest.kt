package com.linecorp.kotlinjdsl.hibernate.integration.querydsl.where

import com.linecorp.kotlinjdsl.test.integration.EntityManagerExtension
import com.linecorp.kotlinjdsl.test.integration.querydsl.where.AbstractCriteriaQueryDslWhereIntegrationTest
import org.junit.jupiter.api.extension.ExtendWith
import javax.persistence.EntityManager

@ExtendWith(EntityManagerExtension::class)
internal class HibernateCriteriaQueryDslWhereIntegrationTest : AbstractCriteriaQueryDslWhereIntegrationTest() {
    override lateinit var entityManager: EntityManager
}