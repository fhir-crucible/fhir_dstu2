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
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hl7.fhir.definitions.generators.specification.ProfileGenerator;
import org.hl7.fhir.definitions.generators.specification.ToolResourceUtilities;
import org.hl7.fhir.definitions.generators.specification.XPathQueryGenerator;
import org.hl7.fhir.definitions.model.BindingSpecification;
import org.hl7.fhir.definitions.model.BindingSpecification.Binding;
import org.hl7.fhir.definitions.model.Profile;
import org.hl7.fhir.definitions.model.Profile.ConformancePackageSourceType;
import org.hl7.fhir.definitions.model.DefinedCode;
import org.hl7.fhir.definitions.model.Definitions;
import org.hl7.fhir.definitions.model.ElementDefn;
import org.hl7.fhir.definitions.model.EventDefn;
import org.hl7.fhir.definitions.model.EventDefn.Category;
import org.hl7.fhir.definitions.model.EventUsage;
import org.hl7.fhir.definitions.model.Example;
import org.hl7.fhir.definitions.model.Example.ExampleType;
import org.hl7.fhir.definitions.model.Invariant;
import org.hl7.fhir.definitions.model.MappingSpace;
import org.hl7.fhir.definitions.model.Operation;
import org.hl7.fhir.definitions.model.OperationParameter;
import org.hl7.fhir.definitions.model.OperationTuplePart;
import org.hl7.fhir.definitions.model.ConstraintStructure;
import org.hl7.fhir.definitions.model.ResourceDefn;
import org.hl7.fhir.definitions.model.SearchParameterDefn;
import org.hl7.fhir.definitions.model.SearchParameterDefn.SearchType;
import org.hl7.fhir.definitions.model.TypeDefn;
import org.hl7.fhir.definitions.model.TypeRef;
import org.hl7.fhir.instance.formats.FormatUtilities;
import org.hl7.fhir.instance.formats.JsonParser;
import org.hl7.fhir.instance.formats.XmlParser;
import org.hl7.fhir.instance.model.Base64BinaryType;
import org.hl7.fhir.instance.model.BooleanType;
import org.hl7.fhir.instance.model.CodeType;
import org.hl7.fhir.instance.model.ContactPoint.ContactPointSystem;
import org.hl7.fhir.instance.model.DateTimeType;
import org.hl7.fhir.instance.model.DateType;
import org.hl7.fhir.instance.model.DecimalType;
import org.hl7.fhir.instance.model.ElementDefinition.BindingStrength;
import org.hl7.fhir.instance.model.Enumerations.ConformanceResourceStatus;
import org.hl7.fhir.instance.model.PositiveIntType;
import org.hl7.fhir.instance.model.SearchParameter;
import org.hl7.fhir.instance.model.SearchParameter.SearchParamType;
import org.hl7.fhir.instance.model.StructureDefinition;
import org.hl7.fhir.instance.model.StructureDefinition.ExtensionContext;
import org.hl7.fhir.instance.model.Factory;
import org.hl7.fhir.instance.model.IdType;
import org.hl7.fhir.instance.model.InstantType;
import org.hl7.fhir.instance.model.IntegerType;
import org.hl7.fhir.instance.model.OidType;
import org.hl7.fhir.instance.model.StringType;
import org.hl7.fhir.instance.model.StructureDefinition.StructureDefinitionType;
import org.hl7.fhir.instance.model.TimeType;
import org.hl7.fhir.instance.model.Type;
import org.hl7.fhir.instance.model.UnsignedIntType;
import org.hl7.fhir.instance.model.UriType;
import org.hl7.fhir.instance.model.UuidType;
import org.hl7.fhir.instance.model.ValueSet;
import org.hl7.fhir.instance.utils.ProfileUtilities;
import org.hl7.fhir.instance.utils.ProfileUtilities.ProfileKnowledgeProvider;
import org.hl7.fhir.instance.utils.ValueSetUtilities;
import org.hl7.fhir.instance.utils.WorkerContext;
import org.hl7.fhir.utilities.CSFile;
import org.hl7.fhir.utilities.Logger;
import org.hl7.fhir.utilities.Logger.LogMessageType;
import org.hl7.fhir.utilities.Utilities;
import org.hl7.fhir.utilities.XLSXmlParser;
import org.hl7.fhir.utilities.XLSXmlParser.Sheet;

import com.trilead.ssh2.crypto.Base64;

public class SpreadsheetParser {

  private String usageContext;
	private String name;
	private XLSXmlParser xls;
	private List<EventDefn> events = new ArrayList<EventDefn>();
	private boolean isProfile;
	private String profileExtensionBase;
	private Definitions definitions;
	private String title;
	private String folder;
	private Logger log;
	private String sheetname;
	private BindingNameRegistry registry;
  private String dataTypesFolder;
  private String txFolder;
  private String version; 
  private WorkerContext context;
  private Calendar genDate;
  private boolean isAbstract;
  private Map<String, BindingSpecification> bindings; // when parsing profiles
  private Map<String, StructureDefinition> extensionDefinitions = new HashMap<String, StructureDefinition>();
  private ProfileKnowledgeProvider pkp;
  
	public SpreadsheetParser(String usageContext, InputStream in, String name,	Definitions definitions, String root, Logger log, BindingNameRegistry registry, String version, WorkerContext context, Calendar genDate, boolean isAbstract, Map<String, StructureDefinition> extensionDefinitions, ProfileKnowledgeProvider pkp, boolean isType) throws Exception {
	  this.usageContext = usageContext;
		this.name = name;
  	xls = new XLSXmlParser(in, name);	
		this.definitions = definitions;
		if (name.indexOf('-') > 0)
			title = name.substring(0, name.indexOf('-'));
		else if (name.indexOf('.') > 0)
			title = name.substring(0, name.indexOf('.'));
		else
		  title = name;
		this.folder = root + (isType ? "datatypes" : title) + File.separator;
    this.dataTypesFolder =  root + "datatypes" + File.separator;
    this.txFolder =  root + "terminologies" + File.separator;
		this.log = log;
		this.registry = registry;
		this.version = version;
		this.context = context;
		this.genDate = genDate;
		this.isAbstract = isAbstract;
		this.extensionDefinitions = extensionDefinitions;
		this.pkp = pkp;
	}


	public TypeDefn parseCompositeType() throws Exception {
		isProfile = false;
		return parseCommonTypeColumns().getRoot();
	}

	private Sheet loadSheet(String name) {
	  sheetname = name;
	  return xls.getSheets().get(name);
	}

	public String getAbbreviationFor(String id) {
    id = id.toLowerCase();
    if (definitions.getTLAs().containsKey(id))
      return definitions.getTLAs().get(id);
    else
      return "inv";
  }
	
	private ResourceDefn parseCommonTypeColumns() throws Exception {
		ResourceDefn resource = new ResourceDefn();
		parseEnteredInError(resource);
		
		Sheet sheet = loadSheet("Bindings");
		Map<String, BindingSpecification> typeLocalBindings = null;
		if (sheet != null)
			typeLocalBindings = readBindings(sheet);
			
		sheet = loadSheet("Invariants");
		Map<String,Invariant> invariants = null;
		if (sheet != null)
			invariants = readInvariants(sheet, title);
		
		sheet = loadSheet("Data Elements");
		if (sheet == null)
		  throw new Exception("No Sheet found for Data Elements");
		for (int row = 0; row < sheet.rows.size(); row++) {
			processLine(resource, sheet, row, invariants, false, null);
		}

		if (invariants != null) {
		  for (Invariant inv : invariants.values()) {
		    if (Utilities.noString(inv.getContext())) 
		      log.log("Type "+resource.getRoot().getName()+" Invariant "+inv.getId()+" has no context", LogMessageType.Warning);
		    else {
		      ElementDefn ed = findContext(resource.getRoot(), inv.getContext(), "Type "+resource.getRoot().getName()+" Invariant "+inv.getId()+" Context");
		      if (ed.getName().endsWith("[x]") && !inv.getContext().endsWith("[x]"))
		        inv.setFixedName(inv.getContext().substring(inv.getContext().lastIndexOf(".")+1));
		      ed.getInvariants().put(inv.getId(), inv);
		      if (Utilities.noString(inv.getXpath()))
		        log.log("Type "+resource.getRoot().getName()+" Invariant "+inv.getId()+" ("+inv.getEnglish()+") has no XPath statement", LogMessageType.Warning);
		      else if (inv.getXpath().contains("\""))
		        log.log("Type "+resource.getRoot().getName()+" Invariant "+inv.getId()+" ("+inv.getEnglish()+") contains a \" character", LogMessageType.Warning);
		    }
		  }
		}
		
		//TODO: Will fail if type has no root. - GG: so? when could that be
		// EK: Future types. But those won't get there.
		if( typeLocalBindings != null)
			resource.getRoot().getNestedBindings().putAll(typeLocalBindings);
		
		scanNestedTypes(resource, resource.getRoot(), resource.getName());
		
		return resource;
	}
	
	
	private void parseEnteredInError(ResourceDefn resource) {
    Sheet sheet = loadSheet("Instructions");
    
    if (sheet != null) {
      for (int row = 0; row < sheet.rows.size(); row++) {
        if (sheet.rows.get(row).size() >= 2 && "entered-in-error-status".equals(sheet.rows.get(row).get(0)))
          resource.setEnteredInErrorStatus(sheet.rows.get(row).get(1));
      }
    }
  }


