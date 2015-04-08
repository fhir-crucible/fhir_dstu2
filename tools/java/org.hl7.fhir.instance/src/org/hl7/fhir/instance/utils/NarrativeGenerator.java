package org.hl7.fhir.instance.utils;


/*
Copyright (c) 2011+, HL7, Inc
  All rights reserved.
  
  Redistribution and use in source and binary forms, with or without modification, 
  are permitted provided that the following conditions are met:
  
   * Redistributions of source code must retain the above copyright notice, this 
     list of conditions and the following disclaimer.
   * Redistributions in binary form must reproduce the above copyright notice, 
     this list of conditions and the following disclaimer in the documentation 
     and/or other materials provided with the distribution.
   * Neither the name of HL7 nor the names of its contributors may be used to 
     endorse or promote products derived from this software without specific 
     prior written permission.
  
  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND 
  ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED 
  WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. 
  IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, 
  INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT 
  NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR 
  PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, 
  WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) 
  ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE 
  POSSIBILITY OF SUCH DAMAGE.
  
*/

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;
import org.hl7.fhir.instance.model.Address;
import org.hl7.fhir.instance.model.Attachment;
import org.hl7.fhir.instance.model.Base;
import org.hl7.fhir.instance.model.Base64BinaryType;
import org.hl7.fhir.instance.model.BooleanType;
import org.hl7.fhir.instance.model.Bundle;
import org.hl7.fhir.instance.model.CodeType;
import org.hl7.fhir.instance.model.CodeableConcept;
import org.hl7.fhir.instance.model.Coding;
import org.hl7.fhir.instance.model.Composition;
import org.hl7.fhir.instance.model.Composition.SectionComponent;
import org.hl7.fhir.instance.model.ConceptMap;
import org.hl7.fhir.instance.model.ConceptMap.ConceptMapContactComponent;
import org.hl7.fhir.instance.model.ConceptMap.ConceptMapElementComponent;
import org.hl7.fhir.instance.model.ConceptMap.ConceptMapElementMapComponent;
import org.hl7.fhir.instance.model.ConceptMap.OtherElementComponent;
import org.hl7.fhir.instance.model.Conformance;
import org.hl7.fhir.instance.model.Conformance.ConformanceRestComponent;
import org.hl7.fhir.instance.model.Conformance.ConformanceRestResourceComponent;
import org.hl7.fhir.instance.model.Conformance.ResourceInteractionComponent;
import org.hl7.fhir.instance.model.Conformance.SystemInteractionComponent;
import org.hl7.fhir.instance.model.Conformance.SystemRestfulInteraction;
import org.hl7.fhir.instance.model.Conformance.TypeRestfulInteraction;
import org.hl7.fhir.instance.model.ContactPoint;
import org.hl7.fhir.instance.model.ContactPoint.ContactPointSystem;
import org.hl7.fhir.instance.model.DateTimeType;
import org.hl7.fhir.instance.model.DomainResource;
import org.hl7.fhir.instance.model.Duration;
import org.hl7.fhir.instance.model.ElementDefinition;
import org.hl7.fhir.instance.model.Enumeration;
import org.hl7.fhir.instance.model.Extension;
import org.hl7.fhir.instance.model.ExtensionHelper;
import org.hl7.fhir.instance.model.HumanName;
import org.hl7.fhir.instance.model.HumanName.NameUse;
import org.hl7.fhir.instance.model.IdType;
import org.hl7.fhir.instance.model.Identifier;
import org.hl7.fhir.instance.model.InstantType;
import org.hl7.fhir.instance.model.Narrative;
import org.hl7.fhir.instance.model.Narrative.NarrativeStatus;
import org.hl7.fhir.instance.model.OperationDefinition;
import org.hl7.fhir.instance.model.OperationDefinition.OperationDefinitionParameterComponent;
import org.hl7.fhir.instance.model.OperationDefinition.OperationDefinitionParameterPartComponent;
import org.hl7.fhir.instance.model.OperationOutcome;
import org.hl7.fhir.instance.model.OperationOutcome.IssueSeverity;
import org.hl7.fhir.instance.model.OperationOutcome.OperationOutcomeIssueComponent;
import org.hl7.fhir.instance.model.Period;
import org.hl7.fhir.instance.model.PrimitiveType;
import org.hl7.fhir.instance.model.Range;
import org.hl7.fhir.instance.model.StructureDefinition;
import org.hl7.fhir.instance.model.Property;
import org.hl7.fhir.instance.model.Quantity;
import org.hl7.fhir.instance.model.Ratio;
import org.hl7.fhir.instance.model.Reference;
import org.hl7.fhir.instance.model.Resource;
import org.hl7.fhir.instance.model.StringType;
import org.hl7.fhir.instance.model.Timing;
import org.hl7.fhir.instance.model.Timing.EventTiming;
import org.hl7.fhir.instance.model.Timing.TimingRepeatComponent;
import org.hl7.fhir.instance.model.Timing.UnitsOfTime;
import org.hl7.fhir.instance.model.UriType;
import org.hl7.fhir.instance.model.ValueSet;
import org.hl7.fhir.instance.model.ValueSet.ConceptDefinitionComponent;
import org.hl7.fhir.instance.model.ValueSet.ConceptDefinitionDesignationComponent;
import org.hl7.fhir.instance.model.ValueSet.ConceptReferenceComponent;
import org.hl7.fhir.instance.model.ValueSet.ConceptSetComponent;
import org.hl7.fhir.instance.model.ValueSet.ConceptSetFilterComponent;
import org.hl7.fhir.instance.model.ValueSet.FilterOperator;
import org.hl7.fhir.instance.model.ValueSet.ValueSetExpansionContainsComponent;
import org.hl7.fhir.utilities.CommaSeparatedStringBuilder;
import org.hl7.fhir.utilities.Utilities;
import org.hl7.fhir.utilities.xhtml.NodeType;
import org.hl7.fhir.utilities.xhtml.XhtmlNode;
import org.hl7.fhir.utilities.xhtml.XhtmlParser;

import com.github.rjeschke.txtmark.Processor;


public class NarrativeGenerator implements INarrativeGenerator {

  public class ResourceWithReference {

    private String reference;
    private Resource resource;

    public ResourceWithReference(String reference, Resource resource) {
      this.reference = reference;
      this.resource = resource;
    }

    public String getReference() {
      return reference;
    }

    public Resource getResource() {
      return resource;
    }
  }

  private String prefix;
  private WorkerContext context;
  private IWorkerContext ctxt;
  
  
  public NarrativeGenerator(String prefix, WorkerContext context) {
    super();
    this.prefix = prefix;
    this.context = context;
    ctxt = null;
  }

  public void generate(DomainResource r) throws Exception {
    if (r instanceof ConceptMap) {
      generate((ConceptMap) r); // Maintainer = Grahame
    } else if (r instanceof ValueSet) {
      generate((ValueSet) r); // Maintainer = Grahame
    } else if (r instanceof OperationOutcome) {
      generate((OperationOutcome) r); // Maintainer = Grahame
    } else if (r instanceof Conformance) {
      generate((Conformance) r);   // Maintainer = Grahame
    } else if (r instanceof OperationDefinition) {
      generate((OperationDefinition) r);   // Maintainer = Grahame
    } else if (context.getProfiles().containsKey(r.getResourceType().toString())) {
      StructureDefinition p = context.getProfiles().get(r.getResourceType().toString());
      generateByProfile(r, p /* context.getProfiles().get(r.getResourceType().toString()) */, true); // todo: make this manageable externally 
    } else if (context.getProfiles().containsKey("http://hl7.org/fhir/profile/"+r.getResourceType().toString().toLowerCase())) {
      generateByProfile(r, context.getProfiles().get("http://hl7.org/fhir/profile/"+r.getResourceType().toString().toLowerCase()), true); // todo: make this manageable externally 
    }
  }
  
  private void generateByProfile(DomainResource r, StructureDefinition profile, boolean showCodeDetails) throws Exception {  
    XhtmlNode x = new XhtmlNode(NodeType.Element, "div");
    x.addTag("p").addTag("b").addText("Generated Narrative"+(showCodeDetails ? " with Details" : ""));
    try {
      generateByProfile(r, profile, r, profile.getSnapshot().getElement(), profile.getSnapshot().getElement().get(0), getChildrenForPath(profile.getSnapshot().getElement(), r.getResourceType().toString()), x, r.getResourceType().toString(), showCodeDetails);
    } catch (Exception e) {
      e.printStackTrace();
      x.addTag("p").addTag("b").setAttribute("style", "color: maroon").addText("Exception generating Narrative: "+e.getMessage());
    }
    inject(r, x,  NarrativeStatus.GENERATED);
  }

  private void generateByProfile(Resource res, StructureDefinition profile, Base e, List<ElementDefinition> allElements, ElementDefinition defn, List<ElementDefinition> children,  XhtmlNode x, String path, boolean showCodeDetails) throws Exception {
    if (children.isEmpty()) {
      renderLeaf(res, e, defn, x, false, showCodeDetails, readDisplayHints(defn));
    } else {
      for (Property p : splitExtensions(profile, e.children())) {
        if (p.hasValues()) {
          ElementDefinition child = getElementDefinition(children, path+"."+p.getName(), p);
          if (child != null) {
            Map<String, String> displayHints = readDisplayHints(child);
            if (!exemptFromRendering(child)) {
              List<ElementDefinition> grandChildren = getChildrenForPath(allElements, path+"."+p.getName());
              if (p.getValues().size() > 0 && child != null) {
                if (isPrimitive(child)) {
                  XhtmlNode para = x.addTag("p");
                  String name = p.getName();
                  if (name.endsWith("[x]"))
                    name = name.substring(0, name.length() - 3);
                  if (showCodeDetails || !isDefaultValue(displayHints, p.getValues())) {
                    para.addTag("b").addText(name);
                    para.addText(": ");
                    if (renderAsList(child) && p.getValues().size() > 1) {
                      XhtmlNode list = x.addTag("ul");
                      for (Base v : p.getValues()) 
                        renderLeaf(res, v, child, list.addTag("li"), false, showCodeDetails, displayHints);
                    } else { 
                      boolean first = true;
                      for (Base v : p.getValues()) {
                        if (first)
                          first = false;
                        else
                          para.addText(", ");
                        renderLeaf(res, v, child, para, false, showCodeDetails, displayHints);
                      }
                    }
                  }
                } else if (canDoTable(path, p, grandChildren)) {
                  x.addTag("h3").addText(Utilities.capitalize(Utilities.camelCase(Utilities.pluralizeMe(p.getName()))));
                  XhtmlNode tbl = x.addTag("table").setAttribute("class", "grid");
                  addColumnHeadings(tbl.addTag("tr"), grandChildren);
                  for (Base v : p.getValues()) {
                    if (v != null) {
                      addColumnValues(res, tbl.addTag("tr"), grandChildren, v, showCodeDetails, displayHints);
                    }
                  }
                } else {
                  for (Base v : p.getValues()) {
                    if (v != null) {
                      XhtmlNode bq = x.addTag("blockquote");
                      bq.addTag("p").addTag("b").addText(p.getName());
                      generateByProfile(res, profile, v, allElements, child, grandChildren, bq, path+"."+p.getName(), showCodeDetails);
                    }
                  } 
                }
              }
            }
          }
        }
      }
    }
  }
  
