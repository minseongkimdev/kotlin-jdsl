package com.linecorp.kotlinjdsl.query.spec

import com.linecorp.kotlinjdsl.query.spec.expression.EntitySpec
import javax.persistence.criteria.From
import javax.persistence.criteria.Root

class Froms internal constructor(
    val root: Root<*>,
    private val map: Map<EntitySpec<*>, From<*, *>>
) {
    @Suppress("UNCHECKED_CAST")
    operator fun <T> get(key: EntitySpec<T>): From<*, T> =
        map[key] as? From<*, T>
            ?: throw IllegalStateException("There is no $key in from or join clause. contains: ${map.keys}")

    operator fun plus(other: Froms): Froms {
        val duplicatedEntities = map.keys.intersect(other.map.keys)

        if (duplicatedEntities.isNotEmpty()) {
            throw IllegalStateException(
                "Other froms has duplicated entitySpec. Please alias the duplicated entities: $duplicatedEntities"
            )
        }

        return Froms(root, map + other.map)
    }
}