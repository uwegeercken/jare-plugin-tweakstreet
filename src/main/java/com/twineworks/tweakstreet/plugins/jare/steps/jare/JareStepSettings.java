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
