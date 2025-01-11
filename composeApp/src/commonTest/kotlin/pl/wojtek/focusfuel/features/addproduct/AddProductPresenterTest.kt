package pl.wojtek.focusfuel.features.addproduct

import arrow.core.left
import arrow.core.right
import com.slack.circuit.test.FakeNavigator
import com.slack.circuit.test.test
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runTest
import pl.wojtek.focusfuel.repository.Product
import pl.wojtek.focusfuel.repository.ShopRepository
import pl.wojtek.focusfuel.util.BaseTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class AddProductPresenterTest : BaseTest() {
    private val navigator = FakeNavigator(AddProductScreen())
    private val shopRepository = mockk<ShopRepository> {
        coEvery { addProduct(any(), any()) } returns Unit.right()
        coEvery { updateProduct(any(), any(), any()) } returns Unit.right()
    }

    private val mockProduct = Product(
        id = 1,
        name = "Test Product",
        costInPomodoros = 10,
    )

    private fun createSut(product: Product? = null) = AddProductPresenter(
        addProductScreen = AddProductScreen(product),
        shopRepository = shopRepository,
        navigator = navigator
    )

    @Test
    fun `Given presenter is created, when initial state is observed, then shows empty form`() = runTest {
        createSut().test {
            val state = awaitItem()
            assertEquals("", state.name)
            assertEquals("", state.price)
            assertNull(state.nameError)
            assertNull(state.priceError)
            assertNull(state.error)
        }
    }

    @Test
    fun `Given presenter is created with existing product, when initial state is observed, then shows prefilled form`() =
        runTest {
            createSut(mockProduct).test {
                val state = awaitItem()
                assertEquals(mockProduct.name, state.name)
                assertEquals(mockProduct.costInPomodoros.toString(), state.price)
                assertNull(state.nameError)
                assertNull(state.priceError)
            }
        }

    @Test
    fun `Given form is empty, when add is clicked, then shows validation errors`() = runTest {
        createSut().test {
            val initialState = awaitItem()

            initialState.eventSink(AddProductEvent.Add)

            val errorState = expectMostRecentItem()
            assertEquals(AddProductError.EMPTY_NAME, errorState.nameError)
            assertEquals(AddProductError.INVALID_PRICE, errorState.priceError)
        }
    }

    @Test
    fun `Given valid form data, when add is clicked, then creates product and navigates back`() = runTest {
        createSut().test {
            val initialState = awaitItem()
            initialState.eventSink(AddProductEvent.SetName("New Product"))
            initialState.eventSink(AddProductEvent.SetPrice("15"))

            initialState.eventSink(AddProductEvent.Add)

            coVerify { shopRepository.addProduct("New Product", 15) }
            navigator.awaitPop()
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `Given existing product, when form is submitted, then updates product and navigates back`() = runTest {
        createSut(mockProduct).test {
            val initialState = awaitItem()

            initialState.eventSink(AddProductEvent.SetName("Updated Product"))
            initialState.eventSink(AddProductEvent.SetPrice("25"))

            initialState.eventSink(AddProductEvent.Add)

            coVerify { shopRepository.updateProduct(mockProduct, "Updated Product", 25) }
            navigator.awaitPop()
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `Given form is open, when close is clicked, then navigates back`() = runTest {
        createSut().test {
            val initialState = awaitItem()

            initialState.eventSink(AddProductEvent.Close)

            navigator.awaitPop()
        }
    }

    @Test
    fun `Given repository fails, when add is clicked, then show and hide error`() = runTest {
        val error = RuntimeException("Failed to add product")
        coEvery { shopRepository.addProduct(any(), any()) } returns error.left()

        createSut().test {
            val initialState = awaitItem()

            initialState.eventSink(AddProductEvent.SetName("New Product"))
            initialState.eventSink(AddProductEvent.SetPrice("15"))

            initialState.eventSink(AddProductEvent.Add)

            val errorState = expectMostRecentItem()
            assertEquals(error, errorState.error)
            navigator.assertPopIsEmpty()

            advanceTimeBy(3001)
            assertNull(expectMostRecentItem().error)
        }
    }

    @Test
    fun `Given updating product fails, when update is clicked, then show and hide error`() = runTest {
        val error = RuntimeException("Failed to update product")
        coEvery { shopRepository.updateProduct(any(), any(), any()) } returns error.left()

        createSut(mockProduct).test {
            val initialState = awaitItem()
            initialState.eventSink(AddProductEvent.SetName("Updated Product"))
            initialState.eventSink(AddProductEvent.SetPrice("25"))

            initialState.eventSink(AddProductEvent.Add)

            val errorState = expectMostRecentItem()
            assertEquals(error, errorState.error)
            navigator.assertPopIsEmpty()

            advanceTimeBy(3001)
            assertNull(expectMostRecentItem().error)
        }
    }
} 
