import entity.User
import org.bson.types.ObjectId
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class EntityTest {
    @Test
    fun testEntityCreate() {
        User(null, "Emile", "Steenkamp", "Steen", 23).save<User>()
    }

    @Test
    fun testEntityUpdate() {
        var user = User(null, "Emile", "Mostert", "Emile", 25).save<User>()
        user = Entity.findById(user.getId())

        assertEquals("Emile", user.nickName)

        user.nickName = "Mostert"
        user.save<User>()
        user = Entity.findById(user.getId())

        assertEquals("Mostert", user.nickName)
    }

    @Test
    fun testEntityFind() {
        Entity.findById<User>(ObjectId("6151dcead7627735cba71645"))
    }

    @Test
    fun testEntityEquality() {
        val user1 = User(null, "Wihan", "Nel", "Wihan", 22).save<User>()
        val user2 = Entity.findById<User>(user1.getId())

        assertEquals(user1, user2)
    }

    @Test
    fun testEntityEqualityOnNullId() {
        val user1 = User(null, "Gerrit", "Burger", "Gerrit", 23)
        val user2 = Entity.findById<User>(ObjectId("6151dcead7627735cba71645"))

        assertNotEquals(user1, user2)
        assertNotEquals(user2, user1)

        val user3 = User(null, "Nicholas", "Van Huysteen", "Nicholas", 24)

        assertNotEquals(user1, user3)
        assertNotEquals(user3, user1)
    }

    @Test
    fun testEntityToString() {
        val user = User(null, "Wihan", "Nel", "Wihan", 22)

        println(user.toString())
    }
}