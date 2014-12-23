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
import java.util.List;

import org.hl7.fhir.definitions.model.Definitions;
import org.hl7.fhir.definitions.model.ElementDefn;
import org.hl7.fhir.definitions.model.TypeRef;
import org.hl7.fhir.tools.implementations.GenBlock;

public class ModelXMLDeSerializerTemplate extends ResourceGenerator {
  
    private static final String DATE_TIME_PARSE_FUNCTION = "parse_date_time";
    
    public ModelXMLDeSerializerTemplate(String name, Definitions definitions, File outputFile) {
      super(name, definitions, outputFile);
    }
     
    @Override
    protected void generateEmbeddedType(File parentDir, GenBlock block, ElementDefn elementDefinition) {
      List<TypeRef> types = elementDefinition.getTypes();
      if(types.size() == 0) {
          
          for (ElementDefn nestedElement : elementDefinition.getElements()) {
            generateEmbeddedType(parentDir, block, nestedElement);
          }
          
          String className = getEmbeddedClassName(elementDefinition, null);
          
          block.ln("def parse_xml_entry_"+className+"(entry) ");
          block.bs();
          block.ln("return nil unless entry");
          block.ln("model = FHIR::"+name+"::"+className+".new");
          // this is things like XML ID, contained array, narrative, etc.
          // see: import utilities
          block.ln("self.parse_element_data(model, entry)");
          
          for (ElementDefn nestedElement : elementDefinition.getElements()) {
            generateElement(block, nestedElement);
          }
          block.ln("model");

          block.es();
          block.ln("end");
          block.ln();

      }
      
    }
    
    @Override
    protected void generateMainHeader(GenBlock fileBlock) {
      fileBlock.ln("module FHIR");
      fileBlock.bs();
      fileBlock.ln("module Deserializer");
      fileBlock.bs();
      fileBlock.ln("module "+name);
      fileBlock.ln("include FHIR::Formats::Utilities");
      fileBlock.ln("include FHIR::Deserializer::Utilities");
      fileBlock.bs();
    }

    @Override
    protected void generateMainFooter(GenBlock fileBlock) {
      fileBlock.ln("end");
      fileBlock.es();
      fileBlock.ln("end");
      fileBlock.es();
      fileBlock.ln("end");
    }

    @Override
    protected void generateResourceHeader(GenBlock fileBlock, ElementDefn elementDefinition) {
    }
    
    @Override
    protected void generatePostEmbedded(GenBlock fileBlock, ElementDefn elementDefinition) {
      fileBlock.ln("def parse_xml_entry(entry) ");
      fileBlock.bs();
      fileBlock.ln("return nil unless entry");
      fileBlock.ln("model = self.new");
      
      // parse the data that comes from the base classes Element and Resource
      // this is things like XML ID, contained array, narrative, etc.
      // see: import utilities
      fileBlock.ln("self.parse_element_data(model, entry)");
      if (isResource(elementDefinition)) fileBlock.ln("self.parse_resource_data(model, entry)");

    }


    @Override
    protected void generateResourceFooter(GenBlock fileBlock) {
      fileBlock.ln("model");
      fileBlock.es();
      fileBlock.ln("end");
      fileBlock.es();
    }

