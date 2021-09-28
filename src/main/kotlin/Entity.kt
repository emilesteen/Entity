import com.mongodb.BasicDBObject
import com.mongodb.MongoClient
import org.bson.Document
import org.bson.types.ObjectId
import kotlin.reflect.*
import kotlin.reflect.full.isSubtypeOf
import kotlin.reflect.full.staticFunctions

abstract class Entity(open val _id: ObjectId?) {
    companion object {
        private val client: MongoClient? = null

        fun getClient(): MongoClient {
            return client ?: MongoClient()
        }

        inline fun <reified E>findById(_id: ObjectId): E {
            val filter = BasicDBObject();
            filter["_id"] = _id

            val document = getClient()
                .getDatabase(getDatabaseName<E>())
                .getCollection(getCollectionName<E>())
                .find(filter)
                .first()

            if (document == null) {
                throw Exception("Document not found")
            } else {
                return createFromDocument(document, E::class.constructors.first())
            }
        }

        inline fun <reified E>getDatabaseName(): String {
            return E::class.annotations.filterIsInstance<DatabaseName>().first().databaseName
        }

        inline fun <reified E>getCollectionName(): String {
            return E::class.annotations.filterIsInstance<CollectionName>().first().collectionName
        }

        inline fun <reified E>createFromDocument(document: Document, constructor: KFunction<*>): E {
            val arguments = generateArguments(document, constructor)

            return constructor.callBy(arguments) as E
        }

        fun generateArguments(document: Document, constructor: KFunction<*>): Map<KParameter, Any?> {
            val arguments = mutableMapOf<KParameter, Any?>()

            for (parameter in constructor.parameters) {
                arguments[parameter] = mapDocumentValueToArgumentValue(
                    document[parameter.name],
                    parameter,
                    parameter.type
                )
            }

            return arguments
        }

        @OptIn(ExperimentalStdlibApi::class)
        private fun mapDocumentValueToArgumentValue(documentValue: Any?, parameter: KParameter, type: KType): Any? {
            return when {
                type.isSubtypeOf(typeOf<ObjectId?>()) -> documentValue
                type.isSubtypeOf(typeOf<Number?>()) -> documentValue
                type.isSubtypeOf(typeOf<String?>()) -> documentValue
                type.isSubtypeOf(typeOf<Boolean?>()) -> documentValue
                type.isSubtypeOf(typeOf<Enum<*>?>()) -> generateEnumArgumentValue(documentValue, type)
                type.isSubtypeOf(typeOf<ArrayList<*>?>()) -> generateIterableArgumentValue(documentValue, parameter)
                type.isSubtypeOf(typeOf<Array<*>?>()) ->
                    throw Exception("Document to Entity mapping is not implemented for Array, use ArrayList")
                type.isSubtypeOf(typeOf<List<*>?>()) ->
                    throw Exception("Document to Entity mapping is not implemented for List, use ArrayList")
                else -> createFromDocument(
                    documentValue as Document,
                    (type.classifier as KClass<*>).constructors.first()
                )
            }
        }

        private fun generateIterableArgumentValue(documentValue: Any?, parameter: KParameter): Iterable<*> {
            val iterableArgumentValue = arrayListOf<Any?>()

            for (iterationValue in documentValue as Iterable<*>) {
                iterableArgumentValue.add(mapDocumentValueToArgumentValue(iterationValue, parameter, parameter.type.arguments.first().type!!))
            }

            return iterableArgumentValue
        }

        private fun generateEnumArgumentValue(documentValue: Any?, type: KType): Enum<*> {
            val function = (type.classifier as KClass<*>)
                .staticFunctions
                .find { staticFunction -> staticFunction.name == "values" }
            val values = function!!.call()

            if (values is Array<*>) {
                val index: Int = documentValue as Int

                return values[index] as Enum<*>
            } else {
                throw Exception()
            }
        }

        fun generateDocument(entity: Any): Document {
            val document = Document()
            val kProperties = entity.javaClass.kotlin.members.filterIsInstance<KProperty<*>>()

            for (kProperty in kProperties) {
                val property = kProperty.getter.call(entity)

                document[kProperty.name] = mapPropertyToDocument(property)
            }

            return document
        }

        private fun mapPropertyToDocument(property: Any?): Any? {
            return when (property) {
                null -> property
                is ObjectId -> property
                is Number -> property
                is String -> property
                is Boolean -> property
                is Enum<*> -> property.ordinal
                is Iterable<*> -> generateDocumentList(property)
                else -> generateDocument(property)
            }
        }

        private fun generateDocumentList(iterableProperty: Iterable<*>): List<Any?>
        {
            val documentArrayList = mutableListOf<Any?>()

            for (property in iterableProperty) {
                documentArrayList.add(mapPropertyToDocument(property))
            }

            return documentArrayList
        }
    }

    fun getId(): ObjectId
    {
        val id = this._id

        if (id == null) {
            throw Exception("The Entity does not have an id")
        } else {
            return id
        }
    }

    inline fun <reified E: Entity>save(): E {
        return if (this._id == null) {
            this.saveInsert()
        } else {
            this.saveReplace()
        }
    }

    inline fun <reified E: Entity>saveInsert(): E {
        val arguments = mutableMapOf<KParameter, Any?>()
        val constructor = E::class.constructors.first()
        val properties = this.javaClass.kotlin.members.filterIsInstance<KProperty<*>>()

        for (parameter in constructor.parameters) {
            if (parameter.name == "_id") {
                arguments[parameter] = ObjectId()
            } else {
                val property = properties.find { member -> member.name == parameter.name }

                if (property == null) {
                    throw Exception("Property not found on object")
                } else {
                    arguments[parameter] = property.getter.call(this)
                }
            }
        }

        val entity = constructor.callBy(arguments)

        getClient()
            .getDatabase(getDatabaseName<E>())
            .getCollection(getCollectionName<E>())
            .insertOne(generateDocument(entity))

        return entity
    }

    inline fun <reified E>saveReplace(): E
    {
        val filter = BasicDBObject();
        filter["_id"] = this.getId()

        getClient()
            .getDatabase(getDatabaseName<E>())
            .getCollection(getCollectionName<E>())
            .replaceOne(filter, generateDocument(this))

        return this as E
    }

    override fun equals(other: Any?): Boolean {
        return if (other is Entity) {
            when {
                this._id == null -> {
                    false
                }
                other._id == null -> {
                    false
                }
                else -> {
                    this._id == other._id
                }
            }
        } else {
            false
        }
    }

    override fun toString(): String {
        return generateDocument(this).toJson()
    }

    override fun hashCode(): Int {
        return _id.hashCode()
    }
}

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class DatabaseName(val databaseName: String)

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class CollectionName(val collectionName: String)