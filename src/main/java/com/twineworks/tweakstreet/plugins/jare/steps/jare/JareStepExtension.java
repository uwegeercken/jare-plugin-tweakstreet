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