  private void scanNestedTypes(ResourceDefn parent, ElementDefn root, String parentName) throws Exception
	{
		for( ElementDefn element : root.getElements() )
		{
			if( element.hasNestedElements() )
			{	
				String nestedTypeName;
				
				ElementDefn newCompositeType = new ElementDefn();
		
				// If user has given an explicit name, use it, otherwise  automatically
				// generated name for this nested type
				if( element.typeCode().startsWith("=") ) 
				{
				  if (isProfile)
				    throw new Exception("Cannot use '=' types in profiles on "+parentName);
				  
				  element.setStatedType(element.typeCode().substring(1));
					nestedTypeName = element.typeCode().substring(1);
				} 
				else 
				{
					nestedTypeName = parentName + Utilities.capitalize(element.getName());
				}
			
		    newCompositeType.setAnonymousTypedGroup(true);
		    
				// Add Component to the actually generated name to avoid
				// confusing between the element name and the element's type
				newCompositeType.setName(nestedTypeName+"Component");
				newCompositeType.setDefinition("A nested type in " + parent.getName() + ": "
								+ element.getDefinition() );
				newCompositeType.getElements().addAll(element.getElements());
			
				if( parent.getRoot().getNestedTypes().containsKey(nestedTypeName) )
					throw new Exception("Nested type " + nestedTypeName + 
							" already exist in resource " + parent.getName());
				
				parent.getRoot().getNestedTypes().put(nestedTypeName, newCompositeType );

				// Clear out the name of the local type, so old code
				// will not see a change.
				element.getTypes().clear();
				element.setDeclaredTypeName(newCompositeType.getName());
				
				scanNestedTypes( parent, element, nestedTypeName);
			}
			
			resolveElementReferences(parent, element);
		}
	}
	
	private void resolveElementReferences(ResourceDefn parent, ElementDefn root)
			throws Exception 
	{
		for (TypeRef ref : root.getTypes()) {
			if (ref.isElementReference()) {
				ElementDefn referredElement = parent.getRoot().getElementByName(ref.getName().substring(1));

				if (referredElement == null)
					throw new Exception("Element reference " + ref.getName()+ " cannot be found in type " + parent.getName());

				if (referredElement.getDeclaredTypeName() == null)
					throw new Exception("Element reference "+ ref.getName()+ " in "+ parent.getName()
					   + " refers to an anonymous group of elements. Please specify names with the '=<name>' construct in the typename column.");

				ref.setResolvedTypeName(referredElement.getDeclaredTypeName());
			}
		}
	}
	
	
	public ResourceDefn parseResource() throws Exception {
	  isProfile = false;
	  ResourceDefn root = parseCommonTypeColumns();

	  readInheritedMappings(root, loadSheet("Inherited Mappings"));
	  readEvents(loadSheet("Events"));
	  readSearchParams(root, loadSheet("Search"), false);
	  if (xls.getSheets().containsKey("Profiles"))
	    readPackages(root, loadSheet("Profiles")); 
	  else
	    readPackages(root, loadSheet("Packages")); 
    readExamples(root, loadSheet("Examples"));
	  readOperations(root, loadSheet("Operations"));

	  return root;
	}

	private void readInheritedMappings(ResourceDefn resource, Sheet sheet) throws Exception {
    if (sheet != null) {
      for (int row = 0; row < sheet.rows.size(); row++) {
        String path = sheet.getColumn(row, "Element");
        if (Utilities.noString(path))
          throw new Exception("Invalid path for element at " +getLocation(row));
        for (String n : definitions.getMapTypes().keySet()) {
          resource.addMapping(path, n, sheet.getColumn(row, definitions.getMapTypes().get(n).getColumnName()));
        }
      }
    }
  }


  private void readOperations(ResourceDefn root, Sheet sheet) throws Exception {
	  Map<String, Operation> ops = new HashMap<String, Operation>();
    Map<String, OperationParameter> params = new HashMap<String, OperationParameter>();
	  
	  if (sheet != null) {
      for (int row = 0; row < sheet.rows.size(); row++) {
        String name = sheet.getColumn(row, "Name");
        String use = sheet.getColumn(row, "Use"); 
        String doco = sheet.getColumn(row, "Documentation");
        String type = sheet.getColumn(row, "Type");
				
				if (name != null && !name.equals("") && !name.startsWith("!")) {
	        if (!name.contains(".")) {
	          if (!type.equals("operation"))
              throw new Exception("Invalid type on operation "+type+" at " +getLocation(row));
	          params.clear();
	          
	          boolean system = false;
	          boolean istype = false;
	          boolean instance = false;
	          for (String c : use.split("\\|")) {
	            c = c.trim();
	            if ("system".equalsIgnoreCase(c))
	              system = true;
	            else if ("resource".equalsIgnoreCase(c))
	              istype = true;
	            else if ("instance".equalsIgnoreCase(c))
	              instance = true;
	            else 
	              throw new Exception("unknown operation use code "+c+" at "+getLocation(row));
	          }
	          Operation op = new Operation(name, system, istype, instance, sheet.getColumn(row, "Type"), sheet.getColumn(row, "Title"), doco, sheet.getColumn(row, "Footer"));
            root.getOperations().add(op);
            ops.put(name, op);
	        } else {
	          String[] parts = name.split("\\.");
	          if (parts.length == 3) {
              // inside of a tuple
              if (!Utilities.noString(use))
                throw new Exception("Tuple parameters: use must be blank at "+getLocation(row));
              Operation operation = ops.get(parts[0]);
              if (operation == null)
                throw new Exception("Unknown Operation '"+parts[0]+"' at "+getLocation(row));
              OperationParameter param = params.get(parts[1]);
              if (param == null)
                throw new Exception("Tuple parameter '"+parts[0]+"."+parts[1]+"' not found at "+getLocation(row));
              if (!param.getType().equals("Tuple"))
                throw new Exception("Tuple parameter '"+parts[0]+"."+parts[1]+"' type must be Tuple at "+getLocation(row));
              String profile = sheet.getColumn(row, "StructureDefinition");
              String min = sheet.getColumn(row, "Min");
              String max = sheet.getColumn(row, "Max");
              param.getParts().add(new OperationTuplePart(parts[2], doco, Integer.parseInt(min), max, type, profile));
	          } else {
              if (parts.length != 2)
                throw new Exception("Parameters: only one '.' in a parameter name at "+getLocation(row));
	            if (!use.equals("in") && !use.equals("out"))
	              throw new Exception("Only allowed use is 'in' or 'out' at "+getLocation(row));
	            Operation operation = ops.get(parts[0]);
	            if (operation == null)
	              throw new Exception("Unknown Operation '"+parts[0]+"' at "+getLocation(row));
	            String profile = sheet.getColumn(row, "StructureDefinition");
	            String min = sheet.getColumn(row, "Min");
	            String max = sheet.getColumn(row, "Max");
	            OperationParameter p = new OperationParameter(parts[1], use, doco, Integer.parseInt(min), max, type, profile);
              operation.getParameters().add(p);
              params.put(parts[1], p);
	          }
	        }
	      }
      }
	  }
	}


	private ExampleType parseExampleType(String s, int row) throws Exception {
	  if (s==null || "".equals(s))
	    return ExampleType.XmlFile;
	  if ("tool".equals(s))
	    return ExampleType.Tool;
	  if ("xml".equals(s))
	    return ExampleType.XmlFile;
	  if ("csv".equals(s))
	    return ExampleType.CsvFile;
	  throw new Exception("Unknown Example Type '" + s + "': " + getLocation(row));
	}

	  
	private void readPackages(ResourceDefn defn, Sheet sheet) throws Exception {
    if (sheet != null) {
      for (int row = 0; row < sheet.rows.size(); row++) {
        String name = sheet.getColumn(row, "Name");
        if (name != null && !name.equals("") && !name.startsWith("!")) {
          Profile pack = new Profile(sheet.getColumn(row, "IG Name"));
          if (Utilities.noString(pack.getCategory()))
            throw new Exception("Missing IG Name at "+getLocation(row));
          pack.setTitle(name);
          pack.setSource(checkFile(sheet, row, "Source", false, sheet.getColumn(row, "Filename"))); // todo-profile
          if (!definitions.getIgs().containsKey(pack.getCategory()))
            throw new Exception("IG Name '"+pack.getCategory()+"' is not reegistered in [igs] in fhir.ini at "+getLocation(row));
            
            
          String type = sheet.getColumn(row, "Type");
          if ("bundle".equalsIgnoreCase(type))
            pack.setSourceType(ConformancePackageSourceType.Bundle);
          else if ("spreadsheet".equalsIgnoreCase(type))
            pack.setSourceType(ConformancePackageSourceType.Spreadsheet);
          else
            throw new Exception("Unknown source type: "+type+" at "+getLocation(row));
          String example = checkFile(sheet, row, "Example", true, null); // todo-profile
          if (example != null)
            pack.getExamples().add(new Example(example, Utilities.fileTitle(example), "General Example for "+pack.getSource(), new File(example), true, ExampleType.XmlFile, isAbstract));
          defn.getConformancePackages().add(pack);
        }
      }
    }
  }


