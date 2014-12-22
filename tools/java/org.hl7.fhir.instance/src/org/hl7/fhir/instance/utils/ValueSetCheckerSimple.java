package org.hl7.fhir.instance.utils;

import java.util.List;

import org.hl7.fhir.instance.model.UriType;
import org.hl7.fhir.instance.model.ValueSet;
import org.hl7.fhir.instance.model.ValueSet.ConceptDefinitionComponent;
import org.hl7.fhir.instance.model.ValueSet.ConceptReferenceComponent;
import org.hl7.fhir.instance.model.ValueSet.ConceptSetComponent;
import org.hl7.fhir.instance.model.ValueSet.ConceptSetFilterComponent;
import org.hl7.fhir.instance.model.ValueSet.ValueSetExpansionContainsComponent;
import org.hl7.fhir.instance.utils.ValueSetExpander.ETooCostly;
import org.hl7.fhir.instance.utils.ValueSetExpander.ValueSetExpansionOutcome;

public class ValueSetCheckerSimple implements ValueSetChecker {

  private ValueSet valueset;
  private ValueSetExpanderFactory factory;
  private WorkerContext context;

  public ValueSetCheckerSimple(ValueSet source, ValueSetExpanderFactory factory, WorkerContext context) {
    this.valueset = source;
    this.factory = factory;
    this.context = context;
  }

  @Override
  public boolean codeInValueSet(String system, String code) throws ETooCostly {
    if (valueset.hasDefine() && system.equals(valueset.getDefine().getSystem()) && codeInDefine(valueset.getDefine().getConcept(), code, valueset.getDefine().getCaseSensitive()))
     return true;

    if (valueset.hasCompose()) {
      boolean ok = false;
      for (UriType uri : valueset.getCompose().getImport()) {
        ok = ok || inImport(uri.getValue(), system, code);
      }
      for (ConceptSetComponent vsi : valueset.getCompose().getInclude()) {
        ok = ok || inComponent(vsi, system, code);
      }
      for (ConceptSetComponent vsi : valueset.getCompose().getExclude()) {
        ok = ok && !inComponent(vsi, system, code);
      }
    }
    
    return false;
  }

  private boolean inImport(String uri, String system, String code) throws ETooCostly {
    ValueSet vs = context.getValueSets().get(uri);
    if (vs == null) 
      return false ; // we can't tell
    return codeInExpansion(factory.getExpander().expand(vs), system, code);
  }

  private boolean codeInExpansion(ValueSetExpansionOutcome vso, String system, String code) throws ETooCostly {
    if (vso.getService() != null) {
      return vso.getService().codeInValueSet(system, code);
    } else {
      for (ValueSetExpansionContainsComponent c : vso.getValueset().getExpansion().getContains()) {
        if (code.equals(c.getCode()) && (system == null || system.equals(c.getSystem())))
          return true;
        if (codeinExpansion(c, system, code)) 
          return true;
      }
    }
    return false;
  }

  private boolean codeinExpansion(ValueSetExpansionContainsComponent cnt, String system, String code) {
    for (ValueSetExpansionContainsComponent c : cnt.getContains()) {
      if (code.equals(c.getCode()) && system.equals(c.getSystem().toString()))
        return true;
      if (codeinExpansion(c, system, code)) 
        return true;
    }
    return false;
  }


  private boolean inComponent(ConceptSetComponent vsi, String system, String code) {
    if (!vsi.getSystem().equals(system))
      return false; 
    // whether we know the system or not, we'll accept the stated codes at face value
    for (ConceptReferenceComponent cc : vsi.getConcept())
      if (cc.getCode().equals(code)) {
        return true;
      }
      
    if (context.getCodeSystems().containsKey(system)) {
      ValueSet def = context.getCodeSystems().get(system);
      if (!def.getDefine().getCaseSensitive()) {
        // well, ok, it's not case sensitive - we'll check that too now
        for (ConceptReferenceComponent cc : vsi.getConcept())
          if (cc.getCode().equalsIgnoreCase(code)) {
            return false;
          }
      }
      if (vsi.getConcept().isEmpty() && vsi.getFilter().isEmpty()) {
        return codeInDefine(def.getDefine().getConcept(), code, def.getDefine().getCaseSensitive());
      }
      for (ConceptSetFilterComponent f: vsi.getFilter())
        throw new Error("not done yet: "+f.getValue());

      return false;
    } else if (context.getTerminologyServices().supportsSystem(system)) {
      return context.getTerminologyServices().checkVS(vsi, system, code);
    } else
      // we don't know this system, and can't resolve it
      return false;
  }

  private boolean codeInDefine(List<ConceptDefinitionComponent> concepts, String code, boolean caseSensitive) {
    for (ConceptDefinitionComponent c : concepts) {
      if (caseSensitive && code.equals(c.getCode()))
        return true;
      if (!caseSensitive && code.equalsIgnoreCase(c.getCode()))
        return true;
      if (codeInDefine(c.getConcept(), code, caseSensitive))
        return true;
    }
    return false;
  }

}
