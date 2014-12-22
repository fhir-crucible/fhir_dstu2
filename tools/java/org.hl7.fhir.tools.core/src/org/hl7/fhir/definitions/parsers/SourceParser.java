package org.hl7.fhir.definitions.parsers;

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
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.print.Doc;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.hl7.fhir.definitions.ecore.fhir.BindingDefn;
import org.hl7.fhir.definitions.ecore.fhir.CompositeTypeDefn;
import org.hl7.fhir.definitions.ecore.fhir.ConstrainedTypeDefn;
import org.hl7.fhir.definitions.ecore.fhir.PrimitiveDefn;
import org.hl7.fhir.definitions.ecore.fhir.TypeDefn;
import org.hl7.fhir.definitions.ecore.fhir.impl.DefinitionsImpl;
import org.hl7.fhir.definitions.generators.specification.ProfileGenerator;
import org.hl7.fhir.definitions.model.ConformancePackage;
import org.hl7.fhir.definitions.model.ConformancePackage.ConformancePackageSourceType;
import org.hl7.fhir.definitions.model.BindingSpecification;
import org.hl7.fhir.definitions.model.BindingSpecification.Binding;
import org.hl7.fhir.definitions.model.Compartment;
import org.hl7.fhir.definitions.model.DefinedCode;
import org.hl7.fhir.definitions.model.DefinedStringPattern;
import org.hl7.fhir.definitions.model.Definitions;
import org.hl7.fhir.definitions.model.ElementDefn;
import org.hl7.fhir.definitions.model.EventDefn;
import org.hl7.fhir.definitions.model.Invariant;
import org.hl7.fhir.definitions.model.MappingSpace;
import org.hl7.fhir.definitions.model.PrimitiveType;
import org.hl7.fhir.definitions.model.ProfileDefn;
import org.hl7.fhir.definitions.model.ProfiledType;
import org.hl7.fhir.definitions.model.RegisteredProfile;
import org.hl7.fhir.definitions.model.ResourceDefn;
import org.hl7.fhir.definitions.model.TypeRef;
import org.hl7.fhir.definitions.model.WorkGroup;
import org.hl7.fhir.definitions.parsers.converters.BindingConverter;
import org.hl7.fhir.definitions.parsers.converters.CompositeTypeConverter;
import org.hl7.fhir.definitions.parsers.converters.ConstrainedTypeConverter;
import org.hl7.fhir.definitions.parsers.converters.EventConverter;
import org.hl7.fhir.definitions.parsers.converters.PrimitiveConverter;
import org.hl7.fhir.instance.formats.FormatUtilities;
import org.hl7.fhir.instance.formats.JsonParser;
import org.hl7.fhir.instance.formats.ResourceOrFeed;
import org.hl7.fhir.instance.formats.XmlParser;
import org.hl7.fhir.instance.model.Bundle;
import org.hl7.fhir.instance.model.Bundle.BundleEntryComponent;
import org.hl7.fhir.instance.model.Bundle.BundleType;
import org.hl7.fhir.instance.model.Composition;
import org.hl7.fhir.instance.model.DomainResource;
import org.hl7.fhir.instance.model.ExtensionDefinition;
import org.hl7.fhir.instance.model.Profile;
import org.hl7.fhir.instance.model.Resource;
import org.hl7.fhir.instance.model.ValueSet;
import org.hl7.fhir.instance.utils.WorkerContext;
import org.hl7.fhir.utilities.CSFile;
import org.hl7.fhir.utilities.CSFileInputStream;
import org.hl7.fhir.utilities.IniFile;
import org.hl7.fhir.utilities.Logger;
import org.hl7.fhir.utilities.Logger.LogMessageType;
import org.hl7.fhir.utilities.TextFile;
import org.hl7.fhir.utilities.Utilities;
import org.hl7.fhir.utilities.XLSXmlParser;
import org.hl7.fhir.utilities.XLSXmlParser.Sheet;
import org.hl7.fhir.utilities.xml.XMLUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * This class parses the master source for FHIR into a single definitions object
 * model
 * 
 * The class knows where to find things, and what order to load them in
 * 
 * @author Grahame
 * 
 */
