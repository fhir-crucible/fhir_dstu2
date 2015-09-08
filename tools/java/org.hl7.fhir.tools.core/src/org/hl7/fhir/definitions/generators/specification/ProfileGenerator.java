package org.hl7.fhir.definitions.generators.specification;
import java.io.File;
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
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hl7.fhir.definitions.ecore.fhir.ConstrainedTypeDefn;
import org.hl7.fhir.definitions.model.BindingSpecification;
import org.hl7.fhir.definitions.model.BindingSpecification.BindingMethod;
import org.hl7.fhir.definitions.model.ConstraintStructure;
import org.hl7.fhir.definitions.model.DefinedStringPattern;
import org.hl7.fhir.definitions.model.Definitions;
import org.hl7.fhir.definitions.model.ElementDefn;
import org.hl7.fhir.definitions.model.ImplementationGuideDefn;
import org.hl7.fhir.definitions.model.Invariant;
import org.hl7.fhir.definitions.model.Operation;
import org.hl7.fhir.definitions.model.OperationParameter;
import org.hl7.fhir.definitions.model.PrimitiveType;
import org.hl7.fhir.definitions.model.Profile;
import org.hl7.fhir.definitions.model.ProfiledType;
import org.hl7.fhir.definitions.model.ResourceDefn;
import org.hl7.fhir.definitions.model.SearchParameterDefn;
import org.hl7.fhir.definitions.model.TypeDefn;
import org.hl7.fhir.definitions.model.TypeRef;
import org.hl7.fhir.definitions.parsers.TypeParser;
import org.hl7.fhir.instance.formats.FormatUtilities;
import org.hl7.fhir.instance.model.Bundle;
import org.hl7.fhir.instance.model.CodeType;
import org.hl7.fhir.instance.model.ContactPoint;
import org.hl7.fhir.instance.model.DataElement;
import org.hl7.fhir.instance.model.ContactPoint.ContactPointSystem;
import org.hl7.fhir.instance.model.DataElement.DataElementStringency;
import org.hl7.fhir.instance.model.ElementDefinition;
import org.hl7.fhir.instance.model.ElementDefinition.AggregationMode;
import org.hl7.fhir.instance.model.ElementDefinition.ConstraintSeverity;
import org.hl7.fhir.instance.model.ElementDefinition.ElementDefinitionBindingComponent;
import org.hl7.fhir.instance.model.ElementDefinition.ElementDefinitionConstraintComponent;
import org.hl7.fhir.instance.model.ElementDefinition.ElementDefinitionMappingComponent;
import org.hl7.fhir.instance.model.ElementDefinition.ElementDefinitionSlicingComponent;
import org.hl7.fhir.instance.model.ElementDefinition.PropertyRepresentation;
import org.hl7.fhir.instance.model.ElementDefinition.SlicingRules;
import org.hl7.fhir.instance.model.ElementDefinition.TypeRefComponent;
import org.hl7.fhir.instance.model.Enumerations.ConformanceResourceStatus;
import org.hl7.fhir.instance.model.Enumerations.SearchParamType;
import org.hl7.fhir.instance.model.Extension;
import org.hl7.fhir.instance.model.Factory;
import org.hl7.fhir.instance.model.InstantType;
import org.hl7.fhir.instance.model.IntegerType;
import org.hl7.fhir.instance.model.Narrative;
import org.hl7.fhir.instance.model.Narrative.NarrativeStatus;
import org.hl7.fhir.instance.model.OperationDefinition.OperationDefinitionParameterComponent;
import org.hl7.fhir.instance.model.OperationDefinition.OperationDefinitionParameterBindingComponent;
import org.hl7.fhir.instance.model.OperationDefinition.OperationKind;
import org.hl7.fhir.instance.model.OperationDefinition.OperationParameterUse;
import org.hl7.fhir.instance.model.OperationDefinition;
import org.hl7.fhir.instance.model.Reference;
import org.hl7.fhir.instance.model.OperationOutcome.IssueSeverity;
import org.hl7.fhir.instance.model.SearchParameter;
import org.hl7.fhir.instance.model.SearchParameter.SearchParameterContactComponent;
import org.hl7.fhir.instance.model.StringType;
import org.hl7.fhir.instance.model.StructureDefinition;
import org.hl7.fhir.instance.model.StructureDefinition.StructureDefinitionContactComponent;
import org.hl7.fhir.instance.model.StructureDefinition.StructureDefinitionDifferentialComponent;
import org.hl7.fhir.instance.model.StructureDefinition.StructureDefinitionMappingComponent;
import org.hl7.fhir.instance.model.StructureDefinition.StructureDefinitionSnapshotComponent;
import org.hl7.fhir.instance.model.StructureDefinition.StructureDefinitionKind;
import org.hl7.fhir.instance.model.OperationOutcome.IssueType;
import org.hl7.fhir.instance.model.Type;
import org.hl7.fhir.instance.model.UriType;
import org.hl7.fhir.instance.utils.NarrativeGenerator;
import org.hl7.fhir.instance.utils.ProfileUtilities;
import org.hl7.fhir.instance.utils.ProfileUtilities.ProfileKnowledgeProvider;
import org.hl7.fhir.instance.utils.ToolingExtensions;
import org.hl7.fhir.instance.validation.ValidationMessage;
import org.hl7.fhir.instance.validation.ValidationMessage.Source;
import org.hl7.fhir.tools.publisher.BuildWorkerContext;
import org.hl7.fhir.utilities.Utilities;
import org.hl7.fhir.utilities.xhtml.NodeType;
import org.hl7.fhir.utilities.xhtml.XhtmlNode;

public class ProfileGenerator {

  public enum SnapShotMode {
    None, 
    Resource,
    DataType
  } 

  private BuildWorkerContext context;
  private Definitions definitions;

  // status
  // note that once we start slicing, the slices keep their own maps, but all share the master pathname list
  private final Map<String, ElementDefinition> paths = new HashMap<String, ElementDefinition>();
  private final List<String> pathNames = new ArrayList<String>();
  private ProfileKnowledgeProvider pkp;
  private Calendar genDate;
  private Bundle dataElements;

  private static class SliceHandle {
    private String name;
    private Map<String, ElementDefinition> paths = new HashMap<String, ElementDefinition>();
  }

  public ProfileGenerator(Definitions definitions, BuildWorkerContext context, ProfileKnowledgeProvider pkp, Calendar genDate, Bundle dataElements) {
    super();
    this.definitions = definitions;
    this.context = context;
    this.pkp = pkp;
    this.genDate = genDate;
    this.dataElements = dataElements;
  }

  private Map<String, DataElement> des = new HashMap<String, DataElement>();
  private static int extensionCounter;
  private static int profileCounter = 0;
  
  private void generateElementDefinition(ElementDefinition ed, ElementDefinition parent) throws Exception {
    String id = ed.getPath().replace("[x]", "X");
    if (id.length() > 64)
      id = id.substring(0, 64);
    
    DataElement de;
    if (des.containsKey(id)) {
      de = des.get(id);
      // do it again because we now have more information to generate with
      de.getElement().clear();
      de.getExtension().clear();
    } else {
      de = new DataElement();
      de.setId(id);
      des.put(id, de);
      de.setUrl("http://hl7.org/fhir/DataElement/"+de.getId());
      if (de.getId().contains("."))
        definitions.addNs(de.getUrl(), "Data Element "+ed.getPath(), definitions.getSrcFile(de.getId().substring(0, de.getId().indexOf(".")))+"-definitions.html#"+de.getId());
      if (dataElements != null)
        dataElements.addEntry().setResource(de).setFullUrl(de.getUrl());
    }
    
    de.getMeta().setLastUpdatedElement(new InstantType(genDate));
    de.setName(ed.getName());
    de.setStatus(ConformanceResourceStatus.DRAFT);
    de.setExperimental(true);
    de.setStringency(DataElementStringency.FULLYSPECIFIED);
    if (parent != null) {
      Extension ext = de.addExtension();
      ext.setUrl("http://hl7.org/fhir/StructureDefinition/dataelement-relationship");
      Extension ext2 = ext.addExtension();
      ext2.setUrl("type");
      ext2.setValue(new CodeType("composed"));
      ext2 = ext.addExtension();
      ext2.setUrl("cardinality");
      ext2.setValue(new StringType("1"));
      ext2 = ext.addExtension();
      ext2.setUrl("target");
      ext2.setValue(new UriType("http://hl7.org/fhir/DataElement/"+parent.getPath()));
    }
    de.addElement(ed);
  }

