/*
 * Copyright (C) 2016 The Android Open Source Project
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
package org.jetbrains.android.actions;

import com.intellij.codeInsight.intention.IntentionAction;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.android.AndroidTestCase;
import org.jetbrains.android.AndroidTestUtils;

public class RegisterComponentsTest extends AndroidTestCase {
  /**
   * "Add activity" intention name, isn't shared with non-test code, used to avoid typos in the test suite
   */
  private static final String ADD_ACTIVITY_TO_MANIFEST = "Add activity to manifest";

  /**
   * Test that "add activity" quick intention does show up and works
   */
  public void testAddActivity() throws Exception {
    final VirtualFile file = myFixture.copyFileToProject("intentions/DummyActivity.java", "src/com/example/DummyActivity.java");
    myFixture.configureFromExistingVirtualFile(file);

    final IntentionAction action = AndroidTestUtils.getIntentionAction(myFixture, ADD_ACTIVITY_TO_MANIFEST);
    assertNotNull(action);

    action.invoke(getProject(), myFixture.getEditor(), myFixture.getFile());

    myFixture.checkResultByFile("AndroidManifest.xml", "intentions/DummyActivity_manifest_after.xml", true);
  }

  /**
   * Test that "add activity" doesn't show up when activity is already registered
   */
  public void testAddActivityNoShow() throws Exception {
    deleteManifest();
    myFixture.copyFileToProject("intentions/DummyActivity_manifest_after.xml", "AndroidManifest.xml");
    final VirtualFile file = myFixture.copyFileToProject("intentions/DummyActivity.java", "src/com/example/DummyActivity.java");
    myFixture.configureFromExistingVirtualFile(file);

    final IntentionAction action = AndroidTestUtils.getIntentionAction(myFixture, ADD_ACTIVITY_TO_MANIFEST);
    assertNull(action);
  }

  /**
   * Test that "add activity" doesn't show up when class doesn't extend "android.app.Activity"
   */
  public void testAddActivityOnNonActivity() throws Exception {
    final VirtualFile file = myFixture.copyFileToProject("intentions/DummyActivity2.java", "src/com/example/DummyActivity.java");
    myFixture.configureFromExistingVirtualFile(file);

    final IntentionAction action = AndroidTestUtils.getIntentionAction(myFixture, ADD_ACTIVITY_TO_MANIFEST);
    assertNull(action);
  }

  /**
   * Test that "add service" quick intention does show up and works
   */
  public void testAddService() throws Exception {
    final VirtualFile file = myFixture.copyFileToProject("intentions/DummyService.java", "src/com/example/DummyService.java");
    myFixture.configureFromExistingVirtualFile(file);

    final IntentionAction action = AndroidTestUtils.getIntentionAction(myFixture, "Add service to manifest");
    assertNotNull(action);

    action.invoke(getProject(), myFixture.getEditor(), myFixture.getFile());
    myFixture.checkResultByFile("AndroidManifest.xml", "intentions/DummyService_manifest_after.xml", true);
  }

  /**
   * Test that "add broadcast receiver" quick intention shows up and works
   */
  public void testAddBroadcastReceiver() throws Exception {
    final VirtualFile file = myFixture.copyFileToProject("intentions/DummyReceiver.java", "src/com/example/DummyReceiver.java");
    myFixture.configureFromExistingVirtualFile(file);

    final IntentionAction action = AndroidTestUtils.getIntentionAction(myFixture, "Add broadcast receiver to manifest");
    assertNotNull(action);

    action.invoke(getProject(), myFixture.getEditor(), myFixture.getFile());
    myFixture.checkResultByFile("AndroidManifest.xml", "intentions/DummyReceiver_manifest_after.xml", true);
  }
}