  private String checkFile(Sheet sheet, int row, String column, boolean canBeNull, String defaultValue) throws Exception {
    String name = sheet.getColumn(row, column);
    if (Utilities.noString(name))
      name = defaultValue;
    
    if (Utilities.noString(name)) {
      if (!canBeNull)
        throw new Exception("Missing filename for '"+column+"' at "+getLocation(row));
      return null;
    }
    String filename = Utilities.path(folder, name);
    if (!(new File(filename).exists()))
      throw new Exception("Unable to find source file "+name);
    return filename;
  }


  private Map<String,Invariant> readInvariants(Sheet sheet, String id) throws Exception {
		Map<String,Invariant> result = new HashMap<String,Invariant>();
		
		for (int row = 0; row < sheet.rows.size(); row++) {
			Invariant inv = new Invariant();

			String s = sheet.getColumn(row, "Id");
			if (!s.startsWith("!")) {
			  inv.setId(s.contains("-") ? s : getAbbreviationFor(id)+"-"+s);
			  inv.setName(sheet.getColumn(row, "Name"));
			  inv.setContext(sheet.getColumn(row, "Context"));
			  inv.setEnglish(sheet.getColumn(row, "English"));
			  inv.setXpath(sheet.getColumn(row, "XPath"));
			  inv.setSeverity(sheet.getColumn(row, "Severity"));
			  if (!Utilities.noString(sheet.getColumn(row,  "Schematron")))
			    log.log("Value found for schematron "+getLocation(row), LogMessageType.Hint);  
			  inv.setOcl(sheet.getColumn(row, "OCL"));
			  if (s.equals("") || result.containsKey(s))
			    throw new Exception("duplicate or missing invariant id "
			        + getLocation(row));
			  result.put(s, inv);
			}
		}
		
		return result;
	}

  /* for profiles that have a "search" tab not tied to a structure */
  private void readSearchParams(Profile pack, Sheet sheet, String prefix) throws Exception {
    for (int row = 0; row < sheet.rows.size(); row++) {

      if (!sheet.hasColumn(row, "Name"))
        throw new Exception("Search Param has no name "+ getLocation(row));
      String n = sheet.getColumn(row, "Name");
      if (!n.startsWith("!")) {
        SearchParameter sp = new SearchParameter();
        if (!sheet.hasColumn(row, "Type"))
          throw new Exception("Search Param "+pack.getTitle()+"/"+n+" has no type "+ getLocation(row));
        if (n.endsWith("-before") || n.endsWith("-after"))
          throw new Exception("Search Param "+pack.getTitle()+"/"+n+" includes relative time "+ getLocation(row));
//        if (!n.toLowerCase().equals(n))
//          throw new Exception("Search Param "+pack.getTitle()+"/"+n+" must be all lowercase "+ getLocation(row));
        sp.setName(n);
        String d = sheet.getColumn(row, "Description");
        sp.setType(SearchParamType.fromCode(sheet.getColumn(row, "Type")));
        List<String> pn = new ArrayList<String>();
        String path = sheet.getColumn(row, "Path");
        if (Utilities.noString(path))
          throw new Exception("Search Param "+pack.getTitle()+"/"+n+" has no path");
        if (!path.contains(".") && !path.startsWith("#"))
          throw new Exception("Search Param "+pack.getTitle()+"/"+n+" has an invalid path: "+path);
        ResourceDefn root2 = null;
        if (!path.startsWith("#")) {
          path = path.substring(0, path.indexOf('.'));
          root2 = definitions.getResourceByName(path);
          if (root2 == null)
            throw new Exception("Search Param "+pack.getTitle()+"/"+n+" has an invalid path (resource not found)");
        }
        if (!Utilities.noString(sheet.getColumn(row, "Target Types"))) 
          throw new Exception("Search Param "+pack.getTitle()+"/"+n+" has manually specified targets (not allowed)");

        if (root2 != null && root2.getSearchParams().containsKey(n))
          throw new Exception("Search Param "+root2.getName()+"/"+n+": duplicate name "+ getLocation(row));

        if (sp.getType() == SearchParamType.COMPOSITE) {
          throw new Exception("not supported");
        } else {
          String[] pl = sheet.getColumn(row, "Path").split("\\|");
          for (String pi : pl) {
            String p = pi.trim();
            ElementDefn e = null;
            if (Utilities.noString(p))
              throw new Exception("Search Param "+root2.getName()+"/"+n+": empty path "+ getLocation(row));
            if (p.startsWith("#")) {
              // root less extension search parameter
              StructureDefinition ex = pack.getExtension(prefix+p.substring(1));
              if (ex == null)
                throw new Exception("Search Param "+pack.getTitle()+"/"+n+" refers to unknown extension '"+p+"' "+ getLocation(row));
              e = definitions.getElementDefn("Extension");
              if (ex.getContextType() != ExtensionContext.RESOURCE || !ex.hasContext())
                throw new Exception("Search Param "+pack.getTitle()+"/"+n+" refers to an extension that is not tied to a particular resource path '"+p+"' "+ getLocation(row));
              path = ex.getContext().get(0).getValue();
              if (Utilities.noString(path))
                throw new Exception("Search Param "+pack.getTitle()+"/"+n+" has no path");
              if (path.contains("."))
                path = path.substring(0, path.indexOf('.'));
              root2 = definitions.getResourceByName(path);
              if (root2 == null)
                  throw new Exception("Search Param "+pack.getTitle()+"/"+n+" has an invalid path (resource not found)");
              if (root2 != null && root2.getSearchParams().containsKey(n))
                throw new Exception("Search Param "+root2.getName()+"/"+n+": duplicate name "+ getLocation(row));
              
              for (StringType t : ex.getContext()) 
                pn.add(t.getValue()+".extension{"+ex.getUrl()+"}");
            } else if (p.contains(".extension{")) {
              String url = extractExtensionUrl(p);
              StructureDefinition ex = extensionDefinitions.get(url); // not created yet?
              if (ex == null)
                throw new Exception("Search Param "+pack.getTitle()+"/"+n+" refers to unknown extension '"+url+"' "+ getLocation(row));
              if (Utilities.noString(d))
                d = ex.getDescription();
              e = definitions.getElementDefn("Extension");
              pn.add(p);
            } else if (!p.startsWith("!") && !p.startsWith("Extension{") ) {
              e = root2.getRoot().getElementForPath(p, definitions, "search param", true); 
            }
            if (Utilities.noString(d) && e != null)
              d = e.getShortDefn();
            if (d == null)
              throw new Exception("Search Param "+root2.getName()+"/"+n+" has no description "+ getLocation(row));
            if (e != null)
              pn.add(p);
            if (sp.getType() == SearchParamType.REFERENCE) {
              // no check?
            } else if (e != null && e.typeCode().startsWith("Reference("))
              throw new Exception("Search Param "+root2.getName()+"/"+n+" wrong type. The search type is "+sp.getType().toCode()+", but the element type is "+e.typeCode());
            sp.setDescription(d);
          }

          sp.setXpath(new XPathQueryGenerator(definitions, log, null).generateXpath(pn));
        }
        sp.setBase(root2.getName());
        sp.setId(pack.getId()+"-"+(root2 == null ? "all" : root2.getName())+"-"+sp.getName());
        sp.setUrl("http://hl7.org/fhir/SearchParameter/"+sp.getId());
        pack.getSearchParameters().add(sp);
      }
    }
  }
  
