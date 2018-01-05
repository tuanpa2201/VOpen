package modules.sys.model;

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

public class SysSequenceDetail extends VModel {
  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  public static final String MODEL_NAME = new String("Sys.Sequence.Detail");
  public static final String F_VALUE_FORMAT = new String("valueFormat");
  public static final String F_SEQUENCE = new String("sequence");
  public static final String F_SEQUENCE_ID = new String("sequenceId");

  @Override
  public String getName() {
    return MODEL_NAME;
  }

  @Override
  public Map<String, VField> getFields() {
    Map<String, VField> fields = new HashMap<>();
    fields.put(F_VALUE_FORMAT, VField.string("Value Format", options("nullable", false)));
    fields.put(F_SEQUENCE, VField.integer("Sequence", options("nullable", false)));
    fields.put(F_SEQUENCE_ID, VField.many2one("Parent Sequence", SysSequence.MODEL_NAME, options()));
    return fields;
  }

  @Override
  public Map<String, Object> getDefaults() {
    return super.getDefaults();
  }

  public static boolean createSequenceDetail(VObject parent, String valueFormat, int sequence) {
    try {
      VController vc = VEnv.sudo().get(MODEL_NAME);
      Map<String, Object> sequenceDetail = new HashMap<>();
      sequenceDetail.put(F_SEQUENCE_ID, parent);
      sequenceDetail.put(F_VALUE_FORMAT, valueFormat);
      sequenceDetail.put(F_SEQUENCE, sequence);
      VActionResponse response = vc.create(sequenceDetail);
      return response.status;
    } catch (Exception e) {
      ShLogger.LOGGER.error("createSequenceDetail", e);
      return false;
    }
  }

  public static boolean updateIndex(int id, int sequence) {
    try {
      VController vc = VEnv.sudo().get(MODEL_NAME);
      Map<String, Object> sequenceDetail = new HashMap<>();
      sequenceDetail.put(F_SEQUENCE, sequence);
      VActionResponse response = vc.update(Arrays.asList(id), sequenceDetail);
      return response.status;
    } catch (Exception e) {
      ShLogger.LOGGER.error("updateIndex", e);
      return false;
    }
  }

  public static VObject getFromKey(VObject parent, String valueFormat) {
    VObject notification = null;
    try {
      VController controller = VEnv.sudo().get(MODEL_NAME);
      String sqlWhere = "sequenceId =:p1 and valueFormat =:p2";
      Map<String, Object> params = new HashMap<>();
      params.put("p1", parent);
      params.put("p2", valueFormat);
      List<Integer> lst = controller.search(new Filter(sqlWhere, params));
      if (lst != null && !lst.isEmpty()) {
        notification = controller.browse(lst.get(0));
      }
    } catch (Exception e) {
      ShLogger.LOGGER.error("getFromKey", e);
    }
    return notification;
  }

  public static boolean deleteFromSequence(VObject sequence) {
    boolean result = true;
    try {
      VController controller = VEnv.sudo().get(MODEL_NAME);
      String sqlWhere = "sequenceId =:p1";
      Map<String, Object> params = new HashMap<>();
      params.put("p1", sequence);
      List<Integer> lst = controller.search(new Filter(sqlWhere, params));
      result = controller.delete(lst).status;
    } catch (Exception e) {
      ShLogger.LOGGER.error("deleteFromSequence", e);
    }
    return result;
  }

}
