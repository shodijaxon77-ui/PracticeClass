package uz.ttpu.permissionslab

/**
 * Task 1. Kotlin model: denial states
 * Represents the permission state lifecycle and transition logic based on user interaction.
 */
class PermissionModel {
    var granted: Boolean = false
        private set

    var rationale: Boolean = false
        private set

    var dialogShows: Boolean = true
        private set

    private var denialCount: Int = 0

    /**
     * Simulates requesting permission from the user.
     * @param userAllows true if user grants permission in the dialog, false if denied.
     */
    fun request(userAllows: Boolean) {
        if (!dialogShows) {
            println("Request result: no dialog, denied immediately")
            return
        }

        if (userAllows) {
            granted = true
            rationale = false
            dialogShows = false
            println("Request result: User ALLOWED the permission")
        } else {
            denialCount++
            granted = false
            if (denialCount == 1) {
                rationale = true
                dialogShows = true
                println("Request result: User DENIED (1st time)")
            } else if (denialCount >= 2) {
                rationale = false
                dialogShows = false
                println("Request result: User DENIED (2nd time - Permanently Denied)")
            }
        }
    }

    fun printState(stepName: String) {
        println("[$stepName] -> $statusString")
    }

    val statusString: String
        get() = "granted=$granted, rationale=$rationale, dialogShows=$dialogShows"
}

fun main() {
    println("==================================================")
    println("Task 1: Runtime Permission Model State Simulation")
    println("==================================================")

    val model = PermissionModel()
    model.printState("Initial State")

    println("\nStep 1: 1st Request -> User DENIES")
    model.request(userAllows = false)
    model.printState("After 1st Denial")

    println("\nStep 2: 2nd Request -> User DENIES")
    model.request(userAllows = false)
    model.printState("After 2nd Denial")

    println("\nStep 3: 3rd Request -> User attempts to request again")
    model.request(userAllows = false)
    model.printState("After 3rd Attempt")

    println("\nStep 4: 4th Request -> User attempts to allow")
    model.request(userAllows = true)
    model.printState("After 4th Attempt")

    println("\nSummary matching Slide 13:")
    println("1. Initial: granted=false, rationale=false, dialogShows=true")
    println("2. Denied 1x: granted=false, rationale=true, dialogShows=true")
    println("3. Denied 2x: granted=false, rationale=false, dialogShows=false")
    println("4. Subsequent calls: no dialog, denied immediately")
    println("==================================================")
}
