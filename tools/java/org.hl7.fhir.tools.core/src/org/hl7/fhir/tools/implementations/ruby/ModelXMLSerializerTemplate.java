package org.hl7.fhir.tools.implementations.ruby;

/*
 Contributed by Mitre Corporation

 Copyright (c) 2011-2014, HL7, Inc & The MITRE Corporation
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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.List;

import org.hl7.fhir.definitions.model.Definitions;
import org.hl7.fhir.definitions.model.ElementDefn;
import org.hl7.fhir.definitions.model.TypeRef;
import org.hl7.fhir.tools.implementations.GenBlock;

public class ModelXMLSerializerTemplate extends ResourceGenerator {

  public ModelXMLSerializerTemplate(String name, Definitions definitions, File outputFile) {
    super(name, definitions, outputFile, false);
  }

  @Override
  protected void generateEmbeddedType(File parentDir, GenBlock block, ElementDefn elementDefinition) throws IOException {
    List<TypeRef> types = elementDefinition.getTypes();
    if (types.size() == 0) {


      for (ElementDefn nestedElement : elementDefinition.getElements()) {
        generateEmbeddedType(parentDir, block, nestedElement);
      }

      block = new GenBlock();
      String className = getEmbeddedClassName(elementDefinition, null);
      File embeddedTemplate = new File(parentDir, name.toLowerCase() + "_" + className + ".xml.erb");
      if (embeddedTemplate.exists()) throw new IOException("Multiple templates resolve to the same file");

      block.ln("<<%= (is_lowercase) ? name.downcase : name %>");
      // add xml id from element if it is present
      block.ln("<%if model.xmlId%> id=\"<%= model.xmlId%>\"<%end%>");
      for (ElementDefn nestedElement : elementDefinition.getElements()) {
        if (nestedElement.isXmlAttribute()) generateElement(block, nestedElement);
      }
      block.ln(">");
      block.bs();
      block.ln("<%== render :template => 'element', :locals => {model: model, is_resource: false} %>");

      for (ElementDefn nestedElement : elementDefinition.getElements()) {
        if (!nestedElement.isXmlAttribute()) generateElement(block, nestedElement);
      }

      block.es();
      block.ln("</<%= (is_lowercase) ? name.downcase : name %>>");

      Writer modelFile = new BufferedWriter(new FileWriter(embeddedTemplate));
      modelFile.write(block.toString());
      modelFile.flush();
      modelFile.close();

    }

  }

  @Override
  protected String getEmbeddedClassName(ElementDefn elementDefinition, TypeRef typeRef) {
    return super.getEmbeddedClassName(elementDefinition, typeRef).toLowerCase();
  }

  @Override
  protected void generateMainHeader(GenBlock fileBlock) {
    fileBlock.ln("<%== '<?xml version=\"1.0\" encoding=\"UTF-8\"?>' if is_root %>");
  }

  @Override
  protected void generateResourceHeader(GenBlock fileBlock, ElementDefn elementDefinition) {
    // open element
    fileBlock.ln("<% local_name = name || '" + name + "' %>");
    fileBlock.ln("<<%= (is_lowercase) ? local_name.downcase : local_name %>");
    if (!isResource(elementDefinition)) {
      // add xml id from element if it is present and this is not a resource
      // resources have their ids as a child rather than an attribute
      fileBlock.ln("<%if model.xmlId%> id=\"<%= model.xmlId%>\"<%end%>");
    }
    // add other attribute elements
    for (ElementDefn nestedElement : elementDefinition.getElements()) {
      if (nestedElement.isXmlAttribute()) generateElement(fileBlock, nestedElement);
    }
    // add namespace if it's the root element and close the tag
    fileBlock.ln("<%== (is_root) ? ' xmlns=\"http://hl7.org/fhir\"' : ''%>>");
    fileBlock.bs();
    
    // render resource and element templates for backbone data
    fileBlock.ln("<%== render :template => 'element', :locals => {model: model, is_resource: "+isResource(elementDefinition)+"} %>");
    if (isResource(elementDefinition)) fileBlock.ln("<%== render :template => 'resource', :locals => {model: model} %>");
  }

  @Override
  protected void generateResourceFooter(GenBlock fileBlock) {
    fileBlock.es();
    fileBlock.ln("</<%= (is_lowercase) ? local_name.downcase : local_name %>>");
  }

  @Override
  protected void generateMainFooter(GenBlock fileBlock) {
  }

  @Override
  protected void generatePostEmbedded(GenBlock fileBlock, ElementDefn elementDefinition) {
  }

  @Override
  protected void handleField(GenBlock block, FieldType fieldType, boolean multipleCardinality, ElementDefn elementDefinition, TypeRef typeRef) {
    String typeName = generateTypeName(elementDefinition, typeRef);
    String originalTypeName = generateTypeName(elementDefinition, typeRef, false);

    switch (fieldType) {
    case ANY:
      block.ln(getAnyValueFieldLine(typeName, originalTypeName));
      break;
    case BINARY:
      block.ln(getValueFieldLine(typeName, originalTypeName, multipleCardinality, elementDefinition.isXmlAttribute()));
      break;
    case INSTANT:
      block.ln(getInstantFieldLine(typeName, originalTypeName));
      break;
    case DATE:
      block.ln(getPartialDateFieldLine(typeName, originalTypeName));
      break;
    case BOOLEAN:
    case INTEGER:
    case DECIMAL:
    case CODE:
    case STRING:
      block.ln(getValueFieldLine(typeName, originalTypeName, multipleCardinality, elementDefinition.isXmlAttribute()));
      break;
    case RESOURCE:
      block.ln(getResourceFieldLine(typeName, originalTypeName));
      break;
    case QUANTITY:
      block.ln(getNestedElementLine(typeName, originalTypeName, "quantity", multipleCardinality));
      break;
    case REFERENCE:
      block.ln(getNestedElementLine(typeName, originalTypeName, typeRef.getName(), multipleCardinality));
      break;
    case EMBEDDED:
      block.ln(getNestedElementLine(typeName, originalTypeName, name + "_" + getEmbeddedClassName(elementDefinition, typeRef), multipleCardinality));
      break;
    case IGNORED:
      block.ln(getIgnoredElementLine(typeName));
      break;
    }
  }

  private String getAnyValueFieldLine(String typeName, String originalTypeName) {
    return ""
        + "<%- if !model." + typeName + "().nil? -%>"
        +   "<%- if FHIR::AnyType::PRIMITIVES.include? model." + typeName + "Type().downcase -%>"
        +     "<%- if model." + typeName + "().is_a?(Hash) -%>"
        +       "<" + originalTypeName + "<%= model." + typeName + "Type() %>" + " value=\"<%= model." + typeName + "()[:value] %>\"/>"
        +     "<%- else -%>"
        +       "<" + originalTypeName + "<%= model." + typeName + "Type() %>" + " value=\"<%= model." + typeName + "() %>\"/>"
        +     "<%- end -%>"
        +   "<%- elsif FHIR::AnyType::DATE_TIMES.include? model." + typeName + "Type().downcase -%>"
        +     "<%- if model." + typeName + "().is_a?(Hash) -%>"
        +       "<" + originalTypeName + "<%= model." + typeName + "Type() %>" + " value=\"<%= model." + typeName + "()[:value] %>\"/>"
        +     "<%- else -%>"
        +       "<" + originalTypeName + "<%= model." + typeName + "Type() %>" + " value=\"<%= model." + typeName + "() %>\"/>"
        +     "<%- end -%>"
        +   "<%- else -%>"
        +     "<%- if model." + typeName + "().is_a?(Hash) -%>"
        +       "<%== model." + typeName + "()[:value].to_xml(is_root: false, name: \"" + originalTypeName + "#{model." + typeName + "Type()}\")%>"
        +     "<%- else -%>"
        +       "<%== model." + typeName + "().to_xml(is_root: false, name: \"" + originalTypeName + "#{model." + typeName + "Type()}\")%>"
        +     "<%- end -%>"
        +   "<%- end -%>"
        + "<%- end -%>";      
  }

  private String getResourceFieldLine(String typeName, String originalTypeName) {
    return ""
        + "<%- if !model." + typeName + "().nil? -%>"
        + "<"+typeName+">"
        +     "<%== model." + typeName + "().to_xml(is_root: false, name: \"#{model." + typeName + "Type()}\")%>"
        + "</"+typeName+">"
        + "<%- end -%>";      
  }

  private String getIgnoredElementLine(String typeName) {
    return ""
        + "<%- if !model." + typeName + "().nil? -%>"
        +   "<%- if ((model." + typeName + "() =~ /^<" + typeName + "/) == 0) -%>"        
        +     "<%== model." + typeName + "() %>"
        +   "<%- else -%>"
        +     "<" + typeName + " xmlns=\"http://www.w3.org/1999/xhtml\"><%== model." + typeName + "() %></" + typeName + ">"
        +   "<%- end -%>"  
        + "<%- end -%>";  
  }
  
  private String getValueFieldLine(String typeName, String originalTypeName, boolean multipleCardinality, boolean isXmlAttribute) {
    
    if (isXmlAttribute) return "<%- if !model." + typeName + "().nil? -%>"+originalTypeName+"=\"<%== model." + typeName + "() %>\"<%- end -%>";
    
    if (multipleCardinality) {
      return ""
          + "<%- if (model." + typeName + "() && !model." + typeName + ".empty?) -%>"
          +   "<%- model." + typeName + "().each do |element| -%>"
          +     "<" + originalTypeName + " value=\"<%= element %>\"/>"
          +   "<%- end -%>"
          + "<%- end -%>";      
    } else {
      return ""
          + "<%- if !model." + typeName + "().nil? -%>"
          +   "<" + originalTypeName + " value=\"<%= model." + typeName + "() %>\"/>"
          + "<%- end -%>";      
    }
  }

  private String getInstantFieldLine(String typeName, String originalTypeName) {
    // TODO: Assumes Zulu time...
    return ""
         + "<%- if !model." + typeName + "().nil? -%>"
         +   "<" + originalTypeName + " value=\"<%= model." + typeName + "().utc.strftime('%Y-%m-%dT%H:%M:%SZ') %>\"/>"
         + "<%- end -%>";
  }

  private String getPartialDateFieldLine(String typeName, String originalTypeName) {
    String d = "";
    if(typeName.endsWith("Date")) {
      // this is only a Date
      d = "().to_date.iso8601";
    } else if(typeName.endsWith("Time") && !typeName.endsWith("DateTime")) {
      // this is only a Time
      d = "().to_time.strftime(\"%T\")";
    } else {
      // this is a full DateTime
      d = "().iso8601";
    }
    return ""
         + "<%- if !model." + typeName + "().nil? -%>"
         +   "<" + originalTypeName + " value=\"<%= model." + typeName + d + " %>\"/>"
         + "<%- end -%>";
  }

  private String getNestedElementLine(String typeName, String originalTypeName, String template, boolean multipleCardinality) {
    if (multipleCardinality) {
      return ""
          + "<%- if (!model." + typeName + "().nil? && !model." + typeName + ".empty?) -%>"
          +   "<%- model." + typeName + "().each do |element| -%>"
          +     "<%== render :template => '" + template.toLowerCase() + "', :locals => {name: '" + originalTypeName + "', model: element} %>"
          +   "<%- end -%>"
          + "<%- end -%>";
    } else {
      return ""
          + "<%- if !model." + typeName + "().nil? -%>"
          +   "<%== render :template => '" + template.toLowerCase() + "', :locals => {name: '" + originalTypeName + "', model: model." + typeName + "()} %>"
          + "<%- end -%>";
    }
  }

}
