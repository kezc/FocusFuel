package pl.wojtek.focusfuel.util.either

import arrow.core.Either
import arrow.core.left
import arrow.core.raise.either
import arrow.core.right
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map

typealias EitherT<T> = Either<Throwable, T>

fun <T> Flow<T>.toEither(): Flow<EitherT<T>> {
    return map { it.right() as EitherT<T> }
        .catch { error ->
            if (error is CancellationException) {
                throw error
            } else {
                emit(error.left())
            }
        }
}

fun <T1, T2, R> combineEither(
    flow1: Flow<EitherT<T1>>,
    flow2: Flow<EitherT<T2>>,
    combiner: (T1, T2) -> R
): Flow<EitherT<R>> {
    return combine(flow1, flow2) { result1, result2 ->
        result1.combineWith(result2) { value1, value2 -> combiner(value1, value2) }
    }
}

fun <T1, T2, R> Either<Throwable, T1>.combineWith(
    other: Either<Throwable, T2>,
    combiner: (T1, T2) -> R
): EitherT<R> = either {
    combiner(this@combineWith.bind(), other.bind())
}
