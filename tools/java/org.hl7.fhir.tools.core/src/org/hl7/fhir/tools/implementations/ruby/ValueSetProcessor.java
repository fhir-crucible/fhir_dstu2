package org.hl7.fhir.tools.implementations.ruby;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hl7.fhir.definitions.model.DefinedCode;
import org.hl7.fhir.definitions.model.Definitions;
import org.hl7.fhir.instance.model.UriType;
import org.hl7.fhir.instance.model.ValueSet;
import org.hl7.fhir.instance.model.ValueSet.ConceptDefinitionComponent;
import org.hl7.fhir.instance.model.ValueSet.ConceptReferenceComponent;
import org.hl7.fhir.instance.model.ValueSet.ConceptSetComponent;
import org.hl7.fhir.instance.model.ValueSet.ConceptSetFilterComponent;
import org.hl7.fhir.instance.model.ValueSet.FilterOperator;
import org.hl7.fhir.instance.utils.ToolingExtensions;

/**
 * Processes ValueSets, to get all the codes, whether declared in-line,
 * included, or imported. Currently, only supports the "concept is a" filter.
 * Does not resolve hyper-links. Included and imported value sets are only
 * resolved if the data is local.
 */
public class ValueSetProcessor 
{
  private List<DefinedCode> allCodes = null;
  private Definitions defns = null;
  protected Set<String> undefinedValueSets = new HashSet<String>();
  protected Set<String> emptyValueSets = new HashSet<String>();

  public ValueSetProcessor(Definitions defns) {
    this.defns = defns;
    this.allCodes = new ArrayList<DefinedCode>();
  }
  
  /**
   * Recursively get all the codes defined in a ValueSet, including
   * imports and included. Does not resolve hyper-linked remote value
   * sets.
   * @param valueSet
   * @return List of all codes
   */
  public List<DefinedCode> getAllCodes(ValueSet valueSet) {
    allCodes.clear();
    extractCodesFromValueSet(valueSet, null);
    return allCodes;
  }
  
  /**
   * Extract all codes out of a ValueSet, including imports and includes.
   * @param valueSet The value set to process.
   * @param filterValue An optional filter. Includes the filterValue and all
   * children (if the ValueSet is hierarchical). If null, all values will be included.
   * @return List of codes, possibly empty.
   */
  private List<DefinedCode> extractCodesFromValueSet(ValueSet valueSet, String filterValue) {
    if (valueSet != null) {
      if (valueSet.hasCodeSystem()) {
        for (ConceptDefinitionComponent c : valueSet.getCodeSystem().getConcept()) {
          processCode(c, valueSet.getCodeSystem().getSystem(), null, filterValue); 
        }
      }
      if (valueSet.hasCompose()) {
        // ------------------------------ INCLUDE VALUESET ------------------------------ //
        for (ConceptSetComponent cc : valueSet.getCompose().getInclude()) {
          // ------------------------------ INCLUDE CONCEPTS ------------------------------ //
          for (ConceptReferenceComponent c : cc.getConcept()) {
            if(filterValue==null) {
              processCode(c, cc.getSystem());
            } else {
              processCode(c, cc.getSystem());              
            }
          }
          if(cc.getConcept()==null || cc.getConcept().size()==0) {
            // ------------------------------ NO CONCEPTS, INCLUDE SYSTEM ------------------------------ //
            ValueSet vs = findValueSet(cc.getSystem());
            if(vs!=null) {
              extractCodesFromValueSet(vs,null);
            } else {
              // System.out.println("Cannot include undefined valueset '" + cc.getSystem() + "' to " + valueSet.getName());
              undefinedValueSets.add(cc.getSystem());
            }
          }
          
          // ------------------------------ FILTER VALUESET ------------------------------ //
          for(ConceptSetFilterComponent cfc : cc.getFilter()) {
            FilterOperator op = cfc.getOp();
            String systemUri = cc.getSystem();
            if(cfc.getProperty().equals("concept") && op==FilterOperator.ISA) {
              ValueSet vs = findValueSet(systemUri);
              if(vs!=null) {
                // filter codes
                extractCodesFromValueSet(vs,cfc.getValue());
              } else {
                // System.out.println("Cannot filter-in undefined valueset '" + cc.getSystem() + "' to " + valueSet.getName());
                undefinedValueSets.add(cc.getSystem());
              }
            } else {
              System.out.println("Ignoring " + valueSet.getName() + " includes filter: " + cfc.getProperty() + " " + op.name() + " " + cfc.getValue() + " from " + systemUri);
            }
                        
          }            
        }
        // ------------------------------ IMPORT VALUESET ------------------------------ //
        for (UriType ut : valueSet.getCompose().getImport()) {
          ValueSet vs = findValueSet(ut.getValue());
          if(vs!=null) {
            extractCodesFromValueSet(vs,null);
          } else {
            // System.out.println("Cannot import undefined valueset '" + ut.getValue() + "' to " + valueSet.getName());
            undefinedValueSets.add(ut.getValue());
          }
        }
      }
      if(allCodes.size()==0) {
        emptyValueSets.add(valueSet.getName() );
      }
    }   
    return allCodes;
  }
  
  /**
   * Find a value set by name or URI.
   * @param name The name or URI of the value set.
   * @return The value set, if it exists locally in any capacity. Otherwise, null.
   */
  public ValueSet findValueSet(String name) {
    ValueSet vs = findValueSet(name, defns.getValuesets());
    if(vs==null) vs = findValueSet(name, defns.getBoundValueSets());
    if(vs==null) vs = findValueSet(name, defns.getCodeSystems());
    if(vs==null) vs = findValueSet(name, defns.getExtraValuesets());
    return vs;
  }
  
  private ValueSet findValueSet(String name, Map<String,ValueSet> collection)
  {
    ValueSet vs = collection.get(name);
    
    if(vs==null) {
      for(String vsname : defns.getValuesets().keySet()) {
        ValueSet t = findValueSet(vsname);
        if(t.getCodeSystem()!=null && t.getCodeSystem().getSystem()==name) {
          vs = t;
        }
      }
    }
    return vs;
  }

  private void processCode(ConceptReferenceComponent c, String system) {
    DefinedCode code = new DefinedCode();
    code.setCode(c.getCode());
    code.setDisplay(c.getDisplay());
    code.setSystem(system);
    allCodes.add(code);
  }

  private void processCode(ConceptDefinitionComponent c, String system, String parent, String filterValue) {
    DefinedCode code = new DefinedCode();
    code.setCode(c.getCode());
    code.setDisplay(c.getDisplay());
    code.setComment(ToolingExtensions.getComment(c));
    code.setDefinition(c.getDefinition());
    code.setParent(parent);
    code.setSystem(system);
    if(filterValue==null) {
      allCodes.add(code);
      for (ConceptDefinitionComponent cc : c.getConcept())
        processCode(cc, system, c.getCode(), null);      
    } else if(filterValue.equals(c.getCode())) {
      // System.out.println("Filtering in " + c.getCode() + " and children.");      
      allCodes.add(code);
      for (ConceptDefinitionComponent cc : c.getConcept())
        processCode(cc, system, c.getCode(), null);     
    } else {
      for (ConceptDefinitionComponent cc : c.getConcept())
        processCode(cc, system, c.getCode(), filterValue);           
    }
  }
}
