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
 *
 * THIS FILE WAS GENERATED BY codergen. EDIT WITH CARE.
 */
package com.android.tools.idea.editors.gfxtrace.service;

import com.android.tools.rpclib.binary.*;
import com.android.tools.rpclib.schema.*;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.AbstractList;
import java.util.Arrays;
import java.util.Collections;
import java.util.RandomAccess;

public final class ReportGroup implements BinaryObject {

  public boolean containsItemForAtom(long atomId, @NotNull Report report) {
    return Arrays.stream(myItems).mapToObj(i -> report.getItems()[i])
             .filter(item -> item.getAtom() == atomId).findAny().isPresent();
  }

  public int getItemCount() {
    return myItems.length;
  }

  public int findItem(@NotNull Report report,
                      @NotNull ReportItem item) {
    final IndexList list = new IndexList(report, myItems);
    return Collections.binarySearch(list, item, (lhs, rhs) -> (int)(lhs.getAtom() - rhs.getAtom()));
  }

  private static class IndexList extends AbstractList<ReportItem> implements RandomAccess {
    @NotNull private final Report myReport;
    @NotNull private final int[] myItems;

    public IndexList(@NotNull Report report, @NotNull int[] items) {
      myReport = report;
      myItems = items;
    }

    @Override
    public int size() {
      return myItems.length;
    }

    @Override
    public ReportItem get(int i) {
      return myReport.getItems()[myItems[i]];
    }
  }

  //<<<Start:Java.ClassBody:1>>>
  private MsgRef myName;
  private int[] myItems;
  private MsgRef[] myTags;

  // Constructs a default-initialized {@link ReportGroup}.
  public ReportGroup() {}


  public MsgRef getName() {
    return myName;
  }

  public ReportGroup setName(MsgRef v) {
    myName = v;
    return this;
  }

  public int[] getItems() {
    return myItems;
  }

  public ReportGroup setItems(int[] v) {
    myItems = v;
    return this;
  }

  public MsgRef[] getTags() {
    return myTags;
  }

  public ReportGroup setTags(MsgRef[] v) {
    myTags = v;
    return this;
  }

  @Override @NotNull
  public BinaryClass klass() { return Klass.INSTANCE; }


  private static final Entity ENTITY = new Entity("service", "ReportGroup", "", "");

  static {
    ENTITY.setFields(new Field[]{
      new Field("Name", new Struct(MsgRef.Klass.INSTANCE.entity())),
      new Field("Items", new Slice("", new Primitive("uint32", Method.Uint32))),
      new Field("Tags", new Slice("", new Struct(MsgRef.Klass.INSTANCE.entity()))),
    });
    Namespace.register(Klass.INSTANCE);
  }
  public static void register() {}
  //<<<End:Java.ClassBody:1>>>
  public enum Klass implements BinaryClass {
    //<<<Start:Java.KlassBody:2>>>
    INSTANCE;

    @Override @NotNull
    public Entity entity() { return ENTITY; }

    @Override @NotNull
    public BinaryObject create() { return new ReportGroup(); }

    @Override
    public void encode(@NotNull Encoder e, BinaryObject obj) throws IOException {
      ReportGroup o = (ReportGroup)obj;
      e.value(o.myName);
      e.uint32(o.myItems.length);
      for (int i = 0; i < o.myItems.length; i++) {
        e.uint32(o.myItems[i]);
      }
      e.uint32(o.myTags.length);
      for (int i = 0; i < o.myTags.length; i++) {
        e.value(o.myTags[i]);
      }
    }

    @Override
    public void decode(@NotNull Decoder d, BinaryObject obj) throws IOException {
      ReportGroup o = (ReportGroup)obj;
      o.myName = new MsgRef();
      d.value(o.myName);
      o.myItems = new int[d.uint32()];
      for (int i = 0; i <o.myItems.length; i++) {
        o.myItems[i] = d.uint32();
      }
      o.myTags = new MsgRef[d.uint32()];
      for (int i = 0; i <o.myTags.length; i++) {
        o.myTags[i] = new MsgRef();
        d.value(o.myTags[i]);
      }
    }
    //<<<End:Java.KlassBody:2>>>
  }
}