  private void readSearchParams(ResourceDefn root2, Sheet sheet, boolean forProfile) throws Exception {
    
    if (sheet != null)
      for (int row = 0; row < sheet.rows.size(); row++) {

        if (!sheet.hasColumn(row, "Name"))
          throw new Exception("Search Param has no name "+ getLocation(row));
        String n = sheet.getColumn(row, "Name");
        if (!n.startsWith("!")) {
//          if (!n.toLowerCase().equals(n))
//            throw new Exception("Search Param "+root2.getName()+"/"+n+" must be all lowercase "+ getLocation(row));

          if (!sheet.hasColumn(row, "Type"))
            throw new Exception("Search Param "+root2.getName()+"/"+n+" has no type "+ getLocation(row));
          if (n.endsWith("-before") || n.endsWith("-after"))
            throw new Exception("Search Param "+root2.getName()+"/"+n+" includes relative time "+ getLocation(row));
          if (root2.getSearchParams().containsKey(n))
            throw new Exception("Search Param "+root2.getName()+"/"+n+": duplicate name "+ getLocation(row));
          String d = sheet.getColumn(row, "Description");
          SearchType t = readSearchType(sheet.getColumn(row, "Type"), row);
          List<String> pn = new ArrayList<String>(); 
          SearchParameterDefn sp = null;
          if (t == SearchType.composite) {
            String[] pl = sheet.getColumn(row, "Path").split("\\&");
            if (Utilities.noString(d)) 
              throw new Exception("Search Param "+root2.getName()+"/"+n+" has no description "+ getLocation(row));
            for (String pi : pl) {
              String p = pi.trim();
              if (!root2.getSearchParams().containsKey(p)) {
                boolean found = false;
                if (p.endsWith("[x]"))
                  for (String pan : root2.getSearchParams().keySet()) {
                    if (pan.startsWith(p.substring(0,  p.length()-3)))
                      found = true;
                  }
                if (!found)                
                  throw new Exception("Composite Search Param "+root2.getName()+"/"+n+"  refers to an unknown component "+p+" at "+ getLocation(row));
              }
              pn.add(p);
              sp = new SearchParameterDefn(n, d, t);
              sp.getComposites().addAll(pn);
            }
          } else {
            String[] pl = sheet.getColumn(row, "Path").split("\\|");
            for (String pi : pl) {
              String p = pi.trim();
              ElementDefn e = null;
              if (!Utilities.noString(p) && !p.startsWith("!") && !p.startsWith("Extension{") ) {
                e = root2.getRoot().getElementForPath(p, definitions, "search param", true); 
              }
              if (Utilities.noString(d) && e != null)
                d = e.getShortDefn();
              if (p.startsWith("Extension(")) {
                String url = extractExtensionUrl(p);
                StructureDefinition ex = extensionDefinitions.get(url);
                if (ex == null)
                  throw new Exception("Search Param "+root2.getName()+"/"+n+" refers to unknown extension '"+url+"' "+ getLocation(row));
                if (Utilities.noString(d))
                  d = ex.getDescription();
                pn.add(p);
              }
              if (d == null)
                throw new Exception("Search Param "+root2.getName()+"/"+n+" has no description "+ getLocation(row));
              if (e != null)
                pn.add(p);
              if (t == SearchType.reference) {
                if (e == null && !forProfile && !sheet.hasColumn(row, "Target Types"))
                  throw new Exception("Search Param "+root2.getName()+"/"+n+" of type reference has wrong path "+ getLocation(row));
                if (!forProfile && e != null && (!e.hasType("Reference")))
                  throw new Exception("Search Param "+root2.getName()+"/"+n+" wrong type. The search type is reference, but the element type is "+e.typeCode());
              } else {
                if (e != null && e.hasOnlyType("Reference"))
                  throw new Exception("Search Param "+root2.getName()+"/"+n+" wrong type. The search type is "+t.toString()+", but the element type is "+e.typeCode());
                if (t == SearchType.uri) {
                  if (e != null && !e.typeCode().equals("uri"))
                    throw new Exception("Search Param "+root2.getName()+"/"+n+" wrong type. The search type is "+t.toString()+", but the element type is "+e.typeCode());
                } else {
                  if (e != null && e.typeCode().equals("uri"))
                    throw new Exception("Search Param "+root2.getName()+"/"+n+" wrong type. The search type is "+t.toString()+", but the element type is "+e.typeCode());
                }
              }
            }
            if (!forProfile && t == SearchType.reference && pn.size() == 0 && !sheet.hasColumn(row, "Target Types"))
              throw new Exception("Search Param "+root2.getName()+"/"+n+" of type reference has no path(s) "+ getLocation(row));

            sp = new SearchParameterDefn(n, d, t);
            sp.getPaths().addAll(pn);
            if (!Utilities.noString(sheet.getColumn(row, "Target Types"))) {
              sp.setManualTypes(sheet.getColumn(row, "Target Types").split("\\,"));
            }
          }
          root2.getSearchParams().put(n, sp);
        }
      }
	}

	private String extractExtensionUrl(String p) {
    String url = p.substring(p.indexOf("{")+1);
    return url.substring(0, url.length()-1);
  }


  private SearchType readSearchType(String s, int row) throws Exception {
		if ("number".equals(s))
			return SearchType.number;
		if ("string".equals(s))
			return SearchType.string;
		if ("date".equals(s))
			return SearchType.date;
		if ("reference".equals(s))
			return SearchType.reference;
    if ("token".equals(s))
      return SearchType.token;
    if ("uri".equals(s))
      return SearchType.uri;
    if ("composite".equals(s))
      return SearchType.composite;
    if ("quantity".equals(s))
      return SearchType.quantity;
		throw new Exception("Unknown Search Type '" + s + "': " + getLocation(row));
	}

 
	// Adds bindings to global definition.bindings. Returns list of
	// newly found bindings in the sheet.
	private Map<String, BindingSpecification> readBindings(Sheet sheet) throws Exception {
		Map<String, BindingSpecification> result = new HashMap<String,BindingSpecification>();
	
		for (int row = 0; row < sheet.rows.size(); row++) {
		  String bindingName = sheet.getColumn(row, "Binding Name"); 
		  
		  // Ignore bindings whose name start with "!"
		  if (Utilities.noString(bindingName) || bindingName.startsWith("!")) continue;
	      
			BindingSpecification cd = new BindingSpecification(usageContext);

			cd.setName(bindingName);
			cd.setDefinition(sheet.getColumn(row, "Definition"));
			cd.setBinding(BindingsParser.readBinding(sheet.getColumn(row, "Binding")));
			cd.setReference(sheet.getColumn(row, "Reference"));
	     if (!cd.getBinding().equals(Binding.Unbound) && Utilities.noString(cd.getReference())) 
         throw new Exception("binding "+cd.getName()+" is missing a reference");

      cd.setDescription(sheet.getColumn(row, "Description"));
      if (parseBoolean(sheet.getColumn(row, "Example"), row, false))
        cd.setStrength(BindingStrength.EXAMPLE);
      else if (parseBoolean(sheet.getColumn(row, "Extensible"), row, false))
        cd.setStrength(BindingStrength.EXTENSIBLE);
      else 
        cd.setStrength(BindingsParser.readBindingStrength(sheet.getColumn(row, "Conformance")));
        
			cd.setId(registry.idForName(cd.getName()));
			cd.setSource(name);
      cd.setUri(sheet.getColumn(row, "Uri"));
      String oid = sheet.getColumn(row, "Oid");
      if (!Utilities.noString(oid))
        cd.setVsOid(oid); // no cs oid in this case
      cd.setStatus(ConformanceResourceStatus.fromCode(sheet.getColumn(row, "Status")));
      cd.setWebSite(sheet.getColumn(row, "Website"));
      cd.setEmail(sheet.getColumn(row, "Email"));
      cd.setCopyright(sheet.getColumn(row, "Copyright"));
      cd.setV2Map(sheet.getColumn(row, "v2"));
      cd.setV3Map(checkV3Mapping(sheet.getColumn(row, "v3")));

			if (cd.getBinding() == BindingSpecification.Binding.CodeList) {
				Sheet codes = xls.getSheets().get(
						cd.getReference().substring(1));
				if (codes == null)
					throw new Exception("code source sheet not found for "+ cd.getName() + ": " + cd.getReference());
				parseCodes(cd.getCodes(), codes);
			}
			
			if (cd.getBinding() == Binding.ValueSet && !Utilities.noString(cd.getReference())) {
			  try {
			    if (cd.getReference().startsWith("http://hl7.org/fhir")) {
			    // ok, it's a reference to a value set defined within this build. Since it's an absolute 
			    // reference, it's into the base infrastructure. That's not loaded yet, so we will try
			    // to resolve it later
			  } else if (new File(Utilities.appendSlash(folder)+cd.getReference()+".xml").exists()) {
			    XmlParser p = new XmlParser();
			    FileInputStream input = new FileInputStream(Utilities.appendSlash(folder)+cd.getReference()+".xml");
	        cd.setReferredValueSet(ValueSetUtilities.makeShareable((ValueSet) p.parse(input)));
			  } else if (new File(Utilities.appendSlash(folder)+cd.getReference()+".json").exists()) {
			    JsonParser p = new JsonParser();
			    FileInputStream input = new FileInputStream(Utilities.appendSlash(folder)+cd.getReference()+".json");
			    cd.setReferredValueSet(ValueSetUtilities.makeShareable((ValueSet) p.parse(input)));
			  } else if (new File(Utilities.appendSlash(dataTypesFolder)+cd.getReference()+".xml").exists()) {
			    XmlParser p = new XmlParser();
			    FileInputStream input = new FileInputStream(Utilities.appendSlash(dataTypesFolder)+cd.getReference()+".xml");
			    cd.setReferredValueSet(ValueSetUtilities.makeShareable((ValueSet) p.parse(input)));
			  } else if (new File(Utilities.appendSlash(dataTypesFolder)+cd.getReference()+".json").exists()) {
			    JsonParser p = new JsonParser();
			    FileInputStream input = new FileInputStream(Utilities.appendSlash(dataTypesFolder)+cd.getReference()+".json");
			    cd.setReferredValueSet(ValueSetUtilities.makeShareable((ValueSet) p.parse(input)));
        } else if (new File(Utilities.appendSlash(txFolder)+cd.getReference()+".xml").exists()) {
          XmlParser p = new XmlParser();
          FileInputStream input = new FileInputStream(Utilities.appendSlash(txFolder)+cd.getReference()+".xml");
          cd.setReferredValueSet(ValueSetUtilities.makeShareable((ValueSet) p.parse(input)));
        } else if (new File(Utilities.appendSlash(txFolder)+cd.getReference()+".json").exists()) {
          JsonParser p = new JsonParser();
          FileInputStream input = new FileInputStream(Utilities.appendSlash(txFolder)+cd.getReference()+".json");
          cd.setReferredValueSet(ValueSetUtilities.makeShareable((ValueSet) p.parse(input)));
			  } else
			    throw new Exception("Unable to find source"); 
        } catch (Exception e) {
          throw new Exception(e.getMessage() + "for "+cd.getReference()+" ("+Utilities.appendSlash(folder)+cd.getReference()+".xml/json)", e);          
        }
			  if (cd.getReferredValueSet() != null) {
			    cd.getReferredValueSet().setId(FormatUtilities.makeId(cd.getReference()));
          cd.getReferredValueSet().setUserData("filename", cd.getReference()+".html");
          if (!cd.getReferredValueSet().hasExperimental())
            cd.getReferredValueSet().setExperimental(true);
          if (!cd.getReferredValueSet().hasVersion())
            cd.getReferredValueSet().setVersion(version);
			  }
			}
			if (definitions.getBindingByName(cd.getName()) != null) {
				throw new Exception("Definition of binding '"
						+ cd.getName()
						+ "' in "
						+ name
						+ " clashes with previous definition in "
						+ definitions.getBindingByName(cd.getName())
								.getSource());
			}
			definitions.getBindings().put(cd.getName(), cd);
			result.put(cd.getName(), cd);
		}
		
		return result;
	}

