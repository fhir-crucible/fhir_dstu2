package org.hl7.fhir.tools.implementations.ruby;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.hl7.fhir.definitions.model.Definitions;
import org.hl7.fhir.definitions.model.ElementDefn;
import org.hl7.fhir.definitions.model.ResourceDefn;
import org.hl7.fhir.definitions.model.TypeRef;
import org.hl7.fhir.tools.implementations.GenBlock;

public abstract class ResourceGenerator {
  private static final String RESOURCE_TYPE = "Resource";
  protected String name;
  protected File outputFile;
  protected Definitions definitions;
  protected boolean xmlAttributeAsField = true;
  public static Set<String> dataTypes = new HashSet<String>();

  protected static final String REGEX_DATE = "/\\A[0-9]{4}(-(0[1-9]|1[0-2])(-(0[0-9]|[1-2][0-9]|3[0-1]))?)?\\Z/";;
  protected static final String REGEX_TIME = "/\\A([01][0-9]|2[0-3]):[0-5][0-9]:[0-5][0-9](\\.[0-9]+)?\\Z/";
  protected static final String REGEX_DATETIME = "/\\A[0-9]{4}(-(0[1-9]|1[0-2])(-(0[0-9]|[1-2][0-9]|3[0-1])(T([01][0-9]|2[0-3]):[0-5][0-9]:[0-5][0-9](\\.[0-9]+)?(Z|(\\+|-)((0[0-9]|1[0-3]):[0-5][0-9]|14:00))?)?)?)?\\Z/";
  protected static final String REGEX_INSTANT = "/\\A[0-9]{4}(-(0[1-9]|1[0-2])(-(0[0-9]|[1-2][0-9]|3[0-1])(T([01][0-9]|2[0-3]):[0-5][0-9]:[0-5][0-9](\\.[0-9]+)?(Z|(\\+|-)((0[0-9]|1[0-3]):[0-5][0-9]|14:00)))))\\Z/";
  
  protected enum FieldType {
    ANY("*"),
    BINARY("base64Binary"),
    BOOLEAN("boolean"),
    INTEGER("integer","positiveInt","unsignedInt"),
    DECIMAL("decimal"),
    DATE("date"),
    DATETIME("dateTime"),
    TIME("time"),
    INSTANT("instant"),
    CODE("code"),
    STRING("string", "uri", "id", "oid", "idref"),
    RESOURCE("Resource"),
    QUANTITY("Age", "Count", "Duration", "Money"),
    IGNORED("xhtml","div"),
    REFERENCE(),
    EMBEDDED();
    
    private List<String> elementTypes;

    FieldType(String... elementTypes) {
      this.elementTypes = elementTypes != null ? Arrays.asList(elementTypes) : null;
    }

    public static FieldType getFieldType(String elementType) {
      FieldType found = REFERENCE;
      for (FieldType fieldType : FieldType.values()) {
        if (fieldType.elementTypes != null && fieldType.elementTypes.contains(elementType)) {
          return fieldType;
        }
      }
      return found;
    }
  }  

  public ResourceGenerator(String name, Definitions definitions, File outputFile) {
    this(name, definitions, outputFile, true);
  }

  public ResourceGenerator(String name, Definitions definitions, File outputFile, boolean xmlAttributeAsField) {
    this.name = name;
    this.definitions = definitions;
    this.outputFile = outputFile;
    this.xmlAttributeAsField = xmlAttributeAsField;
  }

  public void generate() throws Exception {

    outputFile.createNewFile();
    GenBlock fileBlock = new GenBlock();

    generateMainHeader(fileBlock);

    ElementDefn root = getRootDefinition();
    generateResourceHeader(fileBlock, root);

    // first loop through and generate all of the embedded types from this
    // schema
    for (Iterator<ElementDefn> iterator = root.getElements().iterator(); iterator.hasNext();) {
      ElementDefn elementDefinition = iterator.next();
      generateEmbeddedType(outputFile.getParentFile(), fileBlock, elementDefinition);
    }

    generatePostEmbedded(fileBlock, root);

    // next loop through and generate the filed elements
    for (Iterator<ElementDefn> iterator = root.getElements().iterator(); iterator.hasNext();) {
      ElementDefn elementDefinition = iterator.next();
      if (!elementDefinition.isXmlAttribute() || xmlAttributeAsField)
        generateElement(fileBlock, elementDefinition);
    }

    generateResourceFooter(fileBlock);
    generateMainFooter(fileBlock);

    Writer modelFile = new BufferedWriter(new FileWriter(outputFile));
    modelFile.write(fileBlock.toString());
    modelFile.flush();
    modelFile.close();

  }

