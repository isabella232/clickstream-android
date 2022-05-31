package clickstream.health.model

import clickstream.health.constant.CSEventNamesConstant
import java.util.Locale

/**
 * Config for HealthEventTracker, based on which the health events are handled
 *
 * @param minTrackedVersion - The minimum app version above which the event will be sent.
 * @param randomUserIdRemainder - A list with the last char of userID for whom the health events are tracked.
 */
public data class CSHealthEventConfig(
    val minTrackedVersion: String,
    val randomUserIdRemainder: List<Int>,
    val destination: List<String>,
    val verbosityLevel: String
) {
    /**
     * Checking whether the current app version is greater than the version in the config.
     */
    private fun isAppVersionGreater(appVersion: String): Boolean {
        return appVersion.isNotBlank() &&
            minTrackedVersion.isNotBlank() &&
            convertVersionToNumber(appVersion) >= convertVersionToNumber(minTrackedVersion)
    }

    /**
     * Checking whether the userID is present in the randomUserIdRemainder list
     */
    private fun isRandomUser(userId: Int): Boolean {
        return randomUserIdRemainder.isNotEmpty() && randomUserIdRemainder.contains(userId % DIVIDING_FACTOR)
    }

    /**
     * Checking whether the user is on alpha
     */
    private fun isAlpha(appVersion: String): Boolean {
        return appVersion.contains(ALPHA, true)
    }

    /**
     * With the given app version and user ID, it is
     * compared with the values in the config and the value is returned
     *
     * @param appVersion - Current app version
     * @param userId - current user id
     *
     * @return Boolean - True if the condition satisfies else false
     */
    public fun isEnabled(appVersion: String, userId: Int): Boolean {
        return isAppVersionGreater(appVersion) && (isRandomUser(userId) || isAlpha(appVersion))
    }

    /**
     * With the given event name, it returns whether the event should be sent to Clickstream or not.
     *
     * @param eventName - Current event name
     *
     * @return Boolean - True if should be sent to CS
     */
    public fun isTrackedViaClickstream(eventName: String): Boolean =
        listOf(
            CSEventNamesConstant.ClickStreamEventReceived.value,
            CSEventNamesConstant.ClickStreamEventObjectCreated.value,
            CSEventNamesConstant.ClickStreamEventCached.value,
            CSEventNamesConstant.ClickStreamEventBatchCreated.value,
            CSEventNamesConstant.ClickStreamBatchSent.value,
            CSEventNamesConstant.ClickStreamEventBatchAck.value,
            CSEventNamesConstant.ClickStreamFlushOnBackground.value
        ).contains(eventName)

    /**
     * Returns whether logging level is set to Maximum or not
     *
     * @return Boolean - True if the condition satisfies else false
     */
    public fun isVerboseLoggingEnabled(): Boolean {
        return verbosityLevel.toLowerCase(Locale.getDefault()) == MAX_VERBOSITY_LEVEL
    }

    public companion object {
        private const val DIVIDING_FACTOR: Int = 10
        private const val MULTIPLICATION_FACTOR: Int = 10
        private const val ALPHA: String = "alpha"

        public const val MAX_VERBOSITY_LEVEL: String = "maximum"

        /**
         * Creates the default instance of the config
         */
        public fun default(): CSHealthEventConfig = CSHealthEventConfig(
            minTrackedVersion = "",
            randomUserIdRemainder = emptyList(),
            destination = emptyList(),
            verbosityLevel = ""
        )

        /**
         * Converts the app version to the integer format.
         * For example,if the app version is "1.2.1.beta1", it's
         * converted as "121"
         */
        private fun convertVersionToNumber(version: String): Int {
            var versionNum: Int = 0
            version.split(".").asIterable()
                .filter { it.matches("-?\\d+(\\.\\d+)?".toRegex()) }
                .map {
                    versionNum = (versionNum * MULTIPLICATION_FACTOR) + it.toInt()
                }
            return versionNum
        }
    }
}