	private String checkV3Mapping(String value) {
	  if (value.startsWith("http://hl7.org/fhir/v3/vs/"))
	    return value.substring("http://hl7.org/fhir/v3/vs/".length());
	  else
	    return value;
	  }

  private void parseCodes(List<DefinedCode> codes, Sheet sheet)
			throws Exception {
		for (int row = 0; row < sheet.rows.size(); row++) {
			DefinedCode c = new DefinedCode();
			c.setId(sheet.getColumn(row, "Id"));
			c.setCode(sheet.getColumn(row, "Code"));
      c.setDisplay(sheet.getColumn(row, "Display"));
      if (c.hasCode() && !c.hasDisplay())
        c.setDisplay(Utilities.humanize(c.getCode()));
      c.setSystem(sheet.getColumn(row, "System"));
			c.setDefinition(Utilities.appendPeriod(processDefinition(sheet.getColumn(row, "Definition"))));
      c.setComment(sheet.getColumn(row, "Comment"));
      c.setParent(sheet.getColumn(row, "Parent"));
      c.setV2Map(sheet.getColumn(row, "v2"));
      c.setV3Map(sheet.getColumn(row, "v3"));
      for (String ct : sheet.columns) 
        if (ct.startsWith("Display:") && !Utilities.noString(sheet.getColumn(row, ct)))
          c.getLangs().put(ct.substring(8), sheet.getColumn(row, ct));
      if (Utilities.noString(c.getId()) && Utilities.noString(c.getSystem()))
        throw new Exception("code has no id or system ("+sheet.title+") "+getLocation(row));
			codes.add(c);
		}
	}


  private String processDefinition(String definition) {
    
    return definition.replace("$version$", version);
  }


	public void parseConformancePackage(Profile ap, Definitions definitions, String folder, String usage) throws Exception {
	  try {
	    isProfile = true;
	    checkMappings(ap);
	    Sheet sheet = loadSheet("Bindings");
	    if (sheet != null)
	      bindings = readBindings(sheet);

	    sheet = loadSheet("Metadata");
	    for (int row = 0; row < sheet.rows.size(); row++) {
	      String n = sheet.getColumn(row, "Name");
	      String v = sheet.getColumn(row, "Value");
	      if (n != null && v != null) {
	        if (ap.getMetadata().containsKey(n))
	          ap.getMetadata().get(n).add(v);
	        else {
	          ArrayList<String> vl = new ArrayList<String>();
	          vl.add(v);
	          ap.getMetadata().put(n, vl);
	        }
	      }
	    }
      if (!Utilities.noString(ap.metadata("category")))
          usage = ap.metadata("category");        
      if (ap.hasMetadata("name"))
        ap.setTitle(ap.metadata("name"));
      if (ap.hasMetadata("introduction"))
        ap.setIntroduction(Utilities.path(folder, ap.metadata("introduction")));
      if (ap.hasMetadata("notes"))
        ap.setNotes(Utilities.path(folder, ap.metadata("notes")));
      if (!ap.hasMetadata("id"))
        throw new Exception("Error parsing "+ap.getId()+"/"+ap.getTitle()+" no 'id' found in metadata");
      if (!ap.metadata("id").matches(FormatUtilities.ID_REGEX))
        throw new Exception("Error parsing "+ap.getId()+"/"+ap.getTitle()+" 'id' is not a valid id");

      if (!ap.metadata("id").equals(ap.metadata("id").toLowerCase()))
        throw new Exception("Error parsing "+ap.getId()+"/"+ap.getTitle()+" 'id' must be all lowercase");
      
	    this.profileExtensionBase = ap.metadata("extension.uri");

      sheet = loadSheet("Extensions");
      if (sheet != null) {
        int row = 0;
        while (row < sheet.rows.size()) {
          if (sheet.getColumn(row, "Code").startsWith("!"))
            row++;
          else 
            row = processExtension(null, sheet, row, definitions, ap.metadata("extension.uri"), ap);
        }
      }

	    List<String> namedSheets = new ArrayList<String>();

	    if (ap.getMetadata().containsKey("published.structure")) {
	      for (String n : ap.getMetadata().get("published.structure")) {
	        if (!Utilities.noString(n))
	          ap.getProfiles().add(parseProfileSheet(definitions, ap, n, namedSheets, true, usage));
	      }
	    }

	    int i = 0;
	    while (i < namedSheets.size()) {
	      ap.getProfiles().add(parseProfileSheet(definitions, ap, namedSheets.get(i), namedSheets, false, usage));
	      i++;
	    }
	    if (namedSheets.isEmpty() && xls.getSheets().containsKey("Search"))
	      readSearchParams(ap, xls.getSheets().get("Search"), this.profileExtensionBase);

	  } catch (Exception e) {
	    throw new Exception("exception parsing pack "+ap.getSource()+": "+e.getMessage(), e);
	  }
	}


  private void checkMappings(Profile pack) throws Exception {
    pack.getMappingSpaces().clear();
    Sheet sheet = loadSheet("Mappings");
    if (sheet != null) {
      for (int row = 0; row < sheet.rows.size(); row++) {
        String uri = sheet.getNonEmptyColumn(row, "Uri");
        MappingSpace ms = new MappingSpace(sheet.getNonEmptyColumn(row, "Column"), sheet.getNonEmptyColumn(row, "Title"), sheet.getNonEmptyColumn(row, "Id"), sheet.getIntColumn(row, "Sort Order"));
        pack.getMappingSpaces().put(uri, ms);
      }
    }
  }


  private ConstraintStructure parseProfileSheet(Definitions definitions, Profile ap, String n, List<String> namedSheets, boolean published, String usage) throws Exception {
    Sheet sheet;
    ResourceDefn resource = new ResourceDefn();
    resource.setPublishedInProfile(published);

		sheet = loadSheet(n+"-Inv");
	  Map<String,Invariant> invariants = null;
    if (sheet != null) {
	    invariants = readInvariants(sheet, n);
	  } else {
	  	invariants = new HashMap<String,Invariant>();
		}
		
    sheet = loadSheet(n);
    if (sheet == null)
      throw new Exception("The StructureDefinition referred to a tab by the name of '"+n+"', but no tab by the name could be found");
    for (int row = 0; row < sheet.rows.size(); row++) {
      ElementDefn e = processLine(resource, sheet, row, invariants, true, ap);
      if (e != null) 
        for (TypeRef t : e.getTypes()) {
          if (t.getProfile() != null && !t.getName().equals("Extension") && t.getProfile().startsWith("#")) { 
            if (!namedSheets.contains(t.getProfile().substring(1)))
              namedSheets.add(t.getProfile().substring(1));      
          }
        }
    }
    
    sheet = loadSheet(n + "-Extensions");
    if (sheet != null) {
      int row = 0;
      while (row < sheet.rows.size()) {
        if (sheet.getColumn(row, "Code").startsWith("!"))
          row++;
        else
          row = processExtension(resource.getRoot().getElementByName("extensions"), sheet, row, definitions, ap.metadata("extension.uri"), ap);
      }
    }
    sheet = loadSheet(n+"-Search");
    if (sheet != null) {
      readSearchParams(resource, sheet, true);
    }

		if (invariants != null) {
			for (Invariant inv : invariants.values()) {
			  if (Utilities.noString(inv.getContext())) 
			    log.log("Type "+resource.getRoot().getName()+" Invariant "+inv.getId()+" has no context", LogMessageType.Warning);
			  else {
			    ElementDefn ed = findContext(resource.getRoot(), inv.getContext(), "Type "+resource.getRoot().getName()+" Invariant "+inv.getId()+" Context");
			    // TODO: Need to resolve context based on element name, not just path
			    if (ed.getName().endsWith("[x]") && !inv.getContext().endsWith("[x]"))
			      inv.setFixedName(inv.getContext().substring(inv.getContext().lastIndexOf(".")+1));
			    ed.getInvariants().put(inv.getId(), inv);
			    if (Utilities.noString(inv.getXpath()))
		        log.log("Type "+resource.getRoot().getName()+" Invariant "+inv.getId()+" ("+inv.getEnglish()+") has no XPath statement", LogMessageType.Warning);
			    else if (inv.getXpath().contains("\""))
	          log.log("Type "+resource.getRoot().getName()+" Invariant "+inv.getId()+" ("+inv.getEnglish()+") contains a \" character", LogMessageType.Warning);
			  }
			}
		}

    resource.getRoot().setProfileName(n);
		ConstraintStructure p = new ConstraintStructure(ap.getId().toLowerCase()+'-'+n.toLowerCase(), resource.getRoot().getProfileName(), resource, definitions.getUsageIG(usage, "Parsing "+name));
    return p;
  }

