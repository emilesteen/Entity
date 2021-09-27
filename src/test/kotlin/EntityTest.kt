import entity.User
import org.bson.types.ObjectId
import org.junit.Test
import kotlin.test.assertEquals

class EntityTest {
    @Test
    fun testEntityCreate() {
        User("Emile", "Steenkamp", 23).save<User>()
    }

    @Test
    fun testEntityFind() {
        Entity.findById<User>(ObjectId("6151dcead7627735cba71645"))
    }

    @Test
    fun testEntityEquality() {
        val user1 = User("Wihan", "Nel", 22).save<User>()
        val user2 = Entity.findById<User>(user1._id)

        assertEquals(user1, user2)
    }

    @Test
    fun testEntityToString() {
        val user = User("Wihan", "Nel", 22)

        println(user.toString())
    }
}