  public StructureDefinition generate(PrimitiveType type) throws Exception {
    StructureDefinition p = new StructureDefinition();
    p.setId(type.getCode());
    p.setUrl("http://hl7.org/fhir/StructureDefinition/"+ type.getCode());
    p.setKind(StructureDefinitionKind.DATATYPE);
    p.setAbstract(false);
    p.setUserData("filename", type.getCode().toLowerCase());
    p.setUserData("path", "datatypes.html#"+type.getCode());
    p.setBase("http://hl7.org/fhir/StructureDefinition/Element");

    ToolResourceUtilities.updateUsage(p, "core");
    p.setName(type.getCode());
    p.setPublisher("HL7 FHIR Standard");
    p.addContact().getTelecom().add(Factory.newContactPoint(ContactPointSystem.OTHER, "http://hl7.org/fhir"));
    p.setDescription("Base StructureDefinition for "+type.getCode()+" Type: "+type.getDefinition());
    p.setDate(genDate.getTime());
    p.setStatus(ConformanceResourceStatus.fromCode("draft")); // DSTU

    Set<String> containedSlices = new HashSet<String>();

    // first, the differential
    p.setDifferential(new StructureDefinitionDifferentialComponent());
    ElementDefinition ec = new ElementDefinition();
    p.getDifferential().getElement().add(ec);
    ec.setPath(type.getCode());
    ec.setShort("Primitive Type " +type.getCode());
    ec.setDefinition(type.getDefinition());
    ec.setComments(type.getComment());
    ec.setMin(0);
    ec.setMax("*");
    ec.getType().add(new TypeRefComponent().setCode("Element"));
    ec = new ElementDefinition();
    p.getDifferential().getElement().add(ec);
    ec.setPath(type.getCode()+".value");
    ec.addRepresentation(PropertyRepresentation.XMLATTR);
    ec.setShort("Primitive value for " +type.getCode());
    ec.setDefinition("Primitive value for " +type.getCode());
    ec.setMin(0);
    ec.setMax("1");
    ec.getFormatCommentsPre().add("Note: the primitive value does not have an assigned type\r\n      The actual value domain is assumed to be known by magic by\r\n      reading the spec and from the name of the primitive type");
    ec.getFormatCommentsPre().add("Schema Type: "+type.getSchemaType());
    if (!Utilities.noString(type.getRegEx()))
      ec.getFormatCommentsPre().add("Regex: "+type.getRegEx());
    addSpecificDetails(type, ec);

    reset();
    // now. the snapshot
    p.setSnapshot(new StructureDefinitionSnapshotComponent());
    ElementDefinition ec1 = new ElementDefinition();
    p.getSnapshot().getElement().add(ec1);
    ec1.setPath(type.getCode());
    ec1.setShort("Primitive Type " +type.getCode());
    ec1.setDefinition(type.getDefinition());
    ec1.setComments(type.getComment());
    ec1.getType().add(new TypeRefComponent().setCode("Element"));
    ec1.setMin(0);
    ec1.setMax("*");
    generateElementDefinition(ec1, null);

    ElementDefinition ec2 = new ElementDefinition();
    p.getSnapshot().getElement().add(ec2);
    ec2.setPath(type.getCode()+".id");
    ec2.addRepresentation(PropertyRepresentation.XMLATTR);
    ec2.setDefinition("unique id for the element within a resource (for internal references)");
    ec2.setMin(0);
    ec2.setMax("1");
    ec2.setShort("xml:id (or equivalent in JSON)");
    ec2.getType().add(new TypeRefComponent().setCode("id"));
    generateElementDefinition(ec2, ec1);

    makeExtensionSlice("extension", p, p.getSnapshot(), null, type.getCode());

    ElementDefinition ec3 = new ElementDefinition();
    p.getSnapshot().getElement().add(ec3);
    ec3.setPath(type.getCode()+".value");
    ec3.addRepresentation(PropertyRepresentation.XMLATTR);
    ec3.setDefinition("The actual value");
    ec3.setMin(0);
    ec3.setMax("1");
    ec3.setShort("Primitive value for " +type.getCode());
    ec3.getFormatCommentsPre().add("Note: the primitive value does not have an assigned type\r\n        The actual value domain is assumed to be known by magic by\r\n        reading the spec and from the name of the primitive type");
    ec3.getFormatCommentsPre().add("Schema Type: "+type.getSchemaType());
    if (!Utilities.noString(type.getRegEx()))
      ec3.getFormatCommentsPre().add("Regex: "+type.getRegEx());
    addSpecificDetails(type, ec3);
    generateElementDefinition(ec3, ec);

    containedSlices.clear();

//    XhtmlNode div = new XhtmlNode(NodeType.Element, "div");
//    div.addText("to do");
//    p.setText(new Narrative());
//    p.getText().setStatus(NarrativeStatus.GENERATED);
//    p.getText().setDiv(div);
    checkHasTypes(p);
    return p;
  }

  private void addSpecificDetails(PrimitiveType type, ElementDefinition ed) {
    if (type.getCode().equals("integer")) {
      ed.setMinValue(new IntegerType(-2147483648));
      ed.setMaxValue(new IntegerType(2147483647));       
    }
    if (type.getCode().equals("string")) {
      ed.setMaxLength(1024 * 1024);
    }    
  }

  private String prefix(String prefix, String value) {
    if (value == null)
      return prefix;
    if (value.startsWith(prefix))
      return value;
    return prefix + value;
  }

  public StructureDefinition generate(DefinedStringPattern type) throws Exception {

    StructureDefinition p = new StructureDefinition();
    p.setId(type.getCode());
    p.setUrl("http://hl7.org/fhir/StructureDefinition/"+ type.getCode());
    p.setBase("http://hl7.org/fhir/StructureDefinition/"+ type.getBase());
    p.setKind(StructureDefinitionKind.DATATYPE);
    p.setAbstract(false);
    p.setUserData("filename", type.getCode().toLowerCase());
    p.setUserData("path", "datatypes.html#"+type.getCode());

    ToolResourceUtilities.updateUsage(p, "core");
    p.setName(type.getCode());
    p.setConstrainedType(type.getBase());
    p.setPublisher("HL7 FHIR Standard");
    p.addContact().getTelecom().add(Factory.newContactPoint(ContactPointSystem.OTHER, "http://hl7.org/fhir"));
    p.setDescription("Base StructureDefinition for "+type.getCode()+" type: "+type.getDefinition());
    p.setDate(genDate.getTime());
    p.setStatus(ConformanceResourceStatus.fromCode("draft")); // DSTU

    Set<String> containedSlices = new HashSet<String>();

    // first, the differential
    p.setDifferential(new StructureDefinitionDifferentialComponent());
    ElementDefinition ec1 = new ElementDefinition();
    p.getDifferential().getElement().add(ec1);
    ec1.setPath(type.getBase());

    ec1.setShort("Primitive Type " +type.getCode());
    ec1.setDefinition(type.getDefinition());
    ec1.setComments(type.getComment());
    ec1.setMin(0);
    ec1.setMax("*");
    ec1.getType().add(new TypeRefComponent().setCode("Element"));

    ElementDefinition ec2 = new ElementDefinition();
    p.getDifferential().getElement().add(ec2);
    ec2.setPath(type.getBase()+".value");
    ec2.addRepresentation(PropertyRepresentation.XMLATTR);

    ec2.setShort("Primitive value for " +type.getCode());
    ec2.setDefinition("Primitive value for " +type.getCode());
    ec2.setMin(0);
    ec2.setMax("1");
    ec2.getFormatCommentsPre().add("Note: not have an assigned type\r\n        The actual value domain is assumed to be known by magic by\r\n        reading the spec and from the name of the primitive type");
    ec2.getFormatCommentsPre().add("Schema Type: "+type.getSchema());
    if (!Utilities.noString(type.getRegex()))
      ec2.getFormatCommentsPre().add("Regex: "+type.getRegex());

    reset();
    // now. the snapshot
    p.setSnapshot(new StructureDefinitionSnapshotComponent());
    ElementDefinition ecA = new ElementDefinition();
    p.getSnapshot().getElement().add(ecA);
    ecA.setPath(type.getBase());

    ecA.setShort("Primitive Type " +type.getCode());
    ecA.setDefinition(type.getDefinition());
    ecA.setComments(type.getComment());
    ecA.getType().add(new TypeRefComponent().setCode("Element"));
    ecA.setMin(0);
    ecA.setMax("*");
    ecA.getBase().setPath(type.getBase());
    ecA.getBase().setMin(0);
    ecA.getBase().setMax("*");
//    generateElementDefinition(ecA, null);

    makeExtensionSlice("extension", p, p.getSnapshot(), null, type.getBase());


    ElementDefinition ecB = new ElementDefinition();
    p.getSnapshot().getElement().add(ecB);
    ecB.setPath(type.getBase()+".value");
    ecB.addRepresentation(PropertyRepresentation.XMLATTR);

    ecB.setDefinition("Primitive value for " +type.getCode());
    ecB.setShort("Primitive value for " +type.getCode());
    ecB.setMin(0);
    ecB.setMax("1");
    ecB.getBase().setPath(type.getBase()+".value");
    ecB.getBase().setMin(0);
    ecB.getBase().setMax("1");
    ecB.getFormatCommentsPre().add("Note: not have an assigned type\r\n        The actual value domain is assumed to be known by magic by\r\n        reading the spec and from the name of the primitive type");
    ecB.getFormatCommentsPre().add("Schema Type: "+type.getSchema());
    if (!Utilities.noString(type.getRegex()))
      ecB.getFormatCommentsPre().add("Regex: "+type.getRegex());
    generateElementDefinition(ecB, ecA);

    containedSlices.clear();

//    XhtmlNode div = new XhtmlNode(NodeType.Element, "div");
//    div.addText("to do");
//    p.setText(new Narrative());
//    p.getText().setStatus(NarrativeStatus.GENERATED);
//    p.getText().setDiv(div);
    checkHasTypes(p);
    return p;
  }

