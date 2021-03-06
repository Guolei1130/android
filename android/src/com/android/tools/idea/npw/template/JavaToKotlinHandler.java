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
package com.android.tools.idea.npw.template;

import com.android.SdkConstants;
import com.android.tools.idea.gradle.project.sync.GradleSyncListener;
import com.android.tools.idea.gradle.project.sync.GradleSyncState;
import com.google.common.collect.Lists;
import com.intellij.facet.FacetManager;
import com.intellij.facet.FacetType;
import com.intellij.facet.FacetTypeId;
import com.intellij.facet.FacetTypeRegistry;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.ex.ApplicationEx;
import com.intellij.openapi.application.ex.ApplicationManagerEx;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.DumbService;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Disposer;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.PsiManager;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.List;

/**
 * A class for handling actions related to the java to kotlin conversion.
 */
public class JavaToKotlinHandler {

  private JavaToKotlinHandler() {
  }

  @NotNull
  public static ConvertJavaToKotlinProvider getJavaToKotlinConversionProvider() {
    ConvertJavaToKotlinProvider[] providers = ConvertJavaToKotlinProvider.EP_NAME.getExtensions();
    return providers.length != 0 ? providers[0] : new ConvertJavaToKotlinDefaultImpl();
  }

  static void convertJavaFilesToKotlin(@NotNull Project project,
                                       @NotNull List<File> files,
                                       boolean isNewProject,
                                       @NotNull final Runnable postProcessFunction) {
    if (!hasJavaFiles(files)) {
      DumbService.getInstance(project).smartInvokeLater(postProcessFunction);
      return;
    }

    final ConvertJavaToKotlinProvider provider = getJavaToKotlinConversionProvider();

    // If its a new project then call the conversion in response to rootsChanged.
    if (isNewProject) {

      Disposable tempDisposable = Disposer.newDisposable();
      GradleSyncState.subscribe(project, new GradleSyncListener.Adapter() {
        @Override
        public void syncSucceeded(@NotNull Project project) {
          callConverter(project, provider, files, postProcessFunction);
          Disposer.dispose(tempDisposable);
        }

        @Override
        public void syncFailed(@NotNull Project project, @NotNull String errorMessage) {
          callConverter(project, provider, files, postProcessFunction);
          Disposer.dispose(tempDisposable);
        }
      }, tempDisposable);
    }
    else {
      callConverter(project, provider, files, postProcessFunction);
    }
  }

  static boolean hasJavaFiles(@NotNull List<File> files) {
    return files.stream().anyMatch(file -> file.getName().endsWith(SdkConstants.DOT_JAVA));
  }

  private static void callConverter(@NotNull Project project,
                                    @NotNull ConvertJavaToKotlinProvider provider,
                                    @NotNull List<File> files,
                                    @NotNull Runnable postProcessFunction) {

    DumbService.getInstance(project).smartInvokeLater(() -> {
      List<PsiJavaFile> psiJavaFiles = files2PsiJavaFiles(project, files);
      if (!psiJavaFiles.isEmpty()) {
        provider.convertToKotlin(project, psiJavaFiles);
      }
      postProcessFunction.run();
    });
  }

  private static List<PsiJavaFile> files2PsiJavaFiles(Project project, List<File> files) {
    LocalFileSystem localFileSystem = LocalFileSystem.getInstance();
    List<PsiJavaFile> psiJavaFiles = Lists.newArrayListWithExpectedSize(files.size());
    PsiManager psiManager = PsiManager.getInstance(project);

    for (File file : files) {
      VirtualFile virtualFile = localFileSystem.findFileByIoFile(file);
      if (virtualFile == null) {
        continue;
      }
      PsiFile psiFile = psiManager.findFile(virtualFile);
      if (psiFile instanceof PsiJavaFile) {
        psiJavaFiles.add((PsiJavaFile)psiFile);
      }
    }
    return psiJavaFiles;
  }

  static boolean hasKotlinFacet(@NotNull Project project) {
    final FacetType kotlinFacet = FacetTypeRegistry.getInstance().findFacetType("kotlin-language");
    if (kotlinFacet == null) {
      return false;
    }
    FacetTypeId<?> kotlinFacetId = kotlinFacet.getId();
    for (Module module : ModuleManager.getInstance(project).getModules()) {
      if (FacetManager.getInstance(module).getFacetByType(kotlinFacetId) != null) {
        return true;
      }
    }
    return false;
  }
}
