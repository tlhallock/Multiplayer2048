package org.hallock.tfe.msg;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;

public class SimpleParser
{
	private static final JsonToken[] VALUES_TOKENS = new JsonToken[]{
		JsonToken.VALUE_TRUE,
		JsonToken.VALUE_FALSE,
		JsonToken.VALUE_NULL,
		JsonToken.VALUE_NUMBER_FLOAT,
		JsonToken.VALUE_NUMBER_INT,
		JsonToken.VALUE_STRING,
		JsonToken.START_OBJECT,
		JsonToken.START_ARRAY,
	};
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	private static interface ValueListener<T>
	{
		public void setValue(T object);

		public void assumeSet();
	}
	
	
	
	public static class SimpleUnknownValue<T> implements ValueListener<T>
	{
		T object;

		@Override
		public final void setValue(T object)
		{
			this.object = object;
		}

		@Override
		public void assumeSet()
		{
			if (object == null)
				throw new RuntimeException("Should be set!");
		}
		
		@Override
		public String toString()
		{
			return String.valueOf(object);
		}
	}
	
	public static class SimpleKnownValue<T> implements ValueListener<T>
	{
		T t;

		@Override
		public final void setValue(T object)
		{
			t = object;
		}

		public T getValue()
		{
			return t;
		}
		
		@Override
		public void assumeSet()
		{
			if (t == null)
				throw new RuntimeException("Should be set!");
		}
		
		@Override
		public String toString()
		{
			return String.valueOf(t);
		}
	}
	
	private static abstract class ActionHandler<T> implements ValueListener<T>
	{
		boolean set;
		