  public StructureDefinition generate(TypeDefn t) throws Exception {
    StructureDefinition p = new StructureDefinition();
    p.setId(t.getName());
    p.setUrl("http://hl7.org/fhir/StructureDefinition/"+ t.getName());
    p.setKind(StructureDefinitionKind.DATATYPE);
    p.setAbstract(t.getName().equals("Element"));
    p.setUserData("filename", t.getName().toLowerCase());
    p.setUserData("path", "datatypes.html#"+t.getName());
    if (!Utilities.noString(t.typeCode()))
      p.setBase("http://hl7.org/fhir/StructureDefinition/Element"); // all deemd to be element whether they say Type or Structure

    ToolResourceUtilities.updateUsage(p, "core");
    p.setName(t.getName());
    p.setPublisher("HL7 FHIR Standard");
    p.addContact().getTelecom().add(Factory.newContactPoint(ContactPointSystem.OTHER, "http://hl7.org/fhir"));
    p.setDescription("Base StructureDefinition for "+t.getName()+" Type");
    p.setRequirements(t.getRequirements());
    p.setDate(genDate.getTime());
    p.setStatus(ConformanceResourceStatus.fromCode("draft")); // DSTU


    Set<String> containedSlices = new HashSet<String>();

    // first, the differential
    p.setDifferential(new StructureDefinitionDifferentialComponent());
    defineElement(null, p, p.getDifferential().getElement(), t, t.getName(), containedSlices, new ArrayList<ProfileGenerator.SliceHandle>(), SnapShotMode.None, true);

    reset();
    // now. the snapshot
    p.setSnapshot(new StructureDefinitionSnapshotComponent());
    defineElement(null, p, p.getSnapshot().getElement(), t, t.getName(), containedSlices, new ArrayList<ProfileGenerator.SliceHandle>(), SnapShotMode.DataType, true);
    for (ElementDefinition ed : p.getSnapshot().getElement())
      generateElementDefinition(ed, getParent(ed, p.getSnapshot().getElement()));

    containedSlices.clear();

    p.getDifferential().getElement().get(0).getType().clear();
    p.getSnapshot().getElement().get(0).getType().clear();
    if (!t.getName().equals("Element")) {
      p.getDifferential().getElement().get(0).addType().setCode("Element");
      p.getSnapshot().getElement().get(0).addType().setCode("Element");
    }

//    XhtmlNode div = new XhtmlNode(NodeType.Element, "div");
//    div.addText("to do");
//    p.setText(new Narrative());
//    p.getText().setStatus(NarrativeStatus.GENERATED);
//    p.getText().setDiv(div);
    checkHasTypes(p);
    return p;
  }

  public StructureDefinition generate(ProfiledType pt, List<ValidationMessage> issues) throws Exception {
    StructureDefinition p = new StructureDefinition();
    p.setId(pt.getName());
    p.setUrl("http://hl7.org/fhir/StructureDefinition/"+ pt.getName());
    p.setBase("http://hl7.org/fhir/StructureDefinition/"+pt.getBaseType());
    p.setKind(StructureDefinitionKind.DATATYPE);
    p.setConstrainedType(pt.getBaseType());
    p.setAbstract(false);
    p.setUserData("filename", pt.getName().toLowerCase());
    p.setUserData("path", "datatypes.html#"+pt.getName());

    ToolResourceUtilities.updateUsage(p, "core");
    p.setName(pt.getName());
    p.setPublisher("HL7 FHIR Standard");
    p.addContact().getTelecom().add(Factory.newContactPoint(ContactPointSystem.OTHER, "http://hl7.org/fhir"));
    p.setDescription("Base StructureDefinition for "+pt.getName()+" Resource");
    p.setDescription(pt.getDefinition());
    p.setDate(genDate.getTime());
    p.setStatus(ConformanceResourceStatus.fromCode("draft")); // DSTU

    // first, the differential
    p.setName(pt.getName());
    ElementDefinition e = new ElementDefinition();
    e.setPath(pt.getBaseType());
    e.setName(pt.getName());
    e.setShort(pt.getDefinition());
    e.setDefinition(pt.getDescription());
    e.setMin(1);
    e.setMax("1");
    e.setIsModifier(false);

    String s = definitions.getTLAs().get(pt.getName().toLowerCase());
    if (s == null)
      throw new Exception("There is no TLA for '"+pt.getName()+"' in fhir.ini");
    ElementDefinitionConstraintComponent inv = new ElementDefinitionConstraintComponent();
    inv.setKey(s+"-1");
    inv.setRequirements(pt.getInvariant().getRequirements());
    inv.setSeverity(ConstraintSeverity.ERROR);
    inv.setHuman(pt.getInvariant().getEnglish());
    inv.setXpath(pt.getInvariant().getXpath());
    e.getConstraint().add(inv);
    p.setDifferential(new StructureDefinitionDifferentialComponent());
    p.getDifferential().getElement().add(e);

    StructureDefinition base = getTypeSnapshot(pt.getBaseType());

    if (!pt.getRules().isEmpty()) {
      // need to generate a differential based on the rules. 
      // throw new Exception("todo");
      for (String rule : pt.getRules().keySet()) {
        String[] parts = rule.split("\\.");
        String value = pt.getRules().get(rule);
        ElementDefinition er = findElement(p.getDifferential(), pt.getBaseType()+'.'+parts[0]); 
        if (er == null) { 
          er = new ElementDefinition();
          er.setPath(pt.getBaseType()+'.'+parts[0]);
          p.getDifferential().getElement().add(er);
        }
        if (parts[1].equals("min"))
          er.setMin(Integer.parseInt(value));
        else if (parts[1].equals("max"))
          er.setMax(value);
        else if (parts[1].equals("defn"))
          er.setDefinition(value);

      }
      List<String> errors = new ArrayList<String>();
      new ProfileUtilities(context).sortDifferential(base, p, p.getName(), pkp, errors);
      for (String se : errors)
        issues.add(new ValidationMessage(Source.ProfileValidator, IssueType.STRUCTURE, -1, -1, p.getUrl(), se, IssueSeverity.WARNING));
    }

    reset();

    // now, the snapshot
    new ProfileUtilities(context).generateSnapshot(base, p, "http://hl7.org/fhir/StructureDefinition/"+pt.getBaseType(), p.getName(), pkp, issues);
    for (ElementDefinition ed : p.getSnapshot().getElement())
      generateElementDefinition(ed, getParent(ed, p.getSnapshot().getElement()));

    p.getDifferential().getElement().get(0).getType().clear();
    p.getDifferential().getElement().get(0).addType().setCode(pt.getBaseType());
    p.getSnapshot().getElement().get(0).getType().clear();
    p.getSnapshot().getElement().get(0).addType().setCode(pt.getBaseType());
//    XhtmlNode div = new XhtmlNode(NodeType.Element, "div");
//    div.addTag("h2").addText("Data type "+pt.getName());
//    div.addTag("p").addText(pt.getDefinition());
//    div.addTag("h3").addText("Rule");
//    div.addTag("p").addText(pt.getInvariant().getEnglish());
//    div.addTag("p").addText("XPath:");
//    div.addTag("blockquote").addTag("pre").addText(pt.getInvariant().getXpath());
//    p.setText(new Narrative());
//    p.getText().setStatus(NarrativeStatus.GENERATED);
//    p.getText().setDiv(div);
    checkHasTypes(p);
    return p;
  }

  private ElementDefinition findElement(StructureDefinitionDifferentialComponent differential, String path) {
    for (ElementDefinition ed : differential.getElement()) {
      if (ed.getPath().equals(path))
        return ed;
    }
    return null;
  }

  private ElementDefinition getParent(ElementDefinition e, List<ElementDefinition> elist) {
    String p = e.getPath();
    if (!p.contains("."))
      return null;
    p = p.substring(0, p.lastIndexOf("."));
    int i = elist.indexOf(e);
    i--;
    while (i > -1) {
      if (elist.get(i).getPath().equals(p)) {
        return elist.get(i);
      }
      i--;
    }
    return null;
  }

  private StructureDefinition getTypeSnapshot(String baseType) throws Exception {
    StructureDefinition p = definitions.getElementDefn(baseType).getProfile();
    if (p != null &&  p.hasSnapshot())
      return p;
    throw new Exception("Unable to find snapshot for "+baseType);
  }

