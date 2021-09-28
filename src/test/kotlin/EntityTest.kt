import entity.User
import entity.UserName
import entity.UserStatus
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class EntityTest {
    @Test
    fun testEntityCreate() {
        var user: User = User(null, UserName("Emile", "Steenkamp"), 23, UserStatus.ACTIVE, arrayListOf("ZA", "NL")).save()
        user = Entity.findById(user.getId())

        assertEquals("Emile", user.name.firstName)
        assertEquals("Steenkamp", user.name.lastName)
        assertEquals(23, user.age)
        assertEquals(UserStatus.ACTIVE, user.status)
        assertEquals(arrayListOf("ZA", "NL"), user.countriesVisited)
    }

    @Test
    fun testEntityUpdate() {
        var user: User = User(null, UserName("Emile", "Mostert"), 25).save()
        user = Entity.findById(user.getId())

        assertEquals(UserStatus.ACTIVE, user.status)

        user.status = UserStatus.INACTIVE
        user = user.save()
        user = Entity.findById(user.getId())

        assertEquals(UserStatus.INACTIVE, user.status)
    }

    @Test
    fun testEntityEquality() {
        val user1 = User(null, UserName("Wihan", "Nel"), 22).save<User>()
        val user2 = Entity.findById<User>(user1.getId())

        assertEquals(user1, user2)
    }

    @Test
    fun testEntityEqualityOnNullId() {
        val user1 = User(null, UserName("Gerrit", "Burger"), 23)
        val user2 = User(null, UserName("Wikus", "Van der Merwe"), 26)

        assertNotEquals(user1, user2)
        assertNotEquals(user2, user1)

        val user3 = User(null, UserName("Nicholas", "Van Huysteen"), 24)

        assertNotEquals(user1, user3)
        assertNotEquals(user3, user1)
    }

    @Test
    fun testEntityToString() {
        val user = User(null, UserName("Willem", "Aggenbach"), 22)

        println(user.toString())
    }
}