public class SourceParser {

	private Logger logger;
	private IniFile ini;
	private Definitions definitions;
	private String srcDir;
	private String sndBoxDir;
	private String imgDir;
	private String termDir;
	public String dtDir;
	private String rootDir;
	private BindingNameRegistry registry;
	private String version;
	private WorkerContext context; 
	private Calendar genDate;
  private Map<String, ExtensionDefinition> extensionDefinitions = new HashMap<String, ExtensionDefinition>();

	public SourceParser(Logger logger, String root, Definitions definitions, boolean forPublication, String version, WorkerContext context, Calendar genDate, Map<String, ExtensionDefinition> extensionDefinitions) {
		this.logger = logger;
		this.registry = new BindingNameRegistry(root, forPublication);
		this.definitions = definitions;
		this.version = version;
		this.context = context;
		this.genDate = genDate;

		char sl = File.separatorChar;
		srcDir = root + sl + "source" + sl;
		sndBoxDir = root + sl + "sandbox" + sl;
		ini = new IniFile(srcDir + "fhir.ini");

		termDir = srcDir + "terminologies" + sl;
		dtDir = srcDir + "datatypes" + sl;
		imgDir = root + sl + "images" + sl;
		rootDir = root + sl;
    this.extensionDefinitions = extensionDefinitions;
	}

	private org.hl7.fhir.definitions.ecore.fhir.Definitions eCoreParseResults = null;

	public org.hl7.fhir.definitions.ecore.fhir.Definitions getECoreParseResults() {
		return eCoreParseResults;
	}

	
	private List<BindingDefn> sortBindings(List<BindingDefn> unsorted)
	{
		List<BindingDefn> sorted = new ArrayList<BindingDefn>();
		sorted.addAll(unsorted);
		
		Collections.sort(sorted, new Comparator<BindingDefn>() {
			@Override
			public int compare( BindingDefn a, BindingDefn b )
			{
				return a.getName().compareTo(b.getName());
			}
		});
		
		return sorted;
	}
	

	@SuppressWarnings("unchecked")
  private List<TypeDefn> sortTypes(List unsorted)
	{
		List<TypeDefn> sorted = new ArrayList<TypeDefn>();
		sorted.addAll(unsorted);
		
		Collections.sort(sorted, new Comparator() {
			@Override
			public int compare( Object a, Object b )
			{
				if( a instanceof PrimitiveDefn )
					return ((PrimitiveDefn)a).getName().compareTo( ((PrimitiveDefn)b).getName() );
				else
					return ((TypeDefn)a).getName().compareTo(((TypeDefn)b).getName());
			}
		});
		
		return sorted;
	}
	
