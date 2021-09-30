import com.mongodb.BasicDBObject
import entity.User
import entity.UserName
import entity.UserStatus
import org.junit.Test
import kotlin.test.assertEquals

class EntityTest {
    @Test
    fun testEntityCreate() {
        var user = User(
            UserName("Emile", "Steenkamp"),
            23,
            UserStatus.ACTIVE,
            arrayListOf("ZA", "NL")
        ).insert()

        user = EntityQuery.findById(user._id)

        assertEquals("Emile", user.name.firstName)
        assertEquals("Steenkamp", user.name.lastName)
        assertEquals(23, user.age)
        assertEquals(UserStatus.ACTIVE, user.status)
        assertEquals(arrayListOf("ZA", "NL"), user.countriesVisited)
    }

    @Test
    fun testEntityUpdate() {
        var user = User(UserName("Wikus", "van der Merwe"), 25).insert()
        user = EntityQuery.findById(user._id)

        assertEquals(UserStatus.ACTIVE, user.status)

        user.status = UserStatus.INACTIVE
        user = user.update()
        user = EntityQuery.findById(user._id)

        assertEquals(UserStatus.INACTIVE, user.status)
    }

    @Test
    fun testEntityFind() {
        val filter = BasicDBObject()

        EntityQuery.find<User>(filter)
    }

    @Test
    fun testEntityToString() {
        val user = User(UserName("Willem", "Aggenbach"), 22)

        println(user.toString())
    }
}