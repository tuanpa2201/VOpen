package modules.sys.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import base.controller.VActionResponse;
import base.controller.VController;
import base.controller.VEnv;
import base.model.VField;
import base.model.VModel;
import base.model.VObject;
import base.util.Filter;
import modules.smarthome.ShLogger;

/**
 *
 * Mar 15, 2017
 * 
 * @author LongNT
 * 
 **/

public class SysSequence extends VModel {
  /**
   * Format Pattern: [String]_[String]_..._[String]_[xx...x]
   */
  private static final long serialVersionUID = 1L;
  public static final String MODEL_NAME = new String("Sys.Sequence");
  public static final String F_NAME = new String("name");
  public static final String F_PATTERN = new String("pattern");
  public static final String F_STEP = new String("step");
  public static final String F_START_NUMBER = new String("startNumber");

  @Override
  public String getName() {
    return MODEL_NAME;
  }

  @Override
  public Map<String, VField> getFields() {
    Map<String, VField> fields = new HashMap<>();
    fields.put(F_NAME, VField.string("Name", options("nullable", false)));
    fields.put(F_PATTERN, VField.string("Pattern", options("nullable", false)));
    fields.put(F_STEP, VField.integer("Step", options("nullable", false)));
    fields.put(F_START_NUMBER, VField.integer("Start Number", options("nullable", false)));
    return fields;
  }

  @Override
  public Map<String, Object> getDefaults() {
    super.getDefaults().put(F_START_NUMBER, 0);
    return super.getDefaults();
  }

  public static boolean createSequence(String name, String pattern, int step, int startNumber) {
    try {
      if (getSequenceByName(name) != null) {
        return false;
      }
      VController vc = VEnv.sudo().get(MODEL_NAME);
      Map<String, Object> sequence = new HashMap<>();
      sequence.put(F_NAME, name);
      sequence.put(F_PATTERN, pattern);
      sequence.put(F_STEP, step);
      sequence.put(F_START_NUMBER, startNumber);
      VActionResponse response = null;
      response = vc.create(sequence);
      return response.status;
    } catch (Exception e) {
      ShLogger.LOGGER.error("createSequence", e);
      return false;
    }
  }

  public static VObject getSequenceByName(String name) {
    VObject notification = null;
    try {
      VController controller = VEnv.sudo().get(MODEL_NAME);
      String sqlWhere = "name =:p1";
      Map<String, Object> params = new HashMap<>();
      params.put("p1", name);
      List<Integer> lst = controller.search(new Filter(sqlWhere, params));
      if (lst != null && !lst.isEmpty()) {
        notification = controller.browse(lst.get(0));
      }
    } catch (Exception e) {
      ShLogger.LOGGER.error("getSequenceByName", e);
    }
    return notification;
  }

  public static boolean deleteSequence(String name) {
    try {
      VObject se = getSequenceByName(name);
      boolean deleteRef = SysSequenceDetail.deleteFromSequence(se);
      if (deleteRef) {
        VController controller = VEnv.sudo().get(MODEL_NAME);
        return controller.delete(Arrays.asList(se.getId())).status;
      } else {
        return false;
      }
    } catch (Exception e) {
      ShLogger.LOGGER.error("deleteSequence", e);
      return false;
    }
  }

  public static String getNextSequence(String nameSequence, String... strings) {
    try {
      VObject sequenceObj = SysSequence.getSequenceByName(nameSequence);
      if (sequenceObj == null) {
        return null;
      }
      String str = sequenceObj.getValue(SysSequence.F_PATTERN).toString();
      String sequenceValue = sequenceObj.getValue(SysSequence.F_START_NUMBER).toString();
      int step = (Integer) sequenceObj.getValue(SysSequence.F_STEP);
      List<String> listPt = new ArrayList<>();

      String pt = "";
      int lengthParams = 0;
      int lengthIndex = 0;
      String indexSequence = "IndexSequence";
      for (String s : str.split("")) {
        if (s.equals("[") || s.equals("]")) {
          if (s.equals("]")) {
            if (isParamSequence(pt)) {
              lengthIndex = pt.length();
              listPt.add(indexSequence);
            } else {
              listPt.add("String" + String.valueOf(lengthParams));
              lengthParams++;
            }
          } else {
            if (!pt.isEmpty()) {
              listPt.add(pt);
            }
          }
          pt = "";
        } else {
          pt = pt + s;
        }
      }
      if (!pt.isEmpty()) {
        listPt.add(pt);
      }

      Map<String, String> mapParams = new HashMap<>();
      if (lengthParams == strings.length) {
        for (int i = 0; i < strings.length; i++) {
          mapParams.put("String" + String.valueOf(i), strings[i]);
        }
      }
      String formatNotSequence = "";
      for (String s : listPt) {
        if (s.contains("String")) {
          formatNotSequence = formatNotSequence + mapParams.get(s);
        } else {
          if (!s.equals(indexSequence)) {
            formatNotSequence = formatNotSequence + s;
          }
        }
      }

      VObject sd = SysSequenceDetail.getFromKey(sequenceObj, formatNotSequence);
      if (sd == null) {
        boolean create = SysSequenceDetail.createSequenceDetail(sequenceObj, formatNotSequence,
            Integer.parseInt(sequenceValue));
        if (!create) {
          return null;
        }
      } else {
        int index = (Integer) sd.getValue(SysSequenceDetail.F_SEQUENCE);
        index = index + step;
        boolean update = SysSequenceDetail.updateIndex(sd.getId(), index);
        if (update) {
          sequenceValue = String.valueOf(index);
        } else {
          return null;
        }
      }

      int lengthVi = sequenceValue.length();
      for (int i = 0; i < lengthIndex - lengthVi; i++) {
        sequenceValue = "0" + sequenceValue;
      }
      String formatAll = "";
      for (String s : listPt) {
        if (s.equals(indexSequence)) {
          formatAll = formatAll + sequenceValue;
        } else {
          if (s.contains("String")) {
            formatAll = formatAll + mapParams.get(s);
          } else {
            formatAll = formatAll + s;
          }
        }
      }
      return formatAll;
    } catch (Exception e) {
      ShLogger.LOGGER.error("getNextSequence", e);
      return null;
    }

  }

  private static boolean isParamSequence(String param) {
    boolean isSequence = true;
    for (String str : param.split("")) {
      if (!str.equals("x")) {
        isSequence = false;
      }
    }
    return isSequence;
  }

}
