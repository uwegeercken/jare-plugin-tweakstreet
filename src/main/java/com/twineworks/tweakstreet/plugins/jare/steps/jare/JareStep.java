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

import com.datamelt.rules.core.RuleExecutionResult;
import com.datamelt.rules.core.RuleGroup;
import com.datamelt.rules.core.RuleSubGroup;
import com.datamelt.rules.core.XmlAction;
import com.datamelt.rules.core.XmlRule;
import com.datamelt.rules.core.util.Converter;
import com.datamelt.rules.engine.BusinessRulesEngine;
import com.datamelt.util.RowField;
import com.datamelt.util.RowFieldCollection;
import com.twineworks.tweakflow.lang.types.Types;
import com.twineworks.tweakflow.lang.values.*;
import com.twineworks.tweakstreet.api.desc.results.ResultDesc;
import com.twineworks.tweakstreet.api.desc.settings.SettingDesc;
import com.twineworks.tweakstreet.api.steps.BasePassThroughStep;
import com.twineworks.tweakstreet.api.steps.PassThroughStep;

import java.io.File;
import java.io.FilenameFilter;
import java.nio.file.Path;
import java.util.*;
import java.util.zip.ZipFile;

public final class JareStep extends BasePassThroughStep implements PassThroughStep {

  static final List<SettingDesc> declaredSettings = Arrays.asList(
    new SettingDesc("rules"),
    new SettingDesc("input"),
    new SettingDesc("collectResults")
  );

  static final List<SettingDesc> declaredStaticSettings = Collections.emptyList();

  static final List<ResultDesc> declaredResults = Arrays.asList(
    new ResultDesc(Types.DICT, "output"),
    new ResultDesc(Types.LIST, "results"),
    new ResultDesc(Types.LIST, "summary")
  );

  private final JareStepSettings s = new JareStepSettings();

  private BusinessRulesEngine engine;
  private RowFieldCollection row;
  private long rowNr = 0;

  @Override
  public void setSettingProviders(Map<String, ValueProvider> settings) {
    this.s.init(settings);
  }

  @Override
  public Map<String, ValueProvider> getResultProviders(Set<String> names) {

    HashMap<String, ValueProvider> providers = new HashMap<>();
    providers.put("output", () -> rowToValue());
    providers.put("results", () -> resultsToValue());
    providers.put("summary", () -> summaryToValue());

    return providers;
  }

  private void makeEngine() throws Exception {

    // get the path as configured in the step, resolve "./relative/paths" against flow location
    Path path = s.rules.get(context.getFlowInfo().getFlowPath());
    File file = path.toFile();

    log.debug("using rule engine file: {}", path);

    if (!file.exists()) {
      throw new RuntimeException("specified rule file not found: " + path);
    }
    // we can use a zip file containing all rules
    if (file.isFile() && file.getName().toLowerCase().endsWith(".zip")) {
      log.debug("found zip file to read xml rule files: {}", path);
      ZipFile zip = new ZipFile(file);
      engine = new BusinessRulesEngine(zip);
    }
    // we can also use a directory and read all files from there
    else if (file.isDirectory()) {
      // use a filter - we only want to read xml files
      log.debug("found folder to read xml rules files: {}", path);
      FilenameFilter fileNameFilter = new FilenameFilter() {
        @Override
        public boolean accept(File dir, String name) {
          if (name.lastIndexOf('.') > 0) {
            // get last index for '.' char
            int lastIndex = name.lastIndexOf('.');

            // get extension
            String str = name.substring(lastIndex);

            // match path name extension
            return str.equals(".xml");
          }
          return false;
        }
      };
      // get list of files for the given filter
      File[] listOfFiles = file.listFiles(fileNameFilter);
      // initialize rule engine with list of files
      engine = new BusinessRulesEngine(listOfFiles);
    } else if (file.isFile()) {
      log.debug("found single xml rules file: {}", path);
      engine = new BusinessRulesEngine(path.toString());
    }

    log.info("initialized business rule engine version: {} using: {}", BusinessRulesEngine.getVersion(), path);

    if (engine.getNumberOfGroups() == 0) {
      log.info("attention: project zip file contains no rulegroups or no ruleroups that are active based on the valid from/until date");
    }

    engine.setPreserveRuleExcecutionResults(s.collectResults.get());
  }

