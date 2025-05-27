package domain.entities

import com.evennt.domain.entities.User
import com.evennt.domain.valueObjects.*
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.util.*
import kotlin.test.assertFalse

class UserTest {
    private val email = Email("email@example.com")
    private val password = "hashedPassword"
    private val name = "Test User"
    private val authTokenSub = "sub"
    private val authTokenJti = "jti"
    private val authTokenType = TokenType.ACCESS

    @Test
    fun `canLogin returns true when user is active and email is confirmed`() {
        val user = User(email = email, password = password, name = name)
        assertTrue { user.canLogin() }
    }

    @Test
    fun `canLogin returns false when user is not active`() {
        val user =
            User(
                email = email,
                password = password,
                name = name,
                active = false,
        )
        assertFalse { user.canLogin() }
    }

    @Test
    fun `can login returns false when user hasn't confirmed its email`() {
        val user =
            User(
                email = email,
                password = password,
                name = name,
                emailConfirmed = false,
        )
        assertFalse { user.canLogin() }
    }

    @Test
    fun `can login returns true when token was issued after invalidation`() {
        val invalidationTime = System.currentTimeMillis() - 1000
        val token =
            AuthTokenPayload(
                iat = System.currentTimeMillis(),
                sub = authTokenSub,
                exp = System.currentTimeMillis() + 10000,
                jti = authTokenJti,
                type = authTokenType,
            )
        val user =
            User(
                email = email,
                password = password,
            name = name,
            lastCredentialInvalidation = Date(invalidationTime),
        )
        assertTrue { user.canUseToken(token) }
    }

    @Test
    fun `canUseToken returns false when token was issued before invalidation`() {
        val invalidationTime = System.currentTimeMillis() + 1000
        val token =
            AuthTokenPayload(
                iat = System.currentTimeMillis(),
                sub = authTokenSub,
                exp = System.currentTimeMillis() + 10000,
                jti = authTokenJti,
                type = authTokenType,
            )
        val user =
            User(
                email = email,
            password = password,
            name = name,
            lastCredentialInvalidation = Date(invalidationTime),
        )
        assertFalse(user.canUseToken(token))
    }
}