  public StructureDefinition generate(Profile pack, ResourceDefn r, String usage) throws Exception {
    StructureDefinition p = new StructureDefinition();
    p.setId(r.getRoot().getName());
    p.setUrl("http://hl7.org/fhir/StructureDefinition/"+ r.getRoot().getName());
    p.setKind(StructureDefinitionKind.RESOURCE);
    p.setAbstract(r.isAbstract());
    p.setBase("http://hl7.org/fhir/StructureDefinition/"+r.getRoot().typeCode());
    p.setUserData("filename", r.getName().toLowerCase());
    p.setUserData("path", r.getName().toLowerCase()+".html");
    p.setDisplay(pack.metadata("display"));
    
    if (r.getFmmLevel() != null)
      ToolingExtensions.addIntegerExtension(p, ToolingExtensions.EXT_FMM_LEVEL, Integer.parseInt(r.getFmmLevel()));
    if (r.getFmmLevelNoWarnings() != null)
      ToolingExtensions.addIntegerExtension(p, ToolingExtensions.EXT_FMM_LEVEL_NO_WARN, Integer.parseInt(r.getFmmLevelNoWarnings()));
    ToolResourceUtilities.updateUsage(p, usage);
    p.setName(r.getRoot().getName());
    p.setPublisher("Health Level Seven International"+(r.getWg() == null ? "" : " ("+r.getWg().getName()+")"));
    p.addContact().getTelecom().add(Factory.newContactPoint(ContactPointSystem.OTHER, "http://hl7.org/fhir"));
    if (r.getWg() != null)
      p.addContact().getTelecom().add(Factory.newContactPoint(ContactPointSystem.OTHER, r.getWg().getUrl()));
    p.setDescription("Base StructureDefinition for "+r.getRoot().getName()+" Resource");
    p.setRequirements(r.getRoot().getRequirements());
    if (!p.hasRequirements())
      p.setRequirements(r.getRoot().getRequirements());
    p.setDate(genDate.getTime());
    p.setStatus(ConformanceResourceStatus.fromCode("draft")); // DSTU

    Set<String> containedSlices = new HashSet<String>();

    // first, the differential
    p.setDifferential(new StructureDefinitionDifferentialComponent());
    defineElement(null, p, p.getDifferential().getElement(), r.getRoot(), r.getRoot().getName(), containedSlices, new ArrayList<ProfileGenerator.SliceHandle>(), SnapShotMode.None, true);

    reset();
    // now. the snapshot
    p.setSnapshot(new StructureDefinitionSnapshotComponent());
    defineElement(null, p, p.getSnapshot().getElement(), r.getRoot(), r.getRoot().getName(), containedSlices, new ArrayList<ProfileGenerator.SliceHandle>(), SnapShotMode.Resource, true);
    for (ElementDefinition ed : p.getSnapshot().getElement())
      generateElementDefinition(ed, getParent(ed, p.getSnapshot().getElement()));

    List<String> names = new ArrayList<String>();
    names.addAll(r.getSearchParams().keySet());
    Collections.sort(names);
    for (String pn : names) {
      pack.getSearchParameters().add(makeSearchParam(p, r.getName()+"-"+pn.replace("_", ""), r.getName(), r.getSearchParams().get(pn)));
    }
    containedSlices.clear();

    if (r.getName().equals("Resource")) {
      p.getDifferential().getElement().get(0).getType().clear();
      p.getSnapshot().getElement().get(0).getType().clear();
    } else {
      p.getDifferential().getElement().get(0).getType().clear();
      p.getDifferential().getElement().get(0).addType().setCode(r.getRoot().typeCode());
      p.getSnapshot().getElement().get(0).getType().clear();
      p.getSnapshot().getElement().get(0).addType().setCode(r.getRoot().typeCode());
    }
//    XhtmlNode div = new XhtmlNode(NodeType.Element, "div");
//    div.addText("to do");
//    p.setText(new Narrative());
//    p.getText().setStatus(NarrativeStatus.GENERATED);
//    p.getText().setDiv(div);
    checkHasTypes(p);
    return p;
  }

  private void reset() {
    paths.clear();
    pathNames.clear();  
  }

  public StructureDefinition generate(Profile pack, ConstraintStructure profile, ResourceDefn resource, String id, ImplementationGuideDefn usage, List<ValidationMessage> issues) throws Exception {

    try {
      return generate(pack, profile, resource, id, null, usage, issues);
    } catch (Exception e) {
      throw new Exception("Error processing profile '"+id+"': "+e.getMessage(), e);
    }
  }

  public StructureDefinition generate(Profile pack, ConstraintStructure profile, ResourceDefn resource, String id, String html, ImplementationGuideDefn usage, List<ValidationMessage> issues) throws Exception {
    if (profile.getResource() != null)
      return profile.getResource();

    StructureDefinition p = new StructureDefinition();
    p.setId(FormatUtilities.makeId(id));
    p.setUrl("http://hl7.org/fhir/StructureDefinition/"+ id);
    if (usage != null && !usage.isCore()) {
      if (!id.startsWith(usage.getCode()+"-"))
        throw new Exception("Error: "+id+" must start with "+usage.getCode()+"-");
    }

    if (!resource.getRoot().getTypes().isEmpty() && (resource.getRoot().getTypes().get(0).getProfile() != null))
      p.setBase(resource.getRoot().getTypes().get(0).getProfile());
    else
      p.setBase("http://hl7.org/fhir/StructureDefinition/"+resource.getName());    
    p.setKind(StructureDefinitionKind.RESOURCE);
    p.setConstrainedType(resource.getName());    
    p.setAbstract(false);
    p.setUserData("filename", id);
    p.setUserData("path", ((usage == null || usage.isCore()) ? "" : usage.getCode()+File.separator)+id+".html");
    p.setDisplay(pack.metadata("display"));


    ToolResourceUtilities.updateUsage(p, usage.getCode());
    p.setName(pack.metadata("name"));
    p.setPublisher(pack.metadata("author.name"));
    if (pack.hasMetadata("author.reference"))
      p.addContact().getTelecom().add(Factory.newContactPoint(ContactPointSystem.OTHER, pack.metadata("author.reference")));
    //  <code> opt Zero+ Coding assist with indexing and finding</code>
    p.setDescription(resource.getRoot().getShortDefn());    
    if (!p.hasDescriptionElement() && pack.hasMetadata("description"))
      p.setDescription(pack.metadata("description"));
    p.setRequirements(resource.getRoot().getRequirements());
    if (!p.hasRequirements() && pack.hasMetadata("requirements"))
      p.setRequirements(pack.metadata("requirements"));

    if (pack.hasMetadata("date"))
      p.setDateElement(Factory.newDateTime(pack.metadata("date").substring(0, 10)));
    else
      p.setDate(genDate.getTime());

    if (pack.hasMetadata("status")) 
      p.setStatus(ConformanceResourceStatus.fromCode(pack.metadata("status")));
    if (pack.getMetadata().containsKey("code"))
      for (String s : pack.getMetadata().get("code")) 
        if (!Utilities.noString(s))
          p.getCode().add(Factory.makeCoding(s));

    if (pack.hasMetadata("datadictionary"))
      ToolingExtensions.setStringExtension(p, "http://hl7.org/fhir/StructureDefinition/datadictionary", pack.metadata("datadictionary"));

    Set<String> containedSlices = new HashSet<String>();

    p.setDifferential(new StructureDefinitionDifferentialComponent());
    defineElement(pack, p, p.getDifferential().getElement(), resource.getRoot(), resource.getName(), containedSlices, new ArrayList<ProfileGenerator.SliceHandle>(), SnapShotMode.None, true);
    List<String> names = new ArrayList<String>();
    names.addAll(resource.getSearchParams().keySet());
    Collections.sort(names);
    for (String pn : names) {
      pack.getSearchParameters().add(makeSearchParam(p, pack.getId()+"-"+resource.getName()+"-"+pn, resource.getName(), resource.getSearchParams().get(pn)));
    }
    StructureDefinition base = definitions.getSnapShotForBase(p.getBase());

    List<String> errors = new ArrayList<String>();
    new ProfileUtilities(context).sortDifferential(base, p, p.getName(), pkp, errors);
    for (String s : errors)
      issues.add(new ValidationMessage(Source.ProfileValidator, IssueType.STRUCTURE, -1, -1, p.getUrl(), s, IssueSeverity.WARNING));
    reset();
    // ok, c is the differential. now we make the snapshot
    new ProfileUtilities(context).generateSnapshot(base, p, "http://hl7.org/fhir/StructureDefinition/"+p.getConstrainedType(), p.getName(), pkp, issues);
    reset();

    p.getDifferential().getElement().get(0).getType().clear();
    p.getDifferential().getElement().get(0).addType().setCode(p.getSnapshot().getElement().get(0).getPath());
    p.getSnapshot().getElement().get(0).getType().clear();
    p.getSnapshot().getElement().get(0).addType().setCode(p.getSnapshot().getElement().get(0).getPath());

//    XhtmlNode div = new XhtmlNode(NodeType.Element, "div");
//    div.addText("to do");
//    p.setText(new Narrative());
//    p.getText().setStatus(NarrativeStatus.GENERATED);
//    p.getText().setDiv(div);
    checkHasTypes(p);
    return p;
  }

