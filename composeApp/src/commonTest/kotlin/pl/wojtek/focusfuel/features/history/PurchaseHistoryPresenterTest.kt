package pl.wojtek.focusfuel.features.history

import arrow.core.left
import arrow.core.right
import com.slack.circuit.test.FakeNavigator
import com.slack.circuit.test.test
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.LocalDateTime
import pl.wojtek.focusfuel.repository.Purchase
import pl.wojtek.focusfuel.repository.ShopRepository
import pl.wojtek.focusfuel.util.BaseTest
import pl.wojtek.focusfuel.util.datetime.DateTimeFormatter
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class PurchaseHistoryPresenterTest : BaseTest() {
    private val mockPurchases = listOf(
        Purchase(
            purchaseId = 1,
            productName = "Test Product",
            costInPomodoros = 10,
            date = LocalDateTime(2024, 1, 1, 12, 0),
            used = false,
            productId = 10L
        ),
        Purchase(
            purchaseId = 2,
            productName = "Test Product 2",
            costInPomodoros = 20,
            date = LocalDateTime(2024, 2, 1, 12, 0),
            used = true,
            productId = 20L
        )
    )
    private val navigator = FakeNavigator(PurchaseHistoryScreen)
    private val shopRepository = mockk<ShopRepository> {
        coEvery { getPurchases() } returns flowOf(mockPurchases.right())
        coEvery { updatePurchaseUsedStatus(any(), any()) } returns Unit.right()
    }
    private val dateTimeFormatter = mockk<DateTimeFormatter> {
        coEvery { getFormattedDateTime(any()) } returns "2024-01-01 12:00"
    }

    private fun createSut() = PurchaseHistoryPresenter(
        shopRepository = shopRepository,
        dateTimeFormatter = dateTimeFormatter,
        navigator = navigator
    )

    @Test
    fun `Given presenter is created, when initial state is observed, then shows loading followed by purchases`() =
        runTest {
            createSut().test {
                val initialState = awaitItem()
                assertEquals(true, initialState.isLoading)
                assertEquals(emptyList(), initialState.purchases)
                assertNull(initialState.error)

                val loadedState = expectMostRecentItem()
                assertEquals(false, loadedState.isLoading)
                assertEquals(2, loadedState.purchases.size)
                assertEquals(mockPurchases[0].productName, loadedState.purchases[0].productName)
                assertEquals(mockPurchases[0].costInPomodoros, loadedState.purchases[0].price)
                assertEquals(mockPurchases[0].used, loadedState.purchases[0].used)
            }
        }

    @Test
    fun `Given purchase exists, when used status is updated, then repository is called`() = runTest {
        createSut().test {
            expectMostRecentItem().eventSink(PurchaseHistoryEvent.UpdateUsedStatus(purchaseId = 1, used = true))

            coVerify(exactly = 1) { shopRepository.updatePurchaseUsedStatus(1, true) }
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `Given update fails, when used status is updated, then show and hide error`() = runTest {
        val error = RuntimeException("Update failed")
        coEvery { shopRepository.updatePurchaseUsedStatus(any(), any()) } returns error.left()

        createSut().test {
            expectMostRecentItem().eventSink(PurchaseHistoryEvent.UpdateUsedStatus(purchaseId = 1, used = true))

            val errorState = expectMostRecentItem()
            assertEquals(error, errorState.error)

            advanceTimeBy(3001)
            assertNull(expectMostRecentItem().error)
        }
    }
} 
