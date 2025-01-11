package pl.wojtek.focusfuel.features.shop

import arrow.core.left
import arrow.core.right
import com.slack.circuit.test.FakeNavigator
import com.slack.circuit.test.test
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runTest
import pl.wojtek.focusfuel.features.addproduct.AddProductScreen
import pl.wojtek.focusfuel.features.history.PurchaseHistoryScreen
import pl.wojtek.focusfuel.repository.Product
import pl.wojtek.focusfuel.repository.ShopRepository
import pl.wojtek.focusfuel.util.BaseTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class ShopPresenterTest : BaseTest() {
    private val mockProducts = listOf(
        Product(id = 10, name = "Test Product", costInPomodoros = 1),
        Product(id = 20, name = "Test Product 2", costInPomodoros = 2)
    )
    private val navigator = FakeNavigator(ShopScreen)
    private val shopRepository = mockk<ShopRepository> {
        coEvery { getProducts() } returns flowOf(mockProducts.right())
        coEvery { pomodoroBalance() } returns flowOf(100.right())
        coEvery { makePurchase(any()) } returns true.right()
        coEvery { hideProduct(any()) } returns Unit.right()
    }

    private fun createSut() = ShopPresenter(
        shopRepository = shopRepository,
        navigator = navigator
    )

    @Test
    fun `given presenter is created, when initial state is observed, then shows loading followed by products`() = runTest {
        createSut().test {
            val initialState = awaitItem()
            assertEquals(true, initialState.isLoading)
            assertEquals(emptyList(), initialState.products)
            assertEquals(0, initialState.availablePomodoros)
            assertNull(initialState.selectedProductToBuy)
            assertNull(initialState.selectedProductToChange)
            assertNull(initialState.orderResult)

            val loadedState = expectMostRecentItem()
            assertEquals(false, loadedState.isLoading)
            assertEquals(mockProducts, loadedState.products)
            assertEquals(100, loadedState.availablePomodoros)
        }
    }

    @Test
    fun `given shop is loaded, when product is selected to buy, then state is updated`() = runTest {
        createSut().test {
            expectMostRecentItem().eventSink(ShopEvent.SelectProductToBuy(mockProducts[0]))

            val updatedState = expectMostRecentItem()
            assertEquals(mockProducts[0], updatedState.selectedProductToBuy)
        }
    }

    @Test
    fun `given shop is loaded, when purchase is successful, then order result shows success`() = runTest {
        coEvery { shopRepository.makePurchase(mockProducts[0]) } returns true.right()

        createSut().test {
            expectMostRecentItem().eventSink(ShopEvent.Buy(mockProducts[0]))

            val purchaseState = expectMostRecentItem()
            assertEquals(ShopPresenter.OrderResult.SUCCESS, purchaseState.orderResult)
            assertNull(purchaseState.selectedProductToBuy)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `given shop is loaded, when purchase fails, then show insufficient pomodoros error`() = runTest {
        coEvery { shopRepository.makePurchase(mockProducts[0]) } returns false.right()

        createSut().test {
            expectMostRecentItem().eventSink(ShopEvent.Buy(mockProducts[0]))

            val purchaseState = expectMostRecentItem()
            assertEquals(ShopPresenter.OrderResult.INSUFFICIENT_POMODOROS, purchaseState.orderResult)
            assertNull(purchaseState.selectedProductToBuy)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `given purchase fails with error, when buying product, then show and hide error`() = runTest {
        val error = RuntimeException("Purchase failed")
        coEvery { shopRepository.makePurchase(mockProducts[0]) } returns error.left()

        createSut().test {
            expectMostRecentItem().eventSink(ShopEvent.Buy(mockProducts[0]))

            val errorState = expectMostRecentItem()
            assertEquals(error, errorState.error)

            advanceTimeBy(3001)
            assertNull(expectMostRecentItem().error)
        }
    }

    @Test
    fun `given shop is loaded, when navigation events triggered, then navigate to correct screens`() = runTest {
        createSut().test {
            val state = expectMostRecentItem()
            state.eventSink(ShopEvent.NavigateToPurchaseHistory)
            assertEquals(PurchaseHistoryScreen, navigator.awaitNextScreen())

            state.eventSink(ShopEvent.NavigateToAddProduct)
            assertEquals(AddProductScreen(), navigator.awaitNextScreen())
        }
    }
} 