  private SearchParamType getSearchParamType(SearchParameterDefn.SearchType type) {
    switch (type) {
    case number:
      return SearchParamType.NUMBER;
    case string:
      return SearchParamType.STRING;
    case date:
      return SearchParamType.DATE;
    case reference:
      return SearchParamType.REFERENCE;
    case token:
      return SearchParamType.TOKEN;
    case uri:
      return SearchParamType.URI;
    case composite:
      return SearchParamType.COMPOSITE;
    case quantity:
      return SearchParamType.QUANTITY;
    }
    return null;
  }

  public SearchParameter makeSearchParam(StructureDefinition p, String id, String rn, SearchParameterDefn spd) throws Exception  {
    SearchParameter sp = new SearchParameter();
    sp.setId(id.replace("[", "").replace("]", ""));
    sp.setUrl("http://hl7.org/fhir/SearchParameter/"+sp.getId());
    if (context.getSearchParameters().containsKey(sp.getUrl()))
      throw new Exception("Duplicated Search Parameter "+sp.getUrl());
    context.getSearchParameters().put(sp.getUrl(), sp);

    sp.setName(spd.getCode());
    sp.setCode(spd.getCode());
    sp.setDate(genDate.getTime());
    sp.setPublisher(p.getPublisher());
    definitions.addNs(sp.getUrl(), "Search Parameter: "+sp.getName(), rn.toLowerCase()+".html#search");
    for (StructureDefinitionContactComponent tc : p.getContact()) {
      SearchParameterContactComponent t = sp.addContact();
      t.setNameElement(tc.getNameElement().copy());
      for (ContactPoint ts : tc.getTelecom())
        t.getTelecom().add(ts.copy());
    }
    if (p.hasConstrainedType())
      sp.setBase(p.getConstrainedType());
    else
      sp.setBase(p.getName());
    if (!definitions.hasResource(sp.getBase()) && !sp.getBase().equals("Resource"))
      throw new Exception("unknown resource type "+sp.getBase());
    
    sp.setType(getSearchParamType(spd.getType()));
    sp.setDescription(spd.getDescription());
    String xpath = spd.getXPath();
    if (xpath != null) {
      if (xpath.contains("[x]"))
        xpath = convertToXpath(xpath);
      sp.setXpath(xpath);
      sp.setXpathUsage(spd.getxPathUsage());
    }

    for(String target : spd.getTargets()) {
      if("Any".equals(target) == true) {   	  
        for(String resourceName : definitions.sortedResourceNames())
          sp.addTarget(resourceName);
      }
      else
        sp.addTarget(target);
    }

    return sp;
  }


  private String convertToXpath(String xpath) {
    String[] parts = xpath.split("\\/");
    StringBuilder b = new StringBuilder();
    boolean first = true;
    for (String p : parts) {
      if (first)
        first = false;
      else
        b.append("/");
      if (p.startsWith("f:")) {
        String v = p.substring(2);
        if (v.endsWith("[x]"))
          b.append("*[starts-with(local-name(.), '"+v.replace("[x]", "")+"')]");
        else
          b.append(p);
      }        
      else
        b.append(p);
    }
    return b.toString();
  }

  private ElementDefinitionBindingComponent generateBinding(BindingSpecification src) throws Exception {
    if (src == null)
      return null;

    ElementDefinitionBindingComponent dst = new ElementDefinitionBindingComponent();
    dst.setStrength(src.getStrength());    
    dst.setDescription(src.getDefinition());
    if (src.getBinding() != BindingMethod.Unbound)
      dst.setValueSet(buildReference(src));    
    return dst;
  }

  private Type buildReference(BindingSpecification src) throws Exception {
    switch (src.getBinding()) {
    case Unbound: return null;
    case CodeList:
      if (src.getValueSet()!= null)
        return Factory.makeReference(src.getValueSet().getUrl());
      else if (src.getReference().startsWith("#"))
        return Factory.makeReference("http://hl7.org/fhir/ValueSet/"+src.getReference().substring(1));
      else
        throw new Exception("not done yet");
    case ValueSet: 
      if (!Utilities.noString(src.getReference()))
        if (src.getReference().startsWith("http"))
          return Factory.makeReference(src.getReference());
        else if (src.getValueSet()!= null)
          return Factory.makeReference(src.getValueSet().getUrl());
        else if (src.getReference().startsWith("valueset-"))
          return Factory.makeReference("http://hl7.org/fhir/ValueSet/"+src.getReference().substring(9));
        else
          return Factory.makeReference("http://hl7.org/fhir/ValueSet/"+src.getReference());
      else
        return null; // throw new Exception("not done yet");
    case Reference: return Factory.newUri(src.getReference());
    case Special: 
      return Factory.makeReference("http://hl7.org/fhir/ValueSet/"+src.getReference().substring(1));
    default: 
      throw new Exception("not done yet");
    }
  }

