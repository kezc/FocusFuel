package pl.wojtek.focusfuel.util

import co.touchlab.kermit.CommonWriter
import co.touchlab.kermit.Logger
import kotlin.test.BeforeTest

open class BaseTest {
    @BeforeTest
    fun setup() {
        Logger.setLogWriters(CommonWriter())
    }
}
