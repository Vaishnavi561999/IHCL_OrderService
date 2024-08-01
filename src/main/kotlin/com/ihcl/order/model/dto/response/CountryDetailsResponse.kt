import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import org.bson.types.ObjectId

data class  CountryDetailsResponse(
    @JsonDeserialize(using = IdDeserializer::class)
    val _id: Id,
    val id: Int?,
    val name: String?,
    val iso3: String?,
    val iso2: String?,
    val numeric_code: String?,
    val phone_code: String?,
    val capital: String?,
    val currency: String?,
    val currency_name: String?,
    val currency_symbol: String?,
    val tld: String?,
    val native: String?,
    val region: String?,
    val region_id: String?,
    val subregion: String?,
    val subregion_id: String?,
    val nationality: String?,
    val timezones: List<Timezone>?,
    val translations: Translations?,
    val latitude: String?,
    val longitude: String?,
    val emoji: String?,
    val emojiU: String?,
    val states: List<State>?
)

data class Id(val oid: String)

data class Timezone(
    val zoneName: String?,
    val gmtOffset: Int?,
    val gmtOffsetName: String?,
    val abbreviation: String?,
    val tzName: String?
)

data class Translations(
    val kr: String?,
    val pt_BR: String?,
    val pt: String?,
    val nl: String?,
    val hr: String?,
    val fa: String?,
    val de: String?,
    val es: String?,
    val fr: String?,
    val ja: String?,
    val it: String?,
    val cn: String?,
    val tr: String?
)

data class State(
    val id: Int?,
    val name: String?,
    val state_code: String?,
    val latitude: String?,
    val longitude: String?,
    val type: String?,
    val cities: List<City>?
)

data class City(
    val id: Int?,
    val name: String?,
    val latitude: String?,
    val longitude: String?
)
class IdDeserializer : JsonDeserializer<Id>() {
    override fun deserialize(parser: JsonParser, context: DeserializationContext): Id {
        val objectId = parser.readValueAs(ObjectId::class.java)
        return Id(objectId.toString())
    }
}