	public void parse(Calendar genDate) throws Exception {
		logger.log("Loading", LogMessageType.Process);

		eCoreParseResults = DefinitionsImpl.build(genDate.getTime(), version);
		loadWorkGroups();
		loadMappingSpaces();
		loadGlobalConceptDomains();
		eCoreParseResults.getBinding().addAll(sortBindings(BindingConverter.buildBindingsFromFhirModel(definitions.getBindings().values(), null)));

		loadPrimitives();
		eCoreParseResults.getPrimitive().addAll(PrimitiveConverter.buildPrimitiveTypesFromFhirModel(definitions.getPrimitives().values()));
		
		for (String n : ini.getPropertyNames("removed-resources"))
		  definitions.getDeletedResources().add(n);
		
		for (String n : ini.getPropertyNames("types"))
			loadCompositeType(n, definitions.getTypes());	
		for (String n : ini.getPropertyNames("structures"))
			loadCompositeType(n, definitions.getStructures());

		String[] shared = ini.getPropertyNames("shared"); 
		if(shared != null)
		  for (String n : shared )
		    definitions.getShared().add(loadCompositeType(n, definitions.getStructures()));
		
		for (String n : ini.getPropertyNames("infrastructure"))
			loadCompositeType(n, definitions.getInfrastructure());

		List<TypeDefn> allFhirComposites = new ArrayList<TypeDefn>();
	//	allFhirComposites.add( CompositeTypeConverter.buildElementBaseType());
		allFhirComposites.addAll( PrimitiveConverter.buildCompositeTypesForPrimitives( eCoreParseResults.getPrimitive() ) );
		allFhirComposites.addAll( CompositeTypeConverter.buildCompositeTypesFromFhirModel(definitions.getTypes().values(), null ));
		allFhirComposites.addAll( CompositeTypeConverter.buildCompositeTypesFromFhirModel(definitions.getStructures().values(), null ));

		List<CompositeTypeDefn> infra = CompositeTypeConverter.buildCompositeTypesFromFhirModel(definitions.getInfrastructure().values(), null ); 
		for (CompositeTypeDefn composite : infra) 
		  composite.setInfrastructure(true);
		allFhirComposites.addAll( infra );
		allFhirComposites.addAll( ConstrainedTypeConverter.buildConstrainedTypesFromFhirModel(definitions.getConstraints().values()));
		
		eCoreParseResults.getType().addAll( sortTypes(allFhirComposites) );
		
		// basic infrastructure
    for (String n : ini.getPropertyNames("resource-infrastructure")) {
      ResourceDefn r = loadResource(n, null, true);
      String[] parts = ini.getStringProperty("resource-infrastructure", n).split("\\,");
      if (parts[0].equals("abstract"))
        r.setAbstract(true);
      definitions.getBaseResources().put(parts[1], r);
    }
		
    logger.log("Load Resources", LogMessageType.Process);
		for (String n : ini.getPropertyNames("resources"))
			loadResource(n, definitions.getResources(), false);
		
		loadCompartments();
		loadStatusCodes();
		
		//eCoreBaseResource.getElement().add(CompositeTypeConverter.buildInternalIdElement());		
		eCoreParseResults.getType().addAll(sortTypes(CompositeTypeConverter.buildResourcesFromFhirModel(definitions.getBaseResources().values() )));
		eCoreParseResults.getType().addAll(sortTypes(CompositeTypeConverter.buildResourcesFromFhirModel(definitions.getResources().values() )));
		
	//	eCoreParseResults.getType().add(CompositeTypeConverter.buildBinaryResourceDefn());
		
		for (String n : ini.getPropertyNames("svg"))
		  definitions.getDiagrams().put(n, ini.getStringProperty("svg", n));
		
		eCoreParseResults.getEvent().addAll(EventConverter.buildEventsFromFhirModel(definitions.getEvents().values()));
	
		// As a second pass, resolve typerefs to the types
		fixTypeRefs(eCoreParseResults);
		eCoreParseResults.getBinding().add(BindingConverter.buildResourceTypeBinding(eCoreParseResults));
		
		for (String n : ini.getPropertyNames("special-resources"))
			definitions.getAggregationEndpoints().add(n);

		String[] pn = ini.getPropertyNames("valuesets");
		if (pn != null)
		  for (String n : pn) {
		    loadValueSet(n);
		  }
		for (String n : ini.getPropertyNames("profiles")) { // todo-profile: rename this
			loadConformancePackages(n, definitions.getConformancePackages());
		}
		
		for (ResourceDefn r : definitions.getResources().values()) {
		  for (ConformancePackage p : r.getConformancePackages()) 
		    loadConformancePackage(p);
		}
	}


  private void loadWorkGroups() {
    String[] wgs = ini.getPropertyNames("wg-info");
    for (String wg : wgs) {
     String s = ini.getStringProperty("wg-info", wg);
     int i = s.indexOf(" ");
     String url = s.substring(0, i);
     String name = s.substring(i+1).trim();
     definitions.getWorkgroups().put(wg, new WorkGroup(wg, name, url));
    }    
  }


