package org.other.gen;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import org.hallock.tfe.cmn.util.Json;
import org.hallock.tfe.cmn.util.JsonCopier;
import org.other.gen.Terminal.NumberTerminal;
import org.other.gen.Terminal.QuotedRandomStringTerminal;
import org.other.gen.Terminal.StringTerminal;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;

public class TryIt
{
	public static void main(String[] args) throws JsonParseException, IOException
	{
		EBNF grammar = new EBNF();
		
		Terminal startObject     = grammar.addTerminal(new StringTerminal("{", 1));
		Terminal endObject       = grammar.addTerminal(new StringTerminal("}", 1));
		                         
		Terminal startArray      = grammar.addTerminal(new StringTerminal("[", 1));
		Terminal endArrat        = grammar.addTerminal(new StringTerminal("]", 1));
                                         
		Terminal fieldDelim      = grammar.addTerminal(new StringTerminal(":", 1));
		Terminal comma           = grammar.addTerminal(new StringTerminal(",", 1));

		Terminal trueT           = grammar.addTerminal(new StringTerminal("true", 1));
		Terminal falseT          = grammar.addTerminal(new StringTerminal("false", 1));
		Terminal nullT           = grammar.addTerminal(new StringTerminal("null", 1));
		Terminal number          = grammar.addTerminal(new NumberTerminal("number", 1));
		Terminal string          = grammar.addTerminal(new QuotedRandomStringTerminal("string", 1));

		Nonterminal root         = grammar.addNonterminal(new Nonterminal("root node"));

		Nonterminal object       = grammar.addNonterminal(new Nonterminal("object"));
		Nonterminal fieldList    = grammar.addNonterminal(new Nonterminal("field list"));

		Nonterminal value        = grammar.addNonterminal(new Nonterminal("value"));

		Nonterminal array        = grammar.addNonterminal(new Nonterminal("array"));
		Nonterminal valueList    = grammar.addNonterminal(new Nonterminal("value list"));		



		
		root.addRule(  new Rule("roots are objects").add(object));

		object.addRule(new Rule("objects can be empty")
				.add(startObject)
				.add(endObject));
		object.addRule(new Rule("objects are field lists")
				.add(startObject)
				.add(fieldList));
		
		fieldList.addRule(new Rule("field lists can have one value")
				.add(string)
				.add(fieldDelim)
				.add(value)
				.add(endObject));
		fieldList.addRule(new Rule("field lists can have multiple values")
				.add(string)
				.add(fieldDelim)
				.add(value)
				.add(comma)
				.add(fieldList));

		value.addRule(new Rule("strings are values").add(string));
		value.addRule(new Rule("numbers are values").add(number));
		value.addRule(new Rule("false is a value").add(falseT));
		value.addRule(new Rule("true is a value").add(trueT));
		value.addRule(new Rule("values can be null").add(nullT));
		value.addRule(new Rule("objects are values").add(object));
		value.addRule(new Rule("arrays are values").add(array));
		
		array.addRule(new Rule("arrays are value lists")
				.add(startArray)
				.add(valueList));
		array.addRule(new Rule("arrays can be empty")
				.add(startArray)
				.add(endArrat));

		valueList.addRule(new Rule("valueLists can have one value")
				.add(value)
				.add(endArrat));
		valueList.addRule(new Rule("valueLists can have multiple values")
				.add(value)
				.add(comma)
				.add(valueList));
		
		grammar.setRoot(root);
		

		int num = 50;
		grammar.compile(num);
//		
//		try (PrintStream output = new PrintStream("here.txt");)
//		{
//			grammar.append(output);
//		}

		try (PrintStream output = new PrintStream("json.txt");)
		{
			for (int i = 0; i < 10; i++)
			{
				String json = grammar.sample(new StringBuilder(), num - 1).toString();
				System.out.println("original: " + json);

				ByteArrayOutputStream stringOutput = new ByteArrayOutputStream();
				try (
					JsonParser parser = Json.createParser(json);
					JsonGenerator generator = Json.createUnopenedGenerator(stringOutput);)
				{
					new JsonCopier(parser, generator).copy();
				}
				String jsonOutput = new String(stringOutput.toByteArray());

				output.println(jsonOutput + "\n===============================================\n");
				System.out.println("formated: " + jsonOutput);
			}
		}
	}
}
