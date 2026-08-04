package app.wordglass.data.repository

import app.wordglass.data.local.LocalIdentityStore
import app.wordglass.data.local.Script
import app.wordglass.data.local.ScriptDao
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ScriptRepositoryImpl @Inject constructor(
    private val dao: ScriptDao,
    private val identity: LocalIdentityStore,
) : ScriptRepository {

    override fun observeScripts() = dao.observeAll()

    override suspend fun getById(id: String): Script? = dao.getById(id)

    override suspend fun create(body: String): String {
        val id = UUID.randomUUID().toString()
        val now = System.currentTimeMillis()
        dao.upsert(
            Script(
                id = id,
                orgId = identity.orgId(),        // read from LocalIdentityStore, never a literal
                title = deriveTitle(body),
                titleSetByUser = false,          // new script's title tracks the body's first line
                body = body,
                createdBy = identity.userId(),
                createdAt = now,
                updatedAt = now,
                deletedAt = null,
            ),
        )
        return id
    }

    override suspend fun updateBody(id: String, body: String) {
        val existing = dao.getById(id) ?: return
        dao.upsert(
            existing.copy(
                // Re-derive the title from the body only while the user hasn't taken it over.
                title = if (existing.titleSetByUser) existing.title else deriveTitle(body),
                body = body,
                updatedAt = System.currentTimeMillis(), // emptying a body still bumps this (ruling 6)
            ),
        )
    }

    override suspend fun updateTitle(id: String, title: String) {
        val existing = dao.getById(id) ?: return
        dao.upsert(
            existing.copy(
                title = title,
                titleSetByUser = true, // from now on the body no longer overwrites the title
                updatedAt = System.currentTimeMillis(),
            ),
        )
    }

    override suspend fun resetTitleToAuto(id: String) {
        val existing = dao.getById(id) ?: return
        dao.upsert(
            existing.copy(
                title = deriveTitle(existing.body), // re-derive from the stored body's first line
                titleSetByUser = false,
                updatedAt = System.currentTimeMillis(),
            ),
        )
    }

    override suspend fun softDelete(id: String) = dao.softDelete(id, System.currentTimeMillis())

    /** Title = the body's first line, trimmed. Blank first line -> "" (row renders "Untitled"). */
    private fun deriveTitle(body: String): String = body.substringBefore('\n').trim()
}