  private void loadMappingSpaces() throws Exception {
    try {
      DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
      factory.setNamespaceAware(true);
      DocumentBuilder builder = factory.newDocumentBuilder();
      Document doc = builder.parse(new FileInputStream(Utilities.path(srcDir, "mappingSpaces.xml")));
      Element e = XMLUtil.getFirstChild(doc.getDocumentElement());
      while (e != null) {
        MappingSpace m = new MappingSpace(XMLUtil.getNamedChild(e, "columnName").getTextContent(), XMLUtil.getNamedChild(e, "title").getTextContent(), 
             XMLUtil.getNamedChild(e, "id").getTextContent(), Integer.parseInt(XMLUtil.getNamedChild(e, "sort").getTextContent()));
        definitions.getMapTypes().put(XMLUtil.getNamedChild(e, "url").getTextContent(), m);
        Element p = XMLUtil.getNamedChild(e, "preamble");
        if (p != null)
          m.setPreamble(XMLUtil.elementToString(XMLUtil.getFirstChild(p)));
        e = XMLUtil.getNextSibling(e);
      }
    } catch (Exception e) {
      throw new Exception("Error processing mappingSpaces.xml: "+e.getMessage(), e);
    }
  }


  private void loadStatusCodes() throws FileNotFoundException, Exception {
    XLSXmlParser xml = new XLSXmlParser(new CSFileInputStream(srcDir+"status-codes.xml"), "Status Codes");
    Sheet sheet = xml.getSheets().get("Status Codes");
    for (int row = 0; row < sheet.rows.size(); row++) {
      String path = sheet.getColumn(row, "Path");
      ArrayList<String> codes = new ArrayList<String>();
      for (int i = 1; i <= 80; i++) {
        String s = sheet.getColumn(row, "c"+Integer.toString(i));
        if (!Utilities.noString(s))
          codes.add(s);
      }
      definitions.getStatusCodes().put(path, codes); 
    }    
  }
  
	private void loadCompartments() throws FileNotFoundException, Exception {
    XLSXmlParser xml = new XLSXmlParser(new CSFileInputStream(srcDir+"compartments.xml"), "compartments.xml");
    Sheet sheet = xml.getSheets().get("compartments");
    for (int row = 0; row < sheet.rows.size(); row++) {
      Compartment c = new Compartment();
      c.setName(sheet.getColumn(row, "Name"));
      if (!c.getName().startsWith("!")) {
        c.setTitle(sheet.getColumn(row, "Title"));
        c.setDescription(sheet.getColumn(row, "Description"));
        c.setIdentity(sheet.getColumn(row, "Identification"));
        c.setMembership(sheet.getColumn(row, "Inclusion"));
        
        definitions.getCompartments().add(c);
      }
    }
    sheet = xml.getSheets().get("resources");
    for (int row = 0; row < sheet.rows.size(); row++) {
      String mn = sheet.getColumn(row, "Resource");
      if (!mn.startsWith("!")) {
        ResourceDefn r = definitions.getResourceByName(mn);
        for (Compartment c : definitions.getCompartments()) {
          c.getResources().put(r,  sheet.getColumn(row, c.getName()));
        }
      }
    }    
  }


  private void loadValueSet(String n) throws FileNotFoundException, Exception {
    XmlParser xml = new XmlParser();
    ValueSet vs = (ValueSet) xml.parse(new CSFileInputStream(srcDir+ini.getStringProperty("valuesets", n).replace('\\', File.separatorChar)));
    vs.setIdentifier("http://hl7.org/fhir/vs/"+n);
    vs.setId(FormatUtilities.makeId(n));
    definitions.getExtraValuesets().put(n, vs);
  }


