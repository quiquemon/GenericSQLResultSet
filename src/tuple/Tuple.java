package tuple;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

/**
 * Class that represent an immutable tuple. It is backed up by
 * a hash table which maps strings (the name of the field) to
 * objects (the value of the field).
 */
public final class Tuple {
	
	 private final Map<String, Object> tuple;
	 
	 /**
	  * Initializes the tuple with the given field names.
	  * 
	  * @param fields The names of the fields that will be
	  * used inside the tuple.
	  */
	 public Tuple(List<String> fields) {
		 tuple = new HashMap<>();
		 fields.forEach(e -> tuple.put(e, null));
	 }
	 
	 /**
	  * Initializes the tuple with the given map.
	  * 
	  * @param tuple The fields and values that will
	  * be added to the tuple.
	  */
	 public Tuple(Map<String, Object> tuple) {
		 this.tuple = new HashMap<>(tuple);
	 }
	 
	 /**
	  * Maps the passed field to the given value.
	  * 
	  * @param field The name of the field.
	  * @param value The new value.
	  * @return <b>true</b> if the value was successfully linked
	  * to the field. If the field doesn't exist, returns <b>false</b>.
	  */
	 public boolean set(String field, Object value) {
		if (!tuple.containsKey(field))
			return false;
		
		tuple.put(field, value);
		return true;
	 }
	 
	 /**
	  * Returns the value of the given field.
	  * 
	  * @param field The name of the field.
	  * @return The value associated to this field. If the
	  * field doesn't exists, returns <b>null</b>.
	  */
	 public Object get(String field) {
		 return tuple.get(field);
	 }
	 
	 /**
	  * Returns the number of fields of this tuple.
	  * 
	  * @return The size of the tuple.
	  */
	 public int cardinality() {
		 return tuple.size();
	 }
}