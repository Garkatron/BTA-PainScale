package deus.painscale.command;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.ArgumentTypeString;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.CompletableFuture;

public class ArgumentStringEnum implements ArgumentType<String> {
	private Collection<String> examples = Arrays.asList("a", "b");

	private ArgumentStringEnum(Collection<String> examples) {
		this.examples = examples;
	}

	public static ArgumentStringEnum stringEnum(Collection<String> examples) {
		return new ArgumentStringEnum(examples);
	}

	public static String getString(CommandContext<?> context, String name) {
		return (String)context.getArgument(name, String.class);
	}


	public String parse(StringReader reader) throws CommandSyntaxException {
		return reader.readUnquotedString();
	}


	public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {

		for (String example : examples) {
			if (example.startsWith(builder.getRemainingLowerCase())) {
				builder.suggest(example);
			}
		}
		return builder.buildFuture();
	}

	public Collection<String> getExamples() {
		return examples;
	}
}
