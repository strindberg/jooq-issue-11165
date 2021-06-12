package se.jh.jooqissue.repo

import org.jooq.DSLContext
import org.jooq.impl.DSL.field
import org.jooq.impl.DSL.jsonObject
import org.jooq.impl.DSL.select
import org.springframework.stereotype.Repository
import se.jh.jooqissue.db.tables.references.CHILD
import se.jh.jooqissue.db.tables.references.PARENT

@Repository
class ParentRepository(private val dsl: DSLContext) {

    fun save(parent: Parent): Long {
        val record = dsl.newRecord(PARENT, parent)
        record.insert()
        saveChild(parent.child, record.id!!)
        return record.id!!
    }

    private fun saveChild(child: Child, parentId: Long) {
        val record = dsl.newRecord(CHILD, child)
        record.parentId = parentId
        record.insert()
    }

    fun getSimpleById(id: Long): Parent? =
        dsl.select(
            listOf(
                PARENT.ID,
                PARENT.NAME,
                field(
                    select(jsonObject(CHILD.NAME))
                        .from(CHILD).where(
                            CHILD.PARENT_ID.eq(PARENT.ID)
                        )
                ).`as`("child"),
            )
        )
            .from(PARENT).where(PARENT.ID.eq(id)).fetchOne()?.into(Parent::class.java)
}

data class Parent(val id: Long? = null, val name: String, val child: Child)
data class Child(val name: String)