  private void fixTypeRefs( org.hl7.fhir.definitions.ecore.fhir.Definitions defs )
	{
		for( CompositeTypeDefn composite : defs.getLocalCompositeTypes() )
			CompositeTypeConverter.FixTypeRefs(composite);
		
		for( ConstrainedTypeDefn constrained : defs.getLocalConstrainedTypes() )
			ConstrainedTypeConverter.FixTypeRefs(constrained);
		
		for( org.hl7.fhir.definitions.ecore.fhir.ResourceDefn resource : defs.getResources() )
			CompositeTypeConverter.FixTypeRefs(resource);
	}
	
	
	private void loadConformancePackages(String n, Map<String, ConformancePackage> packs) throws Exception {
	  File spreadsheet = new CSFile(rootDir+ ini.getStringProperty("profiles", n));
	  if (TextFile.fileToString(spreadsheet.getAbsolutePath()).contains("urn:schemas-microsoft-com:office:spreadsheet")) {
	    SpreadsheetParser sparser = new SpreadsheetParser(new CSFileInputStream(spreadsheet), spreadsheet.getName(), definitions, srcDir, logger, registry, version, context, genDate, false, extensionDefinitions);
	    try {
	      ConformancePackage pack = new ConformancePackage();
	      pack.setTitle(n);
	      pack.setSource(spreadsheet.getAbsolutePath());
	      pack.setSourceType(ConformancePackageSourceType.Spreadsheet);
        packs.put(n, pack);
	      sparser.parseConformancePackage(pack, definitions, Utilities.getDirectoryForFile(spreadsheet.getAbsolutePath()));
	    } catch (Exception e) {
	      throw new Exception("Error Parsing Profile: '"+n+"': "+e.getMessage(), e);
	    }
	  } else {
	    ConformancePackage pack = new ConformancePackage();
	    parseConformanceDocument(pack, n, spreadsheet);
      packs.put(n, pack);
	  }
	}


  private void parseConformanceDocument(ConformancePackage pack, String n, File file) throws Exception {
    try {
      Resource rf = new XmlParser().parse(new CSFileInputStream(file));
      if (!(rf instanceof Bundle))
        throw new Exception("Error parsing Conformance package: neither a spreadsheet nor a bundle");
      Bundle b = (Bundle) rf;
      if (b.getType() != BundleType.DOCUMENT)
        throw new Exception("Error parsing Conformance package: neither a spreadsheet nor a bundle that is a document");
      for (BundleEntryComponent ae : ((Bundle) rf).getEntry()) {
        String base = ae.hasBase() ? ae.getBase() : b.getBase();
        if (ae.getResource() instanceof Composition)
          pack.loadFromComposition((Composition) ae.getResource(), file.getAbsolutePath());
        else if (ae.getResource() instanceof Profile)
          pack.getProfiles().add(new ProfileDefn((Profile) ae.getResource()));
        else if (ae.getResource() instanceof ExtensionDefinition) {
          ExtensionDefinition ed = (ExtensionDefinition) ae.getResource();
          context.seeExtensionDefinition(base, ed);
          pack.getExtensions().add(ed);
        }
      }
    } catch (Exception e) {
      throw new Exception("Error Parsing Conformance Package: '"+n+"': "+e.getMessage(), e);
    }
  }


  private void loadConformancePackage(ConformancePackage ap) throws FileNotFoundException, IOException, Exception {
    if (ap.getSourceType() == ConformancePackageSourceType.Spreadsheet) {
      SpreadsheetParser sparser = new SpreadsheetParser(new CSFileInputStream(ap.getSource()), ap.getId(), definitions, srcDir, logger, registry, version, context, genDate, false, extensionDefinitions);
      sparser.setFolder(Utilities.getDirectoryForFile(ap.getSource()));
      sparser.parseConformancePackage(ap, definitions, Utilities.getDirectoryForFile(ap.getSource()));
    } else // if (ap.getSourceType() == ConformancePackageSourceType.Bundle) {
      parseConformanceDocument(ap, ap.getId(), new File(ap.getSource()));
  }
	