  /**
   * note: snapshot implies that we are generating a resource or a data type; for other profiles, the snapshot is generated elsewhere
   */
  private ElementDefinition defineElement(Profile ap, StructureDefinition p, List<ElementDefinition> elements, ElementDefn e, String path, Set<String> slices, List<SliceHandle> parentSlices, SnapShotMode snapshot, boolean root) throws Exception 
  {
    ElementDefinition ce = new ElementDefinition();
    elements.add(ce);

    ce.setPath(path);

    if (e.isXmlAttribute())
      ce.addRepresentation(PropertyRepresentation.XMLATTR);
    List<SliceHandle> myParents = new ArrayList<ProfileGenerator.SliceHandle>();
    myParents.addAll(parentSlices);

    // If this element has a profile name, and this is the first of the
    // slicing group, add a slicing group "entry" (= first slice member,
    // which holds Slicing information)
    if (!Utilities.noString(e.getProfileName())) {
      if (e.getDiscriminator().size() > 0 && !slices.contains(path)) {
        ce.setSlicing(new ElementDefinitionSlicingComponent());
        ce.getSlicing().setDescription(e.getSliceDescription());
        String[] d = e.getDiscriminator().get(0).split("\\|");
        if (d.length >= 1)
          ce.getSlicing().addDiscriminator(d[0].trim());
        if (d.length >= 2)
          ce.getSlicing().setOrdered(Boolean.parseBoolean(d[1].trim()));
        else
          ce.getSlicing().setOrdered(false);
        if (d.length >= 3)
          ce.getSlicing().setRules(SlicingRules.fromCode(d[2].trim()));
        else
          ce.getSlicing().setRules(SlicingRules.OPEN);
        for (int i = 1; i < e.getDiscriminator().size(); i++) { // we've already process the first in the list
          String s = e.getDiscriminator().get(i).trim();
          if (s.contains("|"))
            throw new Exception("illegal discriminator \""+s+"\" at "+path);
          ce.getSlicing().addDiscriminator(s);
        }
        //        ce = new ElementDefinition();
        //        elements.add(ce);
        ce.setPath(path);
        slices.add(path);
      }
      SliceHandle hnd = new SliceHandle();
      hnd.name = path; // though this it not used?
      myParents.add(hnd);
      ce.setName(e.getProfileName());
    }
    addToPaths(myParents, path, ce, p.getName());

    if (!Utilities.noString(e.getComments()))
      ce.setComments(e.getComments());
    if (!Utilities.noString(e.getShortDefn()))
      ce.setShort(e.getShortDefn());
    if (!Utilities.noString(e.getDefinition())) {
      ce.setDefinition(e.getDefinition());
      if (!Utilities.noString(e.getShortDefn()))
        ce.setShort(e.getShortDefn());
    }
    if (path.contains(".") && !Utilities.noString(e.getRequirements())) 
      ce.setRequirements(e.getRequirements());
    if (e.hasMustSupport())
      ce.setMustSupport(e.isMustSupport());

    if (!Utilities.noString(e.getStatedType()))
      ToolingExtensions.addStringExtension(ce, "http://hl7.org/fhir/StructureDefinition/structuredefinition-explicit-type-name", e.getStatedType());

    if (e.getMaxLength() != null) 
      ce.setMax(e.getMaxLength()); 

    // no purpose here
    if (e.getMinCardinality() != null)
      ce.setMin(e.getMinCardinality());
    if (e.getMaxCardinality() != null)
      ce.setMax(e.getMaxCardinality() == Integer.MAX_VALUE ? "*" : e.getMaxCardinality().toString());

    // we don't know mustSupport here
    if (e.hasModifier())
      ce.setIsModifier(e.isModifier());

    if (!root) {
      if (e.typeCode().startsWith("@"))  {
        ce.setNameReference(getNameForPath(myParents, e.typeCode().substring(1)));
      } else {
        List<TypeRef> expandedTypes = new ArrayList<TypeRef>();
        for (TypeRef t : e.getTypes()) {
          // Expand any Resource(A|B|C) references
          if(t.hasParams() && !"Reference".equals(t.getName())) {
            throw new Exception("Only resource types can specify parameters.  Path " + path + " in profile " + p.getName());
          }
          if(t.getParams().size() > 1)
          {
            if (t.getProfile() != null && t.getParams().size() !=1) {
              throw new Exception("Cannot declare profile on a resource reference declaring multiple resource types.  Path " + path + " in profile " + p.getName());
            }
            for(String param : t.getParams()) {
              TypeRef childType = new TypeRef(t.getName());
              childType.getParams().add(param);
              childType.getAggregations().addAll(t.getAggregations());
              expandedTypes.add(childType);
            }
          } else if (t.isWildcardType()) {
            // this list is filled out manually because it may be running before the types rerred to have been loaded
            expandedTypes.add(new TypeRef("boolean"));
            expandedTypes.add(new TypeRef("integer"));
            expandedTypes.add(new TypeRef("decimal"));
            expandedTypes.add(new TypeRef("base64Binary"));
            expandedTypes.add(new TypeRef("instant"));
            expandedTypes.add(new TypeRef("string"));
            expandedTypes.add(new TypeRef("uri"));
            expandedTypes.add(new TypeRef("date"));
            expandedTypes.add(new TypeRef("dateTime"));
            expandedTypes.add(new TypeRef("time"));
            expandedTypes.add(new TypeRef("code"));
            expandedTypes.add(new TypeRef("oid"));
            expandedTypes.add(new TypeRef("id"));
            expandedTypes.add(new TypeRef("unsignedInt"));
            expandedTypes.add(new TypeRef("positiveInt"));
            expandedTypes.add(new TypeRef("markdown"));
            expandedTypes.add(new TypeRef("Annotation"));
            expandedTypes.add(new TypeRef("Attachment"));
            expandedTypes.add(new TypeRef("Identifier"));
            expandedTypes.add(new TypeRef("CodeableConcept"));
            expandedTypes.add(new TypeRef("Coding"));
            expandedTypes.add(new TypeRef("Quantity"));
            expandedTypes.add(new TypeRef("Range"));
            expandedTypes.add(new TypeRef("Period"));
            expandedTypes.add(new TypeRef("Ratio"));
            expandedTypes.add(new TypeRef("SampledData"));
            expandedTypes.add(new TypeRef("Signature"));
            expandedTypes.add(new TypeRef("HumanName"));
            expandedTypes.add(new TypeRef("Address"));
            expandedTypes.add(new TypeRef("ContactPoint"));
            expandedTypes.add(new TypeRef("Timing"));
            expandedTypes.add(new TypeRef("Reference"));
            expandedTypes.add(new TypeRef("Meta"));
          } else if (!t.getName().startsWith("=")) {
            if (definitions.isLoaded() && (!definitions.hasResource(t.getName()) && !definitions.hasType(t.getName()) 
                && !definitions.hasElementDefn(t.getName()) && !definitions.getBaseResources().containsKey(t.getName()) &&!t.getName().equals("xhtml") )) {
              throw new Exception("Bad Type '"+t.getName()+"' at "+path+" in profile "+p.getUrl());
            }
            expandedTypes.add(t);
          }
        }
        if (expandedTypes.isEmpty()) {
          if (snapshot != SnapShotMode.None)
            ce.addType().setCode(snapshot == SnapShotMode.DataType ? "Element" : "BackboneElement");
        } else for (TypeRef t : expandedTypes) {
          TypeRefComponent type = new TypeRefComponent();
          String profile = null;
          if (definitions.getConstraints().containsKey(t.getName())) {
            ProfiledType pt = definitions.getConstraints().get(t.getName());
            type.setCode(pt.getBaseType());
            profile = "http://hl7.org/fhir/StructureDefinition/"+pt.getName();
          } else {
            type.setCode(t.getName());
            profile = t.getProfile();
          }
          if (profile == null && t.hasParams()) {
            profile = t.getParams().get(0);
          }
          if (profile != null) {
            if (type.getCode().equals("Extension")) {
              // check that the extension is being used correctly:
              StructureDefinition ext = context.getExtensionStructure(null, profile);
              if (ext == null) {
                throw new Exception("Unable to resolve extension definition: " + profile);
              }
              boolean srcMod = ext.getSnapshot().getElement().get(0).getIsModifier();
              boolean tgtMod = e.isModifier();
              if (srcMod && !tgtMod)
                throw new Exception("The extension '"+profile+"' is a modifier extension, but is being used as if it is not a modifier extension");
              if (!srcMod && tgtMod)
                throw new Exception("The extension '"+profile+"' is not a modifier extension, but is being used as if it is a modifier extension");
            }
            if (profile.startsWith("http:") || profile.startsWith("#")) {
              type.addProfile(profile);
            } else {
              type.addProfile("http://hl7.org/fhir/StructureDefinition/" + (profile.equals("Any") ? "Resource" : profile));
            }
          }

          for (String aggregation : t.getAggregations()) {
            type.addAggregation(AggregationMode.fromCode(aggregation));
          }	      	

          ce.getType().add(type);
        }
      }
    }
    // ce.setConformance(getType(e.getConformance()));
    for (Invariant id : e.getStatedInvariants()) 
      ce.addCondition(id.getId());

    ce.setFixed(e.getFixed());
    ce.setPattern(e.getPattern());
    ce.setDefaultValue(e.getDefaultValue());
    ce.setMeaningWhenMissing(e.getMeaningWhenMissing());
    ce.setExample(e.getExample());
    for (String s : e.getAliases())
      ce.addAlias(s);

    if (e.hasSummaryItem())
      ce.setIsSummaryElement(Factory.newBoolean(e.isSummary()));

    for (String n : definitions.getMapTypes().keySet()) {
      addMapping(p, ce, n, e.getMapping(n), null);
    }
    if (ap != null) {
      for (String n : ap.getMappingSpaces().keySet()) {
        addMapping(p, ce, n, e.getMapping(n), ap);
      }
    }
    if (e.getW5() != null)
      addMapping(p, ce, "http://hl7.org/fhir/w5", e.getW5(), ap);
    ToolingExtensions.addDisplayHint(ce, e.getDisplayHint());

    for (String in : e.getInvariants().keySet()) {
      ElementDefinitionConstraintComponent con = new ElementDefinitionConstraintComponent();
      Invariant inv = e.getInvariants().get(in);
      con.setKey(inv.getId());
      if (!con.hasKey()) {
        profileCounter++;
        con.setKey("prf-"+Integer.toString(profileCounter ));
      }
      con.setRequirements(inv.getRequirements());
      if (Utilities.noString(inv.getSeverity()))
        con.setSeverity(ConstraintSeverity.ERROR);
      else
        con.setSeverity(ConstraintSeverity.fromCode(inv.getSeverity()));
      con.setHuman(inv.getEnglish());
      con.setXpath(inv.getXpath());
      ce.getConstraint().add(con);
    }
    // we don't have anything to say about constraints on resources

    if (e.hasBinding()) {
      ce.setBinding(generateBinding(e.getBinding()));
    }

    if (snapshot != SnapShotMode.None && !e.getElements().isEmpty()) {    
      //      makeExtensionSlice("extension", p, c, e, path);
      //      if (snapshot == SnapShotMode.Resource) { 
      //        makeExtensionSlice("modifierExtension", p, c, e, path);

      //        if (!path.contains(".")) {
      //          c.getElement().add(createBaseDefinition(p, path, definitions.getBaseResources().get("Resource").getRoot().getElementByName("language")));
      //          c.getElement().add(createBaseDefinition(p, path, definitions.getBaseResources().get("DomainResource").getRoot().getElementByName("text")));
      //          c.getElement().add(createBaseDefinition(p, path, definitions.getBaseResources().get("DomainResource").getRoot().getElementByName("contained")));
      //        }
      //      }
    }
    Set<String> containedSlices = new HashSet<String>();
    if (snapshot != SnapShotMode.None) {
      if (!root && Utilities.noString(e.typeCode())) {
        if (snapshot == SnapShotMode.Resource)
          defineAncestorElements("BackboneElement", path, snapshot, containedSlices, p, elements);
        else
          defineAncestorElements("Element", path, snapshot, containedSlices, p, elements);
      } else if (root && !Utilities.noString(e.typeCode())) 
        defineAncestorElements(e.typeCode(), path, snapshot, containedSlices, p, elements);
    }
    for (ElementDefn child : e.getElements()) 
      defineElement(ap, p, elements, child, path+"."+child.getName(), containedSlices, myParents, snapshot, false);

    return ce;
  }

