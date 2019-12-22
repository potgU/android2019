package ddwu.mobile.final_project.ma02_20170979.util;

import android.util.Log;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.ArrayList;

public class JSONUtil {
    private static JSONParser jsonParser;

    public static JSONParser getJsonParser() {
        if (jsonParser == null)
            jsonParser = new JSONParser();

        return jsonParser;
    }

    public static JSONObject parse(String jsonString) {
        getJsonParser();

        try {
            return (JSONObject) jsonParser.parse(jsonString);
        } catch (ParseException e) {
            Log.e("MyExerciseTimer", e.getMessage(), e);

            return null;
        }
    }

    public static String getSharedPreferencesDefaultValue() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("length", 0);

        return jsonObject.toJSONString();
    }
}
