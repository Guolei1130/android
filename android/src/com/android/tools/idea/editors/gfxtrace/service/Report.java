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

import com.android.tools.idea.editors.gfxtrace.service.msg.Arg;
import com.android.tools.idea.editors.gfxtrace.service.stringtable.StringTable;
import com.android.tools.rpclib.binary.*;
import com.android.tools.rpclib.schema.*;
import com.google.common.collect.Range;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;

public final class Report implements BinaryObject {

  @Nullable("there's no report item associated to a given atom id")
  public Range<Integer> getForAtom(long atomId) {
    final ReportItem key = new ReportItem();
    key.setAtom(atomId);
    int index = Arrays.binarySearch(myItems, key, (lhs, rhs) -> Long.compare(lhs.getAtom(), rhs.getAtom()));
    if (index < 0 || index >= myItems.length) {
      return null;
    }
    int start = index;
    while (start >= 0 && myItems[start].getAtom() == atomId) {
      start--;
    }
    int end = index;
    while (end < myItems.length && myItems[end].getAtom() == atomId) {
      end++;
    }
    return Range.closed(start + 1, end - 1);
  }

  @Nullable("there's no report items associated to a given atom range")
  public Range<Integer> getForAtoms(long firstAtomId, long lastAtomId) {
    final ReportItem key = new ReportItem();
    key.setAtom(firstAtomId);
    int index = Arrays.binarySearch(myItems, key, (lhs, rhs) -> (int)(lhs.getAtom() - rhs.getAtom()));
    if (index < 0 || index >= myItems.length) {
      return null;
    }
    int start = index;
    while (start >= 0 && myItems[start].getAtom() == firstAtomId) {
      start--;
    }
    int end = index;
    while (end < myItems.length && myItems[end].getAtom() <= lastAtomId) {
      end++;
    }
    return Range.closed(start + 1, end - 1);
  }

  public String constructMessage(int reportItemIndex) {
    return constructMessage(myItems[reportItemIndex]);
  }

  // TODO: Check if we have to recycle a map
  public String constructMessage(@NotNull final ReportItem reportItem) {
    MsgRef ref = reportItem.getMessage();
    Map<String, BinaryObject> argMap = Arg.constructMap(myMsgArguments, ref.getArguments());
    return StringTable.getMessage(myMsgIdentifiers[ref.getIdentifier()], argMap);
  }

  // Groups are children for now, structure may change...
  public int getChildCount() {
    return myGroups.length;
  }

  //<<<Start:Java.ClassBody:1>>>
  private ReportItem[] myItems;
  private ReportGroup[] myGroups;
  private String[] myMsgIdentifiers;
  private Arg[] myMsgArguments;

  // Constructs a default-initialized {@link Report}.
  public Report() {}


  public ReportItem[] getItems() {
    return myItems;
  }

  public Report setItems(ReportItem[] v) {
    myItems = v;
    return this;
  }

  public ReportGroup[] getGroups() {
    return myGroups;
  }

  public Report setGroups(ReportGroup[] v) {
    myGroups = v;
    return this;
  }

  public String[] getMsgIdentifiers() {
    return myMsgIdentifiers;
  }

  public Report setMsgIdentifiers(String[] v) {
    myMsgIdentifiers = v;
    return this;
  }

  public Arg[] getMsgArguments() {
    return myMsgArguments;
  }

  public Report setMsgArguments(Arg[] v) {
    myMsgArguments = v;
    return this;
  }

  @Override @NotNull
  public BinaryClass klass() { return Klass.INSTANCE; }


  private static final Entity ENTITY = new Entity("service", "Report", "", "");

  static {
    ENTITY.setFields(new Field[]{
      new Field("Items", new Slice("", new Struct(ReportItem.Klass.INSTANCE.entity()))),
      new Field("Groups", new Slice("", new Struct(ReportGroup.Klass.INSTANCE.entity()))),
      new Field("MsgIdentifiers", new Slice("", new Primitive("string", Method.String))),
      new Field("MsgArguments", new Slice("", new Struct(Arg.Klass.INSTANCE.entity()))),
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
    public BinaryObject create() { return new Report(); }

    @Override
    public void encode(@NotNull Encoder e, BinaryObject obj) throws IOException {
      Report o = (Report)obj;
      e.uint32(o.myItems.length);
      for (int i = 0; i < o.myItems.length; i++) {
        e.value(o.myItems[i]);
      }
      e.uint32(o.myGroups.length);
      for (int i = 0; i < o.myGroups.length; i++) {
        e.value(o.myGroups[i]);
      }
      e.uint32(o.myMsgIdentifiers.length);
      for (int i = 0; i < o.myMsgIdentifiers.length; i++) {
        e.string(o.myMsgIdentifiers[i]);
      }
      e.uint32(o.myMsgArguments.length);
      for (int i = 0; i < o.myMsgArguments.length; i++) {
        e.value(o.myMsgArguments[i]);
      }
    }

    @Override
    public void decode(@NotNull Decoder d, BinaryObject obj) throws IOException {
      Report o = (Report)obj;
      o.myItems = new ReportItem[d.uint32()];
      for (int i = 0; i <o.myItems.length; i++) {
        o.myItems[i] = new ReportItem();
        d.value(o.myItems[i]);
      }
      o.myGroups = new ReportGroup[d.uint32()];
      for (int i = 0; i <o.myGroups.length; i++) {
        o.myGroups[i] = new ReportGroup();
        d.value(o.myGroups[i]);
      }
      o.myMsgIdentifiers = new String[d.uint32()];
      for (int i = 0; i <o.myMsgIdentifiers.length; i++) {
        o.myMsgIdentifiers[i] = d.string();
      }
      o.myMsgArguments = new Arg[d.uint32()];
      for (int i = 0; i <o.myMsgArguments.length; i++) {
        o.myMsgArguments[i] = new Arg();
        d.value(o.myMsgArguments[i]);
      }
    }
    //<<<End:Java.KlassBody:2>>>
  }
}
