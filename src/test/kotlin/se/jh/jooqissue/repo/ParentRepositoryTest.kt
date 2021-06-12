package se.jh.jooqissue.repo

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jooq.JooqTest
import org.springframework.context.annotation.Import
import se.jh.jooqissue.JooqConfiguration
import se.jh.jooqissue.testutil.AbstractIntegrationTest

@JooqTest
@Import(JooqConfiguration::class, ParentRepository::class)
class ParentRepositoryTest(@Autowired val parentRepo: ParentRepository) : AbstractIntegrationTest() {

    @Test
    fun `Simple retrieve works`() {
        val parent = Parent(name = "A Parent", child = Child(name = "A Child"))

        val parentId = parentRepo.save(parent)
        val retrievedParent = parentRepo.getSimpleById(parentId)

        println(retrievedParent)
    }

}