  private Value rowToValue() {
    if (row == null) return Values.NIL;
    TransientDictValue t = new TransientDictValue();
    for (RowField field : row.getFields()) {
      t.put(field.getName(), Values.make(field.getValue()));
    }
    return Values.make(t.persistent());
  }

  private Value summaryToValue() {

	ListValue allResults = new ListValue();

    TransientDictValue vSummary = new TransientDictValue();
    vSummary.put("groups", Values.make(engine.getNumberOfGroups()));
    vSummary.put("groups_failed", Values.make(engine.getNumberOfGroupsFailed()));
    vSummary.put("groups_skipped", Values.make(engine.getNumberOfGroupsSkipped()));
    vSummary.put("groups_actions", Values.make(engine.getNumberOfActions()));
    vSummary.put("groups_actions_executed", Values.make(engine.getNumberOfActionsExecuted()));
    vSummary.put("groups_rules", Values.make(engine.getNumberOfRules()));
    vSummary.put("groups_rules_failed", Values.make(engine.getNumberOfRulesFailed()));

    allResults = allResults.append(Values.make(vSummary.persistent()));
    return Values.make(allResults);
  }

  private Value resultsToValue() {

    if (!s.collectResults.isTrue()) return Values.NIL;

    ListValue allResults = new ListValue();

    // loop over all groups
    for (int f = 0; f < engine.getGroups().size(); f++) {
      RuleGroup group = engine.getGroups().get(f);
      
      ArrayList<XmlAction> actions = group.getActions();
      // loop over all actions
      ListValue vActions = new ListValue();
      for (int k = 0; k < actions.size(); k++) {
    	  XmlAction action = actions.get(k);
    	  
    	  TransientDictValue vAction = new TransientDictValue();
    	  vAction.put("action_id", Values.make(action.getId()));
    	  vAction.put("action_execute_if", Values.make(Converter.convertActionTypeToString(action.getExecuteIf()))); 
    	  vActions = vActions.append(Values.make(vAction.persistent()));
      }
      
      // loop over all subgroups
      ListValue vSubGroups = new ListValue();
      for (int g = 0; g < group.getSubGroups().size(); g++) {

        RuleSubGroup subgroup = group.getSubGroups().get(g);
        ArrayList<RuleExecutionResult> results = subgroup.getExecutionCollection().getResults();
        ListValue vRuleResults = new ListValue();

        // loop over all results
        for (int h = 0; h < results.size(); h++) {
          RuleExecutionResult result = results.get(h);
          XmlRule rule = result.getRule();

          TransientDictValue vRule = new TransientDictValue();
          vRule.put("rule_id", Values.make(rule.getId()));
          vRule.put("rule_failed", Values.make(Converter.convertIntegerToBoolean(rule.getFailed())));
          vRule.put("rule_message", Values.make(result.getMessage()));

          vRuleResults = vRuleResults.append(Values.make(vRule.persistent()));
        }

        TransientDictValue vSubGroup = new TransientDictValue();
        vSubGroup.put("subgroup_id", Values.make(subgroup.getId()));
        vSubGroup.put("subgroup_failed", Values.make(Converter.convertIntegerToBoolean((subgroup.getFailed()))));
        vSubGroup.put("subgroup_operator", Values.make(subgroup.getLogicalOperatorSubGroupAsString()));
        vSubGroup.put("subgroup_operator_rules", Values.make(subgroup.getLogicalOperatorRulesAsString()));
        vSubGroup.put("subgroup_rules", Values.make(vRuleResults));
        vSubGroups = vSubGroups.append(Values.make(vSubGroup.persistent()));

        results.clear();
      }

      TransientDictValue vGroup = new TransientDictValue();
      vGroup.put("group_id", Values.make(group.getId()));
      vGroup.put("group_logic", Values.make(group.getRuleLogic()));
      vGroup.put("group_failed", Values.make(Converter.convertIntegerToBoolean(group.getFailed())));
      vGroup.put("group_actions", Values.make(group.getNumberOfActions()));
      vGroup.put("group_actions_executed", Values.make(group.getNumberOfActionsExecuted()));
      vGroup.put("group_rules", Values.make(group.getNumberOfRules()));
      vGroup.put("group_rules_failed", Values.make(group.getNumberOfRulesFailed()));
      vGroup.put("group_subgroups", Values.make(vSubGroups));
      vGroup.put("group_actions", Values.make(vActions));
      
      allResults = allResults.append(Values.make(vGroup.persistent()));

    }

    engine.getRuleExecutionCollection().clear();
    
    return Values.make(allResults);
  }


