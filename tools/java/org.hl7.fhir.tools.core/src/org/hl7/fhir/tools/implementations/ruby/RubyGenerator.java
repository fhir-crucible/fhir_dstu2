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
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hl7.fhir.definitions.model.Definitions;
import org.hl7.fhir.definitions.model.ResourceDefn;
import org.hl7.fhir.definitions.model.ElementDefn;
import org.hl7.fhir.definitions.model.TypeDefn;
import org.hl7.fhir.tools.implementations.BaseGenerator;
import org.hl7.fhir.tools.publisher.PlatformGenerator;
import org.hl7.fhir.utilities.Logger;
import org.hl7.fhir.utilities.TextFile;
import org.hl7.fhir.utilities.Utilities;
import org.hl7.fhir.utilities.ZipGenerator;
import org.stringtemplate.v4.ST;

public class RubyGenerator extends BaseGenerator implements PlatformGenerator {
  
  public static char SEPARATOR = File.separatorChar;

  @Override
  public String getName() {
    return "ruby";
  }

  @Override
  public String getTitle() {
    return "Ruby";
  }

  @Override
  public String getDescription(String version, String svnRevision) {
    return "Generates Mongoid models for FHIR resources";
  }

  @Override
  public String getVersion() {
    return "0.1";
  }

  @Override
  public boolean isECoreGenerator() {
    return false;
  }

  @Override
  public void generate(Definitions definitions, String destDir, String implDir, String version, Date genDate, Logger logger, String svnRevision) throws Exception {
    
    String resourcesDir = Utilities.path(implDir, "resources");
    String resourcesBaseDir = Utilities.path(resourcesDir, "base");
    final String outputDir = Utilities.path(implDir, "output");
    
    Map<String, String> dirs = new HashMap<String, String>() {{
      put("controllerDir",         Utilities.path(outputDir, "server", "app", "controllers"));
      put("modelDir",              Utilities.path(outputDir, "model", "lib", "models", "fhir"));
      put("xmlTemplateDir",        Utilities.path(outputDir, "model", "lib", "formats", "export", "templates"));
      put("deserializerDir",       Utilities.path(outputDir, "model", "lib", "formats", "import", "deserializers"));
    }};
    
    createDirStructrue(dirs);
    Utilities.copyDirectory(resourcesBaseDir, outputDir, null);
   
    
    Map<String, TypeDefn> typeDefs = definitions.getTypes();
    for(String name: typeDefs.keySet()){
      generateMongoidModel(name, dirs.get("modelDir"), definitions);
      generateXMLSerializerTemplate(name, dirs.get("xmlTemplateDir"), definitions);
      generateXMLDeSerializerTemplate(name, dirs.get("deserializerDir"), definitions);
    }
    
    Map<String, TypeDefn> infDefs = definitions.getInfrastructure();
    for(String name: infDefs.keySet()){
      if (name.equals("Element") || name.equals("BackboneElement")) continue;
      generateMongoidModel(name, dirs.get("modelDir"), definitions);
      generateXMLSerializerTemplate(name, dirs.get("xmlTemplateDir"), definitions);
      generateXMLDeSerializerTemplate(name, dirs.get("deserializerDir"), definitions);
    }
    
    Map<String, TypeDefn> structs = definitions.getStructures();
    for(String name: structs.keySet()){
      generateMongoidModel(name, dirs.get("modelDir"), definitions);
      generateXMLSerializerTemplate(name, dirs.get("xmlTemplateDir"), definitions);
      generateXMLDeSerializerTemplate(name, dirs.get("deserializerDir"), definitions);
    }

    String genericControllerTemplate = TextFile.fileToString(Utilities.path(resourcesDir, "templates", "generic_controller.rb.st"));
    Map<String, ResourceDefn> namesAndDefinitions = definitions.getResources();
    for (String name : namesAndDefinitions.keySet()) {
      generateMongoidModel(name, dirs.get("modelDir"), definitions);
      generateController(name, dirs.get("controllerDir"), genericControllerTemplate);
      generateXMLSerializerTemplate(name, dirs.get("xmlTemplateDir"), definitions);
      generateXMLDeSerializerTemplate(name, dirs.get("deserializerDir"), definitions);
    }
        
    ZipGenerator zip = new ZipGenerator(destDir+getReference(version));
    zip.addFolder(implDir, "mongoid", false);
    zip.close();    
  }
  
  private void createDirStructrue(Map<String, String> dirs) throws IOException {
    for (String dir : dirs.values()) {
      Utilities.createDirectory(Utilities.path(dir));
      Utilities.clearDirectory(Utilities.path(dir));
    }
  }

  private void generateXMLSerializerTemplate(String name, String templateDir, Definitions definitions) throws Exception {
    File templateFile = new File(Utilities.path(templateDir, name.toLowerCase() + ".xml.erb"));
    ModelXMLSerializerTemplate model = new ModelXMLSerializerTemplate(name, definitions, templateFile);
    model.generate();
  }

  private void generateXMLDeSerializerTemplate(String name, String deserializerDir, Definitions definitions) throws Exception {
    File templateFile = new File(Utilities.path(deserializerDir, name.toLowerCase() + ".rb"));
    ModelXMLDeSerializerTemplate model = new ModelXMLDeSerializerTemplate(name, definitions, templateFile);
    model.generate();
  }

  private void generateMongoidModel(String name, String modelDir, Definitions definitions) throws Exception {
    File modelFile = new File(Utilities.path(modelDir, name.toLowerCase() + ".rb"));
    MongoidModel model = new MongoidModel(name, definitions, modelFile);
    model.generate();
  }
  
  private void generateController(String name, String controllerDir, String genericControllerTemplate) throws IOException {
    File controllerFile = new File(Utilities.path(controllerDir, name.toLowerCase() + "_controller.rb"));
    ST controllerTemplate = new ST(genericControllerTemplate);
    
    controllerTemplate.add("ModelName", name);
    controllerTemplate.add("LowerCaseModelName", name.toLowerCase());
    
    Writer controllerWriter = new BufferedWriter(new FileWriter(controllerFile));
    controllerWriter.write(controllerTemplate.render());
    controllerWriter.flush();
    controllerWriter.close();
  }

  @Override
  public boolean doesCompile() {
    return false;
  }

  @Override
  public boolean doesTest() {
    return false;
  }

  @Override
  public void loadAndSave(String rootDir, String sourceFile, String destFile) throws Exception {
  }

  @Override
  public void generate(org.hl7.fhir.definitions.ecore.fhir.Definitions definitions, String destDir, String implDir, String version, Date genDate, Logger logger, String svnRevision) throws Exception {
  }

  @Override
  public boolean compile(String rootDir, List<String> errors, Logger logger) throws Exception {
    return false;
  }

  @Override
  public String checkFragments(String rootDir, String fragmentsXml) throws Exception {
    return null;
  }

}
