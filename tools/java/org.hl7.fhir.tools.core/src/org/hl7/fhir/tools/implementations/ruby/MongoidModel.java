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

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hl7.fhir.definitions.model.BindingSpecification;
import org.hl7.fhir.definitions.model.DefinedCode;
import org.hl7.fhir.definitions.model.Definitions;
import org.hl7.fhir.definitions.model.ElementDefn;
import org.hl7.fhir.definitions.model.ResourceDefn;
import org.hl7.fhir.definitions.model.TypeRef;
import org.hl7.fhir.tools.implementations.GenBlock;

public class MongoidModel extends ResourceGenerator {
 
  private ValueSetProcessor valueSetProcessor = null;
  
  public MongoidModel(String name, Definitions definitions, File outputFile) {
    super(name, definitions, outputFile);
    valueSetProcessor = new ValueSetProcessor(definitions);
  }

  @Override
  protected void generateEmbeddedType(File parentDir, GenBlock block, ElementDefn elementDefinition) {
    List<TypeRef> types = elementDefinition.getTypes();
    if (types.size() == 0) {


      for (ElementDefn nestedElement : elementDefinition.getElements()) {
        generateEmbeddedType(parentDir, block, nestedElement);
      }

      block.ln("# This is an ugly hack to deal with embedded structures in the spec " + generateTypeName(elementDefinition, null));
      String className = getEmbeddedClassName(elementDefinition, null);
      block.ln("class " + className);
      block.ln("include Mongoid::Document");
      block.ln("include FHIR::Element");
      block.ln("include FHIR::Formats::Utilities");
      block.bs();

      generateValidCodes(block, elementDefinition);
      generateMultipleTypes(block, elementDefinition);

      for (ElementDefn nestedElement : elementDefinition.getElements()) {
        generateElement(block, nestedElement);
      }
      
      block.es();
      block.ln("end");
      block.ln();

    }

  }

  @Override
  protected void generateMainHeader(GenBlock fileBlock) {
    fileBlock.ln("# Copyright (c) 2011-2015, HL7, Inc & The MITRE Corporation");
    fileBlock.ln("# All rights reserved.");
    fileBlock.ln("# ");
    fileBlock.ln("# Redistribution and use in source and binary forms, with or without modification, ");
    fileBlock.ln("# are permitted provided that the following conditions are met:");
    fileBlock.ln("# ");
    fileBlock.ln("#     * Redistributions of source code must retain the above copyright notice, this ");
    fileBlock.ln("#       list of conditions and the following disclaimer.");
    fileBlock.ln("#     * Redistributions in binary form must reproduce the above copyright notice, ");
    fileBlock.ln("#       this list of conditions and the following disclaimer in the documentation ");
    fileBlock.ln("#       and/or other materials provided with the distribution.");
    fileBlock.ln("#     * Neither the name of HL7 nor the names of its contributors may be used to ");
    fileBlock.ln("#       endorse or promote products derived from this software without specific ");
    fileBlock.ln("#       prior written permission.");
    fileBlock.ln("# ");
    fileBlock.ln("# THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS \"AS IS\" AND ");
    fileBlock.ln("# ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED ");
    fileBlock.ln("# WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. ");
    fileBlock.ln("# IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, ");
    fileBlock.ln("# INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT ");
    fileBlock.ln("# NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR ");
    fileBlock.ln("# PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, ");
    fileBlock.ln("# WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ");
    fileBlock.ln("# ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE ");
    fileBlock.ln("# POSSIBILITY OF SUCH DAMAGE.");
    fileBlock.ln();
    fileBlock.ln("module FHIR");
    fileBlock.bs();
  }

  @Override
  protected void generateMainFooter(GenBlock fileBlock) {
    fileBlock.es();
    fileBlock.ln("end");
  }