  private List<Property> splitExtensions(StructureDefinition profile, List<Property> children) throws Exception {
    List<Property> results = new ArrayList<Property>();
    Map<String, Property> map = new HashMap<String, Property>();
    for (Property p : children)
      if (p.getName().equals("extension") || p.getName().equals("modifierExtension")) {
        // we're going to split these up, and create a property for each url 
        if (p.hasValues()) {
          for (Base v : p.getValues()) {
            Extension ex  = (Extension) v;
            String url = ex.getUrl();
            StructureDefinition ed = context.getExtensionStructure(profile, url);
            if (p.getName().equals("modifierExtension") && ed == null)
              throw new Exception("Unknown modifier extension "+url);
            Property pe = map.get(p.getName()+"["+url+"]");
            if (pe == null) {
              if (ed == null)
                pe = new Property(p.getName()+"["+url+"]", p.getTypeCode(), p.getDefinition(), p.getMinCardinality(), p.getMaxCardinality(), ex);
              else {
                ElementDefinition def = ed.getSnapshot().getElement().get(0);
                pe = new Property(p.getName()+"["+url+"]", "Extension", def.getDefinition(), def.getMin(), def.getMax().equals("*") ? Integer.MAX_VALUE : Integer.parseInt(def.getMax()), ex);
                pe.setStructure(ed);
              }
              results.add(pe);
            } else
              pe.getValues().add(ex);
          }
        }
      } else
        results.add(p);
    return results;
  }

  private boolean isDefaultValue(Map<String, String> displayHints, List<Base> list) {
    if (list.size() != 1)
      return false;
    if (list.get(0) instanceof PrimitiveType) 
      return isDefault(displayHints, (PrimitiveType) list.get(0));
    else
      return false;
  }

  private boolean isDefault(Map<String, String> displayHints, PrimitiveType primitiveType) {
    String v = primitiveType.asStringValue();
    if (!Utilities.noString(v) && displayHints.containsKey("default") && v.equals(displayHints.get("default")))
        return true;
    return false;
  }

  private boolean exemptFromRendering(ElementDefinition child) {
    if (child == null)
      return false;
    if ("Composition.subject".equals(child.getPath()))
      return true;
    if ("Composition.section".equals(child.getPath()))
      return true;
    return false;
  }

  private boolean renderAsList(ElementDefinition child) {
    if (child.getType().size() == 1) {
      String t = child.getType().get(0).getCode();
      if (t.equals("Address") || t.equals("Reference"))
        return true;
    }
    return false;
  }

  private void addColumnHeadings(XhtmlNode tr, List<ElementDefinition> grandChildren) {
    for (ElementDefinition e : grandChildren) 
      tr.addTag("td").addTag("b").addText(Utilities.capitalize(tail(e.getPath())));
  }

  private void addColumnValues(Resource res, XhtmlNode tr, List<ElementDefinition> grandChildren, Base v, boolean showCodeDetails, Map<String, String> displayHints) throws Exception {
    for (ElementDefinition e : grandChildren) {
      Property p = v.getChildByName(e.getPath().substring(e.getPath().lastIndexOf(".")+1));
      if (p == null || p.getValues().size() == 0 || p.getValues().get(0) == null)
        tr.addTag("td").addText(" ");
      else
        renderLeaf(res, p.getValues().get(0), e, tr.addTag("td"), false, showCodeDetails, displayHints);
    }
  }

  private String tail(String path) {
    return path.substring(path.lastIndexOf(".")+1);
  }

  private boolean canDoTable(String path, Property p, List<ElementDefinition> grandChildren) {
    for (ElementDefinition e : grandChildren) {
      List<Property> values = getValues(path, p, e);
      if (values.size() > 1 || !isPrimitive(e) || !canCollapse(e))
        return false;
    }
    return true;
  }

  private List<Property> getValues(String path, Property p, ElementDefinition e) {
    List<Property> res = new ArrayList<Property>();
    for (Base v : p.getValues()) {
      for (Property g : v.children()) {
        if ((path+"."+p.getName()+"."+g.getName()).equals(e.getPath()))
          res.add(p);
      }
    }
    return res;
  }

  private boolean canCollapse(ElementDefinition e) {
    // we can collapse any data type
    return !e.getType().isEmpty();
  }

  private boolean isPrimitive(ElementDefinition e) {
    //we can tell if e is a primitive because it has types
    return !e.getType().isEmpty();
  }
  
  private ElementDefinition getElementDefinition(List<ElementDefinition> elements, String path, Property p) {
    for (ElementDefinition element : elements)
      if (element.getPath().equals(path))
        return element;     
    if (path.endsWith("\"]") && p.getStructure() != null)
      return p.getStructure().getSnapshot().getElement().get(0);
    return null;
  }

  private void renderLeaf(Resource res, Base e, ElementDefinition defn, XhtmlNode x, boolean title, boolean showCodeDetails, Map<String, String> displayHints) throws Exception {
    if (e == null)
      return;
   
    if (e instanceof StringType)
      x.addText(((StringType) e).getValue());
    else if (e instanceof CodeType)
      x.addText(((CodeType) e).getValue());
    else if (e instanceof IdType)
      x.addText(((IdType) e).getValue());
    else if (e instanceof Extension)
      x.addText("Extensions: Todo");
    else if (e instanceof InstantType)
      x.addText(((InstantType) e).toHumanDisplay());
    else if (e instanceof DateTimeType)
      x.addText(((DateTimeType) e).toHumanDisplay());
    else if (e instanceof Base64BinaryType)
      x.addText(new Base64().encodeAsString(((Base64BinaryType) e).getValue()));
    else if (e instanceof org.hl7.fhir.instance.model.DateType)
      x.addText(((org.hl7.fhir.instance.model.DateType) e).toHumanDisplay());
    else if (e instanceof Enumeration)
      x.addText(((Enumeration<?>) e).getValue().toString()); // todo: look up a display name if there is one
    else if (e instanceof BooleanType)
      x.addText(((BooleanType) e).getValue().toString());
    else if (e instanceof CodeableConcept) {
      renderCodeableConcept((CodeableConcept) e, x, showCodeDetails); 
    } else if (e instanceof Coding) {
      renderCoding((Coding) e, x, showCodeDetails);
    } else if (e instanceof Identifier) {
      renderIdentifier((Identifier) e, x);
    } else if (e instanceof org.hl7.fhir.instance.model.IntegerType) {
      x.addText(Integer.toString(((org.hl7.fhir.instance.model.IntegerType) e).getValue()));
    } else if (e instanceof org.hl7.fhir.instance.model.DecimalType) {
      x.addText(((org.hl7.fhir.instance.model.DecimalType) e).getValue().toString());
    } else if (e instanceof HumanName) {
      renderHumanName((HumanName) e, x);
    } else if (e instanceof Address) {
      renderAddress((Address) e, x);
    } else if (e instanceof ContactPoint) {
      renderContactPoint((ContactPoint) e, x);
    } else if (e instanceof UriType) {
      renderUri((UriType) e, x);
    } else if (e instanceof Timing) {
      renderTiming((Timing) e, x);
    } else if (e instanceof Range) {
      renderRange((Range) e, x);
    } else if (e instanceof Quantity || e instanceof Duration) {
      renderQuantity((Quantity) e, x, showCodeDetails);
    } else if (e instanceof Ratio) {
      renderQuantity(((Ratio) e).getNumerator(), x, showCodeDetails);
      x.addText("/");
      renderQuantity(((Ratio) e).getDenominator(), x, showCodeDetails);
    } else if (e instanceof Period) {
      Period p = (Period) e;
      x.addText(!p.hasStart() ? "??" : p.getStartElement().toHumanDisplay());
      x.addText(" --> ");
      x.addText(!p.hasEnd() ? "(ongoing)" : p.getEndElement().toHumanDisplay());
    } else if (e instanceof Reference) {
      Reference r = (Reference) e;
      XhtmlNode c = x;
      ResourceWithReference tr = null;
      if (r.hasReferenceElement()) {
        tr = resolveReference(res, r.getReference());
        if (!r.getReference().startsWith("#")) {
          if (tr != null && tr.getReference() != null)
            c = x.addTag("a").attribute("href", tr.getReference());
          else
            c = x.addTag("a").attribute("href", r.getReference());
        }
      }
      // what to display: if text is provided, then that. if the reference was resolved, then show the generated narrative
      if (r.hasDisplayElement()) {        
        c.addText(r.getDisplay());
        if (tr != null) {
          c.addText(". Generated Summary: ");
          generateResourceSummary(c, tr.getResource(), true, r.getReference().startsWith("#"));
        }
      } else if (tr != null) {
        generateResourceSummary(c, tr.getResource(), r.getReference().startsWith("#"), r.getReference().startsWith("#"));
      } else {
        c.addText(r.getReference());
      }
    } else if (e instanceof Resource) {
      return;
    } else if (e instanceof ElementDefinition) {
      x.addText("todo-bundle");
    } else if (!(e instanceof Attachment) && !(e instanceof Narrative))
      throw new Exception("type "+e.getClass().getName()+" not handled yet");      
  }