	private void readExamples(ResourceDefn defn, Sheet sheet) throws Exception {
		if (sheet != null) {
			for (int row = 0; row < sheet.rows.size(); row++) {
				String name = sheet.getColumn(row, "Name");
				if (name != null && !name.equals("") && !name.startsWith("!")) {
				  String id  = sheet.getColumn(row, "Identity");
          if (id == null || id.equals(""))
            throw new Exception("Example " + name + " has no identity parsing " + this.name);
					String desc = sheet.getColumn(row, "Description");
					if (desc == null || desc.equals(""))
						throw new Exception("Example " + name + " has no description parsing " + this.name);
					String filename = sheet.getColumn(row, "Filename");
					if (filename.startsWith(defn.getName().toLowerCase()+"-examples.")) 
					  throw new Exception("Cannot name an example file "+filename);
					File file = new CSFile(folder + filename);
					String type = sheet.getColumn(row, "Type");
					if (!file.exists() && !("tool".equals(type) || isSpecialType(type)))
						throw new Exception("Example " + name + " file '" + file.getAbsolutePath() + "' not found parsing " + this.name);
					String pn = sheet.getColumn(row, "Profile"); 
					if (Utilities.noString(pn)) {
					  defn.getExamples().add(new Example(name, id, desc, file, 
					      parseBoolean(sheet.getColumn(row, "Registered"), row, true), 
					      parseExampleType(type, row),
					      isAbstract));
					} else {
					  Profile ap = null;
					  for (Profile r : defn.getConformancePackages()) {
					    if (r.getTitle().equals(pn))
					      ap = r;
					  }
					  if (ap == null)
					    throw new Exception("Example " + name + " profile '" + pn + "' not found parsing " + this.name);
					  ap.getExamples().add(new Example(filename, id, desc, file, parseBoolean(sheet.getColumn(row, "Registered"), row, true), parseExampleType(type, row), isAbstract));
					}
				}
			}
		}
		if (defn.getExamples().size() == 0) {
			File file = new CSFile(folder + title + "-example.xml");
			if (!file.exists() && !isAbstract)
				throw new Exception("Example (file '" + file.getAbsolutePath() + "') not found parsing " + this.name);
			if (file.exists())
			  defn.getExamples().add(
			      new Example("General", "example", "Example of " + title, file, true, ExampleType.XmlFile, isAbstract));
		}		
	}

	
	
	
	private boolean isSpecialType(String type) {
    return false;
  }


  private void readEvents(Sheet sheet) throws Exception {
		if (sheet != null) {
			for (int row = 0; row < sheet.rows.size(); row++) {
				String code = sheet.getColumn(row, "Event Code");
				if (code != null && !code.equals("") && !code.startsWith("!")) {
					EventDefn e = new EventDefn();
					events.add(e);
					e.setCode(code);
					e.setDefinition(Utilities.appendPeriod(sheet.getColumn(row, "Description")));
					e.setCategory(readCategory(sheet.getColumn(row, "Category")));
					EventUsage u = new EventUsage();
					e.getUsages().add(u);
					u.setNotes(sheet.getColumn(row, "Notes"));
					for (String s : sheet.getColumn(row, "Request Resources")
							.split(",")) {
						s = s.trim();
						if (!s.isEmpty())
							u.getRequestResources().add(s);
					}
					for (String s : sheet
							.getColumn(row, "Request Aggregations").split("\n")) {
						s = s.trim();
						if (!s.isEmpty())
							u.getRequestAggregations().add(s);
					}
					for (String s : sheet.getColumn(row, "Response Resources")
							.split(",")) {
						s = s.trim();
						if (!s.isEmpty())
							u.getResponseResources().add(s);
					}
					for (String s : sheet.getColumn(row,
							"Response Aggregations").split("\n")) {
						s = s.trim();
						if (!s.isEmpty())
							u.getResponseAggregations().add(s);
					}
					for (String s : sheet.getColumn(row, "Follow Ups").split(
							",")) {
						s = s.trim();
						if (!s.isEmpty())
							e.getFollowUps().add(s);
					}
				}
			}
		}
	}

	private Category readCategory(String s) throws Exception {
    if (Utilities.noString(s))
     return null;
    if (s.equalsIgnoreCase("Consequence"))
      return Category.Consequence;
    if (s.equalsIgnoreCase("Currency"))
      return Category.Currency;
    if (s.equalsIgnoreCase("Notification"))
      return Category.Notification;
    throw new Exception("unknown event category "+s);
  }


