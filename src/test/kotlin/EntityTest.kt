import entity.User
import org.junit.Test

class EntityTest {
    @Test
    fun testModel() {
        val user = User("Emile", "Steenkamp", 23).save()
        user.sayHello()
    }
}