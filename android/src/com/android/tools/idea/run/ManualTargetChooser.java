package com.android.tools.idea.run;

import com.android.ddmlib.AndroidDebugBridge;
import com.android.ddmlib.IDevice;
import com.android.tools.idea.run.editor.ShowChooserTargetProvider;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import org.jetbrains.android.facet.AndroidFacet;
import org.jetbrains.android.sdk.AndroidPlatform;
import org.jetbrains.android.sdk.AndroidSdkUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;

/**
 * A target chooser corresponding to the "Extended device chooser" dialog.
 * Either a device target or a cloud matrix target may be chosen.
 */
public class ManualTargetChooser {
  @NotNull private static final Logger LOG = Logger.getInstance(ManualTargetChooser.class);

  @NotNull private final ShowChooserTargetProvider.State myShowChooserState;
  @NotNull private final AndroidFacet myFacet;
  private final int myRunConfigId;
  @NotNull private final Project myProject;

  public ManualTargetChooser(@NotNull ShowChooserTargetProvider.State state, @NotNull AndroidFacet facet, int runConfigId) {
    myShowChooserState = state;
    myFacet = facet;
    myRunConfigId = runConfigId;
    myProject = facet.getModule().getProject();
  }

  @Nullable
  public DeviceFutures getDevices(@NotNull ConsolePrinter printer, @NotNull DeviceCount deviceCount, boolean debug) {
    if (myShowChooserState.USE_LAST_SELECTED_DEVICE) {
      Collection<IDevice> devices = getLastUsedDevices(myProject, myRunConfigId, deviceCount);
      if (!devices.isEmpty()) {
        return DeviceFutures.forDevices(devices);
      }
    }

    AndroidPlatform platform = myFacet.getConfiguration().getAndroidPlatform();
    if (platform == null) {
      LOG.error("Android platform not set for module: " + myFacet.getModule().getName());
      return null;
    }

    final ExtendedDeviceChooserDialog chooser =
      new ExtendedDeviceChooserDialog(myFacet, platform.getTarget(), deviceCount.isMultiple(), myShowChooserState.USE_LAST_SELECTED_DEVICE);
    chooser.show();
    if (chooser.getExitCode() != DialogWrapper.OK_EXIT_CODE) {
      // The user canceled.
      return null;
    }

    if (chooser.isToLaunchEmulator()) {
      final String selectedAvd = chooser.getSelectedAvd();
      if (selectedAvd == null) {
        return null;
      }
      EmulatorTargetChooser emulatorChooser = new EmulatorTargetChooser(myFacet, selectedAvd);
      return emulatorChooser.getDevices(printer, deviceCount, debug);
    }
    else {
      final IDevice[] selectedDevices = chooser.getSelectedDevices();
      if (selectedDevices.length == 0) {
        return null;
      }
      DevicePickerStateService.getInstance(myProject)
        .setDevicesUsedInLaunch(myRunConfigId, Sets.newHashSet(selectedDevices), getOnlineDevices(myProject));
      myShowChooserState.USE_LAST_SELECTED_DEVICE = chooser.useSameDevicesAgain();
      return DeviceFutures.forDevices(Arrays.asList(selectedDevices));
    }
  }

  /** Re-use the last used devices if we are configured to do so and the online devices have not changed. */
  @NotNull
  public static Collection<IDevice> getLastUsedDevices(@NotNull Project project, int runConfigId, @NotNull DeviceCount deviceCount) {
    DeviceStateAtLaunch devicesToReuse = DevicePickerStateService.getInstance(project).getDevicesUsedInLastLaunch(runConfigId);
    if (devicesToReuse == null) {
      return ImmutableList.of();
    }

    Set<IDevice> onlineDevices = getOnlineDevices(project);
    if (devicesToReuse.matchesCurrentAvailableDevices(onlineDevices)) {
      Collection<IDevice> usedDevices = devicesToReuse.filterByUsed(onlineDevices);
      if (usedDevices.size() == 1 || deviceCount.isMultiple()) {
        return usedDevices;
      }
    }
    return ImmutableList.of();
  }

  public static Set<IDevice> getOnlineDevices(@NotNull Project project) {
    AndroidDebugBridge debugBridge = AndroidSdkUtils.getDebugBridge(project);
    if (debugBridge == null) {
      return Collections.emptySet();
    }
    return Sets.newHashSet(debugBridge.getDevices());
  }
}