  private ElementDefn processLine(ResourceDefn root, Sheet sheet, int row, Map<String, Invariant> invariants, boolean profile, Profile pack) throws Exception {
		ElementDefn e;
		String path = sheet.getColumn(row, "Element");
		if (path.startsWith("!"))
		  return null;
		if (Utilities.noString(path)) 
      throw new Exception("Error reading definitions - no path found @ " + getLocation(row));
		  
		if (path.contains("#"))
			throw new Exception("Old path style @ " + getLocation(row));

    String profileName = isProfile ? sheet.getColumn(row, "Profile Name") : "";
    String discriminator = isProfile ? sheet.getColumn(row, "Discriminator") : "";
		if (!Utilities.noString(profileName) && Utilities.noString(discriminator) && (path.endsWith(".extension") || path.endsWith(".modifierExtension")))
		  discriminator = "url";
		  
		boolean isRoot = !path.contains(".");
		
		if (isRoot) {
			if (root.getRoot() != null)
				throw new Exception("Definitions in " + getLocation(row)+ " contain two roots: " + path + " in "+ root.getName());

			root.setName(path);
			e = new TypeDefn();
			e.setName(path);
			root.setRoot((TypeDefn) e);
		} else {
			e = makeFromPath(root.getRoot(), path, row, profileName, true);
		}

		String tasks = sheet.getColumn(row, "gForge");
		if (!Utilities.noString(tasks)) {
		  for (String t : tasks.split(","))
		    e.getTasks().add(t);
		}
		
		if (e.getName().startsWith("@")) {
		  e.setName(e.getName().substring(1));
		  e.setXmlAttribute(true);
		}
		String c = sheet.getColumn(row, "Card.");
		if (c == null || c.equals("") || c.startsWith("!")) {
			if (!isRoot && !profile)
				throw new Exception("Missing cardinality at "+ getLocation(row) + " on " + path);
			if (isRoot) {
			  e.setMinCardinality(1);
			  e.setMaxCardinality(1);
			}
		} else {
			String[] card = c.split("\\.\\.");
			if (card.length != 2 || !Utilities.IsInteger(card[0]) || (!"*".equals(card[1]) && !Utilities.IsInteger(card[1])))
				throw new Exception("Unable to parse Cardinality '" + c + "' " + c + " in " + getLocation(row) + " on " + path);
			e.setMinCardinality(Integer.parseInt(card[0]));
			e.setMaxCardinality("*".equals(card[1]) ? Integer.MAX_VALUE : Integer.parseInt(card[1]));
		}
		if (profileName.startsWith("#"))
		  throw new Exception("blah: "+profileName);
		e.setProfileName(profileName);
		e.setSliceDescription(isProfile ? sheet.getColumn(row, "Slice Description") : ""); 
		for (String d : discriminator.split("\\,"))
		  if (!Utilities.noString(d))
		    e.getDiscriminator().add(d);
		doAliases(sheet, row, e);
    if (sheet.hasColumn(row, "Must Understand"))
      throw new Exception("Column 'Must Understand' has been renamed to 'Is Modifier'");

		e.setIsModifier(parseBoolean(sheet.getColumn(row, "Is Modifier"), row, null));
		if (isProfile)
		  e.setMustSupport(parseBoolean(sheet.getColumn(row, "Must Support"), row, null));
    e.setSummaryItem(parseBoolean(sheet.getColumn(row, "Summary"), row, null));
    e.setRegex(sheet.getColumn(row, "Regex"));
    String uml = sheet.getColumn(row, "UML");
    if (uml != null) {
      if (uml.contains(";")) {
        String[] parts = uml.split("\\;");
        e.setSvgLeft(Integer.parseInt(parts[0]));
        e.setSvgTop(Integer.parseInt(parts[1]));
        if (parts.length > 2)
          e.setSvgWidth(Integer.parseInt(parts[2]));
        e.setUmlDir("");
      } else if (uml.startsWith("break:")) {
        e.setUmlBreak(true);
        e.setUmlDir(uml.substring(6));
      } else {
        e.setUmlDir(uml);
      }
    }
		String s = sheet.getColumn(row, "Condition");
		if (s != null && !s.equals(""))
			throw new Exception("Found Condition in spreadsheet "+ getLocation(row));
		s = sheet.getColumn(row, "Inv.");
		if (s != null && !s.equals("")) {
		  for (String sn : s.split(",")) {
		    Invariant inv = invariants.get(sn);
		    if (inv == null)
		      throw new Exception("unable to find Invariant '" + sn + "' "   + getLocation(row));
		    e.getStatedInvariants().add(inv);
		  }
		}

		TypeParser tp = new TypeParser();
		e.getTypes().addAll(tp.parse(sheet.getColumn(row, "Type"), isProfile, profileExtensionBase, definitions));

		if (isProfile && ((path.endsWith(".extension") || path.endsWith(".modifierExtension")) && (e.getTypes().size() == 1) && e.getTypes().get(0).hasProfile()) && Utilities.noString(profileName))
		    throw new Exception("need to have a profile name if a profiled extension is referenced for "+ e.getTypes().get(0).getProfile());
		
		if (sheet.hasColumn(row, "Concept Domain"))
			throw new Exception("Column 'Concept Domain' has been retired in "
					+ path);

		e.setBindingName(sheet.getColumn(row, "Binding"));
		if (e.hasBindingName()) { 
		  BindingSpecification binding = definitions.getBindingByName(e.getBindingName());
      if (binding != null && !binding.getUseContexts().contains(name))
        binding.getUseContexts().add(name);
	    }
    if (!Utilities.noString(sheet.getColumn(row, "Short Label")))
      e.setShortDefn(sheet.getColumn(row, "Short Label"));
    else // todo: make this a warning when a fair chunk of the spreadsheets have been converted 
      e.setShortDefn(sheet.getColumn(row, "Short Name"));
    
		e.setDefinition(Utilities.appendPeriod(processDefinition(sheet.getColumn(row, "Definition"))));
		
		if (isRoot) {
			root.setDefinition(e.getDefinition());
		} 
		
		if (isProfile)
		  e.setMaxLength(sheet.getColumn(row, "Max Length"));
		e.setRequirements(Utilities.appendPeriod(sheet.getColumn(row, "Requirements")));
		e.setComments(Utilities.appendPeriod(sheet.getColumn(row, "Comments")));
    for (String n : definitions.getMapTypes().keySet()) {
      e.addMapping(n, sheet.getColumn(row, definitions.getMapTypes().get(n).getColumnName()));
    }
    if (pack != null) {
      for (String n : pack.getMappingSpaces().keySet()) {
        e.addMapping(n, sheet.getColumn(row, pack.getMappingSpaces().get(n).getColumnName()));
      }      
    }
		e.setTodo(Utilities.appendPeriod(sheet.getColumn(row, "To Do")));
		e.setExample(processValue(sheet, row, "Example", e));
		e.setCommitteeNotes(Utilities.appendPeriod(sheet.getColumn(row, "Committee Notes")));
		e.setDisplayHint(sheet.getColumn(row, "Display Hint"));
		if (isProfile) {
      e.setFixed(processValue(sheet, row, "Value", e));
      e.setPattern(processValue(sheet, row, "Pattern", e));
		} else {
      e.setDefaultValue(processValue(sheet, row, "Default Value", e));
      e.setMeaningWhenMissing(sheet.getColumn(row, "Missing Meaning"));
		}
		e.setW5(checkW5(sheet.getColumn(row, "w5"), path));
		return e;
	}

	private String checkW5(String value, String path) throws Exception {
    if (Utilities.noString(value))
      return null;
    if (!definitions.getW5s().containsKey(value))
      throw new Exception("Unknown w5 value "+value+" at "+path);
    return value;
  }


  private Type processValue(Sheet sheet, int row, String column, ElementDefn e) throws Exception {
    String source = sheet.getColumn(row, column);
    if (Utilities.noString(source))
      return null;  
	  if (e.getTypes().size() != 1) 
      throw new Exception("Unable to process "+column+" unless a single type is specified (types = "+e.typeCode()+") "+getLocation(row));
    if (source.startsWith("{")) {
      JsonParser json = new JsonParser();
      return json.parseType(source, e.typeCode());
    } else if (source.startsWith("<")) {
      XmlParser xml = new XmlParser();
      return xml.parseType(source, e.typeCode());
    } else {
      String type = e.typeCode();
      if (type.equals("string"))
        return new StringType(source);
      if (type.equals("boolean"))
        return new BooleanType(Boolean.valueOf(source)); 
      if (type.equals("integer"))
        return new IntegerType(Integer.valueOf(source)); 
      if (type.equals("unsignedInt"))
        return new UnsignedIntType(Integer.valueOf(source)); 
      if (type.equals("positiveInt"))
        return new PositiveIntType(Integer.valueOf(source)); 
      if (type.equals("decimal"))
        return new DecimalType(new BigDecimal(source)); 
      if (type.equals("base64Binary"))
        return new Base64BinaryType(Base64.decode(source.toCharArray()));  
      if (type.equals("instant"))
        return new InstantType(source); 
      if (type.equals("uri"))
        return new UriType(source); 
      if (type.equals("date"))
        return new DateType(source); 
      if (type.equals("dateTime"))
        return new DateTimeType(source); 
      if (type.equals("time"))
        return new TimeType(source); 
      if (type.equals("code"))
        return new CodeType(source); 
      if (type.equals("oid"))
        return new OidType(source); 
      if (type.equals("uuid"))
        return new UuidType(source); 
      if (type.equals("id"))
        return new IdType(source); 
      throw new Exception("Unable to process primitive value provided for "+column+" - unhandled type "+type+" @ " +getLocation(row));
    }
  }


  private void doAliases(Sheet sheet, int row, ElementDefn e) throws Exception {
		String aliases = sheet.getColumn(row, "Aliases");
		if (!Utilities.noString(aliases))
		  if (aliases.contains(";")) {
		    for (String a : aliases.split(";"))
		      e.getAliases().add(a.trim());
		  } else {
	      for (String a : aliases.split(","))
	        e.getAliases().add(a.trim());
		  }
	}

  private int processExtension(ElementDefn extensions, Sheet sheet, int row,	Definitions definitions, String uri, Profile ap) throws Exception {
	  // first, we build the extension definition
    StructureDefinition ex = new StructureDefinition();
    ex.setType(StructureDefinitionType.EXTENSION);
    ex.setBase("http://hl7.org/fhir/StructureDefinition/Extension");
    ex.setAbstract(false);
    ToolResourceUtilities.updateUsage(ex, ap.getCategory());
	  String name = sheet.getColumn(row, "Code");
	  String context = null;
	  if (Utilities.noString(name))
	    throw new Exception("No code found on Extension");
	  
	  if (name.contains("."))
	    throw new Exception("Extension Definition Error: Extension names cannot contain '.': "+name);
	
	  ex.setUrl(uri+name);
    ex.setId(tail(ex.getUrl()));
	  ap.getExtensions().add(ex);
	  if (context == null) {
      ex.setContextType(readContextType(sheet.getColumn(row, "Context Type"), row));
      String cc = sheet.getColumn(row, "Context");
      if (!Utilities.noString(cc))
        for (String c : cc.split("\\;")) {
          definitions.checkContextValid(ex.getContextType(), c, this.name);
          ex.addContext(c);
        }
	  }
	  ex.setDisplay(sheet.getColumn(row, "Display"));
	  
	  ElementDefn exe = new ElementDefn();
	  exe.setName(sheet.getColumn(row, "Code"));
	  
	  ElementDefn exu = new ElementDefn();
	  exu.setName("url");
	  exe.getElements().add(exu);
	  exu.setFixed(new UriType(ex.getUrl()));
	  exu.getTypes().add(new TypeRef().setName("uri"));
     
    parseExtensionElement(sheet, row, definitions, exe);
    String sl = exe.getShortDefn();
    ex.setName(ex.getDisplay());
    if (!Utilities.noString(sl) && (!sl.contains("|") || !ex.hasName())) 
      ex.setName(sl);
    if (!ex.hasName())
      throw new Exception("Extension "+ex.getUrl()+" missing short label at "+getLocation(row));
    ex.setDescription(exe.getDefinition());

    ex.setPublisher(ap.metadata("author.name"));
    if (ap.hasMetadata("author.reference"))
      ex.addContact().getTelecom().add(Factory.newContactPoint(ContactPointSystem.URL, ap.metadata("author.reference")));
    //  <code> opt Zero+ Coding assist with indexing and finding</code>
    if (ap.hasMetadata("date"))
      ex.setDateElement(Factory.newDateTime(ap.metadata("date").substring(0, 10)));
    else
      ex.setDate(genDate.getTime());

    if (ap.hasMetadata("status")) 
      ex.setStatus(ConformanceResourceStatus.fromCode(ap.metadata("status")));
   
    row++;
    if (!ex.getUrl().startsWith("http://hl7.org/fhir/StructureDefinition/"))
      throw new Exception("extension "+ex.getUrl()+" is not valid in the publication tooling");
    
    while (row < sheet.getRows().size() && sheet.getColumn(row, "Code").startsWith(name+".")) {
      String n = sheet.getColumn(row, "Code");
      ElementDefn p = findContext(exe, n.substring(0, n.lastIndexOf(".")), "Extension Definition "+name);
      ElementDefn child = new ElementDefn();
      p.getElements().add(child);
      child.setName(n.substring(n.lastIndexOf(".")+1));
      parseExtensionElement(sheet, row, definitions, child);
      row++;
    }
	  
    new ProfileGenerator(definitions, null, pkp, null).convertElements(exe, ex, null);
    StructureDefinition base = definitions.getSnapShotForType("Extension");
    List<String> errors = new ArrayList<String>();
    new ProfileUtilities(this.context).sortDifferential(base, ex, "extension "+ex.getUrl(), pkp, errors);
    assert(errors.size() == 0);
    new ProfileUtilities(this.context).generateSnapshot(base, ex, ex.getUrl(), ex.getName(), pkp);
	  this.context.seeExtensionDefinition("http://hl7.org/fhir", ex);
	  return row;
	}