  @Override
  protected void generateResourceHeader(GenBlock fileBlock, ElementDefn elementDefinition) {
    fileBlock.ln("class " + name + " ");
    fileBlock.bs();
    fileBlock.ln();

    fileBlock.ln("include Mongoid::Document");
    fileBlock.ln("include Mongoid::History::Trackable");
    fileBlock.ln("include FHIR::Element");
    if (isResource(elementDefinition)) fileBlock.ln("include FHIR::Resource");
    fileBlock.ln("include FHIR::Formats::Utilities");
    fileBlock.ln("include FHIR::Serializer::Utilities");
    fileBlock.ln("extend FHIR::Deserializer::" + name);

    fileBlock.ln();
    generateSearchParams(fileBlock);
    generateValidCodes(fileBlock, elementDefinition);
    generateMultipleTypes(fileBlock, elementDefinition);
  }

  private void generateSearchParams(GenBlock block) {
    ResourceDefn resource = definitions.getResources().get(name);
    if (resource != null) {
      Set<String> params = resource.getSearchParams().keySet();
      block.ln("SEARCH_PARAMS = [");
      block.bs();
      Iterator<String> iter = params.iterator();
      while (iter.hasNext()) {
        String param = iter.next();
        String comma = iter.hasNext() ? "," : "";
        block.ln("'" + param + "'" + comma);
      }
      block.es();
      block.ln("]");
    }
  }
  
  private void generateValidCodes(GenBlock block, ElementDefn elementDefinition ) {
    
    Map<String,List<String>> validCodes = new HashMap<String,List<String>>();
    Map<String,String> specialCodes = new HashMap<String,String>();
    
    for (ElementDefn nestedElement : elementDefinition.getElements()) {
      TypeRef typeRef = null;
      if(nestedElement.getTypes()!=null && nestedElement.getTypes().size() > 0) {
        typeRef = nestedElement.getTypes().get(0);
      }
      String typeName = generateTypeName(nestedElement, typeRef);
      if(nestedElement.hasBinding() && nestedElement.getBinding().getName() != null) {
        List<DefinedCode> definedCodes = valueSetProcessor.getAllCodes(nestedElement.getBinding().getValueSet());
        if(definedCodes!=null && definedCodes.size() > 0) {          
          List<String> codes = new ArrayList<String>();
          for(DefinedCode code : definedCodes)
          {
            codes.add( code.getCode() );
          }
          validCodes.put(typeName, codes);
        } else if(typeRef.getName().equals("code")) {
          specialCodes.put(typeName, nestedElement.getBinding().getName());         
          String path = "\t" + this.name;
          if(!elementDefinition.getName().equals(this.name)) {
            path += "." + elementDefinition.getName();
          }
          RubyGenerator.messages.add(path + "." + typeName + " -- " + typeRef.getName() + " -- " + nestedElement.getBinding().getName());
        }
      }
    }
    
    RubyGenerator.undefinedValueSets.addAll(valueSetProcessor.undefinedValueSets);
    RubyGenerator.emptyValueSets.addAll(valueSetProcessor.emptyValueSets);

    if(!validCodes.isEmpty()) {
      block.ln();
      block.ln("VALID_CODES = {");
      block.bs();
      StringBuffer sb = new StringBuffer();
      Iterator<String> keys = validCodes.keySet().iterator();
      while( keys.hasNext() ) {
        String key = keys.next();
        List<String> codes = validCodes.get(key);
        if(codes.size() > 0) {
          sb.setLength(0);
          sb.append(key).append(": [");
          for(String code : codes) {
            sb.append(" \'").append(code).append("\',");
          }
          sb.setLength( sb.length() - 1 );
          sb.append(" ]");
          if(keys.hasNext()) {
            sb.append(",");
          }
          block.ln( sb.toString() );          
        }
      }
      block.es();
      block.ln("}");
      block.ln();      
    }    
    
    if(!specialCodes.isEmpty()) {
      block.ln("SPECIAL_CODES = {");
      block.bs();
      StringBuffer sb = new StringBuffer();
      Iterator<String> keys = specialCodes.keySet().iterator();
      while( keys.hasNext() ) {
        String key = keys.next();
        String value = specialCodes.get(key);
        sb.setLength(0);
        sb.append(key).append(": \'").append(value).append("\'");
        if(keys.hasNext()) {
          sb.append(",");
        }
        block.ln( sb.toString() );          
      }
      block.es();
      block.ln("}");
      block.ln();      
    } 
  }
  
