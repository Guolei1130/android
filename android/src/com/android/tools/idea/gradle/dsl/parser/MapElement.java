/*
 * Copyright (C) 2015 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.android.tools.idea.gradle.dsl.parser;

import com.google.common.collect.Maps;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.groovy.lang.psi.api.auxiliary.GrListOrMap;
import org.jetbrains.plugins.groovy.lang.psi.api.statements.arguments.GrNamedArgument;
import org.jetbrains.plugins.groovy.lang.psi.api.statements.expressions.literals.GrLiteral;

import java.util.Map;

import static com.intellij.psi.util.PsiTreeUtil.getChildOfType;

/**
 * Represents a map from properties of type {@link String} and values of type {@link LiteralElement}.
 */
public class MapElement extends GradleDslPropertiesElement {
  @NotNull private final String myName;

  public MapElement(@Nullable GradleDslElement parent, @NotNull String name) {
    super(parent);
    myName = name;
  }

  public MapElement(@Nullable GradleDslElement parent, @NotNull String name, @NotNull GrListOrMap map) {
    super(parent);
    assert map.isMap();
    myName = name;
    for (GrNamedArgument argument : map.getNamedArguments()) {
      GrLiteral literal = getChildOfType(argument, GrLiteral.class);
      if (literal != null) {
        String argName = argument.getLabelName();
        setProperty(argName, new LiteralElement(this, name, literal));
      }
    }
  }

  public MapElement(@Nullable GradleDslElement parent, @NotNull String name, @NotNull GrNamedArgument... namedArguments) {
    super(parent);
    myName = name;
    for (GrNamedArgument argument : namedArguments) {
      GrLiteral literal = getChildOfType(argument, GrLiteral.class);
      if (literal != null) {
        String argName = argument.getLabelName();
        setProperty(argName, new LiteralElement(this, name, literal));
      }
    }
  }

  @NotNull
  public String getName() {
    return myName;
  }

  /**
   * Returns the map from properties of the type {@link String} and values of the type {@code clazz}.
   *
   * <p>Returns an empty map when the given there are no values of type {@code clazz}.
   */
  @NotNull
  public <V> Map<String, V> getValues(@NotNull Class<V> clazz) {
    Map<String, V> result = Maps.newHashMap();
    for (String key : getProperties()) {
      V value = getProperty(key, clazz);
      if (value != null) {
        result.put(key, value);
      }
    }
    return result;
  }

  @Override
  @Nullable
  public <T> T getProperty(@NotNull String property, @NotNull Class<T> clazz) {
    T value = super.getProperty(property, clazz);
    if (value instanceof LiteralElement) {
      value = ((LiteralElement)value).getValue(clazz);
    }
    return value;
  }
}