	private void loadGlobalConceptDomains() throws Exception {
		logger.log("Load Concept Domains", LogMessageType.Process);

		BindingsParser parser = new BindingsParser(new CSFileInputStream(new CSFile(termDir + "bindings.xml")), termDir + "bindings.xml", srcDir, registry);
		List<BindingSpecification> cds = parser.parse();

		for (BindingSpecification cd : cds) {
			definitions.getBindings().put(cd.getName(), cd);
			definitions.getCommonBindings().add(cd);
		}

		for (BindingSpecification cd : definitions.getBindings().values()) {
		  if (cd.getBinding() == BindingSpecification.Binding.CodeList) {
		    if (!parser.loadCodes(cd)) {
		      File file = new CSFile(termDir + cd.getReference().substring(1)	+ ".csv");
		      if (!file.exists())
		        throw new Exception("code source file not found for "
		            + cd.getName() + ": " + file.getAbsolutePath());
		      CodeListParser cparser = new CodeListParser(
		          new CSFileInputStream(file));
		      cparser.parse(cd.getCodes());
		      cparser.close();
		    }
		  }
		  if (cd.getBinding() == Binding.ValueSet && !Utilities.noString(cd.getReference())) {
		    if (cd.getReference().startsWith("http://hl7.org/fhir")) {
		      // ok, it's a reference to a value set defined within this build. Since it's an absolute 
		      // reference, it's into the base infrastructure. That's not loaded yet, so we will try
		      // to resolve it later
		    } else if (new File(Utilities.appendSlash(termDir)+cd.getReference()+".xml").exists()) {
		      XmlParser p = new XmlParser();
		      FileInputStream input = new FileInputStream(Utilities.appendSlash(termDir)+cd.getReference()+".xml");
		      cd.setReferredValueSet((ValueSet) p.parse(input));
		    } else if (new File(Utilities.appendSlash(termDir)+cd.getReference()+".json").exists()) {
		      JsonParser p = new JsonParser();
		      FileInputStream input = new FileInputStream(Utilities.appendSlash(termDir)+cd.getReference()+".json");
		      cd.setReferredValueSet((ValueSet) p.parse(input));
		    } else
		      throw new Exception("Unable to find source for "+cd.getReference()+" ("+Utilities.appendSlash(termDir)+cd.getReference()+".xml/json)");
		    if (cd.getReferredValueSet().getId() == null)
		      cd.getReferredValueSet().setId(FormatUtilities.makeId(cd.getBinding().name())); 
		  }
		}
	}

	private void loadPrimitives() throws Exception {
		XLSXmlParser xls = new XLSXmlParser(new CSFileInputStream(dtDir
				+ "primitives.xml"), "primitives");
		Sheet sheet = xls.getSheets().get("Imports");
		for (int row = 0; row < sheet.rows.size(); row++) {
			processImport(sheet, row);
		}
		sheet = xls.getSheets().get("String Patterns");
		for (int row = 0; row < sheet.rows.size(); row++) {
			processStringPattern(sheet, row);
		}
	}

	private void processImport(Sheet sheet, int row) throws Exception {
		PrimitiveType prim = new PrimitiveType();
		prim.setCode(sheet.getColumn(row, "Data Type"));
		prim.setDefinition(sheet.getColumn(row, "Definition"));
		prim.setComment(sheet.getColumn(row, "Comments"));
		prim.setSchemaType(sheet.getColumn(row, "Schema"));
    prim.setRegEx(sheet.getColumn(row, "RegEx"));
    prim.setV2(sheet.getColumn(row, "v2"));
    prim.setV3(sheet.getColumn(row, "v3"));
		TypeRef td = new TypeRef();
		td.setName(prim.getCode());
		definitions.getKnownTypes().add(td);
		definitions.getPrimitives().put(prim.getCode(), prim);
	}

	private void processStringPattern(Sheet sheet, int row) throws Exception {
		DefinedStringPattern prim = new DefinedStringPattern();
		prim.setCode(sheet.getColumn(row, "Data Type"));
		prim.setDefinition(sheet.getColumn(row, "Definition"));
		prim.setComment(sheet.getColumn(row, "Comments"));
		prim.setRegex(sheet.getColumn(row, "RegEx"));
		prim.setSchema(sheet.getColumn(row, "Schema"));
		prim.setBase(sheet.getColumn(row, "Base"));
		TypeRef td = new TypeRef();
		td.setName(prim.getCode());
		definitions.getKnownTypes().add(td);
		definitions.getPrimitives().put(prim.getCode(), prim);
	}