  private boolean displayLeaf(Resource res, Base e, ElementDefinition defn, XhtmlNode x, String name, boolean showCodeDetails) throws Exception {
    if (e == null)
      return false;
    Map<String, String> displayHints = readDisplayHints(defn);
    
    if (name.endsWith("[x]"))
      name = name.substring(0, name.length() - 3);
    
    if (!showCodeDetails && e instanceof PrimitiveType && isDefault(displayHints, ((PrimitiveType) e)))
        return false;
    
    if (e instanceof StringType) {
      x.addText(name+": "+((StringType) e).getValue());
      return true;
    } else if (e instanceof CodeType) {
      x.addText(name+": "+((CodeType) e).getValue());
      return true;
    } else if (e instanceof IdType) {
      x.addText(name+": "+((IdType) e).getValue());
      return true;
    } else if (e instanceof DateTimeType) {
      x.addText(name+": "+((DateTimeType) e).toHumanDisplay());
      return true;
    } else if (e instanceof InstantType) {
      x.addText(name+": "+((InstantType) e).toHumanDisplay());
      return true;
    } else if (e instanceof Extension) {
      x.addText("Extensions: todo");
      return true;
    } else if (e instanceof org.hl7.fhir.instance.model.DateType) {
      x.addText(name+": "+((org.hl7.fhir.instance.model.DateType) e).toHumanDisplay());
      return true;
    } else if (e instanceof Enumeration) {
      x.addText(((Enumeration<?>) e).getValue().toString()); // todo: look up a display name if there is one
      return true;
    } else if (e instanceof BooleanType) {
      if (((BooleanType) e).getValue()) {
        x.addText(name);
          return true;
      }
    } else if (e instanceof CodeableConcept) {
      renderCodeableConcept((CodeableConcept) e, x, showCodeDetails);
      return true;
    } else if (e instanceof Coding) {
      renderCoding((Coding) e, x, showCodeDetails);
      return true;
    } else if (e instanceof org.hl7.fhir.instance.model.IntegerType) {
      x.addText(Integer.toString(((org.hl7.fhir.instance.model.IntegerType) e).getValue()));
      return true;
    } else if (e instanceof org.hl7.fhir.instance.model.DecimalType) {
      x.addText(((org.hl7.fhir.instance.model.DecimalType) e).getValue().toString());
      return true;
    } else if (e instanceof Identifier) {
      renderIdentifier((Identifier) e, x);
      return true;
    } else if (e instanceof HumanName) {
      renderHumanName((HumanName) e, x);
      return true;
    } else if (e instanceof Address) {
      renderAddress((Address) e, x);
      return true;
    } else if (e instanceof ContactPoint) {
      renderContactPoint((ContactPoint) e, x);
      return true;
    } else if (e instanceof Timing) {
      renderTiming((Timing) e, x);
      return true;
    } else if (e instanceof Quantity || e instanceof Duration) {
      renderQuantity((Quantity) e, x, showCodeDetails);
      return true;
    } else if (e instanceof Ratio) {
      renderQuantity(((Ratio) e).getNumerator(), x, showCodeDetails);
      x.addText("/");
      renderQuantity(((Ratio) e).getDenominator(), x, showCodeDetails);
      return true;
    } else if (e instanceof Period) {
      Period p = (Period) e;
      x.addText(name+": ");
      x.addText(!p.hasStart() ? "??" : p.getStartElement().toHumanDisplay());
      x.addText(" --> ");
      x.addText(!p.hasEnd() ? "(ongoing)" : p.getEndElement().toHumanDisplay());
      return true;
    } else if (e instanceof Reference) {
      Reference r = (Reference) e;
      if (r.hasDisplayElement())        
        x.addText(r.getDisplay());
      else if (r.hasReferenceElement()) {
        ResourceWithReference tr = resolveReference(res, r.getReference());
        x.addText(tr == null ? r.getReference() : "????"); // getDisplayForReference(tr.getReference()));
      } else
        x.addText("??");
      return true;
    } else if (e instanceof Narrative) {
      return false;
    } else if (e instanceof Resource) {
      return false;
    } else if (!(e instanceof Attachment))
      throw new Exception("type "+e.getClass().getName()+" not handled yet");      
    return false;
  }


  private Map<String, String> readDisplayHints(ElementDefinition defn) throws Exception {
    Map<String, String> hints = new HashMap<String, String>();
    if (defn != null) {
      String displayHint = ToolingExtensions.getDisplayHint(defn);
      if (!Utilities.noString(displayHint)) {
        String[] list = displayHint.split(";");
        for (String item : list) {
          String[] parts = item.split(":");
          if (parts.length != 2)
            throw new Exception("error reading display hint: '"+displayHint+"'");
          hints.put(parts[0].trim(), parts[1].trim());
        }
      }
    }
    return hints;
  }

  private String displayPeriod(Period p) {
    String s = !p.hasStart() ? "??" : p.getStartElement().toHumanDisplay();
    s = s + " --> ";
    return s + (!p.hasEnd() ? "(ongoing)" : p.getEndElement().toHumanDisplay());
  }

  private void generateResourceSummary(XhtmlNode x, Resource res, boolean textAlready, boolean showCodeDetails) throws Exception {
  	if (!(res instanceof DomainResource))
  		throw new Exception("Not handled yet"); // todo-bundle
  	DomainResource dres = (DomainResource) res;
    if (!textAlready) {
      if (dres.hasText() && dres.getText().hasDiv()) {
        XhtmlNode div = dres.getText().getDiv();
        if (div.allChildrenAreText())
          x.getChildNodes().addAll(div.getChildNodes());
        if (div.getChildNodes().size() == 1 && div.getChildNodes().get(0).allChildrenAreText())
          x.getChildNodes().addAll(div.getChildNodes().get(0).getChildNodes());
      }
      x.addText("Generated Summary: ");
    }
    String path = dres.getResourceType().toString();
    StructureDefinition profile = context.getProfiles().get(path);
    if (profile == null)
      x.addText("unknown resource " +path);
    else {
      boolean firstElement = true;
      boolean last = false;
      for (Property p : dres.children()) {
        ElementDefinition child = getElementDefinition(profile.getSnapshot().getElement(), path+"."+p.getName(), p);
        if (p.getValues().size() > 0 && p.getValues().get(0) != null && child != null && isPrimitive(child) && includeInSummary(child)) {
          if (firstElement)
            firstElement = false;
          else if (last)
            x.addText("; ");
          boolean first = true;
          last = false;
          for (Base v : p.getValues()) {
            if (first)
              first = false;
            else if (last)
              x.addText(", ");
            last = displayLeaf(dres, v, child, x, p.getName(), showCodeDetails) || last;
          }
        }
      }
    }
  }


  private boolean includeInSummary(ElementDefinition child) {
    if (child.getIsModifier())
      return true;
    if (child.getMustSupport())
      return true;
    if (child.getType().size() == 1) {
      String t = child.getType().get(0).getCode();
      if (t.equals("Address") || t.equals("Contact") || t.equals("Reference") || t.equals("Uri"))
        return false;
    }
    return true;
  }

  private ResourceWithReference resolveReference(Resource res, String url) {
    if (url == null)
      return null;
    if (url.startsWith("#") && res instanceof DomainResource) {
      for (Resource r : ((DomainResource) res).getContained()) {
        if (r.getId().equals(url.substring(1)))
          return new ResourceWithReference(null, r);
      }
      return null;
    }
    if (!context.hasClient())
      return null;
    
    Resource ae = context.getClient().read(null, url);
    if (ae == null)
      return null;
    else
      return new ResourceWithReference(url, ae);
  }

  private void renderCodeableConcept(CodeableConcept cc, XhtmlNode x, boolean showCodeDetails) {
    String s = cc.getText();
    if (Utilities.noString(s)) {
      for (Coding c : cc.getCoding()) {
        if (c.hasDisplayElement()) {
          s = c.getDisplay();
          break;
        }
      }
    }
    if (Utilities.noString(s)) {
      // still? ok, let's try looking it up
      for (Coding c : cc.getCoding()) {
        if (c.hasCodeElement() && c.hasSystemElement()) {
          s = lookupCode(c.getSystem(), c.getCode());
          if (!Utilities.noString(s)) 
            break;
        }
      }
    }
      
    if (Utilities.noString(s)) {
      if (cc.getCoding().isEmpty()) 
        s = "";
      else
        s = cc.getCoding().get(0).getCode();
    }

    if (showCodeDetails) {
      x.addText(s+" ");
      XhtmlNode sp = x.addTag("span");
      sp.setAttribute("style", "background: LightGoldenRodYellow ");
      sp.addText("(Details ");
      boolean first = true;
      for (Coding c : cc.getCoding()) {
        if (first) {
          sp.addText(": ");
          first = false;
        } else
          sp.addText("; ");
        sp.addText("{"+describeSystem(c.getSystem())+" code '"+c.getCode()+"' = '"+lookupCode(c.getSystem(), c.getCode())+(c.hasDisplay() ? "', given as '"+c.getDisplay()+"'}" : ""));
      }
      sp.addText(")");
    } else {

    CommaSeparatedStringBuilder b = new CommaSeparatedStringBuilder();
    for (Coding c : cc.getCoding()) {
      if (c.hasCodeElement() && c.hasSystemElement()) {
        b.append("{"+c.getSystem()+" "+c.getCode()+"}");
      }
    }
    
    x.addTag("span").setAttribute("title", "Codes: "+b.toString()).addText(s);
    }
  }

  private void renderCoding(Coding c, XhtmlNode x, boolean showCodeDetails) {
    String s = "";
    if (c.hasDisplayElement()) 
      s = c.getDisplay();
    if (Utilities.noString(s)) 
      s = lookupCode(c.getSystem(), c.getCode());
      
    if (Utilities.noString(s)) 
      s = c.getCode();

    if (showCodeDetails) {
      x.addText(s+" (Details: "+describeSystem(c.getSystem())+" code "+c.getCode()+" = '"+lookupCode(c.getSystem(), c.getCode())+"', stated as '"+c.getDisplay()+"')");
    } else
      x.addTag("span").setAttribute("title", "{"+c.getSystem()+" "+c.getCode()+"}").addText(s);   
  }

  private String describeSystem(String system) {
    if (system == null)
      return "[not stated]";
    if (system.equals("http://loinc.org"))
      return "LOINC";
    if (system.startsWith("http://snomed.info"))
      return "SNOMED CT";
    if (system.equals("http://www.nlm.nih.gov/research/umls/rxnorm"))
      return "RxNorm"; 
    if (system.equals("http://hl7.org/fhir/sid/icd-9"))
      return "ICD-9";
    
    return system;
  }

  private String lookupCode(String system, String code) {
    ConceptDefinitionComponent t;
    if (context.getCodeSystems() == null && context.getTerminologyServices() == null)
    	return code;
    else if (context.getCodeSystems() != null && context.getCodeSystems().containsKey(system)) 
      t = findCode(code, context.getCodeSystems().get(system).getDefine().getConcept());
    else 
      t = context.getTerminologyServices().getCodeDefinition(system, code);
      
    if (t != null && t.hasDisplayElement())
        return t.getDisplay();
    else 
      return code;
    
  }

  private ConceptDefinitionComponent findCode(String code, List<ConceptDefinitionComponent> list) {
    for (ConceptDefinitionComponent t : list) {
      if (code.equals(t.getCode()))
        return t;
      ConceptDefinitionComponent c = findCode(code, t.getConcept());
      if (c != null)
        return c;
    }
    return null;
  }

  private String displayCodeableConcept(CodeableConcept cc) {
    String s = cc.getText();
    if (Utilities.noString(s)) {
      for (Coding c : cc.getCoding()) {
        if (c.hasDisplayElement()) {
          s = c.getDisplay();
          break;
        }
      }
    }
    if (Utilities.noString(s)) {
      // still? ok, let's try looking it up
      for (Coding c : cc.getCoding()) {
        if (c.hasCode() && c.hasSystem()) {
          s = lookupCode(c.getSystem(), c.getCode());
          if (!Utilities.noString(s)) 
            break;
        }
      }
    }
      
    if (Utilities.noString(s)) {
      if (cc.getCoding().isEmpty()) 
        s = "";
      else
        s = cc.getCoding().get(0).getCode();
    }
    return s;
  }