		@Override
		public void setValue(T object)
		{
			handle(object);
			set = object != null;
		}
		public abstract void handle(T t);
		public abstract void wasNull();

		
		@Override
		public void assumeSet()
		{
			if (!set)
				throw new RuntimeException("Should be set!");
		}
	}
	public static abstract class NullableActionHandler<T> extends ActionHandler<T>
	{
		@Override
		public void wasNull() {}
	}
	public static abstract class NotNullableActionHandler<T> extends ActionHandler<T>
	{
		@Override
		public void wasNull()
		{
			throw new RuntimeException("Object cannot be null!");
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

	private static abstract class Notifier<T>
	{
		JsonToken[] tokens;

		public Notifier(JsonToken[] t)
		{
			this.tokens = t;
		}

		public final boolean accepts(JsonToken token)
		{
			for (JsonToken desired : tokens)
				if (token.equals(desired))
					return true;
			return false;
		}

		public abstract void parseObject(JsonParser parser) throws IOException;
		
		public abstract void parseArray(JsonParser parser) throws IOException;

		public abstract void foundNull();

		public abstract void foundString(String string);

		public abstract void foundBoolean(boolean object);

		public abstract void foundNumber(BigDecimal decimal);

		public abstract void assumeSet();
	}
	
	private static abstract class ListeningNotifier<T> extends Notifier<T>
	{
		ValueListener<T> listener;
		
		public ListeningNotifier(ValueListener<T> listener, JsonToken... t)
		{
			super(t);
			this.listener = listener;
		}
		
		@Override
		public void foundNull()
		{
			throw new RuntimeException("Object cannot be null.");
		}

		@Override
		public void foundString(String string)
		{
			throw new RuntimeException("Wrong type.");
		}

		@Override
		public void foundBoolean(boolean object)
		{
			throw new RuntimeException("Wrong type.");
		}

		@Override
		public void foundNumber(BigDecimal decimal)
		{
			throw new RuntimeException("Wrong type.");
		}

		@Override
		public void parseObject(JsonParser decimal) throws IOException
		{
			throw new RuntimeException("Wrong type.");
		}

		@Override
		public void parseArray(JsonParser decimal) throws IOException
		{
			throw new RuntimeException("Wrong type.");
		}
		
		@Override
		public void assumeSet()
		{
			listener.assumeSet();
		}
	}
	
	
	
	
	
	
	
	

	private static Notifier<Object> IGNORE_NOTIFIER = new Notifier<Object>(VALUES_TOKENS)
	{
		@Override
		public void foundNull() {}
		
		@Override
		public void foundString(String string) {}
		
		@Override
		public void foundBoolean(boolean object) {}
		
		@Override
		public void foundNumber(BigDecimal decimal) {}

		@Override
		public void parseObject(JsonParser parser) throws IOException
		{
			finishObject(parser);
		}
		
		@Override
		public void parseArray(JsonParser parser) throws IOException
		{
			finishArray(parser);
		}

		@Override
		public void assumeSet() {}
	};
	
	private static Notifier<Object> ERROR_NOTIFIER = new Notifier<Object>(VALUES_TOKENS)
	{

		@Override
		public void foundNull()
		{
			throw new RuntimeException("Expected nothing, found:" + "null");
		}

		@Override
		public void foundString(String string)
		{
			throw new RuntimeException("Expected nothing, found:" + string);
		}

		@Override
		public void foundBoolean(boolean object)
		{
			throw new RuntimeException("Expected nothing, found:" + object);
		}

		@Override
		public void foundNumber(BigDecimal decimal)
		{
			throw new RuntimeException("Expected nothing, found:" + decimal);
		}

		@Override
		public void parseObject(JsonParser decimal)
		{
			throw new RuntimeException("Expected nothing, found:" + decimal);
		}

		@Override
		public void parseArray(JsonParser decimal)
		{
			throw new RuntimeException("Expected nothing, found:" + decimal);
		}

		@Override
		public void assumeSet() {}
	};
	
	
	
	
	private static class ObjectNotifier<T> extends ListeningNotifier<T>
	{
		ObjectReader<T> reader;
		
		public ObjectNotifier(ValueListener<T> listener, ObjectReader<T> reader)
		{
			super(listener, JsonToken.VALUE_NULL, JsonToken.START_OBJECT);
			this.reader = reader;
		}

		@Override
		public void parseObject(JsonParser parser) throws IOException
		{
			T parse = reader.parse(parser);
			listener.setValue(parse);
		}
	}
	private static class ArrayNotifier<K> extends ListeningNotifier<ArrayList<K>>
	{
		ObjectReader<K> reader;
		
		public ArrayNotifier(ValueListener<ArrayList<K>> listener, ObjectReader<K> reader)
		{
			super(listener, JsonToken.VALUE_NULL, JsonToken.START_ARRAY);
			this.reader = reader;
		}
		
		@Override
		public void parseArray(JsonParser parser) throws IOException
		{
			ArrayList<K> list = new ArrayList<>();
			JsonToken next;
			while (!(next = parser.nextToken()).equals(JsonToken.END_ARRAY))
			{
				switch (next)
				{
				case NOT_AVAILABLE:
				case VALUE_EMBEDDED_OBJECT:
				case END_ARRAY:
				case FIELD_NAME:
					throw new RuntimeException("Unexpected.");
				default:
					list.add(reader.parse(parser));
				}
			}
			listener.setValue(list);
		}
	}
	
	private static class BooleanNotifier extends ListeningNotifier<Boolean>
	{
		public BooleanNotifier(ValueListener<Boolean> listener)
		{
			super(listener, JsonToken.VALUE_NULL, JsonToken.VALUE_FALSE, JsonToken.VALUE_TRUE);
		}

		@Override
		public void foundBoolean(boolean object)
		{
			listener.setValue(object);
		}
	}
	private static class StringNotifier extends ListeningNotifier<String>
	{
		public StringNotifier(ValueListener<String> listener)
		{
			super(listener, JsonToken.VALUE_NULL, JsonToken.VALUE_STRING);
		}

		@Override
		public void foundString(String object)
		{
			listener.setValue(object);
		}
	}
	private static class NumberNotifier extends ListeningNotifier<BigDecimal>
	{
		public NumberNotifier(ValueListener<BigDecimal> listener)
		{
			super(listener, JsonToken.VALUE_NULL, JsonToken.VALUE_NUMBER_INT, JsonToken.VALUE_NUMBER_FLOAT);
		}

		@Override
		public void foundNumber(BigDecimal object)
		{
			listener.setValue(object);
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	private static abstract class SimpleReader
	{
		protected HashMap<String, Notifier<?>> valueNotifiers     = new HashMap<>();
		private boolean errorOnUnexpected;
		
		private SimpleReader(boolean errorOnUnexpected)
		{
			this.errorOnUnexpected = errorOnUnexpected;
		}

		private <T> SimpleReader addObjectListener(String path, ValueListener<T> value, ObjectReader<T> reader)
		{
			return add(path, new ObjectNotifier<T>(value, reader));
		}
		private <T> SimpleReader addArrayListener(String path, ValueListener<ArrayList<T>> value, ObjectReader<T> reader)
		{
			return add(path, new ArrayNotifier<>(value, reader));
		}
		private SimpleReader addBooleanListener(String path, ValueListener<Boolean> value)
		{
			return add(path, new BooleanNotifier(value));
		}
		private SimpleReader addStringListener(String path, ValueListener<String> value)
		{
			return add(path, new StringNotifier(value));
		}
		private SimpleReader addNumberListener(String path, ValueListener<BigDecimal> value)
		{
			return add(path, new NumberNotifier(value));
		}
		
		public <T> SimpleReader add(String path, Notifier<?> t)
		{
			valueNotifiers.put(path, t);
			return this;
		}

		public Notifier<?> getNotifier(String currentPath)
		{
			Notifier<?> notifier = valueNotifiers.get(currentPath);
			if (notifier != null)
			{
				return notifier;
			}
			if (errorOnUnexpected)
			{
				return ERROR_NOTIFIER;
			}
			if (hasChildren(currentPath))
			{
				return null;
			}
			return IGNORE_NOTIFIER;
		}
		
		private boolean hasChildren(String currentPath)
		{
			for (String key : valueNotifiers.keySet())
			{
				if (key.startsWith(currentPath))
					return true;
			}
			return false;
		}

		public void assumeSet()
		{
			for (Notifier<?> values : valueNotifiers.values())
			{
				values.assumeSet();
			}
		}
	}

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public static class KnownValueReader extends SimpleReader
	{
		public KnownValueReader(boolean errorOnUnexpected)
		{
			super(errorOnUnexpected);
		}
		protected <T> KnownValueReader addObjectListener(String path, SimpleKnownValue<T> value, ObjectReader<T> reader)
		{
			super.addObjectListener(path, value, reader);
			return this;
		}
		protected <T> KnownValueReader addArrayListener(String path, SimpleKnownValue<ArrayList<T>> value, ObjectReader<T> reader)
		{
			super.addArrayListener(path, value, reader);
			return this;
		}
		protected KnownValueReader addBooleanListener(String path, SimpleKnownValue<Boolean> value)
		{
			super.addBooleanListener(path, value);
			return this;
		}
		protected KnownValueReader addStringListener(String path, SimpleKnownValue<String> value)
		{
			super.addStringListener(path, value);
			return this;
		}
		protected KnownValueReader addNumberListener(String path, SimpleKnownValue<BigDecimal> value)
		{
			super.addNumberListener(path, value);
			return this;
		}
		
		
		
		
		

		public <T> SimpleKnownValue<T> listenForObject(String path, ObjectReader<T> reader)
		{
			SimpleKnownValue<T> simpleKnownValue = new SimpleKnownValue<>();
			addObjectListener(path, simpleKnownValue, reader);
			return simpleKnownValue;
		}
		public <T> SimpleKnownValue<ArrayList<T>> listenForArray(String path, ObjectReader<T> reader)
		{
			SimpleKnownValue<ArrayList<T>> value = new SimpleKnownValue<>();
			addArrayListener(path, value, reader);
			return value;
		}
		public SimpleKnownValue<Boolean> listenForBoolean(String path)
		{
			SimpleKnownValue<Boolean> value = new SimpleKnownValue<>();
			addBooleanListener(path, value);
			return value;
		}
		public SimpleKnownValue<String> listenForString(String path)
		{
			SimpleKnownValue<String> value = new SimpleKnownValue<>();
			addStringListener(path, value);
			return value;
		}
		public SimpleKnownValue<BigDecimal> listenForNumber(String path)
		{
			SimpleKnownValue<BigDecimal> value = new SimpleKnownValue<>();
			addNumberListener(path, value);
			return value;
		}
		
		
	}
	
	
	
	public static class SimpleActionReader extends SimpleReader
	{
		protected SimpleActionReader(boolean errorOnUnexpected)
		{
			super(errorOnUnexpected);
		}
		public <T> SimpleActionReader addObjectListener(String path, ActionHandler<T> value, ObjectReader<T> reader)
		{
			super.addObjectListener(path, value, reader);
			return this;
		}
		protected <T> SimpleActionReader addArrayListener(String path, ActionHandler<ArrayList<T>> value, ObjectReader<T> reader)
		{
			super.addArrayListener(path, value, reader);
			return this;
		}
		public SimpleActionReader addBooleanListener(String path, ActionHandler<Boolean> value)
		{
			super.addBooleanListener(path, value);
			return this;
		}
		public SimpleActionReader addStringListener(String path, ActionHandler<String> value)
		{
			super.addStringListener(path, value);
			return this;
		}
		public SimpleActionReader addNumberListener(String path, ActionHandler<BigDecimal> value)
		{
			super.addNumberListener(path, value);
			return this;
		}
	}

	public static class UnknownValueReader extends SimpleReader
	{
		protected UnknownValueReader(boolean errorOnUnexpected)
		{
			super(errorOnUnexpected);
		}
		
		Node root;
		
		static class Node
		{
			private final HashMap<String, Object> values = new HashMap<>();
			private final HashMap<String, Node> children = new HashMap<>();
			
			public Node getNode(String path)
			{
				throw new RuntimeException("Not implemented...");
			}
		}

		private UnknownValueReader t()
		{
			return this;
		}

		@Override
		public Notifier<?> getNotifier(final String currentPath)
		{
			final Node node = root.getNode(currentPath);
			
			return new Notifier<Object>(SimpleParser.VALUES_TOKENS) {
				private void addObject(Object object)
				{
					node.values.put(getFieldName(currentPath), object);
				}
				
				@Override
				public void parseObject(JsonParser parser) throws IOException
				{
					node.children.put(getFieldName(currentPath), new Node());
					SimpleParser.parseObject(t(), currentPath, parser);
				}

				@Override
				public void parseArray(JsonParser parser) throws IOException
				{
					throw new RuntimeException("Can't parse arrays yet.");
				}

				@Override
				public void foundNull()
				{
					addObject(NULL_OBJECT);
				}

				@Override
				public void foundString(String string)
				{
					addObject(string);
				}

				@Override
				public void foundBoolean(boolean object)
				{
					addObject(object);
				}

				@Override
				public void foundNumber(BigDecimal decimal)
				{
					addObject(decimal);
				}

				@Override
				public void assumeSet() {}
			};
		}

		private String getFieldName(String currentPath)
		{
			throw new RuntimeException("Not implemented...");
		}
		
		public static final Object NULL_OBJECT = new Object();
		
	}
	
	
	
	
	
	

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

	
	public static void parseObject(SimpleReader reader, String currentPath, JsonParser parser) throws IOException
	{
		JsonToken next;
		while (!(next = parser.nextToken()).equals(JsonToken.END_OBJECT))
		{
			if (!next.equals(JsonToken.FIELD_NAME))
				throw new RuntimeException("Unexpected.");
			
			String currentName = parser.getCurrentName();
			String newPath = currentPath + "/" + currentName;
			Notifier<?> notifier = reader.getNotifier(newPath);
			handleValue(reader, parser, newPath, notifier);
		}
	}

	private static void parseArray(SimpleReader reader, String currentPath, JsonParser parser) throws IOException
	{
		int index = 0;
		while (!parser.nextToken().equals(JsonToken.END_ARRAY))
		{
			String newPath = currentPath + "$" + index++;
			Notifier<?> notifier = reader.getNotifier(newPath);
			handleValue(reader, parser, newPath, notifier);
		}
	}

	private static void handleValue(SimpleReader reader, JsonParser parser, String newPath, Notifier<?> notifier) throws IOException
	{
		JsonToken next = parser.nextToken();
		switch (next)
		{
		case START_ARRAY:
			if (notifier == null)
			{
				parseArray(reader, newPath, parser);
			}
			else
			{
				if (!notifier.accepts(next))
				{
					throw new RuntimeException("Bad stuff");
				}
				
				notifier.parseArray(parser);
			}
			break;
		case START_OBJECT:
			if (notifier == null)
			{
				parseObject(reader, newPath, parser);
			}
			else
			{
				if (!notifier.accepts(next))
				{
					throw new RuntimeException("Bad stuff");
				}
				notifier.parseObject(parser);
			}
			break;
		case VALUE_TRUE:
			if (!notifier.accepts(next))
			{
				throw new RuntimeException("Bad stuff");
			}
			notifier.foundBoolean(true);
			break;
		case VALUE_FALSE:
			if (!notifier.accepts(next))
			{
				throw new RuntimeException("Bad stuff");
			}
			notifier.foundBoolean(false);
			break;
		case VALUE_NULL:
			if (!notifier.accepts(next))
			{
				throw new RuntimeException("Bad stuff");
			}
			notifier.foundNull();
			break;
		case VALUE_NUMBER_FLOAT:
		case VALUE_NUMBER_INT:
			if (!notifier.accepts(next))
			{
				throw new RuntimeException("Bad stuff");
			}
			notifier.foundNumber(parser.getDecimalValue());
			break;
		case VALUE_STRING:
			if (!notifier.accepts(next))
			{
				throw new RuntimeException("Bad stuff");
			}
			notifier.foundString(parser.getValueAsString());
			break;

		case END_ARRAY:
		case END_OBJECT:	
		case NOT_AVAILABLE:
		case VALUE_EMBEDDED_OBJECT:
		case FIELD_NAME:
		default:
			throw new RuntimeException("Unexpected.");
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

	public static interface ObjectReader<T>
	{
		public T parse(JsonParser parser) throws IOException;
	}
	public static class BooleanReader implements ObjectReader<Boolean>
	{
		@Override
		public Boolean parse(JsonParser parser) throws IOException
		{
			return parser.getBooleanValue();
		}
	}
	public static class StringReader implements ObjectReader<String>
	{
		@Override
		public String parse(JsonParser parser) throws IOException
		{
			return parser.getValueAsString();
		}
	}
	public static class NumberReader implements ObjectReader<BigDecimal>
	{
		@Override
		public BigDecimal parse(JsonParser parser) throws IOException
		{
			return parser.getDecimalValue();
		}
	}
	public static class VOID {}












	
	public static void finishObject(JsonParser parser) throws IOException
	{
		int depth = 0;
		while (depth >= 0)
		{
			switch (parser.nextToken())
			{
			case END_OBJECT:
				depth--;
				break;
			case START_OBJECT:
				depth++;
				break;
			default:
			}
		}
	}
	
	public static void finishArray(JsonParser parser) throws IOException
	{
		int depth = 0;
		while (depth >= 0)
		{
			switch (parser.nextToken())
			{
			case END_ARRAY:
				depth--;
				break;
			case START_ARRAY:
				depth++;
				break;
			default:
			}
		}
	}
	
	
	
	
	public static void parseAllOfCurrentObject(SimpleReader reader, JsonParser parser) throws IOException
	{
		parseObject(reader, "", parser);
		reader.assumeSet();
	}
}