  private String tail(String url) {
    return url.substring(url.lastIndexOf("/")+1);
  }

  private void parseExtensionElement(Sheet sheet, int row, Definitions definitions, ElementDefn exe) throws Exception {
    // things that go on Extension
    String[] card = sheet.getColumn(row, "Card.").split("\\.\\.");
    if (card.length != 2 || !Utilities.IsInteger(card[0])
        || (!"*".equals(card[1]) && !Utilities.IsInteger(card[1])))
      throw new Exception("Unable to parse Cardinality "
          + sheet.getColumn(row, "Card.") + " in " + getLocation(row));
    exe.setMinCardinality(Integer.parseInt(card[0]));
    exe.setMaxCardinality("*".equals(card[1]) ? Integer.MAX_VALUE : Integer.parseInt(card[1]));
    exe.setCondition(sheet.getColumn(row, "Condition"));
    exe.setDefinition(Utilities.appendPeriod(processDefinition(sheet.getColumn(row, "Definition"))));
    exe.setRequirements(Utilities.appendPeriod(sheet.getColumn(row, "Requirements")));
    exe.setComments(Utilities.appendPeriod(sheet.getColumn(row, "Comments")));
    doAliases(sheet, row, exe);
    for (String n : definitions.getMapTypes().keySet()) {
      exe.addMapping(n, sheet.getColumn(row, definitions.getMapTypes().get(n).getColumnName()));
    }
    String tasks = sheet.getColumn(row, "gForge");
    if (!Utilities.noString(tasks)) {
      for (String t : tasks.split(","))
        exe.getTasks().add(t);
    }   
    exe.setTodo(Utilities.appendPeriod(sheet.getColumn(row, "To Do")));
    exe.setCommitteeNotes(Utilities.appendPeriod(sheet.getColumn(row, "Committee Notes")));
    exe.setShortDefn(sheet.getColumn(row, "Short Label"));

    exe.setIsModifier(parseBoolean(sheet.getColumn(row, "Is Modifier"), row, null));
    exe.getTypes().add(new TypeRef().setName("Extension"));
    
    // things that go on Extension.value
    if (!Utilities.noString(sheet.getColumn(row, "Type"))) {
      ElementDefn exv = new ElementDefn();
      exv.setName("value[x]");
      exe.getElements().add(exv);
      exv.setBindingName(sheet.getColumn(row, "Binding"));
      exv.setMaxLength(sheet.getColumn(row, "Max Length"));
      exv.getTypes().addAll(new TypeParser().parse(sheet.getColumn(row, "Type"), true, profileExtensionBase, definitions));
      exv.setExample(processValue(sheet, row, "Example", exv));
    }
  }

	private ExtensionContext readContextType(String value, int row) throws Exception {
    if (value.equals("Resource"))
      return ExtensionContext.RESOURCE;
    if (value.equals("DataType") || value.equals("Data Type"))
      return ExtensionContext.DATATYPE;
    if (value.equals("Elements"))
      return ExtensionContext.RESOURCE;
    if (value.equals("Element"))
      return ExtensionContext.RESOURCE;
    if (value.equals("Mapping"))
      return ExtensionContext.MAPPING;
    if (value.equals("Extension"))
      return ExtensionContext.EXTENSION;
	  
    throw new Exception("Unable to read context type '"+value+"' at "+getLocation(row));
  }

	 private ElementDefn findContext(ElementDefn root, String pathname, String source) throws Exception {
	    String[] path = pathname.split("\\.");
	    
	    if (!path[0].equals(root.getName()))
	      throw new Exception("Element Path '" + pathname + "' is not legal found at " + source);
	    ElementDefn res = root;
	    for (int i = 1; i < path.length; i++) {
	      String en = path[i];
	      if (en.length() == 0)
	        throw new Exception("Improper path " + pathname + " found at " + source);
	      if (en.charAt(en.length() - 1) == '*') 
	        throw new Exception("no-list wrapper found at " + source);

	      ElementDefn t = res.getElementByName(en, true);

	      if (t == null) {
          throw new Exception("Reference to undefined Element "+ pathname+ " found at " + source);
	      }
	      res = t;
	    }
	    return res;
	  }

  private ElementDefn makeFromPath(ElementDefn root, String pathname,
			int row, String profileName, boolean allowMake) throws Exception {
		String[] path = pathname.split("\\.");
		boolean n = false;
		if (!path[0].equals(root.getName()))
			throw new Exception("Element Path '" + pathname
					+ "' is not legal in this context in " + getLocation(row));
		ElementDefn res = root;
		for (int i = 1; i < path.length; i++) {
			String en = path[i];
			if (en.length() == 0)
				throw new Exception("Improper path " + pathname + " in "
						+ getLocation(row));
			if (en.charAt(en.length() - 1) == '*') {
				throw new Exception("no list wrapper found " + getLocation(row));
			}
			ElementDefn t = res.getElementByName(en);

			boolean isUnpickingElement = t != null && (i == path.length - 1)
					&& !t.getProfileName().equals("")
					&& !t.getProfileName().equals(profileName);

			if (t == null || isUnpickingElement) {
				if (n)
					throw new Exception("Internal Logic error " + pathname
							+ " @ " + getLocation(row));
				n = true;
				if (i < path.length - 1)
					throw new Exception("Encounter Element "+ pathname+ " before all the elements in the path are defined in "+ getLocation(row));
			  if (!allowMake)
          throw new Exception("Reference to undefined Element "+ pathname+ " in "+ getLocation(row));
			    
				t = new ElementDefn();
				t.setName(en);
				res.getElements().add(t);
			}
			res = t;
		}
		if (!n)
			throw new Exception("Duplicate Row name " + pathname + " @ "
					+ getLocation(row));
		return res;
	}

    /*
	private ElementDefn makeExtension(ElementDefn root, String pathname,
			int row, Definitions definitions) throws Exception {
		String[] path = pathname.split("\\.");

		ElementDefn res = root;
		for (int i = 0; i < path.length; i++) {
			String en = path[i];

			ElementDefn t = res.getElementByProfileName(en);
			if (t == null) {
				if (i < path.length - 1)
					throw new Exception(
							"Encounter Element "
									+ pathname
									+ " before all the elements in the path are defined in "
									+ getLocation(row));
				t = new ElementDefn(definitions.getInfrastructure().get(
						"Extensions"));
				t.setName("extension");
				t.setProfileName(en);
				t.getElementByName("code").setFixed(new CodeType(en));
				res.getElements().add(t);
			}
			res = t;
		}
		return res;
	}
	*/

	private String getLocation(int row) {
		return name + ", sheet \""+sheetname+"\", row " + Integer.toString(row + 2);
	}

	public List<EventDefn> getEvents() {
		return events;
	}

  public String getFolder() {
    return folder;
  }

  public void setFolder(String folder) {
    this.folder = folder;
  }

  protected Boolean parseBoolean(String s, int row, Boolean def) throws Exception {
    if (s == null || s.isEmpty())
      return def;
    s = s.toLowerCase();
    if (s.equalsIgnoreCase("y") || s.equalsIgnoreCase("yes")
        || s.equalsIgnoreCase("true") || s.equalsIgnoreCase("1"))
      return true;
    else if (s.equals("false") || s.equals("0") || s.equals("f")
        || s.equals("n") || s.equals("no"))
      return false;
    else
      throw new Exception("unable to process boolean value: " + s
          + " in " + getLocation(row));
  }


}