  private void renderIdentifier(Identifier ii, XhtmlNode x) {
    x.addText(displayIdentifier(ii));
  }
  
  private void renderTiming(Timing s, XhtmlNode x) {
    x.addText(displayTiming(s));
  }
  
  private void renderQuantity(Quantity q, XhtmlNode x, boolean showCodeDetails) {
    if (q.hasComparator())
      x.addText(q.getComparator().toCode());
    x.addText(q.getValue().toString());
    if (q.hasUnits())
      x.addText(" "+q.getUnits());
    else if (q.hasCode())
      x.addText(" "+q.getCode());
    if (showCodeDetails && q.hasCode()) {
      XhtmlNode sp = x.addTag("span");
      sp.setAttribute("style", "background: LightGoldenRodYellow ");
      sp.addText(" (Details: "+describeSystem(q.getSystem())+" code "+q.getCode()+" = '"+lookupCode(q.getSystem(), q.getCode())+"')"); 
    }
  }
  
  private void renderRange(Range q, XhtmlNode x) {
    if (q.hasLow())
      x.addText(q.getLow().getValue().toString());
    else 
      x.addText("?");
    x.addText("-");
    if (q.hasHigh())
      x.addText(q.getHigh().getValue().toString());
    else 
      x.addText("?");
    if (q.getLow().hasUnits())
      x.addText(" "+q.getLow().getUnits());
  }
  
  private void renderHumanName(HumanName name, XhtmlNode x) {
    x.addText(displayHumanName(name));
  }
  
  private void renderAddress(Address address, XhtmlNode x) {
    x.addText(displayAddress(address));
  }
  
  private void renderContactPoint(ContactPoint contact, XhtmlNode x) {
    x.addText(displayContactPoint(contact));
  }
  
  private void renderUri(UriType uri, XhtmlNode x) {
    x.addTag("a").setAttribute("href", uri.getValue()).addText(uri.getValue());
  }
  
  
  private String displayTiming(Timing s) {
    CommaSeparatedStringBuilder b = new CommaSeparatedStringBuilder();
    if (s.getEvent().size() > 1 || (!s.hasRepeat() && !s.getEvent().isEmpty())) {
      CommaSeparatedStringBuilder c = new CommaSeparatedStringBuilder();
      for (DateTimeType p : s.getEvent()) {
        c.append(p.toHumanDisplay());
      }
      b.append("Events: "+ c.toString());
    }
    if (s.hasRepeat()) {
      TimingRepeatComponent rep = s.getRepeat();
      if (rep.hasBounds() && rep.getBounds().hasStart()) 
        b.append("Starting "+rep.getBounds().getStartElement().toHumanDisplay());
      if (rep.hasCount()) 
        b.append("Count "+Integer.toString(rep.getCount())+"x");
      if (rep.hasDuration()) 
        b.append("Duration "+rep.getDuration().toPlainString()+displayTimeUnits(rep.getPeriodUnits()));
      
      if (rep.hasWhen()) {
        String st = "";
        if (rep.hasPeriod()) {
          st = rep.getPeriod().toPlainString();
          if (rep.hasPeriodMax())
            st = st + "-"+rep.getPeriodMax().toPlainString();
          st = st + displayTimeUnits(rep.getPeriodUnits());
        }
        b.append("Do "+st+displayEventCode(rep.getWhen()));
      } else {
        String st = "";
        if (!rep.hasFrequency() || (!rep.hasFrequencyMax() && rep.getFrequency() == 1) ) 
          st = "Once";
        else {  
          st = Integer.toString(rep.getFrequency());
          if (rep.hasFrequencyMax())
            st = st + "-"+Integer.toString(rep.getFrequency());
        }
        st = st + " per "+rep.getPeriod().toPlainString();
        if (rep.hasPeriodMax())
          st = st + "-"+rep.getPeriodMax().toPlainString();
        st = st + displayTimeUnits(rep.getPeriodUnits());
        b.append("Do "+st);
      }
      if (rep.hasBounds() && rep.getBounds().hasEnd()) 
        b.append("Until "+rep.getBounds().getEndElement().toHumanDisplay());
    } 
    return b.toString();
  }
  
  private Object displayEventCode(EventTiming when) {
    switch (when) {
    case C: return "at meals";
    case CD: return "at lunch";
    case CM: return "at breakfast";
    case CV: return "at dinner";
    case AC: return "before meals";
    case ACD: return "before lunch";
    case ACM: return "before breakfast";
    case ACV: return "before dinner";
    case HS: return "before sleeping";
    case PC: return "after meals";
    case PCD: return "after lunch";
    case PCM: return "after breakfast";
    case PCV: return "after dinner";
    case WAKE: return "after waking";
    default: return "??";
    }
  }

  private String displayTimeUnits(UnitsOfTime units) {
    switch (units) {
    case A: return "years";
    case D: return "days";
    case H: return "hours";
    case MIN: return "minutes";
    case MO: return "months";
    case S: return "seconds";
    case WK: return "weeks";
    default: return "??";
    }
  }

  private String displayHumanName(HumanName name) {
    StringBuilder s = new StringBuilder();
    if (name.hasText())
      s.append(name.getText());
    else {
      for (StringType p : name.getGiven()) { 
        s.append(p.getValue());
        s.append(" ");
      }
      for (StringType p : name.getFamily()) { 
        s.append(p.getValue());
        s.append(" ");
      }
    }
    if (name.hasUse() && name.getUse() != NameUse.USUAL)
      s.append("("+name.getUse().toString()+")");
    return s.toString();
  }

  private String displayAddress(Address address) {
    StringBuilder s = new StringBuilder();
    if (address.hasText())
      s.append(address.getText());
    else {
      for (StringType p : address.getLine()) { 
        s.append(p.getValue());
        s.append(" ");
      }
      if (address.hasCity()) { 
        s.append(address.getCity());
        s.append(" ");
      }
      if (address.hasState()) { 
        s.append(address.getState());
        s.append(" ");
      }
      
      if (address.hasPostalCode()) { 
        s.append(address.getPostalCode());
        s.append(" ");
      }
      
      if (address.hasCountry()) { 
        s.append(address.getCountry());
        s.append(" ");
      }
    }
    if (address.hasUse())
      s.append("("+address.getUse().toString()+")");
    return s.toString();
  }

  private String displayContactPoint(ContactPoint contact) {
    StringBuilder s = new StringBuilder();
    s.append(describeSystem(contact.getSystem()));
    if (Utilities.noString(contact.getValue()))
      s.append("-unknown-");
    else
      s.append(contact.getValue());
    if (contact.hasUse())
      s.append("("+contact.getUse().toString()+")");
    return s.toString();
  }

  private Object describeSystem(ContactPointSystem system) {
    if (system == null)
      return "";
    switch (system) {
    case PHONE: return "ph: ";
    case FAX: return "fax: ";
    default: 
      return "";
    }    
  }

  private String displayIdentifier(Identifier ii) {
    String s = Utilities.noString(ii.getValue()) ? "??" : ii.getValue();
    
    if (ii.hasType()) {
    	if (ii.getType().hasText())
    		s = ii.getType().getText()+" = "+s;
    	else if (ii.getType().hasCoding() && ii.getType().getCoding().get(0).hasDisplay())
    		s = ii.getType().getCoding().get(0).getDisplay()+" = "+s;
    	else if (ii.getType().hasCoding() && ii.getType().getCoding().get(0).hasCode())
    		s = ii.getType().getCoding().get(0).getCode()+" = "+s;
    }

    if (ii.hasUse())
      s = s + " ("+ii.getUse().toString()+")";
    return s;
  }

  private List<ElementDefinition> getChildrenForPath(List<ElementDefinition> elements, String path) throws Exception {
    // do we need to do a name reference substitution?
    for (ElementDefinition e : elements) {
      if (e.getPath().equals(path) && e.hasNameReference()) {
      	String name = e.getNameReference();
      	ElementDefinition t = null;
      	// now, resolve the name
        for (ElementDefinition e1 : elements) {
        	if (name.equals(e1.getName()))
        		t = e1;
        }
        if (t == null)
        	throw new Exception("Unable to resolve name reference "+name+" trying to resolve "+path);
        path = t.getPath();
        break;
      }
    }
    
    List<ElementDefinition> results = new ArrayList<ElementDefinition>();
    for (ElementDefinition e : elements) {
      if (e.getPath().startsWith(path+".") && !e.getPath().substring(path.length()+1).contains("."))
        results.add(e);
    }
    return results;
  }


