package util

import java.util.logging.Logger

interface Loggable {
    val logger: Logger get() = Logger.getLogger(javaClass.name)
}