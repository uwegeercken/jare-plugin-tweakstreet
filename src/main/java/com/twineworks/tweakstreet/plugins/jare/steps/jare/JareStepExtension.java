/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.twineworks.tweakstreet.plugins.jare.steps.jare;

import com.twineworks.tweakstreet.api.desc.results.ResultDesc;
import com.twineworks.tweakstreet.api.desc.settings.SettingDesc;
import com.twineworks.tweakstreet.api.steps.PassThroughStep;
import com.twineworks.tweakstreet.api.steps.PassThroughStepExtension;
import org.pf4j.Extension;

import java.util.List;


@Extension
public final class JareStepExtension implements PassThroughStepExtension {

  @Override
  public String getTypeId() {
    return this.getClass().getPackage().getName();
  }

  @Override
  public PassThroughStep newInstance() {
    return new JareStep();
  }

  @Override
  public List<ResultDesc> getDeclaredResults() {
    return JareStep.declaredResults;
  }

  @Override
  public List<SettingDesc> getDeclaredSettings() {
    return JareStep.declaredSettings;
  }

  @Override
  public List<SettingDesc> getDeclaredStaticSettings() {
    return JareStep.declaredStaticSettings;
  }


}