  public void generate(ConceptMap cm) throws Exception {
    XhtmlNode x = new XhtmlNode(NodeType.Element, "div");
    x.addTag("h2").addText(cm.getName()+" ("+cm.getUrl()+")");

    XhtmlNode p = x.addTag("p");
    p.addText("Mapping from ");
    AddVsRef(((Reference) cm.getSource()).getReference(), p);
    p.addText(" to ");
    AddVsRef(((Reference) cm.getTarget()).getReference(), p);
    
    p = x.addTag("p");
    if (cm.getExperimental())
      p.addText(Utilities.capitalize(cm.getStatus().toString())+" (not intended for production usage). ");
    else
      p.addText(Utilities.capitalize(cm.getStatus().toString())+". ");
    p.addText("Published on "+cm.getDateElement().toHumanDisplay()+" by "+cm.getPublisher());
    if (!cm.getContact().isEmpty()) {
      p.addText(" (");
      boolean firsti = true;
      for (ConceptMapContactComponent ci : cm.getContact()) {
        if (firsti) 
          firsti = false;
        else
          p.addText(", ");
        if (ci.hasName())
          p.addText(ci.getName()+": ");
        boolean first = true;
        for (ContactPoint c : ci.getTelecom()) {
          if (first) 
            first = false;
          else
            p.addText(", ");
          addTelecom(p, c);
        }
        p.addText("; ");
      }
      p.addText(")");
    }
    p.addText(". ");
    p.addText(cm.getCopyright());
    if (!Utilities.noString(cm.getDescription())) 
      x.addTag("p").addText(cm.getDescription());

    x.addTag("br");

    if (!cm.getElement().isEmpty()) {
      ConceptMapElementComponent cc = cm.getElement().get(0);
      String src = cc.getCodeSystem();
      boolean comments = false;
      boolean ok = cc.getMap().size() == 1;
      Map<String, HashSet<String>> sources = new HashMap<String, HashSet<String>>();
      sources.put("code", new HashSet<String>());
      Map<String, HashSet<String>> targets = new HashMap<String, HashSet<String>>();
      targets.put("code", new HashSet<String>());
      if (ok) {
        String dst = cc.getMap().get(0).getCodeSystem();
        for (ConceptMapElementComponent ccl : cm.getElement()) {
          ok = ok && src.equals(ccl.getCodeSystem()) && ccl.getMap().size() == 1 && dst.equals(ccl.getMap().get(0).getCodeSystem()) && ccl.getDependsOn().isEmpty() && ccl.getMap().get(0).getProduct().isEmpty();
          if (ccl.hasCodeSystem())
            sources.get("code").add(ccl.getCodeSystem());
          for (OtherElementComponent d : ccl.getDependsOn()) {
            if (!sources.containsKey(d.getElement()))
              sources.put(d.getElement(), new HashSet<String>());
            sources.get(d.getElement()).add(d.getCodeSystem());
          }
          for (ConceptMapElementMapComponent ccm : ccl.getMap()) {
            comments = comments || !Utilities.noString(ccm.getComments());
            if (ccm.hasCodeSystem())
              targets.get("code").add(ccm.getCodeSystem());
            for (OtherElementComponent d : ccm.getProduct()) {
              if (!targets.containsKey(d.getElement()))
                targets.put(d.getElement(), new HashSet<String>());
              targets.get(d.getElement()).add(d.getCodeSystem());
            }
            
          }
        }
      }
      
      String display;
      if (ok) {
        // simple 
        XhtmlNode tbl = x.addTag("table").setAttribute("class", "grid");
        XhtmlNode tr = tbl.addTag("tr");
        tr.addTag("td").addTag("b").addText("Source Code");
        tr.addTag("td").addTag("b").addText("Equivalence");
        tr.addTag("td").addTag("b").addText("Destination Code");
        if (comments)
          tr.addTag("td").addTag("b").addText("Comments");
        for (ConceptMapElementComponent ccl : cm.getElement()) {
          tr = tbl.addTag("tr");
          XhtmlNode td = tr.addTag("td");
          td.addText(ccl.getCode());
          display = getDisplayForConcept(ccl.getCodeSystem(), ccl.getCode());
          if (display != null)
            td.addText(" ("+display+")");
          ConceptMapElementMapComponent ccm = ccl.getMap().get(0); 
          tr.addTag("td").addText(!ccm.hasEquivalence() ? "" : ccm.getEquivalence().toCode());
          td = tr.addTag("td");
          td.addText(ccm.getCode());
          display = getDisplayForConcept(ccm.getCodeSystem(), ccm.getCode());
          if (display != null)
            td.addText(" ("+display+")");
          if (comments)
            tr.addTag("td").addText(ccm.getComments());
        }
      } else {
        XhtmlNode tbl = x.addTag("table").setAttribute("class", "grid");
        XhtmlNode tr = tbl.addTag("tr");
        XhtmlNode td;
        tr.addTag("td").setAttribute("colspan", Integer.toString(sources.size())).addTag("b").addText("Source Concept");
        tr.addTag("td").addTag("b").addText("Equivalence");
        tr.addTag("td").setAttribute("colspan", Integer.toString(targets.size())).addTag("b").addText("Destination Concept");
        if (comments)
          tr.addTag("td").addTag("b").addText("Comments");
        tr = tbl.addTag("tr");
        if (sources.get("code").size() == 1) 
          tr.addTag("td").addTag("b").addText("Code "+sources.get("code").toString()+"");
        else 
          tr.addTag("td").addTag("b").addText("Code");
        for (String s : sources.keySet()) {
          if (!s.equals("code")) {
            if (sources.get(s).size() == 1)
              tr.addTag("td").addTag("b").addText(getDescForConcept(s) +" "+sources.get(s).toString());
            else 
              tr.addTag("td").addTag("b").addText(getDescForConcept(s));
          }
        }
        tr.addTag("td");
        if (targets.get("code").size() == 1) 
          tr.addTag("td").addTag("b").addText("Code "+targets.get("code").toString());
        else 
          tr.addTag("td").addTag("b").addText("Code");
        for (String s : targets.keySet()) {
          if (!s.equals("code")) {
            if (targets.get(s).size() == 1)
              tr.addTag("td").addTag("b").addText(getDescForConcept(s) +" "+targets.get(s).toString()+"");
            else 
              tr.addTag("td").addTag("b").addText(getDescForConcept(s));
          }
        }
        if (comments)
          tr.addTag("td");
        
        for (ConceptMapElementComponent ccl : cm.getElement()) {
          tr = tbl.addTag("tr");
          td = tr.addTag("td");
          if (sources.get("code").size() == 1) 
            td.addText(ccl.getCode());
          else
            td.addText(ccl.getCodeSystem()+" / "+ccl.getCode());
          display = getDisplayForConcept(ccl.getCodeSystem(), ccl.getCode());
          if (display != null)
            td.addText(" ("+display+")");
          
          for (String s : sources.keySet()) {
            if (!s.equals("code")) { 
              td = tr.addTag("td");
              td.addText(getCode(ccl.getDependsOn(), s, sources.get(s).size() != 1));
              display = getDisplay(ccl.getDependsOn(), s);
              if (display != null)
                td.addText(" ("+display+")");
            }
          }
          ConceptMapElementMapComponent ccm = ccl.getMap().get(0); 
          tr.addTag("td").addText(ccm.getEquivalence().toString());
          td = tr.addTag("td");
          if (targets.get("code").size() == 1) 
            td.addText(ccm.getCode());
          else
            td.addText(ccm.getCodeSystem()+" / "+ccm.getCode());
          display = getDisplayForConcept(ccm.getCodeSystem(), ccm.getCode());
          if (display != null)
            td.addText(" ("+display+")");

          for (String s : targets.keySet()) {
            if (!s.equals("code")) { 
              td = tr.addTag("td");
              td.addText(getCode(ccm.getProduct(), s, targets.get(s).size() != 1));
              display = getDisplay(ccm.getProduct(), s);
              if (display != null)
                td.addText(" ("+display+")");
            }
          }
          if (comments)
            tr.addTag("td").addText(ccm.getComments());
        }
      }
    }
   
    inject(cm, x, NarrativeStatus.GENERATED);
  }
  
  
  
  private void inject(DomainResource r, XhtmlNode x, NarrativeStatus status) {
    if (!r.hasText() || !r.getText().hasDiv() || r.getText().getDiv().getChildNodes().isEmpty()) {
      r.setText(new Narrative());
      r.getText().setDiv(x);
      r.getText().setStatus(status);
    } else {
      XhtmlNode n = r.getText().getDiv();
      n.addTag("hr");
      n.getChildNodes().addAll(x.getChildNodes());
    }
  }

  private String getDisplay(List<OtherElementComponent> list, String s) {
    for (OtherElementComponent c : list) {
      if (s.equals(c.getElement()))
        return getDisplayForConcept(c.getCodeSystem(), c.getCode());
    }
    return null;
  }

  private String getDisplayForConcept(String system, String code) {
    if (code == null)
      return null;
    if (context.getCodeSystems().containsKey(system)) {
      ValueSet vs = context.getCodeSystems().get(system);
      return getDisplayForConcept(code, vs.getDefine().getConcept(), vs.getDefine().getCaseSensitive());
    } else if (context.getTerminologyServices() != null) {
      ConceptDefinitionComponent cl = context.getTerminologyServices().getCodeDefinition(system, code);
      return cl == null ? null : cl.getDisplay();
    } else
      return null;
  }

  private String getDisplayForConcept(String code, List<ConceptDefinitionComponent> concept, boolean cs) {
    for (ConceptDefinitionComponent t : concept) {
      if ((cs && code.equals(t.getCode()) || (!cs && code.equalsIgnoreCase(t.getCode()))))
          return t.getDisplay();
      String disp = getDisplayForConcept(code, t.getConcept(), cs);
      if (disp != null)
        return disp;
    }
    return null;
  }

  private String getDescForConcept(String s) {
    if (s.startsWith("http://hl7.org/fhir/v2/element/"))
        return "v2 "+s.substring("http://hl7.org/fhir/v2/element/".length()); 
    return s;
  }

  private String getCode(List<OtherElementComponent> list, String s, boolean withSystem) {
    for (OtherElementComponent c : list) {
      if (s.equals(c.getElement()))
        if (withSystem)
          return c.getCodeSystem()+" / "+c.getCode();
        else
          return c.getCode();
    }
    return null;
  }

  private void addTelecom(XhtmlNode p, ContactPoint c) {
    if (c.getSystem() == ContactPointSystem.PHONE) {
      p.addText("Phone: "+c.getValue());
    } else if (c.getSystem() == ContactPointSystem.FAX) {
      p.addText("Fax: "+c.getValue());
    } else if (c.getSystem() == ContactPointSystem.EMAIL) {
      p.addTag("a").setAttribute("href",  "mailto:"+c.getValue()).addText(c.getValue());
    } else if (c.getSystem() == ContactPointSystem.URL) {
      if (c.getValue().length() > 30)
        p.addTag("a").setAttribute("href", c.getValue()).addText(c.getValue().substring(0, 30)+"...");
      else
        p.addTag("a").setAttribute("href", c.getValue()).addText(c.getValue());
    }    
  }

  /**
   * This generate is optimised for the FHIR build process itself in as much as it 
   * generates hyperlinks in the narrative that are only going to be correct for
   * the purposes of the build. This is to be reviewed in the future.
   *  
   * @param vs
   * @param codeSystems
   * @throws Exception
   */
  public void generate(ValueSet vs) throws Exception {
    XhtmlNode x = new XhtmlNode(NodeType.Element, "div");
    if (vs.hasExpansion()) {
      if (!vs.hasDefine() && !vs.hasCompose())
        generateExpansion(x, vs);
      else
        throw new Exception("Error: should not encounter value set expansion at this point");
    }
    Integer count = countMembership(vs);
    if (count == null)
      x.addTag("p").addText("This value set does not contain a fixed number of concepts");
    else
      x.addTag("p").addText("This value set contains "+count.toString()+" concepts");
    
    boolean hasExtensions = false;
    if (vs.hasDefine())
      hasExtensions = generateDefinition(x, vs);
    if (vs.hasCompose()) 
      hasExtensions = generateComposition(x, vs) || hasExtensions;
    inject(vs, x, hasExtensions ? NarrativeStatus.EXTENSIONS :  NarrativeStatus.GENERATED);
  }

  private Integer countMembership(ValueSet vs) {
    int count = 0;
    if (vs.hasDefine())
      count = count + countConcepts(vs.getDefine().getConcept());
    if (vs.hasCompose()) {
      if (vs.getCompose().hasExclude()) {
        try {
          ValueSet vse = context.getTerminologyServices().expandVS(vs);
          count = 0;
          for (ValueSetExpansionContainsComponent exc : vse.getExpansion().getContains()) {
            count += conceptCount(exc);
          }
          return count;
        } catch (Exception e) {
          return null;
        }
      } 
      for (ConceptSetComponent inc : vs.getCompose().getInclude()) {
        if (inc.hasFilter())
          return null;
        if (!inc.hasConcept())
          return null;
        count = count + inc.getConcept().size();
      }
    }
    return count;
  }