	private String loadCompositeType(String n, Map<String, org.hl7.fhir.definitions.model.TypeDefn> map) throws Exception {
		TypeParser tp = new TypeParser();
		List<TypeRef> ts = tp.parse(n, false, null);
		definitions.getKnownTypes().addAll(ts);

		try {
		  TypeRef t = ts.get(0);
		  File csv = new CSFile(dtDir + t.getName().toLowerCase() + ".xml");
		  if (csv.exists()) {
		    SpreadsheetParser p = new SpreadsheetParser(new CSFileInputStream(csv), csv.getName(), definitions, srcDir, logger, registry, version, context, genDate, false, extensionDefinitions);
		    org.hl7.fhir.definitions.model.TypeDefn el = p.parseCompositeType();
		    map.put(t.getName(), el);
		    el.getAcceptableGenericTypes().addAll(ts.get(0).getParams());
		    return el.getName();
		  } else {
		    String p = ini.getStringProperty("types", n);
		    csv = new CSFile(dtDir + p.toLowerCase() + ".xml");
		    if (!csv.exists())
		      throw new Exception("unable to find a definition for " + n + " in " + p);
		    XLSXmlParser xls = new XLSXmlParser(new CSFileInputStream(csv),
		        csv.getAbsolutePath());
		    Sheet sheet = xls.getSheets().get("Restrictions");
		    boolean found = false;
		    for (int i = 0; i < sheet.rows.size(); i++) {
		      if (sheet.getColumn(i, "Name").equals(n)) {
		        found = true;
		        Invariant inv = new Invariant();
		        inv.setId(n);
		        inv.setEnglish(sheet.getColumn(i,"Rules"));
		        inv.setOcl(sheet.getColumn(i, "OCL"));
		        inv.setXpath(sheet.getColumn(i, "XPath"));
		        ProfiledType pt = new ProfiledType();
		        pt.setDefinition(sheet.getColumn(i, "Definition"));
		        pt.setDescription(sheet.getColumn(i, "Rules"));
		        pt.setName(n);
		        pt.setBaseType(p);
		        pt.setInvariant(inv);
		        definitions.getConstraints().put(n, pt);
		      }
		    }
		    if (!found)
		      throw new Exception("Unable to find definition for " + n);
		    return n;
		  }
		} catch (Exception e) {
		  throw new Exception("Unable to load "+n+": "+e.getMessage(), e);
		}
	}

	private ResourceDefn loadResource(String n, Map<String, ResourceDefn> map, boolean isAbstract) throws Exception {
    String folder = n;
		File spreadsheet = new CSFile((srcDir) + folder + File.separatorChar + n + "-spreadsheet.xml");

		SpreadsheetParser sparser = new SpreadsheetParser(new CSFileInputStream(
				spreadsheet), spreadsheet.getName(), definitions, srcDir, logger, registry, version, context, genDate, isAbstract, extensionDefinitions);
		ResourceDefn root;
		try {
		  root = sparser.parseResource();
		} catch (Exception e) {
		  throw new Exception("Error Parsing Resource "+n+": "+e.getMessage(), e);
		}
		root.setWg(definitions.getWorkgroups().get(ini.getStringProperty("workgroups", root.getName().toLowerCase())));
		
		for (EventDefn e : sparser.getEvents())
			processEvent(e, root.getRoot());

		if (map != null) {
		  map.put(root.getName(), root);
		  definitions.getKnownResources().put(root.getName(), new DefinedCode(root.getName(), root.getRoot().getDefinition(), n));
		}
		root.setStatus(ini.getStringProperty("status", n));
		return root;
	}

