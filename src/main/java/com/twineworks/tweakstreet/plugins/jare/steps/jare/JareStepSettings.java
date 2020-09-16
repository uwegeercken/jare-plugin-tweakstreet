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

import com.twineworks.tweakflow.lang.values.ValueProvider;
import com.twineworks.tweakstreet.api.util.settings.BooleanSetting;
import com.twineworks.tweakstreet.api.util.settings.DictSetting;
import com.twineworks.tweakstreet.api.util.settings.LocalPathSetting;

import java.util.Map;

public class JareStepSettings {

  LocalPathSetting rules;
  DictSetting input;
  BooleanSetting collectResults;

  void init(Map<String, ValueProvider> settings){

    rules = new LocalPathSetting.Builder("rules")
      .from(settings)
      .nullable(false)
      .build();

    input = new DictSetting.Builder("input")
      .from(settings)
      .nullable(true)
      .build();

    collectResults = new BooleanSetting.Builder("collectResults")
      .from(settings)
      .nullable(true)
      .nilIsFalse()
      .build();

  }

}