  private int conceptCount(ValueSetExpansionContainsComponent exc) {
    int count = 0;
    if (!exc.getAbstract())
      count++;
    for (ValueSetExpansionContainsComponent c : exc.getContains()) 
      count += conceptCount(c);
    return count;
  }

  private int countConcepts(List<ConceptDefinitionComponent> list) {
    int count = list.size();
    for (ConceptDefinitionComponent c : list)
      if (c.hasConcept())
        count = count + countConcepts(c.getConcept());
    return count;
  }

  private boolean generateExpansion(XhtmlNode x, ValueSet vs) {
    boolean hasExtensions = false;
    Map<ConceptMap, String> mymaps = new HashMap<ConceptMap, String>();
    for (ConceptMap a : context.getMaps().values()) {
      if (((Reference) a.getSource()).getReference().equals(vs.getUrl())) {
        String url = "";
        if (context.getValueSets().containsKey(((Reference) a.getTarget()).getReference()))
            url = (String) context.getValueSets().get(((Reference) a.getTarget()).getReference()).getUserData("filename");
        mymaps.put(a, url);
      }
    }

    XhtmlNode h = x.addTag("h3");
    h.addText(vs.getDescription());
    if (vs.hasCopyright())
      generateCopyright(x, vs);

    XhtmlNode t = x.addTag("table").setAttribute("class", "codes");
    XhtmlNode tr = t.addTag("tr");
    tr.addTag("td").addTag("b").addText("Code");
    tr.addTag("td").addTag("b").addText("System");
    tr.addTag("td").addTag("b").addText("Display");

    addMapHeaders(tr, mymaps);
    for (ValueSetExpansionContainsComponent c : vs.getExpansion().getContains()) {
      addExpansionRowToTable(t, c, 0, mymaps);
    }    
    return hasExtensions;
  }

  private boolean generateDefinition(XhtmlNode x, ValueSet vs) {
    boolean hasExtensions = false;
    Map<ConceptMap, String> mymaps = new HashMap<ConceptMap, String>();
    for (ConceptMap a : context.getMaps().values()) {
      if (((Reference) a.getSource()).getReference().equals(vs.getUrl())) {
        String url = "";
        if (context.getValueSets().containsKey(((Reference) a.getTarget()).getReference()))
            url = (String) context.getValueSets().get(((Reference) a.getTarget()).getReference()).getUserData("filename");
        mymaps.put(a, url);
      }
    }
    List<String> langs = new ArrayList<String>();

    XhtmlNode h = x.addTag("h2");
    h.addText(vs.getName());
    XhtmlNode p = x.addTag("p");
    smartAddText(p, vs.getDescription());
    if (vs.hasCopyright())
      generateCopyright(x, vs);
    p = x.addTag("p");
    p.addText("This value set defines its own terms in the system "+vs.getDefine().getSystem());
    XhtmlNode t = x.addTag("table").setAttribute("class", "codes");
    boolean commentS = false;
    boolean deprecated = false;
    boolean display = false;
    boolean heirarchy = false;
    for (ConceptDefinitionComponent c : vs.getDefine().getConcept()) {
      commentS = commentS || conceptsHaveComments(c);
      deprecated = deprecated || conceptsHaveDeprecated(c);
      display = display || conceptsHaveDisplay(c);
      heirarchy = heirarchy || c.hasConcept();
      scanLangs(c, langs);
    }
    addMapHeaders(addTableHeaderRowStandard(t, heirarchy, display, true, commentS, deprecated), mymaps);
    for (ConceptDefinitionComponent c : vs.getDefine().getConcept()) {
      hasExtensions = addDefineRowToTable(t, c, 0, heirarchy, display, commentS, deprecated, mymaps) || hasExtensions;
    }    
    if (langs.size() > 0) {
      Collections.sort(langs);
      x.addTag("p").addTag("b").addText("Additional Language Displays");
      t = x.addTag("table").setAttribute("class", "codes");
      XhtmlNode tr = t.addTag("tr");
      tr.addTag("td").addTag("b").addText("Code");
      for (String lang : langs)
        tr.addTag("td").addTag("b").addText(lang);
      for (ConceptDefinitionComponent c : vs.getDefine().getConcept()) {
        addLanguageRow(c, t, langs);
      }
    }    
    return hasExtensions;
  }

  private void addLanguageRow(ConceptDefinitionComponent c, XhtmlNode t, List<String> langs) {
    XhtmlNode tr = t.addTag("tr");
    tr.addTag("td").addText(c.getCode());
    for (String lang : langs) {
      ConceptDefinitionDesignationComponent d = null;
      for (ConceptDefinitionDesignationComponent designation : c.getDesignation()) {
        if (lang.equals(designation.getLanguage())) 
          d = designation;
      }
      tr.addTag("td").addText(d == null ? "" : d.getValue());
    }
  }

  private void scanLangs(ConceptDefinitionComponent c, List<String> langs) {
    for (ConceptDefinitionDesignationComponent designation : c.getDesignation()) {
      String lang = designation.getLanguage();
      if (langs != null && !langs.contains(lang))
        langs.add(lang);
    }
    for (ConceptDefinitionComponent g : c.getConcept())
      scanLangs(g, langs);
  }

  private void addMapHeaders(XhtmlNode tr, Map<ConceptMap, String> mymaps) {
	  for (ConceptMap m : mymaps.keySet()) {
	  	XhtmlNode td = tr.addTag("td");
	  	XhtmlNode b = td.addTag("b");
	  	XhtmlNode a = b.addTag("a");
	  	a.setAttribute("href", prefix+mymaps.get(m));
	  	a.addText(m.getDescription());	  	
	  }	  
  }

	private void smartAddText(XhtmlNode p, String text) {
	  if (text == null)
	    return;
	  
    String[] lines = text.split("\\r\\n");
    for (int i = 0; i < lines.length; i++) {
      if (i > 0)
        p.addTag("br");
      p.addText(lines[i]);
    }
  }

  private boolean conceptsHaveComments(ConceptDefinitionComponent c) {
    if (ToolingExtensions.hasComment(c)) 
      return true;
    for (ConceptDefinitionComponent g : c.getConcept()) 
      if (conceptsHaveComments(g))
        return true;
    return false;
  }

  private boolean conceptsHaveDisplay(ConceptDefinitionComponent c) {
    if (c.hasDisplay()) 
      return true;
    for (ConceptDefinitionComponent g : c.getConcept()) 
      if (conceptsHaveDisplay(g))
        return true;
    return false;
  }

  private boolean conceptsHaveDeprecated(ConceptDefinitionComponent c) {
    if (ToolingExtensions.hasDeprecated(c)) 
      return true;
    for (ConceptDefinitionComponent g : c.getConcept()) 
      if (conceptsHaveDeprecated(g))
        return true;
    return false;
  }

  private void generateCopyright(XhtmlNode x, ValueSet vs) {
    XhtmlNode p = x.addTag("p");
    p.addTag("b").addText("Copyright Statement:");
    smartAddText(p, " " + vs.getCopyright());
  }


  private XhtmlNode addTableHeaderRowStandard(XhtmlNode t, boolean hasHeirarchy, boolean hasDisplay, boolean definitions, boolean comments, boolean deprecated) {
    XhtmlNode tr = t.addTag("tr");
    if (hasHeirarchy) 
      tr.addTag("td").addTag("b").addText("Lvl");
    tr.addTag("td").addTag("b").addText("Code");
    if (hasDisplay) 
      tr.addTag("td").addTag("b").addText("Display");
    if (definitions) 
      tr.addTag("td").addTag("b").addText("Definition");
    if (deprecated) 
      tr.addTag("td").addTag("b").addText("Deprecated");
    if (comments) 
      tr.addTag("td").addTag("b").addText("Comments");
    return tr;
  }

  private void addExpansionRowToTable(XhtmlNode t, ValueSetExpansionContainsComponent c, int i, Map<ConceptMap, String> mymaps) {
    XhtmlNode tr = t.addTag("tr");
    XhtmlNode td = tr.addTag("td");
    
    
    String s = Utilities.padLeft("", '.', i*2);
    td.addText(s);
    Resource e = context.getCodeSystems().get(c.getSystem());
    if (e == null)
      td.addText(c.getCode());
    else {
      XhtmlNode a = td.addTag("a");
      a.addText(c.getCode());
      a.setAttribute("href", prefix+getCsRef(e)+"#"+Utilities.nmtokenize(c.getCode()));
      
    }
    td = tr.addTag("td");
    td.addText(c.getSystem());
    td = tr.addTag("td");
    if (c.hasDisplayElement())
      td.addText(c.getDisplay());

    for (ConceptMap m : mymaps.keySet()) {
      td = tr.addTag("td");
      List<ConceptMapElementMapComponent> mappings = findMappingsForCode(c.getCode(), m);
      boolean first = true;
      for (ConceptMapElementMapComponent mapping : mappings) {
        if (!first)
            td.addTag("br");
        first = false;
        XhtmlNode span = td.addTag("span");
        span.setAttribute("title", mapping.getEquivalence().toString());
        span.addText(getCharForEquivalence(mapping));
        XhtmlNode a = td.addTag("a");
        a.setAttribute("href", prefix+mymaps.get(m)+"#"+mapping.getCode());
        a.addText(mapping.getCode());
        if (!Utilities.noString(mapping.getComments()))
          td.addTag("i").addText("("+mapping.getComments()+")");
      }
    }
    for (ValueSetExpansionContainsComponent cc : c.getContains()) {
      addExpansionRowToTable(t, cc, i+1, mymaps);
    }    
  }

