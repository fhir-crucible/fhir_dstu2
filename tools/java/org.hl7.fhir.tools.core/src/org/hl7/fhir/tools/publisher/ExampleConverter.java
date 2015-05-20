package org.hl7.fhir.tools.publisher;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;

import org.hl7.fhir.instance.formats.JsonParser;
import org.hl7.fhir.instance.formats.IParser.OutputStyle;
import org.hl7.fhir.instance.formats.XmlParser;
import org.hl7.fhir.instance.model.Resource;

public class ExampleConverter {

  public static void main(String[] args) 
  {
    File temp = new File("./temp");
    System.out.println("Converting XML examples within " + temp.getAbsolutePath() + " into JSON...");
    File[] examples = temp.listFiles(new FilenameFilter() {
      @Override
      public boolean accept(File dir, String name) {
        return name.endsWith(".xml");
      }
    });
    int total = 0;
    int success = 0;
    int fail = 0;
    
    for(File example : examples) {
      total++;
      try {
        convertXmlFileToJson(example);
        success++;
      } catch (Exception e) {
        fail++;
        System.out.println("Failed to convert " + example.getName());
        System.out.println("\t" + e.getMessage());
      }
    }
    System.out.println("Examples: " + total);
    System.out.println("Success:  " + success);
    System.out.println("Failures: " + fail);
    System.out.println("Finished converting examples.");
  }
  
  private static void convertXmlFileToJson(File example) throws Exception
  {
    FileInputStream fis = new FileInputStream(example);
    XmlParser xp = new XmlParser();
    Resource resource = xp.parse(fis);  
    //String json = resource2Json(resource);
    JsonParser jp = new JsonParser();
    jp.setOutputStyle(OutputStyle.PRETTY).compose(new FileOutputStream(rename(example)), resource);
  }
  
  private static String rename(File example)
  {
    String name = example.getAbsolutePath();
    return name.substring(0, name.length()-4) + ".json";
  }
  
//  private String resource2Json(Resource r) throws Exception {
//    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
//    IParser json = new JsonParser().setOutputStyle(OutputStyle.PRETTY);
//    json.setSuppressXhtml("Snipped for Brevity");
//    json.compose(bytes, r);
//    return new String(bytes.toByteArray());
//  }

}
