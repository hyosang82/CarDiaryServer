package kr.hyosang.cardiary.data.model.json.daum;

import java.lang.reflect.Type;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class KeySerializer implements JsonSerializer<Key> {
	@Override
	public JsonElement serialize(Key src, Type typeOfSrc, JsonSerializationContext context) {
		return new JsonPrimitive(KeyFactory.keyToString(src));
	}
}