  private void generateMultipleTypes(GenBlock block, ElementDefn elementDefinition ) {
    
    Map<String,List<String>> multipleTypes = new HashMap<String,List<String>>();
    List<String> anyTypes = new ArrayList<String>();
    
    for (ElementDefn nestedElement : elementDefinition.getElements()) {
      if(nestedElement.getTypes()!=null && nestedElement.getTypes().size() > 0) {
        String name = nestedElement.getName();
        if(name.endsWith("[x]")) {
          name = name.substring(0, name.length()-3);
        }
        List<String> t = new ArrayList<String>();
        for (TypeRef typeRef : nestedElement.getTypes()) {
          String typeName = generateTypeName(nestedElement, typeRef);
          t.add(typeName);
          if(FieldType.ANY == FieldType.getFieldType(typeRef.getName())) {
            anyTypes.add(name);
          }
        }
        if(t.size() > 1) multipleTypes.put(name, t);
      }    
    }
    
    if(!multipleTypes.isEmpty()) {
      block.ln("MULTIPLE_TYPES = {");
      block.bs();
      StringBuffer sb = new StringBuffer();
      Iterator<String> keys = multipleTypes.keySet().iterator();
      while( keys.hasNext() ) {
        String key = keys.next();
        List<String> types = multipleTypes.get(key);
        if(types.size() > 0) {
          sb.setLength(0);
          sb.append(key).append(": [");
          for(String type : types) {
            sb.append(" \'").append(type).append("\',");
          }
          sb.setLength( sb.length() - 1 );
          sb.append(" ]");
          if(keys.hasNext()) {
            sb.append(",");
          }
          block.ln( sb.toString() );          
        }
      }
      block.es();
      block.ln("}");
      block.ln();      
    }
    
    if(!anyTypes.isEmpty()) {
      StringBuffer sb = new StringBuffer();
      sb.append("ANY_TYPES = [");
      for(String attribute : anyTypes) {
        sb.append(" \'").append(attribute).append("\',");
      }
      sb.setLength( sb.length() - 1 );
      sb.append(" ]");
      block.ln( sb.toString() );
      block.ln();
    }    
  }

  @Override
  protected void generateResourceFooter(GenBlock fileBlock) {
    fileBlock.ln("track_history");
    fileBlock.es();
    fileBlock.ln("end");
  }

  @Override
  protected void generatePostEmbedded(GenBlock fileBlock, ElementDefn elementDefinition) {
  }