  private boolean addDefineRowToTable(XhtmlNode t, ConceptDefinitionComponent c, int i, boolean hasHeirarchy, boolean hasDisplay, boolean comment, boolean deprecated, Map<ConceptMap, String> maps) {
    boolean hasExtensions = false;
    XhtmlNode tr = t.addTag("tr");
    XhtmlNode td = tr.addTag("td");
    if (hasHeirarchy) {
      td.addText(Integer.toString(i+1));
      td = tr.addTag("td");
      String s = Utilities.padLeft("", '\u00A0', i*2);
      td.addText(s);
    }
    td.addText(c.getCode());
    XhtmlNode a;
    if (c.hasCodeElement()) {
      a = td.addTag("a");
      a.setAttribute("name", Utilities.nmtokenize(c.getCode()));
      a.addText(" ");
    }
    
    if (hasDisplay) {
      td = tr.addTag("td");
      if (c.hasDisplayElement())
        td.addText(c.getDisplay());
    }
    td = tr.addTag("td");
    if (c != null)
      smartAddText(td, c.getDefinition());
    if (deprecated) {
      td = tr.addTag("td");
      String s = ToolingExtensions.getDeprecated(c);
      if (s != null) {
        smartAddText(td, s);
        hasExtensions = true;
      }
    }
    if (comment) {
      td = tr.addTag("td");
      String s = ToolingExtensions.getComment(c);
      if (s != null) {
        smartAddText(td, s);
        hasExtensions = true;
      }
    }
    for (ConceptMap m : maps.keySet()) {
      td = tr.addTag("td");
      List<ConceptMapElementMapComponent> mappings = findMappingsForCode(c.getCode(), m);
      boolean first = true;
      for (ConceptMapElementMapComponent mapping : mappings) {
      	if (!first)
      		  td.addTag("br");
      	first = false;
      	XhtmlNode span = td.addTag("span");
      	span.setAttribute("title", mapping.hasEquivalence() ?  mapping.getEquivalence().toCode() : "");
      	span.addText(getCharForEquivalence(mapping));
      	a = td.addTag("a");
      	a.setAttribute("href", prefix+maps.get(m)+"#"+mapping.getCode());
      	a.addText(mapping.getCode());
        if (!Utilities.noString(mapping.getComments()))
          td.addTag("i").addText("("+mapping.getComments()+")");
      }
    }
    for (CodeType e : ToolingExtensions.getSubsumes(c)) {
      hasExtensions = true;
      tr = t.addTag("tr");
      td = tr.addTag("td");
      String s = Utilities.padLeft("", '.', i*2);
      td.addText(s);
      a = td.addTag("a");
      a.setAttribute("href", "#"+Utilities.nmtokenize(e.getValue()));
      a.addText(c.getCode());
    }
    for (ConceptDefinitionComponent cc : c.getConcept()) {
      hasExtensions = addDefineRowToTable(t, cc, i+1, hasHeirarchy, hasDisplay, comment, deprecated, maps) || hasExtensions;
    }    
    return hasExtensions;
  }


  private String getCharForEquivalence(ConceptMapElementMapComponent mapping) {
    if (!mapping.hasEquivalence())
      return "";
	  switch (mapping.getEquivalence()) {
	  case EQUAL : return "=";
	  case EQUIVALENT : return "~";
	  case WIDER : return ">";
	  case NARROWER : return "<";
	  case INEXACT : return "><";
	  case UNMATCHED : return "-";
	  case DISJOINT : return "!=";
    default: return "?";
	  }
  }

	private List<ConceptMapElementMapComponent> findMappingsForCode(String code, ConceptMap map) {
	  List<ConceptMapElementMapComponent> mappings = new ArrayList<ConceptMapElementMapComponent>();
	  
  	for (ConceptMapElementComponent c : map.getElement()) {
	  	if (c.getCode().equals(code)) 
	  		mappings.addAll(c.getMap());
	  }
	  return mappings;
  }

	private boolean generateComposition(XhtmlNode x, ValueSet vs) throws Exception {
	  boolean hasExtensions = false;
    if (!vs.hasDefine()) {
      XhtmlNode h = x.addTag("h2");
      h.addText(vs.getName());
      XhtmlNode p = x.addTag("p");
      smartAddText(p, vs.getDescription());
      if (vs.hasCopyrightElement())
        generateCopyright(x, vs);
      p = x.addTag("p");
      p.addText("This value set includes codes defined in other code systems, using the following rules:");
    } else {
      XhtmlNode p = x.addTag("p");
      p.addText("In addition, this value set includes codes defined in other code systems, using the following rules:");

    }
    XhtmlNode ul = x.addTag("ul");
    XhtmlNode li;
    for (UriType imp : vs.getCompose().getImport()) {
      li = ul.addTag("li");
      li.addText("Import all the codes that are part of ");
      AddVsRef(imp.getValue(), li);
    }
    for (ConceptSetComponent inc : vs.getCompose().getInclude()) {
      hasExtensions = genInclude(ul, inc, "Include") || hasExtensions;      
    }
    for (ConceptSetComponent exc : vs.getCompose().getExclude()) {
      hasExtensions = genInclude(ul, exc, "Exclude") || hasExtensions;      
    }
    return hasExtensions;
  }

  private void AddVsRef(String value, XhtmlNode li) {

    ValueSet vs = context.getValueSets().get(value);
    if (vs == null) 
      vs = context.getCodeSystems().get(value); 
    if (vs != null) {
      String ref= (String) vs.getUserData("filename");
      XhtmlNode a = li.addTag("a");
      a.setAttribute("href", prefix+(ref == null ? "??" : ref.replace("\\", "/")));
      a.addText(value);
    } else if (value.equals("http://snomed.info/sct") || value.equals("http://snomed.info/id")) {
      XhtmlNode a = li.addTag("a");
      a.setAttribute("href", value);
      a.addText("SNOMED-CT");      
    }
    else 
      li.addText(value);
  }

  private  boolean genInclude(XhtmlNode ul, ConceptSetComponent inc, String type) throws Exception {
    boolean hasExtensions = false;
    XhtmlNode li;
    li = ul.addTag("li");
    ValueSet e = context.getCodeSystems().get(inc.getSystem());
    
    if (inc.getConcept().size() == 0 && inc.getFilter().size() == 0) { 
      li.addText(type+" all codes defined in ");
      addCsRef(inc, li, e);
    } else { 
      if (inc.getConcept().size() > 0) {
        li.addText(type+" these codes as defined in ");
        addCsRef(inc, li, e);
      
        XhtmlNode t = li.addTag("table");
        boolean hasComments = false;
        boolean hasDefinition = false;
        for (ConceptReferenceComponent c : inc.getConcept()) {
          hasComments = hasComments || ExtensionHelper.hasExtension(c, ToolingExtensions.EXT_COMMENT);
          hasDefinition = hasDefinition || ExtensionHelper.hasExtension(c, ToolingExtensions.EXT_DEFINITION);
        }
        if (hasComments || hasDefinition)
          hasExtensions = true;
        addTableHeaderRowStandard(t, false, true, hasDefinition, hasComments, false);
        for (ConceptReferenceComponent c : inc.getConcept()) {
          XhtmlNode tr = t.addTag("tr");
          tr.addTag("td").addText(c.getCode());
          ConceptDefinitionComponent cc = getConceptForCode(e, c.getCode(), inc.getSystem());
          
          XhtmlNode td = tr.addTag("td");
          if (!Utilities.noString(c.getDisplay()))
            td.addText(c.getDisplay());
          else if (cc != null && !Utilities.noString(cc.getDisplay()))
            td.addText(cc.getDisplay());
          
          td = tr.addTag("td");
          if (ExtensionHelper.hasExtension(c, ToolingExtensions.EXT_DEFINITION))
            smartAddText(td, ToolingExtensions.readStringExtension(c, ToolingExtensions.EXT_DEFINITION));
          else if (cc != null && !Utilities.noString(cc.getDefinition()))
            smartAddText(td, cc.getDefinition());

          if (ExtensionHelper.hasExtension(c, ToolingExtensions.EXT_COMMENT)) {
            smartAddText(tr.addTag("td"), "Note: "+ToolingExtensions.readStringExtension(c, ToolingExtensions.EXT_COMMENT));
          }
        }
      }
      for (ConceptSetFilterComponent f : inc.getFilter()) {
        li.addText(type+" codes from ");
        addCsRef(inc, li, e);
        li.addText(" where "+f.getProperty()+" "+describe(f.getOp())+" ");
        if (e != null && codeExistsInValueSet(e, f.getValue())) {
          XhtmlNode a = li.addTag("a");
          a.addText(f.getValue());
          a.setAttribute("href", prefix+getCsRef(e)+"#"+Utilities.nmtokenize(f.getValue()));
        } else
          li.addText(f.getValue());
        String disp = ToolingExtensions.getDisplayHint(f);
        if (disp != null)
          li.addText(" ("+disp+")");
      }
    }
    return hasExtensions;
  }

  private String describe(FilterOperator opSimple) {
    switch (opSimple) {
    case EQUAL: return " = ";
    case ISA: return " is-a ";
    case ISNOTA: return " is-not-a ";
    case REGEX: return " matches (by regex) ";
		case NULL: return " ?? ";
		case IN: return " in ";
		case NOTIN: return " not in ";
    }
    return null;
  }

  private <T extends Resource> ConceptDefinitionComponent getConceptForCode(T e, String code, String system) {
    if (e == null) {
      if (context.getTerminologyServices() != null)
        return context.getTerminologyServices().getCodeDefinition(system, code);
      else
        return null;
    }
    ValueSet vs = (ValueSet) e;
    if (!vs.hasDefine())
      return null;
    for (ConceptDefinitionComponent c : vs.getDefine().getConcept()) {
      ConceptDefinitionComponent v = getConceptForCode(c, code);   
      if (v != null)
        return v;
    }
    return null;
  }
  
  
  
  private ConceptDefinitionComponent getConceptForCode(ConceptDefinitionComponent c, String code) {
    if (code.equals(c.getCode()))
      return c;
    for (ConceptDefinitionComponent cc : c.getConcept()) {
      ConceptDefinitionComponent v = getConceptForCode(cc, code);   
      if (v != null)
        return v;
    }
    return null;
  }

  private  <T extends Resource> void addCsRef(ConceptSetComponent inc, XhtmlNode li, T cs) {
    String ref = null;
    if (cs != null) {
      ref = (String) cs.getUserData("filename");
      if (Utilities.noString(ref))
        ref = (String) cs.getUserData("filename");
    }
    if (cs != null && ref != null) {
      if (!Utilities.noString(prefix) && ref.startsWith("http://hl7.org/fhir/"))
        ref = ref.substring(20)+"/index.html";
      XhtmlNode a = li.addTag("a");
      a.setAttribute("href", prefix+ref.replace("\\", "/"));
      a.addText(inc.getSystem().toString());
    } else 
      li.addText(inc.getSystem().toString());
  }

  private  <T extends Resource> String getCsRef(T cs) {
    String ref = (String) cs.getUserData("filename");
    if (Utilities.noString(ref))
      ref = (String) cs.getUserData("filename");
    return ref == null ? "??" : ref.replace("\\", "/");
  }

  private  <T extends Resource> boolean codeExistsInValueSet(T cs, String code) {
    ValueSet vs = (ValueSet) cs;
    for (ConceptDefinitionComponent c : vs.getDefine().getConcept()) {
      if (inConcept(code, c))
        return true;
    }
    return false;
  }

  private boolean inConcept(String code, ConceptDefinitionComponent c) {
    if (c.hasCodeElement() && c.getCode().equals(code))
      return true;
    for (ConceptDefinitionComponent g : c.getConcept()) {
      if (inConcept(code, g))
        return true;
    }
    return false;
  }

