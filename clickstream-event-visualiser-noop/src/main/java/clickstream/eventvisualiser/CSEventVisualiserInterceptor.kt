package clickstream.eventvisualiser

import clickstream.interceptor.CSEventInterceptor
import clickstream.interceptor.CSInterceptedEvent


/**
 * An Clickstream [CSEventInterceptor] that delegate the intercepted event handling to [CSEventVisualiser].
 * Can be applied to [CSConfiguration.Builder.addInterceptor].
 *
 * Use [getInstance] to create and get a singleton instance.
 *
 * @property csEventObserver
 */
public class CSEventVisualiserInterceptor private constructor() : CSEventInterceptor {

    public companion object {
        @Volatile
        private lateinit var csEventInterceptor: CSEventVisualiserInterceptor
        private var lock = Any()

        public fun getInstance(): CSEventVisualiserInterceptor {
            if (!::csEventInterceptor.isInitialized) {
                synchronized(lock) {
                    if (!::csEventInterceptor.isInitialized) {
                        csEventInterceptor = CSEventVisualiserInterceptor()
                    }
                }
            }
            return csEventInterceptor
        }
    }

    override fun onIntercept(events: List<CSInterceptedEvent>) {
        /*NoOp*/
    }
}