  private String actualTypeName(String type) {
    if (type.equals("Type"))
      return "Element";
    if (type.equals("Structure"))
      return "Element";
    return type;
  }
  private void defineAncestorElements(String type, String path, SnapShotMode snapshot, Set<String> containedSlices, StructureDefinition p, List<ElementDefinition> elements) throws Exception {
    ElementDefn e = definitions.getElementDefn(actualTypeName(type));
    if (!Utilities.noString(e.typeCode()))
      defineAncestorElements(e.typeCode(), path, snapshot, containedSlices, p, elements);

    for (ElementDefn child : e.getElements()) 
      defineElement(null, p, elements, child, path+"."+child.getName(), containedSlices, new ArrayList<ProfileGenerator.SliceHandle>(), snapshot, false);

  }

  /*
   *     // resource
    // domain resource
    for (ElementDefn child : definitions.getBaseResources().get("DomainResource").getRoot().getElements()) 
      defineElement(null, p, p.getSnapshot(), child, r.getRoot().getName()+"."+child.getName(), containedSlices, new ArrayList<ProfileGenerator.SliceHandle>(), SnapShotMode.Resource);

   */
  /*
  private String registerMapping(ConformancePackage ap, StructureDefinition p, String m) {
    for (StructureDefinitionMappingComponent map : p.getMapping()) {
      if (map.getUri().equals(m))
        return map.getIdentity();
    }
    StructureDefinitionMappingComponent map = new StructureDefinitionMappingComponent();
    MappingSpace space = definitions.getMapTypes().get(m);
    if (space != null)
      map.setIdentity(space.getId());
    else
      map.setIdentity("m" + Integer.toString(p.getMapping().size()+1));
    map.setUri(m);
    String name = ap.metadata(m+"-name");
    if (Utilities.noString(name) && space != null)
      name = space.getTitle();
    if (!Utilities.noString(name))
      map.setName(name);
    String comments = ap.metadata(m+"-comments");
    if (Utilities.noString(comments) && space != null)
        comments = space.getPreamble();
    if (!Utilities.noString(comments))
      map.setComments(comments);
    return map.getIdentity();
  }
   */

  private void addToPaths(List<SliceHandle> myParents, String path, ElementDefinition ce, String profileName) throws Exception {
    Map<String, ElementDefinition> pmap = paths;
    if (!myParents.isEmpty())
      pmap = myParents.get(myParents.size()-1).paths;
    if (pmap.containsKey(path))
      throw new Exception("duplicate path "+path+" on profile "+profileName);
    pmap.put(path, ce);   
  }

  private String getNameForElement(ElementDefinition ce) throws Exception {
    if (ce.getName() == null) {
      String name = tail(ce.getPath());
      if (pathNames.contains(name))
        throw new Exception("Need to improve name generation algorithm (name = "+name+", on path = "+ce.getPath()+")");
      pathNames.add(name);
      ce.setName(name);
    }
    return ce.getName();
  }

  private String getNameForPath(List<SliceHandle> myParents, String path) throws Exception {
    for (int i = myParents.size()-1; i >= 0; i--) {
      Map<String, ElementDefinition> pmap = myParents.get(i).paths;;
      if (pmap.containsKey(path))
        return getNameForElement(pmap.get(path));
    }
    Map<String, ElementDefinition> pmap = paths;
    if (pmap.containsKey(path))
      return getNameForElement(pmap.get(path));
    throw new Exception("Unable to find element for path "+path);  
  }

  private String tail(String path) {
    return path.contains(".") ? path.substring(path.lastIndexOf(".")+1) : path;
  }

  private void makeExtensionSlice(String extensionName, StructureDefinition p, StructureDefinitionSnapshotComponent c, ElementDefn e, String path) throws URISyntaxException, Exception {
    ElementDefinition ex = createBaseDefinition(p, path, definitions.getBaseResources().get("DomainResource").getRoot().getElementByName(extensionName));
    c.getElement().add(ex);
    ex.getBase().setPath(path+".extension");
    ex.getBase().setMin(0);
    ex.getBase().setMax("*");
  }

  private void addMapping(StructureDefinition p, ElementDefinition definition, String target, String map, Profile pack) {
    if (!Utilities.noString(map)) {
      String id;
      if (pack != null && pack.getMappingSpaces().containsKey(target))
        id = pack.getMappingSpaces().get(target).getId();
      else
        id = definitions.getMapTypes().get(target).getId();

      if (!mappingExists(p, id)) {
        StructureDefinitionMappingComponent pm = new StructureDefinitionMappingComponent();
        p.getMapping().add(pm);
        pm.setIdentity(id);
        pm.setUri(target);
        if (pack != null && pack.getMappingSpaces().containsKey(target))
          pm.setName(pack.getMappingSpaces().get(target).getTitle());
        else
          pm.setName(definitions.getMapTypes().get(target).getTitle());
      }
      boolean found = false;
      for (ElementDefinitionMappingComponent m : definition.getMapping()) {
        found = found || (m.getIdentity().equals(id) && m.getMap().equals(map)); 
      }
      if (!found) {
        ElementDefinitionMappingComponent m = new ElementDefinitionMappingComponent();
        m.setIdentity(id);
        m.setMap(map);
        definition.getMapping().add(m);
      }
    }
  }


  private boolean mappingExists(StructureDefinition p, String id) {
    for (StructureDefinitionMappingComponent t : p.getMapping()) {
      if (id.equals(t.getIdentity()))
        return true;
    }
    return false;
  }

  private ElementDefinition createBaseDefinition(StructureDefinition p, String path, ElementDefn src) throws URISyntaxException {
    ElementDefinition ce = new ElementDefinition();
    ce.setPath(path+"."+src.getName());
    ce.setShort(src.getShortDefn());
    ce.setDefinition(src.getDefinition());
    ce.setComments(src.getComments());
    ce.setRequirements(src.getRequirements());
    for (String a : src.getAliases())
      ce.addAlias(a);
    ce.setMin(src.getMinCardinality());
    if (src.getMaxCardinality() != null)
      ce.setMax(src.getMaxCardinality() == Integer.MAX_VALUE ? "*" : src.getMaxCardinality().toString());
    ce.getType().add(new TypeRefComponent());
    ce.getType().get(0).setCode(src.typeCode());
    // this one should never be used
    if (!Utilities.noString(src.getTypes().get(0).getProfile()))
      ce.getType().get(0).addProfile(src.getTypes().get(0).getProfile());
    // todo? conditions, constraints, binding, mapping
    if (src.hasModifier())
      ce.setIsModifier(src.isModifier());
    if (src.hasSummaryItem())
      ce.setIsSummaryElement(Factory.newBoolean(src.isSummary()));
    for (Invariant id : src.getStatedInvariants()) 
      ce.addCondition(id.getId());
    return ce;
  }

  public ConstraintStructure wrapProfile(StructureDefinition profile) throws Exception {
    return new ConstraintStructure(profile, definitions.getUsageIG((String) profile.getUserData(ToolResourceUtilities.NAME_SPEC_USAGE), "generating profile "+profile.getId()));
  }

