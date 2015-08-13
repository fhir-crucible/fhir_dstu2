package org.hl7.fhir.definitions.parsers;

import java.util.HashMap;
import java.util.Map;

import org.hl7.fhir.definitions.parsers.MarkDownResourceDefinition.MarkDownResourceDefinitionCode;
import org.hl7.fhir.definitions.parsers.MarkDownResourceDefinition.MarkDownResourceDefinitionCodeList;
import org.hl7.fhir.instance.model.ValueSet;
import org.hl7.fhir.instance.model.ValueSet.ConceptDefinitionComponent;
import org.hl7.fhir.instance.model.ValueSet.ConceptReferenceComponent;
import org.hl7.fhir.instance.model.ValueSet.ConceptSetComponent;
import org.hl7.fhir.instance.model.ValueSet.ValueSetComposeComponent;
import org.hl7.fhir.instance.model.ValueSet.ValueSetCodeSystemComponent;
import org.hl7.fhir.instance.utils.ToolingExtensions;
import org.hl7.fhir.utilities.Utilities;
import org.hl7.fhir.utilities.XLSXmlParser.Sheet;

public class CodeListToValueSetParser {

  private Sheet sheet;
  private ValueSet valueSet;
  private String version;
  private String sheetName;
  private MarkDownResourceDefinitionCodeList cdl;

  public CodeListToValueSetParser(Sheet sheet, String sheetName, ValueSet valueSet, String version, MarkDownResourceDefinitionCodeList cdl) {
    super();
    this.sheet = sheet;
    this.sheetName = sheetName;
    this.valueSet = valueSet;
    this.version = version;
    this.cdl = cdl;
  }

  public void execute() throws Exception {
    boolean hasDefine = false;
    for (int row = 0; row < sheet.rows.size(); row++) {
      hasDefine = hasDefine || Utilities.noString(sheet.getColumn(row, "System"));
    }

    Map<String, ConceptDefinitionComponent> codes = new HashMap<String, ConceptDefinitionComponent>();
    Map<String, ConceptDefinitionComponent> codesById = new HashMap<String, ConceptDefinitionComponent>();
    
    Map<String, ConceptSetComponent> includes = new HashMap<String, ConceptSetComponent>();
    
    if (hasDefine) {
      ValueSetCodeSystemComponent define = new ValueSetCodeSystemComponent();
      valueSet.setCodeSystem(define);
      define.setSystem("http://hl7.org/fhir/"+sheetName);
      define.setVersion(version);
      define.setCaseSensitive(true);

      for (int row = 0; row < sheet.rows.size(); row++) {
        if (cdl != null) {
          MarkDownResourceDefinitionCode mcd = new MarkDownResourceDefinitionCode();
          cdl.getCodes().add(mcd);
          mcd.setSystem(sheet.getColumn(row, "System"));
          mcd.setId(sheet.getColumn(row, "Id"));
          mcd.setCode(sheet.getColumn(row, "Code"));
          mcd.setDisplay(sheet.getColumn(row, "Display"));
          mcd.setDefinition(sheet.getColumn(row, "Definition"));
          mcd.setComment(sheet.getColumn(row, "Comment"));
          mcd.setV2(sheet.getColumn(row, "v2"));
          mcd.setV3(sheet.getColumn(row, "v3"));
          mcd.setParent(sheet.getColumn(row, "Parent"));
          for (String ct : sheet.columns) 
            if (ct.startsWith("Display:") && !Utilities.noString(sheet.getColumn(row, ct)))
              mcd.getLangs().put(ct.substring(8), sheet.getColumn(row, ct));
        }
        if (Utilities.noString(sheet.getColumn(row, "System"))) {
          MarkDownResourceDefinitionCode cd = new MarkDownResourceDefinitionCode();

          ConceptDefinitionComponent cc = new ConceptDefinitionComponent(); 
          cc.setUserData("id", sheet.getColumn(row, "Id"));
          cc.setCode(sheet.getColumn(row, "Code"));
          if (codes.containsKey(cc.getCode()))
            throw new Exception("Duplicate Code "+cc.getCode());
          codes.put(cc.getCode(), cc);
          codesById.put(cc.getUserString("id"), cc);
          cc.setDisplay(sheet.getColumn(row, "Display"));
          if (cc.hasCode() && !cc.hasDisplay())
            cc.setDisplay(Utilities.humanize(cc.getCode()));
          cc.setDefinition(sheet.getColumn(row, "Definition"));
          if (!Utilities.noString(sheet.getColumn(row, "Comment")))
            ToolingExtensions.addComment(cc, sheet.getColumn(row, "Comment"));
          cc.setUserData("v2", sheet.getColumn(row, "v2"));
          cc.setUserData("v3", sheet.getColumn(row, "v3"));
          for (String ct : sheet.columns) 
            if (ct.startsWith("Display:") && !Utilities.noString(sheet.getColumn(row, ct)))
              cc.addDesignation().setLanguage(ct.substring(8)).setValue(sheet.getColumn(row, ct));
          String parent = sheet.getColumn(row, "Parent");
          if (Utilities.noString(parent))
            define.addConcept(cc);
          else if (parent.startsWith("#") && codesById.containsKey(parent.substring(1)))
            codesById.get(parent.substring(1)).addConcept(cc);
          else if (codes.containsKey(parent))
            codes.get(parent).addConcept(cc);
          else
            throw new Exception("Parent "+parent+" not resolved in "+sheetName);
        }
      }
    }

    for (int row = 0; row < sheet.rows.size(); row++) {
      if (!Utilities.noString(sheet.getColumn(row, "System"))) {
        String system = sheet.getColumn(row, "System");
        ConceptSetComponent t = includes.get(system);
        if (t == null) {
          if (!valueSet.hasCompose())
            valueSet.setCompose(new ValueSetComposeComponent());
          t = valueSet.getCompose().addInclude();
          t.setSystem(system);
          includes.put(system, t);
        }
        ConceptReferenceComponent cc = t.addConcept();
        cc.setCode(sheet.getColumn(row, "Code"));
        if (codes.containsKey(cc.getCode()))
          throw new Exception("Duplicate Code "+cc.getCode());
        codes.put(cc.getCode(), null);
        cc.setDisplay(sheet.getColumn(row, "Display"));
        if (!Utilities.noString(sheet.getColumn(row, "Definition")))
          ToolingExtensions.addDefinition(cc, sheet.getColumn(row, "Definition"));
        if (!Utilities.noString(sheet.getColumn(row, "Comment")))
          ToolingExtensions.addComment(cc, sheet.getColumn(row, "Comment"));
        cc.setUserDataINN("v2", sheet.getColumn(row, "v2"));
        cc.setUserDataINN("v3", sheet.getColumn(row, "v3"));
        for (String ct : sheet.columns) 
          if (ct.startsWith("Display:") && !Utilities.noString(sheet.getColumn(row, ct)))
            cc.addDesignation().setLanguage(ct.substring(8)).setValue(sheet.getColumn(row, ct));       
      }
    }

  }


}
