/*
 * Copyright 2012-present Facebook, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may obtain
 * a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */

package com.facebook.buck.android;

import com.facebook.buck.shell.ShellStep;
import com.facebook.buck.step.ExecutionContext;
import com.facebook.buck.util.AndroidPlatformTarget;
import com.facebook.buck.util.ProjectFilesystem;
import com.facebook.buck.util.Verbosity;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

import java.nio.file.Path;
import java.util.Set;

public class DxStep extends ShellStep {

  private final String outputDexFile;
  private final Set<Path> filesToDex;

  /**
   * @param outputDexFile path to the file where the generated classes.dex should go
   * @param filesToDex each element in this set is a path to a .class file, a zip file of .class
   *     files, or a directory of .class files
   */
  public DxStep(String outputDexFile, Iterable<Path> filesToDex) {
    this.outputDexFile = Preconditions.checkNotNull(outputDexFile);
    this.filesToDex = ImmutableSet.copyOf(filesToDex);
  }

  @Override
  protected ImmutableList<String> getShellCommandInternal(ExecutionContext context) {
    ImmutableList.Builder<String> builder = ImmutableList.builder();

    AndroidPlatformTarget androidPlatformTarget = context.getAndroidPlatformTarget();
    builder.add(androidPlatformTarget.getDxExecutable().getAbsolutePath());
    builder.add("--dex");

    // --statistics flag, if appropriate.
    if (context.getVerbosity().shouldPrintSelectCommandOutput()) {
      builder.add("--statistics");
    }

    // verbose flag, if appropriate.
    if (context.getVerbosity().shouldUseVerbosityFlagIfAvailable()) {
      builder.add("--verbose");
    }

    builder.add("--output", outputDexFile);
    ProjectFilesystem projectFilesystem = context.getProjectFilesystem();
    for (Path fileToDex : filesToDex) {
      builder.add(projectFilesystem.resolve(fileToDex).toString());
    }

    return builder.build();
  }

  @Override
  protected boolean shouldPrintStderr(Verbosity verbosity) {
    return verbosity.shouldPrintSelectCommandOutput();
  }

  @Override
  protected boolean shouldPrintStdout(Verbosity verbosity) {
    return verbosity.shouldPrintSelectCommandOutput();
  }

  @Override
  public String getShortName() {
    return "dx";
  }

}