  private Object toEngineType(Value v) {

    if (v.isNil()) {
      return null;
    } else if (v.isString()) {
      return v.string();
    } else if (v.isBoolean()) {
      return v.bool();
    } else if (v.isLongNum()) {
      return v.longNum();
    } else if (v.isDoubleNum()) {
      return v.doubleNum();
    } else if (v.isDecimal()) {
      return v.decimal();
    } else if (v.isDateTime()) {
      return new Date(v.dateTime().getInstant().toEpochMilli());
    } else if (v.isBinary()) {
      return v.bytes();
    } else if (v.isDict()) {
      Iterator<Map.Entry<String, Value>> i = v.dict().entryIterator();
      HashMap<String, Object> map = new HashMap<>();
      while (i.hasNext()) {
        Map.Entry<String, Value> entry = i.next();
        map.put(entry.getKey(), toEngineType(entry.getValue()));
      }
      return map;
    } else if (v.isList()) {
      Iterator<Value> i = v.list().iterator();
      ArrayList<Object> list = new ArrayList<>();
      while (i.hasNext()) {
        list.add(toEngineType(i.next()));
      }
      return list;
    } else {
      throw new RuntimeException("Cannot pass unsupported value to rule engine: " + ValueInspector.inspect(v));
    }
  }

  private void makeRowFieldCollection() {

    // holds fields for the rule engine row
    // convert input value to engine types
    DictValue dictValue = s.input.get();
    Object[] fields = new Object[dictValue.size()];
    String[] fieldNames = new String[dictValue.size()];

    Iterator<Map.Entry<String, Value>> entryIterator = dictValue.entryIterator();
    int i=0;
    while (entryIterator.hasNext()) {
      Map.Entry<String, Value> inputField = entryIterator.next();
      fieldNames[i] = inputField.getKey();
      fields[i] = toEngineType(inputField.getValue());
      i++;
    }

    // object/collection that holds all the fields and their values required for running the rule engine
    row = new RowFieldCollection(fieldNames, fields);
  }

  private void applySettings() throws Exception {

    if (s.rules.hasChanged()) {
      makeEngine();
    }

    if (s.input.hasChanged()) {
      makeRowFieldCollection();
    }

  }

  @Override
  public void processRow(Value inputRow) {

    try {
      rowNr++;
      applySettings();
      engine.run("row nr: " + rowNr, row);
      logOutcome();
      

    } catch (Exception e) {
      throw new RuntimeException(e);
    }

  }


  private void logOutcome() throws Exception {

    if (log.isDebugEnabled()) {
      for (int i = 0; i < engine.getGroups().size(); i++) {
        for (int f = 0; f < engine.getGroups().get(i).getSubGroups().size(); f++) {
          String message = "line: " + rowNr + ", group: " + engine.getGroups().get(i).getId() + ", subgroup: " + engine.getGroups().get(i).getSubGroups().get(f).getId() + ", failed: " + engine.getGroups().get(i).getSubGroups().get(f).getFailedAsString();
          log.debug(message);
        }
      }
    } else if (log.isTraceEnabled()) {
      for (int i = 0; i < engine.getGroups().size(); i++) {
        for (int f = 0; f < engine.getGroups().get(i).getSubGroups().size(); f++) {
          for (int g = 0; g < engine.getGroups().get(i).getSubGroups().get(f).getRulesCollection().size(); g++) {
            String message = "line: " + rowNr + ", group: " + engine.getGroups().get(i).getId() + ", subgroup: " + engine.getGroups().get(i).getSubGroups().get(f).getId() + ", rule: " + engine.getGroups().get(i).getSubGroups().get(f).getRulesCollection().get(g).getId() + ", failed: " + engine.getGroups().get(i).getSubGroups().get(f).getResults().get(g).getFailedAsString() + ", " + engine.getGroups().get(i).getSubGroups().get(f).getResults().get(g).getMessage();
            log.trace(message);
          }
        }
      }
    }
  }

}
