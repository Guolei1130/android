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
package com.android.tools.idea.tests.gui.framework.fixture.theme;

import com.android.tools.idea.editors.theme.ThemeEditorTable;
import com.android.tools.idea.editors.theme.ui.ResourceComponent;
import com.google.common.base.Objects;
import com.google.common.collect.ImmutableList;
import org.fest.swing.annotation.RunsInCurrentThread;
import org.fest.swing.core.Robot;
import org.fest.swing.data.TableCell;
import org.fest.swing.driver.BasicJTableCellReader;
import org.fest.swing.edt.GuiQuery;
import org.fest.swing.fixture.JComboBoxFixture;
import org.fest.swing.fixture.JTableFixture;
import org.fest.swing.timing.Wait;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.Component;
import java.util.List;

import static com.google.common.base.Preconditions.checkState;

public class ThemeEditorTableFixture extends JTableFixture {
  private ThemeEditorTableFixture(Robot robot, ThemeEditorTable target) {
    super(robot, target);
    replaceCellWriter(new ThemeEditorTableCellWriter(robot));
    replaceCellReader(new BasicJTableCellReader(new ThemeEditorTableCellRendererReader()));
  }

  @NotNull
  public static ThemeEditorTableFixture find(@NotNull Robot robot) {
    return new ThemeEditorTableFixture(robot, robot.finder().findByType(ThemeEditorTable.class));
  }

  @NotNull
  public String attributeNameAt(@NotNull final TableCell cell) {
    return GuiQuery.getNonNull(() -> {
      Component renderer = rendererComponentAt(cell);
      checkState(renderer instanceof ResourceComponent, "not a %s: %s", ResourceComponent.class.getSimpleName(), renderer);
      return new ResourceComponentFixture(robot(), (ResourceComponent)renderer).getLabelText();
    });
  }

  @NotNull
  public List<String> getComboBoxContentsAt(@NotNull final TableCell cell) {
    return GuiQuery.getNonNull(() -> {
      Component renderer = rendererComponentAt(cell);
      checkState(renderer instanceof JComponent, "not a %s: %s", JComponent.class.getSimpleName(), renderer);
      JComboBoxFixture comboBox = new JComboBoxFixture(robot(), robot().finder().findByType((JComponent)renderer, JComboBox.class));
      return ImmutableList.copyOf(comboBox.contents());
    });
  }

  @NotNull
  public String getComboBoxSelectionAt(@NotNull final TableCell cell) {
    return GuiQuery.getNonNull(() -> {
      Component renderer = rendererComponentAt(cell);
      checkState(renderer instanceof JComponent, "not a %s: %s", JComponent.class.getSimpleName(), renderer);
      JComboBoxFixture comboBox = new JComboBoxFixture(robot(), robot().finder().findByType((JComponent)renderer, JComboBox.class));
      return comboBox.selectedItem();
    });
  }

  @RunsInCurrentThread
  @Nullable
  private Component rendererComponentAt(@NotNull final TableCell cell) {
    return target().prepareRenderer(target().getCellRenderer(cell.row, cell.column), cell.row, cell.column);
  }

  public void requireValueAt(@NotNull final TableCell cell, @Nullable final String value) {
    Wait.seconds(1).expecting("theme editor update").until(() -> Objects.equal(valueAt(cell), value));
  }
}
