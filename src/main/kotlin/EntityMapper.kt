import org.bson.BsonRegularExpression
import org.bson.Document
import org.bson.types.Binary
import org.bson.types.Code
import org.bson.types.ObjectId
import org.bson.types.Symbol
import java.sql.Timestamp
import java.util.*
import kotlin.collections.ArrayList
import kotlin.reflect.*
import kotlin.reflect.full.isSubtypeOf
import kotlin.reflect.full.staticFunctions

class EntityMapper {
    companion object {
        inline fun <reified E>createFromDocument(document: Document, entityClass: KClass<*>): E {
            if (!entityClass.isData) {
                throw Exception("${E::class.simpleName} must be declared as a data class")
            }

            val constructor = entityClass.constructors.first()
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
                type.isSubtypeOf(typeOf<String?>()) -> documentValue
                type.isSubtypeOf(typeOf<Number?>()) -> documentValue
                type.isSubtypeOf(typeOf<Boolean?>()) -> documentValue
                type.isSubtypeOf(typeOf<Timestamp?>()) -> if (documentValue == null) null else (Timestamp.from((documentValue as Date).toInstant()))
                type.isSubtypeOf(typeOf<Symbol?>()) -> documentValue
                type.isSubtypeOf(typeOf<Date?>()) -> documentValue
                type.isSubtypeOf(typeOf<ObjectId?>()) -> documentValue
                type.isSubtypeOf(typeOf<Binary?>()) -> documentValue
                type.isSubtypeOf(typeOf<Code?>()) -> documentValue
                type.isSubtypeOf(typeOf<BsonRegularExpression>()) -> documentValue
                type.isSubtypeOf(typeOf<Enum<*>?>()) -> generateArgumentValueEnum(documentValue, type)
                type.isSubtypeOf(typeOf<ArrayList<*>?>()) -> generateArgumentValueArrayList(documentValue, parameter)
                type.isSubtypeOf(typeOf<Array<*>?>()) ->
                    throw Exception("Entity mapping is not implemented for Array, use ArrayList")
                type.isSubtypeOf(typeOf<List<*>?>()) ->
                    throw Exception("Entity mapping is not implemented for List, use ArrayList")
                else -> createFromDocument(
                    documentValue as Document,
                    (type.classifier as KClass<*>)
                )
            }
        }

        private fun generateArgumentValueArrayList(documentValue: Any?, parameter: KParameter): ArrayList<*> {
            val argumentValueArrayList = arrayListOf<Any?>()

            for (iterationValue in documentValue as Iterable<*>) {
                argumentValueArrayList.add(
                    mapDocumentValueToArgumentValue(
                        iterationValue,
                        parameter,
                        parameter.type.arguments.first().type!!
                    )
                )
            }

            return argumentValueArrayList
        }

        private fun generateArgumentValueEnum(documentValue: Any?, type: KType): Enum<*> {
            val function = (type.classifier as KClass<*>)
                .staticFunctions
                .find { staticFunction -> staticFunction.name == "values" }
            val values = function!!.call()

            if (values is Array<*>) {
                val index: Int = documentValue as Int

                return values[index] as Enum<*>
            } else {
                throw Exception("'values' is not an array")
            }
        }

        fun generateDocument(entity: Any): Document {
            if (!entity::class.isData) {
                throw Exception("${entity::class.simpleName} must be declared as a data class")
            }

            val document = Document()
            val properties = entity.javaClass.kotlin.members.filterIsInstance<KProperty<*>>()

            for (property in properties) {
                val propertyValue = property.getter.call(entity)

                document[property.name] = mapPropertyValueToDocumentValue(propertyValue)
            }

            return document
        }

        private fun mapPropertyValueToDocumentValue(property: Any?): Any? {
            return when (property) {
                null -> property
                is String -> property
                is Number -> property
                is Boolean -> property
                is Timestamp -> property
                is Symbol -> property
                is Date -> property
                is ObjectId -> property
                is Binary -> property
                is Code -> property
                is BsonRegularExpression -> property
                is Enum<*> -> property.ordinal
                is ArrayList<*> -> generateDocumentArrayList(property)
                is Array<*> -> throw Exception("Entity mapping is not implemented for Array, use ArrayList")
                is List<*> -> throw Exception("Entity mapping is not implemented for List, use ArrayList")
                else -> generateDocument(property)
            }
        }

        private fun generateDocumentArrayList(propertyArrayList: ArrayList<*>): ArrayList<Any?>
        {
            val documentArrayList = ArrayList<Any?>()

            for (property in propertyArrayList) {
                documentArrayList.add(mapPropertyValueToDocumentValue(property))
            }

            return documentArrayList
        }
    }
}