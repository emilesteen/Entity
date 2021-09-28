import org.bson.Document
import org.bson.types.ObjectId
import kotlin.reflect.*
import kotlin.reflect.full.isSubtypeOf
import kotlin.reflect.full.staticFunctions

class EntityMapper {
    companion object {
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
                type.isSubtypeOf(typeOf<ArrayList<*>?>()) -> generateArrayListArgumentValue(documentValue, parameter)
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

        private fun generateArrayListArgumentValue(documentValue: Any?, parameter: KParameter): Iterable<*> {
            val arrayListArgumentValue = arrayListOf<Any?>()

            for (iterationValue in documentValue as Iterable<*>) {
                arrayListArgumentValue.add(
                    mapDocumentValueToArgumentValue(
                        iterationValue,
                        parameter,
                        parameter.type.arguments.first().type!!
                    )
                )
            }

            return arrayListArgumentValue
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
                throw Exception("'values' is not an array")
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
}