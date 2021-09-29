import com.mongodb.BasicDBObject
import entity.User
import entity.UserName
import entity.UserStatus
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class EntityTest {
    @Test
    fun testEntityCreate() {
        var user = User(
            UserName("Emile", "Steenkamp"),
            23,
            UserStatus.ACTIVE,
            arrayListOf("ZA", "NL")
        ).save()

        user = EntityQuery.findById(user.getId())

        assertEquals("Emile", user.name.firstName)
        assertEquals("Steenkamp", user.name.lastName)
        assertEquals(23, user.age)
        assertEquals(UserStatus.ACTIVE, user.status)
        assertEquals(arrayListOf("ZA", "NL"), user.countriesVisited)
    }

    @Test
    fun testEntityUpdate() {
        var user = User(UserName("Wikus", "van der Merwe"), 25).save()
        user = EntityQuery.findById(user.getId())

        assertEquals(UserStatus.ACTIVE, user.status)

        user.status = UserStatus.INACTIVE
        user = user.save()
        user = EntityQuery.findById(user.getId())

        assertEquals(UserStatus.INACTIVE, user.status)
    }

    @Test
    fun testEntityFind() {
        val filter = BasicDBObject()

        EntityQuery.find<User>(filter)
    }

    @Test
    fun testEntityEquality() {
        val user1 = User(UserName("Wihan", "Nel"), 22).save<User>()
        val user2 = EntityQuery.findById<User>(user1.getId())

        assertEquals(user1, user2)
    }

    @Test
    fun testEntityEqualityOnNullId() {
        val notSavedUser1 = User(UserName("Gerrit", "Burger"), 23)
        val notSavedUser2 = User(UserName("Gerrit", "Burger"), 23)

        assertNotEquals(notSavedUser1, notSavedUser2)
        assertNotEquals(notSavedUser2, notSavedUser1)

        val savedUser1 = User(UserName("Gerrit", "Burger"), 23).save()

        assertNotEquals(notSavedUser1, savedUser1)
        assertNotEquals(savedUser1, notSavedUser1)

        val savedUser2 = User(UserName("Gerrit", "Burger"), 23).save()

        assertNotEquals(savedUser1, savedUser2)
        assertNotEquals(savedUser2, savedUser1)
    }

    @Test
    fun testEntityToString() {
        val user = User(UserName("Willem", "Aggenbach"), 22)

        println(user.toString())
    }
}