package uz.ttpu.permissionslab

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class Task1PermissionModelTest {

    @Test
    fun testPermissionModelLifecycle() {
        val model = PermissionModel()

        // Initial state
        assertFalse(model.granted)
        assertFalse(model.rationale)
        assertTrue(model.dialogShows)

        // 1st denial: rationale becomes true
        model.request(userAllows = false)
        assertFalse(model.granted)
        assertTrue(model.rationale)
        assertTrue(model.dialogShows)

        // 2nd denial: permanently denied, rationale becomes false, dialogShows becomes false
        model.request(userAllows = false)
        assertFalse(model.granted)
        assertFalse(model.rationale)
        assertFalse(model.dialogShows)

        // 3rd request after 2 denials: no dialog shows, state unchanged
        model.request(userAllows = false)
        assertFalse(model.granted)
        assertFalse(model.rationale)
        assertFalse(model.dialogShows)

        // Attempting to allow after 2 denials: dialog is disabled, state remains denied
        model.request(userAllows = true)
        assertFalse(model.granted)
        assertFalse(model.rationale)
        assertFalse(model.dialogShows)
    }
}
