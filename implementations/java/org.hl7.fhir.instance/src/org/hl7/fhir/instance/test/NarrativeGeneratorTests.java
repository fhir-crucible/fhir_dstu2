package org.hl7.fhir.instance.test;

import java.io.FileInputStream;
import java.io.FileOutputStream;

import org.hl7.fhir.instance.formats.XmlParser;
import org.hl7.fhir.instance.model.DomainResource;
import org.hl7.fhir.instance.utils.NarrativeGenerator;
import org.hl7.fhir.instance.utils.SimpleWorkerContext;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class NarrativeGeneratorTests {

	private NarrativeGenerator gen;
	
	@Before
	public void setUp() throws Exception {
		if (gen == null)
  		gen = new NarrativeGenerator("", null, SimpleWorkerContext.fromPack("C:\\work\\org.hl7.fhir\\build\\publish\\validation.zip"));
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() throws Exception {
		process("C:\\work\\org.hl7.fhir\\build\\source\\questionnaireresponse\\questionnaireresponse-example-f201-lifelines.xml");
	}

	private void process(String path) throws Exception {
	  XmlParser p = new XmlParser();
	  DomainResource r = (DomainResource) p.parse(new FileInputStream(path));
	  gen.generate(r);
	  FileOutputStream s = new FileOutputStream("c:\\temp\\gen.xml");
    new XmlParser().compose(s, r, true);
    s.close();
	  
  }

}
