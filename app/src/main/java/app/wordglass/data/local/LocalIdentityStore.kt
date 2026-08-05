package app.wordglass.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

// One DataStore file for the device's local identity. The delegate is tied to the (single)
// application context, so it is effectively a singleton store.
private val Context.identityDataStore: DataStore<Preferences> by
    preferencesDataStore(name = "local_identity")

/**
 * The device's local identity for slice 01: a solo-org id and a local user id (`createdBy`),
 * **both generated once at first launch and persisted** — never compile-time constants
 * (handoff addendum, ruling 5 / B3). A hardcoded UUID would put every install in the same org
 * and collide unrelated users' scripts when slice-13 sync lands.
 *
 * Slice 13 remaps these to the real org/user on sign-in; keeping them stable per install is
 * what makes that adoption deterministic. This is the accessor layer — the repository reads
 * `orgId()` / `userId()` from here, never a literal.
 */
@Singleton
class LocalIdentityStore @Inject constructor(
    @param:ApplicationContext private val context: Context,
) {
    private object Keys {
        val ORG_ID = stringPreferencesKey("org_id")
        val USER_ID = stringPreferencesKey("user_id")
    }

    /** The solo-org id for this install. Generated + persisted on first call. */
    suspend fun orgId(): String = getOrCreate(Keys.ORG_ID)

    /** The local user id stamped as `createdBy`. Generated + persisted on first call. */
    suspend fun userId(): String = getOrCreate(Keys.USER_ID)

    // Read-or-create atomically INSIDE the edit transaction, so two concurrent callers can't
    // generate two different UUIDs for the same key.
    private suspend fun getOrCreate(key: Preferences.Key<String>): String {
        var value = ""
        context.identityDataStore.edit { prefs ->
            value = prefs[key] ?: UUID.randomUUID().toString().also { prefs[key] = it }
        }
        return value
    }
}
