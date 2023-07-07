/*
 * Copyright 2020-2023 Octomix Software Technology Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.octomix.josson;

import com.fasterxml.jackson.databind.JsonNode;
import com.octomix.josson.exception.SyntaxErrorException;

import java.util.HashMap;
import java.util.Map;

import static com.octomix.josson.JossonCore.VARIABLE_PREFIX_SYMBOL;

/**
 * Contains all progressive nodes and variables defined along the path.
 */
public class PathTrace {

    private final JsonNode[] steps;
    private Map<String, JsonNode> variables;

    private PathTrace(final int size, final Map<String, JsonNode> variables) {
        this.steps = new JsonNode[size];
        this.variables = variables;
    }

    private PathTrace(final JsonNode node, final Map<String, JsonNode> variables) {
        this.steps = new JsonNode[]{node};
        this.variables = variables;
    }

    static PathTrace from(final JsonNode node) {
        return new PathTrace(node, null);
    }

    static PathTrace from(final JsonNode node, final Map<String, JsonNode> variables) {
        return new PathTrace(node, variables);
    }

    PathTrace root() {
        return steps.length == 1 ? this : new PathTrace(steps[0], variables);
    }

    PathTrace push(final JsonNode node) {
        if (node == null) {
            return null;
        }
        final PathTrace clone = new PathTrace(steps.length + 1, variables);
        System.arraycopy(steps, 0, clone.steps, 0, steps.length);
        clone.steps[steps.length] = node;
        return clone;
    }

    PathTrace pop(final int steps) {
        final PathTrace clone = new PathTrace(this.steps.length - steps, variables);
        System.arraycopy(this.steps, 0, clone.steps, 0, this.steps.length - steps);
        return clone;
    }

    JsonNode node() {
        return steps[steps.length - 1];
    }

    int steps() {
        return steps.length - 1;
    }

    void setVariable(final String name, final JsonNode value) {
        if (name.length() < 2 || name.charAt(0) != VARIABLE_PREFIX_SYMBOL) {
            throw new SyntaxErrorException("Variable name must start with '$' and has at least 2 characters");
        }
        if (variables == null) {
            variables = new HashMap<>();
        }
        variables.put(name, value);
    }

    JsonNode getVariable(final String name) {
        return variables == null ? null : variables.get(name);
    }

    boolean isObject() {
        return node().isObject();
    }
    
    boolean isArray() {
        return node().isArray();
    }

    boolean isContainer() {
        return node().isContainerNode();
    }

    boolean isValueNode() {
        return node().isValueNode();
    }

    boolean isTextual() {
        return node().isTextual();
    }

    boolean isNumber() {
        return node().isNumber();
    }

    boolean isBoolean() {
        return node().isBoolean();
    }

    boolean isNull() {
        return node().isNull();
    }

    boolean isEmpty() {
        return node().isEmpty();
    }

    String asText() {
        return node().asText();
    }

    double asDouble() {
        return node().asDouble();
    }

    int asInt() {
        return node().asInt();
    }

    boolean asBoolean() {
        return node().asBoolean();
    }

    int containerSize() {
        return node().size();
    }

    JsonNode get(final int i) {
        return node().get(i);
    }

    JsonNode get(final String fieldName) {
        return node().get(fieldName);
    }

    /**
     * Get all progressive nodes along the path.
     *
     * @return all progressive nodes along the path.
     */
    public JsonNode[] getNodes() {
        return steps;
    }

    /**
     * Get all variables defined by function let() along the path.
     *
     * @return all variables defined by function let().
     */
    public Map<String, JsonNode> getVariables() {
        return variables;
    }
}
