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
package com.android.tools.profilers.cpu;

import com.android.tools.adtui.model.ConfigurableDurationData;
import com.android.tools.adtui.model.Range;
import com.android.tools.profiler.proto.CpuProfiler;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;

/**
 * This class contains meta information about a trace file, such as traceId, capture start and capture end.
 */
public class CpuTraceInfo implements ConfigurableDurationData {
  private final int myTraceId;
  @NotNull private final Range myRange;

  public CpuTraceInfo(CpuProfiler.TraceInfo traceInfo) {
    myTraceId = traceInfo.getTraceId();
    myRange = new Range(TimeUnit.NANOSECONDS.toMicros(traceInfo.getFromTimestamp()),
                        TimeUnit.NANOSECONDS.toMicros(traceInfo.getToTimestamp()));
  }

  @Override
  public long getDuration() {
    return (long)myRange.getLength();
  }

  @NotNull
  public Range getRange() {
    return myRange;
  }

  public int getTraceId() {
    return myTraceId;
  }

  @Override
  public boolean getSelectableWhenMaxDuration() {
    return false;
  }

  @Override
  public boolean canSelectPartialRange() {
    return true;
  }
}
