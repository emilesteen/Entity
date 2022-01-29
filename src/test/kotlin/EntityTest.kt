import com.mongodb.BasicDBObject
import entity.User
import org.junit.Test
import kotlin.test.assertEquals

class EntityTest {
    @Test
    fun testEntityCreate() {
        var user = User(
            User.Name("Emile", "Steenkamp"),
            23,
            User.Status.ACTIVE,
            arrayListOf("ZA", "NL")
        ).insert()

        user = EntityQuery.findById(user._id)

        assertEquals("Emile", user.name.firstName)
        assertEquals("Steenkamp", user.name.lastName)
        assertEquals(23, user.age)
        assertEquals(User.Status.ACTIVE, user.status)
        assertEquals(arrayListOf("ZA", "NL"), user.countriesVisited)
    }

    @Test
    fun testEntityUpdate() {
        var user = User(User.Name("Wikus", "van der Merwe"), 25).insert()
        user = EntityQuery.findById(user._id)

        assertEquals(User.Status.ACTIVE, user.status)

        user.status = User.Status.INACTIVE
        user = user.update()
        user = EntityQuery.findById(user._id)

        assertEquals(User.Status.INACTIVE, user.status)
    }

    @Test
    fun testEntityFind() {
        val filter = BasicDBObject()

        EntityQuery.find<User>(filter)
    }

    @Test
    fun testEntityToString() {
        val user = User(User.Name("Willem", "Aggenbach"), 22)

        println(user.toString())
    }

    fun testEntityQuery() {
        val users = EntityQuery<User>()
            .where(EntityQuery.Field(EntityQuery.Path(User::name, EntityQuery.Path(User.Name::firstName))) eq "Emile")
            .where(User::age eq 10)
            .where(User::status eq User.Status.ACTIVE)
            .find<User>()
    }
}