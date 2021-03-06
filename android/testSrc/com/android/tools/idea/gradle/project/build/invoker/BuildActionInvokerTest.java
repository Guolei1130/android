/*
 * Copyright (C) 2017 The Android Open Source Project
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
package com.android.tools.idea.gradle.project.build.invoker;

import com.android.tools.idea.gradle.util.BuildMode;
import com.android.tools.idea.gradle.util.Projects;
import com.android.tools.idea.testing.AndroidGradleTestCase;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;

import java.nio.file.Path;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Tests for making sure that {@link org.gradle.tooling.BuildAction} is run when passed to {@link GradleBuildInvoker}.
 */
public class BuildActionInvokerTest extends AndroidGradleTestCase {
  public void testBuildWithBuildAction() throws Exception {
    loadSimpleApplication();

    AtomicReference<String> model = new AtomicReference<>("");

    GradleBuildInvoker invoker = GradleBuildInvoker.getInstance(getProject());
    invoker.add(result -> {
      Object resultModel = result.getModel();
      if (resultModel instanceof String) {
        model.set((String)resultModel);
      }
    });
    ListMultimap<Path, String> tasks = ArrayListMultimap.create();
    tasks.put(Projects.getBaseDirPath(getProject()).toPath(), "assembleDebug");
    invoker.executeTasks(tasks, BuildMode.ASSEMBLE, Collections.emptyList(), new TestBuildAction());

    assertEquals("test", model.get());
  }
}
