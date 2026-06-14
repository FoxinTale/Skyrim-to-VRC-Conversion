package Json;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class JsonParser {
    private String text;
    private int pos;

    public JsonValue parse(File file) throws IOException {
        byte[] bytes = Files.readAllBytes(file.toPath());
        text = new String(bytes, "UTF-8");
        pos = 0;

        JsonValue value = parseValue();
        skipWhitespace();

        if (pos != text.length()) {
            throw error("Trailing data after JSON value");
        }

        return value;
    }

    private JsonValue parseValue() {
        skipWhitespace();

        if (pos >= text.length()) {
            throw error("Unexpected end of input");
        }

        char c = text.charAt(pos);

        if (c == '{') {
            return new JsonValue(JsonValue.Type.OBJECT, parseObject());
        }

        if (c == '[') {
            return new JsonValue(JsonValue.Type.ARRAY, parseArray());
        }

        if (c == '"') {
            return new JsonValue(JsonValue.Type.STRING, parseString());
        }

        if (c == 't') {
            expectLiteral("true");
            return new JsonValue(JsonValue.Type.BOOLEAN, Boolean.TRUE);
        }

        if (c == 'f') {
            expectLiteral("false");
            return new JsonValue(JsonValue.Type.BOOLEAN, Boolean.FALSE);
        }

        if (c == 'n') {
            expectLiteral("null");
            return new JsonValue(JsonValue.Type.NULL, null);
        }

        if (c == '-' || isDigit(c)) {
            return new JsonValue(JsonValue.Type.NUMBER, parseNumber());
        }

        throw error("Unexpected character: " + c);
    }

    private JsonObject parseObject() {
        JsonObject object = new JsonObject();

        expect('{');
        skipWhitespace();

        if (peek('}')) {
            expect('}');
            return object;
        }

        while (true) {
            skipWhitespace();

            if (pos >= text.length() || text.charAt(pos) != '"') {
                throw error("Expected object key string");
            }

            String key = parseString();

            skipWhitespace();
            expect(':');

            JsonValue value = parseValue();
            object.put(key, value);

            skipWhitespace();

            if (peek('}')) {
                expect('}');
                break;
            }

            expect(',');
        }

        return object;
    }

    private JsonArray parseArray() {
        JsonArray array = new JsonArray();

        expect('[');
        skipWhitespace();

        if (peek(']')) {
            expect(']');
            return array;
        }

        while (true) {
            JsonValue value = parseValue();
            array.add(value);

            skipWhitespace();

            if (peek(']')) {
                expect(']');
                break;
            }

            expect(',');
        }

        return array;
    }

    private String parseString() {
        expect('"');

        StringBuilder sb = new StringBuilder();

        while (pos < text.length()) {
            char c = text.charAt(pos++);

            if (c == '"') {
                return sb.toString();
            }

            if (c == '\\') {
                if (pos >= text.length()) {
                    throw error("Unterminated escape sequence");
                }

                char esc = text.charAt(pos++);

                switch (esc) {
                    case '"':
                        sb.append('"');
                        break;
                    case '\\':
                        sb.append('\\');
                        break;
                    case '/':
                        sb.append('/');
                        break;
                    case 'b':
                        sb.append('\b');
                        break;
                    case 'f':
                        sb.append('\f');
                        break;
                    case 'n':
                        sb.append('\n');
                        break;
                    case 'r':
                        sb.append('\r');
                        break;
                    case 't':
                        sb.append('\t');
                        break;
                    case 'u':
                        sb.append(parseUnicodeEscape());
                        break;
                    default:
                        throw error("Invalid escape character: " + esc);
                }
            } else {
                sb.append(c);
            }
        }

        throw error("Unterminated string");
    }

    private char parseUnicodeEscape() {
        if (pos + 4 > text.length()) {
            throw error("Incomplete unicode escape");
        }

        String hex = text.substring(pos, pos + 4);
        pos += 4;

        try {
            return (char) Integer.parseInt(hex, 16);
        } catch (NumberFormatException e) {
            throw error("Invalid unicode escape: " + hex);
        }
    }

    private Number parseNumber() {
        int start = pos;

        if (peek('-')) {
            pos++;
        }

        while (pos < text.length() && isDigit(text.charAt(pos))) {
            pos++;
        }

        boolean isDouble = false;

        if (peek('.')) {
            isDouble = true;
            pos++;

            while (pos < text.length() && isDigit(text.charAt(pos))) {
                pos++;
            }
        }

        if (peek('e') || peek('E')) {
            isDouble = true;
            pos++;

            if (peek('+') || peek('-')) {
                pos++;
            }

            while (pos < text.length() && isDigit(text.charAt(pos))) {
                pos++;
            }
        }

        String value = text.substring(start, pos);

        try {
            if (isDouble) {
                return Double.valueOf(value);
            }

            return Long.valueOf(value);
        } catch (NumberFormatException e) {
            throw error("Invalid number: " + value);
        }
    }

    private void expectLiteral(String literal) {
        if (!text.startsWith(literal, pos)) {
            throw error("Expected literal: " + literal);
        }

        pos += literal.length();
    }

    private void expect(char expected) {
        if (pos >= text.length() || text.charAt(pos) != expected) {
            throw error("Expected '" + expected + "'");
        }

        pos++;
    }

    private boolean peek(char c) {
        return pos < text.length() && text.charAt(pos) == c;
    }

    private boolean isDigit(char c) {
        return c >= '0' && c <= '9';
    }

    private void skipWhitespace() {
        while (pos < text.length()) {
            char c = text.charAt(pos);

            if (c == ' ' || c == '\n' || c == '\r' || c == '\t') {
                pos++;
            } else {
                break;
            }
        }
    }

    private RuntimeException error(String message) {
        return new RuntimeException(message + " at position " + pos);
    }
}