  @Override
  protected void handleField(GenBlock block, FieldType fieldType, boolean multipleCardinality, ElementDefn elementDefinition, TypeRef typeRef) {
    String typeName = generateTypeName(elementDefinition, typeRef);

    boolean requiredField = (elementDefinition.getMinCardinality() >= 1);
    StringBuilder sb;
    String regex = null;
    
    switch (fieldType) {
    case ANY:
      block.ln(getValueFieldLine(typeName, "FHIR::AnyType", multipleCardinality));
      break;
    case BINARY:
      block.ln(getValueFieldLine(typeName, "String", multipleCardinality));
      // TODO Add base64 validator? regex plus length%4==0
      break;
    case BOOLEAN:
      block.ln(getValueFieldLine(typeName, "Boolean", multipleCardinality));
      break;
    case INTEGER:
      block.ln(getValueFieldLine(typeName, "Integer", multipleCardinality));
      break;
    case DECIMAL:
      block.ln(getValueFieldLine(typeName, "Float", multipleCardinality));
      break;
    case DATE:
      if(regex==null) regex = ResourceGenerator.REGEX_DATE;
    case DATETIME:
      if(regex==null) regex = ResourceGenerator.REGEX_DATETIME;
    case TIME:
      if(regex==null) regex = ResourceGenerator.REGEX_TIME;
    case INSTANT:
      if(regex==null) regex = ResourceGenerator.REGEX_INSTANT;
      
      block.ln(getValueFieldLine(typeName, "String", multipleCardinality));
      if(multipleCardinality) {
        block.ln("validates_each :" + typeName + ", allow_nil: true do |record, attr, values|");
        block.bs();
        block.ln("values.each do |value|");
        block.bs();
        block.ln("record.errors.add(attr, \"#{value} is not a valid "+fieldType.toString().toLowerCase()+".\") if value.match("+regex+").nil?");
        block.es();
        block.ln("end");
        block.es();
        block.ln("end");
      } else {
        sb = new StringBuilder();
        sb.append("validates :").append(typeName).append(", :allow_nil => true, :format => {  with: ");
        sb.append(regex).append(" }");
        block.ln(sb.toString());        
      }
      break;
    case CODE:
      block.ln(getValueFieldLine(typeName, "String", multipleCardinality));
      if( (elementDefinition.hasBinding()) && 
          (elementDefinition.getBinding().getName()!=null) &&  
          (valueSetProcessor.getAllCodes(elementDefinition.getBinding().getValueSet()).size() > 0) )
      {
            
        sb = new StringBuilder();
        sb.append("validates :").append(typeName).append(", :inclusion => { in: VALID_CODES[:");
        sb.append(typeName).append("]");
        if(requiredField) {
          sb.append(" }");
        } else {
          sb.append(", :allow_nil => true }");
        }
        block.ln(sb.toString());
      }      
      break;      
    case STRING:
      block.ln(getValueFieldLine(typeName, "String", multipleCardinality));
      break;
    case RESOURCE:
      block.ln(getValueFieldLine(typeName+"Type", "String", multipleCardinality));
      block.ln("attr_accessor :"+typeName);
      block.ln("# " + getValueFieldLine(typeName, "FHIR::AnyType", multipleCardinality));
      break;
    case QUANTITY:
      block.ln(getNestedElementLine(typeName, "Quantity", multipleCardinality));
      break;
    case REFERENCE:
      block.ln(getNestedElementLine(typeName, typeRef.getName(), multipleCardinality));
      break;
    case EMBEDDED:
      block.ln(getEmbeddedElementLine(typeName, getEmbeddedClassName(elementDefinition, typeRef), multipleCardinality));
      break;
    case IGNORED:
      block.ln(getValueFieldLine(typeName, "String", false));
      break;
    }
    
    if(requiredField) {
      block.ln("validates_presence_of :" + typeName);
    }
  }

  private String getEmbeddedElementLine(String typeName, String resourceName, boolean multipleCardinality) {
    if (multipleCardinality) {
      String cyclic = "";
      if (isCyclic(resourceName)) cyclic = ", cyclic: true";
      return "embeds_many :" + typeName + ", class_name:'FHIR::" + name + "::" + resourceName + "'"+cyclic;
    } else {
      return "embeds_one :" + typeName + ", class_name:'FHIR::" + name + "::" + resourceName + "'";
    }    
  }

  private boolean isCyclic(String resourceName) {
    return ((name.equals("Questionnaire") || name.equals("QuestionnaireAnswers")) && (resourceName.equals("GroupComponent") || resourceName.equals("QuestionComponent")));
           
  }

  private String getValueFieldLine(String typeName, String className, boolean multipleCardinality) {
    if (multipleCardinality) {
      return "field :" + typeName + ", type: Array # Array of " + className + "s";
    } else {
      return "field :" + typeName + ", type: " + className;      
    }
  }

  private String getNestedElementLine(String typeName, String className, boolean multipleCardinality) {
    if (multipleCardinality) {
      return "embeds_many :" + typeName + ", class_name:'FHIR::" + className + "'";
    } else {
      return "embeds_one :" + typeName + ", class_name:'FHIR::" + className + "'";
    }
  }

}