  protected abstract void generateMainFooter(GenBlock fileBlock);
  protected abstract void generateResourceFooter(GenBlock fileBlock);
  protected abstract void generateResourceHeader(GenBlock fileBlock, ElementDefn elementDefn);
  protected abstract void generatePostEmbedded(GenBlock fileBlock, ElementDefn elementDefinition);
  protected abstract void generateMainHeader(GenBlock fileBlock);
  protected abstract void generateEmbeddedType(File parentDir, GenBlock fileBlock, ElementDefn elementDefinition) throws IOException;
  protected abstract void handleField(GenBlock block, FieldType fieldType, boolean multipleCardinality, ElementDefn elementDefinition, TypeRef typeRef);

  protected void generateElement(GenBlock block, ElementDefn elementDefinition) {
    List<TypeRef> types = elementDefinition.getTypes();
    boolean multipleCardinality = elementDefinition.getMaxCardinality() == null||elementDefinition.getMaxCardinality() > 1;
    if (types.size() > 0) {
      for (TypeRef typeRef : types) {
        String elementType = typeRef.getName();
        if (elementType.startsWith("@")) {
          handleField(block, FieldType.EMBEDDED, multipleCardinality, elementDefinition, typeRef);
        } else {
          handleField(block, FieldType.getFieldType(elementType), multipleCardinality, elementDefinition, typeRef);
          dataTypes.add(elementType);
        }
      }
    } else if (types.size() == 0) {
      handleField(block, FieldType.EMBEDDED, multipleCardinality, elementDefinition, null);
    }
  }

  protected String getEmbeddedClassName(ElementDefn elementDefinition, TypeRef typeRef) {
    if (typeRef != null) return typeRef.getResolvedTypeName();
    String typeName = generateTypeName(elementDefinition, null);
    String cname = elementDefinition.getDeclaredTypeName();
    cname = (cname == null) ? typeName : cname;
    return Character.toUpperCase(cname.charAt(0)) + cname.substring(1);

  }

  protected ElementDefn getRootDefinition() {
    ElementDefn el = null;
    ResourceDefn resource = definitions.getResources().get(name);
    resource = (resource == null) ? definitions.getBaseResources().get(name) : resource;
    if (resource != null) {
      el = resource.getRoot();
    } else {
      el = definitions.getInfrastructure().get(name);
      el = (el == null) ? definitions.getTypes().get(name) : el;
      el = (el == null) ? definitions.getStructures().get(name) : el;
    }
    return el;
  }

  protected String generateTypeName(ElementDefn elementDefinition, TypeRef type) {
    return this.generateTypeName(elementDefinition, type, true);
  }

  protected String generateTypeName(ElementDefn elementDefinition, TypeRef type, boolean fixTypes) {
    String elementName = elementDefinition.getName().replace("[x]", "");
    if (elementDefinition.getTypes().size() > 1) {
      String typeName = type.getName();
      typeName = Character.toUpperCase(typeName.charAt(0)) + typeName.substring(1);
      elementName += typeName;
    }
    
    if (!fixTypes) { 
      return elementName;
    } else if (elementName.equals("type")) {
      elementName = "fhirType";
    } else if (elementName.equals("collection")) {
      elementName = "fhirCollection";
    } else if (elementName.equals("deleted")) {
      elementName = "fhirDeleted";
    } else if (elementName.equals("version")) {
      elementName = "versionNum";
    } else if (elementName.equals("class")) {
      elementName = "fhirClass";
    } else if (elementName.equals("hash")) {
      elementName = "fhirHash";
    } else if (elementName.equals("identity")) {
      elementName = "fhirIdentity";
    } else if (elementName.equals("modifier")) {
      elementName = "fhirModifier";
    }

    return elementName;
  }

  protected boolean isResource(ElementDefn elementDefinition) {
    for (TypeRef type : elementDefinition.getTypes()) {
      if(type.getName().endsWith(RESOURCE_TYPE)) return true;
    }
    return false;
  }


}
