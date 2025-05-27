package domain.valueObjects

import com.evennt.domain.valueObjects.AuthTokenPayload
import com.evennt.domain.valueObjects.TokenType
import kotlinx.datetime.*
import org.junit.jupiter.api.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class AuthTokenPayloadTest {
    @Test
    fun testIsExpiredWithGreaterDateThanExp() {
        val exp: Instant = Clock.System.now().minus(100, DateTimeUnit.SECOND)
        val token =
            AuthTokenPayload(
                sub = "sub",
                iat = System.currentTimeMillis(),
                exp = exp.toEpochMilliseconds(),
                jti = "token_in",
                type = TokenType.ACCESS,
        )

        assertTrue("Token be expired") { token.isExpired() }
    }

    @Test
    fun testIsExpiredWithLowerDateThanExp() {
        val exp: Instant = Clock.System.now().plus(100, DateTimeUnit.SECOND)
        val token =
            AuthTokenPayload(
                sub = "sub",
                iat = System.currentTimeMillis(),
                exp = exp.toEpochMilliseconds(),
                jti = "token_in",
            type = TokenType.ACCESS,
        )

        assertFalse("Token should not be expired") { token.isExpired() }
    }

    @Test
    fun isValidType() {
        val token =
            AuthTokenPayload(
                sub = "sub",
                iat = System.currentTimeMillis(),
                exp = System.currentTimeMillis(),
                jti = "token_in",
            type = TokenType.ACCESS,
        )

        assertTrue { token.isValidType(TokenType.ACCESS) }
    }

    @Test
    fun isInvalidType() {
        val token =
            AuthTokenPayload(
                sub = "sub",
                iat = System.currentTimeMillis(),
                exp = System.currentTimeMillis(),
            jti = "token_in",
            type = TokenType.ACCESS,
        )

        assertFalse { token.isValidType(TokenType.REFRESH) }
    }
}
