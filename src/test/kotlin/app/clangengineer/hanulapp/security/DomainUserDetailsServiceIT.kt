package app.clangengineer.hanulapp.security

import app.clangengineer.hanulapp.IntegrationTest
import app.clangengineer.hanulapp.domain.User
import app.clangengineer.hanulapp.repository.UserRepository
import org.apache.commons.lang3.RandomStringUtils
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatExceptionOfType
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.security.core.userdetails.ReactiveUserDetailsService
import java.util.Locale

private const val USER_ONE_LOGIN = "test-user-one"
private const val USER_ONE_EMAIL = "test-user-one@localhost"
private const val USER_TWO_LOGIN = "test-user-two"
private const val USER_TWO_EMAIL = "test-user-two@localhost"
private const val USER_THREE_LOGIN = "test-user-three"
private const val USER_THREE_EMAIL = "test-user-three@localhost"

/**
 * Integration tests for [DomainUserDetailsService].
 */
@IntegrationTest
class DomainUserDetailsServiceIT {

    @Autowired
    private lateinit var userRepository: UserRepository

    @Autowired
    @Qualifier("userDetailsService")
    private lateinit var domainUserDetailsService: ReactiveUserDetailsService

    @BeforeEach
    fun init() {
        userRepository.deleteAll().block()

        val userOne = User(
            login = USER_ONE_LOGIN,
            password = RandomStringUtils.randomAlphanumeric(60),
            activated = true,
            email = USER_ONE_EMAIL,
            firstName = "userOne",
            lastName = "doe",
            langKey = "en"
        )
        userRepository.save(userOne).block()

        val userTwo = User(
            login = USER_TWO_LOGIN,
            password = RandomStringUtils.randomAlphanumeric(60),
            activated = true,
            email = USER_TWO_EMAIL,
            firstName = "userTwo",
            lastName = "doe",
            langKey = "en"
        )
        userRepository.save(userTwo).block()

        val userThree = User(
            login = USER_THREE_LOGIN,
            password = RandomStringUtils.randomAlphanumeric(60),
            activated = false,
            email = USER_THREE_EMAIL,
            firstName = "userThree",
            lastName = "doe",
            langKey = "en"
        )
        userRepository.save(userThree).block()
    }

    @Test
    fun assertThatUserCanBeFoundByLogin() {
        val userDetails = domainUserDetailsService.findByUsername(USER_ONE_LOGIN).block()
        assertThat(userDetails).isNotNull
        assertThat(userDetails.username).isEqualTo(USER_ONE_LOGIN)
    }

    @Test
    fun assertThatUserCanBeFoundByLoginIgnoreCase() {
        val userDetails = domainUserDetailsService.findByUsername(USER_ONE_LOGIN.toUpperCase(Locale.ENGLISH)).block()
        assertThat(userDetails).isNotNull
        assertThat(userDetails.username).isEqualTo(USER_ONE_LOGIN)
    }

    @Test
    fun assertThatUserCanBeFoundByEmail() {
        val userDetails = domainUserDetailsService.findByUsername(USER_TWO_EMAIL).block()
        assertThat(userDetails).isNotNull
        assertThat(userDetails.username).isEqualTo(USER_TWO_LOGIN)
    }

    @Test
    fun assertThatUserCanBeFoundByEmailIgnoreCase() {
        val userDetails = domainUserDetailsService.findByUsername(USER_TWO_EMAIL.toUpperCase(Locale.ENGLISH)).block()
        assertThat(userDetails).isNotNull
        assertThat(userDetails.username).isEqualTo(USER_TWO_LOGIN)
    }

    @Test
    fun assertThatEmailIsPrioritizedOverLogin() {
        val userDetails = domainUserDetailsService.findByUsername(USER_ONE_EMAIL).block()
        assertThat(userDetails).isNotNull
        assertThat(userDetails.username).isEqualTo(USER_ONE_LOGIN)
    }

    @Test
    fun assertThatUserNotActivatedExceptionIsThrownForNotActivatedUsers() {
        assertThatExceptionOfType(UserNotActivatedException::class.java).isThrownBy {
            domainUserDetailsService.findByUsername(USER_THREE_LOGIN).block()
        }
    }
}