  /**
   * This generate is optimised for the build tool in that it tracks the source extension. 
   * But it can be used for any other use.
   *  
   * @param vs
   * @param codeSystems
   * @throws Exception
   */
  public void generate(OperationOutcome op) throws Exception {
    XhtmlNode x = new XhtmlNode(NodeType.Element, "div");
    boolean hasSource = false;
    boolean success = true;
    for (OperationOutcomeIssueComponent i : op.getIssue()) {
    	success = success && i.getSeverity() != IssueSeverity.INFORMATION;
    	hasSource = hasSource || ExtensionHelper.hasExtension(i, ToolingExtensions.EXT_ISSUE_SOURCE);
    }
    if (success)
    	x.addTag("p").addText("All OK");
    if (op.getIssue().size() > 0) {
    		XhtmlNode tbl = x.addTag("table");
    		tbl.setAttribute("class", "grid"); // on the basis that we'll most likely be rendered using the standard fhir css, but it doesn't really matter
    		XhtmlNode tr = tbl.addTag("tr");
    		tr.addTag("td").addTag("b").addText("Severity");
    		tr.addTag("td").addTag("b").addText("Location");
    		tr.addTag("td").addTag("b").addText("Details");
  			tr.addTag("td").addTag("b").addText("Code");
    		if (hasSource)
    			tr.addTag("td").addTag("b").addText("Source");
    		for (OperationOutcomeIssueComponent i : op.getIssue()) {
    			tr = tbl.addTag("tr");
    			tr.addTag("td").addText(i.getSeverity().toString());
    			XhtmlNode td = tr.addTag("td");
    			boolean d = false;
    			for (StringType s : i.getLocation()) {
    				if (d)
    					td.addText(", ");
    				else
    					d = true;
    				td.addText(s.getValue());      		
    			}
    			smartAddText(tr.addTag("td"), i.getDetails());
  				tr.addTag("td").addText(gen(i.getCode()));
    			if (hasSource)
    				tr.addTag("td").addText(gen(ExtensionHelper.getExtension(i, ToolingExtensions.EXT_ISSUE_SOURCE)));
    		}    
    	}
    inject(op, x, hasSource ? NarrativeStatus.EXTENSIONS :  NarrativeStatus.GENERATED);  	
  }


	private String gen(Extension extension) throws Exception {
		if (extension.getValue() instanceof CodeType)
			return ((CodeType) extension.getValue()).getValue();
		if (extension.getValue() instanceof Coding)
			return gen((Coding) extension.getValue());

	  throw new Exception("Unhandled type "+extension.getValue().getClass().getName());
  }

	private String gen(CodeableConcept code) {
		if (code == null)
	  	return null;
		if (code.hasText())
			return code.getText();
		if (code.hasCoding())
			return gen(code.getCoding().get(0));
		return null;
	}
	
	private String gen(Coding code) {
	  if (code == null)
	  	return null;
	  if (code.hasDisplayElement())
	  	return code.getDisplay();
	  if (code.hasCodeElement())
	  	return code.getCode();
	  return null;
  }

	public void generate(OperationDefinition opd) throws Exception {
    XhtmlNode x = new XhtmlNode(NodeType.Element, "div");
    x.addTag("h2").addText(opd.getName());
    x.addTag("p").addText(Utilities.capitalize(opd.getKind().toString())+": "+opd.getName());
    addMarkdown(x, opd.getDescription());
    
    if (opd.getSystem())
      x.addTag("p").addText("URL: [base]/$"+opd.getName());
    for (CodeType c : opd.getType()) {
      x.addTag("p").addText("URL: [base]/"+c.getValue()+"/$"+opd.getName());
      if (opd.getInstance())
        x.addTag("p").addText("URL: [base]/"+c.getValue()+"/[id]/$"+opd.getName());
    }
    
    x.addTag("p").addText("Parameters");
    XhtmlNode tbl = x.addTag("table").setAttribute("class", "grid");
    XhtmlNode tr = tbl.addTag("tr");
    tr.addTag("td").addTag("b").addText("Use");
    tr.addTag("td").addTag("b").addText("Name");
    tr.addTag("td").addTag("b").addText("Cardinality");
    tr.addTag("td").addTag("b").addText("Type");
    tr.addTag("td").addTag("b").addText("Documentation");
    for (OperationDefinitionParameterComponent p : opd.getParameter()) {
      tr = tbl.addTag("tr");
      tr.addTag("td").addText(p.getUse().toString());
      tr.addTag("td").addText(p.getName());
      tr.addTag("td").addText(Integer.toString(p.getMin())+".."+p.getMax());
      tr.addTag("td").addText(p.hasType() ? p.getType() : "");
      addMarkdown(tr.addTag("td"), p.getDocumentation());
      if (!p.hasType()) {
        for (OperationDefinitionParameterPartComponent pp : p.getPart()) {
          tr = tbl.addTag("tr");
          tr.addTag("td");
          tr.addTag("td").addText(pp.getName());
          tr.addTag("td").addText(Integer.toString(pp.getMin())+".."+pp.getMax());
          tr.addTag("td").addText(pp.getType());
          addMarkdown(tr.addTag("td"), pp.getDocumentation());
        }
      }
    }
    addMarkdown(x, opd.getNotes());
    inject(opd, x, NarrativeStatus.GENERATED);
	}
	
	private void addMarkdown(XhtmlNode x, String text) throws Exception {
	  if (text != null) {	    
	    // 1. custom FHIR extensions
	    while (text.contains("[[[")) {
	      String left = text.substring(0, text.indexOf("[[["));
	      String url = text.substring(text.indexOf("[[[")+3, text.indexOf("]]]"));
	      String right = text.substring(text.indexOf("]]]")+3);
	      String actual = url;
	      //      String[] parts = url.split("\\#");
	      //      StructureDefinition p = parts[0]; // todo: definitions.getProfileByURL(parts[0]);
	      //      if (p != null)
	      //        actual = p.getTag("filename")+".html";
	      //      else {
	      //        throw new Exception("Unresolved logical URL "+url);
	      //      }
	      text = left+"["+url+"]("+actual+")"+right;
	    }

	    // 2. markdown
	    String s = Processor.process(Utilities.escapeXml(text));
	    XhtmlParser p = new XhtmlParser();
	    XhtmlNode m = p.parse("<div>"+s+"</div>", "div");
	    x.getChildNodes().addAll(m.getChildNodes());
	  }
  }

  public void generate(Conformance conf) {
    XhtmlNode x = new XhtmlNode(NodeType.Element, "div");
    x.addTag("h2").addText(conf.getName());
    smartAddText(x.addTag("p"), conf.getDescription());
    ConformanceRestComponent rest = conf.getRest().get(0);
    XhtmlNode t = x.addTag("table");
    addTableRow(t, "Mode", rest.getMode().toString());
    addTableRow(t, "Description", rest.getDocumentation());
    
    addTableRow(t, "Transaction", showOp(rest, SystemRestfulInteraction.TRANSACTION));
    addTableRow(t, "System History", showOp(rest, SystemRestfulInteraction.HISTORYSYSTEM));
    addTableRow(t, "System Search", showOp(rest, SystemRestfulInteraction.SEARCHSYSTEM));
    
    t = x.addTag("table");
    XhtmlNode tr = t.addTag("tr");
    tr.addTag("th").addTag("b").addText("Resource Type");
    tr.addTag("th").addTag("b").addText("Profile");
    tr.addTag("th").addTag("b").addText("Read");
    tr.addTag("th").addTag("b").addText("V-Read");
    tr.addTag("th").addTag("b").addText("Search");
    tr.addTag("th").addTag("b").addText("Update");
    tr.addTag("th").addTag("b").addText("Updates");
    tr.addTag("th").addTag("b").addText("Create");
    tr.addTag("th").addTag("b").addText("Delete");
    tr.addTag("th").addTag("b").addText("History");
    
    for (ConformanceRestResourceComponent r : rest.getResource()) {
      tr = t.addTag("tr");
      tr.addTag("td").addText(r.getType());
      if (r.hasProfile()) {
      	XhtmlNode a = tr.addTag("td").addTag("a");
      	a.addText(r.getProfile().getReference());
      	a.setAttribute("href", prefix+r.getProfile().getReference());
      }
      tr.addTag("td").addText(showOp(r, TypeRestfulInteraction.READ));
      tr.addTag("td").addText(showOp(r, TypeRestfulInteraction.VREAD));
      tr.addTag("td").addText(showOp(r, TypeRestfulInteraction.SEARCHTYPE));
      tr.addTag("td").addText(showOp(r, TypeRestfulInteraction.UPDATE));
      tr.addTag("td").addText(showOp(r, TypeRestfulInteraction.HISTORYINSTANCE));
      tr.addTag("td").addText(showOp(r, TypeRestfulInteraction.CREATE));
      tr.addTag("td").addText(showOp(r, TypeRestfulInteraction.DELETE));
      tr.addTag("td").addText(showOp(r, TypeRestfulInteraction.HISTORYTYPE));
    }
    
    inject(conf, x, NarrativeStatus.GENERATED);
  }

  private String showOp(ConformanceRestResourceComponent r, TypeRestfulInteraction on) {
    for (ResourceInteractionComponent op : r.getInteraction()) {
      if (op.getCode() == on)
        return "y";
    }
    return "";
  }

  private String showOp(ConformanceRestComponent r, SystemRestfulInteraction on) {
    for (SystemInteractionComponent op : r.getInteraction()) {
      if (op.getCode() == on)
        return "y";
    }	
    return "";
  }

  private void addTableRow(XhtmlNode t, String name, String value) {
    XhtmlNode tr = t.addTag("tr");
    tr.addTag("td").addText(name);
    tr.addTag("td").addText(value);    
  }

  public XhtmlNode generateDocumentNarrative(Bundle feed) {
    /*
     When the document is presented for human consumption, applications must present the collated narrative portions of the following resources in order:
     * The Composition resource
     * The Subject resource
     * Resources referenced in the section.content
     */
    XhtmlNode root = new XhtmlNode(NodeType.Element, "div");
    Composition comp = (Composition) feed.getEntry().get(0).getResource();
    root.getChildNodes().add(comp.getText().getDiv());
    Resource subject = ResourceUtilities.getById(feed, null, comp.getSubject().getReference());
    if (subject != null && subject instanceof DomainResource) {
      root.addTag("hr");
      root.getChildNodes().add(((DomainResource)subject).getText().getDiv());
    }
    List<SectionComponent> sections = comp.getSection();
    renderSections(feed, root, sections, 1);
    return root;
  }

  private void renderSections(Bundle feed, XhtmlNode node, List<SectionComponent> sections, int level) {
    for (SectionComponent section : sections) {
      node.addTag("hr");
      if (section.hasTitleElement())
        node.addTag("h"+Integer.toString(level)).addText(section.getTitle());
//      else if (section.hasCode())
//        node.addTag("h"+Integer.toString(level)).addText(displayCodeableConcept(section.getCode()));
      
//      if (section.hasText()) {
//        node.getChildNodes().add(section.getText().getDiv());
//      }
//      
//      if (!section.getSection().isEmpty()) {
//        renderSections(feed, node.addTag("blockquote"), section.getSection(), level+1);
//      }
    }
  }
  
}
