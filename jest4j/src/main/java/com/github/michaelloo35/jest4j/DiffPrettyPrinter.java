package com.github.michaelloo35.jest4j;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;

import static com.github.michaelloo35.jest4j.SnapshotAssertConfiguration.getObjectMapper;

class DiffPrettyPrinter {

    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_WHITE = "\u001B[37m";

    public static String print(JsonNode differences) throws JsonProcessingException {
        StringBuilder sb = new StringBuilder();
        sb.append(System.lineSeparator()).append(System.lineSeparator());
        for (JsonNode difference : differences) {
            String textValue = difference.get("op").textValue();
            switch (textValue) {
                case "replace":
                    sb
                            .append("===============================================")
                            .append(System.lineSeparator())
                            .append(white("Value changed:"))
                            .append(System.lineSeparator())
                            .append(white(String.format("%-9s: ", "expected")))
                            .append(difference.get("path").textValue())
                            .append(System.lineSeparator())
                            .append(white(String.format("%-9s: ", "to be")))
                            .append(getObjectMapper().writeValueAsString(difference.get("fromValue")))
                            .append(System.lineSeparator())
                            .append(white(String.format("%-9s: ", "but was")))
                            .append(getObjectMapper().writeValueAsString(difference.get("value")));

                    break;
                case "remove":
                    sb
                            .append("===============================================")
                            .append(System.lineSeparator())
                            .append(white("Missing in actual:"))
                            .append(System.lineSeparator())
                            .append(white(String.format("%-6s: ", "path")))
                            .append(difference.get("path").textValue())
                            .append(System.lineSeparator())
                            .append(white(String.format("%-6s: ", "value")))
                            .append(getObjectMapper().writeValueAsString(difference.get("value")));

                    break;
                case "add":
                    sb
                            .append("===============================================")
                            .append(System.lineSeparator())
                            .append(white("Missing in snapshot:"))
                            .append(System.lineSeparator())
                            .append(white(String.format("%-6s: ", "path")))
                            .append(difference.get("path").textValue())
                            .append(System.lineSeparator())
                            .append(white(String.format("%-6s: ", "value")))
                            .append(getObjectMapper().writeValueAsString(difference.get("value")));

                    break;
                default:
                    sb
                            .append("===============================================")
                            .append(System.lineSeparator())
                            .append(white("Unrecognized difference between snapshot and actual:"))
                            .append(System.lineSeparator())
                            .append(getObjectMapper().writeValueAsString(difference));
                    break;
            }
            sb.append(System.lineSeparator());
        }
        sb.append("===============================================");

        return sb.toString();
    }

    private static String white(String text) {
        return ANSI_WHITE + text + ANSI_RESET;
    }
}