  public void convertElements(ElementDefn src, StructureDefinition ed, String path) throws Exception {
    ElementDefinition dst = new ElementDefinition();
    if (!ed.hasDifferential())
      ed.setDifferential(new StructureDefinitionDifferentialComponent());
    ed.getDifferential().getElement().add(dst);
    String thisPath = path == null ? "Extension" : path;
    dst.setPath(thisPath);
    if (dst.getPath().endsWith(".extension"))
      dst.setName(src.getName());

    dst.setShort(src.getShortDefn());
    dst.setDefinition(src.getDefinition());
    dst.setComments(src.getComments());
    if (src.getMaxCardinality() != null) {
      if (src.getMaxCardinality() == Integer.MAX_VALUE)
        dst.setMax("*");
      else
        dst.setMax(src.getMaxCardinality().toString());
    }
    if (src.getMinCardinality() != null)
      dst.setMin(src.getMinCardinality());
    if (src.getFixed() != null)
      dst.setFixed(src.getFixed());
    if (src.hasMustSupport())
      dst.setMustSupport(src.isMustSupport());
    if (src.hasModifier())
      dst.setIsModifier(src.isModifier());
    if (src.hasSummaryItem())
      dst.setIsSummaryElement(Factory.newBoolean(src.isSummary()));
    for (Invariant id : src.getStatedInvariants()) 
      dst.addCondition(id.getId());

    // dDst.
    for (TypeRef t : src.getTypes()) {
      if (t.hasParams()) {
        for (String tp : t.getParams()) {
          ElementDefinition.TypeRefComponent type = new ElementDefinition.TypeRefComponent();
          type.setCode(t.getName());
          if (t.hasProfile())
            type.addProfile(t.getProfile()); // this should only happen if t.getParams().size() == 1
          else
            type.addProfile("http://hl7.org/fhir/StructureDefinition/"+(tp.equals("Any") ? "Resource" : tp));
          dst.getType().add(type);
        }
      } else if (t.isWildcardType()) {
        dst.addType().setCode("boolean");
        dst.addType().setCode("integer");
        dst.addType().setCode("decimal");
        dst.addType().setCode("base64Binary");
        dst.addType().setCode("instant");
        dst.addType().setCode("string");
        dst.addType().setCode("uri");
        dst.addType().setCode("date");
        dst.addType().setCode("dateTime");
        dst.addType().setCode("time");
        dst.addType().setCode("code");
        dst.addType().setCode("oid");
        dst.addType().setCode("id");
        dst.addType().setCode("unsignedInt");
        dst.addType().setCode("positiveInt");
        dst.addType().setCode("markdown");
        dst.addType().setCode("Annotation");
        dst.addType().setCode("Attachment");
        dst.addType().setCode("Identifier");
        dst.addType().setCode("CodeableConcept");
        dst.addType().setCode("Coding");
        dst.addType().setCode("Quantity");
        dst.addType().setCode("Range");
        dst.addType().setCode("Period");
        dst.addType().setCode("Ratio");
        dst.addType().setCode("SampledData");
        dst.addType().setCode("Signature");
        dst.addType().setCode("HumanName");
        dst.addType().setCode("Address");
        dst.addType().setCode("ContactPoint");
        dst.addType().setCode("Timing");
        dst.addType().setCode("Reference");
        dst.addType().setCode("Meta");
      } else {
        ElementDefinition.TypeRefComponent type = new ElementDefinition.TypeRefComponent();
        if (definitions != null && definitions.getConstraints().containsKey(t.getName())) {
         ProfiledType ct = definitions.getConstraints().get(t.getName());
         type.setCode(ct.getBaseType());
         type.addProfile("http://hl7.org/fhir/StructureDefinition/"+ct.getName());
        } else {
          type.setCode(t.getName());
          if (t.hasProfile())
            type.addProfile(t.getProfile());
        }
        dst.getType().add(type);
      }
    }
    if (definitions != null) { // igtodo - catch this
      for (String mu : definitions.getMapTypes().keySet()) {
        if (src.hasMapping(mu)) {
          addMapping(ed, dst, mu, src.getMapping(mu), null);
        }
      }
    }
    for (String in : src.getInvariants().keySet()) {
      ElementDefinitionConstraintComponent con = new ElementDefinitionConstraintComponent();
      Invariant inv = src.getInvariants().get(in);
      con.setKey(inv.getId());
      if (!con.hasKey()) {
        extensionCounter++;
        con.setKey("exd-"+Integer.toString(extensionCounter));
      }
      con.setRequirements(inv.getRequirements());
      if (Utilities.noString(inv.getSeverity()))
        con.setSeverity(ConstraintSeverity.ERROR);
      else
        con.setSeverity(ConstraintSeverity.fromCode(inv.getSeverity()));
      con.setHuman(inv.getEnglish());
      con.setXpath(inv.getXpath());
      dst.getConstraint().add(con);
    }

    if (src.hasBinding())
      dst.setBinding(generateBinding(src.getBinding()));
    if (src.getElements().isEmpty()) {
      if (path == null)
        throw new Exception("?error parsing extension");
    } else {
      ElementDefn url = src.getElements().get(0);
      if (!url.getName().equals("url"))
        throw new Exception("first child of extension should be "+url);
      convertElements(url, ed, thisPath+".url");
      if (!hasValue(src)) {
        ElementDefn value = new ElementDefn();
        value.setName("value[x]");
        value.setMinCardinality(0);
        value.setMaxCardinality(0);
        src.getElements().add(value);
      } 
      if (src.getElements().size() == 2 && 
          src.getElements().get(0).getName().equals("url") &&
          src.getElements().get(1).getName().equals("value[x]")) {
        ElementDefn value = src.getElements().get(1);
        value.setMinCardinality(1);
        convertElements(value, ed, thisPath+".value[x]");
        ElementDefn ext = new ElementDefn();
        ext.setName("extension"); // can't have an extension if you have a value
        ext.setMaxCardinality(0);
        convertElements(ext, ed, thisPath+".extension");
      } else {
        for (ElementDefn child : src.getElements()) {
          if (child != url) {
            if (child.getName().startsWith("value"))
              convertElements(child, ed, thisPath+"."+child.getName());
            else {
              if (child.getElements().size() == 0 || !child.getElements().get(0).getName().equals("url")) {
                ElementDefn childUrl = new ElementDefn();
                childUrl.setName("url");
                childUrl.getTypes().add(new TypeRef("uri"));
                childUrl.setFixed(new UriType(child.getName()));
                child.getElements().add(0, childUrl);
              }
              if (!hasValue(child)) {
                ElementDefn value = new ElementDefn();
                value.setName("value[x]");
                value.setMinCardinality(0);
                value.setMaxCardinality(0);
                child.getElements().add(value);
              }
              convertElements(child, ed, thisPath+".extension");
            }
          }
        }
      }
    }
  }

  private boolean hasValue(ElementDefn child) {
    for (ElementDefn v : child.getElements()) {
      if (v.getName().startsWith("value"))
        return true;
    }
    return false;
  }

  public OperationDefinition generate(String name, String id, String resourceName, Operation op) throws Exception {
    OperationDefinition opd = new OperationDefinition();
    opd.setId(FormatUtilities.makeId(id));
    opd.setUrl("http://hl7.org/fhir/OperationDefinition/"+id);
    opd.setName(op.getTitle());
    opd.setPublisher("HL7 (FHIR Project)");
    opd.addContact().getTelecom().add(org.hl7.fhir.instance.model.Factory.newContactPoint(ContactPointSystem.OTHER, "http://hl7.org/fhir"));
    opd.getContact().get(0).getTelecom().add(org.hl7.fhir.instance.model.Factory.newContactPoint(ContactPointSystem.EMAIL, "fhir@lists.hl7.org"));
    opd.setDescription(op.getDoco());
    opd.setStatus(ConformanceResourceStatus.DRAFT);
    opd.setDate(genDate.getTime());
    if (op.getKind().toLowerCase().equals("operation"))
      opd.setKind(OperationKind.OPERATION);
    else if (op.getKind().toLowerCase().equals("query"))
      opd.setKind(OperationKind.QUERY);
    else {
      throw new Exception("Unrecognized operation kind: '" + op.getKind() + "' for operation " + name);
    }
    opd.setCode(op.getName());
    opd.setNotes(op.getFooter());
    opd.setSystem(op.isSystem());
    if (op.isType())
      opd.addType(resourceName);
    opd.setInstance(op.isInstance());
    for (OperationParameter p : op.getParameters()) {
      produceOpParam(op.getName(), opd.getParameter(), p, null);
    }
    NarrativeGenerator gen = new NarrativeGenerator("", "", context);
    gen.generate(opd);
    return opd;
  }

  private void produceOpParam(String path, List<OperationDefinitionParameterComponent> opd, OperationParameter p, OperationParameterUse defUse) throws Exception {
    OperationDefinitionParameterComponent pp = new OperationDefinitionParameterComponent();
    pp.setName(p.getName());
    if (p.getUse().equals("in"))
      pp.setUse(OperationParameterUse.IN);
    else if (p.getUse().equals("out"))
      pp.setUse(OperationParameterUse.OUT);
    else if (path.contains("."))
      pp.setUse(defUse);
    else
      throw new Exception("Unable to determine parameter use: "+p.getUse()+" at "+path+"."+p.getName()); // but this is validated elsewhere
    pp.setDocumentation(p.getDoc());
    pp.setMin(p.getMin());
    pp.setMax(p.getMax());
    if (p.getBs() != null)
      pp.setBinding(new OperationDefinitionParameterBindingComponent().setStrength(p.getBs().getStrength()).setValueSet(buildReference(p.getBs())));
    Reference ref = new Reference();
    if (!Utilities.noString(p.getProfile())) {
      ref.setReference(p.getProfile());
      pp.setProfile(ref);
    }
    opd.add(pp);
    if (p.getType().equals("Tuple")) {
      for (OperationParameter part : p.getParts()) {
        produceOpParam(path+"."+p.getName(), pp.getPart(), part, pp.getUse());
      }
    } else {
      TypeRef tr = new TypeParser().parse(p.getType(), false, null, null, false).get(0);
      if (definitions.getConstraints().containsKey(tr.getName())) {
        ProfiledType pt = definitions.getConstraints().get(tr.getName());
        pp.setType(pt.getBaseType());
        pp.setProfile(new Reference().setReference("http://hl7.org/fhir/StructureDefinition/"+pt.getName()));
      } else { 
        pp.setType(tr.getName());
        if (tr.getParams().size() == 1 && !tr.getParams().get(0).equals("Any"))
          pp.setProfile(new Reference().setReference("http://hl7.org/fhir/StructureDefinition/"+tr.getParams().get(0)));
      } 
    }
  }

  private void checkHasTypes(StructureDefinition p) {
    for (ElementDefinition ed : p.getSnapshot().getElement())
      if (!ed.hasType() && !ed.hasNameReference() && !(ed.getPath().equals("Resource") || ed.getPath().equals("Element")) && !ed.hasRepresentation())
        throw new Error("No Type on "+ed.getPath());
  }


}