	private void processEvent(EventDefn defn, ElementDefn root)
			throws Exception {
		// first, validate the event. The Request Resource needs to be this
		// resource - for now
		// if
		// (!defn.getUsages().get(0).getRequestResources().get(0).equals(root.getName()))
		// throw new
		// Exception("Event "+defn.getCode()+" has a mismatched request resource - should be "+root.getName()+", is "+defn.getUsages().get(0).getRequestResources().get(0));
		if (definitions.getEvents().containsKey(defn.getCode())) {
			EventDefn master = definitions.getEvents().get(defn.getCode());
			master.getUsages().add(defn.getUsages().get(0));
			if (!defn.getDefinition().startsWith("(see"))
				master.setDefinition(defn.getDefinition());
		} else
			definitions.getEvents().put(defn.getCode(), defn);
	}

  public boolean checkFile(String purpose, String dir, String file, List<String> errors, String category)
    throws IOException
  {
    CSFile f = new CSFile(dir+file);
    if (!f.exists()) {
      errors.add("Unable to find "+purpose+" file "+file+" in "+dir);
      return false;
    } else {
      long d = f.lastModified();
      if (!dates.containsKey(category) || d > dates.get(category))
        dates.put(category, d);
      return true;
    }
  }
  private Map<String, Long> dates;

	public void checkConditions(List<String> errors, Map<String, Long> dates) throws Exception {
		Utilities.checkFolder(srcDir, errors);
		Utilities.checkFolder(termDir, errors);
		Utilities.checkFolder(imgDir, errors);
		this.dates = dates;
		checkFile("required", termDir, "bindings.xml", errors, "all");
		checkFile("required", dtDir, "primitives.xml", errors, "all");

		for (String n : ini.getPropertyNames("types"))
			if (ini.getStringProperty("types", n).equals("")) {
				TypeRef t = new TypeParser().parse(n, false, null).get(0);
				checkFile("type definition", dtDir, t.getName().toLowerCase() + ".xml", errors, "all");
			}
    for (String n : ini.getPropertyNames("structures"))
      checkFile("structure definition", dtDir, n.toLowerCase() + ".xml",errors,"all");

    String[] shared = ini.getPropertyNames("shared");
    
    if(shared != null)
      for (String n : shared )
        checkFile("shared structure definition", dtDir, n.toLowerCase() + ".xml",errors,"all");
		
    for (String n : ini.getPropertyNames("infrastructure"))
			checkFile("infrastructure definition", dtDir, n.toLowerCase() + ".xml",	errors,"all");

		for (String n : ini.getPropertyNames("resources")) {
		  if (!new File(srcDir + n).exists())
		    errors.add("unable to find folder for resource "+n);
		  else {
		    if (new CSFile(srcDir + n + File.separatorChar + n	+ "-spreadsheet.xml").exists()) {
		      checkFile("definition", srcDir + n+ File.separatorChar, n + "-spreadsheet.xml", errors, n);
		    } else
		      checkFile("definition", srcDir + n + File.separatorChar, n + "-def.xml", errors, n);
		    checkFile("example xml", srcDir + n + File.separatorChar,	n + "-example.xml", errors, n);
		    // now iterate all the files in the directory checking data

		    for (String fn : new File(srcDir + n + File.separatorChar).list())
		      checkFile("source", srcDir + n + File.separatorChar, fn, errors, n);
		  }
		}
		for (String n : ini.getPropertyNames("special-resources")) {
			if (new CSFile(srcDir + n + File.separatorChar + n+ "-spreadsheet.xml").exists())
				checkFile("definition", srcDir + n+ File.separatorChar, n + "-spreadsheet.xml", errors, n);
			else
				checkFile("definition", srcDir + n+ File.separatorChar, n + "-def.xml", errors, n);
      // now iterate all the files in the directory checking data
      for (String fn : new File(srcDir + n + File.separatorChar).list())
        checkFile("source", srcDir + n + File.separatorChar, fn, errors, n);
		}
	}


  public BindingNameRegistry getRegistry() {
    return registry;
  }
}