    @Override
    protected void handleField(GenBlock block, FieldType fieldType, boolean multipleCardinality, ElementDefn elementDefinition, TypeRef typeRef) {
      String typeName = generateTypeName(elementDefinition, typeRef);
      String originalTypeName = generateTypeName(elementDefinition, typeRef, false);
      
      switch (fieldType) {
      case ANY:
        addAnyElementLines(block, typeName);
        break;
      case BINARY:
        block.ln(getValueFieldLine(typeName, originalTypeName, multipleCardinality, elementDefinition.isXmlAttribute()));
        break;
      case INSTANT:
        block.ln(getValueFieldLine(typeName, originalTypeName, multipleCardinality, elementDefinition.isXmlAttribute(), DATE_TIME_PARSE_FUNCTION));
        break;
      case BOOLEAN:
      case INTEGER:
      case DECIMAL:
      case DATE:
      case CODE:
      case STRING:
        block.ln(getValueFieldLine(typeName, originalTypeName, multipleCardinality, elementDefinition.isXmlAttribute()));
        break;
      case REFERENCE:
        block.ln(getNestedElementLine(typeName, originalTypeName, "ResourceReference", multipleCardinality));
        break;
      case QUANTITY:
        block.ln(getNestedElementLine(typeName, originalTypeName, "Quantity", multipleCardinality));
        break;
      case RESOURCE:
        block.ln(getNestedElementLine(typeName, originalTypeName, typeRef.getName(), multipleCardinality));
        break;
      case EMBEDDED:
        block.ln(getEmbeddedElementLine(typeName, originalTypeName, getEmbeddedClassName(elementDefinition, typeRef), multipleCardinality));
        break;
      case IGNORED:
        getIgnoredElementLine(block, typeName);
        break;
      }
    }
    
    private void addAnyElementLines(GenBlock block, String typeName) {
      block.ln("entry.xpath(\"./*[contains(local-name(),'"+typeName+"')]\").each do |e| ");
      block.ln("  model."+typeName+"Type = e.name.gsub('"+typeName+"','')");
      block.ln("  v = e.at_xpath('@value').try(:value)");
      block.ln("  v = \"FHIR::#{model."+typeName+"Type}\".constantize.parse_xml_entry(e) unless v");
      block.ln("  model."+typeName+" = {type: model."+typeName+"Type, value: v}");
      block.ln("end");
    }

    private String getEmbeddedElementLine(String typeName, String originalTypeName, String resourceName, boolean multipleCardinality) {
      if (multipleCardinality) {
        return "set_model_data(model, '"+typeName+"', entry.xpath('./fhir:"+originalTypeName+"').map {|e| parse_xml_entry_"+resourceName+"(e)})";
      } else {
        return "set_model_data(model, '"+typeName+"', parse_xml_entry_"+resourceName+"(entry.at_xpath('./fhir:"+originalTypeName+"')))";
      }
    }

    private String getNestedElementLine(String typeName, String originalTypeName, String resourceName, boolean multipleCardinality) {
      if (multipleCardinality) {
        return "set_model_data(model, '"+typeName+"', entry.xpath('./fhir:"+originalTypeName+"').map {|e| FHIR::"+resourceName+".parse_xml_entry(e)})";
      } else {
        return "set_model_data(model, '"+typeName+"', FHIR::"+resourceName+".parse_xml_entry(entry.at_xpath('./fhir:"+originalTypeName+"')))";
      }
    }
    
    private void getIgnoredElementLine(GenBlock block, String typeName) {
      block.ln("ignored = \"\"");
      block.ln("array = entry.xpath(\"./*[local-name()='"+typeName+"']\")");
      block.ln("array.each { |e| ignored += e.to_s }");
      block.ln("set_model_data(model, '" + typeName + "', ignored )");
    }

    private String getValueFieldLine(String typeName, String originalTypeName, boolean multipleCardinality, boolean isXmlAttribute) {
      return getValueFieldLine(typeName, originalTypeName, multipleCardinality, isXmlAttribute, null);
    }
    private String getValueFieldLine(String typeName, String originalTypeName, boolean multipleCardinality, boolean isXmlAttribute, String parseFunction) {
      String parseFunctionOpen = "";
      String parseFunctionClose = "";
      if (parseFunction != null) {
        parseFunctionOpen = parseFunction + "(";
        parseFunctionClose = ")";
      }
      
      String selector = "./fhir:"+originalTypeName+"/@value";
      if (isXmlAttribute) {
        selector = "./@"+originalTypeName+"";
      }
      
      if (multipleCardinality) {
        return "set_model_data(model, '"+typeName+"', entry.xpath('"+selector+"').map {|e| "+parseFunctionOpen+"e.value"+parseFunctionClose+" })";
      } else {
        return "set_model_data(model, '"+typeName+"', "+parseFunctionOpen+"entry.at_xpath('"+selector+"').try(:value)"+parseFunctionClose+")";
      }
      
    }

    
}
