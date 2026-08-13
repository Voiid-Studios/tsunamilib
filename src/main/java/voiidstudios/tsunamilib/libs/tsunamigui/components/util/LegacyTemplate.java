package voiidstudios.tsunamilib.libs.tsunamigui.components.util;

import java.util.Collections;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class LegacyTemplate {
    private static final Pattern EXPRESSION = Pattern.compile("\\$\\{\\s*(.*?)\\s*}");

    private static final Pattern TERNARY = Pattern.compile("^(.*?)\\?\\s*'([^']*)'\\s*:\\s*'([^']*)'$");

    private static final Pattern COMPARISON = Pattern.compile("^([A-Za-z_][A-Za-z0-9_]*)\\s*(==|!=|>=|<=|>|<)\\s*(.+)$");

    private static final Pattern IDENTIFIER = Pattern.compile("^[A-Za-z_][A-Za-z0-9_]*$");

    private static final Pattern QUOTED = Pattern.compile("^'([^']*)'$");

    private static final Pattern NUMBER = Pattern.compile("^-?\\d+(?:\\.\\d+)?$");

    private LegacyTemplate() {
        throw new UnsupportedOperationException("Class should not be instantiated!");
    }

    public static String resolve(final String text) {
        return resolve(text, Collections.emptyMap());
    }

    public static String resolve(final String text, final Map<String, Object> variables) {
        if (text == null || text.indexOf("${") == -1) {
            return text;
        }

        final Matcher matcher = EXPRESSION.matcher(text);
        final StringBuilder result = new StringBuilder();

        while (matcher.find()) {
            final String replacement = evaluate(matcher.group(1), variables);
            final String safeReplacement = replacement != null ? replacement : matcher.group(0);
            matcher.appendReplacement(result, Matcher.quoteReplacement(safeReplacement));
        }
        matcher.appendTail(result);

        return result.toString();
    }

    private static String evaluate(final String expression, final Map<String, Object> variables) {
        final Matcher ternary = TERNARY.matcher(expression);
        if (ternary.matches()) {
            final String condition = ternary.group(1).trim();
            final Boolean conditionResult = evaluateCondition(condition, variables);
            if (conditionResult == null) {
                return null;
            }
            return conditionResult ? ternary.group(2) : ternary.group(3);
        }

        if (IDENTIFIER.matcher(expression).matches()) {
            if (!variables.containsKey(expression)) {
                return null;
            }
            return String.valueOf(variables.get(expression));
        }

        return null;
    }

    private static Boolean evaluateCondition(final String condition, final Map<String, Object> variables) {
        if ("true".equals(condition)) return Boolean.TRUE;
        if ("false".equals(condition)) return Boolean.FALSE;

        final Matcher comparison = COMPARISON.matcher(condition);
        if (!comparison.matches()) {
            return null;
        }

        final String variableName = comparison.group(1);
        final String operator = comparison.group(2);
        final String rawValue = comparison.group(3).trim();

        if (!variables.containsKey(variableName)) {
            return null;
        }

        final Object variableValue = variables.get(variableName);
        if (variableValue == null) {
            return null;
        }

        return compare(variableValue, operator, rawValue);
    }

    private static Boolean compare(final Object variableValue, final String operator, final String rawValue) {
        if (variableValue instanceof Boolean) {
            if (!"true".equals(rawValue) && !"false".equals(rawValue)) {
                return null;
            }
            final boolean left = (Boolean) variableValue;
            final boolean right = Boolean.parseBoolean(rawValue);
            switch (operator) {
                case "==": return left == right;
                case "!=": return left != right;
                default: return null;
            }
        }

        if (variableValue instanceof Number) {
            if (!NUMBER.matcher(rawValue).matches()) {
                return null;
            }
            final double left = ((Number) variableValue).doubleValue();
            final double right = Double.parseDouble(rawValue);
            return applyOrdering(Double.compare(left, right), operator);
        }

        final String left = String.valueOf(variableValue);
        final Matcher quoted = QUOTED.matcher(rawValue);
        final String right = quoted.matches() ? quoted.group(1) : rawValue;
        return applyOrdering(left.compareTo(right), operator);
    }

    private static Boolean applyOrdering(final int comparison, final String operator) {
        switch (operator) {
            case "==": return comparison == 0;
            case "!=": return comparison != 0;
            case ">": return comparison > 0;
            case "<": return comparison < 0;
            case ">=": return comparison >= 0;
            case "<=": return comparison <= 0;
            default: return null;
        }
    }
}