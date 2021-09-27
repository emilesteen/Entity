import entity.User
import org.bson.types.ObjectId
import org.junit.Test

class EntityTest {
    @Test
    fun testModelCreate() {
        User("Emile", "Steenkamp", 23).save()
    }

    @Test
    fun testModelFind() {
        val user = Entity.findById<User>(ObjectId("6151dcead7627735cba71645"))

        return
    }
}