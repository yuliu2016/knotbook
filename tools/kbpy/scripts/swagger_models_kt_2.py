import json
import pprint
import io

swagger_file = open("api_v3.json")
json_data = swagger_file.read()
swagger_file.close()

data = json.loads(json_data)

api_version = data["info"]["version"]

definitions = data["definitions"]

header = """@file:Suppress("unused", "SpellCheckingInspection", "KDocUnresolvedReference")

package ca.warp7.rt.router.tba

import com.beust.klaxon.JsonObject

/**
 * Represents Alliance Data
 */
data class Alliances<T>(

    /**
     * The Blue Alliance
     */
    val blue: T,

    /**
     * The Red Alliance
     */
    val red: T
)"""

template = """

/**
 * {clz}
 * ------------------------------
 * {des}
 */

data class {clz}(
    /**
     * Raw Data Map
     */
    val raw: JsonObject{dat}
){{
    override fun toString(): String {{
        return raw.toJsonString(true)
    }}
}}"""

class_field_template = """,

    /**
     * {des}
     */
    val {name}: {typing}?"""


def definition_to_kotlin_class(k, v):
    if "properties" in v:
        properties = v["properties"]
    else:
        properties = {}

    if "description" in v:
        description = v["description"]
    else:
        description = "No description available"

    dat = ""

    for propperty_name, property_def in properties.items():

        if "description" in property_def:
            sdes = property_def["description"]
        else:
            sdes = "No description available"

        if "$ref" in property_def:
            reference_class = property_def["$ref"].split("/")[-1]
            kotlin_type = convert_to_kotlin_case(reference_class)

        elif propperty_name == "alliances":
            reference_class = property_def["properties"]["blue"]["$ref"].split("/")[-1]
            kotlin_type = "Alliances<{kk}?>".format(kk=convert_to_kotlin_case(reference_class))

        else:
            data_type = property_def["type"]
            if data_type == "object":
                kotlin_type = "Map<String, Any?>"
            elif data_type == "number":
                kotlin_type = "Double"
            elif data_type == "string":
                kotlin_type = "String"
            elif data_type == "integer":
                kotlin_type = "Int"
            elif data_type == "boolean":
                kotlin_type = "Boolean"
            elif data_type == "array":
                array_items = property_def["items"]

                if "$ref" in array_items:
                    reference_class = array_items["$ref"].split("/")[-1]
                    kotlin_type = "List<" + convert_to_kotlin_case(reference_class) + ">"
                else:
                    array_type = array_items["type"]
                    if array_type == "object":
                        kotlin_type = "List<Map<String, Any?>>"
                    elif array_type == "number":
                        kotlin_type = "List<Double>"
                    elif array_type == "string":
                        kotlin_type = "List<String>"
                    elif array_type == "integer":
                        kotlin_type = "List<Int>"
                    elif array_type == "boolean":
                        kotlin_type = "List<Boolean>"
                    else:
                        print(array_items)
                        raise TypeError()
            else:
                raise TypeError()

        # reserved words
        if propperty_name == "in":
            propperty_name = "_in"

        dat += class_field_template.format(des=sdes, name=propperty_name, typing=kotlin_type)
    s = template.format(des=description, clz=k, dat=dat)
    return s

def convert_to_kotlin_case(k):
    sp = k.split("_")
    sl = list(map(lambda x: x[0].capitalize() + x[1:], sp))
    kk = "".join(sl)
    return kk


with open("Models.kt", mode="w") as f:
    print(header, file=f)
    for def_key, def_content in definitions.items():
        kotlin_name = convert_to_kotlin_case(def_key)
        print(definition_to_kotlin_class(kotlin_name, def_content), file=f)
    
        

    
